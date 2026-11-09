package placeprep.service;

import placeprep.model.Company;
import placeprep.model.Student;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class EligibilityService {
    private EligibilityService() { }

    public static boolean isEligible(Student student, Company company) {
        return student.getCgpa() >= company.getMinCgpa()
                && student.getBacklogs() <= company.getMaxBacklogs()
                && normalized(student.getSkills()).containsAll(normalized(company.getRequiredSkills()))
                && normalized(student.getLanguages()).containsAll(normalized(company.getRequiredLanguages()));
    }

    public static List<String> missingRequirements(Student student, Company company) {
        List<String> missing = new java.util.ArrayList<>();

        if (student.getCgpa() < company.getMinCgpa()) {
            missing.add("CGPA below " + company.getMinCgpa());
        }
        if (student.getBacklogs() > company.getMaxBacklogs()) {
            missing.add("Backlogs exceed " + company.getMaxBacklogs());
        }

        Set<String> studentSkills = normalized(student.getSkills());
        for (String skill : company.getRequiredSkills()) {
            if (!studentSkills.contains(normalize(skill))) {
                missing.add("Missing skill: " + skill);
            }
        }

        Set<String> studentLanguages = normalized(student.getLanguages());
        for (String language : company.getRequiredLanguages()) {
            if (!studentLanguages.contains(normalize(language))) {
                missing.add("Missing language: " + language);
            }
        }
        return missing;
    }

    private static Set<String> normalized(List<String> values) {
        return values.stream()
                .map(EligibilityService::normalize)
                .collect(Collectors.toSet());
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
