package util;

/**
 * Demonstrates 2D arrays in Core Java.
 * Maintains a 2D distance matrix between participating network cities (in kilometers).
 * Used by DonationMatcher and display views to assess transport feasibility.
 */
public class CityDistanceMatrix {
    private static final String[] CITIES = {"Chandigarh", "Mohali", "Ludhiana", "Barnala"};

    // 2D Array: Rows and columns represent cities in the CITIES array
    private static final int[][] DISTANCE_MATRIX = {
        // Chandigarh, Mohali, Ludhiana, Barnala
        {      0,       10,      105,     150 }, // Chandigarh
        {     10,        0,       95,     140 }, // Mohali
        {    105,       95,        0,      65 }, // Ludhiana
        {    150,      140,       65,       0 }  // Barnala
    };

    /**
     * Resolves city index in the 2D array.
     */
    private static int getCityIndex(String city) {
        if (city == null) return -1;
        String clean = city.trim().toLowerCase();
        for (int i = 0; i < CITIES.length; i++) {
            if (CITIES[i].toLowerCase().equals(clean)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Retrieves distance between two cities using 2D array lookup.
     * Returns 0 if same city, calculated distance from 2D array, or 50 as default estimate.
     */
    public static int getDistance(String fromCity, String toCity) {
        int idx1 = getCityIndex(fromCity);
        int idx2 = getCityIndex(toCity);
        if (idx1 != -1 && idx2 != -1) {
            return DISTANCE_MATRIX[idx1][idx2];
        }
        // Fallback for custom cities
        if (fromCity != null && toCity != null && fromCity.trim().equalsIgnoreCase(toCity.trim())) {
            return 0;
        }
        return 50; // Default estimate for unknown cities
    }

    /**
     * Checks if travel between two cities is within acceptable transfer range.
     */
    public static boolean isWithinRange(String fromCity, String toCity, int maxKm) {
        int dist = getDistance(fromCity, toCity);
        return dist <= maxKm;
    }

    /**
     * Prints the 2D distance matrix in a clean tabular grid.
     */
    public static void printDistanceTable() {
        System.out.println("\n========================================================");
        System.out.println("     REGIONAL CITY DISTANCE MATRIX (2D ARRAY) [km]      ");
        System.out.println("========================================================");
        System.out.printf("%-14s", "From / To");
        for (String c : CITIES) {
            System.out.printf("%-12s", c);
        }
        System.out.println();
        System.out.println("--------------------------------------------------------");
        for (int i = 0; i < DISTANCE_MATRIX.length; i++) {
            System.out.printf("%-14s", CITIES[i]);
            for (int j = 0; j < DISTANCE_MATRIX[i].length; j++) {
                System.out.printf("%-12d", DISTANCE_MATRIX[i][j]);
            }
            System.out.println();
        }
        System.out.println("========================================================");
    }
}
