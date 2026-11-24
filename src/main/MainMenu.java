package main;

import manager.*;
import model.*;
import service.*;
import util.*;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Main terminal-based menu interface for the RePlate application.
 * Demonstrates:
 * - Menu-driven user navigation
 * - Role-based dashboard (Donor, NGO, Volunteer, Admin)
 * - Error handling for all user inputs
 * - Full end-to-end food rescue workflow
 */
public class MainMenu {
    private FileManager fileManager;
    private UserManager userManager;
    private DonationManager donationManager;
    private RequestManager requestManager;
    private AssignmentManager assignmentManager;
    private DonationHistory donationHistory;
    private DistributionManager distributionManager;
    private DonationMatcher donationMatcher;
    private DonationExpiryChecker expiryChecker;

    public MainMenu() {
        // Initialize file management and managers
        this.fileManager = new FileManager();
        this.userManager = new UserManager(fileManager);
        this.donationManager = new DonationManager(fileManager);
        this.requestManager = new RequestManager(fileManager);
        this.assignmentManager = new AssignmentManager(fileManager);
        this.donationHistory = new DonationHistory(fileManager);
        this.distributionManager = new DistributionManager(
                assignmentManager, donationManager, userManager, requestManager, donationHistory, fileManager);
        this.donationMatcher = new DonationMatcher();

        // Initialize and start the background donation-expiry checker thread
        // (Multithreading)
        this.expiryChecker = new DonationExpiryChecker(donationManager, 10000);
        this.expiryChecker.setDaemon(true); // Daemon thread runs concurrently in background
        this.expiryChecker.start();
    }

    public static void main(String[] args) {
        MainMenu app = new MainMenu();
        app.run();
    }

    public void run() {
        boolean running = true;
        while (running) {
            printHeader("REPLATE - FOOD RESCUE & SURPLUS NETWORK");
            System.out.println("1. Register New Account");
            System.out.println("2. Login to Account");
            System.out.println("3. View Available Donations");
            System.out.println("4. Search Food Donations");
            System.out.println("5. Community Impact & Statistics");
            System.out.println("6. Exit");
            System.out.println("----------------------------------------");

            int choice = InputValidator.readInt("Enter your choice (1-6): ", 1, 6);

            switch (choice) {
                case 1:
                    handleRegistration();
                    break;
                case 2:
                    handleLogin();
                    break;
                case 3:
                    handleViewAvailableDonations();
                    break;
                case 4:
                    handleSearchDonations();
                    break;
                case 5:
                    ImpactReport.displayImpactReport(donationManager, assignmentManager, userManager);
                    InputValidator.pressEnterToContinue();
                    break;
                case 6:
                    running = false;
                    System.out.println("\n[System] Stopping background expiry checker thread...");
                    if (expiryChecker != null) {
                        expiryChecker.stopChecker();
                    }
                    System.out.println("[System] Background thread stopped cleanly.");
                    System.out.println("Thank you for supporting RePlate Food Rescue Network. Goodbye!");
                    break;
            }
        }
    }

    // ==========================================
    // REGISTRATION & LOGIN
    // ==========================================

