package service;

import model.DonationRequest;
import model.DonationStatus;
import model.FoodDonation;
import util.CityDistanceMatrix;
import util.DonationExpiredException;
import util.InsufficientQuantityException;

import java.util.ArrayList;

/**
 * Service class implementing the core matching logic between NGO requests
 * and available food donations.
 *
 * Matching Criteria:
 * 1. Food Category matches
 * 2. Status is AVAILABLE and not expired
 * 3. Quantity is available
 * 4. Location matches or within feasible transfer distance (using 2D CityDistanceMatrix)
 */
public class DonationMatcher {

    /**
     * Searches available donations to find the best match for the given NGO request.
     * Keeps the logic clean, intuitive, and easy for students to explain during a viva.
     *
     * @param request The NGO's food request
     * @param availableDonations List of current available donations
     * @return Best matching FoodDonation, or null if no match found
     */
    public FoodDonation findBestMatch(DonationRequest request, ArrayList<FoodDonation> availableDonations) {
        if (request == null || availableDonations == null) {
            return null;
        }

        FoodDonation bestMatch = null;

        for (FoodDonation donation : availableDonations) {
            // 1. Must be AVAILABLE and have quantity
            if (!donation.isAvailable()) {
                continue;
            }

            // 2. Food category must match
            if (donation.getFoodItem() == null || donation.getFoodItem().getCategory() != request.getCategory()) {
                continue;
            }

            // 3. Location check: exact city match or same tricity cluster (Chandigarh/Mohali)
            boolean locationMatch = isLocationCompatible(request.getLocation(), donation.getLocation());
            if (!locationMatch) {
                continue;
            }

            // 4. Check if quantity is sufficient
            if (donation.getQuantity() >= request.getQuantityRequired()) {
                return donation; // Exact or surplus quantity match found
            }

            // Fallback: candidate with partial quantity
            if (bestMatch == null || donation.getQuantity() > bestMatch.getQuantity()) {
                bestMatch = donation;
            }
        }

        return bestMatch;
    }

    /**
     * Checks if two simulated city locations are compatible.
     * Uses 2D CityDistanceMatrix to assess transfer feasibility (within 25 km range).
     */
    public boolean isLocationCompatible(String loc1, String loc2) {
        if (loc1 == null || loc2 == null) return false;
        String l1 = loc1.trim().toLowerCase();
        String l2 = loc2.trim().toLowerCase();

        // Exact match
        if (l1.equals(l2)) return true;

        // Use 2D array distance lookup: compatible if distance <= 25 km (e.g. Chandigarh & Mohali = 10 km)
        return CityDistanceMatrix.isWithinRange(loc1, loc2, 25);
    }

    /**
     * Allocates quantity from donation to satisfy the request.
     * Demonstrates throw and throws with custom exceptions.
     *
     * @return allocated quantity
     * @throws DonationExpiredException if donation has expired
     * @throws InsufficientQuantityException if donation has 0 or insufficient surplus
     */
    public int allocateQuantity(FoodDonation donation, DonationRequest request) 
            throws DonationExpiredException, InsufficientQuantityException {
        if (donation == null || request == null) {
            throw new IllegalArgumentException("Donation and Request cannot be null.");
        }
        if (donation.getStatus() == DonationStatus.EXPIRED || donation.isExpired()) {
            throw new DonationExpiredException("Cannot allocate food: Donation #" + donation.getDonationId() + " has expired.");
        }
        int available = donation.getQuantity();
        if (available <= 0) {
            throw new InsufficientQuantityException(request.getQuantityRequired(), 0);
        }

        int needed = request.getQuantityRequired();
        int allocated = Math.min(needed, available);
        int remaining = available - allocated;

        donation.setQuantity(remaining);
        if (remaining == 0) {
            donation.setStatus(DonationStatus.RESERVED);
        }

        if (allocated == needed) {
            request.setStatus("MATCHED");
        } else {
            request.setStatus("PARTIALLY_MATCHED (" + allocated + "/" + needed + ")");
        }

        return allocated;
    }
}
