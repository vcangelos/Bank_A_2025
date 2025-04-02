import java.util.Scanner;

class StudentLoanApplication {
    private String name;
    private String school;
    private double loanAmount;
    private double annualIncome;
    private int loanPeriod;
    private static final double INTEREST_RATE = 5.50;

    // Constructor
    public StudentLoanApplication(String name, String school, double loanAmount, double annualIncome, int loanPeriod) {
        this.name = name;
        this.school = school;
        this.loanAmount = loanAmount;
        this.annualIncome = annualIncome;
        this.loanPeriod = loanPeriod;
    }

    // Calculate total repayment amount
    public double calculateTotalRepayment() {
        return loanAmount * Math.pow(1 + (INTEREST_RATE / 100), loanPeriod);
    }

    // Display Application Summary
    public void displayApplicationDetails() {
        System.out.println("\n=== Student Loan Application Summary ===");
        System.out.println("Applicant Name: " + name);
        System.out.println("School: " + school);
        System.out.println("Loan Amount Requested: $" + loanAmount);
        System.out.println("Annual Income: $" + annualIncome);
        System.out.println("Loan Period: " + loanPeriod + " years");
        System.out.println("Interest Rate: " + INTEREST_RATE + "%");
        System.out.println("Total Repayment Amount: $" + String.format("%.2f", calculateTotalRepayment()));
        System.out.println("Application Status: Submitted");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your full name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your school name: ");
        String school = scanner.nextLine();

        System.out.print("Enter the loan amount you need (in Dollars & the intrest rate will be %5.5): ");
        double loanAmount = scanner.nextDouble();

        System.out.print("Enter your annual income: ");
        double annualIncome = scanner.nextDouble();

        System.out.print("Enter your loan period (in years): ");
        int loanPeriod = scanner.nextInt();

        // Creating the application object
        StudentLoanApplication application = new StudentLoanApplication(name, school, loanAmount, annualIncome, loanPeriod);
        application.displayApplicationDetails();

        scanner.close();
    }
}
