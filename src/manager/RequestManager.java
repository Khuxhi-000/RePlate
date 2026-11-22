package manager;

import model.DonationRequest;
import model.FoodCategory;
import model.RequestPriority;
import util.FileManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Manages NGO food requests.
 * Demonstrates:
 * - ArrayList to store all requests
 * - HashMap for fast lookup by request ID
 * - PriorityQueue to automatically process URGENT requests before LOW priority requests
 */
public class RequestManager {
    private ArrayList<DonationRequest> requestList;
    private HashMap<Integer, DonationRequest> requestMap;
    private PriorityQueue<DonationRequest> priorityQueue;
    private FileManager fileManager;

    public RequestManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.requestList = new ArrayList<>();
        this.requestMap = new HashMap<>();
        this.priorityQueue = new PriorityQueue<>();
        loadRequests();
    }

    private void loadRequests() {
        ArrayList<DonationRequest> loaded = fileManager.loadRequests();
        if (loaded.isEmpty()) {
            seedInitialRequests();
        } else {
            for (DonationRequest r : loaded) {
                requestList.add(r);
                requestMap.put(r.getRequestId(), r);
                if ("PENDING".equalsIgnoreCase(r.getStatus())) {
                    priorityQueue.add(r);
                }
            }
        }
    }

    private void seedInitialRequests() {
        String today = LocalDate.now().toString();
        // Sample request: Helping Hands NGO requesting 30 packets of Cooked Food in Chandigarh
        createRequest(new DonationRequest(201, 4, "Helping Hands NGO", 
                FoodCategory.COOKED_FOOD, 30, "Chandigarh", RequestPriority.HIGH, "PENDING", today));
    }

    public void createRequest(DonationRequest request) {
        requestList.add(request);
        requestMap.put(request.getRequestId(), request);
        if ("PENDING".equalsIgnoreCase(request.getStatus())) {
            priorityQueue.add(request);
        }
        fileManager.saveRequests(requestList);
    }

    public DonationRequest getRequestById(int id) {
        return requestMap.get(id);
    }

    public ArrayList<DonationRequest> getAllRequests() {
        return requestList;
    }

    public ArrayList<DonationRequest> getRequestsByNgo(int ngoId) {
        ArrayList<DonationRequest> result = new ArrayList<>();
        for (DonationRequest r : requestList) {
            if (r.getNgoId() == ngoId) {
                result.add(r);
            }
        }
        return result;
    }

    public DonationRequest peekNextUrgentRequest() {
        return priorityQueue.peek();
    }

    public DonationRequest pollNextUrgentRequest() {
        return priorityQueue.poll();
    }

    public PriorityQueue<DonationRequest> getPriorityQueue() {
        return priorityQueue;
    }

    public boolean updateRequestStatus(int requestId, String newStatus) {
        DonationRequest req = requestMap.get(requestId);
        if (req != null) {
            req.setStatus(newStatus);
            // Rebuild priority queue to reflect status change
            priorityQueue.clear();
            for (DonationRequest r : requestList) {
                if ("PENDING".equalsIgnoreCase(r.getStatus())) {
                    priorityQueue.add(r);
                }
            }
            save();
            return true;
        }
        return false;
    }

    public boolean cancelRequest(int requestId, int ngoId) {
        DonationRequest req = requestMap.get(requestId);
        if (req != null && req.getNgoId() == ngoId && "PENDING".equalsIgnoreCase(req.getStatus())) {
            req.setStatus("CANCELLED");
            priorityQueue.remove(req);
            save();
            return true;
        }
        return false;
    }

    public int getNextRequestId() {
        int max = 200;
        for (DonationRequest r : requestList) {
            if (r.getRequestId() > max) {
                max = r.getRequestId();
            }
        }
        return max + 1;
    }

    public void save() {
        fileManager.saveRequests(requestList);
    }
}
