import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

enum EquipmentType{ //jenis data untuk type of equipment
    LAPTOP, PROJECTORS, CAMERA, SENSOR_KIT
}

enum EquipmentStatus{ //jenis data untuk status of borrowing
    BORROWED, RETURNED, OVERDUE
}

class Equipment{
    private String studentName;
    private String studentID;
    private EquipmentType type; //variable untuk store type of equipment yang guna enum yang kita declare kat atas
    private EquipmentStatus status; //variable untuk store status of borrowing yang guna enum yang kita declare kat atas
    private int borrowDuration; // in days

    private static Vector<Equipment> records = new Vector<>(); // list to store all records
    //guna vector sebab kita boleh tambah new record bila-bila masa (size is filexible)

    // Constructor
    public Equipment(String studentName, String studentID, EquipmentType type, EquipmentStatus status, int borrowDuration){
        this.studentName = studentName;
        this.studentID = studentID;
        this.type = type;
        this.status = status;
        this.borrowDuration = borrowDuration;
    }

    // Getters and setters
    public void setName(String studentName){ this.studentName = studentName;}    
    public String getName() { return studentName;}

    public void setID(String studentID){this.studentID = studentID;}
    public String getID(){return studentID;}

    public void setType(EquipmentType type){this.type = type;}
    public EquipmentType getType(){ return type;}

    public void setStatus(EquipmentStatus status){ this.status = status;}
    public EquipmentStatus getStatus(){ return status;}

    public void setDuration(int borrowDuration){ this.borrowDuration = borrowDuration;}
    public int getDuration(){ return borrowDuration;}


    public static Vector<Equipment> getRecord() {return records;}

    public static int getTotalBorrowed(){
        int count = 0;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getStatus() == EquipmentStatus.BORROWED) {
                count++;
            }
        }
        return count;
    }

    //nak masukkan semua borrowing equipment dalam file txt
    public String toTXTFile(){
        return studentName + "|" + studentID + "|" + type + "|" + status + "|" + borrowDuration;
    }

    //baca dari txt file untuk display
    public static Equipment fromTXTFile(String equipmentInfo){
        String[] info = equipmentInfo.split("\\|");
        String studentName = info[0];
        String studentID = info[1];
        EquipmentType type = EquipmentType.valueOf(info[2]);
        EquipmentStatus status = EquipmentStatus.valueOf(info[3]);
        int borrowDuration = Integer.parseInt(info[4]); //tukar string jadi int sebab kita save dlm txt file 
        return new Equipment(studentName, studentID, type, status, borrowDuration);
    }
}

    
public class BorrowEquipment extends CampusService{
    
    private Scanner scanner;
    private Vector<Equipment> records = Equipment.getRecord();

    BorrowEquipment(Scanner input){
        super("Equipment Borrowing Service", "equipments.txt");
        this.scanner = input;
        loadFromFile();
    }

