package moneytransfer;
import java.util.Scanner;
    
public class moneytransfer 
{
	public static void main(String[] args)
	{
		boolean accselected = false;
		String checkorsave = null;
		Scanner scan = new Scanner(System.in);
		System.out.print("What account would you like to transfer from, checking [1] or savings [2]? ");
		while (accselected == false)
		{
			String transAcc = scan.nextLine();
			if (transAcc.equalsIgnoreCase("1") || transAcc.equalsIgnoreCase("2"))
			{
				accselected = true;
				
				if (transAcc.equalsIgnoreCase("1"))
				{
					// pull from checking
					System.out.println("You selected checking.");
					checkorsave = "checking";
				}
				
				if (transAcc.equalsIgnoreCase("2"))
				{
					// pull from savings
					System.out.println("You selected savings.");
					checkorsave = "savings";
				}
			}
			else 
			{
				System.out.println("Invalid input. Please type the number that corresponds to the correct account.");
			}
		}
		
		String accNum;
		boolean validAcc= false;
		while (!validAcc) 
		{
	            System.out.println("Please enter your 12-digit account number:");
	            System.out.print("#");
	            accNum = scan.nextLine();

	            if (accNum.matches("\\d{12}")) 
	            {
	                System.out.println("Account number is valid.");
	                validAcc = true;
	            } else 
	            {
	                System.out.println("Invalid account number. Please enter a valid 12-digit account number.");
	            }
	    }
		
		String routeNum;
		boolean validRoute = false;
		while (!validRoute) 
		{
	            System.out.println("Please enter your 9-digit routing number:");
	            System.out.print("#");
	            routeNum = scan.nextLine();

	            if (routeNum.matches("\\d{9}")) 
	            {
	                System.out.println("Routing number is valid.");
	                validRoute = true;
	            } else 
	            {
	                System.out.println("Invalid routing number. Please enter a valid 9-digit routing number.");
	            }
	    }
		
		System.out.println("Please enter your billing address:");

        // Street address
        System.out.print("Street Address: ");
        String streetAddress = scan.nextLine();

        // City
        System.out.print("City: ");
        String city = scan.nextLine();

        // State
        System.out.print("State: ");
        String state = scan.nextLine();

        // Zip code
        System.out.print("Zip Code: ");
        String zipCode = scan.nextLine();

        // Country
        System.out.print("Country: ");
        String country = scan.nextLine();

        // Display the collected billing address
        System.out.println("\nBilling Address:");
        System.out.println("Street Address: " + streetAddress);
        System.out.println("City: " + city);
        System.out.println("State: " + state);
        System.out.println("Zip Code: " + zipCode);
        System.out.println("Country: " + country);
	}
}