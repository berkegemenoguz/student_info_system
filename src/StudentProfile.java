/** Öğrenci demografik ve akademik bilgilerini tutar (ad, bölüm, sınıf, kullanıcı adı). */
public class StudentProfile implements Persistable {
    private String fullName;
    private String department;
    private int year;
    private String username;

    public StudentProfile(String fullName, String department, int year, String username) {
        this.fullName = fullName;
        this.department = department;
        this.year = year;
        this.username = username;
    }

    public String getFullName() { return fullName; }
    public String getDepartment() { return department; }
    public int getYear() { return year; }
    public String getUsername() { return username; }

    public String toFileString() {
        return fullName + "|" + department + "|" + year + "|" + username;
    }

    public static StudentProfile fromFileString(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 4) return null;
            return new StudentProfile(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
