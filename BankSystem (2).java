import java.io.*;
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
    private static final double TRANSFER_FEE = 5.0;

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

    public void makePurchase(double amount) {
        if (amount > balance) {
            System.out.println("Purchase declined: Insufficient credit.");
        } else {
            balance -= amount;
            outstandingBalance += amount;
            monthlySpending += amount;
            System.out.println("Purchase approved: " + amount);
        }
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

    public void transferFunds(CreditCard targetCard, double amount) {
        if (amount + TRANSFER_FEE > balance) {
            System.out.println("Transfer failed: Insufficient funds.");
        } else {
            balance -= (amount + TRANSFER_FEE);
            targetCard.balance += amount;
            System.out.println("Transfer successful: " + amount + " (Fee: " + TRANSFER_FEE + ")");
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

    public static void saveToCSV(List<CreditCard> cards) {
        try (PrintWriter writer = new PrintWriter(new File("customers.csv"))) {
            writer.println("CardNumber,CardType,CVV,ExpirationDate,Balance,CreditLimit,OutstandingBalance,CreditScore,MonthlySpending");
            for (CreditCard card : cards) {
                writer.println(card.cardNumber + "," + card.cardType + "," + card.cvv + "," + card.expirationDate + "," +
                        card.balance + "," + card.creditLimit + "," + card.outstandingBalance + "," + card.creditScore + "," + card.monthlySpending);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
}

public class BankSystem {
    public static void main(String[] args) {
        List<CreditCard> cards = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Open Credit Card\n2. Make Purchase\n3. Make Payment\n4. Transfer Funds\n5. Display Info\n6. Close Account\n7. Save & Exit");
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
                System.out.print("Enter Purchase Amount: ");
                double amount = scanner.nextDouble();
                for (CreditCard card : cards) {
                    if (card.cardNumber.equals(number)) {
                        card.makePurchase(amount);
                    }
                }
            } else if (choice == 3) {
                System.out.print("Enter Card Number: ");
                String number = scanner.nextLine();
                System.out.print("Enter Payment Amount: ");
                double amount = scanner.nextDouble();
                for (CreditCard card : cards) {
                    if (card.cardNumber.equals(number)) {
                        card.makePayment(amount);
                    }
                }
            } else if (choice == 4) {
                System.out.print("Enter Source Card Number: ");
                String sourceNumber = scanner.nextLine();
                System.out.print("Enter Target Card Number: ");
                String targetNumber = scanner.nextLine();
                System.out.print("Enter Amount: ");
                double amount = scanner.nextDouble();
                CreditCard source = null, target = null;
                for (CreditCard card : cards) {
                    if (card.cardNumber.equals(sourceNumber)) source = card;
                    if (card.cardNumber.equals(targetNumber)) target = card;
                }
                if (source != null && target != null) {
                    source.transferFunds(target, amount);
                }
            } else if (choice == 5) {
                for (CreditCard card : cards) {
                    card.displayInfo();
                }
            } else if (choice == 6) {
                System.out.print("Enter Card Number to Close: ");
                String number = scanner.nextLine();
                cards.removeIf(card -> card.cardNumber.equals(number) && card.outstandingBalance == 0);
            } else if (choice == 7) {
                CreditCard.saveToCSV(cards);
                return;
            }
        }
    }
}
