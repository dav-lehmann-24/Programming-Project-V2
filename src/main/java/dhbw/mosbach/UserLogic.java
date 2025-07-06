package dhbw.mosbach;

import java.sql.*;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class UserLogic {
    public UserLogic() {

    }

    public void addUser(String name, String password) {
        String passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO users (name, password_hash) VALUES (?, ?) ON CONFLICT (name) DO NOTHING";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setString(2, passwordHash);
                stmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUser(String name) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT id, name, password_hash FROM users WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("id");
                        String passwordHash = rs.getString("password_hash");
                        User user = new User(name, passwordHash, true); // true = already hashed
                        loadMonthsForUser(user, userId, conn);
                        return user;
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteUser(String name) {
        try (Connection conn = Database.getConnection()) {
            String sql = "DELETE FROM users WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                int affected = stmt.executeUpdate();
                return affected > 0;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void loadMonthsForUser(User user, int userId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM months WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String monthName = rs.getString("name");
                    double startingBalance = rs.getDouble("starting_balance");
                    double currentBalance = rs.getDouble("current_balance");
                    String currency = rs.getString("currency");
                    double savingGoal = rs.getDouble("saving_goal");
                    int monthId = rs.getInt("id");
                    MonthData month = new MonthData(monthName, startingBalance, currency, savingGoal);
                    month.setCurrentBalance(currentBalance);
                    loadIncomesForMonth(month, monthId, conn);
                    loadExpensesForMonth(month, monthId, conn);
                    user.getMonthDataMap().put(monthName, month);
                }
            }
        }
    }

    private void loadIncomesForMonth(MonthData month, int monthId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM incomes WHERE month_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, monthId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double amount = rs.getDouble("amount");
                    String description = rs.getString("description");
                    String category = rs.getString("category");
                    String date = rs.getDate("date").toString();
                    month.addIncome(new Income(amount, description, category, date));
                }
            }
        }
    }

    private void loadExpensesForMonth(MonthData month, int monthId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM expenses WHERE month_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, monthId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double amount = rs.getDouble("amount");
                    String description = rs.getString("description");
                    String category = rs.getString("category");
                    String date = rs.getDate("date").toString();
                    month.addExpense(new Expense(amount, description, category, date));
                }
            }
        }
    }
}