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

    // CVC generator (not used anymore)
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

public class BankSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Display menu
            System.out.println("\n===== Bank System Menu =====");
            System.out.println("1. Create New Card");
            System.out.println("2. View Existing Cards");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline

            // Handle user input
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
        // Ask for user details
        System.out.print("Enter the cardholder's first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter the cardholder's last name: ");
        String lastName = scanner.nextLine();

        // Generate card and expiration date
        ExtendedCard card = new ExtendedCard(generateVisaCardNumber());
        String expDate = card.generateExpirationDate();

        // Display generated card details
        System.out.println("\nCard successfully created!");
        System.out.println("Cardholder Name: " + firstName + " " + lastName);
        System.out.println("Card Type: " + card.getCardType());
        System.out.println("Card Number: " + card.cardNumber);
        System.out.println("Expiration Date: " + expDate);

        // Write to CSV file
        writeCardInfoToCSV(firstName, lastName, card.getCardType(), card.cardNumber, expDate);
    }

    private static void viewExistingCards() {
        // Read and display card info from CSV file
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

    private static void writeCardInfoToCSV(String firstName, String lastName, String cardType, String cardNumber, String expirationDate) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("card_info.csv", true))) {
            writer.println(firstName + "," + lastName + "," + cardType + "," + cardNumber + "," + expirationDate);
            System.out.println("Card information written to card_info.csv");
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}
