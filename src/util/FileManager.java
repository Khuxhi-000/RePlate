package util;

import model.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Handles text/CSV file persistence for the application.
 * Manages data loading on startup and data saving when changes occur.
 * Demonstrates Java File I/O: File, BufferedReader, BufferedWriter, FileWriter, FileReader.
 */
public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + File.separator + "users.txt";
    private static final String DONATIONS_FILE = DATA_DIR + File.separator + "donations.txt";
    private static final String REQUESTS_FILE = DATA_DIR + File.separator + "requests.txt";
    private static final String ASSIGNMENTS_FILE = DATA_DIR + File.separator + "assignments.txt";
    private static final String HISTORY_FILE = DATA_DIR + File.separator + "history.txt";

    public FileManager() {
        initDataDirectory();
    }

    /**
     * Initializes the data directory and files if they do not exist.
     */
    private void initDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        ensureFileExists(USERS_FILE);
        ensureFileExists(DONATIONS_FILE);
        ensureFileExists(REQUESTS_FILE);
        ensureFileExists(ASSIGNMENTS_FILE);
        ensureFileExists(HISTORY_FILE);
    }

    private void ensureFileExists(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.out.println(">> Warning: Could not create file " + filePath + ": " + e.getMessage());
            }
        }
    }

    // ==========================================
    // USER PERSISTENCE
    // ==========================================

    public ArrayList<User> loadUsers() {
        ArrayList<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists() || file.length() == 0) {
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",");
                if (parts.length < 8) continue;

                String type = parts[0];
                int userId = Integer.parseInt(parts[1]);
                String name = unescape(parts[2]);
                String email = unescape(parts[3]);
                String phone = unescape(parts[4]);
                String address = unescape(parts[5]);
                String username = unescape(parts[6]);
                String password = unescape(parts[7]);

                switch (type) {
                    case "DONOR":
                        String orgType = parts.length > 8 ? unescape(parts[8]) : "Mess";
                        users.add(new Donor(userId, name, email, phone, address, username, password, orgType));
                        break;
                    case "NGO":
                        String regNo = parts.length > 8 ? unescape(parts[8]) : "REG-000";
                        int people = parts.length > 9 ? Integer.parseInt(parts[9]) : 50;
                        users.add(new NGO(userId, name, email, phone, address, username, password, regNo, people));
                        break;
                    case "VOLUNTEER":
                        String vehicle = parts.length > 8 ? unescape(parts[8]) : "Bike";
                        boolean available = parts.length > 9 ? Boolean.parseBoolean(parts[9]) : true;
                        users.add(new Volunteer(userId, name, email, phone, address, username, password, vehicle, available));
                        break;
                    case "ADMIN":
                        String code = parts.length > 8 ? unescape(parts[8]) : "ADM-01";
                        users.add(new Admin(userId, name, email, phone, address, username, password, code));
                        break;
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println(">> Error reading users file: " + e.getMessage());
        }
        return users;
    }

    public void saveUsers(ArrayList<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User u : users) {
                writer.write(u.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println(">> Error saving users: " + e.getMessage());
        }
    }

    // ==========================================
    // DONATION PERSISTENCE
    // ==========================================

    public ArrayList<FoodDonation> loadDonations() {
        ArrayList<FoodDonation> donations = new ArrayList<>();
        File file = new File(DONATIONS_FILE);
        if (!file.exists() || file.length() == 0) {
            return donations;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] p = line.split(",");
                if (p.length < 11) continue;

                int donId = Integer.parseInt(p[0]);
                int donorId = Integer.parseInt(p[1]);
                String donorName = unescape(p[2]);
                String foodName = unescape(p[3]);
                FoodCategory category = FoodCategory.valueOf(p[4]);
                int qty = Integer.parseInt(p[5]);
                String prepTime = unescape(p[6]);
                String expTime = unescape(p[7]);
                String loc = unescape(p[8]);
                DonationStatus status = DonationStatus.valueOf(p[9]);
                String createdAt = unescape(p[10]);

                FoodItem item = new FoodItem(foodName, category, true);
                FoodDonation don = new FoodDonation(donId, donorId, donorName, item, qty, prepTime, expTime, loc, status, createdAt);
                donations.add(don);
            }
        } catch (Exception e) {
            System.out.println(">> Error reading donations file: " + e.getMessage());
        }
        return donations;
    }

    public void saveDonations(ArrayList<FoodDonation> donations) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DONATIONS_FILE))) {
            for (FoodDonation d : donations) {
                writer.write(d.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println(">> Error saving donations: " + e.getMessage());
        }
    }

    // ==========================================
    // REQUEST PERSISTENCE
    // ==========================================

    public ArrayList<DonationRequest> loadRequests() {
        ArrayList<DonationRequest> requests = new ArrayList<>();
        File file = new File(REQUESTS_FILE);
        if (!file.exists() || file.length() == 0) {
            return requests;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] p = line.split(",");
                if (p.length < 9) continue;

                int reqId = Integer.parseInt(p[0]);
                int ngoId = Integer.parseInt(p[1]);
                String ngoName = unescape(p[2]);
                FoodCategory cat = FoodCategory.valueOf(p[3]);
                int qty = Integer.parseInt(p[4]);
                String loc = unescape(p[5]);
                RequestPriority priority = RequestPriority.valueOf(p[6]);
                String status = unescape(p[7]);
                String date = unescape(p[8]);

                DonationRequest req = new DonationRequest(reqId, ngoId, ngoName, cat, qty, loc, priority, status, date);
                requests.add(req);
            }
        } catch (Exception e) {
            System.out.println(">> Error reading requests file: " + e.getMessage());
        }
        return requests;
    }

    public void saveRequests(ArrayList<DonationRequest> requests) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REQUESTS_FILE))) {
            for (DonationRequest r : requests) {
                writer.write(r.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println(">> Error saving requests: " + e.getMessage());
        }
    }

    // ==========================================
    // ASSIGNMENT PERSISTENCE
    // ==========================================

    public ArrayList<VolunteerAssignment> loadAssignments() {
        ArrayList<VolunteerAssignment> assignments = new ArrayList<>();
        File file = new File(ASSIGNMENTS_FILE);
        if (!file.exists() || file.length() == 0) {
            return assignments;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] p = line.split(",");
                if (p.length < 10) continue;

                int aId = Integer.parseInt(p[0]);
                int dId = Integer.parseInt(p[1]);
                int rId = Integer.parseInt(p[2]);
                int vId = Integer.parseInt(p[3]);
                String vName = unescape(p[4]);
                String donorLoc = unescape(p[5]);
                String ngoLoc = unescape(p[6]);
                String foodDetails = unescape(p[7]);
                int qty = Integer.parseInt(p[8]);
                DonationStatus status = DonationStatus.valueOf(p[9]);

                VolunteerAssignment va = new VolunteerAssignment(aId, dId, rId, vId, vName, donorLoc, ngoLoc, foodDetails, qty, status);
                assignments.add(va);
            }
        } catch (Exception e) {
            System.out.println(">> Error reading assignments file: " + e.getMessage());
        }
        return assignments;
    }

    public void saveAssignments(ArrayList<VolunteerAssignment> assignments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ASSIGNMENTS_FILE))) {
            for (VolunteerAssignment a : assignments) {
                writer.write(a.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println(">> Error saving assignments: " + e.getMessage());
        }
    }

    // ==========================================
    // HISTORY PERSISTENCE
    // ==========================================

    public ArrayList<String> loadHistory() {
        ArrayList<String> records = new ArrayList<>();
        File file = new File(HISTORY_FILE);
        if (!file.exists() || file.length() == 0) {
            return records;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    records.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println(">> Error reading history file: " + e.getMessage());
        }
        return records;
    }

    public void appendHistory(String record) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE, true))) {
            writer.write(record);
            writer.newLine();
        } catch (IOException e) {
            System.out.println(">> Error appending history: " + e.getMessage());
        }
    }

    private String unescape(String s) {
        return s == null ? "" : s.replace(";", ",");
    }
}
