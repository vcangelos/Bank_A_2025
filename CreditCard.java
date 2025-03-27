// Anthony P, Gabriela M
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;

public class CreditCard 
{
    private double creditLimit;
    private double balance;
    private double interestRate;
    private ArrayList<Double> bankStatement;
    private LocalDate billingCycleEnd;

    public CreditCard() 
   {
        // Initialize bank statement and billing cycle
        bankStatement = new ArrayList<>();
        billingCycleEnd = LocalDate.now().plusMonths(1); // Set billing cycle to end of next month
    }

    public static void main(String[] args) 
   {
        // Create instances of required classes
        Scanner scanner = new Scanner(System.in);
        CreditCard creditCard = new CreditCard(); 
        CheckingAccount checkingAccount = new CheckingAccount(e); 
        double balanceCA = checkingAccount.getBalance();
        CustomerInformation customerInformation = new CustomerInformation(); 
        creditCard.menu(scanner); // Start the main menu
    }

    // Main menu for credit card operations
    public void menu(Scanner scanner) 
   {
        while (true) 
        {
            // Display comprehensive menu options
            System.out.println("==== Credit Card Management System ====");
            System.out.println("Please select an option:");
            System.out.println("1. Apply for a New Credit Card");
            System.out.println("2. Make a Credit Card Purchase");
            System.out.println("3. View Bank Statement");
            System.out.println("4. Pay Credit Card Balance");
            System.out.println("5. Return to Main Menu");

            int menuChoice = scanner.nextInt();

            // Handle menu selections with descriptive user guidance
            switch (menuChoice) 
            {
                case 1:
                    applyForCredit(scanner);
                    break;
                case 2:
                    payUsingCredit(scanner);
                    break;
                case 3:
                    printStatement();
                    break;
                case 4:
                    payBankStatement(scanner, balanceCA);
                    break;
                case 5:
                    System.out.println("Returning to main customer menu...");
                    customerInformation.displayMenu();
                    return;
                default:
                    System.out.println("Invalid option. Please select a number between 1 and 5.");
            }
        }
    }

    // Process credit card application
    public void applyForCredit(Scanner scanner) 
   {
        System.out.println("=== Credit Card Application ===");
        System.out.println("Processing your credit card application...");
        
        // Collect and validate application details
        int creditScore = getCreditScore(scanner);
        double interestRateD = getInterestRate(creditScore);
        double creditURate = getCreditURate(scanner);
        double dti = getDTI(scanner);
        
        // Generate credit card details
        long creditCardNumber = getCreditCardNumber();
        long creditCardCVV = getCreditCardCVV();
        creditLimit = getCreditCardLimit(creditScore); 
        String expirationDate = "03/27";
        
        // Display credit card terms
        creditCardTerms(creditCardNumber, creditCardCVV, expirationDate, interestRateD, creditLimit);
        
        // Save user data to CSV
        writeUserDataToCSV("filename", creditCardNumber, creditCardCVV, creditLimit, expirationDate, interestRateD);
        
        menu(scanner); // Return to main menu
    }

    // Validate and input credit score
    public int getCreditScore(Scanner scanner) 
   {
        int creditScore;
        while (true) 
        {
            System.out.print("Enter your credit score (300-850): ");
            if (scanner.hasNextInt()) {
                creditScore = scanner.nextInt();
                if (creditScore > 850 || creditScore < 300) {
                    System.out.println("Error: Credit score must be between 300 and 850. Please retry.");
                    continue;
                }
                break;
            } else 
            {
                System.out.println("Invalid input. Please enter a numeric credit score.");
                scanner.next();
            }
        }
        return creditScore;
    }

    // Calculate and validate debt-to-income ratio
    public double getDTI(Scanner scanner) 
   {
        double monthlyDebt;
        double salary;
        double dti;
        while (true) 
        {
            System.out.print("Enter total monthly debt payments: $");
            if (scanner.hasNextDouble()) 
            {
                monthlyDebt = scanner.nextDouble();
            } else 
            {
                System.out.println("Invalid input for monthly debt. Please retry.");
                scanner.next();
                continue;
            }

            System.out.print("Enter yearly salary: $");
            if (scanner.hasNextDouble()) 
            {
                salary = scanner.nextDouble();
            } else {
                System.out.println("Invalid input for salary. Please retry.");
                scanner.next();
                continue;
            }

            // Calculate debt-to-income ratio
            dti = (monthlyDebt / (salary / 12)) * 100;

            if (dti > 36) {
                System.out.println("Application denied: Debt-to-income ratio exceeds acceptable limit.");
                menu(scanner);
            } else 
            {
                break;
            }
        }
        return dti;
    }

