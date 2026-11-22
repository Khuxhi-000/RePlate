package model;

/**
 * Represents the pickup stage of the food redistribution workflow.
 */
public class Pickup {
    private int pickupId;
    private int assignmentId;
    private String pickupLocation;
    private String pickupTime;
    private boolean isCompleted;
    private String notes;

    public Pickup() {
    }

    public Pickup(int pickupId, int assignmentId, String pickupLocation, String pickupTime) {
        this.pickupId = pickupId;
        this.assignmentId = assignmentId;
        this.pickupLocation = pickupLocation;
        this.pickupTime = pickupTime;
        this.isCompleted = false;
        this.notes = "Pending pickup";
    }

    public void markComplete(String time, String notes) {
        this.isCompleted = true;
        this.pickupTime = time;
        this.notes = notes;
    }

    public int getPickupId() {
        return pickupId;
    }

    public void setPickupId(int pickupId) {
        this.pickupId = pickupId;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
