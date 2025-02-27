import javax.swing.*;// FOR THE GUI'S ( I learned this last year during my create task for CSP)
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;// for unique ID
import java.util.Scanner; //to read the CSV file
//https://www.youtube.com/watch?v=-95U3CZPlE8 referenced for reading the CSV FILE

//https://www.youtube.com/watch?v=TpyRKom0X_s to edit certain parts of CSV TO DO!

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class USERINTERFACE {
    public static void main(String[] args) throws FileNotFoundException { //FileNotFoundException happens if file isnt in the src


        

        int rerun=-3;
        while (rerun!=-1){//so user can return to welcome page
            Boolean valid=false;
            while(!valid){// User must have valid response so he doesnt glitch the code
                String signinorsignup = JOptionPane.showInputDialog(null,"Welcome Would you like to sign in or sign up?");
            signinorsignup.toLowerCase();
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


                valid =true;
            }else if (signinorsignup.equalsIgnoreCase("SignIn")){ // STILL IN WORK GOTTA DO CSV FILE FIRST
                String username=JOptionPane.showInputDialog(null, "Welcome To the bank Please enter your Username" );
                String password=JOptionPane.showInputDialog(null,"HEllO "+username +" Please enter your Password");
                valid =true;
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

}
class UserData {
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private int ssn;
    private String dob;
    private String email;
    private int phone;
    private int minimumdeposit;

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

    UserData(String username, String password, String firstname, String lastname, int ssn, String dob, String email, int phone, double minimumdeposit) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.ssn = ssn;
        this.dob = dob;
        this.email = email;
        this.phone = phone;

    }


}