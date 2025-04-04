package checkingaccount;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

class CheckingAccount {
    private String accUserID; // The user ID associated with this account
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

    // Constructor for NEW accounts
    public CheckingAccount(String accUserID, String accountHolderName, double balance, boolean isOverdraftProtectionEnabled, SavingsAccount linkedSavingsAccount) {
        this.accUserID = accUserID; // Store the accUserID
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.accountNumber = generateUniqueAccountNumber();
        this.isOverdraftProtectionEnabled = isOverdraftProtectionEnabled;
        this.linkedSavingsAccount = linkedSavingsAccount;
        this.dateOpened = new Date();
        this.lastTransactionDate = new Date();
        saveToCSV(); // ONLY new accounts are saved here
        System.out.println("Account created successfully. Account number: " + accountNumber);
    }

    // Constructor for EXISTING accounts
    public CheckingAccount(String accUserID, String accountNumber, String accountHolderName, double balance, boolean isOverdraftProtectionEnabled, Date dateOpened, Date lastTransactionDate) {
        this.accUserID = accUserID; // Store the accUserID
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.isOverdraftProtectionEnabled = isOverdraftProtectionEnabled;
        this.dateOpened = dateOpened;
        this.lastTransactionDate = lastTransactionDate;
        this.linkedSavingsAccount = null; // You can add loading from file if needed
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
        File file = new File(CSV_FILE);
        boolean fileExists = file.exists();
        
        try (FileWriter writer = new FileWriter(CSV_FILE, true)) {
            if (!fileExists) {
                writer.append("accUserID,AccountNumber,AccountHolderName,Balance,OverdraftProtection,DateOpened,LastTransactionDate\n");
            }
            writer.append(accUserID + "," + accountNumber + "," + accountHolderName + "," + balance + "," + isOverdraftProtectionEnabled + "," + dateOpened + "," + lastTransactionDate + "\n");
            System.out.println("Account details saved to CSV.");
        } catch (IOException e) {
            System.out.println("Error saving account to CSV: " + e.getMessage());
        }
    }

    private void updateCSV() {
        List<String> updatedLines = new ArrayList<>();
        boolean isFirstLine = true;
        String header = "accUserID,AccountNumber,AccountHolderName,Balance,OverdraftProtection,DateOpened,LastTransactionDate";

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                
                // Preserve the header
                if (isFirstLine) {
                    if (!line.equals(header)) { // If there's no header, add it
                        updatedLines.add(header);
                    }
                    isFirstLine = false;
                }
                
                if (data.length < 7) continue; // Adjust check to account for the new column

                if (data[1].equals(accountNumber)) {
                    data[3] = String.format("%.2f", balance);
                    data[6] = new Date().toString();
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
    private static final String CSV_FILE = "checkingaccount.csv";

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Welcome to the Checking Account Menu!");
        CheckingAccount checking = null;

        while (true) {
            System.out.println("Do you want to [1] Create a new account or [2] Access an existing account? Enter 1 or 2: ");
            int choice = getValidInput(scan, 1, 2);

            if (choice == 1) {
                checking = createNewAccount(scan);
                break;
            } else {
                checking = accessExistingAccount(scan);
                if (checking != null) {
                    break;
                } else {

                    System.out.println("Returning to main menu...");
                }
            }
        }

        boolean running = true;
        while (running) {
            System.out.print("Choose an option: Check Balance[1], Deposit[2], Withdraw[3], Account Info[4], Quit[5]: ");
            int option = getValidInput(scan, 1, 5);

            switch (option) {
                case 1:
                    System.out.println("Balance: $" + checking.getBalance());
                    break;
                case 2:
                    System.out.print("Enter deposit amount: ");
                    double depositAmount = getValidDouble(scan);
                    checking.deposit(depositAmount);
                    break;
                case 3:
                    System.out.print("Enter withdrawal amount: ");
                    double withdrawalAmount = getValidDouble(scan);
                    checking.withdraw(withdrawalAmount);
                    break;
                case 4:
                    displayAccountInfo(checking);
                    break;
                case 5:
                    running = false;
                    System.out.println("Exiting.");
                    break;
            }
        }
        scan.close();
    }

    private static CheckingAccount createNewAccount(Scanner scan) {
        System.out.print("Enter account holder's name: ");
        String name = scan.next();

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
        return new CheckingAccount(name, 0.00, overdraftProtection, linkedSavingsAccount);
    }

    private static CheckingAccount accessExistingAccount(Scanner scan) {
        while (true) {
            System.out.print("Enter your account number (or type 'back' to return): ");
            String input = scan.next();

            if (input.equalsIgnoreCase("back")) {
                return null; // Go back to main menu
            }

            try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length < 6) continue;
                    if (data[0].equals(input)) {
                        String name = data[1];
                        double balance = Double.parseDouble(data[2]);
                        boolean overdraftProtection = Boolean.parseBoolean(data[3]);
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                            Date dateOpened = formatter.parse(data[4]);
                            Date lastTransactionDate = formatter.parse(data[5]);
                            CheckingAccount existing = new CheckingAccount(data[0], name, balance, overdraftProtection, dateOpened, lastTransactionDate);
                            System.out.println("Account found. Logging in...");
                            return existing;
                        } catch (ParseException e) {
                            System.out.println("Error parsing date: " + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading account file: " + e.getMessage());
                return null;
            }

            System.out.println("Account not found. Please check the account number or type 'back' to return.");
        }
    }

    private static void displayAccountInfo(CheckingAccount checking) {
        System.out.println("Account Opening Date: " + checking.getDateOpened());
        System.out.println("Last Transaction Date: " + checking.getLastTransactionDate());
        System.out.println("Overdraft Protection: " + (checking.isOverdraftProtectionEnabled() ? "Enabled" : "Disabled"));
        if (!checking.isOverdraftProtectionEnabled()) {
            System.out.println("Overdraft Fee: $" + checking.getOverdraftFee());
        }
        System.out.println("Overdraft Limit: $" + checking.getOverdraftLimit());
    }

    private static int getValidInput(Scanner scan, int min, int max) {
        while (!scan.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
            scan.next();
        }
        int choice = scan.nextInt();
        if (choice < min || choice > max) {
            System.out.println("Invalid choice. Try again.");
            return getValidInput(scan, min, max);
        }
        return choice;
    }

    private static double getValidDouble(Scanner scan) {
        while (!scan.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a valid number.");
            scan.next();
        }
        return scan.nextDouble();
    }
}
