package bank;

import java.io.*;
import java.util.*;

public class ATMSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ensure the CSV file exists
        File csvFile = new File("card_info.csv");

        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating the CSV file: " + e.getMessage());
                return;
            }
        }

        // Main loop for the ATM system
        while (true) {
            System.out.println("Welcome to the ATM.");
            System.out.println("1. Login");
            System.out.println("2. Exit");

            String option = scanner.nextLine().trim();

            if (option.equals("1")) {
                // User login
                System.out.print("Enter your first name: ");
                String firstName = scanner.nextLine();
                System.out.print("Enter your last name: ");
                String lastName = scanner.nextLine();
                System.out.print("Enter your 4-digit Account PIN: ");
                String enteredPin = scanner.nextLine();

                if (isValidUser(firstName, lastName, enteredPin)) {
                    atmMenu(scanner, firstName, lastName);
                } else {
                    System.out.println("Invalid login credentials.");
                }
            } else if (option.equals("2")) {
                break;  // Exit the ATM system
            } else {
                System.out.println("Invalid option. Try again.");
            }
        }
    }

    // Validate the user login using the CSV file
    private static boolean isValidUser(String firstName, String lastName, String pin) {
        try (Scanner csvScanner = new Scanner(new File("card_info.csv"))) {
            while (csvScanner.hasNextLine()) {
                String line = csvScanner.nextLine();
                String[] cardInfo = line.split(",");
                String storedFirstName = cardInfo[0];
                String storedLastName = cardInfo[1];
                String storedPin = cardInfo[6]; // Account PIN is at index 6

                // Validate the user details
                if (storedFirstName.equalsIgnoreCase(firstName) && storedLastName.equalsIgnoreCase(lastName)
                        && storedPin.equals(pin)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading the CSV file.");
        }
        return false;
    }

    // ATM menu: Deposit, Withdraw, Balance Check, and Exit
    private static void atmMenu(Scanner scanner, String firstName, String lastName) {
        while (true) {
            System.out.println("ATM Menu:");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Check Balance");
            System.out.println("4. Exit");

            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) {
                depositFunds(scanner, firstName, lastName);
            } else if (choice.equals("2")) {
                withdrawFunds(scanner, firstName, lastName);
            } else if (choice.equals("3")) {
                checkBalance(firstName, lastName);
            } else if (choice.equals("4")) {
                break;  // Exit ATM menu
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Deposit funds into the user's account
    private static void depositFunds(Scanner scanner, String firstName, String lastName) {
        System.out.print("Enter amount to deposit: ");
        double depositAmount = scanner.nextDouble();
        updateBalance(firstName, lastName, depositAmount);
        System.out.println("Deposit successful! Amount deposited: " + depositAmount);
    }

    // Withdraw funds from the user's account
    private static void withdrawFunds(Scanner scanner, String firstName, String lastName) {
        System.out.print("Enter amount to withdraw: ");
        double withdrawAmount = scanner.nextDouble();
        
        // Check if the user has sufficient balance
        double currentBalance = getBalance(firstName, lastName);
        if (currentBalance >= withdrawAmount) {
            updateBalance(firstName, lastName, -withdrawAmount);
            System.out.println("Withdrawal successful! Amount withdrawn: " + withdrawAmount);
        } else {
            System.out.println("Insufficient funds!");
        }
    }

    // Check the balance of the user's account
    private static void checkBalance(String firstName, String lastName) {
        double balance = getBalance(firstName, lastName);
        System.out.println("Current Balance: " + balance);
    }

    // Get the balance from the CSV for the user
    private static double getBalance(String firstName, String lastName) {
        try (Scanner csvScanner = new Scanner(new File("card_info.csv"))) {
            while (csvScanner.hasNextLine()) {
                String line = csvScanner.nextLine();
                String[] cardInfo = line.split(",");
                String storedFirstName = cardInfo[0];
                String storedLastName = cardInfo[1];
                double balance = Double.parseDouble(cardInfo[7]);  // Balance is at index 7
                
                if (storedFirstName.equalsIgnoreCase(firstName) && storedLastName.equalsIgnoreCase(lastName)) {
                    return balance;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading the CSV file.");
        }
        return 0.0;  // Return 0 if no matching account is found
    }

    // Update the balance of a user's account in the CSV
    private static void updateBalance(String firstName, String lastName, double amount) {
        List<String> lines = new ArrayList<>();
        try (Scanner csvScanner = new Scanner(new File("card_info.csv"))) {
            while (csvScanner.hasNextLine()) {
                String line = csvScanner.nextLine();
                String[] cardInfo = line.split(",");
                String storedFirstName = cardInfo[0];
                String storedLastName = cardInfo[1];
                double balance = Double.parseDouble(cardInfo[7]);

                // If the user matches, update the balance
                if (storedFirstName.equalsIgnoreCase(firstName) && storedLastName.equalsIgnoreCase(lastName)) {
                    balance += amount;  // Add or subtract the amount
                    cardInfo[7] = String.valueOf(balance);  // Update balance field
                    line = String.join(",", cardInfo);  // Rebuild the line
                }
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading the CSV file.");
        }

        // Write the updated lines back to the file
        try (PrintWriter writer = new PrintWriter(new FileWriter("card_info.csv"))) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error writing to the CSV file.");
        }
    }
}
