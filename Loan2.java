import java.text.DecimalFormat;
import java.io.BufferedReader; 
import java.io.FileReader; 
import java.io.IOException; 

public class Loan2 {
    protected String borrowerName; 
    protected double loanAmount; 
    protected double interestRate; 
    protected int durationInYears; 

    public Loan2(String borrowerName, double loanAmount, double interestRate, int durationInYears) {
        this.borrowerName = borrowerName; 
        this.loanAmount = loanAmount; 
        this.interestRate = interestRate; 
        this.durationInYears = durationInYears; 
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getDurationInYears() {
        return durationInYears;
    }

    public void setDurationInYears(int durationInYears) {
        this.durationInYears = durationInYears;
    }

    public double calculateTotalRepayment() {
        return loanAmount + (loanAmount * interestRate * durationInYears / 100);
    }

    public void displayLoanDetails() {
        DecimalFormat df = new DecimalFormat("#,###.00"); 
        System.out.println("Borrower: " + borrowerName);
        System.out.println("Loan Amount: $" + df.format(loanAmount));
        System.out.println("Interest Rate: " + interestRate + "%");
        System.out.println("Duration: " + durationInYears + " years");
        System.out.println("Total Repayment: $" + df.format(calculateTotalRepayment()));
    }
}

class MedicalLoan extends Loan2 {
    public MedicalLoan(String borrowerName, double loanAmount, int durationInYears) {
        super(borrowerName, loanAmount, 5.0, durationInYears); // Fixed 5% interest rate
    }
}

class BusinessLoan extends Loan2 {
    public BusinessLoan(String borrowerName, double loanAmount, int durationInYears) {
        super(borrowerName, loanAmount, 7.0, durationInYears); // Fixed 7% interest rate
    }
}
