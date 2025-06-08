import java.time.LocalDate;

public class Budget {
    private int budgetId;
    private int userId;
    private String category;
    private double budgetLimit;
    private double spentAmount;
    private String duration;
    private LocalDate startDate;
    private LocalDate endDate;

    // Updated constructor to match all fields
    public Budget(int budgetId, int userId, String category, double budgetLimit, double spentAmount, String duration, LocalDate startDate, LocalDate endDate) {
        this.budgetId = budgetId;
        this.userId = userId;
        this.category = category;
        this.budgetLimit = budgetLimit;
        this.spentAmount = spentAmount;
        this.duration = duration;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters
    public int getBudgetId() { return budgetId; }
    public int getUserId() { return userId; }
    public String getCategory() { return category; }
    public double getBudgetLimit() { return budgetLimit; }
    public double getSpentAmount() { return spentAmount; }
    public String getDuration() { return duration; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }

    // Helper methods
    public double getPercentage() {
        return (budgetLimit > 0) ? (spentAmount / budgetLimit) * 100 : 0;
    }

    public boolean isExceeded() {
        return spentAmount > budgetLimit;
    }

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        if (startDate == null || endDate == null) return true;
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public String getStatus() {
        if (!isActive()) return "Inactive";
        return isExceeded() ? "Exceeded" : "Active";
    }
}



