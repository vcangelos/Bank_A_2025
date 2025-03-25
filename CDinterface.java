import java.util.*;
import java.io.*;

class CDWelcomeScreen {
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
            double userAmount = 0.00;
            // Ask user if they want to purchase one of the displayed CDs
            System.out.println("Do any of these options appeal to you? (y/n)");
            String selectResponse = userinput.nextLine();
            if (selectResponse.equalsIgnoreCase("y")) {
                System.out.println("Which CD would you like to purchase? (Enter the option number)");
                int selection = userinput.nextInt();
                userinput.nextLine(); // Consume newline after nextInt()

                // Handle invalid input for selection
                if (selection > 0 && selection <= CDI.getCDOptionsSize()) {
                    // Asking the user to input the amount they want to invest
                    System.out.println("Enter the amount you want to invest into this CD (in dollars):");
                    userAmount = userinput.nextDouble();
                    userinput.nextLine(); // Consume newline after nextDouble()

                    if (userAmount > 0) {
                        // Proceeding with the purchase with the user-specified amount
                        double maturityAmount = CDI.getCD(selection).calculateMaturityAmount(userAmount);
                        System.out.println("You have selected CD option " + selection + " with an investment of $" + userAmount + ". At maturity, it will be worth $" + maturityAmount);
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
            String earlyWithdrawResponse = userinput.nextLine();
            if (earlyWithdrawResponse.equalsIgnoreCase("yes")) {
                double penalty = 0.01 * userAmount; // Default penalty rate (1%)
                System.out.println("You will forfeit all interest and be charged a late withdrawal fee of $" + penalty + ".");
                System.out.println("Your CD has been withdrawn early, and no interest is earned.");
            } else if (earlyWithdrawResponse.equalsIgnoreCase("no")) {
                System.out.println("No problem! Let us know when you're ready (we recommend waiting until it matures).");
            } else {
                System.out.println("Invalid answer, interpreting as no.");
            }

        } else if (response.equalsIgnoreCase("no")) {
            System.out.println("No problem! If you change your mind, let us know.");
        } else {
            System.out.println("Invalid input. Please respond with 'yes' or 'no'.");
        }
    }

    public static void addUser(String username,String dateOfBirth, int ID) {
        try {
            Scanner csvreader = new Scanner(new File("src/CD.csv"));
            File tempFile = new File("src/temp.csv");
            PrintWriter out = new PrintWriter(new File("src/temp.csv"));

            while (csvreader.hasNextLine()) {
                String line = csvreader.nextLine().trim();
                String[] UserDatacopier = line.split(",");
                for (int i = 0; i < UserDatacopier.length; i++) {
                    UserDatacopier[i] = UserDatacopier[i].trim();
                }
                out.println(String.join(",", UserDatacopier));
            }

            out.println(String.join(",", username,dateOfBirth  Integer.toString(ID),));

            csvreader.close();
            out.close();

            File userDataFile = new File("src/CD.csv");
            if (userDataFile.exists()) {
                userDataFile.delete();
            }
            tempFile.renameTo(new File("src/CD.csv"));
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading or writing to the file.");
            e.printStackTrace();
        }
    }
}

class CD {
    double term;
    double Principal;
    double IR;

    // Constructor with parameters
    public CD(double t, double p, double ir) {
        this.term = t;
        this.Principal = p;
        this.IR = ir;
    }

    // Method to calculate maturity amount based on principal, interest rate, and term
    public double calculateMaturityAmount(double principal) {
        return principal * (1 + ((IR) / 100) * (term / 12));
    }

    // Display CD information
    public void displayCD() {
        System.out.println("Term: " + term + " month(s), Principal: $" + Principal + ", Interest Rate: " + IR + "%");
    }

    // Getter methods
    public double getTerm() {
        return this.term;
    }

    public double getIR() {
        return this.IR;
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

    public static CD getCD(int selection) {
        return CDoptions.get((selection - 1));
    }
}
