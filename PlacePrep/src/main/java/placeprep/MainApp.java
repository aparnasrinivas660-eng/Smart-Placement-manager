
package placeprep;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import placeprep.model.AppData;
import placeprep.service.QuestionBank;
import placeprep.storage.DataStore;
import placeprep.ui.*;

import java.io.IOException;

public class MainApp extends Application {

    private AppData data;

    private StudentModule studentModule;
    private TestModule testModule;
    private RecruitmentModule recruitmentModule;
    private ReportModule reportModule;

    @Override
    public void start(Stage stage) {

        try {
            data = DataStore.load();
            QuestionBank.seedIfEmpty(data);
            DataStore.save(data);

        } catch (IOException | ClassNotFoundException ex) {
            showStartupError(ex);
            return;
        }

        studentModule = new StudentModule(data, this::saveAndRefresh);
        testModule = new TestModule(data, this::saveAndRefresh);
        recruitmentModule = new RecruitmentModule(data, this::saveAndRefresh);
        reportModule = new ReportModule(data);

        stage.setTitle("PlacePrep - Smart Placement Hub");
        stage.setMinWidth(980);
        stage.setMinHeight(680);

        stage.setOnCloseRequest(event -> {
            try {
                DataStore.save(data);
            } catch (IOException ex) {
                new Alert(
                    Alert.AlertType.ERROR,
                    "Could not save data: " + ex.getMessage()
                ).showAndWait();
            }
        });

        // Show Welcome Page first
        WelcomePage welcomePage = new WelcomePage();

        Scene welcomeScene = new Scene(
            welcomePage.create(() -> showDashboard(stage)),
            1180,
            780
        );

        stage.setScene(welcomeScene);
        stage.show();
    }

    // Open Main Dashboard
    private void showDashboard(Stage stage) {

        TabPane tabs = new TabPane();

        tabs.getTabs().addAll(
            createTab("Students", studentModule),
            createTab("Practice Tests", testModule),
            createTab("Companies & Eligibility", recruitmentModule),
            createTab("Reports", reportModule)
        );

        Label title = new Label("PlacePrep");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label(
            "Smart College Placement Preparation & Recruitment Manager"
        );

        subtitle.getStyleClass().add("app-subtitle");

        VBox header = new VBox(4, title, subtitle);
        header.getStyleClass().add("top-header");
        header.setPadding(new Insets(18, 22, 18, 22));

        BorderPane root = new BorderPane();

        // Apply common background image
        applyBackground(root);

        root.setTop(header);
        root.setCenter(tabs);

        // Make tab area transparent to show background
        tabs.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root, 1180, 780);

        var css = getClass().getResource("/placeprep/style.css");

        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setScene(scene);
        stage.show();
    }

    // Set the same background image used by WelcomePage
    private void applyBackground(BorderPane root) {

        var imageUrl = getClass().getResource(
            "/placeprep/placeprep-background.jpeg"
        );

        if (imageUrl != null) {

            javafx.scene.image.Image image =
                new javafx.scene.image.Image(
                    imageUrl.toExternalForm()
                );

            javafx.scene.layout.BackgroundImage background =
                new javafx.scene.layout.BackgroundImage(
                    image,
                    javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
                    javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
                    javafx.scene.layout.BackgroundPosition.CENTER,
                    new javafx.scene.layout.BackgroundSize(
                        100,
                        100,
                        true,
                        true,
                        false,
                        true
                    )
                );

            root.setBackground(
                new javafx.scene.layout.Background(background)
            );

        } else {

            System.err.println(
                "Background image not found. Check the resources folder."
            );

            root.setStyle(
                "-fx-background-color: #DCEEFF;"
            );
        }
    }

    // Create Dashboard Tabs
    private Tab createTab(
            String title,
            javafx.scene.Node content) {

        Tab tab = new Tab(title, content);
        tab.setClosable(false);

        return tab;
    }

    // Save Data and Refresh
    private void saveAndRefresh() {

        try {
            DataStore.save(data);

        } catch (IOException ex) {

            new Alert(
                Alert.AlertType.ERROR,
                "Could not save application data: " + ex.getMessage()
            ).showAndWait();
        }

        refreshAll();
    }

    // Refresh All Modules
    private void refreshAll() {

        if (studentModule != null) {
            studentModule.refresh();
        }

        if (testModule != null) {
            testModule.refresh();
        }

        if (recruitmentModule != null) {
            recruitmentModule.refresh();
        }

        if (reportModule != null) {
            reportModule.refresh();
        }
    }

    // Startup Error
    private void showStartupError(Exception ex) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("PlacePrep startup error");

        alert.setHeaderText(
            "The saved data could not be loaded."
        );

        alert.setContentText(ex.getMessage());

        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
