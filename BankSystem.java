package bank;

import java.io.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Card {
    String cardNumber;

    Card(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    String getCardType() {
        return "Visa";
    }
}

class ExtendedCard extends Card {
    private Random random = new Random();

    ExtendedCard(String cardNumber) {
        super(cardNumber);
    }

    String generateCVC() {
        return String.format("%03d", random.nextInt(1000));
    }

    String generateExpirationDate() {
        int month = random.nextInt(12) + 1;
        int year = random.nextInt(6) + 25;
        return String.format("%02d/%d", month, year);
    }
}

class BankSecurity {
    private static final Random random = new Random();

    public static String generateCardPin() {
        return String.valueOf(1000 + random.nextInt(9000));
    }
}

public class BankSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== Bank System Menu =====");
            System.out.println("1. Create New Card");
            System.out.println("2. View Existing Cards");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    createNewCard(scanner);
                    break;
                case 2:
                    viewExistingCards();
                    break;
                case 3:
                    System.out.println("Exiting Bank System...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void createNewCard(Scanner scanner) {
        System.out.print("Enter the cardholder's first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter the cardholder's last name: ");
        String lastName = scanner.nextLine();

        ExtendedCard card = new ExtendedCard(generateVisaCardNumber());
        String cvc = card.generateCVC();
        String expDate = card.generateExpirationDate();
        String cardPin = BankSecurity.generateCardPin();
        
        System.out.println("\nCard successfully created!");
        System.out.println("Cardholder Name: " + firstName + " " + lastName);
        System.out.println("Card Type: " + card.getCardType());
        System.out.println("Card Number: " + card.cardNumber);
        System.out.println("CVC: " + cvc);
        System.out.println("Expiration Date: " + expDate);
        System.out.println("Generated Card PIN: " + cardPin);

        System.out.print("Set your Account PIN (4-digit): ");
        String accountPin = scanner.nextLine();
        System.out.println("Account PIN set successfully.");

        writeCardInfoToCSV(firstName, lastName, card.getCardType(), card.cardNumber, cvc, expDate, cardPin, accountPin);
    }

    private static void viewExistingCards() {
        File file = new File("card_info.csv");
        if (!file.exists()) {
            System.out.println("No existing cards found.");
            return;
        }
        
        try (Scanner csvScanner = new Scanner(file)) {
            System.out.println("\nContents of card_info.csv:");
            while (csvScanner.hasNextLine()) {
                System.out.println(csvScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }
    }

    private static String generateVisaCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder("4");
        for (int i = 1; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    private static void writeCardInfoToCSV(String firstName, String lastName, String cardType, String cardNumber, String cvc, String expirationDate, String cardPin, String accountPin) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("card_info.csv", true))) {
            writer.println(firstName + "," + lastName + "," + cardType + "," + cardNumber + "," + cvc + "," + expirationDate + "," + cardPin + "," + accountPin);
            System.out.println("Card information written to card_info.csv");
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}
