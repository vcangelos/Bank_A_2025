package moneytransfer;

import java.text.DecimalFormat;
import java.util.Scanner;

public class moneytransfer {
    public static void main(String[] args) {
        boolean accselected = false;
        String checkorsave;
        Scanner scan = new Scanner(System.in);
        DecimalFormat df = new DecimalFormat("0.00"); // Format for currency

        System.out.print("What account would you like to transfer from, checking [1] or savings [2]? ");
        while (!accselected) {
            String transAcc = scan.nextLine().trim();
            if (transAcc.equals("1")) {
                System.out.println("You selected checking.");
                checkorsave = "checking";
                accselected = true;
            } else if (transAcc.equals("2")) {
                System.out.println("You selected savings.");
                checkorsave = "savings";
                accselected = true;
            } else {
                System.out.println("Invalid input. Please type 1 for checking or 2 for savings.");
            }
        }

        String accNum;
        boolean validAcc = false;
        while (!validAcc) {
            System.out.println("Please enter your 12-digit account number:");
            System.out.print("#");
            accNum = scan.nextLine().trim();

            if (accNum.matches("\\d{12}")) {
                System.out.println("Account number is valid.");
                validAcc = true;
            } else {
                System.out.println("Invalid account number. Please enter a valid 12-digit account number.");
            }
        }

        String routingNumber;
        boolean validRoute = false;
        while (!validRoute) {
            System.out.println("Please enter your 9-digit routing number:");
            System.out.print("#");
            routingNumber = scan.nextLine().trim();

            if (routingNumber.matches("\\d{9}")) {
                System.out.println("Routing number is valid.");
                validRoute = true;
            } else {
                System.out.println("Invalid routing number. Please enter a valid 9-digit routing number.");
            }
        }

        System.out.println("Please enter your billing address:");

        System.out.print("Street Address: ");
        String streetAddress = scan.nextLine().trim();

        System.out.print("City: ");
        String city = scan.nextLine().trim();

        System.out.print("State: ");
        String state = scan.nextLine().trim();

        String zipCode = "";
        boolean validZip = false;
		while (!validZip ) {
            System.out.print("Zip Code: ");
            zipCode = scan.nextLine().trim();

            if (zipCode.matches("\\d{5}")) {
                System.out.println("Zip Code is valid.");
                validZip = true;
            } else {
                System.out.println("Invalid Zip Code. Please enter a valid 5-digit zip code.");
            }
        }

		String country = "";
        boolean validCountry = false;
        double transferFee = 0.00;
        while (!validCountry) {
            System.out.print("Country/Continent (Choose from USA, Canada, EU, China, UK): ");
            country = scan.nextLine().trim().toUpperCase();

            switch (country) {
                case "USA":
                    transferFee = 0.02;
                    validCountry = true;
                    break;
                case "Canada":
                    transferFee = 0.03;
                    validCountry = true;
                    break;
                case "EU":
                    transferFee = 0.05;
                    validCountry = true;
                    break;
                case "CHINA":
                    transferFee = 0.07;
                    validCountry = true;
                    break;
                case "UK":
                    transferFee = 0.05;
                    validCountry = true;
                    break;
                default:
                    System.out.println("Invalid country. Please enter USA, Canada, EU, China, or UK.");
            }
        }

        System.out.println("\nBilling Address:");
        System.out.println("Street Address: " + streetAddress);
        System.out.println("City/Province: " + city);
        System.out.println("State/Region: " + state);
        System.out.println("Zip Code/Postal Code: " + zipCode);
        System.out.println("Country: " + country);

        double amount = 0.0;
        boolean validAmount = false;
        while (!validAmount) {
            System.out.print("Enter the amount to transfer: ");
            if (scan.hasNextDouble()) {
                amount = scan.nextDouble();
                if (amount > 0) {
                    validAmount = true;
                } else {
                    System.out.println("Transfer amount must be greater than zero.");
                }
            } else {
                System.out.println("Invalid amount. Please enter a valid number with a decimal.");
                scan.next(); // Consume invalid input
            }
        }

        scan.nextLine(); // Consume newline

        // Calculate total amount deducted
        double totalAmount = amount + (amount * transferFee);

        // Transfer Summary
        System.out.println("\nTransfer Summary:");
        System.out.println("Transfer Amount: $" + df.format(amount));
        System.out.println("Transfer Fee: " + (transferFee * 100) + "%");
        System.out.println("Total Amount Deducted: $" + df.format(totalAmount));

        scan.close();
    }
}
