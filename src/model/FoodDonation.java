package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
        return status == DonationStatus.AVAILABLE && quantity > 0 && !isExpired();
    }

    /**
     * Checks if the donation is expired against the current system date and time.
     */
    public boolean isExpired() {
        return isExpiredAt(LocalDate.now(), LocalTime.now());
    }

    /**
     * Evaluates expiry against specified date and time.
     * Useful for automated tests and viva demonstrations.
     */
    public boolean isExpiredAt(LocalDate currentDate, LocalTime currentTime) {
        if (status == DonationStatus.EXPIRED) {
            return true;
        }
        if (status != DonationStatus.AVAILABLE && status != DonationStatus.RESERVED) {
            return false;
        }

        try {
            // Check Date (if created on a previous date)
            if (createdAt != null && !createdAt.trim().isEmpty()) {
                LocalDate createdDate = LocalDate.parse(createdAt.trim());
                if (currentDate.isAfter(createdDate)) {
                    return true;
                }
            }

            // Check Expiry Time if created today
            if (expiryTime != null && !expiryTime.trim().isEmpty()) {
                LocalTime expTime = parseTime(expiryTime.trim());
                if (expTime != null && currentTime.isAfter(expTime)) {
                    return true;
                }
            }
        } catch (Exception e) {
            // Keep current status if parsing fails
        }
        return false;
    }

    /**
     * Helper method to parse time strings such as "09:00 PM", "9:00 PM", "21:00".
     */
    public static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) return null;
        String clean = timeStr.trim().toUpperCase();
        try {
            if (clean.endsWith("AM") || clean.endsWith("PM")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
                if (clean.length() >= 7 && clean.charAt(1) == ':') {
                    return LocalTime.parse(clean, formatter);
                } else {
                    DateTimeFormatter f2 = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
                    return LocalTime.parse(clean, f2);
                }
            } else if (clean.contains(":")) {
                return LocalTime.parse(clean);
            }
        } catch (Exception e) {
            // Manual fallback parsing for robust student explanation
            try {
                boolean isPm = clean.endsWith("PM");
                boolean isAm = clean.endsWith("AM");
                String raw = clean.replace("AM", "").replace("PM", "").trim();
                String[] parts = raw.split(":");
                int h = Integer.parseInt(parts[0].trim());
                int m = Integer.parseInt(parts[1].trim());
                if (isPm && h < 12) h += 12;
                if (isAm && h == 12) h = 0;
                return LocalTime.of(h, m);
            } catch (Exception ex) {
                return null;
            }
        }
        return null;
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
        // Demonstrates StringBuilder for string concatenation efficiency
        String foodName = (foodItem != null) ? foodItem.getName() : "Unknown";
        String category = (foodItem != null && foodItem.getCategory() != null) ? foodItem.getCategory().name() : "OTHER";
        StringBuilder sb = new StringBuilder(128);
        sb.append(donationId).append(",")
          .append(donorId).append(",")
          .append(escape(donorName)).append(",")
          .append(escape(foodName)).append(",")
          .append(category).append(",")
          .append(quantity).append(",")
          .append(escape(preparationTime)).append(",")
          .append(escape(expiryTime)).append(",")
          .append(escape(location)).append(",")
          .append(status.name()).append(",")
          .append(escape(createdAt));
        return sb.toString();
    }

    private String escape(String s) {
        return s == null ? "" : s.replace(",", ";");
    }
}
