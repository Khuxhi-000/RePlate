package model;

/**
 * Represents an NGO requesting and receiving food.
 * Extends User to demonstrate Inheritance and Polymorphism.
 */
public class NGO extends User {
    private String registrationNumber;
    private int peopleServedCount;

    public NGO() {
        super();
        setRole(UserRole.NGO);
    }

    public NGO(int userId, String name, String email, String phone, String address,
               String username, String password, String registrationNumber, int peopleServedCount) {
        super(userId, name, email, phone, address, username, password, UserRole.NGO);
        this.registrationNumber = registrationNumber;
        this.peopleServedCount = peopleServedCount;
    }

    @Override
    public void displayRole() {
        System.out.println("Role: NON-GOVERNMENTAL ORGANIZATION (Reg: " + registrationNumber + ")");
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Reg No.   : " + registrationNumber);
        System.out.println("Beneficiaries: Approx " + peopleServedCount + " people");
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getPeopleServedCount() {
        return peopleServedCount;
    }

    public void setPeopleServedCount(int peopleServedCount) {
        this.peopleServedCount = peopleServedCount;
    }

    @Override
    public String toCsv() {
        // Format: NGO,userId,name,email,phone,address,username,password,regNo,peopleServed
        return "NGO," + getUserId() + "," + escape(getName()) + "," + escape(getEmail()) + "," 
                + escape(getPhone()) + "," + escape(getAddress()) + "," + escape(getUsername()) + "," 
                + escape(getPassword()) + "," + escape(registrationNumber) + "," + peopleServedCount;
    }

    private String escape(String s) {
        return s == null ? "" : s.replace(",", ";");
    }
}
