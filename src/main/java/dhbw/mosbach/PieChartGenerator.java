package dhbw.mosbach;

import javafx.scene.chart.PieChart;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PieChartGenerator {
    public static PieChart generateIncomeChart(MonthData data) {
        PieChart chart = new PieChart();
        chart.setTitle("Incomes by Category");

        Map<String, Double> map = sumByCategory(data.getIncomes());
        map.forEach((category, sum) ->
                chart.getData().add(new PieChart.Data(category + " (" + format(sum, data.getCurrency()) + ")", sum))
        );
        return chart;
    }

    public static PieChart generateExpenseChart(MonthData data) {
        PieChart chart = new PieChart();
        chart.setTitle("Expenses by Category");

        Map<String, Double> map = sumByCategory(data.getExpenses());
        map.forEach((category, sum) ->
                chart.getData().add(new PieChart.Data(category + " (" + format(sum, data.getCurrency()) + ")", sum))
        );
        return chart;
    }

    private static Map<String, Double> sumByCategory(List<? extends Transaction> list) {
        Map<String, Double> result = new HashMap<>();
        for (Transaction t : list) {
            String category = (t instanceof Income) ? ((Income)t).getCategory() : ((Expense)t).getCategory();
            result.merge(category, t.getAmount(), Double::sum);
        }
        return result;
    }

    private static String format(double amount, String currency) {
        return String.format("%.2f%s", amount, currency);
    }
}
