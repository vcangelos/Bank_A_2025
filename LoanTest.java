import java.text.DecimalFormat; 
import java.io.BufferedReader; 
import java.io.FileReader; 
import java.io.IOException; 
import java.io.BufferedWriter; 
import java.io.FileWriter; 
import java.util.ArrayList; 
import java.util.List; 

public class LoanTest {
    public static void main(String[] args) {
        String csvFile = "loans.csv"; 
        String line; 
        String splitBy = ","; 

        // Create BankProject instance
        BankProject bankProject = new BankProject();
        BankAccount bankAccount = new BankAccount(0); 
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) { 
            while ((line = br.readLine()) != null) { 
                String[] data = line.split(splitBy); 
                String loanType = data[0].trim(); 
                String borrowerName = data[1].trim(); 
                double loanAmount = Double.parseDouble(data[2].trim());
                int duration = Integer.parseInt(data[3].trim()); 

                Loan loan; 
                switch (loanType.toLowerCase()) { 
                    case "medical":
                        loan = new MedicalLoan(borrowerName, loanAmount, duration); 
                        break;
                    case "business":
                        loan = new BusinessLoan(borrowerName, loanAmount, duration); 
                        break;
                    default:
                        System.out.println("Invalid loan type: " + loanType); 
                        continue; 
                }

                loan.displayLoanDetails();
                System.out.println("-----------"); 

                bankProject.addChecklistItem("Processed loan for " + borrowerName + " of type " + loanType);
            }
            
            System.out.println("\nUpdated Checklist:");
            bankProject.displayChecklist();
            System.out.println("\nBankProject Details: " + bankProject);

            // Example usage of CreditCard class
            CreditCard myCard = new CreditCard("1234-5678-9876-5432", "Visa", "12/25", 5000.0);
            System.out.println("\nCreditCard Details: " + myCard);

            // Example usage of BankAccount
            bankAccount.deposit(1000); // Deposit $1000
            bankAccount.withdraw(200);  // Withdraw $200
            System.out.printf("Final Balance: $%.2f%n", bankAccount.getBalance()); 

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("bank_project.csv"))) {
                writer.write("\"BankProject Checklist\"\n");
                writer.write("\"Item Number\",\"Checklist Item\"\n");
                for (int i = 0; i < bankProject.getChecklist().size(); i++) {
                    writer.write("\"" + (i + 1) + "\",\"" + bankProject.getChecklist().get(i) + "\"\n");
                }

                writer.write("\n\"CreditCard Details\"\n");
                writer.write("\"Card Type\",\"Card Number\",\"Expiration Date\",\"Credit Limit\",\"Outstanding Balance\",\"Credit Score\"\n");
                writer.write("\"" + myCard.getCardType() + "\",\"" + myCard.maskCardNumber() + "\",\"" + myCard.getExpirationDate() + "\",\"" + myCard.getCreditLimit() + "\",\"" + myCard.getOutstandingBalance() + "\",\"" + myCard.getCreditScore() + "\"\n");
                System.out.println("CSV file created successfully.");
            } catch (IOException e) {
                System.out.println("Error writing to CSV: " + e.getMessage());
            }
        } catch (IOException e) { 
            System.out.println("Error reading loans file: " + e.getMessage());
        }
    }
}
