package placeprep.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TestResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String studentId;
    private final String studentName;
    private final String category;
    private final int score;
    private final int total;
    private final LocalDateTime completedAt;

    public TestResult(String studentId, String studentName, String category,
                      int score, int total, LocalDateTime completedAt) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.category = category;
        this.score = score;
        this.total = total;
        this.completedAt = completedAt;
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getCategory() { return category; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public LocalDateTime getCompletedAt() { return completedAt; }

    public double getPercentage() {
        return total == 0 ? 0 : (score * 100.0 / total);
    }
}
