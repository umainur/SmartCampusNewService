import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

enum IssueType {
    ELECTRICAL, NETWORK, FURNITURE, CLEANLINESS, AIR_CONDITIONING
}
enum PriorityLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}
enum ReportStatus {
    OPEN, IN_PROGRESS, RESOLVED, CLOSED
}

class FReportIssue{
    private IssueType issueType;
    private PriorityLevel priorityLevel;
    private ReportStatus reportStatus;
    private String description;
    private String reporterName;
    private String reporterID;

    private static Vector<FReportIssue> issueList = new Vector<>();

    //constructor
    FReportIssue (IssueType issueType, PriorityLevel priorityLevel, ReportStatus reportStatus, String description, String reporterName, String reporterID){
        this.issueType = issueType;
        this.priorityLevel = priorityLevel;
        this.reportStatus = ReportStatus.OPEN; //default status bila ade add new issue, so automatic jadi set as OPEN
        this.description = description;
        this.reporterName = reporterName;
        this.reporterID = reporterID;
    }

    //getters and setters
    public void setRName(String reporterName){ this.reporterName = reporterName;}
    public String getRName() { return reporterName;}

    public void setID(String reporterID){ this.reporterID = reporterID;}
    public String getID() { return reporterID;}

    public void setIssueType(IssueType issueType){ this.issueType = issueType;}
    public IssueType getIssueType() { return issueType;}

    public void setPriorityLevel(PriorityLevel priorityLevel){ this.priorityLevel = priorityLevel;}
    public PriorityLevel getPriorityLevel() { return priorityLevel;}

    public void setReportStatus(ReportStatus reportStatus){ this.reportStatus = reportStatus;}
    public ReportStatus getReportStatus() { return reportStatus;}

    public void setDesc(String description){ this.description = description;}
    public String getDesc() {return description;}


    
    public static Vector<FReportIssue> getRecord() {return issueList;}
    public static int getTotalIssues() {return issueList.size();}
    
    //nak masukkan semua borrowing equipment dalam file txt
    public String toTXTFile(){
        return reporterName + "|" + reporterID + "|" + issueType + "|" + priorityLevel + "|" + reportStatus + "|" + description;
    }

    //baca dari txt file untuk display
    public static FReportIssue fromTXTFile(String issueInfo){
        String[] info = issueInfo.split("\\|");
        String reporterName = info[0];
        String reporterID = info[1];
        IssueType issueType = IssueType.valueOf(info[2]);
        PriorityLevel priorityLevel = PriorityLevel.valueOf(info[3]);
        ReportStatus reportStatus = ReportStatus.valueOf(info[4]); 
        String desc= info[5];
        return new FReportIssue(issueType, priorityLevel, reportStatus, desc, reporterName, reporterID);
    }
    
}

public class IssueReporting extends CampusService {

    private Scanner scanner;
    private Vector<FReportIssue> issueList = FReportIssue.getRecord();
    
    IssueReporting(Scanner input){
        super("Report Issue", "issues.txt");
        this.scanner = input;
        loadFromFile();
    }

