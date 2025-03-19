// Anthony P, Gabriela M
import java.io.*;// saves csv
import java.util.*;
class CreditCard {
   private String cardNumber;
   private String cardType;
   private String cvv;
   private String expirationDate;
   private double balance;
   private double creditLimit;
   private double outstandingBalance;
   private int creditScore;
   private double monthlySpending;
   public CreditCard(String cardNumber, String cardType, String cvv, String expirationDate, double creditLimit) {
       this.cardNumber = cardNumber;
       this.cardType = cardType;
       this.cvv = cvv;
       this.expirationDate = expirationDate;
       this.balance = creditLimit;
       this.creditLimit = creditLimit;
       this.outstandingBalance = 0;
       this.creditScore = 700;
       this.monthlySpending = 0;
   }
   public void makePayment(double amount) {
       if (amount < outstandingBalance * 0.05) {
           System.out.println("Payment too low. Minimum 5% of balance required.");
       } else {
           outstandingBalance -= amount;
           balance += amount;
           if (outstandingBalance == 0) {
               creditScore += 10;
           }
           System.out.println("Payment successful: " + amount);
       }
   }
   public void closeAccount() {
       if (outstandingBalance > 0) {
           System.out.println("Cannot close account. Pay outstanding balance first.");
       } else {
           System.out.println("Account closed successfully.");
       }
   }
   public void displayInfo() {
       System.out.println("Card Type: " + cardType);
       System.out.println("Card Number: " + cardNumber);
       System.out.println("Balance: " + balance);
       System.out.println("Outstanding Balance: " + outstandingBalance);
       System.out.println("Credit Score: " + creditScore);
       System.out.println("Monthly Spending: " + monthlySpending);
   }
   public void displayFeatures() {
       System.out.println("Features of Credit Card:");
       System.out.println("1. Alternative to cash");
       System.out.println("2. Credit Limit");
       System.out.println("3. Payment in Domestic and Foreign Currency");
       System.out.println("4. Record keeping of all transactions");
       System.out.println("5. Regular Charges");
       System.out.println("6. Grace Period or Grace Days");
       System.out.println("7. Higher fees on cash withdrawals");
       System.out.println("8. Additional charges for delay in payment");
       System.out.println("9. Service Tax");
       System.out.println("10. Bonus Points");
       System.out.println("11. Gifts and other Offers");
   }
   public static void saveToCSV(List<CreditCard> cards) {
       try (PrintWriter writer = new PrintWriter(new File("customers.csv"))) {
           writer.println("CardNumber,CardType,CVV,ExpirationDate,Balance,CreditLimit,OutstandingBalance,CreditScore,MonthlySpending");
           for (CreditCard card : cards) {
               writer.println(card.cardNumber + "," + card.cardType + "," + card.cvv + "," + card.expirationDate + "," +
                       card.balance + "," + card.creditLimit + "," + card.outstandingBalance + "," + card.creditScore + "," + card.monthlySpending);
           }// chat gpt helped us with this
           System.out.println("Data saved to CSV.");
       } catch (FileNotFoundException e) {
           System.out.println("Error saving data: " + e.getMessage());
       }
   }
}
public class BankSystem {
   public static void main(String[] args) {
       List<CreditCard> cards = new ArrayList<>();
       Scanner scanner = new Scanner(System.in);//menu system
       while (true) {
           System.out.println("1. Open Credit Card\n2. Make Payment\n3. Display Info\n4. Display Features\n5. Close Account\n6. Save & Exit");
           int choice = scanner.nextInt();
           scanner.nextLine();
           if (choice == 1) {
               System.out.print("Enter Card Type (Visa/MasterCard/Amex/Discover): ");
               String type = scanner.nextLine();
               System.out.print("Enter Card Number: ");
               String number = scanner.nextLine();
               System.out.print("Enter CVV: ");
               String cvv = scanner.nextLine();
               System.out.print("Enter Expiration Date (MM/YY): ");
               String exp = scanner.nextLine();
               System.out.print("Enter Credit Limit: ");
               double limit = scanner.nextDouble();
               cards.add(new CreditCard(number, type, cvv, exp, limit));
           } else if (choice == 2) {
               System.out.print("Enter Card Number: ");
               String number = scanner.nextLine();
               System.out.print("Enter Payment Amount: ");
               double amount = scanner.nextDouble();
               for (CreditCard card : cards) {
                   if (card.cardNumber.equals(number)) {
                       card.makePayment(amount);
                   }
               }
           } else if (choice == 3) {
               for (CreditCard card : cards) {
                   card.displayInfo();
               }
           } else if (choice == 4) {
               for (CreditCard card : cards) {
                   card.displayFeatures();
               }
           } else if (choice == 5) {
               System.out.print("Enter Card Number to Close: ");
               String number = scanner.nextLine();
               cards.removeIf(card -> card.cardNumber.equals(number) && card.outstandingBalance == 0);
           } else if (choice == 6) {
               CreditCard.saveToCSV(cards);
               return;
           }
       }
   }
}
