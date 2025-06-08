

import java.time.LocalDate;

public class Transaction {
	private final int id;
    private LocalDate date;
    private String description;
    private String category;
    private double amount;
    private String type; // "Income" or "Expense"


    public Transaction(int id,LocalDate date, String description, String category, double amount, String type) {
    	this.id=id;
        this.date = date;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.type = type;
    }

    public Transaction(int id,String description, String category, double amount, LocalDate date) {
    	this.id=id;
    	this.description=description;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }


	// Getters
    public int getId() { 
    	return id; 
    	}
    public LocalDate getDate() { 
    	return date;
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
    public String getType() {
    	return type;
    	}

    // For table display
    public Object[] toTableRow() {
        return new Object[]{
            date.toString(),
            description,
            category,
            String.format("%.2f", Double.valueOf(amount)),
            type
        };
    }
    
    
}
