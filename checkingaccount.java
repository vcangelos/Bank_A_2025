package checkingaccount;

import java.io.*;
import java.util.*;

class CheckingAccount {
    private String accountNumber;
    private double balance;
    private String accountHolderName;
    private boolean isOverdraftProtectionEnabled;
    private double overdraftLimit = 200.00;
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
        saveToCSV(); // Save new account details to CSV
        System.out.println("Account created successfully. Account number: " + accountNumber);
    }

    private String generateUniqueAccountNumber() {
        return String.format("%012d", new Random().nextInt(999999999));
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            lastTransactionDate = new Date();
            updateCSV();
            System.out.println("Deposit of $" + amount + " successful. New balance: $" + balance);
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
            System.out.println("Withdrawal of $" + amount + " successful. New balance: $" + balance);
        } else if (isOverdraftProtectionEnabled && linkedSavingsAccount != null && linkedSavingsAccount.getBalance() >= (amount - balance)) {
            double neededAmount = amount - balance;
            linkedSavingsAccount.withdraw(neededAmount);
            balance = 0;
            System.out.println("Overdraft covered by savings account. Withdrawal of $" + amount + " successful.");
        } else if (amount <= balance + overdraftLimit) {
            balance -= (amount + overdraftFee);
            System.out.println("Overdraft occurred. Withdrawal of $" + amount + " successful. Fee of $" + overdraftFee + " applied.");
        } else {
            System.out.println("Transaction declined. Insufficient funds and overdraft limit exceeded.");
            return false;
        }
        
        lastTransactionDate = new Date();
        updateCSV();
        return true;
    }

    public double getBalance() { return balance; }
    public Date getDateOpened() { return dateOpened; }
    public Date getLastTransactionDate() { return lastTransactionDate; }
    public boolean isOverdraftProtectionEnabled() { return isOverdraftProtectionEnabled; }
    public double getOverdraftLimit() { return overdraftLimit; }
    public double getOverdraftFee() { return overdraftFee; }

    private void saveToCSV() {
        try (FileWriter writer = new FileWriter(CSV_FILE, true)) {
            writer.append(accountNumber + "," + accountHolderName + "," + balance + "," + isOverdraftProtectionEnabled + "," + dateOpened + "," + lastTransactionDate + "\n");
            System.out.println("Account details saved to CSV.");
        } catch (IOException e) {
            System.out.println("Error saving account to CSV: " + e.getMessage());
        }
    }

    private void updateCSV() {
        List<String> updatedLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 6) continue;

                if (data[0].equals(accountNumber)) {
                    data[2] = String.format("%.2f", balance);
                    data[5] = new Date().toString();
                }
                updatedLines.add(String.join(",", data));
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE))) {
            for (String line : updatedLines) {
                bw.write(line);
                bw.newLine();
            }
            System.out.println("CSV file updated successfully.");
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}

class SavingsAccount {
    private String accountNumber;
    private String accountHolderName;
    private double balance;

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

public class CheckingAccountApp {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter account holder's name: ");
        String name = scan.nextLine();

        boolean overdraftProtection = false;
        SavingsAccount linkedSavingsAccount = null;

        while (true) {
            System.out.print("Do you want overdraft protection? (true/false): ");
            String input = scan.next().toLowerCase();
            if (input.equals("true")) {
                overdraftProtection = true;
                System.out.print("Enter existing savings account number: ");
                String savingsAccountNumber = scan.next();
                linkedSavingsAccount = new SavingsAccount(savingsAccountNumber, name, 1000.00);
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
