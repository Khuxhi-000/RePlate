package manager;

import model.*;
import util.FileManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages volunteer assignments for transporting allocated surplus food.
 * Demonstrates:
 * - ArrayList to store assignment records
 * - HashMap for fast lookup by assignment ID
 */
public class AssignmentManager {
    private ArrayList<VolunteerAssignment> assignmentList;
    private HashMap<Integer, VolunteerAssignment> assignmentMap;
    private FileManager fileManager;

    public AssignmentManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.assignmentList = new ArrayList<>();
        this.assignmentMap = new HashMap<>();
        loadAssignments();
    }

    private void loadAssignments() {
        ArrayList<VolunteerAssignment> loaded = fileManager.loadAssignments();
        for (VolunteerAssignment a : loaded) {
            assignmentList.add(a);
            assignmentMap.put(a.getAssignmentId(), a);
        }
    }

    public VolunteerAssignment createAssignment(int donationId, int requestId, Volunteer volunteer,
                                                FoodDonation donation, DonationRequest request, int allocatedQty) {
        int nextId = getNextAssignmentId();
        String foodDetails = (donation.getFoodItem() != null ? donation.getFoodItem().getName() : "Food") 
                + " (" + (donation.getFoodItem() != null ? donation.getFoodItem().getCategory() : "GENERAL") + ")";

        VolunteerAssignment assignment = new VolunteerAssignment(
                nextId,
                donationId,
                requestId,
                volunteer != null ? volunteer.getUserId() : 0,
                volunteer != null ? volunteer.getName() : "Unassigned",
                donation.getLocation(),
                request.getLocation(),
                foodDetails,
                allocatedQty,
                DonationStatus.RESERVED
        );

        if (volunteer != null) {
            volunteer.setAvailable(false); // Volunteer is now on an active assignment
        }

        assignmentList.add(assignment);
        assignmentMap.put(assignment.getAssignmentId(), assignment);
        fileManager.saveAssignments(assignmentList);
        return assignment;
    }

    public VolunteerAssignment getAssignmentById(int id) {
        return assignmentMap.get(id);
    }

    public ArrayList<VolunteerAssignment> getAllAssignments() {
        return assignmentList;
    }

    public ArrayList<VolunteerAssignment> getAssignmentsForVolunteer(int volunteerId) {
        ArrayList<VolunteerAssignment> list = new ArrayList<>();
        for (VolunteerAssignment a : assignmentList) {
            if (a.getVolunteerId() == volunteerId) {
                list.add(a);
            }
        }
        return list;
    }

    public boolean updateAssignmentStatus(int assignmentId, DonationStatus newStatus) {
        VolunteerAssignment a = assignmentMap.get(assignmentId);
        if (a != null) {
            a.setStatus(newStatus);
            fileManager.saveAssignments(assignmentList);
            return true;
        }
        return false;
    }

    public int getNextAssignmentId() {
        int max = 300;
        for (VolunteerAssignment a : assignmentList) {
            if (a.getAssignmentId() > max) {
                max = a.getAssignmentId();
            }
        }
        return max + 1;
    }

    public void save() {
        fileManager.saveAssignments(assignmentList);
    }
}
