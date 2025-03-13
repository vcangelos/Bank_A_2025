import java.util.ArrayList;
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        CDinterface CDI= new CDinterface();
        ArrayList<CD> cds= new ArrayList<CD>();
        cds=CDI.generateRandomCDs(25);
        CDI.addCDs(cds);
        CDI.displayCD();

    }
}
