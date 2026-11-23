package test;

import manager.*;
import model.*;
import service.*;
import util.*;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Automated end-to-end test suite to verify the full workflow and requirements:
 * 1. Register donor
 * 2. Login as donor
 * 3. Add food donation
 * 4. Register/login NGO
 * 5. Create food request
 * 6. Search available donations
 * 7. Match donation with request
 * 8. Allocate quantity
 * 9. Assign volunteer
 * 10. Login as volunteer
 * 11. Complete pickup
 * 12. Complete delivery
 * 13. Update donation status
 * 14. Save history
 * 15. Generate impact report
 * 16. Close application (simulated)
 * 17. Restart application (reload from files)
 * 18. Verify data is still available
 * 19. Test input validation and exceptions
 */
public class SystemTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   REPLATE SYSTEM & WORKFLOW VERIFICATION TEST   ");
        System.out.println("==================================================");

        FileManager fileManager = new FileManager();
        UserManager userManager = new UserManager(fileManager);
        DonationManager donationManager = new DonationManager(fileManager);
        RequestManager requestManager = new RequestManager(fileManager);
        AssignmentManager assignmentManager = new AssignmentManager(fileManager);
        DonationHistory donationHistory = new DonationHistory(fileManager);
        DistributionManager distributionManager = new DistributionManager(
                assignmentManager, donationManager, userManager, requestManager, donationHistory, fileManager);
        DonationMatcher donationMatcher = new DonationMatcher();

        // 1. REGISTER DONOR
        System.out.println("\n[Test 1] Registering a new Donor...");
        int donorId = userManager.getNextUserId();
        Donor newDonor = new Donor(donorId, "Royal Palace Hotel", "royal@palace.com", "9988776655", 
                "Chandigarh", "royalpalace", "pass123", "Hotel");
        boolean regSuccess = userManager.registerUser(newDonor);
        assertCondition(regSuccess, "Donor registered successfully");

        // 2. LOGIN AS DONOR
        System.out.println("\n[Test 2] Testing Login...");
        User loggedUser = userManager.login("royalpalace", "pass123");
        assertCondition(loggedUser != null && loggedUser.getRole() == UserRole.DONOR, "Donor login valid");
        User invalidUser = userManager.login("royalpalace", "wrongpass");
        assertCondition(invalidUser == null, "Invalid password correctly rejected");

        // 3. ADD FOOD DONATION
        System.out.println("\n[Test 3] Adding Food Donation...");
        int donId = donationManager.getNextDonationId();
        FoodItem foodItem = new FoodItem("Vegetable Biryani", FoodCategory.COOKED_FOOD, true);
        FoodDonation newDonation = new FoodDonation(donId, newDonor.getUserId(), newDonor.getName(), 
                foodItem, 60, "06:00 PM", "10:30 PM", "Chandigarh", DonationStatus.AVAILABLE, LocalDate.now().toString());
        donationManager.addDonation(newDonation);
        FoodDonation fetchedDon = donationManager.getDonationById(donId);
        assertCondition(fetchedDon != null && fetchedDon.getQuantity() == 60, "Food donation created with 60 packets");

        // 4. REGISTER & LOGIN NGO
        System.out.println("\n[Test 4] Registering & Logging in NGO...");
        int ngoId = userManager.getNextUserId();
        NGO newNgo = new NGO(ngoId, "Care & Share Society", "care@share.org", "9876112233", 
                "Chandigarh", "careshare", "pass123", "NGO-CH-555", 150);
        userManager.registerUser(newNgo);
        User loggedNgo = userManager.login("careshare", "pass123");
        assertCondition(loggedNgo != null && loggedNgo.getRole() == UserRole.NGO, "NGO registered & logged in");

        // 5. CREATE FOOD REQUEST
        System.out.println("\n[Test 5] Creating Food Request...");
        int reqId = requestManager.getNextRequestId();
        DonationRequest request = new DonationRequest(reqId, newNgo.getUserId(), newNgo.getName(), 
                FoodCategory.COOKED_FOOD, 35, "Chandigarh", RequestPriority.HIGH, "PENDING", LocalDate.now().toString());
        requestManager.createRequest(request);
        DonationRequest fetchedReq = requestManager.getRequestById(reqId);
        assertCondition(fetchedReq != null && fetchedReq.getQuantityRequired() == 35, "Donation request created for 35 packets");

        // 6. SEARCH AVAILABLE DONATIONS
        System.out.println("\n[Test 6] Searching available donations...");
        ArrayList<FoodDonation> searchByName = FoodSearch.searchByName("Biryani", donationManager.getAvailableDonations());
        assertCondition(!searchByName.isEmpty(), "FoodSearch by name found matching donation");
        ArrayList<FoodDonation> searchByCat = FoodSearch.searchByCategory(FoodCategory.COOKED_FOOD, donationManager.getAvailableDonations());
        assertCondition(!searchByCat.isEmpty(), "FoodSearch by category found items");
        ArrayList<FoodDonation> searchByLoc = FoodSearch.searchByLocation("Chandigarh", donationManager.getAvailableDonations());
        assertCondition(!searchByLoc.isEmpty(), "FoodSearch by location found items");

        // 7. MATCH DONATION WITH REQUEST
        System.out.println("\n[Test 7] Matching donation with request...");
        FoodDonation matchedDonation = donationMatcher.findBestMatch(request, donationManager.getAvailableDonations());
        assertCondition(matchedDonation != null, "DonationMatcher found compatible donation (ID: " + (matchedDonation != null ? matchedDonation.getDonationId() : 0) + ")");

        // 8. ALLOCATE QUANTITY
        System.out.println("\n[Test 8] Allocating quantity...");
        int initialDonQty = matchedDonation.getQuantity();
        int allocated = donationMatcher.allocateQuantity(matchedDonation, request);
        donationManager.save();
        requestManager.save();
        assertCondition(allocated == 35, "Allocated exactly 35 packets");
        assertCondition(matchedDonation.getQuantity() == (initialDonQty - 35), "Remaining quantity updated to " + (initialDonQty - 35));
        assertCondition("MATCHED".equals(request.getStatus()), "Request status updated to MATCHED");

        // 9. ASSIGN VOLUNTEER
        System.out.println("\n[Test 9] Assigning volunteer...");
        Volunteer volunteer = userManager.getAvailableVolunteer();
        assertCondition(volunteer != null, "Found available volunteer: " + (volunteer != null ? volunteer.getName() : "None"));
        VolunteerAssignment assignment = assignmentManager.createAssignment(
                matchedDonation.getDonationId(), request.getRequestId(), volunteer, matchedDonation, request, allocated);
        assertCondition(assignment != null, "VolunteerAssignment created with ID #" + assignment.getAssignmentId());
        assertCondition(!volunteer.isAvailable(), "Volunteer availability updated to busy (false)");

        // 10 & 11. VOLUNTEER PICKUP
        System.out.println("\n[Test 10 & 11] Volunteer Pickup...");
        boolean pickupDone = distributionManager.completePickup(assignment.getAssignmentId(), "07:00 PM", "Picked up safely in thermal bag");
        assertCondition(pickupDone, "Pickup marked as completed");
        assertCondition(assignment.getStatus() == DonationStatus.PICKED_UP, "Assignment status updated to PICKED_UP");

        // 12. VOLUNTEER DELIVERY & RECEIPT
        System.out.println("\n[Test 12] Volunteer Delivery & Receipt Generation...");
        boolean deliveryDone = distributionManager.completeDelivery(assignment.getAssignmentId(), "07:45 PM", "Rajesh Sharma (Care & Share)");
        assertCondition(deliveryDone, "Delivery marked as completed");
        assertCondition(assignment.getStatus() == DonationStatus.DELIVERED, "Assignment status updated to DELIVERED");
        assertCondition(volunteer.isAvailable(), "Volunteer availability restored to available (true)");

        // 13 & 14. HISTORY & IMPACT REPORT
        System.out.println("\n[Test 13 & 14] History and Impact Report...");
        assertCondition(!donationHistory.getHistoryRecords().isEmpty(), "History records recorded");
        ImpactReport.displayImpactReport(donationManager, assignmentManager, userManager);

        // 15, 16, 17, 18. SIMULATE RESTART & PERSISTENCE VERIFICATION
        System.out.println("\n[Test 15-18] Simulating Application Restart and File Reload...");
        FileManager reloadedFileManager = new FileManager();
        UserManager reloadedUserManager = new UserManager(reloadedFileManager);
        DonationManager reloadedDonationManager = new DonationManager(reloadedFileManager);
        RequestManager reloadedRequestManager = new RequestManager(reloadedFileManager);
        AssignmentManager reloadedAssignmentManager = new AssignmentManager(reloadedFileManager);

        User reloadedUser = reloadedUserManager.login("royalpalace", "pass123");
        assertCondition(reloadedUser != null, "Persisted User successfully reloaded after restart");

        FoodDonation reloadedDon = reloadedDonationManager.getDonationById(matchedDonation.getDonationId());
        assertCondition(reloadedDon != null && reloadedDon.getQuantity() == (initialDonQty - 35), 
                "Persisted Food Donation reloaded with updated remaining quantity (" + (initialDonQty - 35) + ")");

        VolunteerAssignment reloadedAssignment = reloadedAssignmentManager.getAssignmentById(assignment.getAssignmentId());
        assertCondition(reloadedAssignment != null && reloadedAssignment.getStatus() == DonationStatus.DELIVERED, 
                "Persisted VolunteerAssignment reloaded with status DELIVERED");

        // 19. INPUT VALIDATION & CUSTOM EXCEPTION TEST
        System.out.println("\n[Test 19] Testing Input Validation & Exceptions...");
        try {
            InputValidator.validateQuantity(-5);
            assertCondition(false, "Negative quantity should throw ValidationException");
        } catch (ValidationException e) {
            assertCondition(true, "ValidationException caught for negative quantity: " + e.getMessage());
        }

        try {
            InputValidator.validateRequiredText("   ", "Food Name");
            assertCondition(false, "Blank text should throw ValidationException");
        } catch (ValidationException e) {
            assertCondition(true, "ValidationException caught for blank input: " + e.getMessage());
        }

        // Email validation tests
        try {
            InputValidator.validateEmail("invalid-email");
            assertCondition(false, "Invalid email should throw ValidationException");
        } catch (ValidationException e) {
            assertCondition(true, "ValidationException caught for invalid email: " + e.getMessage());
        }
        try {
            InputValidator.validateEmail("user@gmail.com");
            assertCondition(true, "Valid email user@gmail.com accepted");
        } catch (ValidationException e) {
            assertCondition(false, "Valid email threw unexpected exception: " + e.getMessage());
        }

        // Phone validation tests
        try {
            InputValidator.validatePhone("12345");
            assertCondition(false, "Short phone number should throw ValidationException");
        } catch (ValidationException e) {
            assertCondition(true, "ValidationException caught for short phone: " + e.getMessage());
        }
        try {
            InputValidator.validatePhone("abcdefghij");
            assertCondition(false, "Alphabetic phone number should throw ValidationException");
        } catch (ValidationException e) {
            assertCondition(true, "ValidationException caught for alphabetic phone: " + e.getMessage());
        }
        try {
            InputValidator.validatePhone("9876543210");
            assertCondition(true, "Valid 10-digit phone 9876543210 accepted");
        } catch (ValidationException e) {
            assertCondition(false, "Valid phone threw unexpected exception: " + e.getMessage());
        }

        // NGO registration number validation tests
        try {
            InputValidator.validateNgoRegNo("12345");
            assertCondition(false, "Invalid NGO reg number should throw ValidationException");
        } catch (ValidationException e) {
            assertCondition(true, "ValidationException caught for invalid NGO reg no: " + e.getMessage());
        }
        try {
            InputValidator.validateNgoRegNo("NGO-CH-105");
            assertCondition(true, "Valid NGO reg number NGO-CH-105 accepted");
        } catch (ValidationException e) {
            assertCondition(false, "Valid NGO reg no threw unexpected exception: " + e.getMessage());
        }

        System.out.println("\n==================================================");
        System.out.println("   ALL TESTS PASSED SUCCESSFULLY! (100% READY)   ");
        System.out.println("==================================================");
    }

    private static void assertCondition(boolean condition, String description) {
        if (condition) {
            System.out.println("  [PASS] " + description);
        } else {
            System.err.println("  [FAIL] " + description);
            throw new RuntimeException("Assertion failed: " + description);
        }
    }
}
