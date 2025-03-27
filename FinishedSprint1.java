
import java.io.*;
import java.util.*;
import java.util.Random; // for unique ID
import java.util.Scanner; //to read the CSV file
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

//https://www.youtube.com/watch?v=-95U3CZPlE8 referenced for reading the CSV FILE

//https://www.youtube.com/watch?v=TpyRKom0X_s to edit certain parts of CSV TO DO!

//TO DO let user change user options

public class FinishedSprint1 {
    public static void main(String[] args) throws FileNotFoundException {
         Scanner inputScanner = new Scanner(System.in);
        int rerun = -3;
        String username="";
        String password="";
        String firstname="";
        String lastname="";
        String email="";
        String SSNinput="";
        int ID=0;
        String uniqID="";
        String DOB;
        double minimumdeposit = 0;
        long phonenum=0;
        UserData user=new UserData();

        while (rerun != -1) { // so user can return to welcome page
            Boolean valid = false;
            while (!valid) { // User must have valid response so he doesnt glitch the code
                System.out.println("Welcome! Would you like to sign in or sign up?");
                String signinorsignup = inputScanner.nextLine().toLowerCase().replace(" ", ""); // replaces spaces, e.g., "Sign in" becomes "Signin" (edge-casing)

                if (signinorsignup.equalsIgnoreCase("Signup")) {
                    System.out.print("Welcome to the bank! Please enter your Username: ");
                     username = inputScanner.nextLine();
                    String usernameexists = existinguser(username);

                    while (username == null || username.isEmpty() || usernameexists.equals("already exists")) { // isEmpty so it doesn’t edge-case and return a value
                        if (usernameexists.equals("already exists")) {
                            System.out.print("Username is already taken. Please enter a different Username: ");
                        } else {
                            System.out.print("Username cannot be empty. Please enter your Username: ");
                        }
                        username = inputScanner.nextLine();
                        usernameexists = existinguser(username);
                    }

                    System.out.print("Hello " + username + ", please enter your Password: ");
                     password = inputScanner.nextLine();
                    while (password == null || password.isEmpty()) {
                        System.out.print("Password cannot be empty. Please enter your Password: ");
                        password = inputScanner.nextLine();
                    }

                    System.out.print(username + ", please enter your First Name: ");
                     firstname = inputScanner.nextLine();
                    while (firstname == null || firstname.isEmpty()) {
                        System.out.print("First Name cannot be empty. Please enter your First Name: ");
                        firstname = inputScanner.nextLine();
                    }

                    System.out.print(username + ", please enter your Last Name: ");
                     lastname = inputScanner.nextLine();
                    while (lastname == null || lastname.isEmpty()) {
                        System.out.print("Last Name cannot be empty. Please enter your Last Name: ");
                        lastname = inputScanner.nextLine();
                    }

                    System.out.print("Enter your Social Security number (9 digits, no dashes): ");
                     SSNinput = inputScanner.nextLine();

                    // Ensure the SSN is exactly 9 digits and contains only numbers
                    while (SSNinput == null || !SSNinput.matches("\\d{9}") || SSNinput.isEmpty()) {
                        System.out.print("Invalid SSN. Please enter exactly 9 digits, no dashes: ");
                        SSNinput = inputScanner.nextLine();
                    }

                    int SSN = Integer.parseInt(SSNinput);

                    System.out.print("Enter your DOB (MMDDYYYY, no slashes): ");
                     DOB = inputScanner.nextLine();
                    while (DOB == null || DOB.isEmpty() || !DOB.matches("\\d{8}")) {
                        System.out.print("Invalid Date of Birth. Please enter exactly 8 digits, no slashes: ");
                        DOB = inputScanner.nextLine();
                    }

                    System.out.print("Enter your email: ");
                     email = inputScanner.nextLine();
                    while (email == null || !email.contains("@") || email.isEmpty()) {
                        System.out.print("Invalid email. Please enter a valid email with '@': ");
                        email = inputScanner.nextLine();
                    }

                    System.out.print("Enter your phone number: ");
                    String phone = inputScanner.nextLine();
                    while (phone == null || phone.isEmpty() || !phone.matches("\\d{10}")) {
                        System.out.print("Invalid phone number. Please enter a valid phone number with 10 digits: ");
                        phone = inputScanner.nextLine();
                    }
                     phonenum = Long.parseLong(phone);

                     minimumdeposit = 0;
                    double validminimumdepositvalue = 25;
                    while (minimumdeposit < validminimumdepositvalue) {
                        System.out.print("Enter your minimum deposit (must be at least $25): ");
                        String depositInput = inputScanner.nextLine();

                        // Check if the input is blank
                        if (depositInput.isEmpty()) {
                            System.out.println("Deposit cannot be blank. Please enter a valid amount.");
                        } else {
                            // Check if the input can be parsed as a valid number and meets the minimum deposit
                            if (depositInput.matches("\\d+(\\.\\d+)?")) { // checks if it's a valid number
                                minimumdeposit = Double.parseDouble(depositInput);
                                if (minimumdeposit < validminimumdepositvalue) {
                                    System.out.println("Deposit must be at least $25. Please try again.");
                                }
                            } else {
                                System.out.println("Invalid input. Please enter a valid numeric value.");
                            }
                        }
                    }
                     ID = genid();
                     uniqID = String.valueOf(ID);
                     user.setUsername(username);
                     user.setPassword(password);
                     user.setFirstname(firstname);
                     user.setLastname(lastname);
                     user.setEmail(email);
                     user.setSsn(SSN);
                     user.setPhone(phonenum);
                     user.setMinimumdeposit(minimumdeposit);
                     user.setDob(DOB);
                     user.setID(ID);

                    addUser(username, password, firstname, lastname, SSN, DOB, email, phonenum, minimumdeposit, ID);

                    //USER CHOICE GOES HERE
                    valid = true;


                } else if (signinorsignup.equalsIgnoreCase("SignIn")) { // STILL IN WORK GOTTA DO CSV FILE FIRST
                    System.out.print("Welcome To the bank Please enter your Username: ");
                     username = inputScanner.nextLine();

                    // Assuming users() is a method that retrieves user data based on the username
                     user = users(username);

                    // While loop to check if the username is found and keeps going so you cant break it
                    while (user == null) {
                        System.out.print("Your username was not found. Please enter your Username: ");
                        username = inputScanner.nextLine();
                        user = users(username);
                    }

                    System.out.print("Hello " + username + ", Please enter your Password: ");
                     password = inputScanner.nextLine();
                    String truePassword = user.getPassword();

                    // Remove spaces from entered password
                    String forgotpassword = password;
                    forgotpassword = forgotpassword.replace(" ", "");

                    int passwordmatch = -1;
                    while (passwordmatch < 0) {
                        if (truePassword.equals(password)) {
                            // Password matches, welcome the user
                            System.out.println("Welcome To the bank, " + user.getFirstname() + "!");
                            passwordmatch = 1; // Exit the loop
                        } else if (forgotpassword.equalsIgnoreCase("forgotpassword")) {
                            // User forgot password, proceed to reset
                            System.out.print("Please enter your Social Security number without dashes to reset your password: ");
                            String recoveracct = inputScanner.nextLine();


                            // Ensure SSN has no dashes before proceeding
                            while (recoveracct.contains("-")) {
                                System.out.print("Dashes are not allowed. Please enter your Social Security number again: ");
                                recoveracct = inputScanner.nextLine();
                            }

                            int ssn = user.getSsn(); // Get the user's SSN
                            String SSNtoString = Integer.toString(ssn);
                            boolean SSNmatch = false;

                            // Prompt for SSN until the correct one is entered
                            while (!SSNmatch) {
                                if (recoveracct.equals(SSNtoString)) {
                                    // SSN matches, ask for new password
                                    System.out.print("Enter your new password: ");
                                    String newpassword = inputScanner.nextLine();
                                    user.setPassword(newpassword); // Sets it as new password
                                    System.out.println( "Password reset successfully!");
                                    SSNmatch = true; // Successfully reset password, exit the loop
                                    AppendCSV(truePassword, 1, newpassword,user.getUniqID()); // Append new password to CSV or save securely
                                    passwordmatch = 1; // Exit the outer loop since password was reset
                                } else {
                                    // SSN doesn't match, prompt again
                                    System.out.print("Your Social Security number did not match. Please enter it again: ");
                                    recoveracct = inputScanner.nextLine();

                                    // Ensure no dashes in SSN input again
                                    while (recoveracct.contains("-")) {
                                       System.out.print("Dashes are not allowed. Please enter your Social Security number again: ");
                                       recoveracct = inputScanner.nextLine();
                                    }
                                }
                            }
                        } else {
                            // If password is incorrect and "forgotpassword" was not entered, prompt again
                            System.out.print("Incorrect password. Please try again: ");
                            password = inputScanner.nextLine();
                            forgotpassword = password.replace(" ", "");
                        }

                    }
                    System.out.println( "Welcome back "+user.getFirstname()+" "+user.getLastname());
                    valid=true;




                } else {
                    System.out.println( "Invalid Input");
                }

            }
            System.out.println("OUT OF LOOP");
            System.out.println(username);
            boolean exit=false;
            while (exit==false) {
                        System.out.println("Please choose an option: ");
                        System.out.println("[1] Access Credit Card");
                        System.out.println("[2] Access Debit Card");
                        System.out.println("[3] Access CD");
                        System.out.println("[4] Start a money transfer");
                        String MenuOptions = inputScanner.nextLine();
                        if (MenuOptions.equals("1")) {
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
                        }else if (MenuOptions.equals("2")) {
                            System.out.print("Not in github ");
                        }else if (MenuOptions.equals("3")) {
                            System.out.print("In Progress: ");
                        }else if (MenuOptions.equals("4")) {
                            System.out.print("I have no clue on that group  ");
                        }


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
    private double minimumdeposit;
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
    public double getMinimumdeposit() {
        return minimumdeposit;
    }
    public void setMinimumdeposit(double minimumdeposit) {
        this.minimumdeposit = minimumdeposit;
    }
    public int getUniqID() {
        return uniqID;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }
    public void setID(int uniqID) {
        this.uniqID = uniqID;
    }
}




//from GABBY AND ANT CREDIT CARD
class CreditCard {
   public String cardNumber;
   private String cardType;
   private String cvv;
   private String expirationDate;
   private double balance;
   private double creditLimit;
   public double outstandingBalance;
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


//GABE AND LANDON


class CD {


    int term;
    double Principal;
    double IR;
    public CD() {
        this.term=0;
        this.Principal=0.0;
        this.IR=0.0;

    }
    public CD(int t,double p,double ir) {
        this.term=t;
        this.Principal=p;
        this.IR=ir;


    }
    public int getTerm(){
        return this.term;
    }
    public double getIR(){
        return this.IR;
    }




}
class CDinterface {
    private static ArrayList<CD> CDoptions= new ArrayList<CD>();

    public static void addCD(CD c){
        CDoptions.add(c);

    }
    public static void removeCD(CD c){
        CDoptions.remove(c);

    }
    public static void displayCD(){
        for (CD c : CDoptions){
            System.out.println("-------Option "+(CDoptions.indexOf(c)+1)+"-------");
            System.out.println("Interest Rate: "+c.getIR()+"%");
            System.out.println("Term: "+c.getTerm()+" month(s)");


        }
        System.out.println("Do any of these options appeal to you? (y/n)");

    }
    public static void PurchaseCD(){

    }
    public static void CheckCD(){

    }
    public static void WithdrawCD(){

    }
    public static void offerSelections(){
        System.out.println("Select a CD option:");
    }
    public static void addCDs(ArrayList<CD> cds)
    {
        CDoptions.addAll(cds);
    }
    private static double round(double value, int places)
    {
        double factor = Math.pow(10, places);
        return Math.round(value * factor) / factor;
    }
    public static ArrayList<CD> generateRandomCDs(int x)
    {
        ArrayList<CD> newCDs = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < x; i++)
        {
            int term = rand.nextInt(24) + 1; // Random term between 1-10 years
            double principal = 500 + (5000 - 500) * rand.nextDouble(); // Random principal between $500 and $5000
            double interestRate = 1 + (5 - 1) * rand.nextDouble(); // Random interest rate between 1% and 5%
            interestRate = round(interestRate, 2);

            CD cd = new CD(term, principal, interestRate);
            newCDs.add(cd);
        }
        return newCDs;
    }

}
