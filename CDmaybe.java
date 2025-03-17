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
            
            // Optionally, generate random CDs and add them
            System.out.println("Would you like to see some randomly generated CD options? (yes/no)");
            String randomResponse = userinput.nextLine();
            if (randomResponse.equalsIgnoreCase("yes")) {
                ArrayList<CD> randomCDs = CDI.generateRandomCDs(5); // Generate 5 random CDs
                CDI.addCDs(randomCDs);
            }
            
            // Display the available CD options
            CDI.displayCD();
            
            // Ask user if they want to purchase one of the displayed CDs
            System.out.println("Do any of these options appeal to you? (y/n)");
            String selectResponse = userinput.nextLine();
            if (selectResponse.equalsIgnoreCase("y")) {
                System.out.println("Which CD would you like to purchase? (Enter the option number)");
                int selection = userinput.nextInt();
                
                // Handle invalid input for selection
                if (selection > 0 && selection <= CDI.getCDOptionsSize()) {
                    System.out.println("You have selected CD option " + selection + ". Proceeding with the purchase...");
                    // Simulate CD purchase here
                    CDI.PurchaseCD(selection);
                } else {
                    System.out.println("Invalid selection. No CD purchased.");
                }
            } else {
                System.out.println("No CD selected.");
            }
            
        } else if (response.equalsIgnoreCase("no")) {
            System.out.println("No problem! If you change your mind, let us know.");
        } else {
            System.out.println("Invalid input. Please respond with 'yes' or 'no'.");
        }
    }
}

class CD {
    int term;
    double Principal;
    double IR;

    // Default constructor
    public CD() {
        this.term = 0;
        this.Principal = 0.0;
        this.IR = 0.0;
    }

    // Constructor with parameters
    public CD(int t, double p, double ir) {
        this.term = t;
        this.Principal = p;
        this.IR = ir;
    }

    // Getter methods
    public int getTerm() {
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
    public static void PurchaseCD(int selection) {
        CD selectedCD = CDoptions.get(selection - 1);
        System.out.println("You have purchased a " + selectedCD.getTerm() + "-month CD with $" + selectedCD.Principal + " at an interest rate of " + selectedCD.getIR() + "%.");
    }

    // Method to generate random CDs
    public static ArrayList<CD> generateRandomCDs(int x) {
        ArrayList<CD> newCDs = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < x; i++) {
            int term = rand.nextInt(24) + 1; // Random term between 1-24 months
            double principal = 500 + (5000 - 500) * rand.nextDouble(); // Random principal between $500 and $5000
            double interestRate = 1 + (5 - 1) * rand.nextDouble(); // Random interest rate between 1% and 5%
            interestRate = round(interestRate, 2);

            CD cd = new CD(term, principal, interestRate);
            newCDs.add(cd);
        }
        return newCDs;
    }

    // Helper method to round numbers to 2 decimal places
    private static double round(double value, int places) {
        double factor = Math.pow(10, places);
        return Math.round(value * factor) / factor;
    }
}
/// we still have to finish up the early withdraw fee and we need to make a user inputed amount of money they want to put into the CD with a fixed interest rate
//early withdraw = no intrest rate plus a late fee
