package dhbw.mosbach;

// Import libraries
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
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
    private MainController mainController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        logic = new UserLogic();
        mainController = new MainController(logic, this);

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
        btnAddIncome.setOnAction(e -> mainController.addIncome(this::updateLabels));

        Button btnAddExpense = new Button("Add Expense");
        btnAddExpense.setOnAction(e -> mainController.addExpense(this::updateLabels));

        Button btnShowIncomes = new Button("Show Incomes");
        btnShowIncomes.setOnAction(e -> mainController.showIncomes());

        Button btnShowExpenses = new Button("Show Expenses");
        btnShowExpenses.setOnAction(e -> mainController.showExpenses());

        Button btnDeleteIncome = new Button("Delete Income");
        btnDeleteIncome.setOnAction(e -> mainController.deleteIncome(this::updateLabels));

        Button btnDeleteExpense = new Button("Delete Expense");
        btnDeleteExpense.setOnAction(e -> mainController.deleteExpense(this::updateLabels));

        Button btnSetGoal = new Button("Set Goal");
        btnSetGoal.setOnAction(e -> mainController.setGoal(this::updateLabels));

        Button btnShowPieChart = new Button("Show Pie Charts");
        btnShowPieChart.setOnAction(e -> mainController.showPieChart());

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
                btnShowPieChart
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
        mainController.setCurrentUser(currentUser);
        updateLabels();
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
}