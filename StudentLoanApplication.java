import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;

import java.util.*;

class StudentLoanApplication {
    private static List<StudentLoanApplication> applications = new ArrayList<>();

    private int uniqueID;
    private int studentID;
    private String name;
    private String school;
    private double loanAmount;
    private double annualIncome;
    private int loanPeriod;
    private static final double INTEREST_RATE = 5.50;
    private Date time_purchased;
    private static Calendar calendar = Calendar.getInstance();

    // Constructor with applicationID provided externally
    public StudentLoanApplication(int uniqueID, int studentID, String name, String school, double loanAmount, double annualIncome, int loanPeriod, Date time_purchased) {
        this.uniqueID = uniqueID;  // Assign unique ID (index 0 from CSV)
        this.studentID = studentID;  // Assign student ID (index 1 from CSV)
        this.name = name;
        this.school = school;
        this.loanAmount = loanAmount;
        this.annualIncome = annualIncome;
        this.loanPeriod = loanPeriod;
        this.time_purchased = time_purchased;
    }


    public static boolean userExists(int studentID) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/StudentLoan.csv"))) {
            String line = reader.readLine(); // skip header row, if present
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 1) {
                    try {
                        int existingID = Integer.parseInt(values[1].trim()); // values[1] = studentID
                        if (existingID == studentID) {
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid line
                        System.out.println("Skipping invalid line (bad ID): " + Arrays.toString(values));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error checking existing users: " + e.getMessage());
        }
        return false;
    }




    public double calculateTotalRepayment() {
        return loanAmount * Math.pow(1 + (INTEREST_RATE / 100), loanPeriod);
    }

    public void displayApplicationDetails() {
        System.out.println("\n=== Student Loan Application Summary ===");
        System.out.println("Application ID: " + uniqueID);
        System.out.println("Applicant Name: " + name);
        System.out.println("School: " + school);
        System.out.println("Loan Amount Requested: $" + loanAmount);
        System.out.println("Annual Income: $" + annualIncome);
        System.out.println("Loan Period: " + loanPeriod + " years");
        System.out.println("Time Remaining on Loan: " + getRemainingTime());
        System.out.println("Interest Rate: " + INTEREST_RATE + "%");
        System.out.println("Total Repayment Amount: $" + String.format("%.2f", calculateTotalRepayment()));
        System.out.println("Application Status: Submitted");
    }

    public String getRemainingTime() {
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(time_purchased);
        endDate.add(Calendar.YEAR, loanPeriod);

        Date currentDate = new Date();
        long timeDiff = endDate.getTimeInMillis() - currentDate.getTime();

        if (timeDiff <= 0) {
            return "Loan period completed.";
        }

        long daysLeft = timeDiff / (1000 * 60 * 60 * 24);
        long years = daysLeft / 365;
        long months = (daysLeft % 365) / 30;
        long days = (daysLeft % 365) % 30;

        return years + " years, " + months + " months, and " + days + " days remaining";
    }

    public String toCSVRow() {
        return uniqueID + "," + studentID + "," + name + "," + school + "," +
                loanAmount + "," + annualIncome + "," + loanPeriod + "," +
                INTEREST_RATE + "," + time_purchased + "," + String.format("%.2f", calculateTotalRepayment());
    }

    public static void writeToFile(StudentLoanApplication application) {
        try (FileWriter writer = new FileWriter("src/StudentLoan.csv", true)) {
            writer.write(application.toCSVRow() + "\n");
            System.out.println("Application saved to StudentLoan.csv.");
        } catch (Exception e) {
            System.out.println("Error saving application: " + e.getMessage());
        }
    }

    public static List<StudentLoanApplication> getApplications() {
        return applications;
    }

    // Optional: remove all applications (for testing/reset)
    public static void clearApplications() {
        applications.clear();
    }

    // --- Optional main method for demonstration ---
    public static StudentLoanApplication getExistingApplication(int studentID) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/StudentLoan.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 8) {
                    try {
                        // Assuming uniqueID is at index 0 in CSV row
                        int uniqueID = Integer.parseInt(values[0].trim()); // Now uniqueID is at index 0
                        int existingID = Integer.parseInt(values[1].trim()); // Assuming studentID is at index 1

                        if (existingID == studentID) {
                            // Parse values from CSV row
                            String name = values[2].trim();
                            String school = values[3].trim();
                            double loanAmount = Double.parseDouble(values[4].trim());
                            double annualIncome = Double.parseDouble(values[5].trim());
                            int loanPeriod = Integer.parseInt(values[6].trim());
                            Date timePurchased;
                            try {
                                timePurchased = new Date(values[7].trim());  // Assuming time_purchased is at index 7
                            } catch (Exception e) {
                                timePurchased = new Date();  // fallback to current date if parsing fails
                            }

                            // Now we can create the StudentLoanApplication object
                            return new StudentLoanApplication(uniqueID, existingID, name, school, loanAmount, annualIncome, loanPeriod, timePurchased);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid line (bad ID): " + Arrays.toString(values));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading existing application: " + e.getMessage());
        }
        return null;
    }


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Student ID: ");
        int studentID = scanner.nextInt();
        scanner.nextLine();

        if (userExists(studentID)) {
            System.out.println("A user with this Student ID has already submitted an application.");
            scanner.close();

            return;
        }

        System.out.print("Enter Application ID: ");
        int applicationID = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter your full name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your school name: ");
        String school = scanner.nextLine();

        System.out.print("Enter the loan amount you need: ");
        double loanAmount = scanner.nextDouble();

        System.out.print("Enter your annual income: ");
        double annualIncome = scanner.nextDouble();

        System.out.print("Enter your loan period (in years): ");
        int loanPeriod = scanner.nextInt();

        Date time_purchased = calendar.getTime();

        StudentLoanApplication application = new StudentLoanApplication(
                applicationID, studentID, name, school,
                loanAmount, annualIncome, loanPeriod, time_purchased
        );

        application.displayApplicationDetails();
        writeToFile(application);

        scanner.close();
    }
}
