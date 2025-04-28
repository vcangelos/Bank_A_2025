import java.io.*;
import java.net.*;
import java.util.*;

public class FXBank {
    static final String USERS_FILE = "users.csv";
    static final String TRANSACTIONS_FILE = "transactions.csv";
    static final String[] CURRENCIES = {"USD", "EUR", "GBP", "CAD", "INDR"};
    static final double FEE_PERCENTAGE = 2.5;
    static final String API_KEY = "4b50e36e9afb0ce012dfa2d1"; // Replace with your actual API key
    static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeFiles();
        System.out.println("Welcome to Virtual FX Bank");

        if (!authenticateUser()) {
            System.out.println("Authentication failed.");
            return;
        }

        while (true) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. View Exchange Rates");
            System.out.println("2. Convert Currency");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            String input = scanner.nextLine();
            if (input.equals("1")) {
                showRates();
            } else if (input.equals("2")) {
                performConversion();
            } else if (input.equals("3")) {
                System.out.println("Thank you for using FX Bank!");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    static void initializeFiles() {
        try {
            File usersFile = new File(USERS_FILE);
            if (!usersFile.exists()) {
                FileWriter writer = new FileWriter(usersFile);
                writer.write("FirstName,LastName,SSN\n");
                writer.write("John,Doe,123456789\n");
                writer.close();
                System.out.println("Created users.csv with a sample user.");
            }

            File transactionsFile = new File(TRANSACTIONS_FILE);
            if (!transactionsFile.exists()) {
                FileWriter writer = new FileWriter(transactionsFile);
                writer.write("FromCurrency,ToCurrency,OriginalAmount,ConvertedAmount,Fee,FinalAmount,TransactionType,DateTime\n");
                writer.close();
                System.out.println("Created transactions.csv.");
            }
        } catch (IOException e) {
            System.out.println("Error initializing files: " + e.getMessage());
        }
    }

    static boolean authenticateUser() {
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter SSN: ");
        String ssn = scanner.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("FirstName")) continue;
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equalsIgnoreCase(firstName) &&
                        parts[1].equalsIgnoreCase(lastName) && parts[2].equals(ssn)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("User lookup error: " + e.getMessage());
        }
        return false;
    }

    static void showRates() {
        Map<String, Double> rates = getExchangeRates();
        if (rates.isEmpty()) {
            System.out.println("Could not retrieve exchange rates.");
            return;
        }
        System.out.println("Exchange Rates (Base: USD):");
        for (String currency : CURRENCIES) {
            System.out.println("1 USD = " + rates.get(currency) + " " + currency);
        }
    }

    static void performConversion() {
        Map<String, Double> rates = getExchangeRates();
        if (rates.isEmpty()) {
            System.out.println("Could not retrieve rates.");
            return;
        }

        System.out.print("Enter source currency (USD): ");
        String source = scanner.nextLine().toUpperCase();
        if (!source.equals("USD")) {
            System.out.println("Currently only USD is supported as source.");
            return;
        }

        System.out.print("Enter target currency (EUR, GBP, CAD, INDR): ");
        String target = scanner.nextLine().toUpperCase();
        if (!rates.containsKey(target)) {
            System.out.println("Invalid currency.");
            return;
        }

        System.out.print("Enter amount to convert: ");
        double amount = Double.parseDouble(scanner.nextLine());

        double rate = rates.get(target);
        double converted = amount * rate;
        double fee = converted * FEE_PERCENTAGE / 100;
        double finalAmount = converted - fee;

        System.out.println("Select transaction type: 1 = Foreign Card, 2 = Temp Account, 3 = Cash");
        String typeInput = scanner.nextLine();
        String type = typeInput.equals("1") ? "Foreign Card" : typeInput.equals("2") ? "Temp Account" : "Cash";

        System.out.println("\n--- Transaction Summary ---");
        System.out.println("Amount in USD: " + amount);
        System.out.println("Converted to " + target + ": " + converted);
        System.out.println("Fee: " + fee);
        System.out.println("Final Amount: " + finalAmount);
        System.out.println("Transaction Type: " + type);
        System.out.print("Confirm transaction (Y/N)? ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("Y")) {
            saveTransaction(source, target, amount, converted, fee, finalAmount, type);
            System.out.println("Transaction successful.");
        } else {
            System.out.println("Transaction cancelled.");
        }
    }

    static Map<String, Double> getExchangeRates() {
        Map<String, Double> rates = new HashMap<>();
        try {
            URL url = new URL("https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            String json = response.toString();
            int ratesStart = json.indexOf("\"conversion_rates\":{") + 20;
            int ratesEnd = json.indexOf("}", ratesStart);
            if (ratesStart == -1 || ratesEnd == -1) {
                throw new Exception("Invalid JSON format.");
            }

            String ratesBlock = json.substring(ratesStart, ratesEnd);
            String[] pairs = ratesBlock.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.replace("\"", "").split(":");
                if (keyValue.length == 2) {
                    String currency = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    for (String c : CURRENCIES) {
                        if (c.equals(currency)) {
                            rates.put(currency, Double.parseDouble(value));
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error fetching rates: " + e.getMessage());
            rates.put("USD", 1.0);
            rates.put("EUR", 0.91);
            rates.put("GBP", 0.78);
            rates.put("CAD", 1.35);
            rates.put("INDR", 83.12);
        }

        return rates;
    }

    static void saveTransaction(String from, String to, double amount, double converted, double fee, double finalAmount, String type) {
        try (FileWriter writer = new FileWriter(TRANSACTIONS_FILE, true)) {
            writer.write(from + "," + to + "," + amount + "," + converted + "," + fee + "," + finalAmount + "," + type + "," + new Date() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }
}