    private void handleRegistration() {
        printHeader("USER REGISTRATION");
        System.out.println("Select Account Type:");
        System.out.println("1. Food Donor (College Mess, Restaurant, Hotel)");
        System.out.println("2. NGO / Charity Organization");
        System.out.println("3. Volunteer");
        System.out.println("4. Back to Main Menu");

        int roleChoice = InputValidator.readInt("Enter choice (1-4): ", 1, 4);
        if (roleChoice == 4)
            return;

        int nextId = userManager.getNextUserId();
        String name = InputValidator.readNonEmptyString("Enter Full/Organization Name: ");
        String email = InputValidator.readValidEmail("Enter Email: ");
        String phone = InputValidator.readValidPhone("Enter Phone Number: ");
        System.out.println("Locations supported: Chandigarh, Mohali, Ludhiana, Barnala (or enter your city)");
        String address = InputValidator.readNonEmptyString("Enter Location/City: ");

        String username;
        while (true) {
            username = InputValidator.readNonEmptyString("Enter Desired Username: ");
            if (userManager.isUsernameTaken(username)) {
                System.out.println(">> Error: Username already taken! Please choose another.");
            } else {
                break;
            }
        }
        String password = InputValidator.readNonEmptyString("Enter Password: ");

        User newUser = null;
        switch (roleChoice) {
            case 1:
                String orgType = InputValidator
                        .readNonEmptyString("Enter Organization Type (e.g. College Mess, Restaurant, Bakery): ");
                newUser = new Donor(nextId, name, email, phone, address, username, password, orgType);
                break;
            case 2:
                String regNo = InputValidator.readValidNgoRegNo("Enter NGO Registration Number (e.g. NGO-CH-105): ");
                int peopleServed = InputValidator.readPositiveInt("Estimated number of people served daily: ");
                newUser = new NGO(nextId, name, email, phone, address, username, password, regNo, peopleServed);
                break;
            case 3:
                String vehicle = InputValidator.readNonEmptyString("Enter Vehicle Type (e.g. Bike, Car, Scooter): ");
                newUser = new Volunteer(nextId, name, email, phone, address, username, password, vehicle, true);
                break;
        }

        if (newUser != null) {
            userManager.registerUser(newUser);
            System.out.println("\n>> Registration Successful! Assigned User ID: " + nextId);
            newUser.displayRole();
        }
        InputValidator.pressEnterToContinue();
    }

    private void handleLogin() {
        printHeader("ACCOUNT LOGIN");
        String username = InputValidator.readNonEmptyString("Username: ");
        String password = InputValidator.readNonEmptyString("Password: ");

        User user = userManager.login(username, password);
        if (user == null) {
            System.out.println("\n>> Error: Invalid username or password. Please try again.");
            InputValidator.pressEnterToContinue();
            return;
        }

        System.out.println("\n>> Welcome back, " + user.getName() + "!");
        // Polymorphism: calling overridden method on User reference
        user.displayRole();
        InputValidator.pressEnterToContinue();

        switch (user.getRole()) {
            case DONOR:
                donorMenu((Donor) user);
                break;
            case NGO:
                ngoMenu((NGO) user);
                break;
            case VOLUNTEER:
                volunteerMenu((Volunteer) user);
                break;
            case ADMIN:
                adminMenu((Admin) user);
                break;
        }
    }

    // ==========================================
    // DONOR WORKFLOW
    // ==========================================

    private void donorMenu(Donor donor) {
        boolean inDonorMenu = true;
        while (inDonorMenu) {
            printHeader("DONOR DASHBOARD - " + donor.getName().toUpperCase());
            System.out.println("1. Add Food Donation");
            System.out.println("2. View My Donations");
            System.out.println("3. Update Donation Quantity");
            System.out.println("4. Cancel Donation");
            System.out.println("5. Logout");
            System.out.println("----------------------------------------");

            int choice = InputValidator.readInt("Enter your choice (1-5): ", 1, 5);
            switch (choice) {
                case 1:
                    handleAddFoodDonation(donor);
                    break;
                case 2:
                    handleViewDonorDonations(donor);
                    break;
                case 3:
                    handleUpdateDonation(donor);
                    break;
                case 4:
                    handleCancelDonation(donor);
                    break;
                case 5:
                    inDonorMenu = false;
                    System.out.println("\n>> Logged out from Donor account.");
                    break;
            }
        }
    }

