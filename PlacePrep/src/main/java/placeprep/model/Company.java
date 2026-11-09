package placeprep.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Company implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final double minCgpa;
    private final int maxBacklogs;
    private final List<String> requiredSkills;
    private final List<String> requiredLanguages;

    public Company(String id, String name, double minCgpa, int maxBacklogs,
                   List<String> requiredSkills, List<String> requiredLanguages) {
        this.id = id;
        this.name = name;
        this.minCgpa = minCgpa;
        this.maxBacklogs = maxBacklogs;
        this.requiredSkills = new ArrayList<>(requiredSkills);
        this.requiredLanguages = new ArrayList<>(requiredLanguages);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getMinCgpa() { return minCgpa; }
    public int getMaxBacklogs() { return maxBacklogs; }
    public List<String> getRequiredSkills() { return new ArrayList<>(requiredSkills); }
    public List<String> getRequiredLanguages() { return new ArrayList<>(requiredLanguages); }

    @Override
    public String toString() {
        return name;
    }
}
