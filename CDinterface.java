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

        if (response.equalsIgnoreCase("yes")) {
            System.out.println("Great! Let's get started with setting up your certificate of deposit.");

            CDinterface CDI = new CDinterface();
            
            // Add predefined CDs to the list
            CDI.addCD(new CD(3, 5000.00, 1.45));
            CDI.addCD(new CD(6, 5000.00, 2.00));
            CDI.addCD(new CD(12, 5000.00, 2.75));
            
            CDI.displayCD();
            
            System.out.println("Do any of these options appeal to you? (y/n)");
            String selectResponse = userinput.nextLine();
            
            double userAmount = 0.00;
            if (selectResponse.equalsIgnoreCase("y")) {
                System.out.println("Which CD would you like to purchase? (Enter the option number)");
                int selection = userinput.nextInt();
                userinput.nextLine();
                
                if (selection > 0 && selection <= CDI.getCDOptionsSize()) {
                    System.out.println("Enter the amount you want to invest into this CD (in dollars):");
                    userAmount = userinput.nextDouble();
                    userinput.nextLine();

                    if (userAmount > 0) {
                        double maturityAmount = CDI.getCD(selection).calculateMaturityAmount(userAmount);
                        System.out.println("You have selected CD option " + selection + " with an investment of $" + userAmount + ". At maturity, it will be worth $" + maturityAmount);
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

            System.out.println("Do you want to withdraw your CD early? (yes/no)");
            String earlyWithdrawResponse = userinput.nextLine();
            if (earlyWithdrawResponse.equalsIgnoreCase("yes")) {
                double penalty = 0.01 * userAmount;
                System.out.println("You will forfeit all interest and be charged a late withdrawal fee of $" + penalty + ".");
                System.out.println("Your CD has been withdrawn early, and no interest is earned.");
            } else {
                System.out.println("No problem! Let us know when you're ready (we recommend waiting until it matures).");
            }
        } else {
            System.out.println("No problem! If you change your mind, let us know.");
        }
    }

    public static void addUser(String username, String dateOfBirth, int ID) {
        try {
            FileWriter fw = new FileWriter("src/CD.csv", true);
            PrintWriter out = new PrintWriter(fw);
            out.println(username + "," + dateOfBirth + "," + ID);
            out.close();
            fw.close();
            System.out.println("User added successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}

class CD {
    double term;
    double Principal;
    double IR;

    public CD(double t, double p, double ir) {
        this.term = t;
        this.Principal = p;
        this.IR = ir;
    }

    public double calculateMaturityAmount(double principal) {
        return principal * (1 + ((IR) / 100) * (term / 12));
    }

    public void displayCD() {
        System.out.println("Term: " + term + " month(s), Principal: $" + Principal + ", Interest Rate: " + IR + "%");
    }

    public double getTerm() {
        return this.term;
    }

    public double getIR() {
        return this.IR;
    }
}

class CDinterface {
    private static ArrayList<CD> CDoptions = new ArrayList<>();

    public static void addCD(CD c) {
        CDoptions.add(c);
    }

    public static int getCDOptionsSize() {
        return CDoptions.size();
    }

    public static void displayCD() {
        System.out.println("Available Certificate of Deposit Options:");
        for (int i = 0; i < CDoptions.size(); i++) {
            System.out.println("Option " + (i + 1) + ":");
            CDoptions.get(i).displayCD();
        }
    }

    public static void PurchaseCD(int selection, double userAmount) {
        CD selectedCD = CDoptions.get(selection - 1);
        System.out.println("You have purchased a " + selectedCD.getTerm() + "-month CD with $" + userAmount + " at an interest rate of " + selectedCD.getIR() + "%.");
    }

    public static CD getCD(int selection) {
        return CDoptions.get(selection - 1);
    }
}
