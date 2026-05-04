/**
 * User — Encapsulation & Proper Class Design
 * All fields are private; access is via public getters only.
 * Implements Persistable for consistent file serialization (Method Abstraction).
 */
public class User implements Persistable {
    private String username;
    private String password;
    private String role;
    private String fullName;
    private String referenceId;

    public User(String username, String password, String role, String fullName, String referenceId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.referenceId = referenceId;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }
    public String getReferenceId() { return referenceId; }

    public String toFileString() {
        return username + "|" + password + "|" + role + "|" + fullName + "|" + referenceId;
    }

    public static User fromFileString(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 5) return null;
            return new User(parts[0], parts[1], parts[2], parts[3], parts[4]);
        } catch (Exception e) {
            return null;
        }
    }
}
