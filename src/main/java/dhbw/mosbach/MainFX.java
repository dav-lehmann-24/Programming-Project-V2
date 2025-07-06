package dhbw.mosbach;

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
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #e3f0ff, #b3c6e7);");

        Label lblTitle = new Label("Welcome to CashCompass!");
        lblTitle.setStyle("-fx-text-fill: #1565c0; -fx-font-size: 36; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, #90caf9, 8, 0.5, 0, 2);");
        lblTitle.setMaxWidth(Double.MAX_VALUE);
        lblTitle.setAlignment(javafx.geometry.Pos.CENTER);
        VBox.setVgrow(lblTitle, Priority.NEVER);

        lblWelcomeMessage = new Label("");
        lblWelcomeMessage.setStyle("-fx-text-fill: #1976d2; -fx-font-size: 22; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, #bbdefb, 4, 0.5, 0, 1);");

        lblCurrentMonth = new Label("");
        lblCurrentMonth.setStyle("-fx-text-fill: #0d47a1; -fx-font-size: 20; -fx-font-weight: bold;");

        lblCurrentBalance = new Label("Current balance: -");
        lblSavingGoal = new Label("Saving goal: -");
        lblStartingBalance = new Label("Starting balance: -");
        lblMoneyRemaining = new Label("Money remaining: -");

        String buttonStyle = "-fx-font-size: 16; -fx-pref-width: 200px; -fx-pref-height: 40px; -fx-background-color: linear-gradient(to bottom, #64b5f6, #1976d2); -fx-text-fill: white; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, #90caf9, 4, 0.5, 0, 1);";
        String buttonStyleLarge = "-fx-font-size: 16; -fx-pref-width: 220px; -fx-pref-height: 45px; -fx-background-color: linear-gradient(to bottom, #42a5f5, #1565c0); -fx-text-fill: white; -fx-background-radius: 22; -fx-effect: dropshadow(gaussian, #90caf9, 4, 0.5, 0, 1);";
        String panelStyle = "-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 20;";

        Button btnAddIncome = new Button("Add Income");
        btnAddIncome.setOnAction(e -> mainController.addIncome(this::updateLabels));
        btnAddIncome.setStyle(buttonStyle);

        Button btnAddExpense = new Button("Add Expense");
        btnAddExpense.setOnAction(e -> mainController.addExpense(this::updateLabels));
        btnAddExpense.setStyle(buttonStyle);

        Button btnShowIncomes = new Button("Show Incomes");
        btnShowIncomes.setOnAction(e -> mainController.showIncomes());
        btnShowIncomes.setStyle(buttonStyle);

        Button btnShowExpenses = new Button("Show Expenses");
        btnShowExpenses.setOnAction(e -> mainController.showExpenses());
        btnShowExpenses.setStyle(buttonStyle);

        Button btnDeleteIncome = new Button("Delete Income");
        btnDeleteIncome.setOnAction(e -> mainController.deleteIncome(this::updateLabels));
        btnDeleteIncome.setStyle(buttonStyle);

        Button btnDeleteExpense = new Button("Delete Expense");
        btnDeleteExpense.setOnAction(e -> mainController.deleteExpense(this::updateLabels));
        btnDeleteExpense.setStyle(buttonStyle);

        Button btnSetGoal = new Button("Set Goal");
        btnSetGoal.setOnAction(e -> mainController.setGoal(this::updateLabels));
        btnSetGoal.setStyle(buttonStyle);

        Button btnShowPieChart = new Button("Show Pie Charts");
        btnShowPieChart.setOnAction(e -> mainController.showPieChart());
        btnShowPieChart.setStyle(buttonStyle);

        Button btnSelectUser = new Button("Select User");
        btnSelectUser.setOnAction(e -> selectUser());
        btnSelectUser.setStyle(buttonStyleLarge);

        Button btnCreateUser = new Button("Create User");
        btnCreateUser.setOnAction(e -> createUser());
        btnCreateUser.setStyle(buttonStyleLarge);

        Button btnDeleteUser = new Button("Delete User");
        btnDeleteUser.setOnAction(e -> deleteUser());
        btnDeleteUser.setStyle(buttonStyleLarge);

        VBox infoPanel = new VBox(10);
        infoPanel.setPadding(new Insets(40, 40, 40, 40));
        infoPanel.setStyle(panelStyle);
        lblStartingBalance.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1976d2;");
        lblCurrentBalance.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1976d2;");
        lblSavingGoal.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1976d2;");
        lblMoneyRemaining.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1976d2;");
        infoPanel.getChildren().addAll(
            lblWelcomeMessage,
            lblCurrentMonth,
            lblStartingBalance,
            lblCurrentBalance,
            lblSavingGoal,
            lblMoneyRemaining
        );
        infoPanel.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(infoPanel, Priority.ALWAYS);

        HBox userSelectBox = new HBox(20, btnCreateUser, btnSelectUser, btnDeleteUser);
        userSelectBox.setPadding(new Insets(10, 0, 30, 0));
        userSelectBox.setAlignment(javafx.geometry.Pos.CENTER);

        VBox addBox = new VBox(10, btnAddIncome, btnAddExpense);
        addBox.setPadding(new Insets(0,0,10,0));
        addBox.setStyle(panelStyle + "-fx-border-color: #90caf9; -fx-border-width: 0 0 1 0;");
        VBox showBox = new VBox(10, btnShowIncomes, btnShowExpenses);
        showBox.setPadding(new Insets(0,0,10,0));
        showBox.setStyle(panelStyle + "-fx-border-color: #90caf9; -fx-border-width: 0 0 1 0;");
        VBox deleteBox = new VBox(10, btnDeleteIncome, btnDeleteExpense);
        deleteBox.setPadding(new Insets(0,0,10,0));
        deleteBox.setStyle(panelStyle + "-fx-border-color: #90caf9; -fx-border-width: 0 0 1 0;");
        VBox miscBox = new VBox(10, btnSetGoal, btnShowPieChart);
        miscBox.setStyle(panelStyle);

        VBox buttonPanel = new VBox(20, addBox, showBox, deleteBox, miscBox);
        buttonPanel.setPadding(new Insets(20, 40, 20, 0));
        buttonPanel.setStyle("-fx-background-color: transparent;");

        HBox userContent = new HBox(40);
        userContent.getChildren().addAll(buttonPanel, infoPanel);
        userContent.setStyle("-fx-background-color: rgba(33,150,243,0.08); -fx-background-radius: 20;");
        userContent.setPadding(new Insets(20));
        HBox.setHgrow(infoPanel, Priority.ALWAYS);

        userPanel = new VBox(10);
        userPanel.getChildren().addAll(userContent);
        userPanel.setVisible(false);
        userPanel.setStyle("-fx-background-color: transparent;");

        root.getChildren().setAll(
                lblTitle,
                userSelectBox,
                userPanel
        );

        Scene scene = new Scene(root, 1000, 800);
        root.setOpacity(0);
        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1.2), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        for (Button btn : List.of(btnAddIncome, btnAddExpense, btnShowIncomes, btnShowExpenses, btnDeleteIncome, btnDeleteExpense, btnSetGoal, btnShowPieChart, btnCreateUser, btnSelectUser, btnDeleteUser)) {
            btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-scale-x:1.05;-fx-scale-y:1.05;-fx-background-color: linear-gradient(to bottom, #1976d2, #64b5f6);"));
            btn.setOnMouseExited(e -> {
                if (btn.getPrefWidth() > 210) btn.setStyle(buttonStyleLarge);
                else btn.setStyle(buttonStyle);
            });
        }

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

                lblCurrentMonth.setText("Planning for: " + month);
                lblWelcomeMessage.setText("Welcome back, " + currentUser.getName() + "!");
                userPanel.setVisible(true);
                updateLabels();
            }
            catch (Exception e) {
                UIUtils.showAlert("Invalid Input", "Please enter valid numbers for balance and goal.");
                createMonthDialog();
            }
        });
    }

    private void selectOrCreateMonth() {
        Optional<ButtonType> result = DialogHelper.showMonthSelectionModeDialog();

        if (result.isPresent()) {
            ButtonType selected = result.get();

            if (selected.getButtonData() == ButtonBar.ButtonData.YES) {
                List<String> existingMonths = new ArrayList<>();
                for (MonthData md : currentUser.getAllMonths()) {
                    existingMonths.add(md.getMonthName());
                }
                if (existingMonths.isEmpty()) {
                    UIUtils.showAlert("No Months Found", "No existing months available. Please create one.");
                    selectOrCreateMonth();
                    return;
                }
                DialogHelper.showChoiceDialog("Select Month", "Choose a month to continue planning:", "Months:", existingMonths)
                        .ifPresent(selectedMonth -> {
                            currentUser.selectMonth(selectedMonth, 0, "", 0); // Werte werden nicht überschrieben
                            lblCurrentMonth.setText("Planning for: " + selectedMonth);
                            lblWelcomeMessage.setText("Welcome back, " + currentUser.getName() + "!");
                            updateLabels();
                        });

            }
            else if (selected.getButtonData() == ButtonBar.ButtonData.NO) {
                DialogHelper.showMonthCreationDialog().ifPresent(data -> {
                    try {
                        String month = data[0].trim();
                        double balance = Double.parseDouble(data[1].trim());
                        String currency = data[2].trim();
                        double goal = data[3].isEmpty() ? 0.0 : Double.parseDouble(data[3].trim());

                        currentUser.selectMonth(month, balance, currency, goal);
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

    private void deleteUser() {
        DialogHelper.showDeleteUserDialog().ifPresent(result -> {
            String username = result[0];
            String password = result[1];
            String confirmPassword = result[2];
            if (!password.equals(confirmPassword)) {
                UIUtils.showAlert("Password Mismatch", "Passwords do not match.");
                return;
            }
            User user = logic.getUser(username);
            if (user == null) {
                UIUtils.showAlert("User Not Found", "No user found with that name.");
                return;
            }
            if (!user.checkPassword(password)) {
                UIUtils.showAlert("Invalid Password", "Password is incorrect.");
                return;
            }
            boolean deleted = logic.deleteUser(username);
            if (deleted) {
                UIUtils.showAlert("User Deleted", "User '" + username + "' was deleted successfully.");
                userPanel.setVisible(false);
                lblWelcomeMessage.setText("");
                lblCurrentMonth.setText("");
            }
            else {
                UIUtils.showAlert("Delete Failed", "User could not be deleted.");
            }
        });
    }
}