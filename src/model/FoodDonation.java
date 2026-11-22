package model;

/**
 * Represents a donation of surplus food offered by a Donor.
 * Implements Displayable interface.
 */
public class FoodDonation implements Displayable {
    private int donationId;
    private int donorId;
    private String donorName;
    private FoodItem foodItem;
    private int quantity;              // in packets / servings
    private String preparationTime;    // e.g. "05:00 PM"
    private String expiryTime;         // e.g. "09:00 PM"
    private String location;           // e.g. "Chandigarh", "Mohali"
    private DonationStatus status;
    private String createdAt;          // date or timestamp string

    public FoodDonation() {
    }

    public FoodDonation(int donationId, int donorId, String donorName, FoodItem foodItem,
                        int quantity, String preparationTime, String expiryTime,
                        String location, DonationStatus status, String createdAt) {
        this.donationId = donationId;
        this.donorId = donorId;
        this.donorName = donorName;
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.preparationTime = preparationTime;
        this.expiryTime = expiryTime;
        this.location = location;
        this.status = status;
        this.createdAt = createdAt;
    }

    @Override
    public void displayDetails() {
        System.out.println("----------------------------------------");
        System.out.println("Donation ID : " + donationId);
        System.out.println("Donor       : " + donorName + " (ID: " + donorId + ")");
        System.out.println("Food Item   : " + (foodItem != null ? foodItem.getName() : "N/A"));
        System.out.println("Category    : " + (foodItem != null ? foodItem.getCategory() : "N/A"));
        System.out.println("Quantity    : " + quantity + " packets");
        System.out.println("Prep Time   : " + preparationTime);
        System.out.println("Expiry Time : " + expiryTime);
        System.out.println("Location    : " + location);
        System.out.println("Status      : " + status);
        System.out.println("Created At  : " + createdAt);
        System.out.println("----------------------------------------");
    }

    public boolean isAvailable() {
        return status == DonationStatus.AVAILABLE && quantity > 0;
    }

    // Getters and Setters
    public int getDonationId() {
        return donationId;
    }

    public void setDonationId(int donationId) {
        this.donationId = donationId;
    }

    public int getDonorId() {
        return donorId;
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(String preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DonationStatus getStatus() {
        return status;
    }

    public void setStatus(DonationStatus status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String toCsv() {
        // Format: donationId,donorId,donorName,foodName,category,quantity,prepTime,expiryTime,location,status,createdAt
        String foodName = (foodItem != null) ? foodItem.getName() : "Unknown";
        String category = (foodItem != null && foodItem.getCategory() != null) ? foodItem.getCategory().name() : "OTHER";
        return donationId + "," + donorId + "," + escape(donorName) + "," + escape(foodName) + "," 
                + category + "," + quantity + "," + escape(preparationTime) + "," 
                + escape(expiryTime) + "," + escape(location) + "," + status.name() + "," + escape(createdAt);
    }

    private String escape(String s) {
        return s == null ? "" : s.replace(",", ";");
    }
}
