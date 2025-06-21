package dhbw.mosbach;

import javafx.scene.control.Alert;

public class UIUtils {
    public static String formatCurrency(double amount, String currency) {
        return String.format("%.2f%s", amount, currency);
    }

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
