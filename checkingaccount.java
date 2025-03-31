package checkingaccount;

import java.util.*;
import java.io.*;

class CheckingAccount {
    private String accountNumber;
    private double balance;
    private String accountHolderName;
    private boolean isOverdraftProtectionEnabled;
    private double overdraftLimit = 200.00; // Fixed overdraft limit
    private double overdraftFee = 35.00;
    private SavingsAccount linkedSavingsAccount;
    private Date dateOpened;
    private Date lastTransactionDate;

    private static final String CSV_FILE = "checkingaccount.csv";

    public CheckingAccount(String accountHolderName, double balance, boolean isOverdraftProtectionEnabled, SavingsAccount linkedSavingsAccount) {
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.accountNumber = generateUniqueAccountNumber();
        this.isOverdraftProtectionEnabled = isOverdraftProtectionEnabled;
        this.linkedSavingsAccount = linkedSavingsAccount;
        this.dateOpened = new Date();
        this.lastTransactionDate = new Date();
    }

    private String generateUniqueAccountNumber() {
        Random rand = new Random();
        return String.format("%012d", rand.nextLong(999999999999L));
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            lastTransactionDate = new Date();
            logTransaction("Deposit", amount);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount.");
            return false;
        }
        
        if (amount <= balance) {
            balance -= amount;
        } else if (isOverdraftProtectionEnabled && linkedSavingsAccount != null && linkedSavingsAccount.getBalance() >= (amount - balance)) {
            double neededAmount = amount - balance;
            linkedSavingsAccount.withdraw(neededAmount);
            balance = 0;
            System.out.println("Overdraft covered by savings account.");
        } else if (amount <= balance + overdraftLimit) {
            balance -= amount;
            balance -= overdraftFee;
            System.out.println("Overdraft occurred. Fee of $" + overdraftFee + " applied.");
        } else {
            System.out.println("Transaction declined. Insufficient funds and overdraft limit exceeded.");
            return false;
        }
        
        lastTransactionDate = new Date();
        logTransaction("Withdrawal", amount);
        return true;
    }

    public double getBalance() { return balance; }
    public Date getDateOpened() { return dateOpened; }
    public Date getLastTransactionDate() { return lastTransactionDate; }
    public boolean isOverdraftProtectionEnabled() { return isOverdraftProtectionEnabled; }
    public double getOverdraftLimit() { return overdraftLimit; }
    public double getOverdraftFee() { return overdraftFee; }

    private void logTransaction(String type, double amount) {
        System.out.println(type + " of $" + amount + " completed!");
    }
}

class SavingsAccount {
    private String accountNumber;
    private String accountHolderName;
    private double balance;

    // Constructor for existing savings account (not created in this code, just used)
    public SavingsAccount(String accountNumber, String accountHolderName, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
    }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public double getBalance() { return balance; }
    public String getAccountHolderName() { return accountHolderName; }
    public String getAccountNumber() { return accountNumber; }
}

public class CheckingAccountTest {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter account holder's name: ");
        String name = scan.nextLine();

        boolean overdraftProtection = false;
        SavingsAccount linkedSavingsAccount = null; // Default to null until found in database

        while (true) {
            System.out.print("Do you want overdraft protection? (true/false): ");
            String input = scan.next().toLowerCase();
            if (input.equals("true")) {
                overdraftProtection = true;
                // Simulate accessing an existing savings account from a database
                // In a real-world scenario, you'd search for the savings account from a database here
                System.out.print("Enter existing savings account number: ");
                String savingsAccountNumber = scan.next();
                // Assume you have a method to fetch the savings account from a database by account number
                // For simulation, we'll just create a dummy savings account with balance 1000.00
                linkedSavingsAccount = new SavingsAccount(savingsAccountNumber, name, 1000.00); // Dummy balance for example
                break;
            } else if (input.equals("false")) {
                overdraftProtection = false;
                break;
            } else {
                System.out.println("Invalid input. Please enter 'true' or 'false'.");
            }
        }

        CheckingAccount checking = new CheckingAccount(name, 0.00, overdraftProtection, linkedSavingsAccount);

        boolean running = true;
        while (running) {
            System.out.print("Choose an option: Check Balance[1], Deposit[2], Withdraw[3], Account Info[4], Quit[5]: ");
            if (!scan.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid option (1-5). Restarting selection.");
                scan.next();
                continue;
            }
            int option = scan.nextInt();

            if (option == 1) {
                System.out.println("Balance: $" + checking.getBalance());
            } else if (option == 2) {
                System.out.print("Enter deposit amount: ");
                while (!scan.hasNextDouble()) {
                    System.out.println("Invalid input. Please enter a numeric value.");
                    scan.next();
                }
                double amount = scan.nextDouble();
                checking.deposit(amount);
            } else if (option == 3) {
                System.out.print("Enter withdrawal amount: ");
                while (!scan.hasNextDouble()) {
                    System.out.println("Invalid input. Please enter a numeric value.");
                    scan.next();
                }
                double amount = scan.nextDouble();
                checking.withdraw(amount);
            } else if (option == 4) {
                System.out.println("Account Opening Date: " + checking.getDateOpened());
                System.out.println("Last Transaction Date: " + checking.getLastTransactionDate());
                System.out.println("Overdraft Protection: " + (checking.isOverdraftProtectionEnabled() ? "Enabled" : "Disabled"));
                if (!checking.isOverdraftProtectionEnabled()) {
                    System.out.println("Overdraft Fee: $" + checking.getOverdraftFee());
                }
                System.out.println("Overdraft Limit: $" + checking.getOverdraftLimit());
            } else if (option == 5) {
                running = false;
                System.out.println("Exiting.");
            } else {
                System.out.println("Invalid option. Try again.");
            }
        }
        scan.close();
    }
}
