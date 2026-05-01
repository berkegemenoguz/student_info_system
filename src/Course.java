/**
 * Course — Encapsulation & Proper Class Design
 * All fields are private; access is via public getters only.
 * Implements Persistable for consistent file serialization (Method Abstraction).
 */
public class Course implements Persistable {
    private String courseCode;
    private String courseName;
    private int credit;
    private int quota;
    private String instructorUsername;

    public Course(String courseCode, String courseName, int credit, int quota, String instructorUsername) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credit = credit;
        this.quota = quota;
        this.instructorUsername = instructorUsername;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCredit() { return credit; }
    public int getQuota() { return quota; }
    public String getInstructorUsername() { return instructorUsername; }

    public String toFileString() {
        return courseCode + "|" + courseName + "|" + credit + "|" + quota + "|" + instructorUsername;
    }

    public static Course fromFileString(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 5) return null;
            return new Course(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), parts[4]);
        } catch (Exception e) {
            return null;
        }
    }
}
