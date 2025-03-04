import javax.swing.*;// FOR THE GUI'S ( I learned this last year during my create task for CSP)
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;// for unique ID

import java.util.Scanner; //to read the CSV file
//https://www.youtube.com/watch?v=-95U3CZPlE8 referenced for reading the CSV FILE

//https://www.youtube.com/watch?v=TpyRKom0X_s to edit certain parts of CSV TO DO!




//To DO:
//user options after the sign ins
//logging out
//forgot password verification 
//to change userinfo using the verification
//signing out input( it already  brings the user back to welcome just adding the option for the user to return manually instead of automatically)




//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws FileNotFoundException { //FileNotFoundException happens if file isnt in the src
        
        int rerun=-3;
        while (rerun!=-1){//so user can return to welcome page
            Boolean valid=false;
            while(!valid){// User must have valid response so he doesnt glitch the code
                String signinorsignup = JOptionPane.showInputDialog(null,"Welcome Would you like to sign in or sign up?");
            signinorsignup.toLowerCase();
            signinorsignup = signinorsignup.replace(" ","");// replaces spaces ex: Sign in becomes Signin(edgecasing)
            if(signinorsignup.equalsIgnoreCase("Signup")){

                String username=JOptionPane.showInputDialog(null, "Welcome To the bank Please enter your Username" );
                String password=JOptionPane.showInputDialog(null,"HEllO "+username +" Please enter your Password");

                String firstname=JOptionPane.showInputDialog(null,username +" Please enter your First Name");
                String lastname=JOptionPane.showInputDialog(null,username +" Please enter your Last Name");

                String SSNinput=JOptionPane.showInputDialog("Enter your Social Security number, please do not put -");
                while(SSNinput.contains("-")){
                     SSNinput=JOptionPane.showInputDialog("Enter your Social Security number again, please do not put -");

                }
                int SSN = Integer.parseInt(SSNinput);
                String DOB=JOptionPane.showInputDialog(null,"Enter your DOB");

                String email=JOptionPane.showInputDialog(null,"Enter your email");
                String phone=JOptionPane.showInputDialog(null,"Enter your phone number");


                double minimumdeposit=0;
                double validminimumdepositvalue=25;
                while(minimumdeposit<validminimumdepositvalue){
                    String minimumdeppositinput = JOptionPane.showInputDialog(null,"Enter your minimum deposit please note your minimum deposit value has to be greater than $25");
                    minimumdeposit=Double.parseDouble(minimumdeppositinput);

                }
                int ID=genid();


                valid =true;
            }else if (signinorsignup.equalsIgnoreCase("SignIn")){ // STILL IN WORK GOTTA DO CSV FILE FIRST
                String username = JOptionPane.showInputDialog(null, "Welcome To the bank Please enter your Username");

    // Assuming users() is a method that retrieves user data based on the username
                UserData user = users(username);

                // While loop to check if the username is found and keeps going so you cant break it
                while(user == null){
                    username = JOptionPane.showInputDialog(null, "Your username was not found. Please enter your Username");
                    user = users(username); // Fetch user data based on entered username
                }


                    String password = JOptionPane.showInputDialog(null,"Hello " + username + " Please enter your Password");
                    String truePassword = user.getPassword(); // Fetch stored password for comparison

                    // Remove spaces from entered password
                    String forgotpassword=password;
                    forgotpassword.replace(" ","");

                    int passwordmatch = -1;
                    while (passwordmatch < 0){
                        if(truePassword.equals(password)){
                            JOptionPane.showMessageDialog(null,"Welcome To the bank, " + user.getFirstname() + "!");
                            passwordmatch = 1;  //exits the loop
                        }else if(forgotpassword.equalsIgnoreCase("forgotpassword")){
                            String choice = JOptionPane.showInputDialog(null,"Still workingon it");


                        }else{
                            JOptionPane.showMessageDialog(null,"Wrong Password please try again or say forgotpassword");
                             password = JOptionPane.showInputDialog(null,"Hello " + username + " Please enter your Password");
                             forgotpassword=password;
                             forgotpassword=forgotpassword.replace(" ","");
                        }
                    }

                    valid = true;
            }else{
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
    //to add new user data
        public static void addUser(String username, String password, String firstname, String lastname, int ssn, String dob, String email, long phone, double minimumdeposit) throws FileNotFoundException {
            // Scanner to read existing CSV
            Scanner csvreader = new Scanner(new File("src/UserData.csv"));
            File tempFile = new File("src/temp.csv");// I orignally had this as a file manually added in the beginning  but I realized it would be better to create this in the method and delete the UserData that we had before
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

            // From video referenced in beginning
            out.println(String.join(",", username, password, firstname, lastname, Integer.toString(ssn), dob, email, Long.toString(phone), Double.toString(minimumdeposit)));

            // Close resources since they wont be used unless the method is called
            csvreader.close();
            out.close();
            File userDataFile = new File("src/UserData.csv");
            userDataFile.delete();
            tempFile.renameTo(new File("src/UserData.csv"));



        }


        // to replace user data
        public static void AppendCSV( String Keyword, int Index, String replacement) throws FileNotFoundException {
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
                        if (UserDatacopier[Index].equals(Keyword)) {// Replace the old at the  index if it matches the keyword
                        UserDatacopier[Index] = replacement;
                        }
                    }




                        // Write the updated data back to temp.csv
                    out.println(String.join(",", UserDatacopier));// writes every line to the tempfile created

                }
            }

            // Close resources
            csvreader.close();
            out.close();

            // Replace the original file with the updated one
            File originalFile = new File("src/UserData.csv");

        }



        //for sign ins to retrive user info
        public static UserData users(String keyword) throws FileNotFoundException {
            Scanner csvreader = new Scanner(new File("src/userData.csv"));
            if (csvreader.hasNextLine()) {
                csvreader.nextLine(); // Skip header row
            }

            UserData user = null;

            while (csvreader.hasNextLine()) {
                String line = csvreader.nextLine().trim();
                String[] UserDataImport = line.split(",");

                // Trim all values in the array
                for (int i = 0; i < UserDataImport.length; i++) {
                    UserDataImport[i] = UserDataImport[i].trim();
                }

                // Directly create a UserData object when a match is found
                if (UserDataImport[0].equals(keyword)) {
                    int ssn = Integer.parseInt(UserDataImport[4]); // SSN
                    long phone = Long.parseLong(UserDataImport[7]); // Phone number
                    double minimumDeposit = Double.parseDouble(UserDataImport[8]); // Minimum deposit
                    int uniqueID = Integer.parseInt(UserDataImport[9]);

                    // Creates new object,
                    user = new UserData(UserDataImport[0], UserDataImport[1], UserDataImport[2], UserDataImport[3], ssn, UserDataImport[5], UserDataImport[6], phone, minimumDeposit,uniqueID);

                    break; //  user is found
                }
            }

            csvreader.close();
            return user; // Returns  UserData object or null
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
