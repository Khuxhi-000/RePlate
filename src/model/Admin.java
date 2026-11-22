package model;

/**
 * Represents an Admin managing and monitoring the system.
 * Extends User to demonstrate Inheritance and Polymorphism.
 */
public class Admin extends User {
    private String adminCode;

    public Admin() {
        super();
        setRole(UserRole.ADMIN);
    }

    public Admin(int userId, String name, String email, String phone, String address,
                 String username, String password, String adminCode) {
        super(userId, name, email, phone, address, username, password, UserRole.ADMIN);
        this.adminCode = adminCode;
    }

    @Override
    public void displayRole() {
        System.out.println("Role: SYSTEM ADMINISTRATOR (Code: " + adminCode + ")");
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Admin Code: " + adminCode);
    }

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }

    @Override
    public String toCsv() {
        // Format: ADMIN,userId,name,email,phone,address,username,password,adminCode
        return "ADMIN," + getUserId() + "," + escape(getName()) + "," + escape(getEmail()) + "," 
                + escape(getPhone()) + "," + escape(getAddress()) + "," + escape(getUsername()) + "," 
                + escape(getPassword()) + "," + escape(adminCode);
    }

    private String escape(String s) {
        return s == null ? "" : s.replace(",", ";");
    }
}
