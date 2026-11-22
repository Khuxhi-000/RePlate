package util;

import java.util.Scanner;

/**
 * Utility class providing safe and robust terminal input reading.
 * Prevents application crashes from invalid data types, empty strings,
 * negative quantities, or invalid menu choices.
 */
public class InputValidator {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Reads an integer within the specified inclusive range [min, max].
     * Loops until a valid integer is entered.
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println(">> Error: Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println(">> Error: Invalid number format. Please enter an integer.");
            }
        }
    }

    /**
     * Reads a positive integer (e.g., quantity > 0).
     */
    public static int readPositiveInt(String prompt) {
        return readInt(prompt, 1, Integer.MAX_VALUE);
    }

    /**
     * Reads a non-empty string from terminal.
     */
    public static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println(">> Error: Input cannot be empty. Please enter a valid value.");
                continue;
            }
            return input;
        }
    }

    /**
     * Reads an optional string (can be empty).
     */
    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Validates that quantity is greater than 0.
     */
    public static void validateQuantity(int quantity) throws ValidationException {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be greater than zero.");
        }
    }

    /**
     * Validates that a string is not null or blank.
     */
    public static void validateRequiredText(String text, String fieldName) throws ValidationException {
        if (text == null || text.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be blank.");
        }
    }

    /**
     * Pauses the terminal until the user presses Enter.
     */
    public static void pressEnterToContinue() {
        System.out.println("\nPress [Enter] to continue...");
        scanner.nextLine();
    }
}
