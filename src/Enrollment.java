/**
 * Enrollment — Encapsulation & Proper Class Design
 * All fields are private; access is via public getters only.
 * Implements Persistable for consistent file serialization (Method Abstraction).
 */
public class Enrollment implements Persistable {
    private String studentUsername;
    private String courseCode;

    public Enrollment(String studentUsername, String courseCode) {
        this.studentUsername = studentUsername;
        this.courseCode = courseCode;
    }

    public String getStudentUsername() { return studentUsername; }
    public String getCourseCode() { return courseCode; }

    public String toFileString() {
        return studentUsername + "|" + courseCode;
    }

    public static Enrollment fromFileString(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 2) return null;
            return new Enrollment(parts[0], parts[1]);
        } catch (Exception e) {
            return null;
        }
    }
}
