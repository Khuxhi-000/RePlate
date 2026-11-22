package model;

/**
 * Represents the assignment of an available volunteer to transport
 * allocated surplus food from Donor to NGO.
 * Implements Displayable.
 */
public class VolunteerAssignment implements Displayable {
    private int assignmentId;
    private int donationId;
    private int requestId;
    private int volunteerId;
    private String volunteerName;
    private String donorLocation;
    private String ngoLocation;
    private String foodDetails;
    private int quantity;
    private DonationStatus status; // ASSIGNED / RESERVED, PICKED_UP, DELIVERED

    public VolunteerAssignment() {
    }

    public VolunteerAssignment(int assignmentId, int donationId, int requestId, int volunteerId,
                               String volunteerName, String donorLocation, String ngoLocation,
                               String foodDetails, int quantity, DonationStatus status) {
        this.assignmentId = assignmentId;
        this.donationId = donationId;
        this.requestId = requestId;
        this.volunteerId = volunteerId;
        this.volunteerName = volunteerName;
        this.donorLocation = donorLocation;
        this.ngoLocation = ngoLocation;
        this.foodDetails = foodDetails;
        this.quantity = quantity;
        this.status = status;
    }

    @Override
    public void displayDetails() {
        System.out.println("========================================");
        System.out.println("Assignment ID   : " + assignmentId);
        System.out.println("Volunteer       : " + volunteerName + " (ID: " + volunteerId + ")");
        System.out.println("Donation ID     : " + donationId);
        System.out.println("Request ID      : " + requestId);
        System.out.println("Food Details    : " + foodDetails);
        System.out.println("Quantity        : " + quantity + " packets");
        System.out.println("Pickup Location : " + donorLocation);
        System.out.println("Delivery Location: " + ngoLocation);
        System.out.println("Current Status  : " + status);
        System.out.println("========================================");
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getDonationId() {
        return donationId;
    }

    public void setDonationId(int donationId) {
        this.donationId = donationId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(int volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getVolunteerName() {
        return volunteerName;
    }

    public void setVolunteerName(String volunteerName) {
        this.volunteerName = volunteerName;
    }

    public String getDonorLocation() {
        return donorLocation;
    }

    public void setDonorLocation(String donorLocation) {
        this.donorLocation = donorLocation;
    }

    public String getNgoLocation() {
        return ngoLocation;
    }

    public void setNgoLocation(String ngoLocation) {
        this.ngoLocation = ngoLocation;
    }

    public String getFoodDetails() {
        return foodDetails;
    }

    public void setFoodDetails(String foodDetails) {
        this.foodDetails = foodDetails;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public DonationStatus getStatus() {
        return status;
    }

    public void setStatus(DonationStatus status) {
        this.status = status;
    }

    public String toCsv() {
        // Format: assignmentId,donationId,requestId,volunteerId,volunteerName,donorLoc,ngoLoc,foodDetails,quantity,status
        return assignmentId + "," + donationId + "," + requestId + "," + volunteerId + "," 
                + escape(volunteerName) + "," + escape(donorLocation) + "," + escape(ngoLocation) + "," 
                + escape(foodDetails) + "," + quantity + "," + status.name();
    }

    private String escape(String s) {
        return s == null ? "" : s.replace(",", ";");
    }
}
