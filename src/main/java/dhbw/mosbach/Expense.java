package dhbw.mosbach;

import java.util.List;

public class Expense extends Transaction {
    public static final List<String> EXPENSE_TYPES = List.of("Rent", "Groceries", "Utilities", "Transport", "Entertainment", "Bills");

    public Expense(double amount, String description, String category, String date) {
        super(amount, description, category, date);
    }

    @Override
    public List<String> getCategories() {
        return EXPENSE_TYPES;
    }

    @Override
    public String toString() {
        return getCategory() + " - " + getAmount() + " - " + getDescription();
    }
}