package dhbw.mosbach;

// Import libraries
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Type;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import com.google.gson.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

public class UserLogic {
    private static final String FILE_PATH = "users.json"; // File path to store user data
    private Map<String, User> users; // Map for managing the user according to their names
    private Gson gson; // Gson object to convert between JSON and Java objects

    // Initialize the Gson object and load user data from the file
    public UserLogic() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        users = loadUsers();
    }

    // Load users from the JSON file and store it in a map. If there is no file or the file is incorrect, an empty map will return
    private Map<String, User> loadUsers() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<Map<String, User>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    // Save the current user map in a JSON file
    public void saveUsers() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Adds a new user if he doesn't already exist
    public void addUser(String name, double startingBalance, String password) {
        if (!users.containsKey(name)) {
            users.put(name, new User(name, startingBalance, password));
            saveUsers();
        }
    }

    // Adds an expense to a user
    public boolean addExpenseToUser(User user, String category, double amount, String description) {
        if (user != null) {
            String date = java.time.LocalDate.now().toString();
            user.addExpense(category, amount, description, date);
            saveUsers();
            return true;
        }
        return false;
    }

    // Sets a saving goal of a user
    public void setUserSavingGoal(String name, double goalAmount) {
        User user = users.get(name);
        if (user != null) {
            user.setSavingGoal(goalAmount);
            saveUsers();
        }
    }

    // Return the user according to the name
    public User getUser(String name) {
        return users.get(name);
    }

    // Return a list of all users that exist
    public List<String> getAllUserNames() {
        return new ArrayList<>(users.keySet());
    }

    // Deletes a user of the map and saves the change
    public boolean deleteUser(String name) {
        if (users.containsKey(name)) {
            users.remove(name);
            saveUsers();
            return true;
        }
        return false;
    }

    // Deletes an expense from a user according to the description and adds the amount back to the user
    public boolean deleteExpenseFromUser(String userName, String description) {
        User user = users.get(userName);
        if (user != null) {
            List<Expense> expenses = user.getExpenses();
            for (int i = 0; i < expenses.size(); i++) {
                Expense exp = expenses.get(i);
                if (exp.getDescription().equals(description)) {
                    user.setCurrentBalance(user.getCurrentBalance() + exp.getAmount());
                    expenses.remove(i);
                    saveUsers();
                    return true;
                }
            }
        }
        return false;
    }

    // Adds an income to a user
    public boolean addIncomeToUser(User user, String category, double amount, String description) {
        if (user != null) {
            String date = java.time.LocalDate.now().toString();
            user.addIncome(category, amount, description, date);
            saveUsers();
            return true;
        }
        return false;
    }

    // Deletes an income from a user according to the description and subtracts the amount from the user
    public boolean deleteIncomeFromUser(String userName, String description) {
        User user = users.get(userName);
        if (user != null) {
            List<Income> incomes = user.getIncomes();
            for (int i = 0; i < incomes.size(); i++) {
                Income inc = incomes.get(i);
                if (inc.getDescription().equals(description)) {
                    user.setCurrentBalance(user.getCurrentBalance() - inc.getAmount());
                    incomes.remove(i);
                    saveUsers();
                    return true;
                }
            }
        }
        return false;
    }
}