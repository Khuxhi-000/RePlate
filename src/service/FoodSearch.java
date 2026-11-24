package service;

import model.FoodCategory;
import model.FoodDonation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

/**
 * Provides searching and sorting functionalities for food donations.
 * Demonstrates:
 * - Linear searching by various attributes (food name, category, location, ID)
 * - Sorting collections using Comparator
 */
public class FoodSearch {

    public static ArrayList<FoodDonation> searchByName(String name, ArrayList<FoodDonation> donations) {
        ArrayList<FoodDonation> results = new ArrayList<>();
        if (name == null || name.trim().isEmpty())
            return results;
        String query = name.toLowerCase().trim();

        for (FoodDonation d : donations) {
            if (d.getFoodItem() != null && d.getFoodItem().getName().toLowerCase().contains(query)) {
                results.add(d);
            }
        }
        return results;
    }

    public static ArrayList<FoodDonation> searchByCategory(FoodCategory category, ArrayList<FoodDonation> donations) {
        ArrayList<FoodDonation> results = new ArrayList<>();
        for (FoodDonation d : donations) {
            if (d.getFoodItem() != null && d.getFoodItem().getCategory() == category) {
                results.add(d);
            }
        }
        return results;
    }

    public static ArrayList<FoodDonation> searchByLocation(String location, ArrayList<FoodDonation> donations) {
        ArrayList<FoodDonation> results = new ArrayList<>();
        if (location == null || location.trim().isEmpty())
            return results;
        String query = location.toLowerCase().trim();

        for (FoodDonation d : donations) {
            if (d.getLocation() != null && d.getLocation().toLowerCase().contains(query)) {
                results.add(d);
            }
        }
        return results;
    }

    public static FoodDonation searchById(int id, ArrayList<FoodDonation> donations) {
        for (FoodDonation d : donations) {
            if (d.getDonationId() == id) {
                return d;
            }
        }
        return null;
    }

    /**
     * Demonstrates ListIterator from Java Collections Framework.
     * Iterates through donations sequentially to filter exact city matches.
     */
    public static ArrayList<FoodDonation> filterByLocationWithIterator(String location,
            ArrayList<FoodDonation> donations) {
        ArrayList<FoodDonation> results = new ArrayList<>();
        if (location == null || donations == null)
            return results;
        ListIterator<FoodDonation> listIterator = donations.listIterator();
        while (listIterator.hasNext()) {
            FoodDonation d = listIterator.next();
            if (d.getLocation() != null && d.getLocation().equalsIgnoreCase(location.trim())) {
                results.add(d);
            }
        }
        return results;
    }

    /**
     * Sorts donations by quantity (ascending or descending).
     */
    public static void sortByQuantity(ArrayList<FoodDonation> donations, boolean ascending) {
        donations.sort(new Comparator<FoodDonation>() {
            @Override
            public int compare(FoodDonation d1, FoodDonation d2) {
                if (ascending) {
                    return Integer.compare(d1.getQuantity(), d2.getQuantity());
                } else {
                    return Integer.compare(d2.getQuantity(), d1.getQuantity());
                }
            }
        });
    }

    /**
     * Sorts donations by expiry time string (lexicographical / simple compare).
     */
    public static void sortByExpiry(ArrayList<FoodDonation> donations) {
        donations.sort(new Comparator<FoodDonation>() {
            @Override
            public int compare(FoodDonation d1, FoodDonation d2) {
                String e1 = d1.getExpiryTime() != null ? d1.getExpiryTime() : "";
                String e2 = d2.getExpiryTime() != null ? d2.getExpiryTime() : "";
                return e1.compareToIgnoreCase(e2);
            }
        });
    }
}
