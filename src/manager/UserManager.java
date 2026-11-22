package manager;

import model.*;
import util.FileManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages user registration, authentication, and retrieval.
 * Responsibilities:
 * - User registration (Donor, NGO, Volunteer, Admin)
 * - User login and authentication
 * - Duplicate username validation
 * - Lookup by ID or username
 * Demonstrates:
 * - ArrayList to store sequential list of users
 * - HashMap for fast O(1) lookup by ID and username
 */
public class UserManager {
    private ArrayList<User> userList;
    private HashMap<Integer, User> userMap;
    private HashMap<String, User> usernameMap;
    private FileManager fileManager;

    public UserManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.userList = new ArrayList<>();
        this.userMap = new HashMap<>();
        this.usernameMap = new HashMap<>();
        loadUsers();
    }

    private void loadUsers() {
        ArrayList<User> loaded = fileManager.loadUsers();
        if (loaded.isEmpty()) {
            seedInitialUsers();
        } else {
            for (User u : loaded) {
                userList.add(u);
                userMap.put(u.getUserId(), u);
                usernameMap.put(u.getUsername().toLowerCase(), u);
            }
        }
    }

    /**
     * Seeds initial fictional users if the system is started for the first time.
     */
    private void seedInitialUsers() {
        registerUser(new Admin(1, "System Admin", "admin@replate.org", "9876543210", "Chandigarh", "admin", "admin123", "ADM-001"));
        registerUser(new Donor(2, "ABC College Mess", "mess@abc.edu", "9811122233", "Chandigarh", "abcmess", "pass123", "College Mess"));
        registerUser(new Donor(3, "Green Leaf Restaurant", "contact@greenleaf.com", "9822233344", "Mohali", "greenleaf", "pass123", "Restaurant"));
        registerUser(new NGO(4, "Helping Hands NGO", "info@helpinghands.org", "9833344455", "Chandigarh", "helpinghands", "pass123", "NGO-CH-101", 120));
        registerUser(new NGO(5, "Hope Foundation", "contact@hopefdn.org", "9844455566", "Ludhiana", "hopefdn", "pass123", "NGO-LD-202", 80));
        registerUser(new Volunteer(6, "Rahul", "rahul@gmail.com", "9855566677", "Chandigarh", "rahul", "pass123", "Bike", true));
        registerUser(new Volunteer(7, "Aman", "aman@gmail.com", "9866677788", "Mohali", "aman", "pass123", "Car", true));
    }

    public boolean registerUser(User user) {
        if (usernameMap.containsKey(user.getUsername().toLowerCase())) {
            return false; // Username already taken
        }
        userList.add(user);
        userMap.put(user.getUserId(), user);
        usernameMap.put(user.getUsername().toLowerCase(), user);
        fileManager.saveUsers(userList);
        return true;
    }

    public User login(String username, String password) {
        User user = usernameMap.get(username.toLowerCase());
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User getUserById(int id) {
        return userMap.get(id);
    }

    public boolean isUsernameTaken(String username) {
        return usernameMap.containsKey(username.toLowerCase());
    }

    public ArrayList<User> getAllUsers() {
        return userList;
    }

    public ArrayList<Volunteer> getVolunteers() {
        ArrayList<Volunteer> volunteers = new ArrayList<>();
        for (User u : userList) {
            if (u instanceof Volunteer) {
                volunteers.add((Volunteer) u);
            }
        }
        return volunteers;
    }

    public Volunteer getAvailableVolunteer() {
        for (User u : userList) {
            if (u instanceof Volunteer) {
                Volunteer v = (Volunteer) u;
                if (v.isAvailable()) {
                    return v;
                }
            }
        }
        return null;
    }

    public int getNextUserId() {
        int max = 0;
        for (User u : userList) {
            if (u.getUserId() > max) {
                max = u.getUserId();
            }
        }
        return max + 1;
    }

    public void save() {
        fileManager.saveUsers(userList);
    }
}
