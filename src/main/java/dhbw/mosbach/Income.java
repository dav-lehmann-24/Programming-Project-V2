package dhbw.mosbach;

import java.util.List;

public class Income extends Transaction {
    private String category;
    public static final List<String> INCOME_TYPES = List.of("Salary", "Freelance", "Interest", "Gift");

    public Income(double amount, String description, String category, String date) {
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