    // Validate credit utilization rate
    public double getCreditURate(Scanner scanner) 
   {
        double balanceA;
        double limitA;
        double creditURate;
        while (true) 
        {
            System.out.print("Enter total current credit card balances: $");
            if (scanner.hasNextDouble()) {
                balanceA = scanner.nextDouble();
            } else 
            {
                System.out.println("Invalid input for credit card balances. Please retry.");
                scanner.next();
                continue;
            }

            System.out.print("Enter total current credit card limits: $");
            if (scanner.hasNextDouble()) 
            {
                limitA = scanner.nextDouble();
            } else 
            {
                System.out.println("Invalid input for credit card limits. Please retry.");
                scanner.next();
                continue;
            }

            // Calculate credit utilization rate
            creditURate = balanceA / limitA;

            if (creditURate > 0.3) 
            {
                System.out.println("Application denied: Credit utilization rate too high.");
                menu(scanner);
            } else 
            {
                break;
            }
        }
        return creditURate;
    }

    // Determine interest rate based on credit score
    public double getInterestRate(int creditScore) 
   {
        double interestRateD;
        if (creditScore <= 579) {
            interestRateD = 0.211; // High-risk rate
        } else if (creditScore <= 619) 
        {
            interestRateD = 0.202; // Moderate-high risk rate
        } else if (creditScore <= 659) 
        {
            interestRateD = 0.191; // Moderate risk rate
        } else if (creditScore <= 719) 
        {
            interestRateD = 0.169; // Lower risk rate
        } else {
            interestRateD = 0.127; // Low-risk rate
        }
        return interestRateD;
    }

    // Generate random credit card number
    public long getCreditCardNumber() 
   {
        return ThreadLocalRandom.current().nextLong(1000000000000000L, 10000000000000000L);
    }

    // Generate random CVV number
    public long getCreditCardCVV() 
   {
        return ThreadLocalRandom.current().nextLong(100, 1000);
    }

    // Determine credit limit based on credit score
    public double getCreditCardLimit(int creditScore) 
   {
        if (creditScore > 750) 
        {
            return 15000.00; // Excellent credit
        } else if (creditScore >= 700) 
        {
            return 7500.00; // Very good credit
        } else if (creditScore >= 650) 
        {
            return 3500.00; // Good credit
        } else if (creditScore >= 600) 
        {
            return 1250.00; // Fair credit
        } else {
            return 500.00; // Poor credit
        }
    }

    // Display approved credit card terms
    public void creditCardTerms(long creditCardNumber, long creditCardCVV, String expirationDate, double interestRateD, double creditLimit) 
   {
        DecimalFormat df = new DecimalFormat("#0.00");
        System.out.println("=== Credit Card Approval Details ===");
        System.out.println("Congratulations! Your credit card has been approved.");
        System.out.println("Card Number: " + creditCardNumber);
        System.out.println("CVV: " + creditCardCVV);
        System.out.println("Expiration Date: " + expirationDate);
        System.out.println("Interest Rate: " + (interestRateD * 100) + "%");
        System.out.println("Credit Limit: $" + df.format(creditLimit));
    }

    // Process credit card payment
    public void payUsingCredit(Scanner scanner) 
   {
        System.out.println("=== Credit Card Purchase ===");
        System.out.print("Enter credit card number: ");
        long cardNumber = scanner.nextLong();
        System.out.print("Enter CVV: ");
        long cvv = scanner.nextLong();
        
        // Validate card details
        if (!validateCard(cardNumber, cvv)) 
        {
            System.out.println("Error: Invalid credit card number or CVV.");
            return;
        }
        
        System.out.print("Enter purchase amount: $");
        double swipeAmount = scanner.nextDouble();
        swipe(swipeAmount);
    }

