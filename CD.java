import java.util.Calendar; 
import java.util.Date; 
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
public class CD {
    
       
    
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