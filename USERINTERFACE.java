import javax.swing.*; // FOR THE GUI'S (I learned this last year during my create task for CSP)
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random; // for unique ID
import java.util.Scanner; //to read the CSV file
//https://www.youtube.com/watch?v=-95U3CZPlE8 referenced for reading the CSV FILE

//https://www.youtube.com/watch?v=TpyRKom0X_s to edit certain parts of CSV TO DO!

//To DO:
//user options after the sign ins like to change userinfo using the verification, basically done need to connect others first and create the options list
//logging out ability



//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

public class Main {
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
                    String password = JOptionPane.showInputDialog(null, "HEllO " + username + " Please enter your Password");

                    String firstname = JOptionPane.showInputDialog(null, username + " Please enter your First Name");
                    String lastname = JOptionPane.showInputDialog(null, username + " Please enter your Last Name");

                    String SSNinput = JOptionPane.showInputDialog("Enter your Social Security number, please do not put -");
                    while (SSNinput.contains("-")) {
                        SSNinput = JOptionPane.showInputDialog("Enter your Social Security number again, please do not put -");
                    }
                    int SSN = Integer.parseInt(SSNinput);
                    String DOB = JOptionPane.showInputDialog(null, "Enter your DOB");

                    String email = JOptionPane.showInputDialog(null, "Enter your email");
                    String phone = JOptionPane.showInputDialog(null, "Enter your phone number");
                    long phonenum = Long.parseLong(phone);

                    double minimumdeposit = 0;
                    double validminimumdepositvalue = 25;
                    while (minimumdeposit < validminimumdepositvalue) {
                        String minimumdeppositinput = JOptionPane.showInputDialog(null, "Enter your minimum deposit please note your minimum deposit value has to be greater than $25");
                        minimumdeposit = Double.parseDouble(minimumdeppositinput);
                    }
                    int ID = genid();
                    addUser(username,password,firstname,lastname,SSN,DOB,email,phonenum,minimumdeposit,ID);

                    valid = true;
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
                                AppendCSV(truePassword, 1, newpassword); // Append new password to CSV or save securely
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


                    valid = true;
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Input");
                }
            }
        }
    }

    static int genid() {
        StringBuilder id = new StringBuilder(); // Used chatgpt to figure this out I didn't know that you can't append ints
        Random rand = new Random();
        for (int i = 0; i < 2; i++) {
            int randnum = rand.nextInt(10); // Generates numbers between 0 and 9
            id.append(randnum);
        }
        return id.toString().hashCode(); // Chatgpt by debugging, so it's readable
    }

    // to add new user data
    public static void addUser(String username, String password, String firstname, String lastname, int ssn, String dob, String email, long phone, double minimumdeposit,int ID) throws FileNotFoundException {
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

        // From video referenced in the beginning
        out.println(String.join(",", username, password, firstname, lastname, Integer.toString(ssn), dob, email, Long.toString(phone), Double.toString(minimumdeposit)));

        // Close resources since they wont be used unless the method is called
        csvreader.close();
        out.close();
        File userDataFile = new File("src/UserData.csv");
        userDataFile.delete();
        tempFile.renameTo(new File("src/UserData.csv"));
    }

    // to replace user data
    public static void AppendCSV(String Keyword, int Index, String replacement) throws FileNotFoundException {
        Scanner csvreader = new Scanner(new File("src/UserData.csv"));
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
                    if (UserDatacopier[Index].equals(Keyword)) { // Replace the old at the index if it matches the keyword
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

    UserData(String username, String password, String firstname, String lastname, int ssn, String dob, String email, long phone, double minimumdeposit,int uniqID) {
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
