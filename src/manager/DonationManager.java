package manager;

import model.*;
import util.FileManager;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Manages operations on food donations: adding, updating, canceling,
 * and maintaining available surplus quantities.
 * Demonstrates interaction with FoodInventory and persistence via FileManager.
 */
public class DonationManager {
    private FoodInventory inventory;
    private FileManager fileManager;

    public DonationManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.inventory = new FoodInventory();
        loadDonations();
    }

    private void loadDonations() {
        ArrayList<FoodDonation> loaded = fileManager.loadDonations();
        if (loaded.isEmpty()) {
            seedInitialDonations();
        } else {
            for (FoodDonation d : loaded) {
                inventory.addDonation(d);
            }
        }
    }

    /**
     * Seeds initial sample donations matching the requirements:
     * - Rice (Cooked Food, 50 packets, Chandigarh)
     * - Bread (Bakery, 30 packets, Mohali)
     * - Cooked Food (40 packets, Ludhiana)
     * - Fruits (25 packets, Chandigarh)
     */
    private void seedInitialDonations() {
        String today = LocalDate.now().toString();
        addDonation(new FoodDonation(101, 2, "ABC College Mess", 
                new FoodItem("Rice", FoodCategory.COOKED_FOOD, true), 
                50, "05:00 PM", "09:00 PM", "Chandigarh", DonationStatus.AVAILABLE, today));

        addDonation(new FoodDonation(102, 3, "Green Leaf Restaurant", 
                new FoodItem("Bread", FoodCategory.BAKERY, true), 
                30, "04:00 PM", "10:00 PM", "Mohali", DonationStatus.AVAILABLE, today));

        addDonation(new FoodDonation(103, 2, "ABC College Mess", 
                new FoodItem("Cooked Food", FoodCategory.COOKED_FOOD, true), 
                40, "06:00 PM", "11:00 PM", "Ludhiana", DonationStatus.AVAILABLE, today));

        addDonation(new FoodDonation(104, 3, "Green Leaf Restaurant", 
                new FoodItem("Fruits", FoodCategory.FRUITS, true), 
                25, "02:00 PM", "08:00 PM", "Chandigarh", DonationStatus.AVAILABLE, today));
    }

    public void addDonation(FoodDonation donation) {
        inventory.addDonation(donation);
        fileManager.saveDonations(inventory.getAllDonations());
    }

    public FoodDonation getDonationById(int id) {
        return inventory.getDonationById(id);
    }

    public ArrayList<FoodDonation> getAllDonations() {
        return inventory.getAllDonations();
    }

    public ArrayList<FoodDonation> getAvailableDonations() {
        return inventory.getAvailableDonations();
    }

    public ArrayList<FoodDonation> getDonationsByDonor(int donorId) {
        ArrayList<FoodDonation> result = new ArrayList<>();
        for (FoodDonation d : inventory.getAllDonations()) {
            if (d.getDonorId() == donorId) {
                result.add(d);
            }
        }
        return result;
    }

    public boolean updateDonationQuantity(int donationId, int newQuantity) {
        FoodDonation d = inventory.getDonationById(donationId);
        if (d != null && d.getStatus() == DonationStatus.AVAILABLE) {
            d.setQuantity(newQuantity);
            if (newQuantity == 0) {
                d.setStatus(DonationStatus.RESERVED);
            }
            save();
            return true;
        }
        return false;
    }

    public boolean updateDonationStatus(int donationId, DonationStatus newStatus) {
        FoodDonation d = inventory.getDonationById(donationId);
        if (d != null) {
            d.setStatus(newStatus);
            save();
            return true;
        }
        return false;
    }

    public boolean cancelDonation(int donationId, int donorId) {
        FoodDonation d = inventory.getDonationById(donationId);
        if (d != null && d.getDonorId() == donorId && d.getStatus() == DonationStatus.AVAILABLE) {
            d.setStatus(DonationStatus.CANCELLED);
            save();
            return true;
        }
        return false;
    }

    public int getNextDonationId() {
        int max = 100;
        for (FoodDonation d : inventory.getAllDonations()) {
            if (d.getDonationId() > max) {
                max = d.getDonationId();
            }
        }
        return max + 1;
    }

    public void save() {
        fileManager.saveDonations(inventory.getAllDonations());
    }
}
