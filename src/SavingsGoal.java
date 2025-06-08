

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SavingsGoal {
 
    private int goalId;
    private int userId;
    private String goalName;
    private double targetAmount;
    private double currentAmount;
    private Date targetDate;
    
    public SavingsGoal() {}

    public SavingsGoal(int userId, String goalName, double targetAmount, double currentAmount, Date targetDate) {
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
    }

  
    public int getGoalId() { return goalId; }
    public void setGoalId(int goalId) { this.goalId = goalId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getGoalName() { return goalName; }
    public void setGoalName(String goalName) { this.goalName = goalName; }
    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    public Date getTargetDate() { return targetDate; }
    public void setTargetDate(Date targetDate) { this.targetDate = targetDate; }


    public boolean save() {
    	connection conn=new connection();
       Date target_date=new java.sql.Date(this.targetDate.getTime());
        
   String query = "INSERT INTO SavingsGoals (user_id, goal_name, target_amount, current_amount, target_date) VALUES ("+userId+",'"+goalName+"',"+targetAmount+","+currentAmount+",'"+target_date+"')";     
        
        try  { 
            int affectedRows = conn.stm.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
            if (affectedRows == 0) {
                return false;
            }         
            try (ResultSet generatedKeys = conn.stm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.goalId = generatedKeys.getInt(1);
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
 
    public boolean update() {
    	connection conn=new connection();
        Date target_date=new java.sql.Date(this.targetDate.getTime());
   
      String query = "UPDATE SavingsGoals SET goal_name ='"+goalName+"', target_amount ="+targetAmount+", current_amount = "+currentAmount+", target_date = '"+target_date+"' WHERE goal_id ="+goalId+";";
        try {    
            return conn.stm.executeUpdate(query) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    public boolean delete() {
     connection conn=new connection();
        
        String query = "DELETE FROM SavingsGoals WHERE goal_id ="+goalId+";";
        
        try  {
            
            return conn.stm.executeUpdate(query) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
 
    public boolean addFunds(double amount) {
    	connection conn=new connection();
  
        String query = "UPDATE SavingsGoals SET current_amount = current_amount +"+amount+" WHERE goal_id ="+goalId+";";
        
        try  {
            boolean success = conn.stm.executeUpdate(query) > 0;
            if (success) {
                this.currentAmount += amount;
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static  SavingsGoal findById(int goalId) {
    	connection conn=new connection();
        String query = "SELECT * FROM SavingsGoals WHERE goal_id = "+goalId+";";
        
        try  {
   
            ResultSet resultSet = conn.stm.executeQuery(query);
            
            if (resultSet.next()) {
                SavingsGoal goal = new SavingsGoal();
                goal.setGoalId(resultSet.getInt("goal_id"));
                goal.setUserId(resultSet.getInt("user_id"));
                goal.setGoalName(resultSet.getString("goal_name"));
                goal.setTargetAmount(resultSet.getDouble("target_amount"));
                goal.setCurrentAmount(resultSet.getDouble("current_amount"));
                goal.setTargetDate(resultSet.getDate("target_date"));
                return goal;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    

    public static  List<SavingsGoal> findAllByUser(int userId) {
    	connection conn=new connection();
        
        List<SavingsGoal> goals = new ArrayList<>();
        String query = "SELECT * FROM SavingsGoals WHERE user_id = "+userId+";";
        
        try {
            
            ResultSet resultSet = conn.stm.executeQuery(query);
            
            while (resultSet.next()) {
                SavingsGoal goal = new SavingsGoal();
                goal.setGoalId(resultSet.getInt("goal_id"));
                goal.setUserId(resultSet.getInt("user_id"));
                goal.setGoalName(resultSet.getString("goal_name"));
                goal.setTargetAmount(resultSet.getDouble("target_amount"));
                goal.setCurrentAmount(resultSet.getDouble("current_amount"));
                goal.setTargetDate(resultSet.getDate("target_date"));
                goals.add(goal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goals;
    }
    
    public static boolean deleteById(int goalId) {
    	connection conn=new connection();
        
        String query = "DELETE FROM SavingsGoals WHERE goal_id = "+goalId+";";
        
        try {
     
            return conn.stm.executeUpdate(query) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateById(int goalId, String goalName, double targetAmount, double currentAmount, Date targetDate) {
    	connection conn=new connection();
        Date target_date=new java.sql.Date(targetDate.getTime());
   
      String query = "UPDATE SavingsGoals SET goal_name ='"+goalName+"', target_amount ="+targetAmount+", current_amount = "+currentAmount+", target_date = '"+target_date+"' WHERE goal_id ="+goalId+";";
        try {    
            return conn.stm.executeUpdate(query) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addFundsById(int goalId, double amount) {
    	connection conn=new connection();
    	  
        String query = "UPDATE SavingsGoals SET current_amount = current_amount +"+amount+" WHERE goal_id ="+goalId+";";
        
        try  {
          return conn.stm.executeUpdate(query) > 0;
           
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean addFundsWithTransaction(int goalId, double amount, String description, int userId) {
        connection conn=new connection();
        
        try {
            // 1. Add to savings goal
            String query= "UPDATE SavingsGoals SET current_amount = current_amount +"+amount+" WHERE goal_id ="+goalId+";";
        
            int affected =conn.stm.executeUpdate(query);
            if (affected == 0) {
                return false;
            }
            
            String descriptions= description != null ? description : "Savings Goal Deposit";
            // 2. Create transaction record
            String query1 = "INSERT INTO Transactions (user_id, date, description, category, amount, type) VALUES ("+userId+", CURRENT_DATE, "+descriptions+" , 'Savings', "+amount+", 'Expense')";
         
            return conn.stm1.executeUpdate(query1) > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public double calculateProgress() {
        if (this.targetAmount <= 0) return 0;
        return (this.currentAmount / this.targetAmount) * 100;
    }

    @Override
    public String toString() {
        return goalName + " - $" + currentAmount + "/$" + targetAmount + " by " + targetDate;
    }
}
