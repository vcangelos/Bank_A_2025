package bank;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.File;
import java.io.FileNotFoundException;

class Card {
    String cardNumber;

    Card(String cardNumber) {
        this.cardNumber = cardNumber;
    }

// Credit card network #
    String getCardType() {
        return "Visa";
    }
}

class ExtendedCard extends Card {
    private Random random = new Random();

    ExtendedCard(String cardNumber) {
        super(cardNumber);
    }

// CVC generator
    String generateCVC() {
        return String.format("%03d", random.nextInt(1000));
    }

// Expiration generator 2025-2030
    String generateExpirationDate() {
        int month = random.nextInt(12) + 1;
        int year = random.nextInt(6) + 25;
        return String.format("%02d/%d", month, year);
    }
}

class BankSecurity {
    private static final Random random = new Random();
    private static String storedHashedPin;

// 4-digit card pin generator
    public static String generateCardPin() {
        return String.valueOf(1000 + random.nextInt(9000));
    }

// Hashes PIN
    public static String hashPin(String pin) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(pin.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing PIN", e);
        }
    }

// Stores hashed pin
    public static void setAccountPin(String pin) {
        storedHashedPin = hashPin(pin);
    }

// Validates pin
    public static boolean validatePin(String enteredPin) {
        return hashPin(enteredPin).equals(storedHashedPin);
    }
}

// Test class
public class BankSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

// Prompt for user information to make a new debit card
        System.out.println("Please provide the following information to create a new debit card:");

// Ask for cardholder's first and last name
        System.out.print("Enter the cardholder's first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter the cardholder's last name: ");
        String lastName = scanner.nextLine();

// Generate card
        ExtendedCard card = new ExtendedCard(generateVisaCardNumber());
        System.out.println("\nCardholder Name: " + firstName + " " + lastName);
        System.out.println("Card Type: " + card.getCardType());
        System.out.println("Card Number: " + card.cardNumber);
        System.out.println("CVC: " + card.generateCVC());
        System.out.println("Expiration Date: " + card.generateExpirationDate());

// Generate PIN
        String cardPin = BankSecurity.generateCardPin();
        System.out.println("Generated Card PIN: " + cardPin);

// "Set PIN"
        System.out.print("Set your Account PIN (4-digit): ");
        String accountPin = scanner.nextLine();
        BankSecurity.setAccountPin(accountPin);
        System.out.println("Account PIN set successfully.");

// Verify PIN
        System.out.print("Enter your Account PIN to verify: ");
        String enteredPin = scanner.nextLine();
        if (BankSecurity.validatePin(enteredPin)) {
            System.out.println("PIN validation successful! Access granted to card details.");
        } else {
            System.out.println("Incorrect PIN. Access denied.");
        }

// Write to CSV
        writeCardInfoToCSV(firstName, lastName, card.getCardType(), card.cardNumber, card.generateCVC(), card.generateExpirationDate(), cardPin, accountPin);

// Read/print the CSV
        try (Scanner csvScanner = new Scanner(new File("card_info.csv"))) {
            System.out.println("\nContents of card_info.csv:");
            while (csvScanner.hasNextLine()) {
                System.out.println(csvScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }

        scanner.close();
    }

// Generates Visa card number
    private static String generateVisaCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder("4");
        for (int i = 1; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

// Writes card information to CSV
    private static void writeCardInfoToCSV(String firstName, String lastName, String cardType, String cardNumber, String cvc, String expirationDate, String cardPin, String accountPin) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("card_info.csv", true))) {
            writer.println(firstName + "," + lastName + "," + cardType + "," + cardNumber + "," + cvc + "," + expirationDate + "," + cardPin + "," + accountPin);
            System.out.println("Card information written to card_info.csv");
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}
