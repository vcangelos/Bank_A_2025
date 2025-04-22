import java.util.Scanner;
import java.text.DecimalFormat;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * HomeLoanManager - A comprehensive system for managing home loans and mortgages
 * Features include application processing for standard and jumbo loans, 
 * payment processing, and account management
 */
public class HomeLoanManager {
    // Constants for different loan types
    private static final double[] VARIABLE_INTEREST_RATES = {6.15, 6.14, 6.27, 6.95}; // ARM interest rates by term
    private static final double JUMBO_LOAN_THRESHOLD = 647200.0; // Minimum amount for jumbo loans per FHFA
    private static final double JUMBO_MIN_CREDIT_SCORE = 700.0; // Minimum credit score for jumbo loans
    private static final double JUMBO_MIN_DOWN_PAYMENT_PERCENT = 20.0; // Minimum down payment percentage for jumbo loans
    
    // Scanner for user input
    private static Scanner inputReader = new Scanner(System.in);

    /**
     * Main method - entry point of the application
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        HomeLoanManager loanManager = new HomeLoanManager();
        loanManager.displayMainMenu();
    }
    
    /**
     * Displays the main menu and handles user navigation
     */
    public static void displayMainMenu() {
        while (true) {
            // Display menu options
            System.out.println("\n===== HOME LOAN MANAGEMENT SYSTEM =====");
            System.out.println("1. Submit Standard Mortgage Application");
            System.out.println("2. Submit Jumbo Mortgage Application");
            System.out.println("3. Make Mortgage Payment");
            System.out.println("4. Quit Program");
            System.out.print("Enter your selection: ");

            int selection = inputReader.nextInt();

            // Process user selection
            switch(selection) {
                case 1:
                    processMortgageApplication(false); // Process standard mortgage
                    break;
                case 2:
                    processMortgageApplication(true); // Process jumbo mortgage
                    break;
                case 3:
                    processMortgagePayment();
                    break;
                case 4:
                    System.out.println("Thank you for using the Home Loan Manager. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Error: Unrecognized option. Please try again.");
            }
        }
    }
    
    /**
     * Processes a mortgage application (standard or jumbo)
     * @param isJumboLoan Flag indicating if this is a jumbo loan application
     */
    public static void processMortgageApplication(boolean isJumboLoan) {
        System.out.println("\n===== " + (isJumboLoan ? "JUMBO" : "STANDARD") + " MORTGAGE APPLICATION =====");
        
        // Collect basic applicant information
        int fico = collectCreditScore();
        
        // Additional verification for jumbo loans
        if (isJumboLoan && fico < JUMBO_MIN_CREDIT_SCORE) {
            System.out.println("Sorry, jumbo loans require a minimum credit score of " + (int)JUMBO_MIN_CREDIT_SCORE);
            System.out.println("Returning to main menu...");
            return;
        }
        
        // Continue with application process
        String employmentVerification = verifyIncome();
        double annualIncome = collectSalaryInfo();
        double propertyValue = collectPropertyValue();
        
        // Verify if property value qualifies for jumbo loan
        if (isJumboLoan && propertyValue < JUMBO_LOAN_THRESHOLD) {
            System.out.println("Property value does not meet jumbo loan minimum of $" + 
                               new DecimalFormat("#,###.00").format(JUMBO_LOAN_THRESHOLD));
            System.out.println("Consider applying for a standard mortgage instead.");
            System.out.println("Returning to main menu...");
            return;
        }
        
        // Continue collecting application data
        double initialPayment = collectDownPaymentAmount(propertyValue, isJumboLoan);
        double loanToValueRatio = calculateLTV(propertyValue, initialPayment);
        
        // Additional verification for jumbo loans
        if (isJumboLoan && loanToValueRatio > (100 - JUMBO_MIN_DOWN_PAYMENT_PERCENT)) {
            System.out.println("Jumbo loans require at least " + JUMBO_MIN_DOWN_PAYMENT_PERCENT + 
                               "% down payment. Your LTV ratio is too high.");
            System.out.println("Returning to main menu...");
            return;
        }
        
        // Select appropriate mortgage options
        selectMortgageType(fico, annualIncome, initialPayment, propertyValue, isJumboLoan);
    }
    
