package placeprep.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import placeprep.model.AppData;
import placeprep.model.Company;
import placeprep.model.Student;
import placeprep.service.EligibilityService;

import java.util.List;

public class RecruitmentModule extends BorderPane {
    private final AppData data;
    private final Runnable onChange;

    private final TextField nameField = new TextField();
    private final TextField minCgpaField = new TextField();
    private final TextField maxBacklogsField = new TextField();
    private final TextField skillsField = new TextField();
    private final TextField languagesField = new TextField();

    private final TableView<Company> companyTable = new TableView<>();
    private final ComboBox<Company> companyBox = new ComboBox<>();
    private final TableView<Student> eligibleTable = new TableView<>();
    private final Label resultLabel = new Label("Choose a company to view eligible students.");

    public RecruitmentModule(AppData data, Runnable onChange) {
        this.data = data;
        this.onChange = onChange;
        setPadding(new Insets(16));
        setTop(buildCompanyForm());
        setCenter(buildEligibilityArea());
        refresh();
    }

    private VBox buildCompanyForm() {
        Label heading = new Label("Company Management");
        heading.getStyleClass().add("section-title");

        nameField.setPromptText("Company name");
        minCgpaField.setPromptText("Minimum CGPA");
        maxBacklogsField.setPromptText("Maximum backlogs");
        skillsField.setPromptText("Required skills (Java, SQL)");
        languagesField.setPromptText("Required languages (Java, Python)");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.add(new Label("Company"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Minimum CGPA"), 2, 0);
        form.add(minCgpaField, 3, 0);
        form.add(new Label("Maximum backlogs"), 0, 1);
        form.add(maxBacklogsField, 1, 1);
        form.add(new Label("Required skills"), 2, 1);
        form.add(skillsField, 3, 1);
        form.add(new Label("Languages"), 0, 2);
        form.add(languagesField, 1, 2, 3, 1);

        Button addButton = new Button("Add Company");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(event -> addCompany());

        Button deleteButton = new Button("Delete Selected");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(event -> deleteCompany());

        HBox buttons = new HBox(10, addButton, deleteButton);

        TableColumn<Company, String> nameCol = new TableColumn<>("Company");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Company, Number> cgpaCol = new TableColumn<>("Minimum CGPA");
        cgpaCol.setCellValueFactory(new PropertyValueFactory<>("minCgpa"));
        TableColumn<Company, Number> backlogCol = new TableColumn<>("Max Backlogs");
        backlogCol.setCellValueFactory(new PropertyValueFactory<>("maxBacklogs"));
        companyTable.getColumns().addAll(nameCol, cgpaCol, backlogCol);
        companyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        companyTable.setPrefHeight(160);
        companyTable.setPlaceholder(new Label("No companies added yet."));

        VBox box = new VBox(10, heading, form, buttons, companyTable);
        box.getStyleClass().add("panel");
        box.setPadding(new Insets(14));
        return box;
    }

    private VBox buildEligibilityArea() {
        Label heading = new Label("Company Eligibility Checker");
        heading.getStyleClass().add("section-title");

        companyBox.setPromptText("Select company");
        companyBox.setPrefWidth(260);

        Button checkButton = new Button("Find Eligible Students");
        checkButton.getStyleClass().add("success-button");
        checkButton.setOnAction(event -> findEligibleStudents());

        HBox controls = new HBox(10, companyBox, checkButton);
        resultLabel.getStyleClass().add("muted-text");

        TableColumn<Student, String> idCol = new TableColumn<>("Student ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Student, String> nameCol = new TableColumn<>("Student");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Student, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));
        TableColumn<Student, Number> cgpaCol = new TableColumn<>("CGPA");
        cgpaCol.setCellValueFactory(new PropertyValueFactory<>("cgpa"));

        eligibleTable.getColumns().addAll(idCol, nameCol, branchCol, cgpaCol);
        eligibleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        eligibleTable.setPlaceholder(new Label("Eligibility results will appear here."));

        VBox box = new VBox(12, heading, controls, resultLabel, eligibleTable);
        box.setPadding(new Insets(16, 0, 0, 0));
        VBox.setVgrow(eligibleTable, Priority.ALWAYS);
        return box;
    }

    private void addCompany() {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Enter a company name.");
            }

            double minCgpa = Double.parseDouble(minCgpaField.getText().trim());
            int maxBacklogs = Integer.parseInt(maxBacklogsField.getText().trim());

            if (minCgpa < 0 || minCgpa > 10) {
                throw new IllegalArgumentException("Minimum CGPA must be between 0 and 10.");
            }
            if (maxBacklogs < 0) {
                throw new IllegalArgumentException("Maximum backlogs cannot be negative.");
            }

            String id = "COM" + System.currentTimeMillis();
            List<String> skills = UiUtils.parseCommaSeparated(skillsField.getText());
            List<String> languages = UiUtils.parseCommaSeparated(languagesField.getText());

            data.getCompanies().add(new Company(id, name, minCgpa,
                    maxBacklogs, skills, languages));

            nameField.clear();
            minCgpaField.clear();
            maxBacklogsField.clear();
            skillsField.clear();
            languagesField.clear();
            onChange.run();
            UiUtils.info("Company added", name + " was added successfully.");
        } catch (NumberFormatException ex) {
            UiUtils.error("Invalid number", "Enter valid numbers for CGPA and backlogs.");
        } catch (IllegalArgumentException ex) {
            UiUtils.error("Check the form", ex.getMessage());
        }
    }

    private void deleteCompany() {
        Company selected = companyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiUtils.info("Select a company", "Choose a company from the table first.");
            return;
        }
        if (UiUtils.confirm("Delete company", "Delete " + selected.getName() + "?")) {
            data.getCompanies().remove(selected);
            eligibleTable.getItems().clear();
            onChange.run();
        }
    }

    private void findEligibleStudents() {
        Company company = companyBox.getValue();
        if (company == null) {
            UiUtils.error("Select a company", "Choose a company first.");
            return;
        }

        List<Student> eligible = data.getStudents().stream()
                .filter(student -> EligibilityService.isEligible(student, company))
                .toList();

        eligibleTable.setItems(FXCollections.observableArrayList(eligible));
        resultLabel.setText(eligible.size() + " eligible student(s) for " + company.getName() + ".");
    }

    public void refresh() {
        companyTable.setItems(FXCollections.observableArrayList(data.getCompanies()));
        companyBox.setItems(FXCollections.observableArrayList(data.getCompanies()));
    }
}
