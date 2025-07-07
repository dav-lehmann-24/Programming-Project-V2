package dhbw.mosbach;

// Import libraries
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import at.favre.lib.crypto.bcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private String name;
    private String passwordHash;
    private Map<String, MonthData> monthDataMap;
    private String currentMonth;

    public User(String name, String passwordHash, boolean alreadyHashed) {
        this.name = name;
        if (alreadyHashed) {
            this.passwordHash = passwordHash;
        }
        else {
            this.passwordHash = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(12, passwordHash.toCharArray());
        }
        this.monthDataMap = new HashMap<>();
        this.currentMonth = null;
    }

    public void addExpense(String category, double amount, String description, String date) {
        MonthData data = getCurrentMonthData();
        if (data != null) {
            data.addExpense(new Expense(amount, description, category, date));
            data.setCurrentBalance(data.getCurrentBalance() - amount);
            try (Connection conn = Database.getConnection()) {
                String sql = "INSERT INTO expenses (month_id, amount, description, category, date) VALUES ((SELECT id FROM months WHERE user_id = (SELECT id FROM users WHERE name = ?) AND name = ?), ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, name);
                    stmt.setString(2, currentMonth);
                    stmt.setDouble(3, amount);
                    stmt.setString(4, description);
                    stmt.setString(5, category);
                    stmt.setDate(6, java.sql.Date.valueOf(date));
                    stmt.executeUpdate();
                }
                String updateSql = "UPDATE months SET current_balance = current_balance - ? WHERE user_id = (SELECT id FROM users WHERE name = ?) AND name = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setDouble(1, amount);
                    stmt.setString(2, name);
                    stmt.setString(3, currentMonth);
                    stmt.executeUpdate();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Expense> getExpenses() {
        return getTransactions(Expense.class);
    }

    public String getName() {
        return name;
    }

    public List<Income> getIncomes() {
        return getTransactions(Income.class);
    }


    public boolean checkPassword(String inputPassword) {
        return BCrypt.verifyer().verify(inputPassword.toCharArray(), passwordHash).verified;
    }

    public void selectMonth(String monthName, double startingBalance, String currency, double savingGoal) {
        this.currentMonth = monthName;
        try (Connection conn = Database.getConnection()) {
            String selectSql = "SELECT id FROM months WHERE user_id = (SELECT id FROM users WHERE name = ?) AND name = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, name);
                selectStmt.setString(2, monthName);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (!rs.next()) {
                        String insertSql = "INSERT INTO months (user_id, name, starting_balance, current_balance, saving_goal, currency) VALUES ((SELECT id FROM users WHERE name = ?), ?, ?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, name);
                            insertStmt.setString(2, monthName);
                            insertStmt.setDouble(3, startingBalance);
                            insertStmt.setDouble(4, startingBalance);
                            insertStmt.setDouble(5, savingGoal);
                            insertStmt.setString(6, currency);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        monthDataMap.putIfAbsent(monthName, new MonthData(monthName, startingBalance, currency, savingGoal));
    }

    public void setSavingGoal(double goal) {
        MonthData data = getCurrentMonthData();
        if (data != null) {
            data.setSavingGoal(goal);
            try (Connection conn = Database.getConnection()) {
                String sql = "UPDATE months SET saving_goal = ? WHERE user_id = (SELECT id FROM users WHERE name = ?) AND name = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setDouble(1, goal);
                    stmt.setString(2, name);
                    stmt.setString(3, currentMonth);
                    stmt.executeUpdate();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addIncome(String category, double amount, String description, String date) {
        MonthData data = getCurrentMonthData();
        if (data != null) {
            data.addIncome(new Income(amount, description, category, date));
            data.setCurrentBalance(data.getCurrentBalance() + amount);
            try (Connection conn = Database.getConnection()) {
                String sql = "INSERT INTO incomes (month_id, amount, description, category, date) VALUES ((SELECT id FROM months WHERE user_id = (SELECT id FROM users WHERE name = ?) AND name = ?), ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, name);
                    stmt.setString(2, currentMonth);
                    stmt.setDouble(3, amount);
                    stmt.setString(4, description);
                    stmt.setString(5, category);
                    stmt.setDate(6, java.sql.Date.valueOf(date));
                    stmt.executeUpdate();
                }
                String updateSql = "UPDATE months SET current_balance = current_balance + ? WHERE user_id = (SELECT id FROM users WHERE name = ?) AND name = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setDouble(1, amount);
                    stmt.setString(2, name);
                    stmt.setString(3, currentMonth);
                    stmt.executeUpdate();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean removeIncome(String description) {
        MonthData data = getCurrentMonthData();
        if (data != null) {
            for (Income i : new ArrayList<>(data.getIncomes())) {
                if (i.getDescription().equalsIgnoreCase(description)) {
                    data.getIncomes().remove(i);
                    data.setCurrentBalance(data.getCurrentBalance() - i.getAmount());
                    try (Connection conn = Database.getConnection()) {
                        String sql = "DELETE FROM incomes WHERE month_id = (SELECT id FROM months WHERE user_id = (SELECT id FROM users WHERE name = ?) AND name = ?) AND description = ? AND amount = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, name);
                            stmt.setString(2, currentMonth);
                            stmt.setString(3, description);
                            stmt.setDouble(4, i.getAmount());
                            stmt.executeUpdate();
                        }
                        String updateSql = "UPDATE months SET current_balance = current_balance - ? WHERE user_id = (SELECT id FROM users WHERE name = ?) AND name = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                            stmt.setDouble(1, i.getAmount());
                            stmt.setString(2, name);
                            stmt.setString(3, currentMonth);
                            stmt.executeUpdate();
                        }
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeExpense(String description) {
        MonthData data = getCurrentMonthData();
        if (data != null) {
            for (Expense e : new ArrayList<>(data.getExpenses())) {
                if (e.getDescription().equalsIgnoreCase(description)) {
                    data.getExpenses().remove(e);
                    data.setCurrentBalance(data.getCurrentBalance() + e.getAmount());
                    try (Connection conn = Database.getConnection()) {
                        String sql = "DELETE FROM expenses WHERE month_id = (SELECT id FROM months WHERE user_id = (SELECT id FROM users WHERE name = ?) AND name = ?) AND description = ? AND amount = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, name);
                            stmt.setString(2, currentMonth);
                            stmt.setString(3, description);
                            stmt.setDouble(4, e.getAmount());
                            stmt.executeUpdate();
                        }
                        String updateSql = "UPDATE months SET current_balance = current_balance + ? WHERE user_id = (SELECT id FROM users WHERE name = ?) AND name = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                            stmt.setDouble(1, e.getAmount());
                            stmt.setString(2, name);
                            stmt.setString(3, currentMonth);
                            stmt.executeUpdate();
                        }
                    }
                    catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public MonthData getCurrentMonthData() {
        if (currentMonth == null) return null;
        return monthDataMap.get(currentMonth);
    }

    public List<MonthData> getAllMonths() {
        return new ArrayList<>(monthDataMap.values());
    }

    public Map<String, MonthData> getMonthDataMap() {
        return monthDataMap;
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