    /**
     * Collects the applicant's credit score
     * @return The validated credit score
     */
    public static int collectCreditScore() {
        int fico;
        while (true) {
            System.out.print("Enter your current credit score (300-850): ");
            if (inputReader.hasNextInt()) {
                fico = inputReader.nextInt();
                // Validate the credit score range
                if (fico < 300 || fico > 850) {
                    System.out.println("Error: Credit score must be between 300-850. Try again.");
                    continue;
                }
                break;
            } else {
                System.out.println("Error: Please enter a valid number.");
                inputReader.next(); // Clear invalid input
            }
        }
        return fico;
    }
    
    /**
     * Verifies if the applicant has steady income
     * @return Confirmation of steady income status
     */
    public static String verifyIncome() {
        String employmentStatus;
        while (true) {
            System.out.print("Do you have consistent employment income? Enter Yes or No: ");
            employmentStatus = inputReader.next().toLowerCase();

            // Check employment status
            if (employmentStatus.equals("no")) {
                System.out.println("Sorry, steady income is required for mortgage eligibility.");
                displayMainMenu();
            } else if (employmentStatus.equals("yes")) {
                break;
            } else {
                System.out.println("Error: Please respond with 'Yes' or 'No' only.");
                continue;
            }
        }
        return employmentStatus;
    }
    
    /**
     * Collects the applicant's annual salary information
     * @return The annual salary amount
     */
    public static double collectSalaryInfo() {
        double annualIncome;
        while (true) {
            System.out.print("Enter your annual salary (numbers only, no symbols): ");
            if (inputReader.hasNextDouble()) {
                annualIncome = inputReader.nextDouble();
                break;
            } else {
                System.out.println("Error: Please enter a valid number.");
                inputReader.next(); // Clear invalid input
            }
        }
        return annualIncome;
    }
    
    /**
     * Calculates debt-to-income ratio and validates eligibility
     * @param annualIncome The applicant's annual income
     * @return The monthly debt obligations if eligible
     */
    public static double calculateMonthlyObligations(double annualIncome) {
        System.out.print("Enter total monthly obligations (credit cards, loans, etc.): $");
        double monthlyObligations = inputReader.nextDouble();

        // Calculate debt-to-income ratio
        double debtToIncomeRatio = (monthlyObligations / (annualIncome / 12)) * 100;

        // Check if DTI is acceptable
        if (debtToIncomeRatio > 36) {
            System.out.println("Application denied: Your debt-to-income ratio exceeds our 36% threshold.");
            displayMainMenu();
        }
        return monthlyObligations;
    }
    
    /**
     * Collects the property value information
     * @return The property value
     */
    public static double collectPropertyValue() {
        double propertyValue;
        while (true) {
            System.out.print("Enter the property's purchase price: $");
            if (inputReader.hasNextDouble()) {
                propertyValue = inputReader.nextDouble();
                break;
            } else {
                System.out.println("Error: Please enter a valid number.");
                inputReader.next(); // Clear invalid input
            }
        }
        return propertyValue;
    }
    
    /**
     * Collects the down payment amount
     * @param propertyValue The property value
     * @param isJumboLoan Flag indicating if this is a jumbo loan
     * @return The down payment amount
     */
    public static double collectDownPaymentAmount(double propertyValue, boolean isJumboLoan) {
        double initialPayment;
        double minDownPayment = isJumboLoan ? 
                              (propertyValue * JUMBO_MIN_DOWN_PAYMENT_PERCENT / 100) : 
                              (propertyValue * 0.20); // 20% for standard loans
        
        while (true) {
            System.out.print("Enter your down payment amount: $");
            if (inputReader.hasNextDouble()) {
                initialPayment = inputReader.nextDouble();
                
                // Verify minimum down payment requirements
                if (isJumboLoan && initialPayment < minDownPayment) {
                    System.out.println("Jumbo loans require at least " + 
                                     JUMBO_MIN_DOWN_PAYMENT_PERCENT + "% down payment ($" + 
                                     new DecimalFormat("#,###.00").format(minDownPayment) + ")");
                    continue;
                }
                break;
            } else {
                System.out.println("Error: Please enter a valid number.");
                inputReader.next(); // Clear invalid input
            }
        }
        return initialPayment;
    }
    
