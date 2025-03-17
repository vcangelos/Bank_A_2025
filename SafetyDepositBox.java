import java.io.*;
import java.util.*;

public class SafetyDepositBox {

    // In-memory storage for boxes, indexed by user ID and box size
    private static Map<String, BoxDetails> boxes = new HashMap<>();
    
    // Box sizes and costs, hardcoded for simplicity
    private static final Map<String, BoxDetails> BOX_SIZES = new HashMap<>();
    
    // Hardcoded file path to save data to CSV
    private static final String CSV_FILE_PATH = "src//BankDepositBox.csv";  // Change this to your desired path
    
    static {
        // Initializing box sizes with dimensions and cost
        BOX_SIZES.put("Small", new BoxDetails("Small", "5\" x 5\" x 21.5\"", 50.0));
        BOX_SIZES.put("Medium", new BoxDetails("Medium", "3\" x 10\" x 21.5\"", 60.0));
        BOX_SIZES.put("Large", new BoxDetails("Large", "5\" x 10\" x 21.5\"", 80.0));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Hardcoded user credentials (parameters)
        //connect by adding values
        //by checking these values and searching from the data from existing users that will find the users deposit box CONNECT**
        String validUser = "Manav";
        String validDob = "181208";
        String validSsn = "123456789";
        String validPassword = "123";

        while (true) {
            System.out.println("\n=== Safety Deposit Box System ===");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            if (choice == 1) {
                String username = login(scanner, validUser, validDob, validSsn, validPassword);
                if (username != null) {
                    userMenu(scanner, username);
                }
            } else if (choice == 2) {
                System.out.println("Exiting system.");
                break;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
        scanner.close();
    }

    // Login method to authenticate user based on parameters
    private static String login(Scanner scanner, String validUser, String validDob, String validSsn, String validPassword) {
        System.out.println("=== Login ===");
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your date of birth (YYYY-MM-DD): ");
        String dob = scanner.nextLine();
        System.out.print("Enter your Social Security Number (SSN): ");
        String ssn = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        // Authenticate the user by comparing the input with hardcoded parameters
        if (username.equals(validUser) && dob.equals(validDob) && ssn.equals(validSsn) && password.equals(validPassword)) {
            System.out.println("Login successful!");
            return username;
        }

        System.out.println("Invalid credentials. Please try again.");
        return null;
    }

    // Show user menu after successful login
    private static void userMenu(Scanner scanner, String username) {
        while (true) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. Create Box");
            System.out.println("2. Modify Box Contents");
            System.out.println("3. View Box Details and Interactions");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            if (choice == 1) {
                createBox(scanner, username);
            } else if (choice == 2) {
                modifyBoxContents(scanner, username);
            } else if (choice == 3) {
                viewBoxDetails(username);
            } else if (choice == 4) {
                System.out.println("Logged out.");
                break;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Create a box for the user
    private static void createBox(Scanner scanner, String username) {
        System.out.println("=== Choose Box Size ===");
        for (String size : BOX_SIZES.keySet()) {
            BoxDetails boxDetails = BOX_SIZES.get(size);
            System.out.println(size + " - Dimensions: " + boxDetails.getDimensions() + " - $" + boxDetails.getCost());
        }
        System.out.print("Choose a box size (Small/Medium/Large): ");
        String boxSize = scanner.nextLine();
        BoxDetails selectedBox = BOX_SIZES.get(boxSize);

        if (selectedBox == null) {
            System.out.println("Invalid box size. Try again.");
            return;
        }

        // Create a new deposit box for the user and store it in the boxes map
        BoxDetails box = new BoxDetails(boxSize, selectedBox.getDimensions(), selectedBox.getCost());
        String boxKey = username + "_" + boxSize; // Key formed using username and box size
        boxes.put(boxKey, box);
        System.out.println("Box created: " + boxSize + " with a cost of $" + selectedBox.getCost());

        // Save the box details to CSV
        saveBoxDetailsToCSV(username, boxSize, selectedBox.getDimensions(), selectedBox.getCost(), "", 0.0);
    }

    // Modify contents of the deposit box
    private static void modifyBoxContents(Scanner scanner, String username) {
        System.out.print("Enter your box size (e.g., Small, Medium, Large): ");
        String boxSize = scanner.nextLine();
        String boxKey = username + "_" + boxSize; // Generate the key for the box
        BoxDetails box = boxes.get(boxKey);

        if (box != null) {
            System.out.print("Enter the new contents of your box: ");
            String newContents = scanner.nextLine();
            System.out.print("Enter the total value of items in the box: ");
            double newValue = scanner.nextDouble();
            scanner.nextLine();  // Consume newline

            // Update box details
            box.setContents(newContents);
            box.setTotalValue(newValue);
            System.out.println("Box contents updated.");

            // Save the updated box details to CSV
            saveBoxDetailsToCSV(username, boxSize, box.getDimensions(), box.getCost(), box.getContents(), box.getTotalValue());
        } else {
            System.out.println("Box not found.");
        }
    }

    // View box details and interactions
    private static void viewBoxDetails(String username) {
        System.out.println("=== Box Details ===");
        for (String boxId : boxes.keySet()) {
            BoxDetails box = boxes.get(boxId);
            if (boxId.startsWith(username)) {
                System.out.println("Box ID: " + boxId);
                System.out.println("Size: " + box.getSize());
                System.out.println("Box Dimensions: " + box.getDimensions());
                System.out.println("Box Cost: $" + box.getCost());
                System.out.println("Contents: " + box.getContents());
                System.out.println("Total Value: $" + box.getTotalValue());
                System.out.println("-------------------------");
            }
        }
    }

    // Save box details to a CSV file
    private static void saveBoxDetailsToCSV(String username, String boxSize, String dimensions, double cost, String contents, double totalValue) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH, true))) {
            writer.write(username + "," + boxSize + "," + dimensions + "," + cost + "," + contents + "," + totalValue);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving box details to CSV: " + e.getMessage());
        }
    }

    // BoxDetails class to store box information
    static class BoxDetails {
        private String size;
        private String dimensions;
        private double cost;
        private String contents;
        private double totalValue;

        public BoxDetails(String size, String dimensions, double cost) {
            this.size = size;
            this.dimensions = dimensions;
            this.cost = cost;
            this.contents = "";
            this.totalValue = 0.0;
        }

        public String getSize() {
            return size;
        }

        public String getDimensions() {
            return dimensions;
        }

        public double getCost() {
            return cost;
        }

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }

        public double getTotalValue() {
            return totalValue;
        }

        public void setTotalValue(double totalValue) {
            this.totalValue = totalValue;
        }
    }
}
