package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Manages the collection of food donations in memory.
 * Demonstrates:
 * - ArrayList to store sequential list of donations
 * - HashMap for fast O(1) lookup of donation by ID
 * - Thread-safe synchronization on the shared food inventory resource
 * - Iterator pattern to safely inspect elements and avoid ConcurrentModificationException
 */
public class FoodInventory {
    private ArrayList<FoodDonation> donationList;
    private HashMap<Integer, FoodDonation> donationMap;

    public FoodInventory() {
        this.donationList = new ArrayList<>();
        this.donationMap = new HashMap<>();
    }

    /**
     * Synchronized method: Adds a donation safely to the shared list and map.
     */
    public synchronized void addDonation(FoodDonation donation) {
        donationList.add(donation);
        donationMap.put(donation.getDonationId(), donation);
    }

    /**
     * Synchronized method: O(1) lookup by ID.
     */
    public synchronized FoodDonation getDonationById(int id) {
        return donationMap.get(id);
    }

    /**
     * Synchronized method: Returns a defensive copy snapshot of all donations.
     */
    public synchronized ArrayList<FoodDonation> getAllDonations() {
        return new ArrayList<>(donationList);
    }

    /**
     * Synchronized method: Retrieves currently available donations.
     */
    public synchronized ArrayList<FoodDonation> getAvailableDonations() {
        ArrayList<FoodDonation> available = new ArrayList<>();
        for (FoodDonation d : donationList) {
            if (d.isAvailable()) {
                available.add(d);
            }
        }
        return available;
    }

    /**
     * Synchronized method called by background thread or managers.
     * Demonstrates Iterator from the Java Collections Framework.
     * Scans and marks outdated available/reserved donations as EXPIRED.
     */
    public synchronized int expireOutdatedDonations() {
        int count = 0;
        Iterator<FoodDonation> iterator = donationList.iterator();
        while (iterator.hasNext()) {
            FoodDonation donation = iterator.next();
            if ((donation.getStatus() == DonationStatus.AVAILABLE || donation.getStatus() == DonationStatus.RESERVED) 
                    && donation.isExpired()) {
                donation.setStatus(DonationStatus.EXPIRED);
                count++;
            }
        }
        return count;
    }

    public synchronized void clear() {
        donationList.clear();
        donationMap.clear();
    }
}
