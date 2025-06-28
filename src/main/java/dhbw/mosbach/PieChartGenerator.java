package dhbw.mosbach;

import javafx.scene.chart.PieChart;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class PieChartGenerator {

    public static <T extends Transaction> PieChart generateChart(
            List<T> transactions,
            String title,
            Function<T, String> categoryExtractor,
            ToDoubleFunction<T> amountExtractor,
            String currency
    ) {
        PieChart chart = new PieChart();
        chart.setTitle(title);

        Map<String, Double> totals = transactions.stream()
                .collect(Collectors.groupingBy(
                        categoryExtractor,
                        Collectors.summingDouble(amountExtractor)
                ));

        totals.forEach((category, sum) -> chart.getData().add(
                new PieChart.Data(category + " (" + format(sum, currency) + ")", sum)
        ));

        return chart;
    }

    private static String format(double amount, String currency) {
        return UIUtils.formatCurrency(amount, currency);
    }
}
