package model;

/**
 * Represents a Volunteer delivering food from Donor to NGO.
 * Extends User to demonstrate Inheritance and Polymorphism.
 */
public class Volunteer extends User {
    private String vehicleType; // e.g. "Bike", "Car", "Scooter"
    private boolean isAvailable;

    public Volunteer() {
        super();
        setRole(UserRole.VOLUNTEER);
        this.isAvailable = true;
    }

    public Volunteer(int userId, String name, String email, String phone, String address,
                     String username, String password, String vehicleType, boolean isAvailable) {
        super(userId, name, email, phone, address, username, password, UserRole.VOLUNTEER);
        this.vehicleType = vehicleType;
        this.isAvailable = isAvailable;
    }

    @Override
    public void displayRole() {
        System.out.println("Role: VOLUNTEER (Vehicle: " + vehicleType + ")");
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Vehicle   : " + vehicleType);
        System.out.println("Available : " + (isAvailable ? "Yes" : "No (Busy)"));
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toCsv() {
        // Format: VOLUNTEER,userId,name,email,phone,address,username,password,vehicleType,isAvailable
        return "VOLUNTEER," + getUserId() + "," + escape(getName()) + "," + escape(getEmail()) + "," 
                + escape(getPhone()) + "," + escape(getAddress()) + "," + escape(getUsername()) + "," 
                + escape(getPassword()) + "," + escape(vehicleType) + "," + isAvailable;
    }

    private String escape(String s) {
        return s == null ? "" : s.replace(",", ";");
    }
}
