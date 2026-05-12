public class Course implements Persistable {
    private String courseCode;
    private String courseName;
    private int credit;
    private int quota;
    private String instructorUsername;
    private String classroomId;

    public Course(String courseCode, String courseName, int credit, int quota, String instructorUsername) {
        this(courseCode, courseName, credit, quota, instructorUsername, "");
    }

    public Course(String courseCode, String courseName, int credit, int quota, String instructorUsername, String classroomId) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credit = credit;
        this.quota = quota;
        this.instructorUsername = instructorUsername;
        this.classroomId = classroomId != null ? classroomId : "";
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCredit() { return credit; }
    public int getQuota() { return quota; }
    public String getInstructorUsername() { return instructorUsername; }
    public String getClassroomId() { return classroomId; }
    public void setClassroomId(String classroomId) { this.classroomId = classroomId != null ? classroomId : ""; }

    public String toFileString() {
        return courseCode + "|" + courseName + "|" + credit + "|" + quota + "|" + instructorUsername + "|" + classroomId;
    }

    public static Course fromFileString(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 5) return null;
            String classroomId = parts.length >= 6 ? parts[5] : "";
            return new Course(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), parts[4], classroomId);
        } catch (Exception e) {
            return null;
        }
    }
}
