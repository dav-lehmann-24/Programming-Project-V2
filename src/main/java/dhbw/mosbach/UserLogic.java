package dhbw.mosbach;

// Import libraries
import java.util.Map;
import java.util.HashMap;
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
            Map<String, User> loadedUsers = gson.fromJson(reader, type);
            return (loadedUsers != null) ? loadedUsers : new HashMap<>();
        }
        catch (IOException e) {
            return new HashMap<>();
        }
    }

    // Save the current user map in a JSON file
    public void saveUsers() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(users, writer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Adds a new user if he doesn't already exist
    public void addUser(String name, String password) {
        if (!users.containsKey(name)) {
            users.put(name, new User(name, password));
            saveUsers();
        }
    }

    // Return the user according to the name
    public User getUser(String name) {
        return users.get(name);
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
}