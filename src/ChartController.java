import java.time.*;
import java.util.Map;

public class ChartController {
    private final Chart chart;

    public ChartController(Chart chart) {
        this.chart= chart;
    }

    public Map<String, Map<String, Double>> getCategoryData(String timePeriod, int userId) throws Exception {
    	 if (timePeriod.startsWith("custom")) {    
    	        String[] dates = timePeriod.substring(7).split("_");
    	        LocalDate startDate = LocalDate.parse(dates[0]);
    	        LocalDate endDate = LocalDate.parse(dates[1]);
    	        return chart.getCategorySummary(startDate, endDate, userId);
    	    } else {
        LocalDate[] dates = calculateDateRange(timePeriod);
        return chart.getCategorySummary(dates[0], dates[1], userId);
    	    }
    }

    public Map<String, Map<String, Double>> getCustomDateRangeData(LocalDate startDate, LocalDate endDate, int userId) throws Exception {
        return chart.getCategorySummary(startDate, endDate, userId);
    }

    private LocalDate[] calculateDateRange(String timePeriod) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate = today;

        switch (timePeriod.toLowerCase()) {
            case "last_7": startDate = today.minusDays(7); break;
            case "last_30": startDate = today.minusDays(30); break;
            case "last_90": startDate = today.minusDays(90); break;
            case "this_month": startDate = today.withDayOfMonth(1); break;
            case "last_month":
                startDate = today.minusMonths(1).withDayOfMonth(1);
                endDate = today.withDayOfMonth(1).minusDays(1);
                break;
            case "this_year": startDate = today.withDayOfYear(1); break;
            default: startDate = today.minusDays(30);
        }

        return new LocalDate[]{startDate, endDate};
    }
}
