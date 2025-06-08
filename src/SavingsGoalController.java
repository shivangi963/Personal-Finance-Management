
import java.util.Date;
import java.util.List;

public class SavingsGoalController {
    private int userId;
    private TransactionView transactionView;

    public SavingsGoalController(int userId,TransactionView transactionView) {
        this.userId = userId;
        this.transactionView= transactionView;
    }

    // Create operations
    public boolean createSavingsGoal(String goalName, double targetAmount, double currentAmount, Date targetDate) {
        SavingsGoal goal = new SavingsGoal(userId, goalName, targetAmount, currentAmount, targetDate);
        return goal.save();
    }

    // Read operations
    public List<SavingsGoal> getUserSavingsGoals() {
        return SavingsGoal.findAllByUser(userId);
    }

    public SavingsGoal findGoalById(int goalId) {
        return SavingsGoal.findById(goalId);
    }

    // Update operations
    public boolean updateSavingsGoal(SavingsGoal goal) {
        return goal.update();
    }

    // Delete operations
    public boolean deleteSavingsGoal(int goalId) {
        return SavingsGoal.deleteById(goalId);
    }

    // Fund operations
    public boolean addToSavingsGoal(int goalId, double amount) {
        return SavingsGoal.addFundsById(goalId, amount);
    }

    // Utility methods
    public double calculateProgress(SavingsGoal goal) {
        return goal.calculateProgress();
    }
    
    public boolean addToSavingsGoal(int goalId, double amount, String description) {
        boolean success = SavingsGoal.addFundsWithTransaction(goalId, amount, description, userId);
        if (success && transactionView != null) {
            transactionView.loadTransactions(); 
        }
        return success;
    }

}