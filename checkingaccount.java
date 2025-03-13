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
	
	private Date dateOpened; //  get from csv
	private Date lastTransactionDate;
	
	public CheckingAccount()
	{
		this.accountNumber = accountNumber;
		this.id = id;
		this.balance = 0;
	}
	
	public CheckingAccount(String accountNumber, String id, double balance)
	{
		this.accountNumber = accountNumber;
		this.id = id;
		this.balance = balance;
	}
	
	// Making a deposit
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
	
	// Making a withdrawal
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
	
	// getter method for current balance
	public double getBalance()
	{
		return balance;
	}
	
	// log transaction type and amount
	private void logTransaction(String type, double amount)
	{
		System.out.println(type + " of $" + amount + " completed!");
	}

	// get id of checking account
	public String getID()
	{
		return id;
	}
}

public class checkingaccount
{
	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		boolean breakloop = false;
		double amount;
		
		// makes sure a checking account exists, and makes one if it doesn't
		if (!checkingAccount)
		{
			CheckingAccount checking = new CheckingAccount();
		}
		
		// what the user sees and has to input
		while (!breakloop)
		{
			System.out.print("What would you like to do? Check Balance[1], Make a Deposit[2], Make a Withdrawal[3], Get Other Info[4], Quit Checking Account Management[5] ");
			int checkingOption = scan.nextInt();
			if (checkingOption == 1)
			{
				checking.getBalance();
			}

			if (checkingOption == 2)
			{
				System.out.print("How much would you like to deposit? ");
				amount += scan.nextDouble();
				checking.deposit(amount);
				lastTransactionDate = new Date();
			}
        
			if (checkingOption == 3)
			{
				System.out.print("How much would you like to withdraw? ");
				amount -= scan.nextDouble();
				checking.withdraw();
				lastTransactionDate = new Date();
			}
        
			if (checkingOption == 4)
			{
				System.out.println("Printing out checking account info...");
				System.out.println("Account Number: " + accountNumber);
				System.out.println("Account Holder: " + accountHolderName);
				System.out.println("Overdraft Protection Status: " + isOverdraftProtectionEnabled);
				System.out.println("Overdraft Limit: " + overdraftLimit);
				System.out.println("Account Opening Date: " + dateOpened);
				System.out.println("Date of Last Transaction: " + lastTransactionDate);
			}
			
			if (checkingOption == 5)
			{
				breakloop = true;
			}
		}
	}
}