package checkingaccount;

import java.util.*;
import java.io.*;

class CheckingAccount {
    private String accountNumber;
    private double balance;
    private String accountHolderName;
    
    private boolean isOverdraftProtectionEnabled = false;
    private double overdraftLimit;
    private Date dateOpened;
    private Date lastTransactionDate;
    
    private static final String CSV_FILE = "checkingaccount.csv"; // Ensures consistency

    // Constructor: Generate a unique account number
    public CheckingAccount(String accountHolderName, double balance) {
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.accountNumber = generateUniqueAccountNumber(); // Generate unique account number
        this.dateOpened = new Date();
        this.lastTransactionDate = new Date();
    }

    // Generate a unique 12-digit account number
    private String generateUniqueAccountNumber() {
        Random rand = new Random();
        Set<String> existingAccountNumbers = getExistingAccountNumbers(); // Read existing account numbers

        String newAccountNumber;
        do {
            newAccountNumber = String.format("%012d", rand.nextLong(999999999999L)); // Generate 12-digit number
        } while (existingAccountNumbers.contains(newAccountNumber)); // Ensure uniqueness

        return newAccountNumber;
    }

    // Read existing account numbers from CSV
    private Set<String> getExistingAccountNumbers() {
        Set<String> accountNumbers = new HashSet<>();
        File file = new File(CSV_FILE);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length > 1) {
                        accountNumbers.add(parts[1].trim()); // Account number is in the second column
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading CSV file: " + e.getMessage());
            }
        }
        return accountNumbers;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance = Math.round((balance + amount) * 100.0) / 100.0;
            lastTransactionDate = new Date();
            logTransaction("Deposit", amount);
        } else {
            System.out.println("Invalid deposit amount. Please enter a positive dollar amount.");
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            lastTransactionDate = new Date();
            logTransaction("Withdrawal", amount);
            return true;
        } else {
            System.out.println(amount <= 0 ? "Invalid withdrawal amount." : "Insufficient funds. Your balance: $" + balance);
            return false;
        }
    }

    public double getBalance() { return balance; }
    
    private void logTransaction(String type, double amount) {
        System.out.println(type + " of $" + amount + " completed!");
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public Date getDateOpened() { return dateOpened; }
    public Date getLastTransactionDate() { return lastTransactionDate; }
    public boolean getIsOverdraftProtectionEnabled() { return isOverdraftProtectionEnabled; }
    public double getOverdraftLimit() { return overdraftLimit; }

    // Append account details to CSV
    public void appendToCSV() {
        try (FileWriter writer = new FileWriter(CSV_FILE, true)) {
            writer.append(accountHolderName).append(",");
            writer.append(accountNumber).append(",");
            writer.append(String.valueOf(balance)).append("\n");
            System.out.println("Account details saved to CSV.");
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }
}

public class CheckingAccountTest {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        boolean breakloop = false;
        double amount;
        
        CheckingAccount checking = new CheckingAccount(accountNumber, accountHolderName, balance);

        while (!breakloop) {
            System.out.print("What would you like to do? Check Balance[1], Make a Deposit[2], Make a Withdrawal[3], Get Other Info[4], Quit Checking Account Management[5]: ");
            int checkingOption = scan.nextInt();

            if (checkingOption == 1) {
                System.out.println("Your current balance is: $" + checking.getBalance());
            } 
            else if (checkingOption == 2) {
                System.out.print("How much would you like to deposit? ");
                amount = scan.nextDouble();
                checking.deposit(amount);
            } 
            else if (checkingOption == 3) {
                System.out.print("How much would you like to withdraw? ");
                amount = scan.nextDouble();
                checking.withdraw(amount);
            } 
            else if (checkingOption == 4) {
                System.out.println("Printing out checking account info...");
                System.out.println("Account Number: " + checking.getAccountNumber());
                System.out.println("Account Holder: " + checking.getAccountHolderName());
                System.out.println("Overdraft Protection Status: " + checking.isOverdraftProtectionEnabled());
                System.out.println("Overdraft Limit: $" + checking.getOverdraftLimit());
                System.out.println("Account Opening Date: " + checking.getDateOpened());
                System.out.println("Date of Last Transaction: " + checking.getLastTransactionDate());
            } 
            else if (checkingOption == 5) {
                breakloop = true;
                System.out.println("Exiting Checking Account Management.");
            } 
            else {
                System.out.println("Invalid option. Please try again.");
            }
        }
        
        scan.close();
    }
}
