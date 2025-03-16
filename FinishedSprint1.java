import javax.swing.*; // FOR THE GUI'S (I learned this last year during my create task for CSP)
import java.io.*;
import java.util.*;
import java.util.Random; // for unique ID
import java.util.Scanner; //to read the CSV file
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//https://www.youtube.com/watch?v=-95U3CZPlE8 referenced for reading the CSV FILE

//https://www.youtube.com/watch?v=TpyRKom0X_s to edit certain parts of CSV TO DO!

//To DO:
//user options after the sign ins like to change userinfo using the verification, basically done need to connect others first and create the options list
//logging out ability
// TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

public class FinishedSprint1 {
    public static void main(String[] args) throws FileNotFoundException {
        int rerun = -3;
        while (rerun != -1) { // so user can return to welcome page
            Boolean valid = false;
            while (!valid) { // User must have valid response so he doesnt glitch the code
                String signinorsignup = JOptionPane.showInputDialog(null, "Welcome Would you like to sign in or sign up?");
                signinorsignup = signinorsignup.toLowerCase();
                signinorsignup = signinorsignup.replace(" ", ""); // replaces spaces ex: Sign in becomes Signin(edgecasing)
                if (signinorsignup.equalsIgnoreCase("Signup")) {
                    String username = JOptionPane.showInputDialog(null, "Welcome To the bank Please enter your Username");
                    String usernameexists=existinguser(username);
                    while (username == null || username.isEmpty()||usernameexists.equals("already exists")) {//isEmpty so it doesnt edgecase and return a value
                        if(usernameexists.equals("already exists")){
                            username = JOptionPane.showInputDialog(null, "Username is already taken. Please enter a different Username");
                            usernameexists=existinguser(username);

                        }else{
                            username = JOptionPane.showInputDialog(null, "Username cannot be empty. Please enter your Username");
                            usernameexists=existinguser(username);
                        }


                    }

                    String password = JOptionPane.showInputDialog(null, "Hello " + username + " Please enter your Password");
                    while (password == null || password.isEmpty()) {
                        password = JOptionPane.showInputDialog(null, "Password cannot be empty. Please enter your Password");
                    }

                    String firstname = JOptionPane.showInputDialog(null, username + " Please enter your First Name");
                    while (firstname == null || firstname.isEmpty()) {
                        firstname = JOptionPane.showInputDialog(null, username + " First Name cannot be empty. Please enter your First Name");
                    }

                    String lastname = JOptionPane.showInputDialog(null, username + " Please enter your Last Name");
                    while (lastname == null || lastname.isEmpty()) {
                        lastname = JOptionPane.showInputDialog(null, username + " Last Name cannot be empty. Please enter your Last Name");
                    }

                    String SSNinput = JOptionPane.showInputDialog("Enter your Social Security number (9 digits, no dashes):");

                    // Ensure the SSN is exactly 9 digits and contains only numbers
                    while (SSNinput == null ||!SSNinput.matches("\\d{9}")|| SSNinput.isEmpty()) {
                        SSNinput = JOptionPane.showInputDialog("Invalid SSN. Please enter exactly 9 digits, no dashes:");
                    }

                    int SSN = Integer.parseInt(SSNinput);
                    String DOB = JOptionPane.showInputDialog(null, "Enter your DOB, MMDDYY no slashes");
                    while (DOB == null || DOB.isEmpty() || !DOB.matches("\\d{6}")) {
                        DOB = JOptionPane.showInputDialog(null, "Invalid Date of Birth. Please enter exactly 6 digits, no slashes:");
                    }

                    String email = JOptionPane.showInputDialog(null, "Enter your email");
                    while (email == null || !email.contains("@")|| email.isEmpty()) {
                        email = JOptionPane.showInputDialog(null, "Invalid email. Please enter a valid email with '@':");
                    }
                    String phone = JOptionPane.showInputDialog(null, "Enter your phone number");
                    while (phone == null || phone.isEmpty()||!phone.matches("\\d{10}")) {
                        phone = JOptionPane.showInputDialog(null, "Invalid phone number. Please enter a valid phone number with 10 digits");
                    }
                    long phonenum = Long.parseLong(phone);

                    double minimumdeposit = 0;
                    double validminimumdepositvalue = 25;
                    while (minimumdeposit < validminimumdepositvalue) {
                        String minimumdeppositinput = JOptionPane.showInputDialog(null, "Enter your minimum deposit please note your minimum deposit value has to be greater than $25");
                        minimumdeposit = Double.parseDouble(minimumdeppositinput);
                    }
                    int ID = genid();
                    String uniqID = String.valueOf(ID);
                    UserData user=new UserData(username, password, firstname, lastname, SSN, DOB, email, phonenum, minimumdeposit, ID);
                    addUser(username, password, firstname, lastname, SSN, DOB, email, phonenum, minimumdeposit, ID);


                    //USER CHOICE GOES HERE
                    Boolean exit = false;
                    while (exit==false) {
                            String MenuOptions=JOptionPane.showInputDialog(null, "Please choose a option: [1] Access Credit Card, [2] Access Debit Card, [3] Change your personal information, [4] log out ");
                        if(MenuOptions.equals("1")){
                            List<CreditCard> cards = new ArrayList<>();
                            Scanner scanner = new Scanner(System.in);
                            while (true) {
                                System.out.println("1. Open Credit Card\n2. Make Purchase\n3. Make Payment\n4. Transfer Funds\n5. Display Info\n6. Close Account\n7. Save & Exit");
                                int choice = scanner.nextInt();
                                scanner.nextLine();

                                if (choice == 1) {
                                    //from GABBY AND ANT (CREDIT CARD)
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
                                    return; // menu
                                }
                            }
                        } else if (MenuOptions.equals("2")) {
                            Scanner scanner = new Scanner(System.in);

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
                            writeCardInfoToCSV(firstName, lastName, card.getCardType(), card.cardNumber, card.generateCVC(), card.generateExpirationDate(), cardPin, accountPin);
                            // Read and print the CSV file
                            try (Scanner csvScanner = new Scanner(new File("card_info.csv"))) {
                                System.out.println("\nContents of card_info.csv:");
                                while (csvScanner.hasNextLine()) {
                                    System.out.println(csvScanner.nextLine());
                                }
                            } catch (FileNotFoundException e) {
                                System.out.println("Error reading CSV file: " + e.getMessage());
                            }

                            scanner.close();


                        }else if(MenuOptions.equals("3")){
                            String changeUserDetails=JOptionPane.showInputDialog("[1] Change first name, [2] Change last name,[3] change username, [4] change Social Security Number,[5] change email, [6] change phone number, [7] cancel and go back to menu  ");
                            if(changeUserDetails.equals("1")) {
                                String newFirstname = JOptionPane.showInputDialog("What would you like your new first name to be on your account");
                                while (newFirstname == null || newFirstname.isEmpty()) {//isEmpty so it doesnt edgecase and return a value
                                    newFirstname = JOptionPane.showInputDialog(null, "Firstname cannot be empty. Please enter your First name");
                                }
                                AppendCSV(user.getFirstname(), 2, newFirstname, user.getUniqID());
                                user.setFirstname(newFirstname);


                            } else if (changeUserDetails.equals("2")) {
                                String newlastname = JOptionPane.showInputDialog("What would you like your new first name to be on your account");
                                while (newlastname == null || newlastname.isEmpty()) {//isEmpty so it doesnt edgecase and return a value
                                    newlastname = JOptionPane.showInputDialog(null, "Firstname cannot be empty. Please enter your First name");
                                }
                                AppendCSV(user.getLastname(), 3, newlastname, user.getUniqID());
                                user.setLastname(newlastname);


                            } else if (changeUserDetails.equals("3")){
                                String newusername=JOptionPane.showInputDialog("what would you like your new username to be?");

                                  String Ausernameexists=existinguser(newusername);
                                  while (newusername == null || newusername.isEmpty()||Ausernameexists.equals("already exists")) {//isEmpty so it doesnt edgecase and return a value
                                        if(Ausernameexists.equals("already exists")){
                                            newusername = JOptionPane.showInputDialog(null, "Username is already taken. Please enter a different Username");
                                        Ausernameexists=existinguser(newusername);
                                        }else{
                                            newusername = JOptionPane.showInputDialog(null, "Username cannot be empty. Please enter your Username");
                                            Ausernameexists=existinguser(newusername);
                                        }



                                  }
                                  AppendCSV(username,0,newusername, user.getUniqID());
                                  user.setUsername(newusername);


                            }else if(changeUserDetails.equals("4")){
                                String newSSNinput = JOptionPane.showInputDialog("Enter your new Social Security number (9 digits, no dashes):");

                                // Ensure the SSN is exactly 9 digits and contains only numbers
                                while (newSSNinput == null ||!newSSNinput.matches("\\d{9}")|| newSSNinput.isEmpty()) {
                                    newSSNinput = JOptionPane.showInputDialog("Invalid SSN. Please enter exactly 9 digits, no dashes:");
                                }
                                int newSSN = Integer.parseInt(newSSNinput);

                                String SSNinstring = Integer.toString(user.getSsn());
                                AppendCSV(SSNinstring,4,newSSNinput, user.getUniqID());
                                user.setSsn(newSSN);
                            }else if(changeUserDetails.equals("5")){
                                String newemail = JOptionPane.showInputDialog(null, "Enter your email");
                                while (newemail == null || !newemail.contains("@")|| newemail.isEmpty()) {
                                    newemail = JOptionPane.showInputDialog(null, "Invalid email. Please enter a valid email with '@':");
                                }
                                AppendCSV(user.getEmail(),5,newemail, user.getUniqID());
                                user.setEmail(newemail);
                            }else if(changeUserDetails.equals("6")){
                                String newphone = JOptionPane.showInputDialog(null, "Enter your phone number");
                                while (newphone == null || newphone.isEmpty()||!newphone.matches("\\d{10}")) {
                                    newphone = JOptionPane.showInputDialog(null, "Invalid phone number. Please enter a valid phone number with 10 digits");
                                }
                                long newphonenum = Long.parseLong(newphone);
                                String origphone=Long.toString(user.getPhone());
                                AppendCSV(origphone,7,newphone, user.getUniqID());
                                user.setPhone(phonenum);
                            }else if(changeUserDetails.equals("7")){
                                exit=false;
                            }
                        } else if (MenuOptions.equals("4")) {
                            exit=true;
                            break;
                        }else{
                            JOptionPane.showMessageDialog(null, "Please select a valid option");
                        }


                        }
                        String signout=JOptionPane.showInputDialog(null,"[1] proceed to the sign out screen, any other number to continue");
                        if(signout.equals("1")) {
                            valid = true;

                        }else if(signout.equals("2")) {
                            valid = false;
                            break;

                        }


                } else if (signinorsignup.equalsIgnoreCase("SignIn")) { // STILL IN WORK GOTTA DO CSV FILE FIRST
                    String username = JOptionPane.showInputDialog(null, "Welcome To the bank Please enter your Username");

                    // Assuming users() is a method that retrieves user data based on the username
                    UserData user = users(username);

                    // While loop to check if the username is found and keeps going so you cant break it
                    while (user == null) {
                        username = JOptionPane.showInputDialog(null, "Your username was not found. Please enter your Username");
                        user = users(username); // Fetch user data based on entered username
                    }

                    String password = JOptionPane.showInputDialog(null, "Hello " + username + " Please enter your Password");
                    String truePassword = user.getPassword(); // Fetch stored password for comparison

                    // Remove spaces from entered password
                    String forgotpassword = password;
                    forgotpassword = forgotpassword.replace(" ", "");

                    int passwordmatch = -1;
                    while (passwordmatch < 0) {
                        if (truePassword.equals(password)) {
                            // Password matches, welcome the user
                            JOptionPane.showMessageDialog(null, "Welcome To the bank, " + user.getFirstname() + "!");
                            passwordmatch = 1; // Exit the loop
                        } else if (forgotpassword.equalsIgnoreCase("forgotpassword")) {
                            // User forgot password, proceed to reset
                            String recoveracct = JOptionPane.showInputDialog(null,
                                    "Please enter your Social Security number without dashes to reset your password:");

                            // Ensure SSN has no dashes before proceeding
                            while (recoveracct.contains("-")) {
                                recoveracct = JOptionPane.showInputDialog(null, "Dashes are not allowed. Please enter your Social Security number again:");
                            }

                            int ssn = user.getSsn(); // Get the user's SSN
                            String SSNtoString = Integer.toString(ssn);
                            boolean SSNmatch = false;

                            // Prompt for SSN until the correct one is entered
                            while (!SSNmatch) {
                                if (recoveracct.equals(SSNtoString)) {
                                    // SSN matches, ask for new password
                                    String newpassword = JOptionPane.showInputDialog(null, "Enter your new password:");
                                    user.setPassword(newpassword); // Sets it as new password
                                    JOptionPane.showMessageDialog(null, "Password reset successfully!");
                                    SSNmatch = true; // Successfully reset password, exit the loop
                                    AppendCSV(truePassword, 1, newpassword,user.getUniqID()); // Append new password to CSV or save securely
                                    passwordmatch = 1; // Exit the outer loop since password was reset
                                } else {
                                    // SSN doesn't match, prompt again
                                    recoveracct = JOptionPane.showInputDialog(null,
                                            "Your Social Security number did not match. Please enter it again:");

                                    // Ensure no dashes in SSN input again
                                    while (recoveracct.contains("-")) {
                                        recoveracct = JOptionPane.showInputDialog(null,
                                                "Dashes are not allowed. Please enter your Social Security number again:");
                                    }
                                }
                            }
                        } else {
                            // If password is incorrect and "forgotpassword" was not entered, prompt again
                            password = JOptionPane.showInputDialog(null, "Incorrect password. Please try again.");
                            forgotpassword = password.replace(" ", "");
                        }

                    }
                     JOptionPane.showMessageDialog(null, "Welcome back "+user.getFirstname()+" "+user.getLastname());
                    Boolean exit = false;
                    while (exit==false) {
                            String MenuOptions=JOptionPane.showInputDialog(null, "Please choose a option: [1] Access Credit Card, [2] Access Debit Card, [3] Change your personal information, [4] log out ");
                        if(MenuOptions.equals("1")){
                            List<CreditCard> cards = new ArrayList<>();
                            Scanner scanner = new Scanner(System.in);
                            while (true) {
                                System.out.println("1. Open Credit Card\n2. Make Purchase\n3. Make Payment\n4. Transfer Funds\n5. Display Info\n6. Close Account\n7. Save & Exit");
                                int choice = scanner.nextInt();
                                scanner.nextLine();

                                if (choice == 1) {
                                    //from GABBY AND ANT (CREDIT CARD)
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
                                    return; // menu
                                }
                            }
                        } else if (MenuOptions.equals("2")) {
                            Scanner scanner = new Scanner(System.in);

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
                            writeCardInfoToCSV(firstName, lastName, card.getCardType(), card.cardNumber, card.generateCVC(), card.generateExpirationDate(), cardPin, accountPin);
                            // Read and print the CSV file
                            try (Scanner csvScanner = new Scanner(new File("card_info.csv"))) {
                                System.out.println("\nContents of card_info.csv:");
                                while (csvScanner.hasNextLine()) {
                                    System.out.println(csvScanner.nextLine());
                                }
                            } catch (FileNotFoundException e) {
                                System.out.println("Error reading CSV file: " + e.getMessage());
                            }

                            scanner.close();


                        }else if(MenuOptions.equals("3")){
                            String changeUserDetails=JOptionPane.showInputDialog("[1] Change first name, [2] Change last name,[3] change username, [4] change Social Security Number,[5] change email, [6] change phone number, [7] cancel and go back to menu  ");
                            if(changeUserDetails.equals("1")) {
                                String newFirstname = JOptionPane.showInputDialog("What would you like your new first name to be on your account");
                                while (newFirstname == null || newFirstname.isEmpty()) {//isEmpty so it doesnt edgecase and return a value
                                    newFirstname = JOptionPane.showInputDialog(null, "Firstname cannot be empty. Please enter your First name");
                                }
                                AppendCSV(user.getFirstname(), 2, newFirstname, user.getUniqID());
                                user.setFirstname(newFirstname);


                            } else if (changeUserDetails.equals("2")) {
                                String newlastname = JOptionPane.showInputDialog("What would you like your new first name to be on your account");
                                while (newlastname == null || newlastname.isEmpty()) {//isEmpty so it doesnt edgecase and return a value
                                    newlastname = JOptionPane.showInputDialog(null, "Firstname cannot be empty. Please enter your First name");
                                }
                                AppendCSV(user.getLastname(), 3, newlastname, user.getUniqID());
                                user.setLastname(newlastname);


                            } else if (changeUserDetails.equals("3")){
                                String newusername=JOptionPane.showInputDialog("what would you like your new username to be?");

                                  String usernameexists=existinguser(newusername);
                                  while (newusername == null || newusername.isEmpty()||usernameexists.equals("already exists")) {//isEmpty so it doesnt edgecase and return a value
                                        if(usernameexists.equals("already exists")){
                                            newusername = JOptionPane.showInputDialog(null, "Username is already taken. Please enter a different Username");
                                        usernameexists=existinguser(newusername);
                                        }else{
                                            newusername = JOptionPane.showInputDialog(null, "Username cannot be empty. Please enter your Username");
                                            usernameexists=existinguser(newusername);
                                        }



                                  }
                                  AppendCSV(username,0,newusername, user.getUniqID());
                                  user.setUsername(newusername);


                            }else if(changeUserDetails.equals("4")){
                                String newSSNinput = JOptionPane.showInputDialog("Enter your new Social Security number (9 digits, no dashes):");

                                // Ensure the SSN is exactly 9 digits and contains only numbers
                                while (newSSNinput == null ||!newSSNinput.matches("\\d{9}")|| newSSNinput.isEmpty()) {
                                    newSSNinput = JOptionPane.showInputDialog("Invalid SSN. Please enter exactly 9 digits, no dashes:");
                                }
                                int newSSN = Integer.parseInt(newSSNinput);

                                String SSNinstring = Integer.toString(user.getSsn());
                                AppendCSV(SSNinstring,4,newSSNinput, user.getUniqID());
                                user.setSsn(newSSN);
                            }else if(changeUserDetails.equals("5")){
                                String newemail = JOptionPane.showInputDialog(null, "Enter your email");
                                while (newemail == null || !newemail.contains("@")|| newemail.isEmpty()) {
                                    newemail = JOptionPane.showInputDialog(null, "Invalid email. Please enter a valid email with '@':");
                                }
                                AppendCSV(user.getEmail(),5,newemail, user.getUniqID());
                                user.setEmail(newemail);
                            }else if(changeUserDetails.equals("6")){
                                String newphone = JOptionPane.showInputDialog(null, "Enter your phone number");
                                while (newphone == null || newphone.isEmpty()||!newphone.matches("\\d{10}")) {
                                    newphone = JOptionPane.showInputDialog(null, "Invalid phone number. Please enter a valid phone number with 10 digits");
                                }
                                long phonenum = Long.parseLong(newphone);
                                String origphone=Long.toString(user.getPhone());
                                AppendCSV(origphone,7,newphone, user.getUniqID());
                                user.setPhone(phonenum);
                            }else if(changeUserDetails.equals("7")){
                                exit=false;
                            }
                        } else if (MenuOptions.equals("4")) {
                            exit=true;
                            break;
                        }else{
                            JOptionPane.showMessageDialog(null, "Please select a valid option");
                        }


                        }
                        String signout=JOptionPane.showInputDialog(null,"[1] proceed to the sign out screen, any other number to continue");
                        if(signout.equals("1")) {
                            valid = false;
                            break;

                        }








                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Input");
                }

            }

            String exitprogram=JOptionPane.showInputDialog(null,"[-1] sign out and exit program, any other number to sign out");
                if(exitprogram.equals("-1")) {
                    rerun=-1;
                    break;
                }else{
                    rerun=1;
                }
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

    // Revised genid() method to generate a 6-digit ID as an integer
    static int genid() {
        StringBuilder id = new StringBuilder(); // Used chatgpt to figure this out I didn't know that you can't append ints
        Random rand = new Random();
        for (int i = 0; i < 6; i++) {
            int randnum = rand.nextInt(10); // Generates numbers between 0 and 9
            id.append(randnum);
        }
        return Integer.parseInt(id.toString()); // Converts the generated string to an integer
    }
    public static String existinguser(String username) throws FileNotFoundException { //to make sure two users cannot have the same username when signing up or changing username
        Scanner csvreader = new Scanner(new File("src/UserData.csv"));
        boolean userexists = false;
        while (csvreader.hasNextLine()) {
            String line = csvreader.nextLine().trim();
            if (!line.isEmpty()) {  // Skip empty lines
                String[] UserDatacopier = line.split(",");
                if (UserDatacopier[0].equals(username)) {
                    userexists = true;
                    break;
                }else{
                    userexists = false;
                }
            }
        }
        if (userexists) {
            return "already exists";
        }else{
            return "does not exists";
        }



    }

    // to add new user data
    public static void addUser(String username, String password, String firstname, String lastname, int ssn, String dob, String email, long phone, double minimumdeposit, int ID) throws FileNotFoundException {
        // Scanner to read existing CSV
        Scanner csvreader = new Scanner(new File("src/UserData.csv"));
        File tempFile = new File("src/temp.csv"); // I originally had this as a file manually added in the beginning but I realized it would be better to create this in the method and delete the UserData that we had before
        // PrintWriter to write to temp.csv
        PrintWriter out = new PrintWriter(new File("src/temp.csv"));

        // Copy existing data from UserData.csv to temp.csv
        while (csvreader.hasNextLine()) {
            String line = csvreader.nextLine().trim();
            String[] UserDatacopier = line.split(",");

            for (int i = 0; i < UserDatacopier.length; i++) {
                UserDatacopier[i] = UserDatacopier[i].trim();
            }

            // Write the formatted data back to temp.csv
            out.println(String.join(",", UserDatacopier));
        }

        // From video referenced in the beginning, now including the unique ID as the 10th field
        out.println(String.join(",", username, password, firstname, lastname, Integer.toString(ssn), dob, email, Long.toString(phone), Double.toString(minimumdeposit), Integer.toString(ID)));

        // Close resources since they wont be used unless the method is called
        csvreader.close();
        out.close();
        File userDataFile = new File("src/UserData.csv");
        userDataFile.delete();
        tempFile.renameTo(new File("src/UserData.csv"));
    }

    // to replace user data
    public static void AppendCSV(String Keyword, int Index, String replacement,int UniqID) throws FileNotFoundException {
        Scanner csvreader = new Scanner(new File("src/UserData.csv"));
        String UniqueID = Integer.toString(UniqID);
        File tempFile = new File("src/temp.csv");
        PrintWriter out = new PrintWriter(tempFile);

        // Copy existing data from UserData.csv to temp.csv
        while (csvreader.hasNextLine()) {
            String line = csvreader.nextLine().trim();
            if (!line.isEmpty()) {  // Skip empty lines
                String[] UserDatacopier = line.split(",");

                // Trim each column value
                for (int i = 0; i < UserDatacopier.length; i++) {
                    UserDatacopier[i] = UserDatacopier[i].trim();
                    if (UserDatacopier[Index].equals(Keyword) && UserDatacopier[9].equals(UniqueID)) { // Replace the old at the index if it matches the keyword
                        UserDatacopier[Index] = replacement;
                    }
                }

                out.println(String.join(",", UserDatacopier)); // Write to the new file

            }
        }

        // Close resources
        csvreader.close();
        out.close();

        File originalFile = new File("src/UserData.csv");
        originalFile.delete(); // Delete the original file
        tempFile.renameTo(new File("src/UserData.csv")); // Rename the temporary file
    }

    // this returns the user info based on the input username
    public static UserData users(String username) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("src/UserData.csv"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] userArray = line.split(",");
            if (userArray[0].equals(username)) {
                // Parse the user data to create a UserData object
                return new UserData(userArray[0], userArray[1], userArray[2], userArray[3],
                        Integer.parseInt(userArray[4]), userArray[5], userArray[6], Long.parseLong(userArray[7]),
                        Double.parseDouble(userArray[8]), Integer.parseInt(userArray[9]));
            }
        }
        return null; // return null if the user is not found
    }
}

