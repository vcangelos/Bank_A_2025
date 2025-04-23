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


// Main method
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CreditCard creditCard = new CreditCard();
        creditCard.mainMenu(scanner);
    }
class CreditCard {

    // Constants
    private static final double MAX_CREDIT_LIMIT = 15000.00;
    private static final double MIN_CREDIT_LIMIT = 500.00;
    private static final double MAX_CREDIT_SCORE = 850;
    private static final double MIN_CREDIT_SCORE = 300;
    private static final double MAX_DTI_RATIO = 36.0;
    private static final double MAX_CREDIT_UTILIZATION = 0.3;

    // Instance variables
    private double creditLimit;
    private double balance;
    private double interestRate;
    private ArrayList<Double> bankStatement;
    private LocalDate billingCycleEnd;
    private long creditCardNumber;
    private long creditCardCVV;
    private String expirationDate;
    private int UniqueID;

    // Constructor
    public CreditCard() {
        bankStatement = new ArrayList<>();
        billingCycleEnd = LocalDate.now().plusMonths(1);
        expirationDate = "03/28"; // Default expiration date
    }

    

    // Main Menu method with two different paths
    public void mainMenu(Scanner scanner) {
        while (true) {
            try {
                System.out.println("\nWelcome to Credit Card Services:");
                System.out.println("1. Do you have a credit card?");// search for unique id if no, do option 2
                System.out.println("2. Do you want to apply for a credit card?");
                System.out.println("3. Exit");

                int mainChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

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

    // Menu for existing credit card holders
    public void existingCardMenu(Scanner scanner) {
        while (true) {
            try {
                System.out.println("\nExisting Credit Card Holder Menu:");
                System.out.println("1. Pay Using Credit Card");
                System.out.println("2. Review Bank Statement");
                System.out.println("3. Pay Bank Statement");
                System.out.println("4. Return to Main Menu");

                int menuChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (menuChoice) {
                    case 1:
                        payUsingCredit(scanner);
                        break;
                    case 2:
                        printStatement();
                        break;
                    case 3:
                        payBankStatement(scanner);
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Menu for new credit card applicants
    public void newCardMenu(Scanner scanner) {
        while (true) {
            try {
                System.out.println("\nNew Credit Card Applicant Menu:");
                System.out.println("1. Apply for Credit");
                System.out.println("2. Return to Main Menu");

                int menuChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (menuChoice) {
                    case 1:
                        applyForCredit(scanner);
                        return;
                    case 2:
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Credit Card Approval method with improved validation
    public void applyForCredit(Scanner scanner) {
        System.out.println("Applying for credit...");

        int creditScore = getCreditScore(scanner);
        double interestRateD = getInterestRate(creditScore);
        double creditURate = getCreditURate(scanner);
        double dti = getDTI(scanner);

        // Generate card details
        creditCardNumber = getCreditCardNumber();
        creditCardCVV = getCreditCardCVV();
        creditLimit = getCreditCardLimit(creditScore);

        // Display and save credit card terms
        creditCardTerms(creditCardNumber, creditCardCVV, expirationDate, interestRateD, creditLimit);
        writeUserDataToCSV("creditCardData.csv", creditCardNumber, creditCardCVV, creditLimit, expirationDate, interestRateD, UniqueID);
    }

    // Validate and get credit score
    public int getCreditScore(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Please enter your credit score: ");
                int creditScore = scanner.nextInt();

                if (creditScore < MIN_CREDIT_SCORE || creditScore > MAX_CREDIT_SCORE) {
                    System.out.printf("Credit score must be between %.0f and %.0f. Please try again.\n",
                            MIN_CREDIT_SCORE, MAX_CREDIT_SCORE);
                    continue;
                }

                return creditScore;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid credit score.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Validate Debt-to-Income Ratio
    public double getDTI(Scanner scanner) {
        while (true) {
            try {
                System.out.print("What are your total monthly debt payments? ");
                double monthlyDebt = scanner.nextDouble();

                System.out.print("What is your yearly salary? ");
                double yearlySalary = scanner.nextDouble();

                double dti = (monthlyDebt / (yearlySalary / 12)) * 100;

                if (dti > MAX_DTI_RATIO) {
                    System.out.println("Your debt-to-income ratio is too high to be approved for a credit card.");
                    continue;
                }

                return dti;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter valid numbers.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Validate Credit Utilization Rate
    public double getCreditURate(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Sum of current credit card balances: ");
                double balanceA = scanner.nextDouble();

                System.out.print("Sum of current credit card limits: ");
                double limitA = scanner.nextDouble();

                double creditURate = balanceA / limitA;

                if (creditURate > MAX_CREDIT_UTILIZATION) {
                    System.out.println("Credit utilization rate is too high for approval.");
                    continue;
                }

                return creditURate;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter valid numbers.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Determine Interest Rate Based on Credit Score
    public double getInterestRate(int creditScore) {
        if (creditScore <= 579) return 0.211;
        if (creditScore <= 619) return 0.202;
        if (creditScore <= 659) return 0.191;
        if (creditScore <= 719) return 0.169;
        return 0.127;
    }

    // Generate Random Credit Card Number
    public long getCreditCardNumber() {
        return ThreadLocalRandom.current().nextLong(1_000_000_000_000_000L, 10_000_000_000_000_000L);
    }

    // Generate Random CVV Number
    public long getCreditCardCVV() {
        return ThreadLocalRandom.current().nextLong(100, 1000);
    }

    // Determine Credit Card Limit Based on Credit Score
    public double getCreditCardLimit(int creditScore) {
        if (creditScore > 750) return MAX_CREDIT_LIMIT;
        if (creditScore >= 700) return 7500.00;
        if (creditScore >= 650) return 3500.00;
        if (creditScore >= 600) return 1250.00;
        return MIN_CREDIT_LIMIT;
    }

    // Print Credit Card Terms
    public void creditCardTerms(long creditCardNumber, long creditCardCVV,
                                String expirationDate, double interestRateD, double creditLimit) {
        DecimalFormat df = new DecimalFormat("#0.00");

        System.out.println("\nApproved Credit Card Details:");
        System.out.println("Number: " + creditCardNumber);
        System.out.println("CVV: " + creditCardCVV);
        System.out.println("Expiration Date: " + expirationDate);
        System.out.println("Default Interest Rate: " + interestRateD);
        System.out.println("Credit Card Limit: $" + df.format(creditLimit));
    }

    // Pay Using Credit Card
    public void payUsingCredit(Scanner scanner) {
        System.out.println("Enter credit card number:");
        long cardNumber = scanner.nextLong();

        System.out.println("Enter CVV:");
        long cvv = scanner.nextLong();

        if (!validateCard(cardNumber, cvv)) {
            System.out.println("Invalid credit card number or CVV.");
            return;
        }

        System.out.println("Enter amount to swipe:");
        double swipeAmount = scanner.nextDouble();

        swipe(swipeAmount);
    }

    // Validate Card Against CSV File
    public boolean validateCard(long cardNumber, long cvv) {
        String csvFile = "csv/creditCardData.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                long storedCardNumber = Long.parseLong(data[0].trim());
                long storedCVV = Long.parseLong(data[1].trim());

                if (storedCardNumber == cardNumber && storedCVV == cvv) {
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
        if (balance + swipeAmount > creditLimit) {
            System.out.println("Transaction failed. Credit limit exceeded.");
        } else {
            balance += swipeAmount;
            bankStatement.add(swipeAmount);
            System.out.println("Transaction successful.");
        }
    }

    // Print Bank Statement
    public void printStatement() {
        DecimalFormat df = new DecimalFormat("#0.00");

        System.out.println("Bank Statement:");
        bankStatement.forEach(transaction -> System.out.println("$" + transaction));

        System.out.println("Current amount spent: $" + df.format(balance));

        LocalDate currentDate = LocalDate.now();
        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        int daysLeft = lastDayOfMonth.getDayOfMonth() - currentDate.getDayOfMonth() + 1;

        System.out.println("Days left in billing cycle: " + daysLeft);
    }

    // Pay Bank Statement
    public void payBankStatement(Scanner scanner) {
        DecimalFormat df = new DecimalFormat("#0.00");

        double totalStatement = getTotalStatement();
        double minimumPayment = totalStatement * 0.05;

        System.out.println("Your minimum payment is: $" + df.format(minimumPayment));
        System.out.println("Enter amount to pay:");

        double payAmount = scanner.nextDouble();

        if (payAmount >= minimumPayment && payAmount <= totalStatement) {
            balance -= payAmount;
            System.out.println("Payment successful. Remaining balance: $" + df.format(balance));
        } else {
            System.out.println("Payment failed. Please pay at least the minimum statement and not more than the total statement.");
        }
    }

    // Calculate Total Statement Amount
    public double getTotalStatement() {
        return bankStatement.stream().mapToDouble(Double::doubleValue).sum();
    }

    // Write User Data to CSV
    public void writeUserDataToCSV(String filename, long creditCardNumber, long creditCardCVV,
                                   double creditLimit, String expirationDate, double interestRateD) {
        String directoryName = "csv";
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }

        String fullPath = directoryName + File.separator + filename;

        try (FileWriter writer = new FileWriter(fullPath, true)) {
            writer.write("Number,CVV,Credit Limit,Expiration Date,Default Interest Rate\n");
            writer.write(String.format("%d,%d,%.2f,%s,%.3f\n",
                    creditCardNumber, creditCardCVV, creditLimit, expirationDate, interestRateD));
            System.out.println("User data has been written to CSV file.");
        } catch (IOException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }
}







// the one i connected 


class CreditCard {

    // Constants
    private static final double MAX_CREDIT_LIMIT = 15000.00;
    private static final double MIN_CREDIT_LIMIT = 500.00;
    private static final double MAX_CREDIT_SCORE = 850;
    private static final double MIN_CREDIT_SCORE = 300;
    private static final double MAX_DTI_RATIO = 36.0;
    private static final double MAX_CREDIT_UTILIZATION = 0.3;

    // Instance variables
    private double creditLimit;
    private double balance;
    private double interestRate;
    private ArrayList<Double> bankStatement;
    private LocalDate billingCycleEnd;
    private long creditCardNumber;
    private long creditCardCVV;
    private String expirationDate;
    private int UniqueID;

    // Constructor
    public CreditCard() {
        bankStatement = new ArrayList<>();
        billingCycleEnd = LocalDate.now().plusMonths(1);
        expirationDate = "03/28"; // Default expiration date
    }
    public void getID(int iD){
        this.UniqueID = iD;
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
                scanner.nextLine(); // Consume newline

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

    // Menu for existing credit card holders
    public void existingCardMenu(Scanner scanner) {
        while (true) {
            try {
                System.out.println("\nExisting Credit Card Holder Menu:");
                System.out.println("1. Pay Using Credit Card");
                System.out.println("2. Review Bank Statement");
                System.out.println("3. Pay Bank Statement");
                System.out.println("4. Return to Main Menu");

                int menuChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (menuChoice) {
                    case 1:
                        payUsingCredit(scanner);
                        break;
                    case 2:
                        printStatement();
                        break;
                    case 3:
                        payBankStatement(scanner);
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Menu for new credit card applicants
    public void newCardMenu(Scanner scanner) {
        while (true) {
            try {
                System.out.println("\nNew Credit Card Applicant Menu:");
                System.out.println("1. Apply for Credit");
                System.out.println("2. Return to Main Menu");

                int menuChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (menuChoice) {
                    case 1:
                        applyForCredit(scanner);
                        return;
                    case 2:
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Credit Card Approval method with improved validation
    public void applyForCredit(Scanner scanner) {
        System.out.println("Applying for credit...");

        int creditScore = getCreditScore(scanner);
        double interestRateD = getInterestRate(creditScore);
        double creditURate = getCreditURate(scanner);
        double dti = getDTI(scanner);

        // Generate card details
        creditCardNumber = getCreditCardNumber();
        creditCardCVV = getCreditCardCVV();
        creditLimit = getCreditCardLimit(creditScore);

        // Display and save credit card terms
        creditCardTerms(creditCardNumber, creditCardCVV, expirationDate, interestRateD, creditLimit);
        writeUserDataToCSV("creditCardData.csv", creditCardNumber, creditCardCVV, creditLimit, expirationDate, interestRateD, UniqueID );
    }

    // Validate and get credit score
    public int getCreditScore(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Please enter your credit score: ");
                int creditScore = scanner.nextInt();

                if (creditScore < MIN_CREDIT_SCORE || creditScore > MAX_CREDIT_SCORE) {
                    System.out.printf("Credit score must be between %.0f and %.0f. Please try again.\n",
                            MIN_CREDIT_SCORE, MAX_CREDIT_SCORE);
                    continue;
                }

                return creditScore;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid credit score.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Validate Debt-to-Income Ratio
    public double getDTI(Scanner scanner) {
        while (true) {
            try {
                System.out.print("What are your total monthly debt payments? ");
                double monthlyDebt = scanner.nextDouble();

                System.out.print("What is your yearly salary? ");
                double yearlySalary = scanner.nextDouble();

                double dti = (monthlyDebt / (yearlySalary / 12)) * 100;

                if (dti > MAX_DTI_RATIO) {
                    System.out.println("Your debt-to-income ratio is too high to be approved for a credit card.");
                    continue;
                }

                return dti;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter valid numbers.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Validate Credit Utilization Rate
    public double getCreditURate(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Sum of current credit card balances: ");
                double balanceA = scanner.nextDouble();

                System.out.print("Sum of current credit card limits: ");
                double limitA = scanner.nextDouble();

                double creditURate = balanceA / limitA;

                if (creditURate > MAX_CREDIT_UTILIZATION) {
                    System.out.println("Credit utilization rate is too high for approval.");
                    continue;
                }

                return creditURate;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter valid numbers.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Determine Interest Rate Based on Credit Score
    public double getInterestRate(int creditScore) {
        if (creditScore <= 579) return 0.211;
        if (creditScore <= 619) return 0.202;
        if (creditScore <= 659) return 0.191;
        if (creditScore <= 719) return 0.169;
        return 0.127;
    }

    // Generate Random Credit Card Number
    public long getCreditCardNumber() {
        return ThreadLocalRandom.current().nextLong(1_000_000_000_000_000L, 10_000_000_000_000_000L);
    }

    // Generate Random CVV Number
    public long getCreditCardCVV() {
        return ThreadLocalRandom.current().nextLong(100, 1000);
    }

    // Determine Credit Card Limit Based on Credit Score
    public double getCreditCardLimit(int creditScore) {
        if (creditScore > 750) return MAX_CREDIT_LIMIT;
        if (creditScore >= 700) return 7500.00;
        if (creditScore >= 650) return 3500.00;
        if (creditScore >= 600) return 1250.00;
        return MIN_CREDIT_LIMIT;
    }

    // Print Credit Card Terms
    public void creditCardTerms(long creditCardNumber, long creditCardCVV,
                                String expirationDate, double interestRateD, double creditLimit) {
        DecimalFormat df = new DecimalFormat("#0.00");

        System.out.println("\nApproved Credit Card Details:");
        System.out.println("Number: " + creditCardNumber);
        System.out.println("CVV: " + creditCardCVV);
        System.out.println("Expiration Date: " + expirationDate);
        System.out.println("Default Interest Rate: " + interestRateD);
        System.out.println("Credit Card Limit: $" + df.format(creditLimit));
    }

    // Pay Using Credit Card
    public void payUsingCredit(Scanner scanner) {
        System.out.println("Enter credit card number:");
        long cardNumber = scanner.nextLong();

        System.out.println("Enter CVV:");
        long cvv = scanner.nextLong();

        if (!validateCard(cardNumber, cvv)) {
            System.out.println("Invalid credit card number or CVV.");
            return;
        }

        System.out.println("Enter amount to swipe:");
        double swipeAmount = scanner.nextDouble();

        swipe(swipeAmount);
    }

    // Validate Card Against CSV File
    public boolean validateCard(long cardNumber, long cvv) {
        String csvFile = "csv/creditCardData.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                long storedCardNumber = Long.parseLong(data[0].trim());
                long storedCVV = Long.parseLong(data[1].trim());

                if (storedCardNumber == cardNumber && storedCVV == cvv) {
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
        if (balance + swipeAmount > creditLimit) {
            System.out.println("Transaction failed. Credit limit exceeded.");
        } else {
            balance += swipeAmount;
            bankStatement.add(swipeAmount);
            System.out.println("Transaction successful.");
        }
    }

    // Print Bank Statement
    public void printStatement() {
        DecimalFormat df = new DecimalFormat("#0.00");

        System.out.println("Bank Statement:");
        bankStatement.forEach(transaction -> System.out.println("$" + transaction));

        System.out.println("Current amount spent: $" + df.format(balance));

        LocalDate currentDate = LocalDate.now();
        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        int daysLeft = lastDayOfMonth.getDayOfMonth() - currentDate.getDayOfMonth() + 1;

        System.out.println("Days left in billing cycle: " + daysLeft);
    }

    // Pay Bank Statement
    public void payBankStatement(Scanner scanner) {
        DecimalFormat df = new DecimalFormat("#0.00");

        double totalStatement = getTotalStatement();
        double minimumPayment = totalStatement * 0.05;

        System.out.println("Your minimum payment is: $" + df.format(minimumPayment));
        System.out.println("Enter amount to pay:");

        double payAmount = scanner.nextDouble();

        if (payAmount >= minimumPayment && payAmount <= totalStatement) {
            balance -= payAmount;
            System.out.println("Payment successful. Remaining balance: $" + df.format(balance));
        } else {
            System.out.println("Payment failed. Please pay at least the minimum statement and not more than the total statement.");
        }
    }

    // Calculate Total Statement Amount
    public double getTotalStatement() {
        return bankStatement.stream().mapToDouble(Double::doubleValue).sum();
    }

    // Write User Data to CSV
    public void writeUserDataToCSV(String filename, long creditCardNumber, long creditCardCVV,
                                   double creditLimit, String expirationDate, double interestRateD) {
        String directoryName = "csv";
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }

        String fullPath = directoryName + File.separator + filename;

        try (FileWriter writer = new FileWriter(fullPath, true)) {
            writer.write("Number,CVV,Credit Limit,Expiration Date,Default Interest Rate\n");
            writer.write(String.format("%d,%d,%.2f,%s,%.3f\n",
                    creditCardNumber, creditCardCVV, creditLimit, expirationDate, interestRateD, UniqueID));
            System.out.println("User data has been written to CSV file.");
        } catch (IOException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }
}




