import java.util.Random;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Card {
    String cardNumber;

    Card(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    // Credit card network #
    String getCardType() {
        char firstDigit = cardNumber.charAt(0);

        if (firstDigit == '3') return "American Express";
        if (firstDigit == '4') return "Visa";
        if (firstDigit == '5') return "MasterCard";
        if (firstDigit == '6') return "Discover";
        return "Unknown";
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

        // Ask for cardholder's first and last name
        System.out.print("Enter the cardholder's first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter the cardholder's last name: ");
        String lastName = scanner.nextLine();

        // User chooses card type
        System.out.println("Select your Card Type:");
        System.out.println("1. American Express");
        System.out.println("2. Visa");
        System.out.println("3. MasterCard");
        System.out.println("4. Discover");
        System.out.print("Enter the number of your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        // Check if the choice is valid
        if (choice < 1 || choice > 4) {
            System.out.println("Invalid input! Please enter a number between 1 and 4.");
            scanner.close();
            return;
        }

        // Generate card
        ExtendedCard card = new ExtendedCard(generateCardNumber(choice));
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
            System.out.println("PIN validation successful!");
            System.out.println("Access granted to card details.");
        } else {
            System.out.println("Incorrect PIN. Access denied.");
        }

        scanner.close();
    }

    // Generates card # from company #
    private static String generateCardNumber(int choice) {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();

        // Assign the first digit based on user choice
        char firstDigit;
        if (choice == 1) {
            firstDigit = '3';  // (American Express)
        } else if (choice == 2) {
            firstDigit = '4';  // (Visa)
        } else if (choice == 3) {
            firstDigit = '5';  // (MasterCard)
        } else {
            firstDigit = '6';  // (Discover)
        }

        cardNumber.append(firstDigit);

        for (int i = 1; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }

        return cardNumber.toString();
    }
}
