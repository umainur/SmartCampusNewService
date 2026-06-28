import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;

enum FacilityType{STUDY_ROOM, COMPUTER_LAB, SPORTS_COURT, SEMINAR_HALL}
enum TimeSlot{MORNING, AFTERNOON, EVENING}
enum BookingStatus{PENDING, APPROVED, REJECTED, CANCELLED}               

class Bookings {
    private String bookingId;
    private String studentId;
    private FacilityType facilityType;
    private TimeSlot timeSlot;
    private int durationHours;
    private String bookingDate;  
    private BookingStatus status;

    public Bookings(String bi, String si, FacilityType ft, TimeSlot ts, int dh, String bd) {
        this.bookingId     = bi;
        this.studentId     = si;
        this.facilityType  = ft;
        this.timeSlot      = ts;
        this.durationHours = dh;
        this.bookingDate   = bd;
        this.status        = BookingStatus.PENDING; 
    }
    public String getBookingId() {return bookingId;}
    public String getStudentId() {return studentId;}
    public FacilityType getFacilityType() {return facilityType;}
    public TimeSlot getTimeSlot() {return timeSlot;}
    public int getDurationHours() {return durationHours;}
    public String getDate() {return bookingDate;}
    public BookingStatus getStatus() {return status;}
    public void setStatus(BookingStatus status) {this.status = status;}
    

    public void displayInfo() {
        System.out.println("\n--------Booking Information--------");
        System.out.println("Booking ID  : " + bookingId);
        System.out.println("Student ID  : " + studentId);
        System.out.println("Facility    : " + facilityType);
        System.out.println("Time Slot   : " + timeSlot);
        System.out.println("Duration    : " + durationHours + " hour(s)");
        System.out.println("Date        : " + bookingDate);
        System.out.println("Status      : " + status);
        System.out.println("-----------------------------------");
    }
}

public class FacilityBookings extends CampusService {
    private Vector<Bookings> bookings = new Vector<Bookings>();
    private Scanner scanner;
    private int bookingCounter = 1;

    public FacilityBookings(Scanner scanner) {
        super("Facility Booking", "facility_bookings.txt");
        this.scanner = scanner;
        loadFromFile();
    }

    public int getBookingCount() { return bookings.size();}

    @Override
    public void displayMenu() {
        int option;
        do {
            System.out.println("\n--------Facility Booking Menu----------\n");
            System.out.println("1. Create a new booking");
            System.out.println("2. Display All Bookings ");
            System.out.println("3. Search Bookings by Student ID");
            System.out.println("4. Calculate total booking duration for a student");
            System.out.println("5. Update Booking Status");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select an option: ");
            option = scanner.nextInt();
            scanner.nextLine(); 

            switch (option) {
                case 1:
                    addRecord();
                    break;
                case 2:
                    displayRecords();
                    break;
                case 3:
                    searchRecord();
                    break;
                case 4:
                    calculateTotalDuration();
                    break;
                case 5:
                    updateBookingStatus();
                    break;
                case 0:
                    return;    
                default:
                    System.out.println("Invalid option. Please try again.\n");
            }
        } while (option != 0);
    }
    
    @Override
    public void addRecord() {
        System.out.println("\n-------Create New Booking-------");
        System.out.print("Student ID: ");
        String si = scanner.nextLine().toUpperCase();
        FacilityType ft = readFacilityType();
        if (ft == null) return;
        TimeSlot ts = readTimeSlot();
        if (ts == null) return;
        System.out.print("\nDuration (hours): ");
        int duration = readInt();
        System.out.print("\nBooking Date (DD/MM/YYYY): ");
        String date = scanner.nextLine().trim();
        
        String bookingId = "BK" + String.format("%03d", bookingCounter++);
        bookings.add(new Bookings(bookingId, si, ft, ts, duration, date));
        saveToFile();
        System.out.println("Booking added successfully! \nBooking ID: " + bookingId + "\n");
    }
    @Override
    public void displayRecords() {
        if (bookings.size() == 0) {
            System.out.println("No bookings recorded yet.");
            return;
        }
        System.out.println("\nTotal Bookings: " + bookings.size());
        for (int i = 0; i < bookings.size(); i++) {
            bookings.get(i).displayInfo();
        }
    }

    @Override
    public void searchRecord(String id) {
        boolean found = false;
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getStudentId().equalsIgnoreCase(id)) {
                bookings.get(i).displayInfo();
                found = true;
            }
        }
        if (!found)
            System.out.println("No bookings found for Student ID: " + id);
    } 

    private void searchRecord() {
        System.out.println("\n-------Search Booking by Student ID-------");
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().toUpperCase();
        searchRecord(id);   // delegate to the overridden method
    }

