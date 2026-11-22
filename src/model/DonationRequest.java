package model;

/**
 * Represents a food request placed by an NGO.
 * Implements Comparable for PriorityQueue sorting based on RequestPriority.
 * Implements Displayable.
 */
public class DonationRequest implements Displayable, Comparable<DonationRequest> {
    private int requestId;
    private int ngoId;
    private String ngoName;
    private FoodCategory category;
    private int quantityRequired;
    private String location;
    private RequestPriority priority;
    private String status; // "PENDING", "PARTIALLY_MATCHED", "MATCHED", "CANCELLED"
    private String requestDate;

    public DonationRequest() {
    }

    public DonationRequest(int requestId, int ngoId, String ngoName, FoodCategory category,
                           int quantityRequired, String location, RequestPriority priority,
                           String status, String requestDate) {
        this.requestId = requestId;
        this.ngoId = ngoId;
        this.ngoName = ngoName;
        this.category = category;
        this.quantityRequired = quantityRequired;
        this.location = location;
        this.priority = priority;
        this.status = status;
        this.requestDate = requestDate;
    }

    /**
     * Compare based on Priority level descending (URGENT comes before LOW).
     */
    @Override
    public int compareTo(DonationRequest other) {
        return Integer.compare(other.priority.getLevel(), this.priority.getLevel());
    }

    @Override
    public void displayDetails() {
        System.out.println("----------------------------------------");
        System.out.println("Request ID   : " + requestId);
        System.out.println("NGO          : " + ngoName + " (ID: " + ngoId + ")");
        System.out.println("Category     : " + category);
        System.out.println("Qty Required : " + quantityRequired + " packets");
        System.out.println("Location     : " + location);
        System.out.println("Priority     : " + priority);
        System.out.println("Status       : " + status);
        System.out.println("Request Date : " + requestDate);
        System.out.println("----------------------------------------");
    }

    // Getters and Setters
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getNgoId() {
        return ngoId;
    }

    public void setNgoId(int ngoId) {
        this.ngoId = ngoId;
    }

    public String getNgoName() {
        return ngoName;
    }

    public void setNgoName(String ngoName) {
        this.ngoName = ngoName;
    }

    public FoodCategory getCategory() {
        return category;
    }

    public void setCategory(FoodCategory category) {
        this.category = category;
    }

    public int getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(int quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public RequestPriority getPriority() {
        return priority;
    }

    public void setPriority(RequestPriority priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String toCsv() {
        // Format: requestId,ngoId,ngoName,category,quantityRequired,location,priority,status,requestDate
        return requestId + "," + ngoId + "," + escape(ngoName) + "," + category.name() + "," 
                + quantityRequired + "," + escape(location) + "," + priority.name() + "," 
                + escape(status) + "," + escape(requestDate);
    }

    private String escape(String s) {
        return s == null ? "" : s.replace(",", ";");
    }
}
