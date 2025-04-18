import java.text.DecimalFormat; 
import java.io.BufferedReader; 
import java.io.FileReader; 
import java.io.IOException; 
import java.io.BufferedWriter; 
import java.io.FileWriter; 
import java.util.ArrayList; 
import java.util.List; 

public class Loan {
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
    
    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

public String getBorrowerName() {
        return borrowerName;
    }
    
      public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

public double getInterestRate() {
        return interestRate;
    }
    
    public void setDurationInYears(int durationInYears) {
        this.durationInYears = durationInYears;
    }
    
    public int getDurationInYears() {
        return durationInYears;
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

class MedicalLoan extends Loan {
    public MedicalLoan(String borrowerName, double loanAmount, int durationInYears) {
        super(borrowerName, loanAmount, 5.0, durationInYears); // Fixed 5% interest rate
    }
}


class BusinessLoan extends Loan {
    public BusinessLoan(String borrowerName, double loanAmount, int durationInYears) {
        super(borrowerName, loanAmount, 7.0, durationInYears); // Fixed 7% interest rate
    }
}

class BankProject {
    private List<String> checklist;

    public BankProject() {
        checklist = new ArrayList<>();
    }

    public void addChecklistItem(String item) {
        checklist.add(item);
    }

    public List<String> getChecklist() {
        return checklist;
    }

    public void displayChecklist() {
        for (String item : checklist) {
            System.out.println("- " + item);
        }
    }

    // Override toString() method
    @Override
    public String toString() {
        return "BankProject Checklist: " + checklist.toString();
    }
}

// CreditCard class
class CreditCard {
    private String cardNumber;
    private String cardType;
    private String expirationDate;
    private double creditLimit;
    private double outstandingBalance;
    private double creditScore;

    public CreditCard(String cardNumber, String cardType, String expirationDate, double creditLimit) {
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.expirationDate = expirationDate;
        this.creditLimit = creditLimit;
        this.outstandingBalance = 0.0; 
        this.creditScore = 700; 
    }

    public String getCardType() {
        return cardType;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public double getOutstandingBalance() {
        return outstandingBalance;
    }

    public double getCreditScore() {
        return creditScore;
    }

    public String maskCardNumber() {
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }

    @Override
    public String toString() {
        return "CreditCard [Card Type=" + cardType + ", Card Number=" + maskCardNumber() + 
               ", Expiration Date=" + expirationDate + ", Credit Limit=" + creditLimit + 
               ", Outstanding Balance=" + outstandingBalance + ", Credit Score=" + creditScore + "]";
    }
}

// Debit Card 
class BankAccount {
    private double balance;

    public BankAccount(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
        System.out.printf("Deposited: $%.2f. New balance: $%.2f%n", amount, balance);
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds.");
        }
        balance -= amount;
        System.out.printf("Withdrew: $%.2f. New balance: $%.2f%n", amount, balance);
    }

    public double getBalance() {
        return balance;
    }
}
