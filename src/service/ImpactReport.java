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

        System.out.println("\n========================================");
        System.out.println("       COMMUNITY IMPACT REPORT");
        System.out.println("========================================");
        System.out.println("Total Donations Registered : " + totalDonations);
        System.out.println("Total Food Surplus Handled : " + overallSurplusRecorded + " packets");
        System.out.println("Total Food Distributed     : " + totalFoodDistributed + " packets");
        System.out.println("Successful Deliveries      : " + successfulDeliveries);
        System.out.println("Active Available Donations : " + activeDonations);
        System.out.println("Expired/Cancelled Donations: " + expiredOrCancelled);
        System.out.println("----------------------------------------");
        System.out.println("Community Network Members:");
        System.out.println(" - Active Donors           : " + donorCount);
        System.out.println(" - Partner NGOs            : " + ngoCount);
        System.out.println(" - Dedicated Volunteers    : " + volunteerCount);
        System.out.println("========================================");
    }
}
