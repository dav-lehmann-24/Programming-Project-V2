package dhbw.mosbach;

import java.util.ArrayList;
import java.util.List;

public class MonthData {
    private String monthName;
    private double startingBalance;
    private double currentBalance;
    private double savingGoal;
    private String currency;
    private List<Income> incomes;
    private List<Expense> expenses;

    public MonthData(String monthName, double startingBalance, String currency, double savingGoal) {
        this.monthName = monthName;
        this.startingBalance = startingBalance;
        this.currentBalance = startingBalance;
        this.currency = currency;
        this.savingGoal = savingGoal;
        this.incomes = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }

    public String getMonthName() {
        return monthName;
    }

    public double getStartingBalance() {
        return startingBalance;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double amount) {
        this.currentBalance = amount;
    }

    public double getSavingGoal() {
        return savingGoal;
    }

    public void setSavingGoal(double goal) {
        this.savingGoal = goal;
    }

    public String getCurrency() {
        return currency;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public void addIncome(Income income) {
        incomes.add(income);
    }

    public List<Income> getIncomes() {
        return incomes;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public double getTotalIncome() {
        return incomes.stream().mapToDouble(Income::getAmount).sum();
    }

    public double getTotalExpense() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }
}
