package dhbw.mosbach;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.geometry.Insets;
import java.util.List;

public class MainController {
    private final UserLogic logic;
    private User currentUser;
    private final MainFX mainFX;

    public MainController(UserLogic logic, MainFX mainFX) {
        this.logic = logic;
        this.mainFX = mainFX;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void addIncome(Runnable updateLabels) {
        DialogHelper.showAddIncomeDialog().ifPresent(data -> {
            try {
                String category = data[0];
                double amount = Double.parseDouble(data[1]);
                String description = data[2];
                String date = data[3];
                currentUser.addIncome(category, amount, description, date);
                logic.saveUsers();
                updateLabels.run();
            }
            catch (NumberFormatException e) {
                UIUtils.showAlert("Invalid Input", "Please enter a valid number for the amount.");
            }
        });
    }

    public void addExpense(Runnable updateLabels) {
        DialogHelper.showAddExpenseDialog().ifPresent(data -> {
            try {
                String category = data[0];
                double amount = Double.parseDouble(data[1]);
                String description = data[2];
                String date = data[3];
                currentUser.addExpense(category, amount, description, date);
                logic.saveUsers();
                updateLabels.run();
            }
            catch (NumberFormatException e) {
                UIUtils.showAlert("Invalid Input", "Please enter a valid number for the amount.");
            }
        });
    }

    public <T extends Transaction> void showTransactions(List<T> transactions, String title) {
        if (currentUser == null) return;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("List of " + title.toLowerCase() + ":");
        StringBuilder sb = new StringBuilder();
        for (T t : transactions) {
            sb.append(t.getCategory()).append(" - ").append(t.getAmount()).append(" - ").append(t.getDescription()).append("\n");
        }
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    public void showIncomes() {
        showTransactions(currentUser.getIncomes(), "Incomes");
    }

    public void showExpenses() {
        showTransactions(currentUser.getExpenses(), "Expenses");
    }

    public void deleteIncome(Runnable updateLabels) {
        if (currentUser == null) return;
        List<Income> incomes = currentUser.getIncomes();
        if (incomes.isEmpty()) {
            UIUtils.showAlert("No Incomes", "There are no incomes to delete.");
            return;
        }
        DialogHelper.showDeleteIncomeDialog(incomes).ifPresent(income -> {
            MonthData data = currentUser.getCurrentMonthData();
            if (data != null) {
                data.setCurrentBalance(data.getCurrentBalance() - income.getAmount());
                data.getIncomes().remove(income);
            }
            logic.saveUsers();
            updateLabels.run();
        });
    }

    public void deleteExpense(Runnable updateLabels) {
        if (currentUser == null) return;
        List<Expense> expenses = currentUser.getExpenses();
        if (expenses.isEmpty()) {
            UIUtils.showAlert("No Expenses", "There are no expenses to delete.");
            return;
        }
        DialogHelper.showDeleteExpenseDialog(expenses).ifPresent(expense -> {
            MonthData data = currentUser.getCurrentMonthData();
            if (data != null) {
                data.setCurrentBalance(data.getCurrentBalance() + expense.getAmount());
                data.getExpenses().remove(expense);
            }
            logic.saveUsers();
            updateLabels.run();
        });
    }

    public void setGoal(Runnable updateLabels) {
        if (currentUser == null) return;
        MonthData data = currentUser.getCurrentMonthData();
        if (data == null) return;
        DialogHelper.showSetGoalDialog().ifPresent(goal -> {
            data.setSavingGoal(goal);
            logic.saveUsers();
            updateLabels.run();
        });
    }

    public void showPieChart() {
        if (currentUser == null) return;
        MonthData data = currentUser.getCurrentMonthData();
        if (data == null) return;
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));
        boolean hasData = false;
        if (!data.getIncomes().isEmpty()) {
            PieChart incomeChart = PieChartGenerator.generateChart(
                    data.getIncomes(),
                    "Incomes by Category",
                    Income::getCategory,
                    Income::getAmount,
                    data.getCurrency()
            );
            contentBox.getChildren().add(incomeChart);
            hasData = true;
        }
        else {
            contentBox.getChildren().add(new javafx.scene.control.Label("No incomes recorded."));
        }
        if (!data.getExpenses().isEmpty()) {
            PieChart expenseChart = PieChartGenerator.generateChart(
                    data.getExpenses(),
                    "Expenses by Category",
                    Expense::getCategory,
                    Expense::getAmount,
                    data.getCurrency()
            );
            contentBox.getChildren().add(expenseChart);
            hasData = true;
        }
        else {
            contentBox.getChildren().add(new javafx.scene.control.Label("No expenses recorded."));
        }
        if (hasData || !contentBox.getChildren().isEmpty()) {
            Stage chartStage = new Stage();
            chartStage.setTitle("Monthly Overview");
            Scene scene = new Scene(contentBox, 600, 500);
            chartStage.setScene(scene);
            chartStage.show();
        }
        else {
            UIUtils.showAlert("No Data", "No income or expense data available to show pie charts.");
        }
    }
}
