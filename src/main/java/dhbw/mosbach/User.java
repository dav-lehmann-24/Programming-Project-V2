package dhbw.mosbach;

// Import libraries
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class User {
    // Attributes of a user
    String name;
    private String passwordHash;
    double startingBalance;
    double currentBalance;
    double savingGoal;
    Map<String, MonthData> monthDataMap;
    String currentMonth;

    // Constructor of a user
    public User(String name, double startingBalance, String plainPassword) {
        this.name = name;
        this.startingBalance = startingBalance;
        this.currentBalance = startingBalance;
        this.savingGoal = 0;
        this.passwordHash = BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
        this.monthDataMap = new HashMap<>();
        this.currentMonth = null;
    }

    // Adds an expense and subtracts it from the current balance
    public void addExpense(String category, double amount, String description, String date) {
        if (currentMonth == null) return;
        monthDataMap.get(currentMonth).addExpense(new Expense(amount, description, category, date));
        currentBalance -= amount;
    }

    // Sets a saving goal for a user
    public void setSavingGoal(double goal) {
        this.savingGoal = goal;
    }

    // Removes an expense according to the description if there is any
    public boolean removeExpense(String description) {
        MonthData data = getCurrentMonthData();
        if (data != null) {
            return data.getExpenses().removeIf(expense -> expense.getDescription().equals(description));
        }
        return false;
    }

    // Returns list of expenses of a user
    public List<Expense> getExpenses() {
        MonthData data = getCurrentMonthData();
        if (data != null) {
            return data.getExpenses();
        }
        return new ArrayList<>();
    }

    // Returns name of the user
    public String getName() {
        return name;
    }

    // Setting the current balance
    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    // Returns the current balance
    public double getCurrentBalance() {
        return currentBalance;
    }

    // Calculate the sum of all expenses of the user
    public double getTotalExpenses() {
        MonthData data = getCurrentMonthData();
        if (data != null) {
            return data.getTotalExpense();
        }
        return 0.0;
    }

    // Return the list of incomes of a user
    public List<Income> getIncomes() {
        MonthData data = getCurrentMonthData();
        if (data != null) {
            return data.getIncomes();
        }
        return new ArrayList<>();
    }

    // Adds an income and adds it to the current balance
    public void addIncome(String category, double amount, String description, String date) {
        if (currentMonth == null) return;
        monthDataMap.get(currentMonth).addIncome(new Income(amount, description, category, date));
        currentBalance += amount;
    }

    // Removes an income according to the description if there is any
    public boolean removeIncome(String description) {
        MonthData data = getCurrentMonthData();
        if (data != null) {
            return data.getIncomes().removeIf(income -> income.getDescription().equalsIgnoreCase(description));
        }
        return false;
    }

    // Calculate the sum of all incomes of the user
    public double getTotalIncome() {
        MonthData data = getCurrentMonthData();
        if (data != null) {
            return data.getTotalIncome();
        }
        return 0.0;
    }

    // Check if the password is correct
    public boolean checkPassword(String inputPassword) {
        return BCrypt.verifyer().verify(inputPassword.toCharArray(), passwordHash).verified;
    }

    public void selectMonth(String monthName) {
        this.currentMonth = monthName;
        monthDataMap.putIfAbsent(monthName, new MonthData(monthName));
    }

    public MonthData getCurrentMonthData() {
        return currentMonth != null ? monthDataMap.get(currentMonth) : null;
    }

    public String getCurrentMonth() {
        return currentMonth;
    }
}