package bankproject;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class CheckingAccountApp {
    private static final String CSV_FILE = "checkingaccount.csv";

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String userID = "0";
        CheckingAccount checking = null;

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            br.readLine(); // skip header
            boolean accountFound = false;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 8) continue;
                if (data[0].equals(userID)) {
                    String accID = data[0];
                    String accNumber = data[1];
                    String name = data[2];
                    double balance = Double.parseDouble(data[3]);
                    boolean overdraftProtection = Boolean.parseBoolean(data[4]);
                    double overdraftLimit = Double.parseDouble(data[5]);
                    SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                    Date dateOpened = formatter.parse(data[6]);
                    Date lastTransactionDate = formatter.parse(data[7]);
                    checking = new CheckingAccount(accID, accNumber, name, balance, overdraftProtection, overdraftLimit, dateOpened, lastTransactionDate);
                    System.out.println("Account loaded successfully.");
                    accountFound = true;
                    break;
                }
            }
            if (!accountFound) {
                System.out.println("No existing account found. Creating a new one...");
                checking = createNewAccount(scan, userID); // Create a new account if none found
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error loading account, creating a new one...");
            checking = createNewAccount(scan, userID); // Fallback to create a new account
        }

        boolean running = true;
        while (running) {
        	System.out.print("Choose an option: Check Balance[1], Deposit[2], Withdraw[3], Account Info[4], Quit[5]: ");
        	int option = getValidInput(scan, 1, 6);

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
                    if (!checking.withdraw(withdrawalAmount)) {
                        System.out.println("Insufficient funds.");
                    }
                    break;
                case 4:
                    displayAccountInfo(checking); // Display account info only if Checking is properly initialized
                    break;
                case 5:
                    running = false;
                    System.out.println("Exiting.");
                    break;
                case 6:
                    deleteAccount(checking);
                    checking = null;
                    System.out.println("Account deleted.");
                    running = false;
                    break;

            }
        }
        scan.close();
    }

    private static CheckingAccount createNewAccount(Scanner scan, String userID) {
        System.out.print("Enter account holder's name: ");
        String name = scan.nextLine().trim();

        double overdraftLimit = 200.00;
        boolean overdraftProtection = false;

        while (true) {
            System.out.print("Do you want overdraft protection? (true/false): ");
            String input = scan.nextLine().trim().toLowerCase();
            if (input.equals("true")) {
                System.out.print("Enter existing checking account number: ");
                String checkingAccNum = scan.nextLine().trim();
                CheckingAccount linked = CheckingAccount.loadByAccountNumber(checkingAccNum);

                if (linked != null && linked.getUniqueID().equals(userID)) {
                    overdraftProtection = true;
                    System.out.println("Overdraft protection enabled using Checking Account.");
                } else {
                    System.out.println("Invalid checking account number. Overdraft protection not enabled.");
                }
                break;
            } else if (input.equals("false")) {
                overdraftProtection = false;
                break;
            } else {
                System.out.println("Invalid input. Please enter 'true' or 'false'.");
            }
        }

        CheckingAccount newAccount = new CheckingAccount(userID, name, 0.00, overdraftProtection, overdraftLimit);
        newAccount.saveToCSV(); // Save the new account to the CSV
        return newAccount;
    }
    
    private static void deleteAccount(CheckingAccount account) {
        if (account == null) return;

        File file = new File(System.getProperty("user.dir") + "/checkingaccount.csv");
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            String header = br.readLine();
            lines.add(header);
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (!parts[1].equals(account.getAccountNumber())) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file during deletion.");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file during deletion.");
        }
    }


    private static int getValidInput(Scanner scan, int min, int max) {
        while (true) {
            if (scan.hasNextInt()) {
                int choice = scan.nextInt();
                scan.nextLine();
                if (choice >= min && choice <= max) {
                    return choice;
                }
            } else {
                scan.nextLine();
            }
            System.out.print("Invalid input. Please enter a number between " + min + " and " + max + ": ");
        }
    }

    private static double getValidDouble(Scanner scan) {
        while (true) {
            if (scan.hasNextDouble()) {
                double val = scan.nextDouble();
                scan.nextLine();
                return val;
            } else {
                scan.nextLine();
            }
            System.out.print("Invalid input. Please enter a valid number: ");
        }
    }

    private static void displayAccountInfo(CheckingAccount account) {
        if (account != null) { // Check for null account
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Date Opened: " + account.getDateOpened());
            System.out.println("Last Transaction Date: " + account.getLastTransactionDate());
            System.out.println("Balance: $" + account.getBalance());
            System.out.println("Overdraft Protection: " + account.isOverdraftProtectionEnabled());
            System.out.println("Overdraft Limit: $" + account.getOverdraftLimit());
            System.out.println("Overdraft Fee: $" + account.getOverdraftFee());
        } else {
            System.out.println("Account information could not be displayed. Account is not initialized.");
        }
    }
}

class CheckingAccount {
    private String uniqueID;
    private String accountNumber;
    private String accountHolderName;
    private double balance;
    private boolean isOverdraftProtectionEnabled;
    private double overdraftLimit;
    private double overdraftFee = 35.00;
    private CheckingAccount linkedcheckingAccount;
    private Date dateOpened;
    private Date lastTransactionDate;
    private static final String CSV_FILE = "checkingaccount.csv";

