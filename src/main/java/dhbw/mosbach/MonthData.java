package dhbw.mosbach;

import java.util.ArrayList;
import java.util.List;

public class MonthData {
    private String monthName;
    private List<Income> incomes;
    private List<Expense> expenses;

    public MonthData(String monthName) {
        this.monthName = monthName;
        this.incomes = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }

    public String getMonthName() {
        return monthName;
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