    // Validate card details against stored records
    public boolean validateCard(long cardNumber, long cvv) 
   {
        String csvFile = "csv/creditCardData.csv";
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) 
        {
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) 
            {
                String[] data = line.split(cvsSplitBy);
                long storedCardNumber = Long.parseLong(data[0].trim());
                long storedCVV = Long.parseLong(data[1].trim());
                if (storedCardNumber == cardNumber && storedCVV == cvv) 
                {
                    return true; // Valid card found
                }
            }
        } catch (IOException e) 
        {
            System.out.println("Error reading card data: " + e.getMessage());
        }
        return false; // Card not found
    }

    // Process credit card transaction
    public void swipe(double swipeAmount) 
   {
        if (balance + swipeAmount > creditLimit) 
        {
            System.out.println("Transaction Failed: Purchase exceeds credit limit.");
        } else {
            balance += swipeAmount;
            bankStatement.add(swipeAmount);
            System.out.println("Transaction Successful: $" + swipeAmount + " charged to credit card.");
        }
    }

    // Print detailed bank statement
    public void printStatement() 
   {
        DecimalFormat df = new DecimalFormat("#0.00");
        System.out.println("=== Detailed Bank Statement ===");
        if (bankStatement.isEmpty()) 
        {
            System.out.println("No transactions recorded.");
        } else {
            System.out.println("Transaction History:");
            for (double transaction : bankStatement) 
            {
                System.out.println("- Purchase: $" + df.format(transaction));
            }
        }
        System.out.println("Current Balance: $" + df.format(balance));
        
        // Calculate and display billing cycle information
        LocalDate currentDate = LocalDate.now();
        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        int daysLeft = lastDayOfMonth.getDayOfMonth() - currentDate.getDayOfMonth() + 1;
        System.out.println("Days Remaining in Billing Cycle: " + daysLeft);
    }

    // Process bank statement payment
    public void payBankStatement(Scanner scanner, double balanceCA) 
   {
        DecimalFormat df = new DecimalFormat("#0.00");
        double totalStatement = getTotalStatement();
        double minimumPayment = totalStatement * 0.05;
        
        System.out.println("=== Credit Card Balance Payment ===");
        System.out.println("Total Statement Balance: $" + df.format(totalStatement));
        System.out.println("Minimum Payment Required: $" + df.format(minimumPayment));
        
        System.out.print("Enter payment amount: $");
        double payAmount = scanner.nextDouble();
        
        // Validate payment amount
        if (payAmount >= minimumPayment && payAmount <= totalStatement) 
        {
            balance -= payAmount;
            balanceCA -= payAmount;
            System.out.println("Payment Successful. Remaining Balance: $" + df.format(balance));
        } else 
        {
            System.out.println("Payment Failed: Must pay at least minimum amount, not exceeding total balance.");
        }
    }

    // Calculate total statement amount
    public double getTotalStatement() 
   {
        double totalStatement = 0;
        for (double transaction : bankStatement) 
        {
            totalStatement += transaction;
        }
        return totalStatement;
    }

    // Write user data to CSV file
    public void writeUserDataToCSV(String filename, long creditCardNumber, long creditCardCVV, double creditLimit, String expirationDate, double interestRateD) {
        String directoryName = "csv";
        File directory = new File(directoryName);
        if (!directory.exists()) 
        {
            directory.mkdir(); // Create directory if not exists
        }

        String fileName = directoryName + File.separator + "creditCardData.csv";
        try (FileWriter writer = new FileWriter(fileName)) 
        {
            // Write CSV header and data
            writer.write("Number, CVV, Credit Limit, Expiration Date, Default Interest Rate\n");
            writer.write(creditCardNumber + "," + creditCardCVV + "," + creditLimit + "," + expirationDate + "," +
                    interestRateD + "\n");
            System.out.println("User data successfully saved to CSV file.");
        } catch (IOException e) 
        {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }

    // Read and modify CSV file (for potential future use)
    public void readModifyCSV(String fileName) 
   {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("creditCardData.csv", true));
             BufferedReader reader = new BufferedReader(new FileReader(fileName))) 
        {
            String line;
            System.out.println("\nProcessing data from " + fileName + ":");
            while ((line = reader.readLine()) != null) 
            {
                // Potential data modification logic
                line += ",Modified";
                writer.write(line);
                writer.newLine();
                System.out.println("Processed line: " + line);
            }
            System.out.println("Data processing completed.");
        } catch (IOException e) 
        {
            System.out.println("Error processing CSV file: " + e.getMessage());
        }
    }
}
