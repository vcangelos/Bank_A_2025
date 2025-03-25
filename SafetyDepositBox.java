import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class SafetyDepositBox {

    private static Map<String, BoxDetails> boxes = new HashMap<>();
    private static final Map<String, BoxDetails> BOX_SIZES = new HashMap<>();
    private static final String CSV_FILE_PATH = "src//BankDepositBox.csv";

    static {
        BOX_SIZES.put("Small", new BoxDetails("Small", "5\" x 5\" x 21.5\"", 50.0));
        BOX_SIZES.put("Medium", new BoxDetails("Medium", "3\" x 10\" x 21.5\"", 60.0));
        BOX_SIZES.put("Large", new BoxDetails("Large", "5\" x 10\" x 21.5\"", 80.0));

        loadBoxesFromCSV();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String validUser = "Manav";
        String validDob = "12182008";
        String validSsn = "123456789";
        String validPassword = "123";

        while (true) {
            System.out.println("\n=== Safety Deposit Box System ===");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

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

    private static String login(Scanner scanner, String validUser, String validDob, String validSsn, String validPassword) {
        System.out.println("=== Login ===");
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your date of birth (MMDDYYYY): ");
        String dob = scanner.nextLine();

        System.out.print("Enter your Social Security Number (SSN): ");
        String ssn = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (username.equals(validUser) && dob.equals(validDob) && ssn.equals(validSsn) && password.equals(validPassword)) {
            System.out.println("Login successful!");
            return username;
        }

        System.out.println("Invalid credentials. Please try again.");
        return null;
    }

    private static void userMenu(Scanner scanner, String username) {
        System.out.println("\n=== User Menu ===");
        if (hasExistingBox(username)) {
            System.out.println("You already have a deposit box:");
            viewBoxDetails(username);
        }

        while (true) {
            System.out.println("\n1. Create Box");
            System.out.println("2. Modify Box Contents");
            System.out.println("3. View Box Details");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

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

    private static boolean hasExistingBox(String username) {
        for (String boxId : boxes.keySet()) {
            if (boxId.startsWith(username)) {
                return true;
            }
        }
        return false;
    }

    private static void createBox(Scanner scanner, String username) {
        if (hasExistingBox(username)) {
            System.out.println("You already have a box. You cannot create another.");
            return;
        }

        System.out.println("=== Choose Box Size ===");
        for (String size : BOX_SIZES.keySet()) {
            BoxDetails boxDetails = BOX_SIZES.get(size);
            System.out.println(size + " - Dimensions: " + boxDetails.getDimensions() + " - $" + boxDetails.getCost());
        }
        System.out.print("Choose a box size (Small/Medium/Large): ");
        String boxSize = scanner.nextLine();
        boxSize = boxSize.substring(0, 1).toUpperCase() + boxSize.substring(1).toLowerCase();

        BoxDetails selectedBox = BOX_SIZES.get(boxSize);
        if (selectedBox == null) {
            System.out.println("Invalid box size. Try again.");
            return;
        }

        BoxDetails box = new BoxDetails(boxSize, selectedBox.getDimensions(), selectedBox.getCost());
        String boxKey = username + "_" + boxSize;
        boxes.put(boxKey, box);
        System.out.println("Box created: " + boxSize + " with a cost of $" + selectedBox.getCost());

        String action = "Created";
        logAction(username, action + " Box: " + boxSize);
        saveBoxDetailsToCSV(action);
    }

    private static void modifyBoxContents(Scanner scanner, String username) {
        System.out.print("Enter your box size (e.g., Small, Medium, Large): ");
        String boxSize = scanner.nextLine();
        boxSize = boxSize.substring(0, 1).toUpperCase() + boxSize.substring(1).toLowerCase();

        String boxKey = username + "_" + boxSize;
        BoxDetails box = boxes.get(boxKey);

        if (box != null) {
            System.out.print("Enter the new contents of your box: ");
            String newContents = scanner.nextLine();

            System.out.print("Enter the total value of items in the box: ");
            double newValue = scanner.nextDouble();
            scanner.nextLine();

            box.setContents(newContents);
            box.setTotalValue(newValue);
            System.out.println("Box contents updated.");

            String action = "Modified Contents";
            logAction(username, action + " of Box: " + boxSize);
            saveBoxDetailsToCSV(action);
        } else {
            System.out.println("Box not found.");
        }
    }

    private static void viewBoxDetails(String username) {
        System.out.println("=== Box Details ===");
        for (String boxId : boxes.keySet()) {
            if (boxId.startsWith(username)) {
                BoxDetails box = boxes.get(boxId);
                System.out.println("Size: " + box.getSize());
                System.out.println("Dimensions: " + box.getDimensions());
                System.out.println("Cost: $" + box.getCost());
                System.out.println("Contents: " + box.getContents());
                System.out.println("Total Value: $" + box.getTotalValue());
                System.out.println("-------------------------");
            }
        }
    }

    private static void saveBoxDetailsToCSV(String action) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(new Date());

        try (FileWriter fileWriter = new FileWriter(CSV_FILE_PATH, true);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {
            for (String key : boxes.keySet()) {
                BoxDetails box = boxes.get(key);
                String username = key.substring(0, key.lastIndexOf("_"));
                writer.write(key + "," + box.getSize() + "," + box.getDimensions() + "," + box.getCost() + "," +
                        box.getContents() + "," + box.getTotalValue() + "," + action + "," + currentDate);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving box details: " + e.getMessage());
        }
    }

    private static void loadBoxesFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line = reader.readLine(); // Read and discard the header line
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String boxKey = parts[0];
                    String size = parts[1];
                    String dimensions = parts[2];
                    try {
                        double cost = Double.parseDouble(parts[3]);
                        String contents = parts[4];
                        double totalValue = Double.parseDouble(parts[5]);
                        BoxDetails box = new BoxDetails(size, dimensions, cost, contents, totalValue);
                        boxes.put(boxKey, box);
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid data line: " + line + " - Could not parse cost or value.");
                    }
                } else if (!line.trim().isEmpty()) {
                    System.out.println("Skipping invalid data line: " + line + " - Incorrect number of fields.");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No previous data file found.");
        } catch (IOException e) {
            System.out.println("Error reading data file: " + e.getMessage());
        }
    }

    private static void logAction(String username, String action) {
        System.out.println("Log: " + username + " - " + action);
    }

    static class BoxDetails {
        private String size, dimensions, contents = "";
        private double cost, totalValue = 0.0;

        public BoxDetails(String size, String dimensions, double cost) {
            this(size, dimensions, cost, "", 0.0);
        }

        public BoxDetails(String size, String dimensions, double cost, String contents, double totalValue) {
            this.size = size;
            this.dimensions = dimensions;
            this.cost = cost;
            this.contents = contents;
            this.totalValue = totalValue;
        }

        public String getSize() { return size; }
        public String getDimensions() { return dimensions; }
        public double getCost() { return cost; }
        public String getContents() { return contents; }
        public double getTotalValue() { return totalValue; }
        public void setContents(String contents) { this.contents = contents; }
        public void setTotalValue(double totalValue) { this.totalValue = totalValue; }
    }
}
