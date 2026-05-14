/**
 * Bir öğrencinin bir dersteki not kaydı.
 * Ortalama hesaplaması: vize %40 + final %60.
 * Harf notu eşikleri: AA(90), BA(85), BB(80), CB(70), CC(60), DC(50), DD(40), FD(30), FF(<30).
 */
public class GradeRecord implements Persistable {
    private String studentUsername;
    private String courseCode;
    private double midterm;
    private double finalExam;

    public GradeRecord(String studentUsername, String courseCode, double midterm, double finalExam) {
        this.studentUsername = studentUsername;
        this.courseCode = courseCode;
        this.midterm = midterm;
        this.finalExam = finalExam;
    }

    public String getStudentUsername() { return studentUsername; }
    public String getCourseCode() { return courseCode; }
    public double getMidterm() { return midterm; }
    public double getFinalExam() { return finalExam; }

    public void setMidterm(double midterm) { this.midterm = midterm; }
    public void setFinalExam(double finalExam) { this.finalExam = finalExam; }

    public double calculateAverage() {
        return midterm * 0.4 + finalExam * 0.6;
    }

    public String getLetterGrade() {
        double avg = calculateAverage();
        if (avg >= 90) return "AA";
        if (avg >= 85) return "BA";
        if (avg >= 80) return "BB";
        if (avg >= 70) return "CB";
        if (avg >= 60) return "CC";
        if (avg >= 50) return "DC";
        if (avg >= 40) return "DD";
        if (avg >= 30) return "FD";
        return "FF";
    }

    public String toFileString() {
        return studentUsername + "|" + courseCode + "|" + midterm + "|" + finalExam;
    }

    public static GradeRecord fromFileString(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 4) return null;
            return new GradeRecord(parts[0], parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
