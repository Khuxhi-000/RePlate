package service;

import manager.AssignmentManager;
import manager.DonationManager;
import manager.UserManager;
import model.*;

import java.util.ArrayList;

/**
 * Generates dynamic community impact statistics from actual application data.
 * Computes:
 * - Total donations registered
 * - Total food packets donated
 * - Total food packets distributed
 * - Successful deliveries count
 * - Active donations count
 * - Expired/cancelled donations count
 * - Donors, NGOs, and Volunteers involved
 */
public class ImpactReport {

    public static void displayImpactReport(DonationManager donationManager,
                                           AssignmentManager assignmentManager,
                                           UserManager userManager) {
        ArrayList<FoodDonation> donations = donationManager.getAllDonations();
        ArrayList<VolunteerAssignment> assignments = assignmentManager.getAllAssignments();
        ArrayList<User> users = userManager.getAllUsers();

        int totalDonations = donations.size();
        int totalFoodDonated = 0;
        int activeDonations = 0;
        int expiredOrCancelled = 0;

        for (FoodDonation d : donations) {
            totalFoodDonated += d.getQuantity();
            if (d.getStatus() == DonationStatus.AVAILABLE) {
                activeDonations++;
            } else if (d.getStatus() == DonationStatus.CANCELLED || d.getStatus() == DonationStatus.EXPIRED) {
                expiredOrCancelled++;
            }
        }

        int totalFoodDistributed = 0;
        int successfulDeliveries = 0;

        for (VolunteerAssignment a : assignments) {
            if (a.getStatus() == DonationStatus.DELIVERED) {
                successfulDeliveries++;
                totalFoodDistributed += a.getQuantity();
            }
        }

        // Add distributed packets to total food donated to give total historical surplus accounted for
        int overallSurplusRecorded = totalFoodDonated + totalFoodDistributed;

        int donorCount = 0;
        int ngoCount = 0;
        int volunteerCount = 0;

        for (User u : users) {
            if (u instanceof Donor) donorCount++;
            else if (u instanceof NGO) ngoCount++;
            else if (u instanceof Volunteer) volunteerCount++;
        }

        // Demonstrates StringBuilder from Java standard library for high-performance formatting
        StringBuilder sb = new StringBuilder(512);
        sb.append("\n========================================\n");
        sb.append("       COMMUNITY IMPACT REPORT\n");
        sb.append("========================================\n");
        sb.append("Total Donations Registered : ").append(totalDonations).append("\n");
        sb.append("Total Food Surplus Handled : ").append(overallSurplusRecorded).append(" packets\n");
        sb.append("Total Food Distributed     : ").append(totalFoodDistributed).append(" packets\n");
        sb.append("Successful Deliveries      : ").append(successfulDeliveries).append("\n");
        sb.append("Active Available Donations : ").append(activeDonations).append("\n");
        sb.append("Expired/Cancelled Donations: ").append(expiredOrCancelled).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Community Network Members:\n");
        sb.append(" - Active Donors           : ").append(donorCount).append("\n");
        sb.append(" - Partner NGOs            : ").append(ngoCount).append("\n");
        sb.append(" - Dedicated Volunteers    : ").append(volunteerCount).append("\n");
        sb.append("========================================\n");

        System.out.print(sb.toString());
    }
}
