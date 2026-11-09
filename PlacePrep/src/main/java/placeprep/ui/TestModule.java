package placeprep.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import placeprep.model.AppData;
import placeprep.model.Question;
import placeprep.model.Student;
import placeprep.model.TestResult;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestModule extends TabPane {
    private final AppData data;
    private final Runnable onChange;

    private final ComboBox<Student> studentBox = new ComboBox<>();
    private final ComboBox<String> categoryBox = new ComboBox<>();
    private final Label timerLabel = new Label("Time: 01:00");
    private final VBox questionArea = new VBox(12);
    private final Button startButton = new Button("Start Test");
    private final Button submitButton = new Button("Submit Test");

    private final ComboBox<String> newCategory = new ComboBox<>();
    private final TextField promptField = new TextField();
    private final TextField optionAField = new TextField();
    private final TextField optionBField = new TextField();
    private final TextField optionCField = new TextField();
    private final TextField optionDField = new TextField();
    private final ComboBox<String> correctOption = new ComboBox<>();
    private final TableView<Question> questionTable = new TableView<>();

    private List<Question> activeQuestions = new ArrayList<>();
    private final Map<Question, ToggleGroup> answerGroups = new LinkedHashMap<>();
    private ScheduledExecutorService scheduler;
    private int remainingSeconds;
    private Student activeStudent;
    private String activeCategory;

    public TestModule(AppData data, Runnable onChange) {
        this.data = data;
        this.onChange = onChange;
        Tab testTab = new Tab("Take a Test", buildTestTab());
        Tab bankTab = new Tab("Manage Questions", buildQuestionBankTab());
        testTab.setClosable(false);
        bankTab.setClosable(false);
        getTabs().addAll(testTab, bankTab);
        refresh();
    }

    private VBox buildTestTab() {
        studentBox.setPromptText("Select student");
        studentBox.setPrefWidth(230);
        categoryBox.setItems(FXCollections.observableArrayList("Aptitude", "Technical"));
        categoryBox.setValue("Aptitude");

        timerLabel.getStyleClass().add("timer-label");
        startButton.getStyleClass().add("primary-button");
        submitButton.getStyleClass().add("success-button");
        submitButton.setDisable(true);

        startButton.setOnAction(event -> startTest());
        submitButton.setOnAction(event -> submitTest(false));

        HBox controls = new HBox(12,
                new Label("Student:"), studentBox,
                new Label("Test:"), categoryBox,
                startButton, submitButton, timerLabel);
        controls.setPadding(new Insets(12));
        controls.getStyleClass().add("panel");

        questionArea.setPadding(new Insets(12));
        ScrollPane scroll = new ScrollPane(questionArea);
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Label heading = new Label("Timed Practice Test");
        heading.getStyleClass().add("section-title");
        Label note = new Label("Each test uses up to 5 questions and has a 60-second time limit.");
        note.getStyleClass().add("muted-text");

        VBox root = new VBox(12, heading, note, controls, scroll);
        root.setPadding(new Insets(16));
        return root;
    }

    private VBox buildQuestionBankTab() {
        Label heading = new Label("Question Bank Management");
        heading.getStyleClass().add("section-title");

        newCategory.setItems(FXCollections.observableArrayList("Aptitude", "Technical"));
        newCategory.setValue("Aptitude");
        correctOption.setItems(FXCollections.observableArrayList("A", "B", "C", "D"));
        correctOption.setValue("A");

        promptField.setPromptText("Enter the question");
        optionAField.setPromptText("Option A");
        optionBField.setPromptText("Option B");
        optionCField.setPromptText("Option C");
        optionDField.setPromptText("Option D");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.add(new Label("Category"), 0, 0);
        form.add(newCategory, 1, 0);
        form.add(new Label("Correct option"), 2, 0);
        form.add(correctOption, 3, 0);
        form.add(new Label("Question"), 0, 1);
        form.add(promptField, 1, 1, 3, 1);
        form.add(new Label("Option A"), 0, 2);
        form.add(optionAField, 1, 2);
        form.add(new Label("Option B"), 2, 2);
        form.add(optionBField, 3, 2);
        form.add(new Label("Option C"), 0, 3);
        form.add(optionCField, 1, 3);
        form.add(new Label("Option D"), 2, 3);
        form.add(optionDField, 3, 3);

        Button addQuestionButton = new Button("Add Question");
        addQuestionButton.getStyleClass().add("primary-button");
        addQuestionButton.setOnAction(event -> addQuestion());

        VBox formBox = new VBox(10, form, addQuestionButton);
        formBox.getStyleClass().add("panel");
        formBox.setPadding(new Insets(14));

        TableColumn<Question, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<Question, String> promptCol = new TableColumn<>("Question");
        promptCol.setCellValueFactory(new PropertyValueFactory<>("prompt"));
        questionTable.getColumns().addAll(categoryCol, promptCol);
        questionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        questionTable.setPlaceholder(new Label("No questions available."));

        Button deleteButton = new Button("Delete Selected Question");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(event -> {
            Question selected = questionTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.info("Select a question", "Choose a question from the table first.");
                return;
            }
            if (UiUtils.confirm("Delete question", "Delete the selected question?")) {
                data.getQuestions().remove(selected);
                onChange.run();
            }
        });

        VBox root = new VBox(12, heading, formBox, questionTable, deleteButton);
        root.setPadding(new Insets(16));
        VBox.setVgrow(questionTable, Priority.ALWAYS);
        return root;
    }

    private void addQuestion() {
        String prompt = promptField.getText().trim();
        List<String> options = List.of(optionAField.getText().trim(),
                optionBField.getText().trim(), optionCField.getText().trim(),
                optionDField.getText().trim());

        if (prompt.isEmpty() || options.stream().anyMatch(String::isEmpty)) {
            UiUtils.error("Incomplete question", "Enter the question and all four options.");
            return;
        }

        int correctIndex = correctOption.getSelectionModel().getSelectedIndex();
        String id = "Q" + System.currentTimeMillis();
        data.getQuestions().add(new Question(id, newCategory.getValue(),
                prompt, options, correctIndex));

        promptField.clear();
        optionAField.clear();
        optionBField.clear();
        optionCField.clear();
        optionDField.clear();
        onChange.run();
        UiUtils.info("Question added", "The question was added to the question bank.");
    }

    private void startTest() {
        Student selectedStudent = studentBox.getValue();
        String selectedCategory = categoryBox.getValue();

        if (selectedStudent == null) {
            UiUtils.error("Select a student", "Register and select a student before starting a test.");
            return;
        }

        List<Question> pool = data.getQuestions().stream()
                .filter(question -> question.getCategory().equalsIgnoreCase(selectedCategory))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        if (pool.isEmpty()) {
            UiUtils.error("No questions", "There are no questions in this category. Add questions first.");
            return;
        }

        Collections.shuffle(pool);
        activeQuestions = new ArrayList<>(pool.subList(0, Math.min(5, pool.size())));
        activeStudent = selectedStudent;
        activeCategory = selectedCategory;
        answerGroups.clear();
        questionArea.getChildren().clear();

        for (int i = 0; i < activeQuestions.size(); i++) {
            Question question = activeQuestions.get(i);
            VBox card = new VBox(8);
            card.getStyleClass().add("question-card");

            Label prompt = new Label((i + 1) + ". " + question.getPrompt());
            prompt.setWrapText(true);
            prompt.getStyleClass().add("question-text");

            ToggleGroup group = new ToggleGroup();
            answerGroups.put(question, group);

            VBox choices = new VBox(6);
            List<String> options = question.getOptions();
            for (int j = 0; j < options.size(); j++) {
                RadioButton radio = new RadioButton(options.get(j));
                radio.setToggleGroup(group);
                radio.setUserData(j);
                choices.getChildren().add(radio);
            }

            card.getChildren().addAll(prompt, choices);
            questionArea.getChildren().add(card);
        }

        remainingSeconds = 60;
        updateTimerLabel();
        startButton.setDisable(true);
        submitButton.setDisable(false);
        studentBox.setDisable(true);
        categoryBox.setDisable(true);

        scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "PlacePrep-Test-Timer");
            thread.setDaemon(true);
            return thread;
        });

        scheduler.scheduleAtFixedRate(() -> {
            remainingSeconds--;
            Platform.runLater(() -> {
                updateTimerLabel();
                if (remainingSeconds <= 0 && !activeQuestions.isEmpty()) {
                    submitTest(true);
                }
            });
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void updateTimerLabel() {
        int minutes = Math.max(0, remainingSeconds) / 60;
        int seconds = Math.max(0, remainingSeconds) % 60;
        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }

    private void submitTest(boolean automatic) {
        if (activeQuestions.isEmpty() || activeStudent == null) {
            return;
        }

        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }

        int score = 0;
        for (Question question : activeQuestions) {
            ToggleGroup group = answerGroups.get(question);
            Toggle selected = group == null ? null : group.getSelectedToggle();
            if (selected != null && selected.getUserData() instanceof Integer selectedIndex
                    && selectedIndex == question.getCorrectIndex()) {
                score++;
            }
        }

        int total = activeQuestions.size();
        data.getResults().add(new TestResult(activeStudent.getId(),
                activeStudent.getName(), activeCategory, score, total, LocalDateTime.now()));

        String studentName = activeStudent.getName();
        activeQuestions = new ArrayList<>();
        activeStudent = null;
        answerGroups.clear();

        startButton.setDisable(false);
        submitButton.setDisable(true);
        studentBox.setDisable(false);
        categoryBox.setDisable(false);
        timerLabel.setText(automatic ? "Time: 00:00" : "Test submitted");

        onChange.run();
        UiUtils.info("Test completed",
                studentName + " scored " + score + " out of " + total
                        + (automatic ? ". Time expired; the test was submitted automatically." : "."));
    }

    public void refresh() {
        studentBox.setItems(FXCollections.observableArrayList(data.getStudents()));
        questionTable.setItems(FXCollections.observableArrayList(data.getQuestions()));
    }
}
