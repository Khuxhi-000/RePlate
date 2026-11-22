package service;

import util.FileManager;

import java.util.ArrayList;

/**
 * Maintains and displays historical records of completed,
 * cancelled, or expired donation and delivery events.
 */
public class DonationHistory {
    private ArrayList<String> historyRecords;
    private FileManager fileManager;

    public DonationHistory(FileManager fileManager) {
        this.fileManager = fileManager;
        this.historyRecords = fileManager.loadHistory();
    }

    public void addRecord(String record) {
        historyRecords.add(record);
        fileManager.appendHistory(record);
    }

    public ArrayList<String> getHistoryRecords() {
        return historyRecords;
    }

    public void displayHistory() {
        System.out.println("\n================================================================================");
        System.out.println("                         FOOD DONATION & DELIVERY HISTORY");
        System.out.println("================================================================================");
        if (historyRecords.isEmpty()) {
            System.out.println("No history records available yet.");
        } else {
            for (int i = 0; i < historyRecords.size(); i++) {
                System.out.println((i + 1) + ". " + historyRecords.get(i));
            }
        }
        System.out.println("================================================================================");
    }
}
