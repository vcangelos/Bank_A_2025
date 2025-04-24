package bank;

import java.io.*;
import java.util.*;

public class BankSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ensureFile("account_info.csv");

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. View existing account info");
            System.out.println("2. Create a new account");
            System.out.println("3. Close an account");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> viewAccountInfo(scanner);
                case "2" -> createNewAccount(scanner);
                case "3" -> closeAccount(scanner);
                default  -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // View existing account info with PIN verification
    private static void viewAccountInfo(Scanner sc) {
        System.out.print("Enter account holder's name: ");
        String holderName = sc.nextLine().trim();

        System.out.print("Enter your 4-digit PIN: ");
        String enteredPin = sc.nextLine().trim();

        try (Scanner csv = new Scanner(new File("account_info.csv"))) {
            boolean found = false;
            while (csv.hasNextLine()) {
                String[] f = csv.nextLine().split(",");
                if (f.length < 9) continue;
                String accountHolder = f[2].trim();
                String storedPin = f[8].trim();

                if (accountHolder.equalsIgnoreCase(holderName)) {
                    if (storedPin.equals(enteredPin)) {
                        found = true;
                        System.out.println("\nAccount Holder: " + f[2]);
                        System.out.println("Account Number: " + f[1]);
                        // Format the balance to two decimal places
                        System.out.println("Balance: " + String.format("%.2f", Double.parseDouble(f[3])));
                        System.out.println("PIN: " + f[8]);
                        break;
                    } else {
                        System.out.println("Incorrect PIN.");
                        return;
                    }
                }
            }
            if (!found) {
                System.out.println("No matching account found.");
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
        waitForBack(sc);
    }

    // Account creation with generated 16-digit number, CVV, expiry, and 4-digit non-security code
    private static void createNewAccount(Scanner sc) {
        System.out.println("Provide the following information to create a new account:");
        System.out.print("Account holder name: ");
        String holderName = sc.nextLine();

        // Generate a 16-digit account number starting with 4
        String accountNumber = "4" + String.format("%015d", (long) (Math.random() * 1_000_000_000_000_000L));

        double balance = 0.0;
        boolean overdraftProtection = false;
        double overdraftLimit = 0.0;
        String dateOpened = java.time.LocalDate.now().toString();
        String lastTransactionDate = "None";

        // Generate 3 random values for the card details
        String cvv = String.format("%03d", (int) (Math.random() * 1000));  // CVV 3 digits
        String expiryDate = java.time.LocalDate.now().plusYears(5).format(java.time.format.DateTimeFormatter.ofPattern("MM/yy"));  // Expiry in MM/yy format
        String fourDigitCode = String.format("%04d", (int) (Math.random() * 10000));  // 4-digit code

        // Set Security PIN
        System.out.print("Set your 4-digit Account Security PIN: ");
        String pin = sc.nextLine();

        // Only write the data to the CSV
        int id = nextUniqueID();
        writeAccountInfoToCSV(id, accountNumber, holderName, balance, overdraftProtection, overdraftLimit, dateOpened, lastTransactionDate, pin);

        // Display the card details for the user
        System.out.println("\nCard details (not saved, displayed for this session only):");
        System.out.println("Account Number: " + accountNumber);
        System.out.println("CVV: " + cvv);
        System.out.println("Expiry Date: " + expiryDate);
        System.out.println("4-digit Code: " + fourDigitCode);

        System.out.println("Account created and saved to account_info.csv.");

        waitForBack(sc);
    }

    // Close account with PIN verification
    private static void closeAccount(Scanner sc) {
        System.out.print("Enter account holder's name: ");
        String holderName = sc.nextLine().trim();

        System.out.print("Enter your 4-digit PIN: ");
        String enteredPin = sc.nextLine().trim();

        List<String> keep = new ArrayList<>();
        boolean removed = false;

        try (Scanner csv = new Scanner(new File("account_info.csv"))) {
            while (csv.hasNextLine()) {
                String line = csv.nextLine();
                String[] f = line.split(",");
                if (f.length < 9) continue;

                String accountHolder = f[2].trim();
                String storedPin = f[8].trim();

                if (accountHolder.equalsIgnoreCase(holderName) && storedPin.equals(enteredPin)) {
                    removed = true;
                } else {
                    keep.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }

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

    // CSV helpers
    private static void writeAccountInfoToCSV(int id, String accountNumber, String holderName,
                                               double balance, boolean overdraftProtection, double overdraftLimit,
                                               String dateOpened, String lastTransactionDate, String pin) {
        try (PrintWriter w = new PrintWriter(new FileWriter("account_info.csv", true))) {
            w.println(id + "," + accountNumber + "," + holderName + "," + balance + "," +
                      overdraftProtection + "," + overdraftLimit + "," + dateOpened + "," + lastTransactionDate + "," + pin);
        } catch (IOException e) {
            System.out.println("Error writing CSV: " + e.getMessage());
        }
    }

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

    // Utilities
    private static void waitForBack(Scanner sc) {
        System.out.println("\nType 'back' to return to the main menu.");
        while (!sc.nextLine().trim().equalsIgnoreCase("back"))
            System.out.print("Type 'back': ");
    }

    private static void ensureFile(String name) {
        try {
            File f = new File(name);
            if (!f.exists()) f.createNewFile();
        } catch (IOException e) {
            System.out.println("Cannot create " + name + ": " + e.getMessage());
            System.exit(1);
        }
    }
}
