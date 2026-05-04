/**
 * ValidationUtils — Method Abstraction & Reusability
 *
 * Centralizes all input validation logic so that no panel or class
 * needs to duplicate the same checks. Every validation rule is defined
 * once and reused across Admin, Instructor, and Student panels.
 */
public class ValidationUtils {

    /** Returns true if the string is null or blank. */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Checks that none of the provided strings are null or blank.
     * @param fields varargs of strings to check
     * @return true if all fields are non-empty
     */
    public static boolean allFieldsFilled(String... fields) {
        for (String f : fields) {
            if (isNullOrEmpty(f)) return false;
        }
        return true;
    }

    /**
     * Parses a string as a double score and validates the range [0, 100].
     * @param s the string to parse
     * @return the parsed double
     * @throws IllegalArgumentException if not a number or out of range
     */
    public static double parseScore(String s) {
        double val;
        try {
            val = Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + s + "' is not a valid number.");
        }
        if (val < 0 || val > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100. Got: " + val);
        }
        return val;
    }

    /**
     * Parses a string as a positive integer.
     * @param s the string to parse
     * @return the parsed int
     * @throws IllegalArgumentException if not a number or not positive
     */
    public static int parsePositiveInt(String s) {
        int val;
        try {
            val = Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + s + "' is not a valid integer.");
        }
        if (val <= 0) {
            throw new IllegalArgumentException("Value must be a positive number. Got: " + val);
        }
        return val;
    }

    /**
     * Parses and validates a student year (must be 1–4).
     * @param s the string to parse
     * @return the parsed year
     * @throws IllegalArgumentException if out of range
     */
    public static int parseYear(String s) {
        int val;
        try {
            val = Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Year must be a number between 1 and 4.");
        }
        if (val < 1 || val > 4) {
            throw new IllegalArgumentException("Year must be between 1 and 4. Got: " + val);
        }
        return val;
    }

    /**
     * Returns a formatted GPA string with 2 decimal places.
     * @param gpa the GPA value
     * @return formatted string e.g. "3.25"
     */
    public static String formatGpa(double gpa) {
        return String.format("%.2f", gpa);
    }

    /**
     * Returns a formatted score string with 1 decimal place.
     * @param score the score value
     * @return formatted string e.g. "87.5"
     */
    public static String formatScore(double score) {
        return String.format("%.1f", score);
    }
}
