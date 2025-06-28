package dhbw.mosbach;

import java.util.List;

public abstract class Transaction {
    private double amount;
    private String description;
    private String category;
    private String date;

    public Transaction(double amount, String description, String category, String date) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public abstract List<String> getCategories();
}
