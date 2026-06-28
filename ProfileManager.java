//newservice
//lama punya
import java.io.*;
import java.util.Scanner;
import java.util.Vector;

enum StudentCategory{
    UNDERGRADUATE,POSTGRADUATE,EXCHANGE
}

class Students{

    private String studentId;
    private String studentName;
    private String programme;
    private int yearOfStudy;
    private StudentCategory category;

    //constructor
    public Students(String studentId,String studentName,String programme,int yearOfStudy,StudentCategory category){
        this.studentId = studentId;
        this.studentName = studentName ;
        this.programme =programme ;
        this.yearOfStudy = yearOfStudy ;
        this.category =category ;
    }

    //Getters
    public String getStudentID(){ return studentId;}
    public String getStudentName(){ return studentName;}
    public String getProgramme(){ return programme;}
    public int getYearOfStudy(){ return yearOfStudy;}
    public StudentCategory getCategory(){ return category;}
    

    //display method
    public void displayInfo(){
        System.out.println("\n--------Student Information--------\n");
        System.out.println("Student ID    : " + studentId);
        System.out.println("Name          : " + studentName);
        System.out.println("Programme     : " + programme);
        System.out.println("Year Of Study : " + yearOfStudy);
        System.out.println("Category      : " + category);
        System.out.println("------------------------------------");
    }

    //simpan maklumat dalam txt
    public String toTXT(){
        return studentId + "|" + studentName + "|" + programme + "|" + yearOfStudy + "|" + category.name() ;
    }

    //reconstruct student object from txt""
    public static Students fromTXT(String line) throws IllegalArgumentException{
        String[] parts = line.split("\\|", 5);
        String id = parts[0].trim();
        String name = parts[1].trim();
        String prog = parts[2].trim();
        int year;; 
        try{ year = Integer.parseInt(parts[3].trim());}
        catch (NumberFormatException e){ 
            throw new IllegalArgumentException("Invalid year in CSV" + parts[3]); }
            StudentCategory cat = StudentCategory.valueOf(parts[4].trim().toUpperCase());
            return new Students(id, name, prog, year, cat);
        }
}

//subclass CampusService
public class ProfileManager extends CampusService{
    
    private Vector<Students> students = new Vector<Students>();
    private Scanner scanner;

    public ProfileManager(Scanner scanner){
        super("Student Profile Management", "students.txt");
        this.scanner=scanner;
        loadFromFile();          // auto-load on startup //ni pun?
    }

    public void displayMenu(){
        int option;
        do{
            System.out.println("\n--------Student Profile Menu----------\n");
            System.out.println("1. Add a new student");
            System.out.println("2. Display All Students");
            System.out.println("3. Search Student by ID");
            System.out.println("4. Count Students by Category");
            System.out.println("5. Save Records to File");
            System.out.println("6. Load Records from File");
            System.out.println("0. Back to Main Menu");
            System.out.print("\nEnter choice: ");
            option = readInt();

            switch(option){
                case 1: addRecord();
                    break;
                case 2: displayRecords();
                    break;
                case 3: 
                    System.out.print("Enter Student ID to search: ");
                    searchRecord(scanner.nextLine().trim().toUpperCase());
                    break;
                case 4: countByCategory();
                    break;
                case 5: saveToFile();        //ni tengok kat campuservice      
                    break;
                case 6: loadFromFile();      //ni tengok kat campuservice
                    break;
                case 0: System.out.println("Returning to Main Menu...");
                    return;
                default: System.out.println("Invalid choice.");
            }
        } while (option != 0);
    }

    @Override
    public void addRecord() {
        System.out.println("\n-------Add New Student-------");
    try{
        System.out.print("Student ID : ");
        String id = scanner.nextLine().trim().toUpperCase(); 
        if (id.isEmpty()) throw new IllegalArgumentException("Student ID cannot be empty.");

        // cek duplicate id
        if (findById(id) != null) {
            System.out.println("Error: Student ID '" + id + "' already exists.");
            return;
        }

        System.out.print("Name          : ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");

        System.out.print("Programme     : ");
        String programme = scanner.nextLine();
        if (programme.isEmpty()) throw new IllegalArgumentException("Programme cannot be empty.");

        System.out.print("Year of Study : ");
        int year = readInt();
        if (year < 1 || year > 6) throw new IllegalArgumentException("Year must be between 1 and 6.");

        StudentCategory cat = readCategory();
        if (cat == null) return;

        students.add(new Students(id, name, programme, year, cat));
        System.out.println("Student added successfully!\n");
        saveToFile();

    }   catch (IllegalArgumentException e) {
            System.out.println("Input Error: " + e.getMessage());
        }
    }
        @Override
        public void displayRecords(){
            if(students.isEmpty()){
                System.out.print("No student records found!");
                return;
            }
            System.out.println("\nTotal Students: " + students.size());
            for (int i = 0; i < students.size(); i++) {
                students.get(i).displayInfo();
            }
        }

        @Override
        public void searchRecord(String id) {
            Students s = findById(id);
                if (s != null)
                    s.displayInfo();
                else
                    System.out.println("Student not found for ID: " + id);
        }

        @Override
        public void saveToFile() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
                for (int i = 0; i < students.size(); i++) {
                    bw.write(students.get(i).toTXT());
                    bw.newLine();
                }
                saveSuccess();
            } catch (IOException e) {
                System.out.println("File Error (save): " + e.getMessage());
            }
        }

        @Override
        public void loadFromFile() {
            File file = new File(fileName);
                if (!file.exists()) return; // nothing to load yet

                try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                    String line;
                while ((line = br.readLine()) != null) {
                    if (line.isEmpty()) continue;
                    try {
                        students.add(Students.fromTXT(line));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Skipping invalid record: " + e.getMessage());
                    }
            }
            loadSuccess();
        } catch (IOException e) {
            System.out.println("File Error: " + e.getMessage());
    }
}
        public void countByCategory(){
            int ug=0, pg=0, ex=0;
            for(int i=0;i<students.size(); i++){
                switch (students.get(i).getCategory()) {
                    case UNDERGRADUATE: ug++; 
                        break;
                    case POSTGRADUATE: pg++;  
                        break;
                    case EXCHANGE: ex++;  
                        break;
                }
            }
            System.out.println("\n-------Student Count by Category-------\n");
            System.out.println("UNDERGRADUATE : " + ug);
            System.out.println("POSTGRADUATE  : " + pg);
            System.out.println("EXCHANGE      : " + ex);
        }

        public Students findById(String id) {
            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).getStudentID().equalsIgnoreCase(id))
                    return students.get(i);
            }
            return null;
        }

        public int getStudentCount() { return students.size(); }

        private StudentCategory readCategory() {
            System.out.println("Category: 1.UNDERGRADUATE  2.POSTGRADUATE  3.EXCHANGE");
            System.out.print("Enter choice: ");
            switch (readInt()) {
                case 1: return StudentCategory.UNDERGRADUATE;
                case 2: return StudentCategory.POSTGRADUATE;
                case 3: return StudentCategory.EXCHANGE;
                default: System.out.println("Invalid category."); return null;
            }
        }
    
        private int readInt() {
            try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Invalid input."); return -1; }
        }

        
    
}