import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class BankProject {

    private ArrayList<String> checklist;

    // Constructor

    public BankProject() {
        checklist = new ArrayList<>();
    }

    // Method to add an item to the checklist

    public void addChecklistItem(String item) {
        if (item != null && !item.trim().isEmpty()) {
            checklist.add(item);
        } else {
            System.out.println("Invalid item. Cannot be null or empty.");
        }
    }

    // Getter Method
    
    public ArrayList<String> getChecklist() {
      return checklist;
    }

    // Display Method

    public void displayChecklist() {
        if (checklist.isEmpty()) {
            System.out.println("Checklist is empty.");
        } else {
            for (int i = 0; i < checklist.size(); i++) {
                System.out.println((i + 1) + "-" + checklist.get(i));
            }
        }
    }

    // Override toString() method

    @Override
    public String toString() {
        return "BankProject Checklist: " + checklist.toString();
    }
    public static void main(String[] args) {
      BankProject bankProject = new BankProject();

        // Example: Adding a new item

        bankProject.addChecklistItem("New Feature - Display Transaction History");

        // Display the updated checklist

        System.out.println("\nUpdated Checklist:");
        bankProject.displayChecklist();

        // Print the object using overridden toString()

        System.out.println("\nBankProject Details: " + bankProject);

        // Example usage of CreditCard class

        CreditCard myCard = new CreditCard("1234-5678-9876-5432", "Visa", "12/25", 5000.0);
        System.out.println("\nCreditCard Details: " + myCard);

        // Create CSV file

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bank_project.csv"))) {

            // Write BankProject Checklist

            writer.write("\"BankProject Checklist\"\n");
            writer.write("\"Item Number\",\"Checklist Item\"\n");
            for (int i = 0; i < bankProject.getChecklist().size(); i++) {
                writer.write("\"" + (i + 1) + "\",\"" + bankProject.getChecklist().get(i) + "\"\n");
            }

            // Write CreditCard Details

            writer.write("\n\"CreditCard Details\"\n");
            writer.write("\"Card Type\",\"Card Number\",\"Expiration Date\",\"Credit Limit\",\"Outstanding Balance\",\"Credit Score\"\n");
            writer.write("\"" + myCard.getCardType() + "\",\"" + myCard.maskCardNumber() + "\",\"" + myCard.getExpirationDate() + "\",\"" + myCard.getCreditLimit() + "\",\"" + myCard.getOutstandingBalance() + "\",\"" + myCard.getCreditScore() + "\"\n");
            System.out.println("CSV file created successfully.");
        } 
            catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// CreditCard Class

class CreditCard {
 private String cardNumber;
    private String cardType;
    private String expirationDate;
    private double creditLimit;
    private double outstandingBalance;
    private int creditScore;

    // Constructor

    public CreditCard(String cardNumber, String cardType, String expirationDate, double creditLimit) {
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.expirationDate = expirationDate;
        this.creditLimit = creditLimit;
        this.outstandingBalance = 0; // Default value
        this.creditScore = 700; // Default credit score
    }

    // Method to mask part of the card number for security

    public String maskCardNumber() {
        return "****_****_****_" + cardNumber.substring(cardNumber.length() - 4);
    }

    // Override toString() method

    @Override
    public String toString() {
        return "CreditCard[Card Type: " + cardType + ", Card Number: " + maskCardNumber() + ", Expiration Date: " + expirationDate + ", Credit Limit: $" + creditLimit + ", Outstanding Balance: $" + outstandingBalance + ", Credit Score: " + creditScore + "]";
    }

    // Getters for CreditCard properties

    public String getCardNumber() {
        return cardNumber;
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
    public int getCreditScore() {
        return creditScore;
    }
} 
