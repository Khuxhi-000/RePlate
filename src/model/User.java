package model;

/**
 * Abstract base class representing a generic user in the system.
 * Demonstrates:
 * - Abstraction (abstract class with abstract method displayRole)
 * - Encapsulation (private fields, public getters and setters)
 * - Interface implementation (implements Displayable)
 */
public abstract class User implements Displayable {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String username;
    private String password;
    private UserRole role;

    // Default constructor
    public User() {
    }

    // Parameterized constructor
    public User(int userId, String name, String email, String phone, String address, 
                String username, String password, UserRole role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Abstract method to be overridden by subclasses (Polymorphism)
    public abstract void displayRole();

    @Override
    public void displayDetails() {
        System.out.println("User ID   : " + userId);
        System.out.println("Name      : " + name);
        System.out.println("Username  : " + username);
        System.out.println("Role      : " + role);
        System.out.println("Email     : " + email);
        System.out.println("Phone     : " + phone);
        System.out.println("Location  : " + address);
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * Converts user state to CSV string for simple file persistence.
     */
    public abstract String toCsv();
}
