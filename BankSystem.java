package bank;

import java.io.*;
import java.util.*;

public class BankSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ensure the CSV file exists before proceeding
        File csvFile = new File("card_info.csv");
        if (!csvFile.exists()) {
            try {
                // Create an empty file if it doesn't exist
                csvFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating the CSV file: " + e.getMessage());
                return;  // Exit if we can't create the file
            }
        }

        // Main loop
        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Access existing card info");
            System.out.println("2. Create a new card");
            System.out.println("3. Close a debit card");

            String option = scanner.nextLine().trim();  // Read input as a string

            if (option.equals("1")) {
                // Access existing card info
                accessCardInfo(scanner);
            } else if (option.equals("2")) {
                // Create new card
                createNewCard(scanner);
            } else if (option.equals("3")) {
                // Close debit card
                closeDebitCard(scanner);
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Function to access existing card info
    private static void accessCardInfo(Scanner scanner) {
        System.out.print("Enter cardholder's first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter cardholder's last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter your 4-digit Account PIN: ");
        String enteredPin = scanner.nextLine();

        if (validatePinForExistingCard(firstName, lastName, enteredPin)) {
            // Display card info after successful PIN validation
            System.out.println("Access granted. Here are the card details:");
            showCardInfo(firstName, lastName);  // Show card info
        } else {
            System.out.println("Invalid PIN or no matching card information found.");
        }

        // Ask the user to type 'back' to return to the main menu
        System.out.println("Type 'back' to return to the main menu.");
        while (true) {
            String backInput = scanner.nextLine().trim().toLowerCase();
            if (backInput.equals("back")) {
                break;  // Break the loop and return to the main menu
            } else {
                System.out.println("Invalid input. Type 'back' to return to the main menu.");
            }
        }
    }

    // Function to show card info (called after PIN validation)
    private static void showCardInfo(String firstName, String lastName) {
        try (Scanner csvScanner = new Scanner(new File("card_info.csv"))) {
            while (csvScanner.hasNextLine()) {
                String line = csvScanner.nextLine();
                String[] cardInfo = line.split(",");
                String storedFirstName = cardInfo[0];
                String storedLastName = cardInfo[1];

                // Check if the first name and last name match
                if (storedFirstName.equalsIgnoreCase(firstName) && storedLastName.equalsIgnoreCase(lastName)) {
                    System.out.println("Cardholder: " + storedFirstName + " " + storedLastName);
                    System.out.println("Card Type: " + cardInfo[2]);
                    System.out.println("Card Number: " + cardInfo[3]);
                    System.out.println("CVC: " + cardInfo[4]);
                    System.out.println("Expiration Date: " + cardInfo[5]);
                    return;  // Return once the matching card info is found
                }
            }
            System.out.println("No matching card information found.");
        } catch (FileNotFoundException e) {
            System.out.println("Error reading the CSV file: " + e.getMessage());
        }
    }

    // Function to create a new card
    private static void createNewCard(Scanner scanner) {
        System.out.println("Please provide the following information to create a new debit card:");
        System.out.print("Enter the cardholder's first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter the cardholder's last name: ");
        String lastName = scanner.nextLine();

        // Here you would generate a new card and PIN
        String cardNumber = "4" + (1000000000000000L + (long)(Math.random() * 1000000000000000L));
        String cvc = String.format("%03d", (int)(Math.random() * 1000));
        String expirationDate = "12/25"; // Example expiration date

        System.out.println("\nCardholder Name: " + firstName + " " + lastName);
        System.out.println("Card Type: Visa");
        System.out.println("Card Number: " + cardNumber);
        System.out.println("CVC: " + cvc);
        System.out.println("Expiration Date: " + expirationDate);

        // Ask the user to set the 4-digit Account PIN
        System.out.print("Set your 4-digit Account PIN (this will be used to access your card): ");
        String accountPin = scanner.nextLine();

        // Save the card information and PIN to CSV
        writeCardInfoToCSV(firstName, lastName, "Visa", cardNumber, cvc, expirationDate, accountPin);
        System.out.println("Card created successfully and saved to card_info.csv.");

        // Prompt the user to type 'back' to return to the main menu
        System.out.println("Card creation successful. Type 'back' to return to the main menu.");
        while (true) {
            String backInput = scanner.nextLine().trim().toLowerCase();
            if (backInput.equals("back")) {
                break;  // Break the loop and return to the main menu
            } else {
                System.out.println("Invalid input. Type 'back' to return to the main menu.");
            }
        }
    }

    // Function to close a debit card (remove from CSV)
    private static void closeDebitCard(Scanner scanner) {
        System.out.print("Enter cardholder's first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter cardholder's last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter your 4-digit Account PIN: ");
        String enteredPin = scanner.nextLine();

        if (removeCardFromCSV(firstName, lastName, enteredPin)) {
            System.out.println("Debit card closed successfully.");
            
            // Prompt the user to type "back" to return to the menu
            System.out.println("Type 'back' to return to the main menu.");
            while (true) {
                String backInput = scanner.nextLine().trim().toLowerCase();
                if (backInput.equals("back")) {
                    break;  // Break the loop and return to the main menu
                } else {
                    System.out.println("Invalid input. Type 'back' to return to the main menu.");
                }
            }
        } else {
            System.out.println("Invalid PIN or no matching card information found.");
        }
    }

    // Function to write card info to CSV file
    private static void writeCardInfoToCSV(String firstName, String lastName, String cardType, String cardNumber, String cvc, String expirationDate, String accountPin) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("card_info.csv", true))) {
            writer.println(firstName + "," + lastName + "," + cardType + "," + cardNumber + "," + cvc + "," + expirationDate + "," + accountPin);
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    // Function to validate PIN for existing card
    private static boolean validatePinForExistingCard(String firstName, String lastName, String enteredPin) {
        try (Scanner csvScanner = new Scanner(new File("card_info.csv"))) {
            while (csvScanner.hasNextLine()) {
                String line = csvScanner.nextLine();
                String[] cardInfo = line.split(",");
                String storedFirstName = cardInfo[0];
                String storedLastName = cardInfo[1];
                String storedAccountPin = cardInfo[6]; // The Account PIN is stored at index 6

                // Check if the entered PIN matches the stored PIN
                if (storedFirstName.equalsIgnoreCase(firstName) && storedLastName.equalsIgnoreCase(lastName)
                    && storedAccountPin.equals(enteredPin)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading the CSV file: " + e.getMessage());
        }
        return false;
    }

    // Function to remove a card from the CSV file
    private static boolean removeCardFromCSV(String firstName, String lastName, String enteredPin) {
        List<String> lines = new ArrayList<>();
        boolean cardFound = false;

        try (Scanner csvScanner = new Scanner(new File("card_info.csv"))) {
            while (csvScanner.hasNextLine()) {
                String line = csvScanner.nextLine();
                String[] cardInfo = line.split(",");
                String storedFirstName = cardInfo[0];
                String storedLastName = cardInfo[1];
                String storedAccountPin = cardInfo[6];

                // If the card matches the entered data, skip adding it to the new list
                if (storedFirstName.equalsIgnoreCase(firstName) && storedLastName.equalsIgnoreCase(lastName)
                        && storedAccountPin.equals(enteredPin)) {
                    cardFound = true; // Found the card, so don't add it
                    continue;
                }
                lines.add(line);
            }

            if (cardFound) {
                // Re-write the CSV file without the deleted card
                try (PrintWriter writer = new PrintWriter(new FileWriter("card_info.csv"))) {
                    for (String line : lines) {
                        writer.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("Error writing the CSV file: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading the CSV file: " + e.getMessage());
        }
        return cardFound;
    }
}
