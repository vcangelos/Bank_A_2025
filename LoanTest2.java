import java.text.DecimalFormat;
import java.io.BufferedReader; 
import java.io.FileReader; 
import java.io.IOException; 

public class LoanTest2 {
    public static void main(String[] args) {
        String csvFile = "loans2.csv"; 
        processLoans(csvFile);
    }

    public static void processLoans(String csvFile) {
        String line;
        String splitBy = ","; 

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) { 
            while ((line = br.readLine()) != null) { 
                processLoanLine(line, splitBy);
            }
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }

    public static void processLoanLine(String line, String splitBy) {
        String[] data = line.split(splitBy); 
        String loanType = data[0].trim(); 
        String borrowerName = data[1].trim(); 
        double loanAmount = Double.parseDouble(data[2].trim()); 
        int duration = Integer.parseInt(data[3].trim()); 

        Loan2 loan;
        switch (loanType.toLowerCase()) { 
            case "medical":
                loan = new MedicalLoan(borrowerName, loanAmount, duration); 
                break;
            case "business":
                loan = new BusinessLoan(borrowerName, loanAmount, duration); 
                break;
            default:
                System.out.println("Invalid loan type: " + loanType); 
                return; 
        }

        loan.displayLoanDetails(); 
        System.out.println("-----------"); 
    }
}
