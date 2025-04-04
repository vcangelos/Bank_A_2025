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




import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

public class SafetyDepositBox {

    private static Map<String, BoxDetails> boxes = new HashMap<>();
    private static final Map<String, BoxDetails> BOX_SIZES = new HashMap<>();
    private static final String CSV_FILE_PATH = "src//BankDepositBox.csv";
    private static final Map<String, Set<String>> authorizedUsers = new HashMap<>(); // BoxKey -> Set of authorized usernames
    private static final SimpleDateFormat DOB_FORMAT = new SimpleDateFormat("MMddyyyy");

    static {
        BOX_SIZES.put("Small", new BoxDetails("Small", "5\" x 5\" x 21.5\"", 50.0));
        BOX_SIZES.put("Medium", new BoxDetails("Medium", "3\" x 10\" x 21.5\"", 60.0));
        BOX_SIZES.put("Large", new BoxDetails("Large", "5\" x 10\" x 21.5\"", 80.0));

        loadBoxesFromCSV();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String validFirstName = "Manav";
        String validLastName = "Shah";
        String validDob = "12182008";
        String validSsn = "123456789";
        String validPassword = "123";


        while (true) {
            System.out.println("\n=== Safety Deposit Box System ===");
            System.out.println("1. Login");
            System.out.println("2. Login as an authorized user");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                String uniqueId = login(scanner, validFirstName, validLastName, validDob, validSsn, validPassword);
                if (uniqueId != null) {
                    
                    ownerMenu(scanner, uniqueId);
                }
                else if (choice == 2) {
                      /*System.out.print("Enter your First Name");
                      System.out.print("Enter your Last Name");*/
            } else if (choice == 3) {
                System.out.println("Exiting system.");
                break;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
            }
        }
        scanner.close();
    }

    private static String login(Scanner scanner, String validFirstName, String validLastName, String validDob, String validSsn, String validPassword) {
        System.out.println("=== Login ===");
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        String dob;
        while (true) {
            System.out.print("Enter your date of birth (MMDDYYYY): ");
            dob = scanner.nextLine();
            if (isValidDate(dob)) {
                break;
            } else {
                System.out.println("Invalid date format. Please enter in MMDDYYYY format.");
            }
        }

        String ssn;
        while (true) {
            System.out.print("Enter your Social Security Number (SSN): ");
            ssn = scanner.nextLine();
            if (ssn.matches("\\d{9}")) {
                break;
            } else {
                System.out.println("Invalid SSN format. Please enter exactly 9 digits.");
            }
        }

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (username.equals(validFirstName + validLastName) && dob.equals(validDob) && ssn.equals(validSsn) && password.equals(validPassword)) {
            System.out.println("Login successful!");
            return username;
        }

        System.out.println("Invalid credentials. Please try again.");
        return null;
    }

