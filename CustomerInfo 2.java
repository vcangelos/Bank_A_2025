import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList; 

class BankProject {
    private ArrayList<String> checklist;
    
    public class BankProject() {
        checklist = new ArrayList<>();
        initializeChecklist();
    }
    
    //Constructor 
    public bankProject() {
        checklist = new ArrayList<>();
    }
    
        
    }
    // Setter Method 
    public void setChecklist(String item) {
        if (item != null && !item.trim().isEmpty()) {
        checklist.add(item);
    } 
    else {
        System.out.println("Invalid item. Cannot be null or empty.");
    }
    } 
    
   //Getter Method 
    public ArrayList<String> getChecklist() {
        return checklist;
    }
    
    //Display Method 
    public void displayChecklist() {
        if (checklist.isEmpty()) {
            System.out.println("Checklist is empty.");
        }
        else {
            
        for (int i = 0; i < checklist.size(); i++) {
            System.out.println((i + 1) + "-" + checklist.get(i));
        }
    }
    }
    
    //Override toString()method
    @Override 
    public StringtoString() {
        return "BankProject Checklist:" +checlist.toString();
    }
    
    public static void main(String[] args) {
        BankProject bankProject = new BankProject();
        
        // Example: Adding a new item 
        bankProject.addChecklistItem("New Feature - Display Transaction History");
        
        // Display the updated checklist
        System.out.println("\nUpdated Checklist:");
        bankProject.displayChecklist();
    }
}

//Print the object using overridden toString()
System.out.println("\nBankProject Details:" + bankProject);

// Example usage of CreditCard class 
CreditCard myCard = new 
CreditCard("1234-5678-9876-5432);
System.out.println("\nCreditCrad Details:" + myCard);
}
}

// CreditCard Class 
class CreditCard {
    private String cardNumber;
    private String cardType;
     private String expirationDate;
    private double balance;
    private double creditLimit;
    private double outstandingBalance;
    private int creditScore;
    
}

// Constructor 
public CreditCard(string cardNumber, String cardType, String expirationDate, double creditLimit) {
    this.cardType = cardType;
    this.expirationDate = expirationDate;
    this.creditLimit =  creditLimit;
    this.outstandingBalance = 0;
    this.creditScore = 700;    // Default credit score 
} 

// Override toString() method 
@Override 
public String toString() {
return "CreditCard[Card Type:" +cradType + ", Card Number:" + maskCardNumber() + ", Expiration Date:" + expirationDate + ", Credit Limit:$" + creditLimit + ", outstandingBalance + ", Credit Score: " +creditScore + "]";   
}

// Method to mask part of the card number for security 
private String maskCardNumber() {
    return "****_****_****_" +
    cardNumber.substring(cardNumber.length() - 4);
}

// Getters for CreditCard properties 
public String getCardNumber() {
    return cardNumber;
}

public String getCardType() {
    return cardType;
}

public String getExpirationDate () {
    return expirationDate;
}

public double getCreditLimit() {
    return creditLimit;
}

public double getOutstandingBalance() {
    return outstandingBlance;
}

public int getCreditScore() {
    return outstandingBalance;
}

public int getCreditScore() {
    return creditScore; 
}
}