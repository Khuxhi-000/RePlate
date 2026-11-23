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
     * Reads a valid email address from terminal.
     * Loops until a valid email is entered.
     */
    public static String readValidEmail(String prompt) {
        while (true) {
            String input = readNonEmptyString(prompt);
            try {
                validateEmail(input);
                return input;
            } catch (ValidationException e) {
                System.out.println(">> Error: " + e.getMessage());
            }
        }
    }

    /**
     * Reads a valid 10-digit Indian phone number from terminal.
     * Loops until a valid phone number is entered.
     */
    public static String readValidPhone(String prompt) {
        while (true) {
            String input = readNonEmptyString(prompt);
            try {
                validatePhone(input);
                return input;
            } catch (ValidationException e) {
                System.out.println(">> Error: " + e.getMessage());
            }
        }
    }

    /**
     * Reads a valid NGO registration number from terminal.
     * Loops until a valid registration number is entered.
     */
    public static String readValidNgoRegNo(String prompt) {
        while (true) {
            String input = readNonEmptyString(prompt);
            try {
                validateNgoRegNo(input);
                return input.toUpperCase();
            } catch (ValidationException e) {
                System.out.println(">> Error: " + e.getMessage());
            }
        }
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
     * Validates standard email address format.
     */
    public static void validateEmail(String email) throws ValidationException {
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Invalid email format. Please enter a valid email (e.g., user@gmail.com).");
        }
    }

    /**
     * Validates 10-digit Indian mobile number format.
     * Starts with 6, 7, 8, or 9 and contains exactly 10 digits.
     */
    public static void validatePhone(String phone) throws ValidationException {
        if (phone == null || !phone.matches("^[6-9]\\d{9}$")) {
            throw new ValidationException("Invalid phone number. Must be a valid 10-digit Indian mobile number (e.g., 9876543210).");
        }
    }

    /**
     * Validates NGO registration number format (e.g., NGO-CH-105).
     * Must follow NGO-XX-XXX format.
     */
    public static void validateNgoRegNo(String regNo) throws ValidationException {
        if (regNo == null || !regNo.trim().toUpperCase().matches("^NGO-[A-Z]{2,4}-[0-9]{3,6}$")) {
            throw new ValidationException("Invalid NGO registration number. Expected format: NGO-XX-XXX (e.g., NGO-CH-105).");
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
