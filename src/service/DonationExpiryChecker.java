package service;

import manager.DonationManager;

/**
 * Background thread that periodically checks for and updates expired food donations.
 * Demonstrates:
 * - Multithreading via Thread class
 * - Thread lifecycle (NEW, RUNNABLE, TIMED_WAITING, TERMINATED)
 * - Thread methods: start(), run(), sleep(), interrupt(), join()
 * - Synchronization: coordinates with DonationManager critical sections
 * - Clean thread termination without abrupt stoppage
 */
public class DonationExpiryChecker extends Thread {
    private final DonationManager donationManager;
    private final long intervalMillis;
    private volatile boolean running;

    public DonationExpiryChecker(DonationManager donationManager, long intervalMillis) {
        super("DonationExpiryChecker-Thread");
        this.donationManager = donationManager;
        this.intervalMillis = intervalMillis;
        this.running = true;
    }

    @Override
    public void run() {
        while (running && !isInterrupted()) {
            try {
                // Critical section access: synchronized inside DonationManager
                int expiredCount = donationManager.checkAndUpdateExpiredDonations();
                if (expiredCount > 0) {
                    System.out.println("\n[Background Worker Alert] " + getName() 
                            + " auto-expired " + expiredCount + " donation(s) past validity time.");
                }
                // Transition to TIMED_WAITING state
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                // Thread interrupted during sleep() for clean shutdown
                break;
            } catch (Exception e) {
                System.out.println("[Background Thread Warning] Error during expiry check: " + e.getMessage());
            }
        }
    }

    /**
     * Gracefully stops the background thread using interrupt() and waits using join().
     */
    public void stopChecker() {
        this.running = false;
        this.interrupt(); // Interrupt sleep() immediately
        try {
            this.join(2000); // Wait up to 2 seconds for clean thread termination
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isRunning() {
        return running;
    }
}
