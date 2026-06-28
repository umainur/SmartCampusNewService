import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

enum Category { COLLEGE, BUS, FOOD }

class Feedback {
    private String name;
    private int rating;
    private String comment;
    private Category category;

    public Feedback(String name, int rating, String comment, Category category) {
        this.name = name;
        this.rating = rating;
        this.comment = comment;
        this.category = category;
    }

    public String getName(){ 
        return name;
    }

    public int getRating(){
        return rating;
    }

    public String getComment(){ 
        return comment; 
    }

    public Category getCategory(){ 
        return category; 
    }

    public void display() {
        System.out.println("\nName: " + name);
        System.out.println("Rating: " + rating);
        System.out.println("Comment: " + comment);
        System.out.println("Category: " + category);
    }

    //ni format nak pindahkan dia ke dalam .txt
    public String toTXT() {
        return name + "|" + rating + "|" + category.name() + "|" + comment;
    }

    //convert text line from .txt into Feedback object
    public static Feedback fromTXT(String line){
        String[] p = line.split("\\|"); //part ni untuk split bahagian (name[0] to category[4])
        String name = p[0];
        int rating = Integer.parseInt(p[1]); //convert string to integer
        Category category = Category.valueOf(p[2]);  //convert string to object
        String comment = p[3];
       
        return new Feedback(name, rating, comment, category);   //create object
    }
}

public class FeedbackSystem extends CampusService {

    private ArrayList<Feedback> collegeFeedback = new ArrayList<>();
    private ArrayList<Feedback> foodFeedback = new ArrayList<>();
    private ArrayList<Feedback> busFeedback = new ArrayList<>();
    private Scanner scanner;

    public FeedbackSystem(Scanner in) {
        super("Student Feedback Collection", "feedback.txt");
        this.scanner = in;
        loadFromFile();   //load data dalam feedback.txt
        if (collegeFeedback.isEmpty() && foodFeedback.isEmpty() && busFeedback.isEmpty()) {
            seedSampleData();  //keluarkan data sedia ada
        }
    }

    //data sedia ada
    private void seedSampleData() {
        collegeFeedback.add(new Feedback("Maria", 5, "Nice college (KTF), close with Meranti",Category.COLLEGE));
        collegeFeedback.add(new Feedback("Sarah", 3, "Limited parking slots",Category.COLLEGE));
        foodFeedback.add(new Feedback("Jamal", 5, "Banyak pilihan",Category.FOOD));
        busFeedback.add(new Feedback("Mirah", 5, "Bus on time",Category.BUS));
    }

    public void displayServiceHeader() {
        System.out.println("\n---------------------------------------");
        System.out.println("     STUDENT FEEDBACK COLLECTION      ");
        System.out.println("----------------------------------------");
    }

    @Override
    public void displayMenu() {
        displayServiceHeader();
        int choice;
        do {
            System.out.println("\n----------Feedback Main Menu---------");
            System.out.println("1. Give Feedback");
            System.out.println("2. View Feedback");
            System.out.println("3. Save Feedback to File");
            System.out.println("4. Load Feedback from File");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choice: ");
            choice = readInt();

            switch (choice) {
                case 1: addRecord(); break;
                case 2: displayRecords(); break;
                case 3: saveToFile(); break;
                case 4: loadFromFile(); break;
                case 0: System.out.println("Returning to Main Menu..."); break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    @Override
    public void addRecord() {
        try {
            Category cat = readCategory();
            if (cat == null) return;

            ArrayList<Feedback> selectedList = getListByCategory(cat);

            // Show existing feedback for the category
            System.out.println("\n-----------" + cat + " Feedback-----------");
            if (selectedList.isEmpty()) {
                System.out.println("No feedback yet.");
            } else {
                for (Feedback f : selectedList) f.display();
            }

            // Collect new feedback
            boolean addMore = true;
            while (addMore) {
                System.out.println("\n-- Enter Your Feedback --");
                System.out.print("Name: ");
                String name = scanner.nextLine();
                if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");

                System.out.print("Rating (1-5): ");
                int rating = readInt();
                if (rating < 1 || rating > 5)
                    throw new IllegalArgumentException("Rating must be between 1 and 5.");

                System.out.print("Comment: ");
                String comment = scanner.nextLine();

                selectedList.add(new Feedback(name, rating, comment, cat));
                System.out.println("Feedback added!");

                System.out.print("Add another feedback? (yes/no): ");
                addMore = scanner.nextLine().equalsIgnoreCase("yes");
            }
            saveToFile();

        } catch (IllegalArgumentException e) {
            System.out.println("Input Error: " + e.getMessage());
        }
    }

    @Override
    public void displayRecords() {
        try {
            Category cat = readCategory();
            if (cat == null) return;
            ArrayList<Feedback> selectedList = getListByCategory(cat);
            System.out.println("\n---------" + cat + " Feedback--------");
            if (selectedList.isEmpty()) {
                System.out.println("No feedback yet.");
            } else {
                for (Feedback f : selectedList) f.display();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //BufferedWriter tu untuk store dulu semua
    //FileWriter tu akan open file then write direct
    @Override
    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Feedback f : getAllFeedback()) {
                bw.write(f.toTXT());
                bw.newLine();
            }
            saveSuccess();
        } catch (IOException e) {
            System.out.println("File Error (save): " + e.getMessage());
        }
    }

    //this will read feedback.txt, take each line to Feedback object
    @Override
    public void loadFromFile() {
        collegeFeedback.clear();  // Clear old data before loading to avoid duplicates
        foodFeedback.clear(); //because when you load file, you will add data again
        busFeedback.clear();

        //open file
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) { //read line by line
                if (line.isEmpty()) continue;
                Feedback f = Feedback.fromTXT(line); //convert text to object
                getListByCategory(f.getCategory()).add(f);
            }
            loadSuccess();
        } catch (IOException e) {
            System.out.println("File Error" + e.getMessage());
        }
    }

    private Category readCategory() {
        System.out.println("Choose Category:\n1. College\n2. Bus\n3. Food");
        System.out.print("Choice: ");
        switch (readInt()) {
            case 1: return Category.COLLEGE;
            case 2: return Category.BUS;
            case 3: return Category.FOOD;
            default: System.out.println("Invalid category."); return null;
        }
    }

    private ArrayList<Feedback> getListByCategory(Category cat) {
        switch (cat) {
            case COLLEGE: return collegeFeedback;
            case BUS: return busFeedback;
            case FOOD: return foodFeedback;
            default: return new ArrayList<>();
        }
    }

    private ArrayList<Feedback> getAllFeedback() {
        ArrayList<Feedback> all = new ArrayList<>();
        all.addAll(collegeFeedback);
        all.addAll(busFeedback);
        all.addAll(foodFeedback);
        return all;
    }

    private int readInt() {
        try { return Integer.parseInt(scanner.nextLine()); }
        catch (NumberFormatException e) {
            System.out.println("Invalid input , please enter a number.");
            return -1;
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        FeedbackSystem fs = new FeedbackSystem(in);
        fs.displayMenu();
    }
}
