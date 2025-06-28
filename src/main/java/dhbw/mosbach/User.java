package dhbw.mosbach;

// Import libraries
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class User {
    // Attributes of a user
    private String name;
    private String passwordHash;
    private Map<String, MonthData> monthDataMap;
    private String currentMonth;

    // Constructor of a user
    public User(String name, String plainPassword) {
        this.name = name;
        this.passwordHash = BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
        this.monthDataMap = new HashMap<>();
        this.currentMonth = null;
    }

    // Adds an expense and subtracts it from the current balance
    public void addExpense(String category, double amount, String description, String date) {
        addTransaction(Expense.class, category, amount, description, date);
    }

    // Returns list of expenses of a user
    public List<Expense> getExpenses() {
        return getTransactions(Expense.class);
    }

    // Returns name of the user
    public String getName() {
        return name;
    }

    // Return the list of incomes of a user
    public List<Income> getIncomes() {
        return getTransactions(Income.class);
    }

    // Adds an income and adds it to the current balance
    public void addIncome(String category, double amount, String description, String date) {
        addTransaction(Income.class, category, amount, description, date);
    }

    // Check if the password is correct
    public boolean checkPassword(String inputPassword) {
        return BCrypt.verifyer().verify(inputPassword.toCharArray(), passwordHash).verified;
    }

    public void selectMonth(String monthName, double startingBalance, String currency, double savingGoal) {
        this.currentMonth = monthName;
        monthDataMap.putIfAbsent(monthName, new MonthData(monthName, startingBalance, currency, savingGoal));
    }

    public MonthData getCurrentMonthData() {
        return currentMonth != null ? monthDataMap.get(currentMonth) : null;
    }

    public Map<String, MonthData> getMonthDataMap() {
        return monthDataMap;
    }

    public void selectMonth(String monthName) {
        if (monthDataMap.containsKey(monthName)) {
            this.currentMonth = monthName;
        }
    }

    public <T extends Transaction> void addTransaction(Class<T> clazz, String category, double amount, String description, String date) {
        MonthData data = getCurrentMonthData();
        if (data != null) {
            if (clazz == Income.class) {
                data.addIncome(new Income(amount, description, category, date));
                data.setCurrentBalance(data.getCurrentBalance() + amount);
            }
            else if (clazz == Expense.class) {
                data.addExpense(new Expense(amount, description, category, date));
                data.setCurrentBalance(data.getCurrentBalance() - amount);
            }
        }
    }

    public <T extends Transaction> boolean removeTransaction(Class<T> clazz, String description) {
        MonthData data = getCurrentMonthData();
        if (data != null) {
            if (clazz == Income.class) {
                for (Income i : new ArrayList<>(data.getIncomes())) {
                    if (i.getDescription().equalsIgnoreCase(description)) {
                        data.getIncomes().remove(i);
                        data.setCurrentBalance(data.getCurrentBalance() - i.getAmount());
                        return true;
                    }
                }
            }
            else if (clazz == Expense.class) {
                for (Expense e : new ArrayList<>(data.getExpenses())) {
                    if (e.getDescription().equalsIgnoreCase(description)) {
                        data.getExpenses().remove(e);
                        data.setCurrentBalance(data.getCurrentBalance() + e.getAmount());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T extends Transaction> List<T> getTransactions(Class<T> clazz) {
        MonthData data = getCurrentMonthData();
        if (data != null) {
            if (clazz == Income.class) {
                return (List<T>) data.getIncomes();
            }
            else if (clazz == Expense.class) {
                return (List<T>) data.getExpenses();
            }
        }
        return new ArrayList<>();
    }
}