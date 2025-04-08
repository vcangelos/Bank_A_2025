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

    // Returns the card type
    String getCardType() {
        return "Visa";
    }
}

class ExtendedCard extends Card {
    private Random random = new Random();

    ExtendedCard(String cardNumber) {
        super(cardNumber);
    }

// Generates a 3-digit CVC (no longer used)
    String generateCVC() {
        return String.format("%03d", random.nextInt(1000));
    }

    // Generates expiration date between 2025 and 2030
    String generateExpirationDate() {
        int month = random.nextInt(12) + 1;
        int year = random.nextInt(6) + 25;
        return String.format("%02d/%d", month, year);
    }
}

class BankSecurity {
    private static final Random random = new Random();
    private static String storedHashedPin;

// Generates a 4-digit card PIN
    public static String generateCardPin() {
        return String.valueOf(1000 + random.nextInt(9000));
    }

// Hashes a PIN using SHA-256
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

// Stores the hashed PIN
    public static void setAccountPin(String pin) {
        storedHashedPin = hashPin(pin);
    }

// Validates entered PIN against stored hash
    public static boolean validatePin(String enteredPin) {
        return hashPin(enteredPin).equals(storedHashedPin);
    }
}

public class BankSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Display main menu
            System.out.println("\n===== Bank System Menu =====");
            System.out.println("1. Create New Card");
            System.out.println("2. View Existing Cards");
            System.out.println("3. Exit");
            System.out.println("4. Close (Delete) a Card");
            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

    // menu
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
                case 4:
                    closeCard(scanner);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

// Handles creation of a new card
    private static void createNewCard(Scanner scanner) {
        System.out.print("Enter the cardholder's first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter the cardholder's last name: ");
        String lastName = scanner.nextLine();

        ExtendedCard card = new ExtendedCard(generateVisaCardNumber());
        String expDate = card.generateExpirationDate();

        System.out.println("\nCard successfully created!");
        System.out.println("Cardholder Name: " + firstName + " " + lastName);
        System.out.println("Card Type: " + card.getCardType());
        System.out.println("Card Number: " + card.cardNumber);
        System.out.println("Expiration Date: " + expDate);

        writeCardInfoToCSV(firstName, lastName, card.getCardType(), card.cardNumber, expDate);
    }

// Displays contents of the CSV
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

// Deletes a specific card from CSV
    private static void closeCard(Scanner scanner) {
        System.out.print("Enter the cardholder's first name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Enter the cardholder's last name: ");
        String lastName = scanner.nextLine().trim();
        System.out.print("Enter the card number: ");
        String cardNumber = scanner.nextLine().trim();

        File file = new File("card_info.csv");
        if (!file.exists()) {
            System.out.println("No cards found to delete.");
            return;
        }

        List<String> updatedLines = new ArrayList<>();
        boolean cardFound = false;

// Read each line and filter out matching card
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String fName = parts[0].trim();
                String lName = parts[1].trim();
                String cNumber = parts[3].trim();

                if (fName.equalsIgnoreCase(firstName) &&
                    lName.equalsIgnoreCase(lastName) &&
                    cNumber.equals(cardNumber)) {
                    cardFound = true;
                } else {
                    updatedLines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            return;
        }

    // Write updated data back to file if card was found
        if (cardFound) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (String line : updatedLines) {
                    writer.println(line);
                }
                System.out.println("Card successfully deleted.");
            } catch (IOException e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }
        } else {
            System.out.println("No matching card found.");
        }
    }

// Generates a random 16-digit Visa card number
    private static String generateVisaCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder("4");
        for (int i = 1; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

// Writes a card's info to the CSV file
    private static void writeCardInfoToCSV(String firstName, String lastName, String cardType, String cardNumber, String expirationDate) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("card_info.csv", true))) {
            writer.println(firstName + "," + lastName + "," + cardType + "," + cardNumber + "," + expirationDate);
            System.out.println("Card information written to card_info.csv");
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}
