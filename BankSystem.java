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

    // ─────────────────── view existing account info ─────────────────────
    private static void viewAccountInfo(Scanner sc) {
        System.out.print("Enter account holder's name: ");
        String holderName = sc.nextLine();

        try (Scanner csv = new Scanner(new File("account_info.csv"))) {
            boolean found = false;
            while (csv.hasNextLine()) {
                String[] f = csv.nextLine().split(",");
                if (f.length < 9) continue;
                if (f[2].equalsIgnoreCase(holderName)) {
                    found = true;
                    // Display account holder name, account number, balance, and PIN
                    System.out.println("\nAccount Holder: " + f[2]);
                    System.out.println("Account Number: " + f[1]);
                    System.out.println("Balance: " + f[3]);
                    System.out.println("PIN: " + f[8]);  // Display the PIN
                    break;
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

    // ─────────────────── account creation ──────────────────────────────
    private static void createNewAccount(Scanner sc) {
        System.out.println("Provide the following information to create a new account:");
        System.out.print("Account holder name: ");
        String holderName = sc.nextLine();
        
        String accountNumber = "1" + (1000000000L + (long) (Math.random() * 1_000_000_000));
        double balance = 0.0;
        boolean overdraftProtection = false;
        double overdraftLimit = 0.0;
        String dateOpened = java.time.LocalDate.now().toString();
        String lastTransactionDate = "None";

        // Set PIN
        System.out.print("Set your 4-digit Account PIN: ");
        String pin = sc.nextLine();

        // Only write the data to the CSV without showing it to the user
        int id = nextUniqueID();
        writeAccountInfoToCSV(id, accountNumber, holderName, balance, overdraftProtection, overdraftLimit, dateOpened, lastTransactionDate, pin);
        System.out.println("Account created and saved to account_info.csv.");

        waitForBack(sc);
    }

    // ─────────────────── close account ─────────────────────────────────
    private static void closeAccount(Scanner sc) {
        System.out.print("Enter account holder's name: ");
        String holderName = sc.nextLine();

        List<String> keep = new ArrayList<>();
        boolean removed = false;

        try (Scanner csv = new Scanner(new File("account_info.csv"))) {
            while (csv.hasNextLine()) {
                String line = csv.nextLine();
                String[] f = line.split(",");
                if (f.length < 9) continue;

                boolean match = f[2].equalsIgnoreCase(holderName);
                if (match) removed = true; else keep.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }

        if (!removed) {
            System.out.println("No matching account found.");
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

    // ─────────────────── CSV helpers ────────────────────────────────
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

    // ─────────────────── utilities ──────────────────────────────────
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
