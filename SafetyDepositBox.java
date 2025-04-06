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
    private static final Map<String, String[]> userInfoMap = new HashMap<>();
    private static String username;

    static {
        BOX_SIZES.put("Small", new BoxDetails("Small", "5\" x 5\" x 21.5\"", 50.0));
        BOX_SIZES.put("Medium", new BoxDetails("Medium", "3\" x 10\" x 21.5\"", 60.0));
        BOX_SIZES.put("Large", new BoxDetails("Large", "5\" x 10\" x 21.5\"", 80.0));

        loadBoxesFromCSV();
    }

    static {
        userInfoMap.put("123456", new String[]{"Manav", "Shah", "12182008", "123456789", "123"});
        userInfoMap.put("654321", new String[]{"Jane", "Doe", "01011990", "987654321", "abc"});
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Safety Deposit Box System ===");
            System.out.println("1. Login");
            System.out.println("2. Login as an authorized user");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                String uniqueId = login(scanner);
                if (uniqueId != null) {
                    ownerMenu(scanner, uniqueId);
                }
            } else if (choice == 2) {
                // Code for Login as an authorized user goes here
            } else if (choice == 3) {
                System.out.println("Exiting system.");
                break;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
        scanner.close();
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

    private static String login(Scanner scanner) {
        System.out.println("=== Login ===");

        System.out.print("Enter your First Name: ");
        String firstname = scanner.nextLine();

        System.out.print("Enter your Last Name: ");
        String lastname = scanner.nextLine();

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

        for (Map.Entry<String, String[]> entry : userInfoMap.entrySet()) {
            String id = entry.getKey();
            String[] info = entry.getValue();

            if ((firstname + lastname).equals(info[0] + info[1]) && dob.equals(info[2]) && ssn.equals(info[3]) && password.equals(info[4])) {
                if(isUniqueIdInCSV(id)) {
                    System.out.println("Login successful!");
                    return id;
                } else {
                    System.out.println("You are not in the system. Access denied.");
                    return null;
                }
            }
        }

        System.out.println("Invalid credentials. Please try again.");
        return null;
    }
    
    private static boolean isUniqueIdInCSV(String uniqueId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 11 && parts[10].trim().equals(uniqueId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
        return false;
    }
    

    private static void loadBoxesFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String headerLine = reader.readLine(); // Read the header line
            if (headerLine == null || !headerLine.equals("BoxKey,Size,Dimensions,Cost,Contents,TotalValue,Action,Date,AuthFirst,AuthLast,UniqueId")) {
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
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid data line: " + line + " - Could not parse cost or value.");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No previous data file found.");
        } catch (IOException e) {
            System.out.println("Error reading data file: " + e.getMessage());
        }
    }

    private static void ownerMenu(Scanner scanner, String uniqueId) {
        System.out.println("\n=== Owner Menu ===");
        if (hasExistingBox(uniqueId)) {
            System.out.println("Your deposit box:");
            viewBoxDetails(uniqueId);
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
                createBox(scanner, uniqueId);
            } else if (choice == 2) {
                modifyBoxContents(scanner, uniqueId);
            } else if (choice == 3) {
                viewBoxDetails(uniqueId);
            } else if (choice == 4) {
                // Grant Access logic
            } else if (choice == 5) {
                // View Authorized Users logic
            } else if (choice == 6) {
                System.out.println("Logged out.");
                break;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static boolean hasExistingBox(String uniqueId) {
        for (String boxId : boxes.keySet()) {
            if (boxId.startsWith(uniqueId)) {
                return true;
            }
        }
        return false;
    }

    private static void viewBoxDetails(String uniqueId) {
    	// Look for a box associated with the uniqueId
        for (String boxKey : boxes.keySet()) {
            if (boxKey.startsWith(uniqueId)) {
                BoxDetails box = boxes.get(boxKey);
                System.out.println("\nBox Details:");
                System.out.println("Size: " + box.getSize());
                System.out.println("Dimensions: " + box.getDimensions());
                System.out.println("Contents: " + box.getContents());
                System.out.println("Cost: $" + box.getCost());
                System.out.println("Total Value: $" + box.getTotalValue());
                return;
            }
        }
        System.out.println("No box found for your account.");
    }

    private static void createBox(Scanner scanner, String uniqueId) {
    	  System.out.println("\n=== Create Box ===");

    	    System.out.println("Available box sizes:");
    	    for (String size : BOX_SIZES.keySet()) {
    	        BoxDetails details = BOX_SIZES.get(size);
    	        System.out.println("- " + size + " | Dimensions: " + details.getDimensions() + " | Price: $" + details.getCost());
    	    }

    	    System.out.print("Enter the size of the box you want to create: ");
    	    String size = scanner.nextLine();

    	    if (!BOX_SIZES.containsKey(size)) {
    	        System.out.println("Invalid size. Please try again.");
    	        return;
    	    }

    	    BoxDetails template = BOX_SIZES.get(size);

    	    System.out.print("Enter the contents of the box: ");
    	    String contents = scanner.nextLine();

    	    System.out.print("Enter the total value of the box's contents: ");
    	    double totalValue = scanner.nextDouble();
    	    scanner.nextLine(); // clear newline

    	    String boxKey = uniqueId + "_" + size;

    	    BoxDetails newBox = new BoxDetails(size, template.getDimensions(), template.getCost(), contents, totalValue);
    	    boxes.put(boxKey, newBox);

    	    System.out.println("Box created successfully!");

    	    saveBoxesToCSV(); // Save to file

    }
    private static void saveBoxesToCSV() {
        try {
            // Read the existing file content
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            // Open a BufferedWriter to overwrite the CSV file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
                boolean headerWritten = false; // Flag to track if header is written

                // Iterate over the lines in the file
                boolean rowUpdated = false;
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    String[] parts = line.split(",");

                    // If we haven't written the header yet, write it
                    if (!headerWritten) {
                        writer.write("BoxKey,Size,Dimensions,BoxCost,Contents,Totalvalue,Action,Date,AuthFirst,AuthLast,UniqueId\n");
                        headerWritten = true; // Set the flag to true after writing the header
                    }

                    if (parts.length >= 11) {
                        String existingUniqueId = parts[10].trim();

                        // Check if this line's UniqueId matches any box's UniqueId
                        for (Map.Entry<String, BoxDetails> entry : boxes.entrySet()) {
                            String boxKey = entry.getKey();
                            BoxDetails box = entry.getValue();
                            String boxUniqueId = boxKey.split("_")[0]; // Extract the UniqueId from the BoxKey

                            if (existingUniqueId.equals(boxUniqueId)) {
                                // Update the line with new box data (size, contents, value, etc.)
                                String updatedLine = String.join(",", Arrays.asList(
                                    boxKey,
                                    box.getSize(),
                                    box.getDimensions(),
                                    String.valueOf(box.getCost()),
                                    box.getContents(),
                                    String.valueOf(box.getTotalValue()),
                                    "Created", // Action, you can change this to "Modified" or "Updated"
                                    new SimpleDateFormat("MM/dd/yyyy").format(new Date()), // Current Date
                                    "", "", // AuthFirst, AuthLast placeholders (you can populate if needed)
                                    boxUniqueId
                                ));
                                writer.write(updatedLine); // Write updated line to file
                                writer.newLine(); // New line after the row
                                rowUpdated = true;
                                break; // Exit loop since we found and updated the relevant row
                            }
                        }
                    } else {
                        // Write rows that don't have a valid UniqueId (for the header row)
                        writer.write(line);
                        writer.newLine();
                    }
                }

                // If no row was updated, add a new row for the box that was created
                if (!rowUpdated) {
                    for (Map.Entry<String, BoxDetails> entry : boxes.entrySet()) {
                        String boxKey = entry.getKey();
                        BoxDetails box = entry.getValue();
                        String boxUniqueId = boxKey.split("_")[0];

                        writer.write(String.join(",", Arrays.asList(
                            boxKey,
                            box.getSize(),
                            box.getDimensions(),
                            String.valueOf(box.getCost()),
                            box.getContents(),
                            String.valueOf(box.getTotalValue()),
                            "Created", // Action
                            new SimpleDateFormat("MM/dd/yyyy").format(new Date()), // Date
                            "", "", // AuthFirst, AuthLast
                            boxUniqueId
                        )));
                        writer.newLine();
                    }
                }

                System.out.println("Boxes saved to CSV.");
            }

        } catch (IOException e) {
            System.out.println("Error saving to CSV: " + e.getMessage());
        }
    }

    private static void saveBoxesToCSVWithAction(String modifiedBoxKey, String action) {
        try {
            // Read the existing file content
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            // Open a BufferedWriter to overwrite the CSV file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
                // Write the header line
                writer.write("BoxKey,Size,Dimensions,BoxCost,Contents,Totalvalue,Action,Date,AuthFirst,AuthLast,UniqueId\n");

                // Iterate through each line and find the line with the matching uniqueId
                for (int i = 1; i < lines.size(); i++) { // Start from 1 to skip header
                    String line = lines.get(i);
                    String[] parts = line.split(",");
                    
                    if (parts.length >= 11) {
                        String existingUniqueId = parts[10].trim();

                        // If the uniqueId in the CSV matches the user, update the action
                        if (existingUniqueId.equals(modifiedBoxKey.split("_")[0])) {
                            // Update the action for the specific box
                            String updatedLine = line.replace(parts[6], action);
                            lines.set(i, updatedLine);
                        }
                    }
                }

                // Write the updated lines back to the file
                for (String updatedLine : lines) {
                    writer.write(updatedLine);
                    writer.newLine();
                }

                System.out.println("Boxes saved to CSV with action updated.");
            }

        } catch (IOException e) {
            System.out.println("Error saving to CSV: " + e.getMessage());
        }
    }


    private static void modifyBoxContents(Scanner scanner, String uniqueId) {
        System.out.println("\n=== Modify Box Contents ===");

        // Find the box belonging to this user
        String userBoxKey = null;
        for (String boxKey : boxes.keySet()) {
            if (boxKey.startsWith(uniqueId)) {
                userBoxKey = boxKey;
                break;
            }
        }

        if (userBoxKey == null) {
            System.out.println("You don’t have a box to modify.");
            return;
        }

        BoxDetails box = boxes.get(userBoxKey);

        System.out.println("Current contents: " + box.getContents());
        System.out.println("Current total value: $" + box.getTotalValue());

        System.out.print("Enter new contents: ");
        String newContents = scanner.nextLine();

        System.out.print("Enter new total value: ");
        double newValue = scanner.nextDouble();
        scanner.nextLine(); // flush newline

        box.setContents(newContents);
        box.setTotalValue(newValue);

        // Save updated box info with new action "Modified"
        saveBoxesToCSVWithAction(userBoxKey, "Modified");

        System.out.println("Box updated successfully.");
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

