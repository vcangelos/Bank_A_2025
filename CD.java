import java.util.Calendar; 
import java.util.Date; 
import java.util.Scanner;
import java.util.ArrayList;
public class CD {
    
       
    
    int term;
    double Principal;
    double IR;
    public CD() {
        int term=0;
        double Principal=0.0;
        double IR=0.0;

    }
    public CD(int t,double p,double ir) {
        int term=t;
        double Principal=p;
        double IR=ir;

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
            System.out.println("-------Option "+CDoptions.indexOf(c)+"-------");
            System.out.println("Interest Rate: "+c.getIR()+"%");
            System.out.println("Term: "+c.getTerm()+" year(s)");


        }

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

}