package model;

/**
 * Represents a Food Donor (e.g., College Mess, Restaurant, Hotel).
 * Extends User to demonstrate Inheritance and Polymorphism.
 */
public class Donor extends User {
    private String organizationType; // e.g. "College Mess", "Restaurant", "Hotel"

    public Donor() {
        super();
        setRole(UserRole.DONOR);
    }

    public Donor(int userId, String name, String email, String phone, String address,
                 String username, String password, String organizationType) {
        super(userId, name, email, phone, address, username, password, UserRole.DONOR);
        this.organizationType = organizationType;
    }

    @Override
    public void displayRole() {
        System.out.println("Role: FOOD DONOR (" + organizationType + ")");
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Org Type  : " + organizationType);
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    @Override
    public String toCsv() {
        // Format: DONOR,userId,name,email,phone,address,username,password,organizationType
        return "DONOR," + getUserId() + "," + escape(getName()) + "," + escape(getEmail()) + "," 
                + escape(getPhone()) + "," + escape(getAddress()) + "," + escape(getUsername()) + "," 
                + escape(getPassword()) + "," + escape(organizationType);
    }

    private String escape(String s) {
        return s == null ? "" : s.replace(",", ";");
    }
}
