

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BudgetController {
    private BudgetView view;
    private int userId;

    public BudgetController(int userId, BudgetView view) {
        if (view == null) {
            throw new IllegalArgumentException("BudgetView cannot be null");
        }
        this.view = view;
        this.userId = userId;    
        // Initialize the view with controller reference
        this.view.setController(this);
        
        // Load budgets after everything is set up
        loadBudgets();
    }

    public void loadBudgets() {
        try {
            List<Budget> budgets = getUserBudgets();
            view.displayBudgets(budgets);
        } catch (SQLException e) {
            showError("Error loading budgets: " + e.getMessage());
        }
    }

    private List<Budget> getUserBudgets() throws SQLException {
    	connection conn=new connection();
        List<Budget> budgets = new ArrayList<>();
        
        String query = "SELECT b.budget_id, b.category, b.budget_limit, b.duration, " +
                      "b.startDate, b.endDate, COALESCE(SUM(t.amount), 0) as spent_amount " +
                      "FROM Budgets b LEFT JOIN Transactions t ON " +
                      "b.user_id = t.user_id AND b.category = t.category " +
                      "WHERE b.user_id = "+userId+" GROUP BY b.budget_id, b.category, b.budget_limit, b.duration, b.startDate, b.endDate";
        
        try  {
           
            ResultSet res = conn.stm.executeQuery(query);
            
            while (res.next()) {
                budgets.add(new Budget(
                    res.getInt("budget_id"),
                    userId,
                    res.getString("category"),
                    res.getDouble("budget_limit"),
                    res.getDouble("spent_amount"),
                    res.getString("duration"),
                    res.getDate("startDate") != null ?res.getDate("startDate").toLocalDate() : null,
                    res.getDate("endDate") != null ?res.getDate("endDate").toLocalDate() : null
                ));
            }
        }  catch (SQLException e1) {  		
    		e1.printStackTrace();
    	}
        return budgets;
    }

    public void addBudget(String category, double limit, String duration, LocalDate startDate, LocalDate endDate) {
    	connection conn =new connection();
        try {
            String query = "INSERT INTO Budgets (user_id, category, budget_limit, duration, startDate, endDate)VALUES ("+userId+",'"+category+"',"+limit+",'"+duration+"','"+startDate+"','"+endDate+"');";
            
            	int affectedRows=conn.stm.executeUpdate(query);
                loadBudgets();
           
        } catch (SQLException e) {
            showError("Error adding budget: " + e.getMessage());
        }
    }

    
    public void updateBudget(int budgetId, double newLimit, String duration,LocalDate startDate, LocalDate endDate) {
        connection conn =new connection();
    	try {
            String query = "UPDATE Budgets SET budget_limit ="+newLimit+", duration ='"+duration+"', startDate ='"+startDate+"', endDate = '"+endDate+"' WHERE budget_id = "+budgetId+";";
          
                conn.stm.executeUpdate(query);
                loadBudgets();
            
        } catch (SQLException e) {
            showError("Error updating budget: " + e.getMessage());
        }
    }
    
    public void onEditButtonClicked(Budget budget) {
        view.showEditBudgetDialog(budget);
    }

    public void checkForBudgetExceed(String category, double amount) {
        try {
            List<Budget> budgets = getUserBudgets();
            for (Budget budget : budgets) {
                if (budget.getCategory().equalsIgnoreCase(category) && budget.isActive()) {
                    if (budget.getSpentAmount() + amount > budget.getBudgetLimit()) {
                        view.showBudgetExceedWarning(budget, amount);
                    }
                    break;
                }
            }
        } catch (SQLException e) {
            showError("Error checking budget: " + e.getMessage());
        }
    }
    
    private Budget getBudgetById(int budgetId) throws SQLException {
    	
    	connection conn=new connection();
        String query = "SELECT * FROM Budgets WHERE budget_id = "+budgetId+";";
        try{
            
            ResultSet res = conn.stm.executeQuery(query);
            if (res.next()) {
                return new Budget(
                    res.getInt("budget_id"),
                    res.getInt("user_id"),
                    res.getString("category"),
                    res.getDouble("budget_limit"),
                    0, // spent amount not needed for update
                    res.getString("duration"),
                    res.getDate("startDate") != null ? res.getDate("startDate").toLocalDate() : null,
                    res.getDate("endDate") != null ? res.getDate("endDate").toLocalDate() : null
                );
            }
        }catch (SQLException e1) {  		
    		e1.printStackTrace();
    	}
        return null;
    }
    
    public void updateBudgetLimit(int budgetId, double newLimit) {
        try {
            // First get the existing budget to preserve other fields
            Budget budget = getBudgetById(budgetId);
            if (budget != null) {
                updateBudget(budgetId, newLimit, budget.getDuration(), 
                            budget.getStartDate(), budget.getEndDate());
            }
        } catch (SQLException e) {
            showError("Error updating budget limit: " + e.getMessage());
        }
    }
    
    
    public void deleteBudget(int budgetId) {
    	connection conn=new connection();
        try {
            String query = "DELETE FROM Budgets WHERE budget_id ="+budgetId+" AND user_id ="+userId+";";
           
                int rowsAffected = conn.stm.executeUpdate(query);
                
                if (rowsAffected > 0) {
                    loadBudgets();
                } else {
                    JOptionPane.showMessageDialog(view, "Budget not found or not deleted",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error deleting budget: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
}


