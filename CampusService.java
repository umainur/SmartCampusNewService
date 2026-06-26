//superclass

public class CampusService{
    protected String serviceType; 
    protected String fileName; 

    //constructor
    CampusService(String serviceType, String fileName){
        this.serviceType = serviceType;
        this.fileName = fileName;
    }

    //polymorphism (semua subclass override methods)

    //display sub-menu
    public void displayMenu(){};

    //add new record
    public void addRecord(){};

    //display record
    public void displayRecord(){};

    //seacrh record by studend ID
    public void searchRecord(String id){};

    //save all record to file 
    public void saveToFile(){};

    //load all record from file 
    public void loadFromFile(){};

    public String getServiceType(){ return serviceType;}
    public String getFileName(){ return fileName;}

    //confirmation record dah save dalam file
    protected void saveSuccess(){
        System.out.println("Records is successfully save to " + fileName);
    }

    //confirmation record dah load from file
    protected void loadSuccess(){
        System.out.println("Records is loaded from " + fileName);
    }
}