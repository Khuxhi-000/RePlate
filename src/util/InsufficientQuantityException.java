package util;

/**
 * Custom checked exception thrown when requested food quantity exceeds available surplus.
 * Demonstrates custom exception handling with throw and throws in Java.
 */
public class InsufficientQuantityException extends Exception {
    private final int requested;
    private final int available;

    public InsufficientQuantityException(int requested, int available) {
        super("Requested quantity (" + requested + " packets) exceeds available quantity (" + available + " packets).");
        this.requested = requested;
        this.available = available;
    }

    public int getRequested() {
        return requested;
    }

    public int getAvailable() {
        return available;
    }
}