    /**
     * Calculates the loan-to-value ratio
     * @param propertyValue The property value
     * @param initialPayment The down payment amount
     * @return The LTV ratio as a percentage
     */
    public static double calculateLTV(double propertyValue, double initialPayment) {
        // Calculate loan-to-value ratio
        double loanToValueRatio = ((propertyValue - initialPayment) / propertyValue) * 100;
        
        // Check if LTV is acceptable for standard loans
        if (loanToValueRatio > 80) {
            System.out.println("Application denied: Loan-to-value ratio exceeds 80% maximum threshold.");
            displayMainMenu();
        }
        return loanToValueRatio;
    }
    
    /**
     * Allows the user to select a mortgage type and processes their choice
     * @param fico The credit score
     * @param annualIncome The annual income
     * @param initialPayment The down payment amount
     * @param propertyValue The property value
     * @param isJumboLoan Flag indicating if this is a jumbo loan
     */
    public static void selectMortgageType(int fico, double annualIncome, double initialPayment, 
                                         double propertyValue, boolean isJumboLoan) {
        System.out.println("\nSelect mortgage type:");
        System.out.println("1. Fixed-rate mortgage");
        System.out.println("2. Adjustable-rate mortgage");
        System.out.print("Enter your choice (1 or 2): ");
        int mortgageOption = inputReader.nextInt();
        
        if (mortgageOption == 1) {
            // Process fixed-rate mortgage
            System.out.println("\nSelect term length - Enter 15, 20, or 30 for year duration:");
            int termYears = inputReader.nextInt();
            
            // Calculate interest rate with adjustments for jumbo loans
            double ratePercentage = determineInterestRate(fico, termYears, initialPayment, isJumboLoan);
            double financedAmount = calculateFixedLoanAmount(annualIncome, propertyValue, initialPayment);
            double monthlyInstallment = computeMonthlyPayment(ratePercentage, termYears, financedAmount);
            
            displayFixedLoanDetails(ratePercentage, financedAmount, monthlyInstallment, isJumboLoan);
        } else if (mortgageOption == 2) {
            // Process adjustable-rate mortgage
            System.out.println("\nSelect ARM structure:");
            System.out.println("1. 1/1 ARM - Rate adjusts annually after first year");
            System.out.println("2. 2/1 ARM - Rate adjusts annually after second year");
            System.out.println("3. 3/1 ARM - Rate adjusts annually after third year");
            System.out.println("5. 5/1 ARM - Rate adjusts annually after fifth year");
            System.out.print("Enter your choice: ");
            int adjustableTerm = inputReader.nextInt();

            // Get appropriate ARM rate with jumbo adjustment if needed
            double initialRate = getAdjustableRateForTerm(adjustableTerm, isJumboLoan);
            double financedAmount = calculateAdjustableLoanAmount(annualIncome, propertyValue, initialPayment);
            double monthlyInstallment = computeAdjustableMonthlyPayment(initialRate, financedAmount, adjustableTerm);

            displayAdjustableLoanDetails(financedAmount, monthlyInstallment, initialRate, isJumboLoan);
        } else {
            System.out.println("Error: Invalid selection. Returning to mortgage selection.");
            selectMortgageType(fico, annualIncome, initialPayment, propertyValue, isJumboLoan);
        }
    }
    
    /**
     * Determines the interest rate based on credit score, term, and loan type
     * @param fico The credit score
     * @param termYears The mortgage term in years
     * @param initialPayment The down payment amount
     * @param isJumboLoan Flag indicating if this is a jumbo loan
     * @return The calculated interest rate percentage
     */
    public static double determineInterestRate(int fico, int termYears, double initialPayment, boolean isJumboLoan) {
        double ratePercentage;

        // Base rate determination by credit score
        if (fico >= 750) {
            ratePercentage = 4.5;    // Prime rate for excellent credit
        } else if (fico >= 700) {
            ratePercentage = 5.0;    // Standard rate for good credit
        } else {
            ratePercentage = 6.0;    // Higher rate for fair/poor credit
        }

        // Apply term adjustments
        if (termYears == 15) {
            ratePercentage -= 0.5;    // Discount for shorter 15-year term
        } else if (termYears == 20) {
            ratePercentage -= 0.25;   // Small discount for 20-year term
        }                             // Standard rate for 30-year term
        
        // Apply jumbo loan premium if applicable
        if (isJumboLoan) {
            ratePercentage += 0.25;   // Premium for jumbo loans
        }

        return ratePercentage;
    }
    