    // Constructor for loading existing account
    public CheckingAccount(String uniqueID, String accountNumber, String accountHolderName, double balance, boolean isOverdraftProtectionEnabled, double overdraftLimit, Date dateOpened, Date lastTransactionDate) {
        this.uniqueID = uniqueID;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.isOverdraftProtectionEnabled = isOverdraftProtectionEnabled;
        this.overdraftLimit = overdraftLimit;
        this.dateOpened = dateOpened;
        this.lastTransactionDate = lastTransactionDate;
        this.linkedcheckingAccount = null;
    }

    // Constructor for new account
    public CheckingAccount(String uniqueID, String accountHolderName, double balance, boolean isOverdraftProtectionEnabled, double overdraftLimit) {
        this.uniqueID = uniqueID;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.accountNumber = generateUniqueAccountNumber();
        this.isOverdraftProtectionEnabled = isOverdraftProtectionEnabled;
        this.overdraftLimit = overdraftLimit;
        this.dateOpened = new Date();
        this.lastTransactionDate = new Date();
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) return false;
        if (amount <= balance) {
            balance -= amount;
            lastTransactionDate = new Date();
            updateCSV();
            return true;
        } else if (isOverdraftProtectionEnabled && linkedcheckingAccount != null && linkedcheckingAccount.getBalance() >= (amount - balance)) {
            double needed = amount - balance;
            linkedcheckingAccount.withdraw(needed);
            balance = 0;
            lastTransactionDate = new Date();
            updateCSV();
            return true;
        } else if (amount <= balance + overdraftLimit) {
            balance -= (amount + overdraftFee);
            lastTransactionDate = new Date();
            updateCSV();
            return true;
        }
        return false;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            lastTransactionDate = new Date();
            updateCSV();
        }
    }

    public double getBalance() { return balance; }
    public String getAccountNumber() { return accountNumber; }
    public Date getDateOpened() { return dateOpened; }
    public Date getLastTransactionDate() { return lastTransactionDate; }
    public boolean isOverdraftProtectionEnabled() { return isOverdraftProtectionEnabled; }
    public double getOverdraftLimit() { return overdraftLimit; }
    public double getOverdraftFee() { return overdraftFee; }

    private String generateUniqueAccountNumber() {
        Set<String> existing = getExistingAccountNumbers();
        String num;
        do {
            num = String.format("%012d", (long)(Math.random() * 1_000_000_000_000L));
        } while (existing.contains(num));
        return num;
    }

    private Set<String> getExistingAccountNumbers() {
        Set<String> nums = new HashSet<>();
        File file = new File(CSV_FILE);
        if (!file.exists()) return nums;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) nums.add(parts[1]);
            }
        } catch (IOException e) {}
        return nums;
    }

    public void saveToCSV() {
        boolean exists = new File(CSV_FILE).exists();
        try (FileWriter fw = new FileWriter(CSV_FILE, true)) {
            if (!exists) fw.append("uniqueID,AccountNumber,AccountHolderName,Balance,OverdraftProtection,OverdraftLimit,DateOpened,LastTransactionDate\n");
            fw.append(String.join(",",
                uniqueID,
                accountNumber,
                accountHolderName,
                String.format("%.2f", balance),
                String.valueOf(isOverdraftProtectionEnabled),
                String.format("%.2f", overdraftLimit),
                dateOpened.toString(),
                lastTransactionDate.toString()
            ));
            fw.append("\n");
            System.out.println("[✓] Account saved to " + CSV_FILE);
        } catch (IOException e) {
            System.out.println("[✗] Error saving account to " + CSV_FILE);
            e.printStackTrace();
        }
    }

    public void updateCSV() {
        List<String> lines = new ArrayList<>();
        String header = "uniqueID,AccountNumber,AccountHolderName,Balance,OverdraftProtection,OverdraftLimit,DateOpened,LastTransactionDate";
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String ln;
            boolean first = true;
            while ((ln = br.readLine()) != null) {
                if (first) { lines.add(header); first = false; continue; }
                String[] parts = ln.split(",");
                if (parts[1].equals(accountNumber)) {
                    parts[3] = String.format("%.2f", balance);
                    parts[7] = lastTransactionDate.toString();
                }
                lines.add(String.join(",", parts));
            }
        } catch (IOException e) {
            System.out.println("[✗] Error reading file during update.");
            e.printStackTrace();
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE))) {
            for (String line : lines) writer.write(line + "\n");
            System.out.println("[✓] Account updated in " + CSV_FILE);
        } catch (IOException e) {
            System.out.println("[✗] Error writing file during update.");
            e.printStackTrace();
        }
    }

    public static CheckingAccount loadByAccountNumber(String accountNumber) {
        try (BufferedReader br = new BufferedReader(new FileReader("checkingaccount.csv"))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 8 && data[1].equals(accountNumber)) {
                    String uniqueID = data[0];
                    String accNum = data[1];
                    String name = data[2];
                    double balance = Double.parseDouble(data[3]);
                    boolean overdraft = Boolean.parseBoolean(data[4]);
                    double limit = Double.parseDouble(data[5]);
                    SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                    Date opened = formatter.parse(data[6]);
                    Date last = formatter.parse(data[7]);

                    return new CheckingAccount(uniqueID, accNum, name, balance, overdraft, limit, opened, last);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getUniqueID() {
        return uniqueID;
    }

}