/* 
    private void searchRecord() {
       System.out.println("\n-------Search Booking by Student ID-------");
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().toUpperCase();
 
        boolean found = false;
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getStudentId().equalsIgnoreCase(id)) {
                bookings.get(i).displayInfo();
                found = true;
            }
        }
        if (!found)
            System.out.println("No bookings found for Student ID: " + id);
    }
*/
    @Override
    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (int i = 0; i < bookings.size(); i++) {
                Bookings b = bookings.get(i);
                pw.println(b.getBookingId() + "|" +
                           b.getStudentId() + "|" +
                           b.getFacilityType() + "|" +
                           b.getTimeSlot() + "|" +
                           b.getDurationHours() + "|" +
                           b.getDate() + "|" +
                           b.getStatus());
            }
            saveSuccess(); // inherited from CampusService
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }

    @Override
    public void loadFromFile() {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("No saved data found for " + serviceType);
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            bookings.clear();
            bookingCounter = 1;
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 7) continue;
                String bookingId  = parts[0];
                String studentId  = parts[1];
                FacilityType ft   = FacilityType.valueOf(parts[2]);
                TimeSlot ts       = TimeSlot.valueOf(parts[3]);
                int duration      = Integer.parseInt(parts[4]);
                String date       = parts[5];
                BookingStatus status = BookingStatus.valueOf(parts[6]);

                Bookings b = new Bookings(bookingId, studentId, ft, ts, duration, date);
                b.setStatus(status);
                bookings.add(b);
                bookingCounter++;
            }
            loadSuccess(); // inherited from CampusService
        } catch (IOException e) {
            System.out.println("Error loading from file: " + e.getMessage());
        }
    }

    private void calculateTotalDuration() {
        System.out.println("\n-------Calculate Total Booking Duration-------");
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().toUpperCase();
 
        int totalHours = 0;
        int count = 0;
        for (int i = 0; i < bookings.size(); i++) {
            Bookings b = bookings.get(i);
            if (b.getStudentId().equalsIgnoreCase(id)) {
                totalHours += b.getDurationHours();
                count++;
            }
        }
 
        if (count == 0) {
            System.out.println("No bookings found for Student ID: " + id);
        } else {
            System.out.println("\nStudent ID     : " + id);
            System.out.println("Total Bookings : " + count);
            System.out.println("Total Duration : " + totalHours + " hour(s)");
        }
    }

    private void updateBookingStatus() {    
        System.out.println("\n-------Update Booking Status-------");
        System.out.print("Enter Booking ID (e.g. BK001): ");
        String bookingId = scanner.nextLine().toUpperCase();
 
        int index = -1;
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getBookingId().equalsIgnoreCase(bookingId)) {
                index = i;
                break;
            }
        }
 
        if (index == -1) {
            System.out.println("Booking ID not found: " + bookingId);
            return;
        }
 
        bookings.get(index).displayInfo();
        BookingStatus newStatus = readBookingStatus();
        if (newStatus == null) return;
        bookings.get(index).setStatus(newStatus);
        bookings.set(index, bookings.get(index));
        System.out.println("Status successfully updated to " + newStatus + "\n");
        saveToFile();
    }

    private FacilityType readFacilityType() {
        System.out.println("\nFacility Type: \n1.STUDY_ROOM  \n2.COMPUTER_LAB  \n3.SPORTS_COURT  \n4.SEMINAR_HALL");
        System.out.print("Enter choice: ");
        switch (readInt()) {
            case 1: return FacilityType.STUDY_ROOM;
            case 2: return FacilityType.COMPUTER_LAB;
            case 3: return FacilityType.SPORTS_COURT;
            case 4: return FacilityType.SEMINAR_HALL;
            default: System.out.println("Invalid facility type."); return null;
        }
    }
    private TimeSlot readTimeSlot() {
        System.out.println("\nTime Slot: \n1.MORNING  \n2.AFTERNOON  \n3.EVENING");
        System.out.print("Enter choice: ");
        switch (readInt()) {
            case 1: return TimeSlot.MORNING;
            case 2: return TimeSlot.AFTERNOON;
            case 3: return TimeSlot.EVENING;
            default: System.out.println("Invalid time slot."); return null;
        }
    }

    private BookingStatus readBookingStatus() {
    System.out.println("New Status: \n1.PENDING  \n2.APPROVED  \n3.REJECTED  \n4.CANCELLED");
    System.out.print("Enter choice: ");
    switch (readInt()) {
        case 1: return BookingStatus.PENDING;
        case 2: return BookingStatus.APPROVED;
        case 3: return BookingStatus.REJECTED;
        case 4: return BookingStatus.CANCELLED;
        default: System.out.println("Invalid status."); return null;
    }
}

   private int readInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid input."); return -1; }
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        FacilityBookings fbm = new FacilityBookings(in);
        fbm.displayMenu();
    }

}