    private static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.length() != 8) {
            return false;
        }
        try {
            DOB_FORMAT.setLenient(false);
            DOB_FORMAT.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private static void ownerMenu(Scanner scanner, String username) {
        System.out.println("\n=== Owner Menu ===");
        if (hasExistingBox(username)) {
            System.out.println("Your deposit box:");
            viewBoxDetails(username);
        }

        while (true) {
        	System.out.println("\n1. Create Box");
        	System.out.println("2. Modify Box Contents");
        	System.out.println("3. View Box Details");
        	System.out.println("4. Grant Access to Other User");
        	System.out.println("5. View Authorized Users");
        	System.out.println("6. Logout");
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
                grantAccess(scanner, username);
            } else if (choice == 5) {
                viewAuthorizedUsers(username);
            } else if (choice == 6) {
                System.out.println("Logged out.");
                break;
            } else {
                System.out.println("Invalid choice. Try again.");
            }}
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
        authorizedUsers.put(boxKey, new HashSet<>()); // Initialize authorized users for the new box
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
        System.out.println("=== Your Box Details ===");
        for (String boxId : boxes.keySet()) {
            if (boxId.startsWith(username)) {
                BoxDetails box = boxes.get(boxId);
                System.out.println("Size: " + box.getSize());
                System.out.println("Dimensions: " + box.getDimensions());
                System.out.println("Cost: $" + box.getCost());
                System.out.println("Contents: " + box.getContents());
                System.out.println("Total Value: $" + box.getTotalValue());
                System.out.println("Authorized Users: " + authorizedUsers.getOrDefault(boxId, Collections.emptySet()));
                System.out.println("-------------------------");
            }
        }
    }
    private static void viewAuthorizedUsers(String username) {
        System.out.println("=== Authorized Users for Your Box ===");
        for (String boxId : boxes.keySet()) {
            if (boxId.startsWith(username)) {
                Set<String> users = authorizedUsers.getOrDefault(boxId, new HashSet<>());
                System.out.println("Box: " + boxId);
                System.out.println("Authorized Users: " + (users.isEmpty() ? "None" : String.join(", ", users)));
                System.out.println("-------------------------");
            }
        }
    }

    private static void grantAccess(Scanner scanner, String ownerUsername) {
        System.out.print("Enter the size of the box you want to grant access to (e.g., Small, Medium, Large): ");
        String boxSize = scanner.nextLine();
        boxSize = boxSize.substring(0, 1).toUpperCase() + boxSize.substring(1).toLowerCase();
        String boxKey = ownerUsername + "_" + boxSize;

        if (!boxes.containsKey(boxKey)) {
            System.out.println("Safety deposit box not found for this user and size.");
            return;
        }

        System.out.println("\n=== Verify User to Grant Access ===");
        System.out.print("Enter the username of the person you want to grant access to: ");
        String otherUsername = scanner.nextLine();
        if (otherUsername.equals(ownerUsername)) {
            System.out.println("You cannot grant access to yourself.");
            return;
        }

        String otherDob;
        while (true) {
            System.out.print("Enter their date of birth (MMDDYYYY): ");
            otherDob = scanner.nextLine();
            if (isValidDate(otherDob)) {
                break;
            } else {
                System.out.println("Invalid date format. Please enter in MMDDYYYY format.");
            }
        }

        String otherSsn;
        while (true) {
            System.out.print("Enter their Social Security Number (SSN): ");
            otherSsn = scanner.nextLine();
            if (otherSsn.matches("\\d{9}")) {
                break;
            } else {
                System.out.println("Invalid SSN format. Please enter exactly 9 digits.");
            }
        }

        System.out.println("\n=== Verify Yourself (Box Owner) ===");
        System.out.print("Enter your username: ");
        String enteredOwnerUsername = scanner.nextLine();

        String enteredOwnerDob;
        while (true) {
            System.out.print("Enter your date of birth (MMDDYYYY): ");
            enteredOwnerDob = scanner.nextLine();
            if (isValidDate(enteredOwnerDob)) {
                break;
            } else {
                System.out.println("Invalid date format. Please enter in MMDDYYYY format.");
            }
        }

        String enteredOwnerSsn;
        while (true) {
            System.out.print("Enter your Social Security Number (SSN): ");
            enteredOwnerSsn = scanner.nextLine();
            if (enteredOwnerSsn.matches("\\d{9}")) {
                break;
            } else {
                System.out.println("Invalid SSN format. Please enter exactly 9 digits.");
            }
        }

        String validUser = "Manav";
        String validDob = "12182008";
        String validSsn = "123456789";

        if (enteredOwnerUsername.equals(ownerUsername) && enteredOwnerDob.equals(validDob) && enteredOwnerSsn.equals(validSsn)) {
            authorizedUsers.computeIfAbsent(boxKey, k -> new HashSet<>()).add(otherUsername);
            System.out.println("Successfully granted access to user: " + otherUsername + " for box size: " + boxSize + ".");
            logAction(ownerUsername, "Granted access to " + otherUsername + " for Box: " + boxSize);
            saveBoxDetailsToCSV("Access Granted");
        } else {
            System.out.println("Verification failed. Could not grant access.");
        }
    }

    private static void saveBoxDetailsToCSV(String action) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(new Date());

        try (FileWriter fileWriter = new FileWriter(CSV_FILE_PATH);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {
            writer.write("BoxKey,Size,Dimensions,Cost,Contents,TotalValue,Action,Date,AuthorizedUsers,UniqueId");
            writer.newLine();
            for (Map.Entry<String, BoxDetails> entry : boxes.entrySet()) {
                String key = entry.getKey();
                BoxDetails box = entry.getValue();
                String authorizedList = authorizedUsers.getOrDefault(key, Collections.emptySet()).stream().collect(Collectors.joining(";"));
                writer.write(key + "," + box.getSize() + "," + box.getDimensions() + "," + box.getCost() + "," +
                        box.getContents() + "," + box.getTotalValue() + "," + action + "," + currentDate + "," + authorizedList);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving box details: " + e.getMessage());
        }
    }

    private static void loadBoxesFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String headerLine = reader.readLine(); // Read the header line
            if (headerLine == null || !headerLine.equals("BoxKey,Size,Dimensions,Cost,Contents,TotalValue,Action,Date,AuthorizedUsers,UniqueId")) {
                System.out.println("Warning: CSV file header might be missing or incorrect.");
            }
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    String boxKey = parts[0];
                    String size = parts[1];
                    String dimensions = parts[2];
                    try {
                        double cost = Double.parseDouble(parts[3]);
                        String contents = parts[4];
                        double totalValue = Double.parseDouble(parts[5]);
                        BoxDetails box = new BoxDetails(size, dimensions, cost, contents, totalValue);
                        boxes.put(boxKey, box);
                        String authorizedString = parts[8];
                        if (!authorizedString.isEmpty()) {
                            authorizedUsers.put(boxKey, new HashSet<>(Arrays.asList(authorizedString.split(";"))));
                        } else {
                            authorizedUsers.put(boxKey, new HashSet<>());
                        }
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

