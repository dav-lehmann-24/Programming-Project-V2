package dhbw.mosbach;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.util.List;
import java.util.Optional;

public class DialogHelper {
    public static Optional<String[]> showUserCreationDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Create User");
        dialog.setHeaderText("Enter username and password:");

        Label lblUsername = new Label("Username:");
        TextField txtUsername = new TextField();
        Label lblPassword = new Label("Password:");
        PasswordField txtPassword = new PasswordField();
        Label lblConfirm = new Label("Confirm Password:");
        PasswordField txtConfirm = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(lblUsername, 0, 0);
        grid.add(txtUsername, 1, 0);
        grid.add(lblPassword, 0, 1);
        grid.add(txtPassword, 1, 1);
        grid.add(lblConfirm, 0, 2);
        grid.add(txtConfirm, 1, 2);

        dialog.getDialogPane().setContent(grid);
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == createButtonType) {
                return new String[] {
                        txtUsername.getText(),
                        txtPassword.getText(),
                        txtConfirm.getText()
                };
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static Optional<String[]> showLoginDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Login");
        dialog.setHeaderText("Enter username and password:");

        Label lblUsername = new Label("Username:");
        TextField txtUsername = new TextField();
        Label lblPassword = new Label("Password:");
        PasswordField txtPassword = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(lblUsername, 0, 0);
        grid.add(txtUsername, 1, 0);
        grid.add(lblPassword, 0, 1);
        grid.add(txtPassword, 1, 1);

        dialog.getDialogPane().setContent(grid);
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == loginButtonType) {
                return new String[] {
                        txtUsername.getText(),
                        txtPassword.getText()
                };
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static Optional<String[]> showMonthCreationDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Create New Month");
        dialog.setHeaderText("Enter month name, starting balance, currency, and optional saving goal:");

        Label lblMonth = new Label("Month (e.g., April 2025):");
        TextField txtMonth = new TextField();
        Label lblBalance = new Label("Starting Balance:");
        TextField txtBalance = new TextField();
        Label lblCurrency = new Label("Currency (e.g., €):");
        TextField txtCurrency = new TextField();
        Label lblGoal = new Label("Saving Goal (optional):");
        TextField txtGoal = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(lblMonth, 0, 0); grid.add(txtMonth, 1, 0);
        grid.add(lblBalance, 0, 1); grid.add(txtBalance, 1, 1);
        grid.add(lblCurrency, 0, 2); grid.add(txtCurrency, 1, 2);
        grid.add(lblGoal, 0, 3); grid.add(txtGoal, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new String[] {
                        txtMonth.getText(),
                        txtBalance.getText(),
                        txtCurrency.getText(),
                        txtGoal.getText()
                };
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static Optional<String> showChoiceDialog(String title, String header, String contentLabel, List<String> choices) {
        if (choices == null || choices.isEmpty()) return Optional.empty();

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(contentLabel);

        return dialog.showAndWait();
    }

    public static Optional<ButtonType> showMonthSelectionModeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Month Planning");
        dialog.setHeaderText("Would you like to select an existing month or create a new one?");
        ButtonType btnSelect = new ButtonType("Select Existing", ButtonBar.ButtonData.YES);
        ButtonType btnCreate = new ButtonType("Create New", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().addAll(btnSelect, btnCreate, ButtonType.CANCEL);

        dialog.setResultConverter(button -> button);
        return dialog.showAndWait();
    }
}
