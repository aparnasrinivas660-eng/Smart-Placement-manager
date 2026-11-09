
package placeprep.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import placeprep.model.AppData;
import placeprep.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class StudentModule extends BorderPane {

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L}]+(?:\\s+[\\p{L}]+)*$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
            );

    private final AppData data;
    private final Runnable onChange;

    // Personal details
    private final TextField nameField = new TextField();
    private final TextField emailField = new TextField();

    private final ComboBox<String> branchComboBox =
            new ComboBox<>();

    private final TextField branchOtherField =
            new TextField();

    // Academic details
    private final TextField cgpaField = new TextField();
    private final TextField backlogsField = new TextField();

    // Additional details
    private final TextField skillsField = new TextField();
    private final TextField languagesField = new TextField();
    private final TextField activitiesField = new TextField();

    // Error labels
    private final Label nameError = createErrorLabel();
    private final Label emailError = createErrorLabel();
    private final Label branchError = createErrorLabel();
    private final Label branchOtherError = createErrorLabel();

    private final Label cgpaError = createErrorLabel();
    private final Label backlogsError = createErrorLabel();

    private final Label skillsError = createErrorLabel();
    private final Label languagesError = createErrorLabel();
    private final Label activitiesError = createErrorLabel();

    private final TableView<Student> table = new TableView<>();

    private final VBox stepContent = new VBox(10);
    private final Label stepLabel = new Label();

    private final Button backButton = new Button("Back");
    private final Button nextButton = new Button("Next");

    private int currentStep = 0;

    public StudentModule(AppData data, Runnable onChange) {

        this.data = data;
        this.onChange = onChange;

        setPadding(new Insets(16));

        configureFields();
        installValidationListeners();

        setTop(buildForm());
        setCenter(buildTable());

        refresh();
    }

    // ERROR LABEL STYLE

    private Label createErrorLabel() {

        Label label = new Label();

        label.setStyle(
                "-fx-text-fill: #d32f2f;" +
                "-fx-font-size: 11px;"
        );

        return label;
    }

    // CONFIGURE INPUT FIELDS

    private void configureFields() {

        nameField.setPromptText("Enter full name");
        emailField.setPromptText("Enter email address");

        branchComboBox.getItems().addAll(
                "CSE",
                "ECE",
                "EEE",
                "IT",
                "Mechanical",
                "Civil",
                "AI & DS",
                "AI & ML",
                "Other"
        );

        branchComboBox.setPromptText("Select your branch");
        branchComboBox.setMaxWidth(Double.MAX_VALUE);

        branchOtherField.setPromptText(
                "Specify your branch"
        );

        branchOtherField.setVisible(false);
        branchOtherField.setManaged(false);

        branchComboBox.valueProperty().addListener(
                (observable, oldValue, newValue) -> {

                    boolean isOther = "Other".equals(newValue);

                    branchOtherField.setVisible(isOther);
                    branchOtherField.setManaged(isOther);

                    branchError.setText("");
                    branchOtherError.setText("");

                    if (!isOther) {
                        branchOtherField.clear();
                    }
                }
        );

        cgpaField.setPromptText("CGPA (0-10)");
        backlogsField.setPromptText("Number of backlogs");

        skillsField.setPromptText(
                "Skills (Java, SQL, Communication)"
        );

        languagesField.setPromptText(
                "Programming languages (Java, Python)"
        );

        activitiesField.setPromptText(
                "Optional: Hackathons, Workshops, Coding competitions"
        );
    }

    // VALIDATION WHEN A FIELD LOSES FOCUS

    private void installValidationListeners() {

        watchField(nameField, this::validateName, nameError);
        watchField(emailField, this::validateEmail, emailError);

        watchField(
                branchComboBox,
                this::validateBranch,
                branchError
        );

        watchField(
                branchOtherField,
                this::validateOtherBranch,
                branchOtherError
        );

        watchField(cgpaField, this::validateCgpa, cgpaError);
        watchField(
                backlogsField,
                this::validateBacklogs,
                backlogsError
        );

        watchField(skillsField, this::validateSkills, skillsError);
        watchField(
                languagesField,
                this::validateLanguages,
                languagesError
        );

        watchField(
                activitiesField,
                this::validateActivities,
                activitiesError
        );
    }

    private void watchField(
            Control field,
            Supplier<String> validator,
            Label errorLabel) {

        field.focusedProperty().addListener(
                (observable, wasFocused, isFocused) -> {

                    if (!isFocused) {
                        setAndCheck(errorLabel, validator.get());
                    }
                }
        );
    }

    private boolean setAndCheck(
            Label errorLabel,
            String error) {

        errorLabel.setText(error == null ? "" : error);

        return error == null;
    }

    // BUILD REGISTRATION FORM

    private VBox buildForm() {

        Label heading = new Label("Register Student");
        heading.getStyleClass().add("section-title");

        backButton.setOnAction(event ->
                showStep(currentStep - 1)
        );

        nextButton.setOnAction(event ->
                handleNext()
        );

        Button clearButton = new Button("Clear");

        clearButton.setOnAction(event ->
                clearForm()
        );

        backButton.getStyleClass().add("secondary-button");
        nextButton.getStyleClass().add("primary-button");

        HBox actions = new HBox(
                10,
                backButton,
                nextButton,
                clearButton
        );

        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox form = new VBox(
                12,
                heading,
                stepLabel,
                stepContent,
                actions
        );

        form.getStyleClass().add("panel");
        form.setPadding(new Insets(16));

        showStep(0);

        return form;
    }

    // CREATE GRID

    private GridPane createStepGrid() {

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(12);

        ColumnConstraints labelColumn =
                new ColumnConstraints();

        labelColumn.setMinWidth(110);

        ColumnConstraints inputColumn =
                new ColumnConstraints();

        inputColumn.setHgrow(Priority.ALWAYS);
        inputColumn.setFillWidth(true);

        grid.getColumnConstraints().addAll(
                labelColumn,
                inputColumn
        );

        return grid;
    }

    private VBox fieldGroup(Node field, Label error) {

        VBox box = new VBox(4);

        box.getChildren().addAll(field, error);

        if (field instanceof Region region) {
            region.setMaxWidth(Double.MAX_VALUE);
        }

        return box;
    }

    private void addFieldRow(
            GridPane grid,
            String label,
            Node field,
            int row) {

        grid.add(new Label(label), 0, row);
        grid.add(field, 1, row);

        if (field instanceof Region region) {
            region.setMaxWidth(Double.MAX_VALUE);
        }

        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    // STEP 1: PERSONAL DETAILS

    private GridPane buildPersonalStep() {

        GridPane grid = createStepGrid();

        addFieldRow(
                grid,
                "Full Name",
                fieldGroup(nameField, nameError),
                0
        );

        addFieldRow(
                grid,
                "Email",
                fieldGroup(emailField, emailError),
                1
        );

        VBox branchGroup = new VBox(
                5,
                branchComboBox,
                branchError,
                branchOtherField,
                branchOtherError
        );

        addFieldRow(
                grid,
                "Branch",
                branchGroup,
                2
        );

        return grid;
    }

    // STEP 2: ACADEMIC DETAILS

    private GridPane buildAcademicStep() {

        GridPane grid = createStepGrid();

        addFieldRow(
                grid,
                "CGPA",
                fieldGroup(cgpaField, cgpaError),
                0
        );

        addFieldRow(
                grid,
                "Backlogs",
                fieldGroup(backlogsField, backlogsError),
                1
        );

        return grid;
    }

    // STEP 3: SKILLS AND ACTIVITIES

    private GridPane buildSkillsStep() {

        GridPane grid = createStepGrid();

        addFieldRow(
                grid,
                "Skills",
                fieldGroup(skillsField, skillsError),
                0
        );

        addFieldRow(
                grid,
                "Languages",
                fieldGroup(languagesField, languagesError),
                1
        );

        addFieldRow(
                grid,
                "Curricular Activities (Optional)",
                fieldGroup(activitiesField, activitiesError),
                2
        );

        return grid;
    }

    // NAVIGATION BETWEEN STEPS

    private void showStep(int step) {

        if (step < 0 || step > 2) {
            return;
        }

        currentStep = step;

        if (step == 0) {

            stepLabel.setText(
                    "Step 1 of 3 - Personal Details"
            );

            stepContent.getChildren().setAll(
                    buildPersonalStep()
            );

        } else if (step == 1) {

            stepLabel.setText(
                    "Step 2 of 3 - Academic Details"
            );

            stepContent.getChildren().setAll(
                    buildAcademicStep()
            );

        } else {

            stepLabel.setText(
                    "Step 3 of 3 - Skills and Additional Details"
            );

            stepContent.getChildren().setAll(
                    buildSkillsStep()
            );
        }

        backButton.setDisable(step == 0);

        nextButton.setText(
                step == 2 ? "Register Student" : "Next"
        );
    }

    // HANDLE NEXT BUTTON

    private void handleNext() {

        if (currentStep == 0) {

            if (validatePersonalDetails()) {
                showStep(1);
            }

        } else if (currentStep == 1) {

            if (validateAcademicDetails()) {
                showStep(2);
            }

        } else {

            registerStudent();
        }
    }

    // PERSONAL DETAILS VALIDATION

    private String validateName() {

        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            return "Name is required.";
        }

        if (!NAME_PATTERN.matcher(name).matches()) {
            return "Name must contain only letters and spaces.";
        }

        return null;
    }

    private String validateEmail() {

        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            return "Email is required.";
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Enter a valid email address.";
        }

        return null;
    }

    private String validateBranch() {

        if (branchComboBox.getValue() == null) {
            return "Please select your branch.";
        }

        return null;
    }

    private String validateOtherBranch() {

        if (!"Other".equals(branchComboBox.getValue())) {
            return null;
        }

        String branch = branchOtherField.getText().trim();

        if (branch.isEmpty()) {
            return "Please specify your branch.";
        }

        if (!NAME_PATTERN.matcher(branch).matches()) {
            return "Use letters and spaces only.";
        }

        return null;
    }

    private boolean validatePersonalDetails() {

        boolean valid = true;

        valid &= setAndCheck(nameError, validateName());
        valid &= setAndCheck(emailError, validateEmail());
        valid &= setAndCheck(branchError, validateBranch());
        valid &= setAndCheck(
                branchOtherError,
                validateOtherBranch()
        );

        return valid;
    }

    // ACADEMIC DETAILS VALIDATION

    private String validateCgpa() {

        String value = cgpaField.getText().trim();

        if (value.isEmpty()) {
            return "CGPA is required.";
        }

        try {

            double cgpa = Double.parseDouble(value);

            if (!Double.isFinite(cgpa) ||
                    cgpa < 0 || cgpa > 10) {

                return "CGPA must be between 0 and 10.";
            }

        } catch (NumberFormatException ex) {

            return "Enter a valid numerical CGPA.";
        }

        return null;
    }

    private String validateBacklogs() {

        String value = backlogsField.getText().trim();

        if (value.isEmpty()) {
            return "Backlogs field is required.";
        }

        try {

            int backlogs = Integer.parseInt(value);

            if (backlogs < 0) {
                return "Backlogs cannot be negative.";
            }

        } catch (NumberFormatException ex) {

            return "Enter a valid whole number.";
        }

        return null;
    }

    private boolean validateAcademicDetails() {

        boolean valid = true;

        valid &= setAndCheck(cgpaError, validateCgpa());

        valid &= setAndCheck(
                backlogsError,
                validateBacklogs()
        );

        return valid;
    }

    // SKILLS VALIDATION

    private String validateSkills() {

        String value = skillsField.getText().trim();

        if (value.isEmpty()) {
            return "Please enter your skills.";
        }

        List<String> values =
                UiUtils.parseCommaSeparated(value);

        if (values.isEmpty()) {
            return "Enter valid skills separated by commas.";
        }

        return null;
    }

    // LANGUAGES VALIDATION

    private String validateLanguages() {

        String value = languagesField.getText().trim();

        if (value.isEmpty()) {
            return "Please enter programming languages.";
        }

        List<String> values =
                UiUtils.parseCommaSeparated(value);

        if (values.isEmpty()) {
            return "Enter valid languages separated by commas.";
        }

        return null;
    }

    // CURRICULAR ACTIVITIES VALIDATION
    // IMPORTANT: THIS FIELD IS OPTIONAL

    private String validateActivities() {

        String value = activitiesField.getText().trim();

        // Blank curricular activities are allowed.
        if (value.isEmpty()) {
            return null;
        }

        List<String> values =
                UiUtils.parseCommaSeparated(value);

        if (values.isEmpty()) {
            return "Enter valid activities or leave blank.";
        }

        return null;
    }

    private boolean validateAdditionalDetails() {

        boolean valid = true;

        valid &= setAndCheck(skillsError, validateSkills());

        valid &= setAndCheck(
                languagesError,
                validateLanguages()
        );

        // Optional field: blank value is valid.
        valid &= setAndCheck(
                activitiesError,
                validateActivities()
        );

        return valid;
    }

    // REGISTER STUDENT

    private void registerStudent() {

        // Check all required details again before registration.

        if (!validatePersonalDetails()) {
            showStep(0);
            return;
        }

        if (!validateAcademicDetails()) {
            showStep(1);
            return;
        }

        if (!validateAdditionalDetails()) {
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        String branch;

        if ("Other".equals(branchComboBox.getValue())) {
            branch = branchOtherField.getText().trim();
        } else {
            branch = branchComboBox.getValue();
        }

        double cgpa = Double.parseDouble(
                cgpaField.getText().trim()
        );

        int backlogs = Integer.parseInt(
                backlogsField.getText().trim()
        );

        String id = "STU" + System.currentTimeMillis();

        List<String> skills =
                UiUtils.parseCommaSeparated(
                        skillsField.getText()
                );

        List<String> languages =
                UiUtils.parseCommaSeparated(
                        languagesField.getText()
                );

        // Curricular activities are optional.
        List<String> activities;

        if (activitiesField.getText().trim().isEmpty()) {

            activities = new ArrayList<>();

        } else {

            activities = UiUtils.parseCommaSeparated(
                    activitiesField.getText()
            );
        }

        Student student = new Student(
                id,
                name,
                email,
                branch,
                cgpa,
                backlogs,
                skills,
                languages,
                activities
        );

        data.getStudents().add(student);

        onChange.run();

        clearForm();

        UiUtils.info(
                "Student Added",
                "Student " + name +
                        " was registered successfully."
        );
    }

    // CLEAR FORM

    private void clearForm() {

        nameField.clear();
        emailField.clear();

        branchComboBox.setValue(null);
        branchOtherField.clear();

        cgpaField.clear();
        backlogsField.clear();

        skillsField.clear();
        languagesField.clear();
        activitiesField.clear();

        nameError.setText("");
        emailError.setText("");
        branchError.setText("");
        branchOtherError.setText("");

        cgpaError.setText("");
        backlogsError.setText("");

        skillsError.setText("");
        languagesError.setText("");
        activitiesError.setText("");

        showStep(0);
    }

    // REGISTERED STUDENTS TABLE

    private VBox buildTable() {

        TableColumn<Student, String> idCol =
                new TableColumn<>("Student ID");

        idCol.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        TableColumn<Student, String> nameCol =
                new TableColumn<>("Name");

        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );

        TableColumn<Student, String> branchCol =
                new TableColumn<>("Branch");

        branchCol.setCellValueFactory(
                new PropertyValueFactory<>("branch")
        );

        TableColumn<Student, Number> cgpaCol =
                new TableColumn<>("CGPA");

        cgpaCol.setCellValueFactory(
                new PropertyValueFactory<>("cgpa")
        );

        TableColumn<Student, Number> backlogCol =
                new TableColumn<>("Backlogs");

        backlogCol.setCellValueFactory(
                new PropertyValueFactory<>("backlogs")
        );

        TableColumn<Student, String> emailCol =
                new TableColumn<>("Email");

        emailCol.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );

        table.getColumns().addAll(
                idCol,
                nameCol,
                branchCol,
                cgpaCol,
                backlogCol,
                emailCol
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setPlaceholder(
                new Label("No students registered yet.")
        );

        Button deleteButton =
                new Button("Delete Selected Student");

        deleteButton.getStyleClass().add("danger-button");

        deleteButton.setOnAction(event -> {

            Student selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {

                UiUtils.info(
                        "Select a student",
                        "Choose a student from the table first."
                );

                return;
            }

            if (UiUtils.confirm(
                    "Delete student",
                    "Delete " + selected.getName() + "?"
            )) {

                data.getStudents().remove(selected);

                onChange.run();
            }
        });

        Label heading = new Label("Registered Students");
        heading.getStyleClass().add("section-title");

        VBox box = new VBox(
                10,
                heading,
                table,
                deleteButton
        );

        box.setPadding(new Insets(16, 0, 0, 0));

        VBox.setVgrow(table, Priority.ALWAYS);

        return box;
    }

    // REFRESH TABLE

    public void refresh() {

        table.setItems(
                FXCollections.observableArrayList(
                        data.getStudents()
                )
        );
    }
}
