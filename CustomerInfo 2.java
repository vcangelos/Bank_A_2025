import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

class CreditCard {

    private static final double MAX_CREDIT_LIMIT = 15000.00;
    private static final double MIN_CREDIT_LIMIT = 500.00;
    private static final double MAX_CREDIT_SCORE = 850;
    private static final double MIN_CREDIT_SCORE = 300;
    private static final double MAX_DTI_RATIO = 36.0;
    private static final double MAX_CREDIT_UTILIZATION = 0.3;
    private double limit;
    private double currentBalance;
    private double rate;
    private ArrayList<Double> transactions;
    private LocalDate billingCycleEndDate;
    private long cardNumber;
    private long cvv;
    private String expiryDate;
    
    // Constructor

    public CreditCard() {
        transactions = new ArrayList<>();
        billingCycleEndDate = LocalDate.now().plusMonths(1);
        expiryDate = "03/28"; 
    }

    // Main method

    public static void main(String[] args) 
        Scanner scanner = new Scanner(System.in);
        CreditCard creditCard = new CreditCard();
        creditCard.mainMenu(scanner);
    }

    // Main Menu method with two different paths

    public void mainMenu(Scanner scanner) {
        while (true) {
            try {
                System.out.println("\nWelcome to Credit Card Services:");
                System.out.println("1. Do you have a credit card?");
                System.out.println("2. Do you want to apply for a credit card?");
                System.out.println("3. Exit");
                int mainChoice = scanner.nextInt();
                scanner.nextLine();
                switch (mainChoice) {
                    case 1:
                        existingCardMenu(scanner);
                        break;
                    case 2:
                        newCardMenu(scanner);
                        break;
                    case 3:
                        System.out.println("Exiting program...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Pay Using Credit Card

    public void payUsingCredit(Scanner scanner) {
        System.out.println("Enter credit card number:");
        long inputCardNumber = scanner.nextLong();
        System.out.println("Enter CVV:");
        long inputCvv = scanner.nextLong();
        if (!validateCard(inputCardNumber, inputCvv)) {
            System.out.println("Invalid credit card number or CVV.");
            return;
        }
        System.out.println("Enter amount to swipe:");
        double swipeAmount = scanner.nextDouble();
        swipe(swipeAmount);
    }
    
    // Validate Card Against CSV File

    public boolean validateCard(long inputCardNumber, long inputCvv) {
        String csvFile = "csv/creditCardData.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                long storedCardNumber = Long.parseLong(data[0].trim());
                long storedCvv = Long.parseLong(data[1].trim());
                if (storedCardNumber == inputCardNumber && storedCvv == inputCvv) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading card data: " + e.getMessage());
        }
        return false;
    }

    // Swipe Transaction

    public void swipe(double swipeAmount) {
        if (currentBalance + swipeAmount > limit) {
            System.out.println("Transaction failed. Credit limit exceeded.");
        } else {
            currentBalance += swipeAmount;
            transactions.add(swipeAmount);
            System.out.println("Transaction successful.");
        }
    }

    public void printStatement() {
        DecimalFormat df = new DecimalFormat("#0.00");
        System.out.println("Bank Statement:");
        transactions.forEach(transaction -> System.out.println("$" + transaction));
        System.out.println("Current amount spent: $" + df.format(currentBalance));
        LocalDate currentDate = LocalDate.now();
        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        int daysLeft = lastDayOfMonth.getDayOfMonth() - currentDate.getDayOfMonth() + 1;
        System.out.println("Days left in billing cycle: " + daysLeft);
    }

    public void payBankStatement(Scanner scanner) {
        DecimalFormat df = new DecimalFormat("#0.00");
        double totalStatement = getTotalStatement();
        double minimumPayment = totalStatement * 0.05;
        System.out.println("Your minimum payment is: $" + df.format(minimumPayment));
        System.out.println("Enter amount to pay:");
        double payAmount = scanner.nextDouble();
        if (payAmount >= minimumPayment && payAmount <= totalStatement) {
            currentBalance -= payAmount;
            System.out.println("Payment successful. Remaining balance: $" + df.format(currentBalance));
        } else {
            System.out.println("Payment failed. Please pay at least the minimum statement and not more than the total statement.");
        }
    }

    public double getTotalStatement() {
        return transactions.stream().mapToDouble(Double::doubleValue).sum();
    }

    public void writeUser DataToCSV(String filename, long cardNumber, long cvv,
                                    double limit, String expiryDate, double rate) {
        String directoryName = "csv";
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String fullPath = directoryName + File.separator + filename;
        try (FileWriter writer = new FileWriter(fullPath, true)) {
            writer.write("Number,CVV,Credit Limit,Expiration Date,Default Interest Rate\n");
            writer.write(String.format("%d,%d,%.2f,%s,%.3f\n",
                    cardNumber, cvv, limit, expiryDate, rate));
            System.out.println("User  data has been written to CSV file.");
        } catch (IOException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }
} 
