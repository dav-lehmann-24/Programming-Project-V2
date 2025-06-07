package dhbw.mosbach;

import java.util.List;

public class Expense extends Transaction {
    private String category;
    public static final List<String> EXPENSE_TYPES = List.of("Rent", "Groceries", "Utilities", "Transport", "Entertainment", "Bills");

    public Expense(double amount, String description, String category, String date) {
        super(amount, description, date);
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}