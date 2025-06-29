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
        lblTitle.setMaxWidth(Double.MAX_VALUE);
        lblTitle.setAlignment(javafx.geometry.Pos.CENTER);
        VBox.setVgrow(lblTitle, Priority.NEVER);

        lblWelcomeMessage = new Label("");
        lblWelcomeMessage.setStyle("-fx-text-fill: green; -fx-font-size: 22; -fx-font-weight: bold;");

        lblCurrentMonth = new Label("");
        lblCurrentMonth.setStyle("-fx-text-fill: black; -fx-font-size: 20; -fx-font-weight: bold;");

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
        
        VBox infoPanel = new VBox(10);
        infoPanel.setPadding(new Insets(20, 0, 20, 40));
        infoPanel.setStyle("-fx-background-color: transparent;");
        lblWelcomeMessage.setStyle("-fx-text-fill: green; -fx-font-size: 22; -fx-font-weight: bold;");
        lblCurrentMonth.setStyle("-fx-text-fill: black; -fx-font-size: 20; -fx-font-weight: bold;");
        lblStartingBalance.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        lblCurrentBalance.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        lblSavingGoal.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        lblMoneyRemaining.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        infoPanel.getChildren().addAll(
            lblWelcomeMessage,
            lblCurrentMonth,
            lblStartingBalance,
            lblCurrentBalance,
            lblSavingGoal,
            lblMoneyRemaining
        );

        btnAddIncome.setStyle("-fx-font-size: 16; -fx-pref-width: 200px; -fx-pref-height: 40px;");
        btnAddExpense.setStyle("-fx-font-size: 16; -fx-pref-width: 200px; -fx-pref-height: 40px;");
        btnShowIncomes.setStyle("-fx-font-size: 16; -fx-pref-width: 200px; -fx-pref-height: 40px;");
        btnShowExpenses.setStyle("-fx-font-size: 16; -fx-pref-width: 200px; -fx-pref-height: 40px;");
        btnDeleteIncome.setStyle("-fx-font-size: 16; -fx-pref-width: 200px; -fx-pref-height: 40px;");
        btnDeleteExpense.setStyle("-fx-font-size: 16; -fx-pref-width: 200px; -fx-pref-height: 40px;");
        btnSetGoal.setStyle("-fx-font-size: 16; -fx-pref-width: 200px; -fx-pref-height: 40px;");
        btnShowPieChart.setStyle("-fx-font-size: 16; -fx-pref-width: 200px; -fx-pref-height: 40px;");
        btnCreateUser.setStyle("-fx-font-size: 16; -fx-pref-width: 220px; -fx-pref-height: 45px;");
        btnSelectUser.setStyle("-fx-font-size: 16; -fx-pref-width: 220px; -fx-pref-height: 45px;");

        HBox userSelectBox = new HBox(20, btnCreateUser, btnSelectUser);
        userSelectBox.setPadding(new Insets(10, 0, 30, 0));
        userSelectBox.setAlignment(javafx.geometry.Pos.CENTER);

        VBox addBox = new VBox(10, btnAddIncome, btnAddExpense);
        addBox.setPadding(new Insets(0,0,10,0));
        addBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        VBox showBox = new VBox(10, btnShowIncomes, btnShowExpenses);
        showBox.setPadding(new Insets(0,0,10,0));
        showBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        VBox deleteBox = new VBox(10, btnDeleteIncome, btnDeleteExpense);
        deleteBox.setPadding(new Insets(0,0,10,0));
        deleteBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        VBox miscBox = new VBox(10, btnSetGoal, btnShowPieChart);

        VBox buttonPanel = new VBox(20, addBox, showBox, deleteBox, miscBox);
        buttonPanel.setPadding(new Insets(20, 40, 20, 0));
        buttonPanel.setStyle("-fx-background-color: transparent;");

        HBox userContent = new HBox(40);
        userContent.getChildren().addAll(buttonPanel, infoPanel);

        userPanel = new VBox(10);
        userPanel.getChildren().addAll(userContent);
        userPanel.setVisible(false);

        root.getChildren().setAll(
                lblTitle,
                userSelectBox,
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