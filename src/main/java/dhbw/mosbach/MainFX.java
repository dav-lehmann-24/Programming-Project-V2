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

        Button btnShowIncomeChart = new Button("Show Income Chart");
        btnShowIncomeChart.setOnAction(e -> showIncomeChart());

        Button btnShowExpenseChart = new Button("Show Expense Chart");
        btnShowExpenseChart.setOnAction(e -> showExpenseChart());

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
                btnShowIncomeChart,
                btnShowExpenseChart
        );
        userPanel.setVisible(false);

        root.getChildren().addAll(
                lblTitle,
                btnCreateUser,
                btnSelectUser,
                userPanel
        );

        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createUser() {
        DialogHelper.showUserCreationDialog().ifPresent(result -> {
            String username = result[0];
            String password = result[1];
            String confirmPassword = result[2];

            if (!password.equals(confirmPassword)) {
                UIUtils.showAlert("Password Mismatch", "Passwords do not match.");
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
        DialogHelper.showLoginDialog().ifPresent(result -> {
            String username = result[0];
            String password = result[1];
            User user = logic.getUser(username);
            if (user == null) {
                UIUtils.showAlert("User Not Found", "No user found with that name.");
            }
            else if (!user.checkPassword(password)) {
                UIUtils.showAlert("Invalid Password", "Password is incorrect.");
            }
            else {
                currentUser = user;
                selectOrCreateMonth();
                finalizeLogin(username);
            }
        });
    }

    private void createMonthDialog() {
        DialogHelper.showMonthCreationDialog().ifPresent(data -> {
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
            }
            catch (Exception e) {
                UIUtils.showAlert("Invalid Input", "Please enter valid numbers for balance and goal.");
                createMonthDialog(); // Retry
            }
        });
    }

    private void selectOrCreateMonth() {
        Optional<ButtonType> result = DialogHelper.showMonthSelectionModeDialog();

        if (result.isPresent()) {
            ButtonType selected = result.get();

            if (selected.getButtonData() == ButtonBar.ButtonData.YES) {
                // For selecting an existing month
                List<String> existingMonths = new ArrayList<>(currentUser.getMonthDataMap().keySet());

                if (existingMonths.isEmpty()) {
                    UIUtils.showAlert("No Months Found", "No existing months available. Please create one.");
                    selectOrCreateMonth();
                    return;
                }

                DialogHelper.showChoiceDialog("Select Month", "Choose a month to continue planning:", "Months:", existingMonths)
                        .ifPresent(selectedMonth -> {
                            currentUser.selectMonth(selectedMonth);
                            logic.saveUsers();
                            lblCurrentMonth.setText("Planning for: " + selectedMonth);
                            lblWelcomeMessage.setText("Welcome back, " + currentUser.getName() + "!");
                            updateLabels();
                        });

            }
            else if (selected.getButtonData() == ButtonBar.ButtonData.NO) {
                // For creating a new month
                DialogHelper.showMonthCreationDialog().ifPresent(data -> {
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
                    } catch (Exception e) {
                        UIUtils.showAlert("Invalid Input", "Please enter valid numbers for balance and goal.");
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
                UIUtils.showAlert("Invalid Input", "Please enter a valid number for the amount.");
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
                UIUtils.showAlert("Invalid Input", "Please enter a valid number for the amount.");
            }
        });
    }

    private void deleteIncome() {
        if (currentUser == null) return;

        List<Income> incomes = currentUser.getIncomes();
        if (incomes.isEmpty()) {
            UIUtils.showAlert("No Incomes", "There are no incomes to delete.");
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
                UIUtils.showAlert("Success", "Income removed successfully.");
                logic.saveUsers();
                updateLabels();
            }
            else {
                UIUtils.showAlert("Not Found", "Could not find matching income.");
            }
        });
    }

    private void deleteExpense() {
        if (currentUser == null) return;

        List<Expense> expenses = currentUser.getExpenses();
        if (expenses.isEmpty()) {
            UIUtils.showAlert("No Expenses", "There are no expenses to delete.");
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
                UIUtils.showAlert("Success", "Expense removed successfully.");
                logic.saveUsers();
                updateLabels();
            }
            else {
                UIUtils.showAlert("Not Found", "Could not find matching expense.");
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

    private void showIncomeChart() {
        if (currentUser == null) return;
        MonthData data = currentUser.getCurrentMonthData();
        if (data == null || data.getIncomes().isEmpty()) {
            UIUtils.showAlert("No Incomes", "No incomes available for this month.");
            return;
        }

        PieChart chart = PieChartGenerator.generateIncomeChart(data);
        showChartInWindow(chart, "Income Distribution");
    }

    private void showExpenseChart() {
        if (currentUser == null) return;
        MonthData data = currentUser.getCurrentMonthData();
        if (data == null || data.getExpenses().isEmpty()) {
            UIUtils.showAlert("No Expenses", "No expenses available for this month.");
            return;
        }

        PieChart chart = PieChartGenerator.generateExpenseChart(data);
        showChartInWindow(chart, "Expense Distribution");
    }

    private void updateLabels() {
        MonthData data = currentUser.getCurrentMonthData();
        if (data == null) return;

        String currency = data.getCurrency();
        double startingBalance = data.getStartingBalance();
        double currentBalance = data.getCurrentBalance();
        double savingGoal = data.getSavingGoal();

        lblStartingBalance.setText("Starting balance: " + UIUtils.formatCurrency(startingBalance, currency));
        lblCurrentBalance.setText("Current balance: " + UIUtils.formatCurrency(currentBalance, currency));
        lblSavingGoal.setText("Saving goal: " + UIUtils.formatCurrency(savingGoal, currency));
        double remainingBalance = Math.max(0, currentBalance - savingGoal);
        lblMoneyRemaining.setText("Money remaining: " + UIUtils.formatCurrency(remainingBalance, currency));
    }

    private void showChartInWindow(PieChart chart, String title) {
        Stage chartStage = new Stage();
        chartStage.setTitle(title);

        VBox vbox = new VBox(chart);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 500, 400);
        chartStage.setScene(scene);
        chartStage.show();
    }
}