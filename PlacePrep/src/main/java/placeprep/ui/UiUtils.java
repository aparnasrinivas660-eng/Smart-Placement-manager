package placeprep.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Arrays;
import java.util.List;

public final class UiUtils {
    private UiUtils() { }

    public static void info(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void error(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public static List<String> parseCommaSeparated(String input) {
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .distinct()
                .toList();
    }
}
