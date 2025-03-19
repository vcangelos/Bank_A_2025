import java.util.*;

public class CDWelcomeScreen {
    public static void main(String[] args) {
        Scanner userinput = new Scanner(System.in);
        
        // Asking for user's name and welcoming
        System.out.println("Enter User Name:");
        String name = userinput.nextLine();
        System.out.println("Welcome New User " + name);
        
        // Ask if the user wants to create a certificate of deposit
        System.out.println("Would you like to create a certificate of deposit at this time? (yes/no)");
        String response = userinput.nextLine();
        
        // Handle user input for creating a CD
        if (response.equalsIgnoreCase("yes")) {
            System.out.println("Great! Let's get started with setting up your certificate of deposit.");
            
            // Create CDinterface instance to manage CDs
            CDinterface CDI = new CDinterface();
            
            // Add predefined CDs to the list
            CDI.addCD(new CD(3, 5000.00, 1.45));
            CDI.addCD(new CD(6, 5000.00, 2.00));
            CDI.addCD(new CD(12, 5000.00, 2.75));
            
            // Display the available CD options
            CDI.displayCD();
            double userAmount=0.00;
            // Ask user if they want to purchase one of the displayed CDs
            System.out.println("Do any of these options appeal to you? (y/n)");
            String selectResponse = userinput.nextLine();
            if (selectResponse.equalsIgnoreCase("y")) {
                System.out.println("Which CD would you like to purchase? (Enter the option number)");
                int selection = userinput.nextInt();
                
                // Handle invalid input for selection
                if (selection > 0 && selection <= CDI.getCDOptionsSize()) {
                    // Asking the user to input the amount they want to invest
                    System.out.println("Enter the amount you want to invest into this CD (in dollars):");
                    userAmount = userinput.nextDouble();
                    if (userAmount > 0) {
                        // Proceeding with the purchase with the user-specified amount
                        System.out.println("You have selected CD option " + selection + " with an investment of $" + userAmount + ". At maturity, it will be worth $"+((userAmount)*(1+((CDI.getCD(selection).getIR())/100)*((CDI.getCD(selection).getTerm())/12))));
                        // Proceed with the CD purchase simulation
                        CDI.PurchaseCD(selection, userAmount);
                    } else {
                        System.out.println("Invalid investment amount. No CD purchased.");
                    }
                } else {
                    System.out.println("Invalid selection. No CD purchased.");
                }
            } else {
                System.out.println("No CD selected.");
            }
            
            // Ask if the user wants to withdraw early
            System.out.println("Do you want to withdraw your CD early? (yes/no)");
            String earlyWithdrawResponse = userinput.next();
            if (earlyWithdrawResponse.equalsIgnoreCase("yes")) {
                System.out.println("You will forfeit all interest and be charged a late withdrawal fee of $"+ (0.01 * (userAmount))+".");
                // Simulate early withdrawal fee
                System.out.println("Your CD has been withdrawn early, and no interest is earned.");
            }
            else if(earlyWithdrawResponse.equalsIgnoreCase("no")){
                System.out.println("No problem! Let us know when you're ready (we recommend waiting until it matures).");
            }
            else{
                System.out.println("invalid answer, interpreting no.");
            }
            
        } else if (response.equalsIgnoreCase("no")) {
            System.out.println("No problem! If you change your mind, let us know.");
        } else {
            System.out.println("Invalid input. Please respond with 'yes' or 'no'.");
        }
    }
}

class CD {
    double term;
    double Principal;
    double IR;

    // Default constructor
    public CD() {
        this.term = 0;
        this.Principal = 0.0;
        this.IR = 0.0;
    }

    // Constructor with parameters
    public CD(double t, double p, double ir) {
        this.term = t;
        this.Principal = p;
        this.IR = ir;
    }

    // Getter methods
    public double getTerm() {
        return this.term;
    }

    public double getIR() {
        return this.IR;
    }

    // Display CD information
    public void displayCD() {
        System.out.println("Term: " + term + " month(s), Principal: $" + Principal + ", Interest Rate: " + IR + "%");
    }
}

class CDinterface {
    private static ArrayList<CD> CDoptions = new ArrayList<>();

    // Add a CD to the list
    public static void addCD(CD c) {
        CDoptions.add(c);
    }

    // Add multiple CDs
    public static void addCDs(ArrayList<CD> cds) {
        CDoptions.addAll(cds);
    }

    // Get the size of available CDs
    public static int getCDOptionsSize() {
        return CDoptions.size();
    }

    // Display all CDs
    public static void displayCD() {
        System.out.println("Available Certificate of Deposit Options:");
        for (int i = 0; i < CDoptions.size(); i++) {
            System.out.println("Option " + (i + 1) + ":");
            CDoptions.get(i).displayCD();
        }
    }

    // Method to simulate CD purchase
    public static void PurchaseCD(int selection, double userAmount) {
        CD selectedCD = CDoptions.get(selection - 1);
        System.out.println("You have purchased a " + selectedCD.getTerm() + "-month CD with $" + userAmount + " at an interest rate of " + selectedCD.getIR() + "%.");
    }
    public static CD getCD(int selection){
       CD Selected=CDoptions.get((selection-1));
        return Selected;
    }
    

    // Method to generate random CDs
    

    // Helper method to round numbers to 2 decimal places
    private static double round(double value, int places) {
        double factor = Math.pow(10, places);
        return Math.round(value * factor) / factor;
    }
}
