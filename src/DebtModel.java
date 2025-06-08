
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DebtModel {
	connection conn;
	private int userId;
	
	 public DebtModel(int userId) {
	        
	        this.userId = userId;
	    }
	
    public static class Debt {
        private int id;
        private String name;
        private double totalAmount;
        private double paidAmount;
        private String dueDate;
        private double interestRate;

        public Debt(int id, String name, double totalAmount, double paidAmount, String dueDate, double interestRate) {
            this.id = id;
            this.name = name;
            this.totalAmount = totalAmount;
            this.paidAmount = paidAmount;
            this.dueDate = dueDate;
            this.interestRate = interestRate;
        }

       
        public int getId() { return id; }
        public String getName() { return name; }
        public double getTotalAmount() { return totalAmount; }
        public double getPaidAmount() { return paidAmount; }
        public String getDueDate() { return dueDate; }
        public double getInterestRate() { return interestRate; }
        public double getRemainingAmount() { return totalAmount - paidAmount; }
        public int getProgressPercentage() { return (int) ((paidAmount / totalAmount) * 100); }
    }

    public List<Debt> getDebts() {
    	conn = new connection();
        List<Debt> debts = new ArrayList<>();
        String query1 = "SELECT * FROM debts WHERE user_id ="+userId+";";        
        try  {
        	
            ResultSet res1 = conn.stm.executeQuery(query1);
            
            while (res1.next()) {
                debts.add(new Debt(
                    res1.getInt("id"),
                    res1.getString("name"),
                    res1.getDouble("total_amount"),
                    res1.getDouble("paid_amount"),
                    res1.getDate("due_date").toString(),
                    res1.getDouble("interest_rate")
                ));
            }
        } catch (SQLException e) {
       	 e.printStackTrace();
       }
        return debts;
    }
    public List<Debt> getCredits() {
    	conn = new connection();
        List<Debt> credits = new ArrayList<>();
        String query2 = "SELECT * FROM credits WHERE user_id ="+userId+";";        
        try  {
        	
            ResultSet res2 = conn.stm.executeQuery(query2);
            
            while (res2.next()) {
                credits.add(new Debt(
                    res2.getInt("id"),
                    res2.getString("name"),
                    res2.getDouble("total_amount"),
                    res2.getDouble("paid_amount"),
                    res2.getDate("due_date").toString(),
                    res2.getDouble("interest_rate")
                ));
            }
        } catch (SQLException e) {
       	 e.printStackTrace();
       }
        return credits;
    }

    public double getTotalDebt() throws SQLException {
        String query3 = "SELECT SUM(total_amount - paid_amount) AS total FROM debts WHERE user_id = "+userId+";";
        try (   ResultSet res = conn.stm.executeQuery(query3);) {
            return res.next() ? res.getDouble("total") : 0.0;
        }
    }

    public double getTotalCredit() throws SQLException {
        String query4 = "SELECT SUM(total_amount - paid_amount) AS total FROM credits WHERE user_id ="+userId+";";
        try (ResultSet res = conn.stm.executeQuery(query4);) {           
            return res.next() ? res.getDouble("total") : 0.0;
        }
    }

    public double getNetPosition() throws SQLException {
        return getTotalCredit() - getTotalDebt();
    } 
    
    public boolean addDebt (String name, double totalAmount, String dueDate, double interestRate) {
    	int affectedRows=0;
    	
        String query = "INSERT INTO debts (user_id, name, total_amount, due_date, interest_rate) VALUES ("+userId+",'"+name+"',"+totalAmount+",'"+dueDate+"',"+interestRate+");";                                                            
        try{
          affectedRows = conn.stm.executeUpdate(query);
          if(affectedRows==0) {
        	  return false;
          }
    } catch (SQLException e) {
      	 e.printStackTrace();
      }
       return true ;
    }
    
    public boolean addCredit(String name, double totalAmount, String dueDate, double interestRate) {
    	int affectedRows=0;
        String query = "INSERT INTO credits (user_id, name, total_amount, due_date, interest_rate) VALUES ("+userId+",'"+name+"',"+totalAmount+",'"+dueDate+"',"+interestRate+");";
        try{
            affectedRows = conn.stm.executeUpdate(query);
            if(affectedRows==0) {
          	  return false;
            }
      } catch (SQLException e) {
        	 e.printStackTrace();
        }
         return true ;
      }  
    
    public boolean updateDebtPayment(int debtId, double paymentAmount)  {
    	int affectedRows=0;
        String query = "UPDATE debts SET paid_amount = paid_amount + "+paymentAmount+" WHERE id ="+debtId+" AND user_id ="+userId+";";
        try {           
        	affectedRows = conn.stm.executeUpdate(query);
            if(affectedRows==0) {
          	  return false;
            }
    } catch (SQLException e) {
   	 e.printStackTrace();
   }
        return true;
  }

    public boolean updateCreditPayment(int creditId, double paymentAmount)  {
     	int affectedRows=0;
        String query = "UPDATE credits SET paid_amount = paid_amount + "+paymentAmount+" WHERE id ="+creditId+" AND user_id ="+userId+";";
        try {           
        	affectedRows = conn.stm.executeUpdate(query);
            if(affectedRows==0) {
          	  return false;
            }
    } catch (SQLException e) {
   	 e.printStackTrace();
   }
        return true;
    }


    public boolean deleteDebt(int debtId) throws SQLException {
    	int affectedRows=0;
        String query = "DELETE FROM debts WHERE id ="+debtId+" AND user_id ="+userId+";";
        try  {
            affectedRows= conn.stm.executeUpdate(query) ;
        }catch (SQLException e) {
          	 e.printStackTrace();
        }
        return affectedRows>0;
    }

    public boolean deleteCredit(int creditId) throws SQLException {
    	int affectedRows=0;
        String query = "DELETE FROM credits WHERE id ="+creditId+" AND user_id ="+userId+";";
        try  {
            affectedRows= conn.stm.executeUpdate(query) ;
        }catch (SQLException e) {
          	 e.printStackTrace();
        }
        return affectedRows>0;
    }

    
}
