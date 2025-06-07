package dhbw.mosbach;

// Import libraries
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;

public class MainFX extends Application {
    private UserLogic logic;
    private User currentUser;
    private Label lblWelcomeMessage;
    private Label lblCurrentUser;
    private Label lblCurrentBalance;
    private Label lblSavingGoal;
    private Label lblStartingBalance;
    private Label lblMoneyRemaining;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        logic = new UserLogic();

        primaryStage.setTitle("CashCompass");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: lightgray;");

        Label lblTitle = new Label("Welcome to CashCompass!");
        lblTitle.setStyle("-fx-text-fill: red; -fx-font-size: 36; -fx-alignment: center; -fx-font-weight: bold;");

        lblWelcomeMessage = new Label("");
        lblWelcomeMessage.setStyle("-fx-text-fill: green; -fx-font-size: 18; -fx-font-weight: bold;");

        lblCurrentUser = new Label("Current user: -");
        lblCurrentBalance = new Label("Balance: -");
        lblSavingGoal = new Label("Saving goal: -");
        lblStartingBalance = new Label("Starting balance: -");
        lblMoneyRemaining = new Label("Money remaining: -");

        Button btnAddIncome = new Button("Add Income");
        btnAddIncome.setOnAction(e -> addIncome());

        Button btnAddExpense = new Button("Add Expense");
        btnAddExpense.setOnAction(e -> addExpense());

        Button btnShowIncomes = new Button("Show Incomes");
        btnShowIncomes.setOnAction(e -> showIncomes());

        Button btnShowExpenses = new Button("Show Expenses");
        btnShowExpenses.setOnAction(e -> showExpenses());

        Button btnSetGoal = new Button("Set Goal");
        btnSetGoal.setOnAction(e -> setGoal());

        Button btnShowPieCharts = new Button("Show Pie Charts");
        btnShowPieCharts.setOnAction(e -> showPieChart());

        Button btnSelectUser = new Button("Select User");
        btnSelectUser.setOnAction(e -> selectUser());

        Button btnCreateUser = new Button("Create User");
        btnCreateUser.setOnAction(e -> createUser());

        root.getChildren().addAll(
                lblTitle,
                lblWelcomeMessage,
                lblCurrentUser,
                lblCurrentBalance,
                lblSavingGoal,
                lblStartingBalance,
                lblMoneyRemaining,
                btnCreateUser,
                btnSelectUser,
                btnAddIncome,
                btnAddExpense,
                btnShowIncomes,
                btnShowExpenses,
                btnSetGoal,
                btnShowPieCharts
        );

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createUser() {
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

        dialog.showAndWait().ifPresent(result -> {
            String username = result[0];
            String password = result[1];
            String confirmPassword = result[2];

            if (!password.equals(confirmPassword)) {
                showAlert("Password Mismatch", "Passwords do not match.");
            }
            else {
                logic.addUser(username, 1000, password);
                currentUser = logic.getUser(username);
                lblWelcomeMessage.setText("Welcome back, " + username + "!");
                updateLabels();
            }
        });
    }

    private void selectUser() {
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

        dialog.showAndWait().ifPresent(result -> {
            String username = result[0];
            String password = result[1];
            User user = logic.getUser(username);
            if (user == null) {
                showAlert("User Not Found", "No user found with that name.");
            }
            else if (!user.checkPassword(password)) {
                showAlert("Invalid Password", "Password is incorrect.");
            }
            else {
                currentUser = user;
                lblWelcomeMessage.setText("Welcome back, " + username + "!");
                updateLabels();
            }
        });
    }

    private void addIncome() {
        if (currentUser == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Income");
        dialog.setHeaderText("Enter category, amount, and description (comma separated):");
        dialog.setContentText("Input:");
        dialog.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",");
            if (parts.length == 3) {
                logic.addIncomeToUser(currentUser, parts[0].trim(), Double.parseDouble(parts[1].trim()), parts[2].trim());
                updateLabels();
            }
        });
    }

    private void addExpense() {
        if (currentUser == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Expense");
        dialog.setHeaderText("Enter category, amount, and description (comma separated):");
        dialog.setContentText("Input:");
        dialog.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",");
            if (parts.length == 3) {
                logic.addExpenseToUser(currentUser, parts[0].trim(), Double.parseDouble(parts[1].trim()), parts[2].trim());
                updateLabels();
            }
        });
    }

    private void setGoal() {
        if (currentUser == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set Saving Goal");
        dialog.setHeaderText("Enter new saving goal:");
        dialog.setContentText("Goal:");
        dialog.showAndWait().ifPresent(input -> {
            currentUser.setSavingGoal(Double.parseDouble(input));
            logic.saveUsers();
            updateLabels();
        });
    }

    private void showIncomes() {
        if (currentUser == null) return;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Incomes");
        alert.setHeaderText("List of incomes:");
        StringBuilder sb = new StringBuilder();
        for (Income i : currentUser.getIncomes()) {
            sb.append(i.getCategory()).append(" - ").append(i.getAmount()).append(" - ").append(i.getDescription()).append("\n");
        }
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    private void showExpenses() {
        if (currentUser == null) return;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Expenses");
        alert.setHeaderText("List of expenses:");
        StringBuilder sb = new StringBuilder();
        for (Expense e : currentUser.getExpenses()) {
            sb.append(e.getCategory()).append(" - ").append(e.getAmount()).append(" - ").append(e.getDescription()).append("\n");
        }
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    private void showPieChart() {
        if (currentUser == null || currentUser.getExpenses().isEmpty()) return;
        Stage chartStage = new Stage();
        chartStage.setTitle("Expense Breakdown");

        PieChart pieChart = new PieChart();
        double total = currentUser.getTotalExpenses();
        currentUser.getExpenses().stream()
                .collect(java.util.stream.Collectors.groupingBy(Expense::getCategory, java.util.stream.Collectors.summingDouble(Expense::getAmount)))
                .forEach((category, sum) -> {
                    double percent = (sum / total) * 100;
                    pieChart.getData().add(new PieChart.Data(category + String.format(" (%.2f%%)", percent), sum));
                });

        VBox vbox = new VBox(pieChart);
        Scene scene = new Scene(vbox, 500, 400);
        chartStage.setScene(scene);
        chartStage.show();
    }

    private void updateLabels() {
        lblCurrentUser.setText("Current user: " + currentUser.getName());
        lblCurrentBalance.setText("Balance: " + currentUser.getCurrentBalance());
        lblSavingGoal.setText("Saving goal: " + currentUser.savingGoal);
        lblStartingBalance.setText("Starting balance: " + currentUser.startingBalance);
        double remaining = currentUser.savingGoal - currentUser.getCurrentBalance();
        lblMoneyRemaining.setText("Money remaining: " + (remaining > 0 ? remaining : 0));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}