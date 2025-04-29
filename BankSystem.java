package bank;

import java.io.*;
import java.util.*;

public class BankSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

// Ensure that the account_info.csv file exists
        ensureFile("account_info.csv");

        while (true) {
            // Menu options
            System.out.println("Choose an option:");
            System.out.println("1. View existing account info");
            System.out.println("2. Create a new account");
            System.out.println("3. Close an account");
            String option = scanner.nextLine().trim();

// Handle choices with switch statement
            switch (option) {
                case "1" -> viewAccountInfo(scanner);
                case "2" -> createNewAccount(scanner);
                case "3" -> closeAccount(scanner);
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

// View account info - needs name and PIN
    private static void viewAccountInfo(Scanner sc) {
        System.out.print("Enter account holder's name: ");
        String holderName = sc.nextLine().trim();

        System.out.print("Enter your 4-digit PIN: ");
        String enteredPin = sc.nextLine().trim();

// Read CSV file containing account info
        try (Scanner csv = new Scanner(new File("account_info.csv"))) {
            boolean found = false;

            while (csv.hasNextLine()) {
                String[] f = csv.nextLine().split(",");

                if (f.length < 9) continue;

                String accountHolder = f[2].trim();
                String storedPin = f[8].trim();

// If info matches, display account info
                if (accountHolder.equalsIgnoreCase(holderName)) {
                    if (storedPin.equals(enteredPin)) {
                        found = true;
                        System.out.println("\nAccount Holder: " + f[2]);
                        System.out.println("Account Number: " + f[1]);
                        System.out.println("Balance: " + String.format("%.2f", Double.parseDouble(f[3])));
                        System.out.println("PIN: " + f[8]);
                        break;
                    } else {
                        System.out.println("Incorrect PIN.");
                        return;
                    }
                }
            }

// If no matching account
            if (!found) {
                System.out.println("No matching account found.");
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }

        waitForBack(sc);
    }

// Create new account
    private static void createNewAccount(Scanner sc) {
        System.out.println("Provide the following information to create a new account:");

        System.out.print("Account holder name: ");
        String holderName = sc.nextLine();

        String accountNumber = "4" + String.format("%015d", (long) (Math.random() * 1_000_000_000_000_000L));

        double balance = 0.0;
        boolean overdraftProtection = false;
        double overdraftLimit = 0.0;
        String dateOpened = java.time.LocalDate.now().toString();
        String lastTransactionDate = "None";

        String cvv = String.format("%03d", (int) (Math.random() * 1000));
        String expiryDate = java.time.LocalDate.now().plusYears(5).format(java.time.format.DateTimeFormatter.ofPattern("MM/yy"));
        String fourDigitCode = String.format("%04d", (int) (Math.random() * 10000));

// User set pin
        System.out.print("Set your 4-digit Account Security PIN: ");
        String pin = sc.nextLine();

// Write new account to CSV
        int id = nextUniqueID();
        writeAccountInfoToCSV(id, accountNumber, holderName, balance, overdraftProtection, overdraftLimit, dateOpened, lastTransactionDate, pin);

// Display the newly created account's card details
        System.out.println("\nYour new card details:");
        System.out.println("Account Number: " + accountNumber);
        System.out.println("CVV: " + cvv);
        System.out.println("Expiry Date: " + expiryDate);
        System.out.println("4-Digit Code: " + fourDigitCode);

        waitForBack(sc);
    }

    // Close account
    private static void closeAccount(Scanner sc) {
        // Prompt for account holder's name and PIN
        System.out.print("Enter account holder's name: ");
        String holderName = sc.nextLine().trim();
        System.out.print("Enter your 4-digit PIN: ");
        String enteredPin = sc.nextLine().trim();

        List<String> keep = new ArrayList<>();
        boolean removed = false;

// Read CSV and removes value
        try (Scanner csv = new Scanner(new File("account_info.csv"))) {
            while (csv.hasNextLine()) {
                String line = csv.nextLine();
                String[] f = line.split(",");
                if (f.length < 9) continue;

                String accountHolder = f[2].trim();
                String storedPin = f[8].trim();

// If info matches mark for removal
                if (accountHolder.equalsIgnoreCase(holderName) && storedPin.equals(enteredPin)) {
                    removed = true;
                } else {
                    keep.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }

// If account was found and removed
        if (!removed) {
            System.out.println("No matching account found or incorrect PIN.");
        } else {
            try (PrintWriter w = new PrintWriter(new FileWriter("account_info.csv"))) {
                for (String line : keep) w.println(line);
            } catch (IOException e) {
                System.out.println("Error writing CSV: " + e.getMessage());
            }
            System.out.println("Account closed successfully.");
        }

        waitForBack(sc);
    }

// Write the new account info to CSV
    private static void writeAccountInfoToCSV(int id, String accountNumber, String holderName, double balance,
                                               boolean overdraftProtection, double overdraftLimit, String dateOpened,
                                               String lastTransactionDate, String pin) {
        try (PrintWriter w = new PrintWriter(new FileWriter("account_info.csv", true))) {
            // Write account data in CSV format
            w.println(id + "," + accountNumber + "," + holderName + "," + balance + "," +
                      overdraftProtection + "," + overdraftLimit + "," + dateOpened + "," + lastTransactionDate + "," + pin);
        } catch (IOException e) {
            System.out.println("Error writing CSV: " + e.getMessage());
        }
    }

// Get next unique ID for new accounts
    private static int nextUniqueID() {
        int max = 1000;
        try (Scanner csv = new Scanner(new File("account_info.csv"))) {
            while (csv.hasNextLine()) {
                String[] f = csv.nextLine().split(",");
                if (f.length >= 1) max = Math.max(max, Integer.parseInt(f[0]));
            }
        } catch (IOException ignored) { }
        return max + 1;  
    }

// "Back" to return to the main menu
    private static void waitForBack(Scanner sc) {
        System.out.println("\nType 'back' to return to the main menu.");
        while (!sc.nextLine().trim().equalsIgnoreCase("back"))
            System.out.print("Type 'back': ");
    }

// Ensure that the account_info.csv file exists
    private static void ensureFile(String name) {
        try {
            File f = new File(name);
            if (!f.exists()) f.createNewFile();
        } catch (IOException e) {
            System.out.println("Error ensuring file existence: " + e.getMessage());
        }
    }


    public static String getRowByUniqueID(String uniqueID) {
        try (Scanner csv = new Scanner(new File("account_info.csv"))) {
            while (csv.hasNextLine()) {
                String line = csv.nextLine();
                String[] fields = line.split(",");
                if (fields.length >= 1 && fields[0].equals(uniqueID)) {
                    return line; 
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
        return null;
    }

// Set balance in the CSV (for backend interaction)
    public static void setBalance(double newBalance, String uniqueID) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

// Read the CSV and find the row to update
        try (Scanner csv = new Scanner(new File("account_info.csv"))) {
            while (csv.hasNextLine()) {
                String line = csv.nextLine();
                String[] fields = line.split(",");
                if (fields.length >= 1 && fields[0].equals(uniqueID)) {
// Found the row to update, replace the balance field
                    found = true;
                    fields[3] = String.format("%.2f", newBalance); 
                    line = String.join(",", fields);
                }
                lines.add(line); 
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }

// If the account was found and balance was updated, rewrite the CSV
        if (found) {
            try (PrintWriter writer = new PrintWriter(new FileWriter("account_info.csv"))) {
                for (String line : lines) {
                    writer.println(line); 
                }
            } catch (IOException e) {
                System.out.println("Error writing CSV: " + e.getMessage());
            }
            System.out.println("Balance updated successfully.");
        } else {
            System.out.println("Account not found with the given unique ID.");
        }
    }
}
