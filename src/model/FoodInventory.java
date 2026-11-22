package model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages the collection of food donations in memory.
 * Demonstrates:
 * - ArrayList to store sequential list of donations
 * - HashMap for fast O(1) lookup of donation by ID
 */
public class FoodInventory {
    private ArrayList<FoodDonation> donationList;
    private HashMap<Integer, FoodDonation> donationMap;

    public FoodInventory() {
        this.donationList = new ArrayList<>();
        this.donationMap = new HashMap<>();
    }

    public void addDonation(FoodDonation donation) {
        donationList.add(donation);
        donationMap.put(donation.getDonationId(), donation);
    }

    public FoodDonation getDonationById(int id) {
        return donationMap.get(id);
    }

    public ArrayList<FoodDonation> getAllDonations() {
        return donationList;
    }

    public ArrayList<FoodDonation> getAvailableDonations() {
        ArrayList<FoodDonation> available = new ArrayList<>();
        for (FoodDonation d : donationList) {
            if (d.isAvailable()) {
                available.add(d);
            }
        }
        return available;
    }

    public void clear() {
        donationList.clear();
        donationMap.clear();
    }
}
