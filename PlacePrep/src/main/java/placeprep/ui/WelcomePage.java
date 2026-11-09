
package placeprep.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.net.URL;

public class WelcomePage {

    public Parent create(Runnable onGetStarted) {

        BorderPane root = new BorderPane();

        // Set the background image
        setBackgroundImage(root);

        // Header
        Label logo = new Label("P");

        logo.setStyle(
            "-fx-background-color: #38BDF8;" +
            "-fx-text-fill: #082F49;" +
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 10 17;"
        );

        Label appName = new Label("PlacePrep");

        appName.setStyle(
            "-fx-text-fill: #123B70;" +
            "-fx-font-size: 25px;" +
            "-fx-font-weight: bold;"
        );

        Label tagline = new Label("SMART PLACEMENT HUB");

        tagline.setStyle(
            "-fx-text-fill: #25638A;" +
            "-fx-font-size: 11px;"
        );

        VBox brand = new VBox(4, appName, tagline);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        Label rightText = new Label("YOUR CAREER PARTNER");

        rightText.setStyle(
            "-fx-text-fill: #123B70;" +
            "-fx-font-weight: bold;"
        );

        HBox header = new HBox(
            15, logo, brand, space, rightText
        );

        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(25, 40, 20, 40));

        root.setTop(header);

        // Welcome content
        Label welcome = new Label("WELCOME TO PLACEPREP");

        welcome.setStyle(
            "-fx-text-fill: #1677A5;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;"
        );

        Label heading = new Label(
            "Prepare. Practice.\nGet Placed."
        );

        heading.setStyle(
            "-fx-text-fill: #123B70;" +
            "-fx-font-size: 46px;" +
            "-fx-font-weight: bold;"
        );

        Label description = new Label(
            "Your career journey starts here!\n\n" +
            "Manage your student profile, improve your skills, " +
            "practice placement questions and discover companies " +
            "that match your eligibility."
        );

        description.setWrapText(true);
        description.setMaxWidth(650);

        description.setStyle(
            "-fx-text-fill: #315875;" +
            "-fx-font-size: 16px;"
        );

        Button startButton = new Button("Get Started  →");

        startButton.setStyle(
            "-fx-background-color: #7DD3FC;" +
            "-fx-text-fill: #082F49;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 14 30;" +
            "-fx-cursor: hand;"
        );

        startButton.setOnAction(
            event -> onGetStarted.run()
        );

        VBox hero = new VBox(
            20,
            welcome,
            heading,
            description,
            startButton
        );

        hero.setAlignment(Pos.CENTER_LEFT);
        hero.setPadding(new Insets(35));
        hero.setMaxWidth(950);

        hero.setStyle(
            "-fx-background-color: rgba(255,255,255,0.65);" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: rgba(255,255,255,0.8);" +
            "-fx-border-radius: 20;"
        );

        // Feature cards
        HBox features = new HBox(15);

        features.getChildren().addAll(

            featureCard(
                "01",
                "Student Profile",
                "Manage academic details and skills."
            ),

            featureCard(
                "02",
                "Practice Tests",
                "Improve aptitude and technical knowledge."
            ),

            featureCard(
                "03",
                "Company Eligibility",
                "Find companies matching your profile."
            ),

            featureCard(
                "04",
                "Performance Reports",
                "Track scores and preparation."
            )
        );

        VBox centerContent = new VBox(
            25,
            hero,
            features
        );

        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(
            new Insets(20, 35, 30, 35)
        );

        root.setCenter(centerContent);

        // Footer
        Label footer = new Label(
            "PREPARE YOURSELF TODAY FOR A BETTER TOMORROW"
        );

        footer.setStyle(
            "-fx-text-fill: #25638A;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );

        BorderPane.setAlignment(footer, Pos.CENTER);
        BorderPane.setMargin(footer, new Insets(15));

        root.setBottom(footer);

        return root;
    }

    // BACKGROUND IMAGE METHOD

    private void setBackgroundImage(BorderPane root) {

        URL imageUrl = getClass().getResource(
            "/placeprep/placeprep-background.jpeg"
        );

        if (imageUrl != null) {

            Image backgroundImage = new Image(
                imageUrl.toExternalForm()
            );

            BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                    100,
                    100,
                    true,
                    true,
                    false,
                    true
                )
            );

            root.setBackground(
                new Background(background)
            );

        } else {

            System.err.println(
                "Background image not found! Check the resources folder."
            );

            root.setStyle(
                "-fx-background-color: #DCEEFF;"
            );
        }
    }

    // FEATURE CARD METHOD

    private VBox featureCard(
            String number,
            String title,
            String description) {

        Label num = new Label(number);

        num.setStyle(
            "-fx-text-fill: #1677A5;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;"
        );

        Label heading = new Label(title);

        heading.setWrapText(true);

        heading.setStyle(
            "-fx-text-fill: #123B70;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;"
        );

        Label details = new Label(description);

        details.setWrapText(true);

        details.setStyle(
            "-fx-text-fill: #315875;" +
            "-fx-font-size: 12px;"
        );

        VBox card = new VBox(
            10,
            num,
            heading,
            details
        );

        card.setPadding(new Insets(15));
        card.setPrefWidth(210);
        card.setMaxWidth(Double.MAX_VALUE);

        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.70);" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: rgba(255,255,255,0.85);" +
            "-fx-border-radius: 15;"
        );

        HBox.setHgrow(card, Priority.ALWAYS);

        return card;
    }
}
