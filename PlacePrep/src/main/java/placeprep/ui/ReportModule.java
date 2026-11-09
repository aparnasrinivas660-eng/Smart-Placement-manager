package placeprep.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import placeprep.model.AppData;
import placeprep.model.Company;
import placeprep.model.Student;
import placeprep.model.TestResult;
import placeprep.service.EligibilityService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ReportModule extends BorderPane {
    private final AppData data;
    private final ComboBox<Student> studentBox = new ComboBox<>();
    private final TextArea reportArea = new TextArea();

    public ReportModule(AppData data) {
        this.data = data;
        setPadding(new Insets(16));
        setTop(buildControls());
        setCenter(buildReportArea());
        refresh();
    }

    private VBox buildControls() {
        Label heading = new Label("Performance and Placement Reports");
        heading.getStyleClass().add("section-title");

        studentBox.setPromptText("Select student");
        studentBox.setPrefWidth(260);

        Button generateButton = new Button("Generate Student Report");
        generateButton.getStyleClass().add("primary-button");
        generateButton.setOnAction(event -> generateReport());

        Button exportStudentButton = new Button("Export Student Report");
        exportStudentButton.setOnAction(event -> exportStudentReport());

        Button exportEligibilityButton = new Button("Export All Eligibility CSV");
        exportEligibilityButton.getStyleClass().add("success-button");
        exportEligibilityButton.setOnAction(event -> exportEligibilityReport());

        HBox controls = new HBox(10, studentBox, generateButton,
                exportStudentButton, exportEligibilityButton);

        VBox box = new VBox(12, heading, controls);
        box.getStyleClass().add("panel");
        box.setPadding(new Insets(14));
        return box;
    }

    private VBox buildReportArea() {
        reportArea.setEditable(false);
        reportArea.setWrapText(true);
        reportArea.setPromptText("Select a student and generate a report.");
        VBox box = new VBox(reportArea);
        box.setPadding(new Insets(16, 0, 0, 0));
        VBox.setVgrow(reportArea, Priority.ALWAYS);
        return box;
    }

    private String buildStudentReport(Student student) {
        StringBuilder output = new StringBuilder();
        output.append("PLACEPREP – STUDENT PERFORMANCE REPORT\n");
        output.append("========================================\n\n");
        output.append("Student ID: ").append(student.getId()).append('\n');
        output.append("Name: ").append(student.getName()).append('\n');
        output.append("Email: ").append(student.getEmail()).append('\n');
        output.append("Branch: ").append(student.getBranch()).append('\n');
        output.append("CGPA: ").append(student.getCgpa()).append('\n');
        output.append("Backlogs: ").append(student.getBacklogs()).append('\n');
        output.append("Skills: ").append(String.join(", ", student.getSkills())).append('\n');
        output.append("Programming languages: ")
                .append(String.join(", ", student.getLanguages())).append("\n\n");

        List<TestResult> results = data.getResults().stream()
                .filter(result -> result.getStudentId().equals(student.getId()))
                .toList();

        output.append("PRACTICE TEST HISTORY\n");
        output.append("---------------------\n");
        if (results.isEmpty()) {
            output.append("No practice tests completed yet.\n");
        } else {
            double average = results.stream()
                    .mapToDouble(TestResult::getPercentage).average().orElse(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            for (TestResult result : results) {
                output.append(result.getCategory()).append(": ")
                        .append(result.getScore()).append('/')
                        .append(result.getTotal()).append(" (")
                        .append(String.format(Locale.ROOT, "%.1f", result.getPercentage()))
                        .append("%) – ")
                        .append(result.getCompletedAt().format(formatter))
                        .append('\n');
            }
            output.append("Average test percentage: ")
                    .append(String.format(Locale.ROOT, "%.1f", average)).append("%\n");
        }

        output.append("\nCOMPANY ELIGIBILITY\n");
        output.append("-------------------\n");
        if (data.getCompanies().isEmpty()) {
            output.append("No companies have been added.\n");
        } else {
            for (Company company : data.getCompanies()) {
                boolean eligible = EligibilityService.isEligible(student, company);
                output.append(company.getName()).append(": ")
                        .append(eligible ? "ELIGIBLE" : "NOT ELIGIBLE").append('\n');
                if (!eligible) {
                    for (String missing : EligibilityService.missingRequirements(student, company)) {
                        output.append("  - ").append(missing).append('\n');
                    }
                }
            }
        }
        return output.toString();
    }

    private void generateReport() {
        Student student = studentBox.getValue();
        if (student == null) {
            UiUtils.error("Select a student", "Choose a student first.");
            return;
        }
        reportArea.setText(buildStudentReport(student));
    }

    private void exportStudentReport() {
        Student student = studentBox.getValue();
        if (student == null) {
            UiUtils.error("Select a student", "Choose a student first.");
            return;
        }

        try {
            Files.createDirectories(Path.of("reports"));
            Path file = Path.of("reports", "student_" + student.getId() + "_report.txt");
            Files.writeString(file, buildStudentReport(student));
            UiUtils.info("Report exported", "Saved to: " + file.toAbsolutePath());
        } catch (IOException ex) {
            UiUtils.error("Export failed", ex.getMessage());
        }
    }

    private void exportEligibilityReport() {
        StringBuilder csv = new StringBuilder();
        csv.append("Company,Student ID,Student Name,Eligible,Reason if not eligible\n");

        for (Company company : data.getCompanies()) {
            for (Student student : data.getStudents()) {
                boolean eligible = EligibilityService.isEligible(student, company);
                String reason = eligible ? "" :
                        String.join("; ", EligibilityService.missingRequirements(student, company));
                csv.append(csvEscape(company.getName())).append(',')
                        .append(csvEscape(student.getId())).append(',')
                        .append(csvEscape(student.getName())).append(',')
                        .append(eligible ? "YES" : "NO").append(',')
                        .append(csvEscape(reason)).append('\n');
            }
        }

        try {
            Files.createDirectories(Path.of("reports"));
            Path file = Path.of("reports", "company_eligibility_report.csv");
            Files.writeString(file, csv.toString());
            UiUtils.info("Eligibility report exported",
                    "Saved to: " + file.toAbsolutePath());
        } catch (IOException ex) {
            UiUtils.error("Export failed", ex.getMessage());
        }
    }

    private String csvEscape(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    public void refresh() {
        studentBox.setItems(FXCollections.observableArrayList(data.getStudents()));
    }
}