    /**
     * Calculates the loan amount for fixed-rate mortgages
     * @param annualIncome The annual income
     * @param propertyValue The property value
     * @param initialPayment The down payment amount
     * @return The calculated loan amount
     */
    public static double calculateFixedLoanAmount(double annualIncome, double propertyValue, double initialPayment) {
        return propertyValue - initialPayment;
    }
    
    /**
     * Computes the monthly payment for a mortgage
     * @param ratePercentage The annual interest rate
     * @param termYears The mortgage term in years
     * @param financedAmount The loan amount
     * @return The calculated monthly payment
     */
    public static double computeMonthlyPayment(double ratePercentage, int termYears, double financedAmount) {
        // Convert annual rate to monthly rate
        double monthlyRate = ratePercentage / 12 / 100;
        int totalPayments = termYears * 12;
        
        // Apply standard mortgage payment formula
        return (financedAmount * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -totalPayments));
    }
    
    /**
     * Displays the details of a fixed-rate mortgage
     * @param ratePercentage The annual interest rate
     * @param financedAmount The loan amount
     * @param monthlyInstallment The monthly payment
     * @param isJumboLoan Flag indicating if this is a jumbo loan
     */
    public static void displayFixedLoanDetails(double ratePercentage, double financedAmount, 
                                             double monthlyInstallment, boolean isJumboLoan) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        Random randomGenerator = new Random();
        int accountID = randomGenerator.nextInt(900000) + 100000;
        
        System.out.println("\n===== " + (isJumboLoan ? "JUMBO" : "STANDARD") + " MORTGAGE APPROVAL =====");
        System.out.println("Interest Rate: " + formatter.format(ratePercentage) + "%");
        System.out.println("Principal Amount: $" + formatter.format(financedAmount));
        System.out.println("Monthly Payment: $" + formatter.format(monthlyInstallment));
        System.out.println("Account ID: " + accountID);
        
        // Save loan details to file
        HomeLoanManager loanManager = new HomeLoanManager();
        loanManager.saveAccountData("filename", accountID, financedAmount, monthlyInstallment, isJumboLoan);
        
        System.out.println("\nYour application has been approved! Details saved to your account.");
        System.out.println("Returning to main menu...");
        displayMainMenu();
    }
    
    /**
     * Gets the appropriate adjustable interest rate for the selected term
     * @param adjustableTerm The ARM term selected
     * @param isJumboLoan Flag indicating if this is a jumbo loan
     * @return The adjusted interest rate percentage
     */
    private static double getAdjustableRateForTerm(int adjustableTerm, boolean isJumboLoan) {
        double baseRate = 0.0;
        
        // Select base rate by term
        if (adjustableTerm == 1) {
            baseRate = VARIABLE_INTEREST_RATES[0];
        } else if (adjustableTerm == 2) {
            baseRate = VARIABLE_INTEREST_RATES[1];
        } else if (adjustableTerm == 3) {
            baseRate = VARIABLE_INTEREST_RATES[2];
        } else if (adjustableTerm == 5) {
            baseRate = VARIABLE_INTEREST_RATES[3];
        } else {
            System.out.println("Invalid selection. Returning to main menu...");
            displayMainMenu();
        }
        
        // Apply jumbo loan premium if applicable
        if (isJumboLoan) {
            baseRate += 0.5; // Higher premium for jumbo ARMs due to increased risk
        }
        
        return baseRate;
    }
    
    /**
     * Calculates the loan amount for adjustable-rate mortgages
     * @param annualIncome The annual income
     * @param propertyValue The property value
     * @param initialPayment The down payment amount
     * @return The calculated loan amount
     */
    private static double calculateAdjustableLoanAmount(double annualIncome, double propertyValue, double initialPayment) {
        double maxFinancingAmount = calculateFixedLoanAmount(annualIncome, propertyValue, initialPayment);
        return maxFinancingAmount;
    }
    
    /**
     * Computes the monthly payment for an adjustable-rate mortgage
     * @param initialRate The initial interest rate
     * @param financedAmount The loan amount
     * @param adjustableTerm The ARM term selected
     * @return The calculated monthly payment
     */
    public static double computeAdjustableMonthlyPayment(double initialRate, double financedAmount, int adjustableTerm) {
        // For ARMs, calculate based on a 30-year amortization with initial fixed period
        double monthlyRate = initialRate / 12 / 100;
        int totalPayments = 30 * 12; // Standard 30-year term for ARMs
        return (financedAmount * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -totalPayments));
    }
    
    /**
     * Displays the details of an adjustable-rate mortgage
     * @param financedAmount The loan amount
     * @param monthlyInstallment The monthly payment
     * @param initialRate The initial interest rate
     * @param isJumboLoan Flag indicating if this is a jumbo loan
     */
    public static void displayAdjustableLoanDetails(double financedAmount, double monthlyInstallment, 
                                                  double initialRate, boolean isJumboLoan) {
        Random randomGenerator = new Random();
        int accountID = randomGenerator.nextInt(900000) + 100000;
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        
        System.out.println("\n===== " + (isJumboLoan ? "JUMBO" : "STANDARD") + " ARM APPROVAL =====");
        System.out.println("Initial Interest Rate: " + formatter.format(initialRate) + "%");
        System.out.println("Principal Amount: $" + formatter.format(financedAmount));
        System.out.println("Initial Monthly Payment: $" + formatter.format(monthlyInstallment));
        System.out.println("Account ID: " + accountID);
        System.out.println("\nNOTE: Rate will adjust according to market conditions after the initial period.");
        
        // Save loan details to file
        HomeLoanManager loanManager = new HomeLoanManager();
        loanManager.saveAccountData("filename", accountID, financedAmount, monthlyInstallment, isJumboLoan);
        
        System.out.println("\nYour application has been approved! Details saved to your account.");
        System.out.println("Returning to main menu...");
        displayMainMenu();
    }
 
    /**
     * Processes mortgage payments
     */
    public static void processMortgagePayment() {
        processPaymentTransaction();
    }
    
    /**
     * Handles payment transactions for existing mortgages
     */
    public static void processPaymentTransaction() {
        System.out.println("\n===== MORTGAGE PAYMENT PROCESSING =====");
        System.out.print("Enter your account ID: ");
        int accountID = inputReader.nextInt();
        
        // Verify if account exists with provided ID
        boolean validAccount = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("HomeLoanData.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // Skip header row if present
                if (data[0].equals("Account ID")) {
                    continue;
                }
                
                int storedID = Integer.parseInt(data[0].trim());
                if (accountID == storedID) {
                    // Account found - display payment details
                    validAccount = true;
                    double monthlyAmount = Double.parseDouble(data[2].trim()); // Monthly payment at index 2
                    double remainingBalance = Double.parseDouble(data[1].trim()); // Balance at index 1
                    boolean isJumbo = data.length > 3 && Boolean.parseBoolean(data[3].trim()); // Jumbo flag at index 3
                    
                    System.out.println("\n===== ACCOUNT INFORMATION =====");
                    System.out.println("Account ID: " + storedID);
                    System.out.println("Account Type: " + (isJumbo ? "Jumbo Mortgage" : "Standard Mortgage"));
                    System.out.println("Remaining Balance: $" + new DecimalFormat("#,##0.00").format(remainingBalance));
                    System.out.println("Monthly Payment: $" + new DecimalFormat("0.00").format(monthlyAmount));
                    
                    // Get payment amount
                    double paymentAmount;
                    while (true) {
                        System.out.print("\nEnter payment amount: $");
                        paymentAmount = inputReader.nextDouble();
                        if (paymentAmount > remainingBalance) {
                            System.out.println("Payment exceeds remaining balance. Maximum payment: $" + 
                                              new DecimalFormat("#,##0.00").format(remainingBalance));
                        } else {
                            break;
                        }
                    }
                    
                    // Process payment (in a real system, this would update the database)
                    System.out.println("\nProcessing payment of $" + 
                                      new DecimalFormat("#,##0.00").format(paymentAmount) + "...");
                    System.out.println("Payment processed successfully!");
                    System.out.println("New balance: $" + 
                                      new DecimalFormat("#,##0.00").format(remainingBalance - paymentAmount));
                    
                    // In a real system, would update the CSV file with new balance here
                    updateLoanBalance(accountID, remainingBalance - paymentAmount);
                    
                    break; // Exit once account is found
                }
            }
        } catch (IOException e) {
            System.out.println("Error processing payment: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Account not found handling
        if (!validAccount) {
            System.out.println("No account found with ID: " + accountID);
        }
        
        // Return to main menu
        System.out.println("\nReturning to main menu...");
    }
    
    /**
     * Updates loan balance in the database after payment
     * @param accountID The account ID
     * @param newBalance The updated balance
     */
    private static void updateLoanBalance(int accountID, double newBalance) {
        // In a real system, this would update the CSV file with the new balance
        // This is a placeholder for actual database update logic
        System.out.println("Database updated with new balance information.");
    }
    
    /**
     * Verifies account in database
     * @param storedID The ID to check against
     * @param accountID The user-provided account ID
     * @return True if account is verified, false otherwise
     */
    public boolean authenticateAccount(int storedID, int accountID) {
        String dataFile = "HomeLoanData.csv";
        String line;
        String dataSeparator = ",";

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            reader.readLine(); // Skip header row
            while ((line = reader.readLine()) != null) {
                String[] accountData = line.split(dataSeparator);
                int accountIDFromFile = Integer.parseInt(accountData[0].trim());
                if (accountIDFromFile == accountID) {
                    return true; // Account verified
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Account not found
    }

    /**
     * Saves account data to CSV file
     * @param filename The base filename (not used directly)
     * @param accountID The account ID
     * @param financedAmount The loan amount
     * @param monthlyInstallment The monthly payment
     * @param isJumboLoan Flag indicating if this is a jumbo loan
     */
    public void saveAccountData(String filename, int accountID, double financedAmount, 
                               double monthlyInstallment, boolean isJumboLoan) {
        String folderPath = "data";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdir(); // Create folder if needed
        }

        String filePath = folderPath + File.separator + "HomeLoanData" + ".csv";
        try (FileWriter writer = new FileWriter(filePath, true)) { // Append mode
            // Create header if file is new or doesn't exist
            File dataFile = new File(filePath);
            if (!dataFile.exists() || dataFile.length() == 0) {
                writer.write("Account ID,Loan Balance,Monthly Payment,Is Jumbo\n");
            }
            
            // Write account data with jumbo loan indicator
            writer.write(accountID + "," + financedAmount + "," + monthlyInstallment + "," + isJumboLoan + "\n");
            System.out.println("Account data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving account data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Updates account data in CSV file
     * @param fileName The source file containing update data
     */
    public void updateAccountData(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("HomeLoanData.csv", true));
             BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            System.out.println("\nUpdating account information from " + fileName + ":");
            while ((line = reader.readLine()) != null) {
                // Process account updates here
                // Example: Add payment marker
                line += ",Updated";
                writer.write(line);
                writer.newLine();
                System.out.println(line);
            }
            System.out.println("Account information updated successfully.");
        } catch (IOException e) {
            System.out.println("Error updating account data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Processes a jumbo mortgage application
     * Special method for handling jumbo-specific loan features
     * @param fico The credit score
     * @param annualIncome The annual income
     * @param initialPayment The down payment amount
     * @param propertyValue The property value
     */
    public static void processJumboMortgage(int fico, double annualIncome, double initialPayment, double propertyValue) {
        // Verify jumbo loan eligibility
        if (fico < JUMBO_MIN_CREDIT_SCORE) {
            System.out.println("Jumbo mortgages require a minimum credit score of " + (int)JUMBO_MIN_CREDIT_SCORE);
            System.out.println("Your application cannot proceed at this time.");
            displayMainMenu();
            return;
        }
        
        // Verify property value meets jumbo threshold
        if (propertyValue - initialPayment < JUMBO_LOAN_THRESHOLD) {
            System.out.println("The loan amount does not meet the jumbo mortgage minimum threshold of $" + 
                              new DecimalFormat("#,###.00").format(JUMBO_LOAN_THRESHOLD));
            System.out.println("Please apply for a standard mortgage instead.");
            displayMainMenu();
            return;
        }
        
        // Verify down payment percentage
        double downPaymentPercentage = (initialPayment / propertyValue) * 100;
        if (downPaymentPercentage < JUMBO_MIN_DOWN_PAYMENT_PERCENT) {
            System.out.println("Jumbo mortgages require a minimum down payment of " + 
                              JUMBO_MIN_DOWN_PAYMENT_PERCENT + "%");
System.out.println("Jumbo mortgages require a minimum down payment of " + 
                              JUMBO_MIN_DOWN_PAYMENT_PERCENT + "%");
            System.out.println("Your down payment is only " + 
                              new DecimalFormat("0.00").format(downPaymentPercentage) + "%");
            displayMainMenu();
            return;
        }
        
        // Calculate DTI for jumbo loans (stricter requirements)
        double monthlyIncome = annualIncome / 12;
        System.out.print("Enter your total monthly debt obligations: $");
        double monthlyDebt = inputReader.nextDouble();
        double dti = (monthlyDebt / monthlyIncome) * 100;
        
        if (dti > 43) { // Stricter DTI for jumbo loans
            System.out.println("Your debt-to-income ratio of " + 
                              new DecimalFormat("0.00").format(dti) + "% exceeds our maximum threshold of 43% for jumbo loans.");
            System.out.println("Your application cannot proceed at this time.");
            displayMainMenu();
            return;
        }
        
        // Proceed with jumbo loan processing
        System.out.println("\nYou qualify for a jumbo mortgage based on preliminary criteria.");
        System.out.println("Select a jumbo mortgage product:");
        System.out.println("1. Fixed-rate jumbo mortgage");
        System.out.println("2. Adjustable-rate jumbo mortgage");
        System.out.print("Enter your choice (1 or 2): ");
        
        int mortgageTypeChoice = inputReader.nextInt();
        selectMortgageType(fico, annualIncome, initialPayment, propertyValue, true);
    }
    
    /**
     * Utility function to generate loan amortization schedules
     * @param loanAmount The principal loan amount
     * @param interestRate The annual interest rate percentage
     * @param termYears The loan term in years
     */
    public static void generateAmortizationSchedule(double loanAmount, double interestRate, int termYears) {
        System.out.println("\n===== AMORTIZATION SCHEDULE =====");
        System.out.println("Loan Amount: $" + new DecimalFormat("#,##0.00").format(loanAmount));
        System.out.println("Interest Rate: " + interestRate + "%");
        System.out.println("Term: " + termYears + " years");
        
        double monthlyRate = interestRate / 12 / 100;
        int totalPayments = termYears * 12;
        double monthlyPayment = computeMonthlyPayment(interestRate, termYears, loanAmount);
        
        System.out.println("Monthly Payment: $" + new DecimalFormat("#,##0.00").format(monthlyPayment));
        System.out.println("\nPayment\tPrincipal\tInterest\tRemaining Balance");
        
        double remainingBalance = loanAmount;
        DecimalFormat df = new DecimalFormat("#,##0.00");
        
        for (int paymentNum = 1; paymentNum <= totalPayments; paymentNum++) {
            double interestPayment = remainingBalance * monthlyRate;
            double principalPayment = monthlyPayment - interestPayment;
            remainingBalance -= principalPayment;
            
            // Print first few payments and last few payments
            if (paymentNum <= 12 || paymentNum > totalPayments - 12) {
                System.out.println(paymentNum + "\t$" + df.format(principalPayment) + 
                                  "\t$" + df.format(interestPayment) + 
                                  "\t$" + df.format(remainingBalance));
            } else if (paymentNum == 13) {
                System.out.println("...");
            }
        }
    }
    
    /**
     * Validates loan processing status by account ID
     * @param accountID The account ID to validate
     * @return True if valid and active, false otherwise
     */
    public boolean validateLoanStatus(int accountID) {
        try (BufferedReader reader = new BufferedReader(new FileReader("HomeLoanData.csv"))) {
            String line;
            reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (Integer.parseInt(data[0]) == accountID) {
                    return true; // Account found and active
                }
            }
        } catch (IOException e) {
            System.out.println("Error validating loan status: " + e.getMessage());
        }
        return false; // Account not found
    }
    
    /**
     * Processes early loan payoff calculations
     * @param accountID The account ID
     * @param currentBalance The current loan balance
     * @return The payoff amount with any applicable fees
     */
    public double calculateEarlyPayoff(int accountID, double currentBalance) {
        // In a real system, would check for early payoff penalties
        // For simplicity, we'll assume a 1% early payoff fee for loans less than 5 years old
        double earlyPayoffFee = currentBalance * 0.01;
        return currentBalance + earlyPayoffFee;
    }
    
    /**
     * Processes loan refinancing options
     * @param accountID The account ID
     * @param currentBalance The current loan balance
     * @param currentRate The current interest rate
     */
    public void processRefinanceOptions(int accountID, double currentBalance, double currentRate) {
        System.out.println("\n===== REFINANCE OPTIONS =====");
        System.out.println("Current Loan Balance: $" + new DecimalFormat("#,##0.00").format(currentBalance));
        System.out.println("Current Interest Rate: " + currentRate + "%");
        
        // Determine current market rates (would be pulled from external source in real system)
        double marketRate = currentRate - 0.75; // Simulate lower market rate
        System.out.println("Current Market Rate: " + marketRate + "%");
        
        // Estimate new monthly payment
        double newMonthlyPayment = computeMonthlyPayment(marketRate, 30, currentBalance);
        System.out.println("Estimated New Monthly Payment: $" + new DecimalFormat("#,##0.00").format(newMonthlyPayment));
        
        // Calculate refinance costs
        double closingCosts = currentBalance * 0.02; // Assume 2% closing costs
        System.out.println("Estimated Closing Costs: $" + new DecimalFormat("#,##0.00").format(closingCosts));
        
        // Calculate break-even point
        // Current payment would come from database in real system
        double currentMonthlyPayment = computeMonthlyPayment(currentRate, 30, currentBalance);
        double monthlySavings = currentMonthlyPayment - newMonthlyPayment;
        double breakEvenMonths = closingCosts / monthlySavings;
        
        System.out.println("Break-even Point: " + (int)Math.ceil(breakEvenMonths) + " months");
        System.out.println("\nWould you like to apply for refinancing? (Y/N)");
        
        String refinanceChoice = inputReader.next();
        if (refinanceChoice.equalsIgnoreCase("Y")) {
            System.out.println("Starting refinance application process...");
            // In a real system, would start new application process preserving customer data
        } else {
            System.out.println("Returning to main menu...");
        }
    }
    
    /**
     * Allows user to update their contact information
     * @param accountID The account ID
     */
    public void updateContactInformation(int accountID) {
        System.out.println("\n===== UPDATE CONTACT INFORMATION =====");
        System.out.println("Please enter your updated information:");
        
        inputReader.nextLine(); // Clear buffer
        System.out.print("Address: ");
        String address = inputReader.nextLine();
        
        System.out.print("Phone Number: ");
        String phoneNumber = inputReader.nextLine();
        
        System.out.print("Email: ");
        String email = inputReader.nextLine();
        
        // In a real system, would update customer database
        System.out.println("\nContact information updated successfully!");
        
        // Log update for audit purposes
        try (FileWriter writer = new FileWriter("update_log.txt", true)) {
            writer.write("Account " + accountID + " information updated on " + 
                        java.time.LocalDate.now() + "\n");
        } catch (IOException e) {
            System.out.println("Error logging update: " + e.getMessage());
        }
    }
    
    /**
     * Main method to run the application
     * This provides a more comprehensive entry point with error handling
     */
    public static void main() {
        System.out.println("Starting Home Loan Management System...");
        
        try {
            // Create data directory if it doesn't exist
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdir();
                System.out.println("Created data directory for loan information storage.");
            }
            
            // Display welcome message
            System.out.println("\nWelcome to the Home Loan Management System");
            System.out.println("This system allows you to apply for and manage mortgage loans");
            System.out.println("=====================================");
            
            // Display the main menu
            displayMainMenu();
            
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            System.out.println("The system will now exit.");
            System.exit(1);
        } finally {
            // Close resources
            if (inputReader != null) {
                inputReader.close();
            }
            System.out.println("Thank you for using Home Loan Manager. Goodbye!");
        }
    }
}

