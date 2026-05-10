import java.io.*;
import java.util.*;

public class DataStore {
    private List<User> users;
    private List<StudentProfile> students;
    private List<Course> courses;
    private List<Enrollment> enrollments;
    private List<GradeRecord> grades;

    private static final Map<String, Double> GPA_MAP = new LinkedHashMap<>();
    static {
        GPA_MAP.put("AA", 4.0);
        GPA_MAP.put("BA", 3.5);
        GPA_MAP.put("BB", 3.0);
        GPA_MAP.put("CB", 2.5);
        GPA_MAP.put("CC", 2.0);
        GPA_MAP.put("DC", 1.5);
        GPA_MAP.put("DD", 1.0);
        GPA_MAP.put("FD", 0.5);
        GPA_MAP.put("FF", 0.0);
    }

    public DataStore() {
        initialize();
    }

    public void initialize() {
        users = new ArrayList<>();
        students = new ArrayList<>();
        courses = new ArrayList<>();
        enrollments = new ArrayList<>();
        grades = new ArrayList<>();
        loadUsers();
        loadStudents();
        loadCourses();
        loadEnrollments();
        loadGrades();
        if (users.isEmpty()) {
            users.add(new User("admin", "admin", "Admin", "Administrator", ""));
            saveUsers();
        }
    }

    // ---- Authentication & Lookup ----

    public User authenticate(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public User findUser(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

    public StudentProfile findStudentProfileByUsername(String username) {
        for (StudentProfile sp : students) {
            if (sp.getUsername().equals(username)) return sp;
        }
        return null;
    }

    public Course findCourseByCode(String courseCode) {
        for (Course c : courses) {
            if (c.getCourseCode().equals(courseCode)) return c;
        }
        return null;
    }

    public List<Course> getCoursesByInstructor(String instructorUsername) {
        List<Course> result = new ArrayList<>();
        for (Course c : courses) {
            if (c.getInstructorUsername().equals(instructorUsername)) result.add(c);
        }
        return result;
    }

    // ---- Enrollment Logic ----

    public int countEnrollmentsForCourse(String courseCode) {
        int count = 0;
        for (Enrollment e : enrollments) {
            if (e.getCourseCode().equals(courseCode)) count++;
        }
        return count;
    }

    public boolean studentEnroll(String studentUsername, String courseCode) {
        Course course = findCourseByCode(courseCode);
        if (course == null) return false;
        for (Enrollment e : enrollments) {
            if (e.getStudentUsername().equals(studentUsername) && e.getCourseCode().equals(courseCode)) {
                return false;
            }
        }
        if (countEnrollmentsForCourse(courseCode) >= course.getQuota()) return false;
        enrollments.add(new Enrollment(studentUsername, courseCode));
        saveEnrollments();
        return true;
    }

    public List<Course> getEnrolledCoursesByStudent(String studentUsername) {
        List<Course> result = new ArrayList<>();
        for (Enrollment e : enrollments) {
            if (e.getStudentUsername().equals(studentUsername)) {
                Course c = findCourseByCode(e.getCourseCode());
                if (c != null) result.add(c);
            }
        }
        return result;
    }

    public List<Enrollment> getEnrollmentsByCourse(String courseCode) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : enrollments) {
            if (e.getCourseCode().equals(courseCode)) result.add(e);
        }
        return result;
    }

    public void removeEnrollment(String studentUsername, String courseCode) {
        enrollments.removeIf(e -> e.getStudentUsername().equals(studentUsername) && e.getCourseCode().equals(courseCode));
        saveEnrollments();
    }

    // ---- Grade Logic ----

    public GradeRecord findGrade(String studentUsername, String courseCode) {
        for (GradeRecord g : grades) {
            if (g.getStudentUsername().equals(studentUsername) && g.getCourseCode().equals(courseCode)) return g;
        }
        return null;
    }

    public List<GradeRecord> getGradesByStudent(String studentUsername) {
        List<GradeRecord> result = new ArrayList<>();
        for (GradeRecord g : grades) {
            if (g.getStudentUsername().equals(studentUsername)) result.add(g);
        }
        return result;
    }