    private void handleAddFoodDonation(Donor donor) {
        printHeader("ADD FOOD DONATION");
        String foodName = InputValidator.readNonEmptyString("Food Name (e.g. Rice, Bread, Cooked Food): ");

        System.out.println("\nSelect Food Category:");
        FoodCategory[] categories = FoodCategory.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        int catChoice = InputValidator.readInt("Enter category number (1-" + categories.length + "): ", 1,
                categories.length);
        FoodCategory category = categories[catChoice - 1];

        int quantity = InputValidator.readPositiveInt("Quantity (number of packets/servings): ");
        String prepTime = InputValidator.readNonEmptyString("Preparation Time (e.g. 05:00 PM): ");
        String expiryTime = InputValidator.readNonEmptyString("Expiry Time (e.g. 09:00 PM): ");

        System.out.println("Default Location from profile: " + donor.getAddress());
        String locChoice = InputValidator.readString("Press Enter to use profile location or enter new location: ");
        String location = locChoice.isEmpty() ? donor.getAddress() : locChoice;

        int newId = donationManager.getNextDonationId();
        FoodItem item = new FoodItem(foodName, category, true);
        FoodDonation donation = new FoodDonation(newId, donor.getUserId(), donor.getName(), item,
                quantity, prepTime, expiryTime, location, DonationStatus.AVAILABLE, LocalDate.now().toString());

        donationManager.addDonation(donation);

        System.out.println("\n>> Donation added successfully!");
        System.out.println(">> Donation ID: " + newId);
        System.out.println(">> Status     : AVAILABLE (" + quantity + " packets)");

        // Try to see if any pending NGO request can match this immediately
        checkPendingRequestsForNewDonation(donation);

        InputValidator.pressEnterToContinue();
    }

    private void checkPendingRequestsForNewDonation(FoodDonation donation) {
        for (DonationRequest req : requestManager.getAllRequests()) {
            if ("PENDING".equalsIgnoreCase(req.getStatus())) {
                ArrayList<FoodDonation> singleList = new ArrayList<>();
                singleList.add(donation);
                FoodDonation match = donationMatcher.findBestMatch(req, singleList);
                if (match != null) {
                    System.out.println("\n>> Notice: A pending request from NGO '" + req.getNgoName()
                            + "' matches this donation!");
                    break;
                }
            }
        }
    }

    private void handleViewDonorDonations(Donor donor) {
        printHeader("MY DONATIONS - " + donor.getName());
        ArrayList<FoodDonation> list = donationManager.getDonationsByDonor(donor.getUserId());
        if (list.isEmpty()) {
            System.out.println("You have not added any donations yet.");
        } else {
            for (FoodDonation d : list) {
                // Interface polymorphism: Displayable
                d.displayDetails();
            }
        }
        InputValidator.pressEnterToContinue();
    }

