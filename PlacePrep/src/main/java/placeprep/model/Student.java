
package placeprep.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String email;
    private final String branch;
    private final double cgpa;
    private final int backlogs;

    private final List<String> skills;
    private final List<String> languages;
    private final List<String> curricularActivities;

    // Original constructor for compatibility
    public Student(
            String id,
            String name,
            String email,
            String branch,
            double cgpa,
            int backlogs,
            List<String> skills,
            List<String> languages) {

        this(id, name, email, branch, cgpa, backlogs,
                skills, languages, Collections.emptyList());
    }

    // Constructor including curricular activities
    public Student(
            String id,
            String name,
            String email,
            String branch,
            double cgpa,
            int backlogs,
            List<String> skills,
            List<String> languages,
            List<String> curricularActivities) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.branch = branch;
        this.cgpa = cgpa;
        this.backlogs = backlogs;

        this.skills = new ArrayList<>(skills);
        this.languages = new ArrayList<>(languages);

        this.curricularActivities =
                curricularActivities == null
                        ? new ArrayList<>()
                        : new ArrayList<>(curricularActivities);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBranch() {
        return branch;
    }

    public double getCgpa() {
        return cgpa;
    }

    public int getBacklogs() {
        return backlogs;
    }

    public List<String> getSkills() {
        return new ArrayList<>(skills);
    }

    public List<String> getLanguages() {
        return new ArrayList<>(languages);
    }

    public List<String> getCurricularActivities() {
        return curricularActivities == null
                ? new ArrayList<>()
                : new ArrayList<>(curricularActivities);
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