class UserData {
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private int ssn;
    private String dob;
    private String email;
    private long phone;
    private int minimumdeposit;
    private int uniqID;

    UserData() {
        this.username = "";
        this.password = "";
        this.firstname = "";
        this.lastname = "";
        this.ssn = 0;
        this.dob = "";
        this.email = "";
        this.phone = 0;
        this.minimumdeposit = 0;
    }

    UserData(String username, String password, String firstname, String lastname, int ssn, String dob, String email, long phone, double minimumdeposit, int uniqID) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.ssn = ssn;
        this.dob = dob;
        this.email = email;
        this.phone = phone;
        this.uniqID = uniqID;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public int getSsn() {
        return ssn;
    }
    public void setSsn(int ssn) {
        this.ssn = ssn;
    }
    public String getDob() {
        return dob;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public long getPhone() {
        return phone;
    }
    public void setPhone(long phone) {
        this.phone = phone;
    }
    public int getMinimumdeposit() {
        return minimumdeposit;
    }
    public void setMinimumdeposit(int minimumdeposit) {
        this.minimumdeposit = minimumdeposit;
    }
    public int getUniqID() {
        return uniqID;
    }
}




//from GABBY AND ANT CREDIT CARD
class CreditCard {
    public String cardNumber;
    public String cardType;
    public String cvv;
    public String expirationDate;
    public double balance;
    public double creditLimit;
    public double outstandingBalance;
    public int creditScore;
    public double monthlySpending;
    public static final double TRANSFER_FEE = 5.0;//constnt 5 dollar fee for transfers

    public CreditCard(String cardNumber, String cardType, String cvv, String expirationDate, double creditLimit) {
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.cvv = cvv;
        this.expirationDate = expirationDate;
        this.balance = creditLimit;
        this.creditLimit = creditLimit;
        this.outstandingBalance = 0;
        this.creditScore = 700;// automatically 700, goes up or goes down
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
            System.out.println("Error saving data: " + e.getMessage());// idk chatgpt helped w this part, saving to csv file and updating it
        }
    }
}




//From Rohan and WILL (debit card)
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


//Ostap Aaron MJ