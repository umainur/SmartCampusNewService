import java.security.Provider.Service;
import java.util.Scanner;

public class MainSystem {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        ProfileManager pm = new ProfileManager(in);
        FacilityBookings fb = new FacilityBookings(in);
        BorrowEquipment eb = new BorrowEquipment(in);
        IssueReporting ir = new IssueReporting(in);
        FeedbackSystem fs = new FeedbackSystem(in);

        CampusService[] services = { pm, fb, eb, ir, fs };

        boolean running = true;
        while (running) {
            printMainMenu();

            int choice;
            try {
                choice = Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    pm.displayMenu();
                    break;
                case 2:
                    fb.displayMenu();
                    break;
                case 3:
                    eb.displayMenu();
                    break;
                case 4:
                    ir.displayMenu();
                    break;
                case 5:
                    fs.displayMenu();
                    break;
                case 6:
                    displaySummary(pm, fb);
                    break;
                case 0:
                    System.out.println("\nGoodbye, Thank you for using Smart Campus System.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        in.close();
    }

    private static void printMainMenu() {
        System.out.println();
            System.out.println("--------------------------------------");
            System.out.println("SMART CAMPUS SERVICE MANAGEMENT SYSTEM");
            System.out.println("---------------------------------------");
            System.out.println("1. Manage Student Profiles");
            System.out.println("2. Manage Facility Bookings");
            System.out.println("3. Manage Equipment Borrowing");
            System.out.println("4. Manage Facility Issue Reports");
            System.out.println("5. Student Feedback Collection");
            System.out.println("6. View System Summary");
            System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void displaySummary(ProfileManager pm, FacilityBookings fb) {
        System.out.println("\n--------------------------------------");
        System.out.println("           SYSTEM SUMMARY             ");
        System.out.println("--------------------------------------");
        System.out.printf( "\n  Total Registered Students :" + pm.getStudentCount());
        System.out.printf( "\n  Total Facility Bookings   : " + fb.getBookingCount());
        System.out.printf( "\n  Total Equipment Borrowed  : " +  Equipment.getTotalBorrowed());
        System.out.printf( "\n  Total Issue Reports       : " + FReportIssue.getTotalIssues());
        
    }
}
