package bank;

import java.io.*;
import java.util.*;

public class ATM {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ensureFile("account_info.csv");

// sign in or exit
        while (true) {
            System.out.println("Welcome to the ATM system.");
            System.out.println("1. Sign In");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> signIn(scanner);  // Calls the sign-in method
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

// Sign in - name and pin
    private static void signIn(Scanner sc) {
        System.out.print("Enter account holder's name: ");
        String holderName = sc.nextLine().trim();

        System.out.print("Enter your 4-digit PIN: ");
        String enteredPin = sc.nextLine().trim();

        // Authenticate the user and proceed if valid
        if (authenticate(holderName, enteredPin)) {
            System.out.println("Sign in successful!");
            handleAccountActions(sc, holderName);  // Handles account actions after successful sign-in
        } else {
            System.out.println("Invalid account or PIN. Please try again.");
        }
    }

// check CSV for matching name and pin
    private static boolean authenticate(String holderName, String enteredPin) {
        try (Scanner csv = new Scanner(new File("account_info.csv"))) {
            while (csv.hasNextLine()) {
                String[] f = csv.nextLine().split(",");
                if (f.length < 9) continue;
                String accountHolder = f[2].trim();
                String storedPin = f[8].trim();

// Check if account name and pin match
                if (accountHolder.equalsIgnoreCase(holderName) && storedPin.equals(enteredPin)) {
                    return true;  // Authentication successful
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
        return false;  // Authentication failed
    }

// view balance, deposit, withdraw
    private static void handleAccountActions(Scanner sc, String holderName) {
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. View Balance");
            System.out.println("2. Deposit Funds");
            System.out.println("3. Withdraw Funds");
            System.out.println("4. Back to Main Menu");
            String option = sc.nextLine().trim();

            switch (option) {
                case "1" -> viewBalance(holderName);  // Show account balance
                case "2" -> depositFunds(sc, holderName);  // Deposit funds
                case "3" -> withdrawFunds(sc, holderName);  // Withdraw funds
                case "4" -> {
                    System.out.println("\nReturning to the main menu...");
                    return;  // Exit to the main menu
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

// View account balance
    private static void viewBalance(String holderName) {
        try (Scanner csv = new Scanner(new File("account_info.csv"))) {
            while (csv.hasNextLine()) {
                String[] f = csv.nextLine().split(",");
                if (f.length < 9) continue;

                String accountHolder = f[2].trim();
                if (accountHolder.equalsIgnoreCase(holderName)) {
                    double balance = Double.parseDouble(f[3].trim());
                    System.out.printf("\nYour Balance: $%.2f\n", balance);  // Display balance with 2 decimal places
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
        System.out.println("No matching account found.");
    }

// Deposit and conversion
    private static void depositFunds(Scanner sc, String holderName) {
        System.out.println("\nSelect currency for deposit:");
        System.out.println("1. USD");
        System.out.println("2. Euro (€)");
        System.out.println("3. Peso (₱)");
        System.out.println("4. Canadian Dollar (CAD$)");

        String currencyOption = sc.nextLine().trim();
        double exchangeRate = 1.0;  // Default to USD (1 USD = 1 USD)

// exchange rate
        switch (currencyOption) {
            case "1" -> System.out.print("Enter amount in USD: ");
            case "2" -> {
                exchangeRate = 1.1;  // Example rate for Euro to USD
                System.out.print("Enter amount in Euro (€): ");
            }
            case "3" -> {
                exchangeRate = 0.018;  // Example rate for Peso to USD
                System.out.print("Enter amount in Peso (₱): ");
            }
            case "4" -> {
                exchangeRate = 0.74;  // Example rate for CAD to USD
                System.out.print("Enter amount in Canadian Dollar (CAD$): ");
            }
            default -> {
                System.out.println("Invalid currency option.");
                return;
            }
        }

// Convert amount to USD and update balance
        double amount = Double.parseDouble(sc.nextLine().trim());
        double depositAmountInUSD = amount * exchangeRate;

        updateBalance(holderName, depositAmountInUSD);  // Update the account balance

        System.out.printf("Successfully deposited $%.2f in USD.\n", depositAmountInUSD);
    }

// Update balance after deposit-withdrawal
    private static void updateBalance(String holderName, double amount) {
        List<String> keep = new ArrayList<>();
        boolean updated = false;

        try (Scanner csv = new Scanner(new File("account_info.csv"))) {
            while (csv.hasNextLine()) {
                String line = csv.nextLine();
                String[] f = line.split(",");
                if (f.length < 9) continue;

                String accountHolder = f[2].trim();
                if (accountHolder.equalsIgnoreCase(holderName)) {
                    double balance = Double.parseDouble(f[3].trim());
                    balance += amount;  // Add deposit or subtract withdrawal
                    f[3] = String.format("%.2f", balance);  // Update balance in the array
                    updated = true;
                }
                keep.add(String.join(",", f));
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }

// Write updated balance back to the file
        if (updated) {
            try (PrintWriter writer = new PrintWriter(new FileWriter("account_info.csv"))) {
                for (String line : keep) {
                    writer.println(line);
                }
            } catch (IOException e) {
                System.out.println("Error writing CSV: " + e.getMessage());
            }
        }
    }

// Withdraw funds
    private static void withdrawFunds(Scanner sc, String holderName) {
        System.out.print("Enter withdrawal amount in USD: ");
        double amount = Double.parseDouble(sc.nextLine().trim());

        try (Scanner csv = new Scanner(new File("account_info.csv"))) {
            List<String> keep = new ArrayList<>();
            boolean found = false;

// Check balance is sufficient for withdrawal
            while (csv.hasNextLine()) {
                String line = csv.nextLine();
                String[] f = line.split(",");
                if (f.length < 9) continue;

                String accountHolder = f[2].trim();
                if (accountHolder.equalsIgnoreCase(holderName)) {
                    double balance = Double.parseDouble(f[3].trim());

// Ensure sufficient funds
                    if (balance >= amount) {
                        balance -= amount;
                        f[3] = String.format("%.2f", balance);  // Update balance after withdrawal
                        found = true;
                    } else {
                        System.out.println("Insufficient funds.");
                    }
                }
                keep.add(String.join(",", f));
            }

// Write updated balance back to the file
            if (found) {
                try (PrintWriter writer = new PrintWriter(new FileWriter("account_info.csv"))) {
                    for (String line : keep) {
                        writer.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("Error writing CSV: " + e.getMessage());
                }
                System.out.printf("Successfully withdrawn $%.2f.\n", amount);
            } else {
                System.out.println("No matching account found.");
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
    }

// Ensure the account_info.csv file exists
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
