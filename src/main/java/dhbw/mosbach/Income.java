package dhbw.mosbach;

import java.util.List;

public class Income extends Transaction {
    public static final List<String> INCOME_TYPES = List.of("Salary", "Freelance", "Interest", "Gift");

    public Income(double amount, String description, String category, String date) {
        super(amount, description, category, date);
    }

    public static List<String> getCategories() {
        return INCOME_TYPES;
    }

}