    @Override
    public void displayMenu(){
        int choice;

        do{
            System.out.println("\n---------Campus Issue Reporting Menu -----------");
            System.out.println("1. Add New Issue Report");
            System.out.println("2. Display All Issues");
            System.out.println("3. Search Issue by Reporter ID");
            System.out.println("4. Count Total Number of Issues by Priority Level");
            System.out.println("5. Update Issue Status");
            System.out.println("6. Save Record to File");
            System.out.println("7. Load Record from File");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                case 1:
                    addRecord();  break;
                   
                case 2:
                    displayRecords(); break;
                case 3:
                    System.out.println("Enter ID: ");
                        searchRecord(scanner.nextLine().trim()); 
                        break;
                case 4:
                    countIssue(); break;
                case 5:
                    System.out.println("Enter ID: ");
                        updateStatus(scanner.nextLine().trim()); 
                        break;
                case 6:
                    saveToFile(); break;
                case 7:
                    loadFromFile(); break;
                case 0:
                    break;
                    
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please try again.");
            } 
        } while (choice != 0);
    }

    @Override //(i) add new issue 
    public void addRecord(){
        System.out.println("\n---------Add New Issue---------");
        System.out.print("Enter Reporter ID: ");
        String reporterID = scanner.nextLine();
        System.out.print("Enter Reporter Name: ");
        String reporterName = scanner.nextLine();
        
        
        IssueType issueType = null;
        while ( issueType == null) {
            System.out.println("\nList of Issue Type : \n1. ELECTRICAL\n2. NETWORK\n3. FURNITURE\n4. CLEANLINESS\n5. AIR_CONDITIONING");
            System.out.print("Enter issue choice: ");
        
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                case 1:
                    issueType = IssueType.ELECTRICAL; break;
                case 2:
                    issueType = IssueType.NETWORK; break;
                case 3:
                    issueType = IssueType.FURNITURE; break;
                case 4:
                    issueType = IssueType.CLEANLINESS; break;
                case 5:
                    issueType = IssueType.AIR_CONDITIONING; break;
                default:
                    System.out.println("\nInvalid choice. Please try again!");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice.Please try again");
            }
        
        
        }
        
        PriorityLevel priorityLevel = null;
        while (priorityLevel == null){
            System.out.println("\nPriority Level: \n1. LOW \n2. MEDIUM \n3. HIGH\n4. CRITICAL");
            System.out.print("Enter priority level: ");
            try {
                int level = Integer.parseInt(scanner.nextLine().trim());
                switch (level){
                    case 1:
                        priorityLevel = PriorityLevel.LOW; break;
                    case 2:
                        priorityLevel = PriorityLevel.MEDIUM; break;
                    case 3:
                        priorityLevel = PriorityLevel.HIGH; break;
                    case 4:
                        priorityLevel = PriorityLevel.CRITICAL; break;
                    default:
                        System.out.println("\nInvalid choice. Please try again!");
                        continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid choice. Please try again!");
            }
        }
       
        System.out.print("Enter Description: ");
        String desccription = scanner.nextLine();
        ReportStatus reportStatus = ReportStatus.OPEN; //default status bila ade add new issue, so automatic jadi set as OPEN

        //buat object baru untuk issue yang baru dibuat
        FReportIssue newIssue = new FReportIssue(issueType, priorityLevel, reportStatus, desccription, reporterName, reporterID); 
        FReportIssue.getRecord().addElement(newIssue); //add object baru ke dalam vector issueList
        //System.out.println("\nNew issue added successfully!");
        saveToFile();
    }

    @Override //(ii) display all issues
    public void displayRecords(){
        System.out.println("\n----------List of Repoted Issues---------");
        if (issueList.isEmpty()) {
            System.out.println("\nNo issues reported.");
        } else {
            System.out.println("\nAll Campus Issues: ");
            for (int i = 0; i < issueList.size(); i++) {
                //ReportIssue issue = issueList.get(i);
                System.out.println("\nIssue " + (i + 1) + ":");
                System.out.println("Reporter ID: " + issueList.get(i).getID());
                System.out.println("\nReporter Name: " + issueList.get(i).getRName());
                System.out.println("Issue Type: " + issueList.get(i).getIssueType());
                System.out.println("Priority Level: " + issueList.get(i).getPriorityLevel());
                System.out.println("Description: " + issueList.get(i).getDesc());
                System.out.println("Report Status: " + issueList.get(i).getReportStatus());
            }
        }
    }

    @Override //(iii) search issue by reporter ID
    public void searchRecord(String reporterID){
        boolean found = false;
        for (int i = 0; i < issueList.size(); i++) {
            if (issueList.get(i).getID().equalsIgnoreCase(reporterID)){
                System.out.println("\nIssue found!");

                System.out.println("Reporter ID: " + issueList.get(i).getID());
                System.out.println("\nReporter Name: " + issueList.get(i).getRName());
                System.out.println("Issue Type: " + issueList.get(i).getIssueType());
                System.out.println("Priority Level: " + issueList.get(i).getPriorityLevel());
                System.out.println("Description: " + issueList.get(i).getDesc());
                System.out.println("Report Status: " + issueList.get(i).getReportStatus());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No issues found for the specified reporter ID.");
        }
    }

    //(iv)count total number of issue by priority level
    public void countIssue(){
        System.out.println("\n---Count Total Number of Issues by Priority Level---");
        PriorityLevel plevel = null;
        while (plevel == null){
            System.out.println("\nPriority Level: \n1. LOW \n2. MEDIUM \n3. HIGH\n4. CRITICAL");
            System.out.print("Enter priority level to be count: ");
            int level = Integer.parseInt(scanner.nextLine().trim());
            
            switch (level){
                case 1:
                    plevel = PriorityLevel.LOW; break;
                case 2:
                    plevel = PriorityLevel.MEDIUM; break;
                case 3:
                    plevel= PriorityLevel.HIGH; break;
                case 4:
                    plevel = PriorityLevel.CRITICAL; break;
                default:
                    System.out.println("\nInvalid choice. Please try again!");
                    continue;
            }
            break;
        }

        int count = 0;
        for (int i = 0; i < issueList.size(); i++) {
            if (issueList.get(i).getPriorityLevel() == plevel){
                count++;
            }
        }
        System.out.println("\nTotal number of issues for level " + plevel + ": " + count);
    }

    //(v) update issue status
    public void updateStatus(String reporterID){
        System.out.println("\n----------Update Issue Status----------");
        boolean foundID = false;
        for (int i = 0; i < issueList.size(); i++) {
            if (issueList.get(i).getID().equalsIgnoreCase(reporterID)){

                System.out.println("\nIssue found! ");
                System.out.println("\nDescription: " + issueList.get(i).getDesc());
                System.out.println("Current Status: " + issueList.get(i).getReportStatus());

                ReportStatus newStatus = null;
                while(newStatus == null){
                    System.out.println("Choose new status: \n1. OPEN\n2. IN_PROGRESS\n3. RESOLVED\n4. CLOSED");
                    System.out.print("Enter chocie:");
                    int choice = Integer.parseInt(scanner.nextLine().trim());

                    switch (choice) {
                        case 1:
                            newStatus = ReportStatus.OPEN;
                            break;
                        case 2:
                            newStatus = ReportStatus.IN_PROGRESS;
                            break;
                        case 3:
                            newStatus = ReportStatus.RESOLVED;
                            break;
                        case 4:
                            newStatus = ReportStatus.CLOSED;
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again!");
                            continue;
                    }
                }
                issueList.get(i).setReportStatus(newStatus);
                System.out.println("\nIssue status updated successfully!");
                foundID = true;
            }
        }
        if (!foundID) {
            System.out.println("No issue found");
        }
    }

    @Override
    public void saveToFile(){
        try (BufferedWriter saveFile = new BufferedWriter(new FileWriter(fileName))){
            for (int i = 0; i < issueList.size(); i++){
                saveFile.write(issueList.get(i).toTXTFile());
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
            FReportIssue.getRecord().clear(); //clearkan dulu nanti tak duplicate bila nak load info
            while ((line = readFile.readLine()) != null){
                FReportIssue.getRecord().add(FReportIssue.fromTXTFile(line));
            }
            loadSuccess();
        } catch (IOException e) {
            System.out.println("File cannot be load");
        }
    }
}




































































