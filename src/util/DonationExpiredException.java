package util;

/**
 * Custom checked exception thrown when an operation is attempted on an expired donation.
 * Demonstrates custom exception handling with throw and throws in Java.
 */
public class DonationExpiredException extends Exception {
    public DonationExpiredException(String message) {
        super(message);
    }
}
