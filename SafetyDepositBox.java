
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
            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            
            

            if (choice == 1) {
                String uniqueId = login(scanner);
                if (uniqueId != null) {
                    ownerMenu(scanner, uniqueId);
                }
            } else if (choice == 2) {
                loginAsAuthorizedUser(scanner);
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
            System.out.println("4. Change Box size");
            System.out.println("5. Grant Access to Other User");
            System.out.println("6. View Authorized Users");
            System.out.println("7. Logout");
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
            	 changeBoxSize(scanner, uniqueId);
            } else if (choice == 5) {
                grantAccessToUser(scanner, uniqueId);
            } else if (choice == 6) {
                viewAuthorizedUsers(uniqueId);

            } else if (choice == 7) {
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
    private static void changeBoxSize(Scanner scanner, String uniqueId) {
        System.out.println("\n=== Change Box Size ===");

        // Find current box
        String oldBoxKey = null;
        for (String key : boxes.keySet()) {
            if (key.startsWith(uniqueId)) {
                oldBoxKey = key;
                break;
            }
        }

        if (oldBoxKey == null) {
            System.out.println("No box found for this user.");
            return;
        }

        BoxDetails currentBox = boxes.get(oldBoxKey);

        System.out.println("Current size: " + currentBox.getSize());
        System.out.println("Available sizes:");
        for (String size : BOX_SIZES.keySet()) {
            System.out.println("- " + size);
        }

        System.out.print("Enter new box size: ");
        String inputSize = scanner.nextLine();
        String newSize = normalizeSize(inputSize);

        if (newSize == null || !BOX_SIZES.containsKey(newSize)) {
            System.out.println("Invalid size.");
            return;
        }


        BoxDetails newTemplate = BOX_SIZES.get(newSize);
        currentBox.setSize(newSize);
        currentBox.setDimensions(newTemplate.getDimensions());
        currentBox.setCost(newTemplate.getCost());

        String newBoxKey = uniqueId + "_" + newSize;

        boxes.remove(oldBoxKey);
        boxes.put(newBoxKey, currentBox);

        saveBoxesToCSV(newBoxKey, "Modified");

        System.out.println("Box size updated successfully!");
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
    	
    	for (String key : boxes.keySet()) {
    	    if (key.startsWith(uniqueId + "_")) {
    	        System.out.println("You already have a box. Cannot create another one.");
    	        return;
    	    }
    	}
    	  System.out.println("\n=== Create Box ===");

    	    System.out.println("Available box sizes:");
    	    for (String size : BOX_SIZES.keySet()) {
    	        BoxDetails details = BOX_SIZES.get(size);
    	        System.out.println("- " + size + " | Dimensions: " + details.getDimensions() + " | Price: $" + details.getCost());
    	    }

    	    System.out.print("Enter the size of the box you want to create: ");
    	    String inputSize = scanner.nextLine();
    	    String size = normalizeSize(inputSize);

    	    if (size == null || !BOX_SIZES.containsKey(size)) {
    	        System.out.println("Invalid size.");
    	        return;
    	    }

    	    if (!BOX_SIZES.containsKey(size)) {
    	        System.out.println("Invalid size. Please try again.");
    	        return;
    	    }

    	    BoxDetails template = BOX_SIZES.get(size);

    	    System.out.print("Enter the contents of the box: ");
    	    String contents = scanner.nextLine();

    	    System.out.print("Enter the total value of the box's contents: $");
    	    double totalValue = scanner.nextDouble();
    	    scanner.nextLine(); // clear newline

    	    String boxKey = uniqueId + "_" + size;

    	    BoxDetails newBox = new BoxDetails(size, template.getDimensions(), template.getCost(), contents, totalValue);
    	    boxes.put(boxKey, newBox);

    	    System.out.println("Box created successfully!");

    	    saveBoxesToCSV(boxKey, "Created");


    }
    private static String normalizeSize(String input) {
        if (input == null) return null;
        String lower = input.trim().toLowerCase();
        switch (lower) {
            case "small": return "Small";
            case "medium": return "Medium";
            case "large": return "Large";
            default: return null;
        }
    }
  
    private static void saveBoxesToCSV(String modifiedBoxKey, String action) {
        try {
            List<String> updatedLines = new ArrayList<>();
            boolean headerAdded = false;
            String targetUniqueId = modifiedBoxKey.split("_")[0];

            try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", -1); // keep empty values

                    // Header row
                    if (!headerAdded && line.toLowerCase().contains("boxkey")) {
                        updatedLines.add(line);
                        headerAdded = true;
                        continue;
                    }

                    // Match the UniqueId column (11th index == 10)
                    if (parts.length >= 12 && parts[10].equals(targetUniqueId)) {
                        BoxDetails box = boxes.get(modifiedBoxKey);
                        if (box != null) {
                            String updatedLine = String.join(",", Arrays.asList(
                                modifiedBoxKey,
                                box.getSize(),
                                box.getDimensions(),
                                String.valueOf(box.getCost()),
                                box.getContents(),
                                String.valueOf(box.getTotalValue()),
                                action,
                                new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()),
                                parts[8], parts[9], parts[10], parts[11] // preserve auth info
                            ));
                            updatedLines.add(updatedLine);
                            continue;
                        }
                    }

                    // Preserve untouched lines
                    updatedLines.add(line);
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
                for (String l : updatedLines) {
                    writer.write(l);
                    writer.newLine();
                }
            }

            System.out.println("Box was successfully: " + action);
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
        saveBoxesToCSV(userBoxKey, "Modified");
 

        
    }
   
   private static void grantAccessToUser(Scanner scanner, String uniqueId) {
    String boxKey = boxes.keySet().stream()
            .filter(key -> key.startsWith(uniqueId))
            .findFirst().orElse(null);

    if (boxKey == null) {
        System.out.println("No box found for your ID.");
        return;
    }

    String authUsername;
    while (true) {
        System.out.print("Enter a username to provide to an authorized user: ");
        authUsername = scanner.nextLine().trim();

        if (!isAuthUsernameTaken(authUsername)) break;

        System.out.println("Username already taken. Try another.");
    }

    System.out.print("Enter a password for the authorized user: ");
    String authPassword = scanner.nextLine().trim();

    // Ask for permission level
    System.out.print("Grant permission level (view/full): ");
    String permission = scanner.nextLine().trim().toLowerCase();

    while (!permission.equals("view") && !permission.equals("full")) {
        System.out.print("Invalid input. Enter 'view' or 'full': ");
        permission = scanner.nextLine().trim().toLowerCase();
    }

    updateAuthInfoInCSV(boxKey, authUsername, authPassword, permission);
    System.out.println("Access granted successfully.");
}

    private static boolean isAuthUsernameTaken(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 10 && username.equals(parts[8].trim())) return true;
            }
        } catch (IOException e) {
            System.out.println("Error checking username: " + e.getMessage());
        }
        return false;
    }
    
    private static void updateAuthInfoInCSV(String boxKey, String authUsername, String authPassword, String permission) {
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 11 && parts[0].equals(boxKey)) {
                    if (parts.length == 11) {
                        parts = Arrays.copyOf(parts, 12); // Add space for AuthPerm
                        parts[11] = ""; // Init AuthPerm if missing
                    }
                    parts[8] = authUsername;   // Username
                    parts[9] = authPassword;   // Password
                    parts[11] = permission;    // Permission (view/full)
                    line = String.join(",", parts);
                }
                updatedLines.add(line);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
                for (String l : updatedLines) {
                    writer.write(l);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            System.out.println("Error updating CSV: " + e.getMessage());
        }
    }


    private static void viewAuthorizedUsers(String uniqueId) {
        for (String boxKey : boxes.keySet()) {
            if (boxKey.startsWith(uniqueId)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 11 && parts[0].equals(boxKey)) {
                            if (parts.length == 11) {
                                parts = Arrays.copyOf(parts, 12); // Add room for permission if missing
                                parts[11] = ""; // Default empty permission
                            }

                            System.out.println("Authorized Username: " + parts[8]);
                            System.out.println("Authorized Password: " + parts[9]);
                            System.out.println("Permission Level: " + parts[11]); // Now safe
                            return;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error reading authorized user: " + e.getMessage());
                }
            }
        }
        System.out.println("No authorized users found.");
    }


    private static void loginAsAuthorizedUser(Scanner scanner) {
        System.out.println("=== Authorized User Login ===");
        System.out.print("Enter authorized username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 12 && parts[8].equals(username) && parts[9].equals(password)) {
                    System.out.println("Access granted...");
                    String uniqueId = parts[10];
                    String permission = parts[11].trim().toLowerCase();
                    authorizedUserMenu(uniqueId, permission);
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error during login: " + e.getMessage());
        }

        System.out.println("Invalid authorized credentials.");
    }


