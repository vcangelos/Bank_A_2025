
package checkingaccount;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

class CheckingAccount {
    private String uniqueID;
    private String accountNumber;
    private double balance;
    private String accountHolderName;
    private boolean isOverdraftProtectionEnabled;
    private double overdraftLimit;
    private double overdraftFee = 35.00;
    private SavingsAccount linkedSavingsAccount;
    private Date dateOpened;
    private Date lastTransactionDate;
    private String routingNumber;

    private static final String CSV_FILE = "checkingaccount.csv";

    public CheckingAccount(String uniqueID, String accountHolderName, double balance, boolean isOverdraftProtectionEnabled, SavingsAccount linkedSavingsAccount, double overdraftLimit, String routingNumber) {
        this.uniqueID = uniqueID;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.accountNumber = generateUniqueAccountNumber();
        this.isOverdraftProtectionEnabled = isOverdraftProtectionEnabled;
        this.linkedSavingsAccount = linkedSavingsAccount;
        this.overdraftLimit = overdraftLimit;
        this.dateOpened = new Date();
        this.lastTransactionDate = new Date();
        this.routingNumber = routingNumber;
        saveToCSV();
        System.out.println("Account created successfully. Account number: " + accountNumber);
    }

    public CheckingAccount(String uniqueID, String accountNumber, String accountHolderName, double balance, boolean isOverdraftProtectionEnabled, double overdraftLimit, Date dateOpened, Date lastTransactionDate) {
        this.uniqueID = uniqueID;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.isOverdraftProtectionEnabled = isOverdraftProtectionEnabled;
        this.overdraftLimit = overdraftLimit;
        this.dateOpened = dateOpened;
        this.lastTransactionDate = lastTransactionDate;
        this.linkedSavingsAccount = null;
    }

    private String generateUniqueAccountNumber() {
        String newAccountNumber;
        Set<String> existingNumbers = getExistingAccountNumbers();
        do {
            long number = (long)(Math.random() * 1_000_000_000_000L);
            newAccountNumber = String.format("%012d", number);
        } while (existingNumbers.contains(newAccountNumber));
        return newAccountNumber;
    }

    private Set<String> getExistingAccountNumbers() {
        Set<String> accountNumbers = new HashSet<>();
        File file = new File(CSV_FILE);
        if (!file.exists()) return accountNumbers;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    accountNumbers.add(data[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading account numbers from CSV: " + e.getMessage());
        }
        return accountNumbers;
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
                writer.append("uniqueID,AccountNumber,AccountHolderName,Balance,OverdraftProtection,OverdraftLimit,DateOpened,LastTransactionDate,RoutingNumber\n");
            }
            writer.append(String.join(",",
                uniqueID,
                accountNumber,
                accountHolderName,
                String.format("%.2f", balance),
                String.valueOf(isOverdraftProtectionEnabled),
                String.format("%.2f", overdraftLimit),
                dateOpened.toString(),
                lastTransactionDate.toString(),
                routingNumber
            ));
            writer.append("\n");
            System.out.println("Account details saved to CSV.");
        } catch (IOException e) {
            System.out.println("Error saving account to CSV: " + e.getMessage());
        }
    }

    private void updateCSV() {
        List<String> updatedLines = new ArrayList<>();
        boolean isFirstLine = true;
        String header = "uniqueID,AccountNumber,AccountHolderName,Balance,OverdraftProtection,OverdraftLimit,DateOpened,LastTransactionDate,RoutingNumber";

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (isFirstLine) {
                    if (!line.equals(header)) {
                        updatedLines.add(header);
                    } else {
                        updatedLines.add(line);
                    }
                    isFirstLine = false;
                    continue;
                }

                if (data.length < 8) continue;

                if (data[1].equals(accountNumber)) {
                    data[3] = String.format("%.2f", balance);
                    data[7] = new Date().toString();
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
        String userID = "0";
        CheckingAccount checking = null;

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 8) continue;
                if (data[0].equals(userID)) {
                    String accNumber = data[1];
                    String name = data[2];
                    double balance = Double.parseDouble(data[3]);
                    boolean overdraftProtection = Boolean.parseBoolean(data[4]);
                    double overdraftLimit = Double.parseDouble(data[5]);
                    SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                    Date dateOpened = formatter.parse(data[6]);
                    Date lastTransactionDate = formatter.parse(data[7]);
                    checking = new CheckingAccount(userID, accNumber, name, balance, overdraftProtection, overdraftLimit, dateOpened, lastTransactionDate);
                    System.out.println("Account loaded successfully.");
                    break;
                }
            }
        } catch (IOException | ParseException e) {
            System.out.println("No existing account found. Creating a new one...");
            checking = createNewAccount(scan);
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
        double overdraftLimit = 200.00;

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

        String[] validRoutingNumbers = {
            "832954724", "723297259", "563934953", "384239475", "274539242", "384507747"
        };
        Set<String> validSet = new HashSet<>(Arrays.asList(validRoutingNumbers));
        String routingNumber;

        while (true) {
            System.out.print("Enter routing number: ");
            routingNumber = scan.next();
            if (validSet.contains(routingNumber)) {
                break;
            } else {
                System.out.println("Invalid routing number. Please enter a valid one.");
            }
        }

        return new CheckingAccount("0", name, 0.00, overdraftProtection, linkedSavingsAccount, overdraftLimit, routingNumber);
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
