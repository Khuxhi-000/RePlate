package util;

/**
 * Custom exception class representing input validation failures.
 * Demonstrates Java Exception Handling concept.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
