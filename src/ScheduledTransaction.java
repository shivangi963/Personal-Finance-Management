

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScheduledTransaction {
	private int id;
    private String description;
    private String category;
    private double amount;
    private String type; // "Income" or "Expense"
    private LocalDate startDate;
    private LocalDate endDate;
    private int frequencyDays;
    private boolean active;
 //   private String transactionId; // Unique identifier

    public ScheduledTransaction(int id,String description, String category, double amount,  String type, LocalDate startDate, LocalDate endDate, int frequencyDays) {
    	this.id=id;
        this.description = Objects.requireNonNull(description);
        this.category = Objects.requireNonNull(category);
        this.amount = Objects.requireNonNull(amount);
        this.type = Objects.requireNonNull(type);
        this.startDate = Objects.requireNonNull(startDate);
        this.endDate = Objects.requireNonNull(endDate);
        this.frequencyDays = frequencyDays;
        this.active = true;
 
    }

	// Primary business logic method
    public List<Transaction> generateTransactions(LocalDate fromDate, LocalDate toDate) {
        List<Transaction> transactions = new ArrayList<>();
        
        if (!active || startDate.isAfter(toDate) || endDate.isBefore(fromDate)) {
            return transactions;
        }

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            if (!current.isBefore(fromDate) && !current.isAfter(toDate)) {
                transactions.add(new Transaction(   
                	id,	
                    current,
                    description,
                    category,
                    amount,
                    type
                ));
            }
            current = current.plusDays(frequencyDays);
        }
        return transactions;
    }


    // Helper method

    // Getters
    public int getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransactionType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getFrequencyDays() {
        return frequencyDays;
    }

    public boolean isActive() {
        return active;
    }


    // Setters with validation
    
    public void setId(int id) {
    	this.id=id;   	
    }
    
    public void setDescription(String description) {
        this.description = Objects.requireNonNull(description, "Description cannot be null");
    }

    public void setCategory(String category) {
        this.category = Objects.requireNonNull(category, "Category cannot be null");
    }

    public void setAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.amount = amount;
    }

    public void setTransactionType(String type) {
        if (!type.equals("Income") && !type.equals("Expense")) {
            throw new IllegalArgumentException("Type must be 'Income' or 'Expense'");
        }
        this.type = type;
    }

    public void setStartDate(LocalDate startDate) {
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        this.startDate = Objects.requireNonNull(startDate);
    }

    public void setEndDate(LocalDate endDate) {
        if (startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        this.endDate = Objects.requireNonNull(endDate);
    }

    public void setFrequencyDays(int frequencyDays) {
        if (frequencyDays <= 0) {
            throw new IllegalArgumentException("Frequency must be at least 1 day");
        }
        this.frequencyDays = frequencyDays;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Utility methods
    public boolean shouldRunOn(LocalDate date) {
        return active &&
               !date.isBefore(startDate) &&
               !date.isAfter(endDate) &&
               ChronoUnit.DAYS.between(startDate, date) % frequencyDays == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduledTransaction that = (ScheduledTransaction) o;
        return id==that.id;
    }


    @Override
    public String toString() {
        return String.format(
            "ScheduledTransaction[%s, %s, %.2f, %s to %s, every %d days]",
            description, category, amount, startDate, endDate, frequencyDays
        );
    }
}




