package dhbw.mosbach;

// Import libraries
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class MainFX extends Application {
    private UserLogic logic;
    private User currentUser;
    private VBox userPanel;
    private Label lblWelcomeMessage;
    private Label lblCurrentMonth;
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
        lblTitle.setStyle("-fx-text-fill: red; -fx-font-size: 36; -fx-font-weight: bold;");

        lblWelcomeMessage = new Label("");
        lblWelcomeMessage.setStyle("-fx-text-fill: green; -fx-font-size: 18; -fx-font-weight: bold;");

        lblCurrentMonth = new Label("");
        lblCurrentMonth.setStyle("-fx-text-fill: black; -fx-font-size: 16; -fx-font-weight: bold;");

        lblCurrentBalance = new Label("Current balance: -");
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

        Button btnDeleteIncome = new Button("Delete Income");
        btnDeleteIncome.setOnAction(e -> deleteIncome());

        Button btnDeleteExpense = new Button("Delete Expense");
        btnDeleteExpense.setOnAction(e -> deleteExpense());

        Button btnSetGoal = new Button("Set Goal");
        btnSetGoal.setOnAction(e -> setGoal());

        Button btnShowPieCharts = new Button("Show Pie Charts");
        btnShowPieCharts.setOnAction(e -> showPieChart());

        Button btnSelectUser = new Button("Select User");
        btnSelectUser.setOnAction(e -> selectUser());

        Button btnCreateUser = new Button("Create User");
        btnCreateUser.setOnAction(e -> createUser());

        userPanel = new VBox(10);
        userPanel.getChildren().addAll(
                lblWelcomeMessage,
                lblCurrentMonth,
                lblStartingBalance,
                lblCurrentBalance,
                lblSavingGoal,
                lblMoneyRemaining,
                btnAddIncome,
                btnAddExpense,
                btnShowIncomes,
                btnShowExpenses,
                btnDeleteIncome,
                btnDeleteExpense,
                btnSetGoal,
                btnShowPieCharts
        );
        userPanel.setVisible(false);

        root.getChildren().addAll(
                lblTitle,
                btnCreateUser,
                btnSelectUser,
                userPanel
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
                logic.addUser(username, password);
                currentUser = logic.getUser(username);
                createMonthDialog();
                finalizeLogin(username);
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
                selectOrCreateMonth();
                finalizeLogin(username);
            }
        });
    }

    private void createMonthDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Create New Month");
        dialog.setHeaderText("Enter month name, starting balance, currency, and optional saving goal:");

        Label lblMonth = new Label("Month (e.g., July 2025):");
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

        dialog.showAndWait().ifPresent(data -> {
            try {
                String month = data[0].trim();
                double balance = Double.parseDouble(data[1].trim());
                String currency = data[2].trim();
                double goal = data[3].isEmpty() ? 0.0 : Double.parseDouble(data[3].trim());

                currentUser.selectMonth(month, balance, currency, goal);
                logic.saveUsers();

                lblCurrentMonth.setText("Planning for: " + month);
                lblWelcomeMessage.setText("Welcome back, " + currentUser.getName() + "!");
                userPanel.setVisible(true);
                updateLabels();
            } catch (Exception e) {
                showAlert("Invalid Input", "Please enter valid numbers for balance and goal.");
                createMonthDialog(); // Retry
            }
        });
    }

    private void selectOrCreateMonth() {
        Dialog<ButtonType> choiceDialog = new Dialog<>();
        choiceDialog.setTitle("Month Planning");
        choiceDialog.setHeaderText("Would you like to select an existing month or create a new one?");
        ButtonType btnSelect = new ButtonType("Select Existing", ButtonBar.ButtonData.YES);
        ButtonType btnCreate = new ButtonType("Create New", ButtonBar.ButtonData.NO);
        choiceDialog.getDialogPane().getButtonTypes().addAll(btnSelect, btnCreate, ButtonType.CANCEL);

        choiceDialog.setResultConverter(button -> button);

        Optional<ButtonType> result = choiceDialog.showAndWait();

        if (result.isPresent()) {
            if (result.get() == btnSelect) {
                // --- SELECT EXISTING ---
                List<String> existingMonths = new ArrayList<>(currentUser.getMonthDataMap().keySet());

                if (existingMonths.isEmpty()) {
                    showAlert("No Months Found", "No existing months available. Please create one.");
                    selectOrCreateMonth();
                    return;
                }

                ChoiceDialog<String> monthChoice = new ChoiceDialog<>(existingMonths.get(0), existingMonths);
                monthChoice.setTitle("Select Month");
                monthChoice.setHeaderText("Choose a month to continue planning:");
                monthChoice.setContentText("Months:");

                monthChoice.showAndWait().ifPresent(month -> {
                    currentUser.selectMonth(month);
                    lblCurrentMonth.setText("Planning for: " + month);
                    lblWelcomeMessage.setText("Welcome back, " + currentUser.getName() + "!");
                    updateLabels();
                });

            }
            else if (result.get() == btnCreate) {
                // --- CREATE NEW ---
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

                dialog.showAndWait().ifPresent(data -> {
                    try {
                        String month = data[0].trim();
                        double balance = Double.parseDouble(data[1].trim());
                        String currency = data[2].trim();
                        double goal = data[3].isEmpty() ? 0.0 : Double.parseDouble(data[3].trim());

                        currentUser.selectMonth(month, balance, currency, goal);
                        logic.saveUsers();
                        lblCurrentMonth.setText("Planning for: " + month);
                        lblWelcomeMessage.setText("Welcome back, " + currentUser.getName() + "!");
                        updateLabels();
                    }
                    catch (Exception e) {
                        showAlert("Invalid Input", "Please enter valid numbers for balance and goal.");
                    }
                });
            }
        }
    }

    private void finalizeLogin(String username) {
        userPanel.setVisible(true);
        lblWelcomeMessage.setText("Welcome back, " + currentUser.getName() + "!");
        updateLabels();
    }

    private void addIncome() {
        if (currentUser == null) return;

        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Add Income");
        dialog.setHeaderText("Enter income details:");

        Label lblAmount = new Label("Amount:");
        TextField txtAmount = new TextField();

        Label lblDescription = new Label("Description:");
        TextField txtDescription = new TextField();

        Label lblDate = new Label("Date:");
        TextField txtDate = new TextField(java.time.LocalDate.now().toString());

        Label lblCategory = new Label("Category:");
        ComboBox<String> cmbCategory = new ComboBox<>();
        cmbCategory.getItems().addAll(Income.getCategories());
        cmbCategory.getSelectionModel().selectFirst();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(lblAmount, 0, 0); grid.add(txtAmount, 1, 0);
        grid.add(lblDescription, 0, 1); grid.add(txtDescription, 1, 1);
        grid.add(lblDate, 0, 2); grid.add(txtDate, 1, 2);
        grid.add(lblCategory, 0, 3); grid.add(cmbCategory, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new String[]{
                        txtAmount.getText().trim(),
                        txtDescription.getText().trim(),
                        txtDate.getText().trim(),
                        cmbCategory.getValue()
                };
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            try {
                double amount = Double.parseDouble(result[0]);
                String description = result[1];
                String date = result[2];
                String category = result[3];

                currentUser.addIncome(category, amount, description, date);
                logic.saveUsers();
                updateLabels();
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number for the amount.");
            }
        });
    }

    private void addExpense() {
        if (currentUser == null) return;

        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Add Expense");
        dialog.setHeaderText("Enter expense details:");

        Label lblAmount = new Label("Amount:");
        TextField txtAmount = new TextField();

        Label lblDescription = new Label("Description:");
        TextField txtDescription = new TextField();

        Label lblDate = new Label("Date:");
        TextField txtDate = new TextField(java.time.LocalDate.now().toString());

        Label lblCategory = new Label("Category:");
        ComboBox<String> cmbCategory = new ComboBox<>();
        cmbCategory.getItems().addAll(Expense.getCategories()); // Make sure this is static
        cmbCategory.getSelectionModel().selectFirst();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(lblAmount, 0, 0); grid.add(txtAmount, 1, 0);
        grid.add(lblDescription, 0, 1); grid.add(txtDescription, 1, 1);
        grid.add(lblDate, 0, 2); grid.add(txtDate, 1, 2);
        grid.add(lblCategory, 0, 3); grid.add(cmbCategory, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new String[]{
                        txtAmount.getText().trim(),
                        txtDescription.getText().trim(),
                        txtDate.getText().trim(),
                        cmbCategory.getValue()
                };
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            try {
                double amount = Double.parseDouble(result[0]);
                String description = result[1];
                String date = result[2];
                String category = result[3];

                currentUser.addExpense(category, amount, description, date);
                logic.saveUsers();
                updateLabels();
            }
            catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number for the amount.");
            }
        });
    }

    private void deleteIncome() {
        if (currentUser == null) return;

        List<Income> incomes = currentUser.getIncomes();
        if (incomes.isEmpty()) {
            showAlert("No Incomes", "There are no incomes to delete.");
            return;
        }

        List<String> descriptions = incomes.stream()
                .map(Income::getDescription)
                .toList();

        ChoiceDialog<String> dialog = new ChoiceDialog<>(descriptions.get(0), descriptions);
        dialog.setTitle("Delete Income");
        dialog.setHeaderText("Select an income description to delete:");
        dialog.setContentText("Description:");

        dialog.showAndWait().ifPresent(selectedDesc -> {
            boolean removed = currentUser.removeIncome(selectedDesc);
            if (removed) {
                showAlert("Success", "Income removed successfully.");
                logic.saveUsers();
                updateLabels();
            }
            else {
                showAlert("Not Found", "Could not find matching income.");
            }
        });
    }

    private void deleteExpense() {
        if (currentUser == null) return;

        List<Expense> expenses = currentUser.getExpenses();
        if (expenses.isEmpty()) {
            showAlert("No Expenses", "There are no expenses to delete.");
            return;
        }

        List<String> descriptions = expenses.stream()
                .map(Expense::getDescription)
                .toList();

        ChoiceDialog<String> dialog = new ChoiceDialog<>(descriptions.get(0), descriptions);
        dialog.setTitle("Delete Expense");
        dialog.setHeaderText("Select an expense description to delete:");
        dialog.setContentText("Description:");

        dialog.showAndWait().ifPresent(selectedDesc -> {
            boolean removed = currentUser.removeExpense(selectedDesc);
            if (removed) {
                showAlert("Success", "Expense removed successfully.");
                logic.saveUsers();
                updateLabels();
            }
            else {
                showAlert("Not Found", "Could not find matching expense.");
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
        MonthData data = currentUser.getCurrentMonthData();
        if (data == null) return;

        String currency = data.getCurrency();
        double startingBalance = data.getStartingBalance();
        double currentBalance = data.getCurrentBalance();
        double savingGoal = data.getSavingGoal();

        lblStartingBalance.setText("Starting balance: " + formatCurrency(startingBalance, currency));
        lblCurrentBalance.setText("Current balance: " + formatCurrency(currentBalance, currency));
        lblSavingGoal.setText("Saving goal: " + formatCurrency(savingGoal, currency));
        double remainingBalance = Math.max(0, currentBalance - savingGoal);
        lblMoneyRemaining.setText("Money remaining: " + formatCurrency(remainingBalance, currency));
    }

    private String formatCurrency(double amount, String currency) {
        return String.format("%.2f%s", amount, currency);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}