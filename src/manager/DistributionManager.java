package manager;

import model.*;
import service.DonationHistory;
import util.FileManager;

import java.time.LocalDate;

/**
 * Coordinates pickup and delivery workflows, updates status,
 * releases volunteers upon completion, and prints receipt-style output.
 */
public class DistributionManager {
    private AssignmentManager assignmentManager;
    private DonationManager donationManager;
    private UserManager userManager;
    private RequestManager requestManager;
    private DonationHistory donationHistory;
    private FileManager fileManager;

    public DistributionManager(AssignmentManager assignmentManager, DonationManager donationManager,
                               UserManager userManager, RequestManager requestManager,
                               DonationHistory donationHistory, FileManager fileManager) {
        this.assignmentManager = assignmentManager;
        this.donationManager = donationManager;
        this.userManager = userManager;
        this.requestManager = requestManager;
        this.donationHistory = donationHistory;
        this.fileManager = fileManager;
    }

    /**
     * Marks pickup as completed by the volunteer.
     */
    public boolean completePickup(int assignmentId, String pickupTime, String notes) {
        VolunteerAssignment assignment = assignmentManager.getAssignmentById(assignmentId);
        if (assignment == null) {
            System.out.println(">> Error: Assignment ID not found.");
            return false;
        }

        if (assignment.getStatus() != DonationStatus.RESERVED) {
            System.out.println(">> Error: Assignment is not in ASSIGNED/RESERVED state.");
            return false;
        }

        assignment.setStatus(DonationStatus.PICKED_UP);
        assignmentManager.save();

        // Update corresponding donation if applicable
        FoodDonation donation = donationManager.getDonationById(assignment.getDonationId());
        if (donation != null && donation.getQuantity() == 0) {
            donation.setStatus(DonationStatus.PICKED_UP);
            donationManager.save();
        }

        String record = "[" + LocalDate.now() + "] PICKUP: Assignment #" + assignmentId 
                + " for " + assignment.getFoodDetails() + " (" + assignment.getQuantity() + " pkts) picked up by " 
                + assignment.getVolunteerName() + " at " + pickupTime + ".";
        donationHistory.addRecord(record);

        System.out.println("\n>> Status Updated: Food picked up successfully!");
        return true;
    }

    /**
     * Marks delivery as completed to the NGO.
     * Displays a receipt and releases the volunteer.
     */
    public boolean completeDelivery(int assignmentId, String deliveryTime, String receiverName) {
        VolunteerAssignment assignment = assignmentManager.getAssignmentById(assignmentId);
        if (assignment == null) {
            System.out.println(">> Error: Assignment ID not found.");
            return false;
        }

        if (assignment.getStatus() != DonationStatus.PICKED_UP) {
            System.out.println(">> Error: Food must be picked up before marking delivery as completed.");
            return false;
        }

        assignment.setStatus(DonationStatus.DELIVERED);
        assignmentManager.save();

        // Free the assigned volunteer so they become available again
        User volUser = userManager.getUserById(assignment.getVolunteerId());
        if (volUser instanceof Volunteer) {
            ((Volunteer) volUser).setAvailable(true);
            userManager.save();
        }

        // Update corresponding donation status
        FoodDonation donation = donationManager.getDonationById(assignment.getDonationId());
        if (donation != null && donation.getQuantity() == 0) {
            donation.setStatus(DonationStatus.DELIVERED);
            donationManager.save();
        }

        // Update request status if request exists
        DonationRequest req = requestManager.getRequestById(assignment.getRequestId());
        if (req != null) {
            req.setStatus("DELIVERED");
            requestManager.save();
        }

        // Display receipt
        displayReceipt(assignment, donation, req, deliveryTime, receiverName);

        // Record history
        String donorName = donation != null ? donation.getDonorName() : "Donor";
        String ngoName = req != null ? req.getNgoName() : "NGO";
        String record = "[" + LocalDate.now() + "] DELIVERED: Donation #" + assignment.getDonationId() 
                + " (" + assignment.getQuantity() + " pkts) from " + donorName 
                + " to " + ngoName + " by Volunteer " + assignment.getVolunteerName() 
                + ". Received by: " + receiverName + ".";
        donationHistory.addRecord(record);

        return true;
    }

    /**
     * Displays a clean terminal receipt as required in the project specification.
     */
    public void displayReceipt(VolunteerAssignment assignment, FoodDonation donation, 
                               DonationRequest req, String deliveryTime, String receiverName) {
        String donorName = donation != null ? donation.getDonorName() : "Partner Donor";
        String ngoName = req != null ? req.getNgoName() : "Partner NGO";

        System.out.println("\n========================================");
        System.out.println("      FOOD REDISTRIBUTION RECEIPT");
        System.out.println("========================================");
        System.out.println("Donation ID : " + assignment.getDonationId());
        System.out.println("Assignment  : #" + assignment.getAssignmentId());
        System.out.println("Donor       : " + donorName);
        System.out.println("NGO         : " + ngoName);
        System.out.println();
        System.out.println("Food        : " + assignment.getFoodDetails());
        System.out.println("Quantity    : " + assignment.getQuantity() + " packets");
        System.out.println();
        System.out.println("Delivered By: " + assignment.getVolunteerName());
        System.out.println("Received By : " + receiverName);
        System.out.println("Time        : " + deliveryTime);
        System.out.println("Pickup      : Completed");
        System.out.println("Delivery    : Completed");
        System.out.println();
        System.out.println("Thank you for helping reduce food waste.");
        System.out.println("========================================\n");
    }
}