    public void upsertGrade(String studentUsername, String courseCode, double midterm, double finalExam) {
        GradeRecord existing = findGrade(studentUsername, courseCode);
        if (existing != null) {
            existing.setMidterm(midterm);
            existing.setFinalExam(finalExam);
        } else {
            grades.add(new GradeRecord(studentUsername, courseCode, midterm, finalExam));
        }
        saveGrades();
    }

    public double calculateGPA(String studentUsername) {
        List<GradeRecord> studentGrades = getGradesByStudent(studentUsername);
        if (studentGrades.isEmpty()) return 0.0;

        double totalPoints = 0;
        int totalCredits = 0;
        for (GradeRecord g : studentGrades) {
            Course c = findCourseByCode(g.getCourseCode());
            int credit = (c != null) ? c.getCredit() : 1;
            Double gpaValue = GPA_MAP.get(g.getLetterGrade());
            if (gpaValue != null) {
                totalPoints += gpaValue * credit;
                totalCredits += credit;
            }
        }
        return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }

    // ---- Add Methods ----

    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public void addStudent(StudentProfile sp) {
        students.add(sp);
        saveStudents();
    }

    public void addCourse(Course c) {
        courses.add(c);
        saveCourses();
    }

    // ---- Remove Methods ----

    public void removeUser(String username) {
        users.removeIf(u -> u.getUsername().equals(username));
        saveUsers();
    }

    public void removeStudent(String studentId) {
        students.removeIf(sp -> sp.getStudentId().equals(studentId));
        saveStudents();
    }

    public void removeCourse(String courseCode) {
        courses.removeIf(c -> c.getCourseCode().equals(courseCode));
        // Also remove related enrollments and grades for consistency
        enrollments.removeIf(e -> e.getCourseCode().equals(courseCode));
        grades.removeIf(g -> g.getCourseCode().equals(courseCode));
        saveCourses();
        saveEnrollments();
        saveGrades();
    }

    // ---- Getters ----

    public List<User> getUsers() { return users; }
    public List<StudentProfile> getStudents() { return students; }
    public List<Course> getCourses() { return courses; }
    public List<Enrollment> getEnrollments() { return enrollments; }
    public List<GradeRecord> getGrades() { return grades; }

    // ---- File I/O ----

    // Uses the Persistable-typed saveToFile — Reusability in action
    public void saveUsers()       { saveToFile("users.txt",       users); }
    public void saveStudents()    { saveToFile("students.txt",    students); }
    public void saveCourses()     { saveToFile("courses.txt",     courses); }
    public void saveEnrollments() { saveToFile("enrollments.txt", enrollments); }
    public void saveGrades()      { saveToFile("grades.txt",      grades); }

    public void loadUsers() {
        users = loadFromFile("users.txt", User::fromFileString);
    }
    public void loadStudents() {
        students = loadFromFile("students.txt", StudentProfile::fromFileString);
    }
    public void loadCourses() {
        courses = loadFromFile("courses.txt", Course::fromFileString);
    }
    public void loadEnrollments() {
        enrollments = loadFromFile("enrollments.txt", Enrollment::fromFileString);
    }
    public void loadGrades() {
        grades = loadFromFile("grades.txt", GradeRecord::fromFileString);
    }

    /**
     * Generic save method — Reusability & Method Abstraction.
     * Works with ANY Persistable type: User, Course, Enrollment, etc.
     * The Persistable interface contract guarantees toFileString() exists,
     * so no type-specific logic is needed here.
     */
    private <T extends Persistable> void saveToFile(String filename, List<T> list) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (T item : list) {
                writer.write(item.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving " + filename + ": " + e.getMessage());
        }
    }

    private <T> List<T> loadFromFile(String filename, java.util.function.Function<String, T> parser) {
        List<T> list = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) return list;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                T item = parser.apply(line);
                if (item != null) list.add(item);
            }
        } catch (IOException e) {
            System.err.println("Error loading " + filename + ": " + e.getMessage());
        }
        return list;
    }
}
