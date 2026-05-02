/**
 * StudentProfile — Encapsulation & Proper Class Design
 * All fields are private; access is via public getters only.
 * Implements Persistable for consistent file serialization (Method Abstraction).
 */
public class StudentProfile implements Persistable {
    private String studentId;
    private String fullName;
    private String department;
    private int year;
    private String username;

    public StudentProfile(String studentId, String fullName, String department, int year, String username) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.department = department;
        this.year = year;
        this.username = username;
    }

    public String getStudentId() { return studentId; }
    public String getFullName() { return fullName; }
    public String getDepartment() { return department; }
    public int getYear() { return year; }
    public String getUsername() { return username; }

    public String toFileString() {
        return studentId + "|" + fullName + "|" + department + "|" + year + "|" + username;
    }

    public static StudentProfile fromFileString(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 5) return null;
            return new StudentProfile(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4]);
        } catch (Exception e) {
            return null;
        }
    }
}
