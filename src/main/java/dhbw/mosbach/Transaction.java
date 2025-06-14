package dhbw.mosbach;

public abstract class Transaction {
    protected double amount;
    protected String description;
    protected String category;
    protected String date;

    public Transaction(double amount, String description, String category, String date) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }
}
