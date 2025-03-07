import java.util.ArrayList;

class BankProject {
    private ArrayList<String> checklist;
    
    public BankProject() {
        checklist = new ArrayList<>();
        initializeChecklist();
    }
    
    private void initializeChecklist() {
        checklist.add("Credit Card - Display Credit Score + Balance + Owed Amount");
        checklist.add("Home Equity - Display Loan Info + Home Value");
        checklist.add("Bank Menu - Display Basic User Info");
        checklist.add("Mortgage - Display Mortgage");
        checklist.add("Savings - Display balance");
        checklist.add("Get ID from Main File");
    }
   
   //Getter Method 
    public ArrayList<String> getChecklist() {
        return checklist;
    }
    
    // Setter Method 
    public void setChecklist(String item) {
        checklist.add(item);
    }
    
    //Display Method 
    public void displayChecklist() {
        for (int i = 0; i < checklist.size(); i++) {
            System.out.println((i + 1) + ". " + checklist.get(i));
        }
    }
    
    public static void main(String[] args) {
        BankProject bankProject = new BankProject();
        
        // Example: Adding a new item 
        bankProject.setChecklist("New Feature - Display Transaction History");
        
        // Display the updated checklist
        System.out.println("\nUpdated Checklist:");
        bankProject.displayChecklist();
    }
}