    private void handleUpdateDonation(Donor donor) {
        printHeader("UPDATE DONATION QUANTITY");
        ArrayList<FoodDonation> list = donationManager.getDonationsByDonor(donor.getUserId());
        if (list.isEmpty()) {
            System.out.println("You have no donations to update.");
            InputValidator.pressEnterToContinue();
            return;
        }

        int id = InputValidator.readPositiveInt("Enter Donation ID to update: ");
        FoodDonation d = donationManager.getDonationById(id);

        if (d == null || d.getDonorId() != donor.getUserId()) {
            System.out.println(">> Error: Donation not found or does not belong to you.");
        } else if (d.getStatus() != DonationStatus.AVAILABLE) {
            System.out.println(">> Error: Cannot update donation. Current status: " + d.getStatus());
        } else {
            System.out.println("Current Quantity: " + d.getQuantity() + " packets");
            int newQty = InputValidator.readInt("Enter New Quantity (0 to mark fully reserved): ", 0, 10000);
            donationManager.updateDonationQuantity(id, newQty);
            System.out.println(">> Donation updated successfully.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void handleCancelDonation(Donor donor) {
        printHeader("CANCEL DONATION");
        int id = InputValidator.readPositiveInt("Enter Donation ID to cancel: ");
        boolean success = donationManager.cancelDonation(id, donor.getUserId());
        if (success) {
            System.out.println(">> Donation #" + id + " has been cancelled.");
        } else {
            System.out.println(">> Error: Could not cancel donation. Ensure it is yours and currently AVAILABLE.");
        }
        InputValidator.pressEnterToContinue();
    }

    // ==========================================
    // NGO WORKFLOW
    // ==========================================

    private void ngoMenu(NGO ngo) {
        boolean inNgoMenu = true;
        while (inNgoMenu) {
            printHeader("NGO DASHBOARD - " + ngo.getName().toUpperCase());
            System.out.println("1. View Available Food Donations");
            System.out.println("2. Search & Sort Food Donations");
            System.out.println("3. Create Food Request (With Auto-Matching)");
            System.out.println("4. View My Food Requests");
            System.out.println("5. Track Request & Deliveries");
            System.out.println("6. Logout");
            System.out.println("----------------------------------------");

            int choice = InputValidator.readInt("Enter your choice (1-6): ", 1, 6);
            switch (choice) {
                case 1:
                    handleViewAvailableDonations();
                    break;
                case 2:
                    handleSearchDonations();
                    break;
                case 3:
                    handleCreateFoodRequest(ngo);
                    break;
                case 4:
                    handleViewNgoRequests(ngo);
                    break;
                case 5:
                    handleTrackRequest(ngo);
                    break;
                case 6:
                    inNgoMenu = false;
                    System.out.println("\n>> Logged out from NGO account.");
                    break;
            }
        }
    }

    private void handleCreateFoodRequest(NGO ngo) {
        printHeader("CREATE FOOD REQUEST");

        System.out.println("Select Food Category Required:");
        FoodCategory[] categories = FoodCategory.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        int catChoice = InputValidator.readInt("Enter category (1-" + categories.length + "): ", 1, categories.length);
        FoodCategory category = categories[catChoice - 1];

        int qtyRequired = InputValidator.readPositiveInt("Quantity Required (packets): ");

        System.out.println("Profile Location: " + ngo.getAddress());
        String locChoice = InputValidator.readString("Press Enter to use profile location or enter delivery location: ");
        String location = locChoice.isEmpty() ? ngo.getAddress() : locChoice;

        System.out.println("\nSelect Request Priority:");
        System.out.println("1. LOW");
        System.out.println("2. MEDIUM");
        System.out.println("3. HIGH");
        System.out.println("4. URGENT");
        int prioChoice = InputValidator.readInt("Enter priority (1-4): ", 1, 4);
        RequestPriority priority = RequestPriority.values()[prioChoice - 1];

        int reqId = requestManager.getNextRequestId();
        DonationRequest request = new DonationRequest(reqId, ngo.getUserId(), ngo.getName(),
                category, qtyRequired, location, priority, "PENDING", LocalDate.now().toString());

        requestManager.createRequest(request);

        System.out.println("\n>> Request created successfully!");
        System.out.println(">> Request ID: " + reqId);
        System.out.println(">> Priority  : " + priority);

        // Perform instant matching check
        System.out.println("\n>> Searching available food donations matching your request...");
        FoodDonation match = donationMatcher.findBestMatch(request, donationManager.getAvailableDonations());

        if (match != null) {
            System.out.println("\n========================================");
            System.out.println("             MATCH FOUND!");
            System.out.println("========================================");
            System.out.println("Donation ID        : " + match.getDonationId());
            System.out.println("Donor              : " + match.getDonorName());
            System.out.println("Food Category      : " + match.getFoodItem().getCategory());
            System.out.println("Available Quantity : " + match.getQuantity());
            System.out.println("Requested Quantity : " + request.getQuantityRequired());
            System.out.println("Location           : " + match.getLocation());
            System.out.println("Expiry Time        : " + match.getExpiryTime());
            System.out.println("========================================");

            String confirm = InputValidator.readNonEmptyString("Do you want to allocate this food now? (yes/no): ");
            if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
                try {
                    int allocated = donationMatcher.allocateQuantity(match, request);
                    donationManager.save();
                    requestManager.save();

                    System.out.println("\n>> Allocated Quantity: " + allocated + " packets");
                    System.out.println(">> Remaining in Donation: " + match.getQuantity() + " packets");

                    // Calculate estimated transit distance using 2D CityDistanceMatrix
                    int transitDistance = CityDistanceMatrix.getDistance(match.getLocation(), request.getLocation());

                    // Assign volunteer
                    Volunteer volunteer = userManager.getAvailableVolunteer();
                    VolunteerAssignment assignment = assignmentManager.createAssignment(
                            match.getDonationId(), request.getRequestId(), volunteer, match, request, allocated);

                    System.out.println("\n========================================");
                    System.out.println("         VOLUNTEER ASSIGNMENT");
                    System.out.println("========================================");
                    System.out.println("Assignment ID     : #" + assignment.getAssignmentId());
                    System.out.println("Assigned Volunteer: " + assignment.getVolunteerName());
                    System.out.println("Pickup Location   : " + assignment.getDonorLocation());
                    System.out.println("Delivery Location : " + assignment.getNgoLocation());
                    System.out.println("Est. Distance     : " + transitDistance + " km (via 2D Distance Matrix)");
                    System.out.println("Status            : " + assignment.getStatus());
                    System.out.println("========================================");

                    if (volunteer == null) {
                        System.out.println(">> Notice: All volunteers are currently busy. Assignment is queued for next available volunteer.");
                    } else {
                        userManager.save();
                        System.out.println(">> Volunteer has been notified for pickup!");
                    }
                } catch (DonationExpiredException | InsufficientQuantityException e) {
                    System.out.println("\n>> Allocation Error: " + e.getMessage());
                }
            }
        } else {
            System.out.println("\n>> No immediate match found for " + category + " in " + location + ".");
            System.out.println(">> Your request has been queued with priority " + priority + " and will be matched when surplus food arrives.");
        }

        InputValidator.pressEnterToContinue();
    }

    private void handleViewNgoRequests(NGO ngo) {
        printHeader("MY REQUESTS - " + ngo.getName());
        ArrayList<DonationRequest> requests = requestManager.getRequestsByNgo(ngo.getUserId());
        if (requests.isEmpty()) {
            System.out.println("No requests placed yet.");
        } else {
            for (DonationRequest r : requests) {
                r.displayDetails();
            }
        }
        InputValidator.pressEnterToContinue();
    }

    private void handleTrackRequest(NGO ngo) {
        printHeader("TRACK REQUEST STATUS");
        int reqId = InputValidator.readPositiveInt("Enter Request ID to track: ");
        DonationRequest req = requestManager.getRequestById(reqId);

        if (req == null || req.getNgoId() != ngo.getUserId()) {
            System.out.println(">> Error: Request ID not found under your organization.");
        } else {
            req.displayDetails();
            // Check for assignments
            System.out.println("Related Volunteer Assignments:");
            boolean found = false;
            for (VolunteerAssignment a : assignmentManager.getAllAssignments()) {
                if (a.getRequestId() == reqId) {
                    a.displayDetails();
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No volunteer assignment linked yet (Request is still " + req.getStatus() + ").");
            }
        }
        InputValidator.pressEnterToContinue();
    }

    // ==========================================
    // VOLUNTEER WORKFLOW
    // ==========================================

    private void volunteerMenu(Volunteer volunteer) {
        boolean inVolMenu = true;
        while (inVolMenu) {
            printHeader("VOLUNTEER DASHBOARD - " + volunteer.getName().toUpperCase());
            System.out.println("1. View My Active Assignments");
            System.out.println("2. Mark Pickup Completed");
            System.out.println("3. Mark Delivery Completed (Generates Receipt)");
            System.out.println("4. View Delivery History");
            System.out.println("5. Logout");
            System.out.println("----------------------------------------");

            int choice = InputValidator.readInt("Enter your choice (1-5): ", 1, 5);
            switch (choice) {
                case 1:
                    handleViewVolunteerAssignments(volunteer);
                    break;
                case 2:
                    handleMarkPickup(volunteer);
                    break;
                case 3:
                    handleMarkDelivery(volunteer);
                    break;
                case 4:
                    donationHistory.displayHistory();
                    InputValidator.pressEnterToContinue();
                    break;
                case 5:
                    inVolMenu = false;
                    System.out.println("\n>> Logged out from Volunteer account.");
                    break;
            }
        }
    }

    private void handleViewVolunteerAssignments(Volunteer volunteer) {
        printHeader("ACTIVE ASSIGNMENTS - " + volunteer.getName());
        ArrayList<VolunteerAssignment> list = assignmentManager.getAssignmentsForVolunteer(volunteer.getUserId());
        if (list.isEmpty()) {
            System.out.println("You have no assigned deliveries right now.");
        } else {
            for (VolunteerAssignment a : list) {
                a.displayDetails();
            }
        }
        InputValidator.pressEnterToContinue();
    }

    private void handleMarkPickup(Volunteer volunteer) {
        printHeader("MARK PICKUP COMPLETED");
        int aId = InputValidator.readPositiveInt("Enter Assignment ID to mark picked up: ");
        VolunteerAssignment a = assignmentManager.getAssignmentById(aId);

        if (a == null || a.getVolunteerId() != volunteer.getUserId()) {
            System.out.println(">> Error: Assignment not found or not assigned to you.");
        } else {
            String time = InputValidator.readNonEmptyString("Enter Current Pickup Time (e.g. 05:30 PM): ");
            String notes = InputValidator.readString("Enter any pickup remarks (optional): ");
            distributionManager.completePickup(aId, time, notes);
        }
        InputValidator.pressEnterToContinue();
    }

    private void handleMarkDelivery(Volunteer volunteer) {
        printHeader("MARK DELIVERY COMPLETED");
        int aId = InputValidator.readPositiveInt("Enter Assignment ID to mark delivered: ");
        VolunteerAssignment a = assignmentManager.getAssignmentById(aId);

        if (a == null || a.getVolunteerId() != volunteer.getUserId()) {
            System.out.println(">> Error: Assignment not found or not assigned to you.");
        } else {
            String time = InputValidator.readNonEmptyString("Enter Current Delivery Time (e.g. 06:15 PM): ");
            String receiver = InputValidator.readNonEmptyString("Enter NGO Receiver Person's Name: ");
            distributionManager.completeDelivery(aId, time, receiver);
        }
        InputValidator.pressEnterToContinue();
    }

    // ==========================================
    // ADMIN WORKFLOW
    // ==========================================

    private void adminMenu(Admin admin) {
        boolean inAdminMenu = true;
        while (inAdminMenu) {
            printHeader("ADMIN DASHBOARD - " + admin.getName().toUpperCase());
            System.out.println("1. View All Registered Users");
            System.out.println("2. View All Food Donations");
            System.out.println("3. View All NGO Requests (Priority Queue order)");
            System.out.println("4. View All Volunteer Assignments");
            System.out.println("5. View Full Community Impact Report");
            System.out.println("6. View System Event History");
            System.out.println("7. View Regional City Distance Matrix (2D Array)");
            System.out.println("8. Check Background Expiry Checker Thread Status");
            System.out.println("9. Logout");
            System.out.println("----------------------------------------");

            int choice = InputValidator.readInt("Enter choice (1-9): ", 1, 9);
            switch (choice) {
                case 1:
                    printHeader("ALL REGISTERED USERS");
                    for (User u : userManager.getAllUsers()) {
                        // Polymorphism in action
                        u.displayDetails();
                        u.displayRole();
                        System.out.println("----------------------------------------");
                    }
                    InputValidator.pressEnterToContinue();
                    break;
                case 2:
                    printHeader("ALL FOOD DONATIONS");
                    for (FoodDonation d : donationManager.getAllDonations()) {
                        d.displayDetails();
                    }
                    InputValidator.pressEnterToContinue();
                    break;
                case 3:
                    printHeader("ALL NGO REQUESTS");
                    for (DonationRequest r : requestManager.getAllRequests()) {
                        r.displayDetails();
                    }
                    InputValidator.pressEnterToContinue();
                    break;
                case 4:
                    printHeader("ALL VOLUNTEER ASSIGNMENTS");
                    for (VolunteerAssignment a : assignmentManager.getAllAssignments()) {
                        a.displayDetails();
                    }
                    InputValidator.pressEnterToContinue();
                    break;
                case 5:
                    ImpactReport.displayImpactReport(donationManager, assignmentManager, userManager);
                    InputValidator.pressEnterToContinue();
                    break;
                case 6:
                    donationHistory.displayHistory();
                    InputValidator.pressEnterToContinue();
                    break;
                case 7:
                    CityDistanceMatrix.printDistanceTable();
                    InputValidator.pressEnterToContinue();
                    break;
                case 8:
                    printHeader("BACKGROUND EXPIRY CHECKER THREAD STATUS");
                    if (expiryChecker != null) {
                        System.out.println("Thread Name     : " + expiryChecker.getName());
                        System.out.println("Thread Priority : " + expiryChecker.getPriority());
                        System.out.println("Thread State    : " + expiryChecker.getState());
                        System.out.println("Is Alive?       : " + expiryChecker.isAlive());
                        System.out.println("Is Daemon?      : " + expiryChecker.isDaemon());
                        System.out.println("Check Interval  : 10,000 ms (10 seconds)");
                    } else {
                        System.out.println("Expiry checker thread is not initialized.");
                    }
                    InputValidator.pressEnterToContinue();
                    break;
                case 9:
                    inAdminMenu = false;
                    System.out.println("\n>> Logged out from Admin account.");
                    break;
            }
        }
    }

    // ==========================================
    // PUBLIC / GUEST BROWSING & SEARCHING
    // ==========================================

    private void handleViewAvailableDonations() {
        printHeader("AVAILABLE FOOD DONATIONS");
        ArrayList<FoodDonation> list = donationManager.getAvailableDonations();
        if (list.isEmpty()) {
            System.out.println("No donations are currently available. Check back soon!");
        } else {
            System.out.println("Found " + list.size() + " active donation(s):\n");
            for (FoodDonation d : list) {
                d.displayDetails();
            }
        }
        InputValidator.pressEnterToContinue();
    }

    private void handleSearchDonations() {
        printHeader("SEARCH & SORT FOOD DONATIONS");
        System.out.println("1. Search by Food Name");
        System.out.println("2. Search by Category");
        System.out.println("3. Search by Location");
        System.out.println("4. Sort by Quantity (Descending)");
        System.out.println("5. Sort by Expiry Time");
        System.out.println("6. Back");
        System.out.println("----------------------------------------");

        int choice = InputValidator.readInt("Enter choice (1-6): ", 1, 6);
        ArrayList<FoodDonation> available = donationManager.getAvailableDonations();
        ArrayList<FoodDonation> results = new ArrayList<>();

        switch (choice) {
            case 1:
                String name = InputValidator.readNonEmptyString("Enter food name keyword: ");
                results = FoodSearch.searchByName(name, available);
                break;
            case 2:
                System.out.println("Select Category:");
                FoodCategory[] cats = FoodCategory.values();
                for (int i = 0; i < cats.length; i++) {
                    System.out.println((i + 1) + ". " + cats[i]);
                }
                int c = InputValidator.readInt("Choice: ", 1, cats.length);
                results = FoodSearch.searchByCategory(cats[c - 1], available);
                break;
            case 3:
                String loc = InputValidator.readNonEmptyString("Enter City/Location: ");
                results = FoodSearch.searchByLocation(loc, available);
                break;
            case 4:
                results = new ArrayList<>(available);
                FoodSearch.sortByQuantity(results, false);
                break;
            case 5:
                results = new ArrayList<>(available);
                FoodSearch.sortByExpiry(results);
                break;
            case 6:
                return;
        }

        printHeader("SEARCH / SORT RESULTS");
        if (results.isEmpty()) {
            System.out.println("No matching donations found.");
        } else {
            System.out.println("Found " + results.size() + " result(s):\n");
            for (FoodDonation d : results) {
                d.displayDetails();
            }
        }
        InputValidator.pressEnterToContinue();
    }

    private void printHeader(String title) {
        System.out.println("\n========================================");
        System.out.println("  " + title);
        System.out.println("========================================");
    }
}
