package model;

/**
 * Represents the delivery stage of the food redistribution workflow.
 */
public class Delivery {
    private int deliveryId;
    private int assignmentId;
    private String deliveryLocation;
    private String deliveryTime;
    private boolean isCompleted;
    private String receiverName;

    public Delivery() {
    }

    public Delivery(int deliveryId, int assignmentId, String deliveryLocation, String deliveryTime) {
        this.deliveryId = deliveryId;
        this.assignmentId = assignmentId;
        this.deliveryLocation = deliveryLocation;
        this.deliveryTime = deliveryTime;
        this.isCompleted = false;
        this.receiverName = "Pending";
    }

    public void markComplete(String time, String receiverName) {
        this.isCompleted = true;
        this.deliveryTime = time;
        this.receiverName = receiverName;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}
