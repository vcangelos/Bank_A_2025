package checkingaccount;

import java.util.Scanner;
import java.util.Date;

class CheckingAccount
{
	private String accountNumber;
	private double balance;
	// connect to user info
	private String accountHolderName;
	
	private boolean isOverdraftProtectionEnabled;
	private double overdraftLimit;
	private Date dateOpened;
	private Date lastTransactionDate;
	
	public checkingaccount(String accnum, String id, double balance)
	{
		this.accnum = accnum;
		this.id = id;
		this.balance = balance;
	}
	
	public void deposit(double amount)
	{
		if (amount > 0)
		{
			amount = Math.round(amount * 100.0) / 100.0;
			balance += amount;
			logTransaction("Deposit", amount);
		}
		else
		{
			System.out.println("Invalid deposit amount. Please type a postive dollar amount.");
		}
	}
	public boolean withdraw(double amount)
	{
		if (amount > 0 && amount <= balance)
		{
			balance -= amount;
			logTransaction("Withdrawal", amount);
			return true;
		}
		else if (amount <= 0)
		{
			System.out.println("Invalid deposit amount. Please enter a positive number.");
			return false;
		}
		else
		{
			System.out.println("Insufficient funds. Your account has $" + balance + ".");
			return false;
		}
	}
	
	public double getBalance()
	{
		return balance;
	}
	
	private void logTransaction(String type, double amount)
	{
		System.out.println(type + " of $" + amount + " completed!")
	}
	
	public String getID()
	{
		return id;
	}
}

public class checkingtest
{
	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		boolean breakloop = false;
		double amount;
		
		
		if (!checkingAccount)
		{
			CheckingAccount checking = new CheckingAccount;
		}
		while (!breakloop)
		{
			System.out.print("What would you like to do? Check Balance[1], Make a Deposit[2], Make a Withdrawal[3], Get Other Info[4], Quit Checking Account Management[5] ");
			int checkingOption = scan.nextLine();
			if (checkingOption.equalsIgnoreCase("1"))
			{
				checking.getBalance();
			}

			if (checkingOption.equalsIgnoreCase("2"))
			{
				System.out.print("How much would you like to deposit?");
				amount = scan.nextDouble();
				checking.deposit(amount);
			}
        
			if (checkingOption.equalsIgnoreCase("3"))
			{
				checking.withdraw();
			}
        
			if (checkingOption.equalsIgnoreCase("4"))
			{
				
			}
			
			if (checkingOption.equalsIgnoreCase("5"))
			{
				breakloop = true;
			}
		}
	}
}