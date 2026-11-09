package placeprep.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Student> students = new ArrayList<>();
    private final List<Company> companies = new ArrayList<>();
    private final List<Question> questions = new ArrayList<>();
    private final List<TestResult> results = new ArrayList<>();

    public List<Student> getStudents() { return students; }
    public List<Company> getCompanies() { return companies; }
    public List<Question> getQuestions() { return questions; }
    public List<TestResult> getResults() { return results; }
}