    @Override
    public void displayMenu(){
        int choice;

        do {
            System.out.println("\n--------- Equipment Borrowing Menu ---------");
            System.out.println("\n1. Add borrowing record");
            System.out.println("2. Display all records");
            System.out.println("3. Search record by student ID");
            System.out.println("4. Calculate total borrowed items");
            System.out.println("5. Check duration rule");
            System.out.println("6. Save records to file");
            System.out.println("7. Load records from file");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose option: ");
            //System.out.println();

            choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                    switch (choice) {
                        case 1:
                            addRecord(); break;
                        case 2:
                            displayRecords(); break;
                        case 3:
                            System.out.println("Enter ID: ");
                            searchRecord(scanner.nextLine().trim()); 
                            break;
                        case 4:
                            totalBorowed(); break;
                        case 5:
                            durationRule(); break;
                        case 6:
                            saveToFile(); break;
                        case 7:
                            loadFromFile(); break;
                        case 0:
                            break;
                        default:
                            System.out.println("\nInvalid choice. Please try again!");
                            continue;
                    }
            } catch (NumberFormatException e) { System.out.println("Invalid choice.Please try again");} 
        } while (choice != 0);
    }

    @Override //(i) add a new record 
    public void addRecord(){
        
        System.out.println("\n-------Add New Record for Equipment Borrowing--------");
        System.out.print("Enter student ID: ");
        String studentID = scanner.nextLine();
        System.out.print("Enter student name: ");
        String studentName = scanner.nextLine();
        

        //nak guna choice sebab mula ii guna masukkan input guna keyboard tulis full name tapi nanti if if type then susah so nak senang buat lah choice 
        EquipmentType type = null;
        while (true) {
        System.out.println("\nEquipment List");
        System.out.println("1. LAPTOP \n2. CAMERA \n3. PROJECTORS \n4. SENSOR_KIT"); 
        System.out.print("Enter your choice: ");
        
        try{
            int choice = Integer.parseInt(scanner.nextLine().trim());

        switch (choice) {
            case 1:
                type = EquipmentType.LAPTOP;
                break;
            case 2:
                type = EquipmentType.CAMERA;
                break;
            case 3:
                type = EquipmentType.PROJECTORS;
                break;
            case 4:
                type = EquipmentType.SENSOR_KIT;
                break;
            default:
                System.out.println("\nInvalid choice. Please try again");
                continue; //loop balik
            }
            break;
        } catch (NumberFormatException e){
            //kalau user masukkan input selain number
            System.out.println("Invalid choice. Please enter number (1-4)");
            continue; //loop balik 
        }    
    }

        System.out.print("Enter duration of borrowing (in days): ");
        int borrowDuration = Integer.parseInt(scanner.nextLine().trim()); //ambik input dari user as string tapi untuk trim() kita buang space depan dgn belakang and convert jadi int
        EquipmentStatus status = EquipmentStatus.BORROWED; //default status bila ade add new record, so automatic jadi set as BORROWED

        //buat object baru untuk record baru 
        Equipment newRecord = new Equipment(studentName, studentID, type, status, borrowDuration);
        Equipment.getRecord().addElement(newRecord); //tambah record baru ke vector
        //System.out.println("\nRecord added successfully!");
        saveToFile();
    }   

    @Override //(ii) display all borrowingrecords 
    public void displayRecords(){

        System.out.println("\n---------All Borrowing Records---------");
        if (records.isEmpty()){
            System.out.println("\nNo records found.");
        }
        else{
            for (int i = 0; i < records.size(); i++){ //records kita ambik dari Vector yang ade semua data even yang baru add takdi
                //Equipment list = records.get(i);
                System.out.println("\nRecords " + (i+1) + ":");
                System.out.println("\nStudent Name: " + records.get(i).getName());
                System.out.println("Student ID: " + records.get(i).getID());
                System.out.println("Equipment: " + records.get(i).getType());
                System.out.println("Status: " + records.get(i).getStatus());
                System.out.println("Borrow Duration: " + records.get(i).getDuration() + " days");
            }          
        }
    }
    

    @Override //(iii) search borrrow record by student ID
    public void searchRecord(String studentID){
        System.out.println("\n------------Search Borrowing Record by Student ID----------");
        System.out.print("\nEnter student ID: ");
        boolean found = false; //assume belum jumpa record lagi

        for (int i = 0; i < records.size(); i++){
            if (records.get(i).getID().equalsIgnoreCase(studentID)){ // get() - ambik element kat specific index, lepastu compare dengan studentID entered
                System.out.println("\nRecord found!");
                System.out.println("\nStudent Name: " + records.get(i).getName());
                System.out.println("Equipment Type: " + records.get(i).getType());
                System.out.println("Status: " + records.get(i).getStatus());
                System.out.println("Borrow Duration: " + records.get(i).getDuration() + " days");
                found = true; //sebab dah jumpa so jadi true
            }
        }
        if (!found) {
            System.out.println("Record not found.");
        }
    }

    @Override
    public void saveToFile(){
        try (BufferedWriter saveFile = new BufferedWriter(new FileWriter(fileName))){
            for (int i = 0; i < records.size(); i++){
                saveFile.write(records.get(i).toTXTFile());
                saveFile.newLine();
            }
            saveSuccess();
        } catch (IOException e){
            System.out.println("Record cannot save in " + fileName);
        }
    }

    @Override
    public void loadFromFile(){
        File file = new File(fileName);
        if (!file.exists()) return;

        try(BufferedReader readFile = new BufferedReader(new FileReader(fileName))) {
            String line;
            Equipment.getRecord().clear(); //clearkan dulu nanti tak duplicate bila nak load info
            while ((line = readFile.readLine()) != null){
                Equipment.getRecord().add(Equipment.fromTXTFile(line));
            }
            loadSuccess();
        } catch (IOException e) {
            System.out.println("File cannot be load");
        }
    }
    
    public void totalBorowed(){
        System.out.println("\n----------Total Number of Borrowed Equipment----------");
        if (records.isEmpty()){
            System.out.println("\nNo records found.");
            return;
        }

        int total = 0;
        for (int i = 0; i < records.size(); i++){ //loop dalam semua records
            total++;
        }
        System.out.println();
        System.out.println("\nTotal number of all borrowed equipment: " + total);
    }

    //(v) determine whether the borrowing duration is acceptable.
    public void durationRule(){
        System.out.println("\n---------Check Borrowing Duration---------");
        if (records.isEmpty()){
            System.out.println("No records found");
        }
        else {
            System.out.print("\nEnter student ID: ");
            String studentID = scanner.nextLine();
            boolean found = false;

            for (int i =0; i < records.size(); i++){
                if (records.get(i).getID().equalsIgnoreCase(studentID)){
                    int borrowDuration = records.get(i).getDuration();

                    System.out.println("\nStudeng Name: " + records.get(i).getName());
                    System.out.println("Equipment Type: " + records.get(i).getType());
                    //System.out.println("Borrow Duration: " + records.get(i).getDuration() + " days");
                    System.out.println("Borrow Duration: " + borrowDuration + " days");

                    if (borrowDuration > 7){
                        System.out.println("\nRequires special approval");
                    }
                    else if (borrowDuration >= 4 && borrowDuration <= 7) {
                        System.out.println("\nExtended borrowing");
                    }
                    else {
                        System.out.println("\nNormal borrowing");
                    }
                    found = true;
                }
            }

            if (!found){
                System.out.println("No record found for your student ID");
            }         
        }
    }
}




