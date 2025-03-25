package checkingaccount;

import java.util.Scanner;
import java.util.Date;
import java.io.*;

class CheckingAccount {
    private String accountNumber;
    private double balance;
    private String accountHolderName;
    
    private boolean isOverdraftProtectionEnabled;
    private double overdraftLimit;
    private Date dateOpened;
    private Date lastTransactionDate;
    
    public CheckingAccount(String accountNumber, String accountHolderName, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.dateOpened = new Date();
        this.lastTransactionDate = new Date();
    }
    
    public void deposit(double amount) {
        if (amount > 0) {
            amount = Math.round(amount * 100.0) / 100.0;
            balance += amount;
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
        } else if (amount <= 0) {
            System.out.println("Invalid withdrawal amount. Please enter a positive number.");
            return false;
        } else {
            System.out.println("Insufficient funds. Your account has $" + balance + ".");
            return false;
        }
    }

    public double getBalance() {
        return balance;
    }

    private void logTransaction(String type, double amount) {
        System.out.println(type + " of $" + amount + " completed!");
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public boolean isOverdraftProtectionEnabled() {
        return isOverdraftProtectionEnabled;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public Date getDateOpened() {
        return dateOpened;
    }

    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }
    
    private static void appendToCSV() {
            String filePath = "data.csv";
            String dataToAppend = "John Doe,30,john.doe@example.com";

            try (FileWriter writer = new FileWriter(filePath, true)) {
                writer.append(dataToAppend);
                writer.append("\n"); 
                System.out.println("Data appended to CSV file successfully.");
            } catch (IOException e) {
                System.err.println("Error appending data to CSV file: " + e.getMessage());
		}
    }
}

public class CheckingAccountTest {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        boolean breakloop = false;
        double amount;
        
        CheckingAccount checking = new CheckingAccount("12345", "John Doe", 1000.0);

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