private static void authorizedUserMenu(String uniqueId, String permission) {
    Scanner scanner = new Scanner(System.in);
    while (true) {
        System.out.println("\n--- Authorized User Menu ---");
        System.out.println("1. View Box Details");
        if (permission.equals("full")) {
            System.out.println("2. Modify Box Contents");
        }
        System.out.println("0. Logout");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                viewBoxDetails(uniqueId);
                break;
            case "2":
                if (permission.equals("full")) {
                    modifyBoxContents(scanner, uniqueId);
                } else {
                    System.out.println("You do not have permission to modify.");
                }
                break;
            case "0":
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }
}


    static class BoxDetails {
        private String size, dimensions, contents = "";
        private double cost, totalValue = 0.0;

        public BoxDetails(String size, String dimensions, double cost) {
            this(size, dimensions, cost, "", 0.0);
        }

        public void setSize(String newSize) {
			this.size = newSize;
		}

		public void setCost(double newcost) {
			
			this.cost = newcost;
		}

		public void setDimensions(String newdimensions) {
			this.dimensions = newdimensions;
		}

		public BoxDetails(String size, String dimensions, double cost, String contents, double totalValue) {
            this.size = size;
            this.dimensions = dimensions;
            this.cost = cost;
            this.contents = contents;  
            this.totalValue = totalValue; }

        public String getSize() { return size; }
        public String getDimensions() { return dimensions; }
        public double getCost() { return cost; }
        public String getContents() { return contents; }
        public double getTotalValue() { return totalValue; }
        public void setContents(String contents) { this.contents = contents; }
        public void setTotalValue(double totalValue) { this.totalValue = totalValue; }
    }
}
