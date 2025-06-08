import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.time.LocalDate;

public class Chart {
	connection con;
    
    public Chart() {
    	  try {
              con= new connection(); 
          } catch (Exception e) {
              e.printStackTrace();
          }
    }  
    
    public Map<String, Map<String, Double>> getCategorySummary(LocalDate startDate, LocalDate endDate, int userId) throws SQLException {
        Map<String, Map<String, Double>> result = new HashMap<>();
        result.put("income", getCategoryData("Income", startDate, endDate, userId));
        result.put("expenses", getCategoryData("Expense", startDate, endDate, userId));
        return result;
    }

    private Map<String, Double> getCategoryData(String type, LocalDate startDate, LocalDate endDate, int userId) throws SQLException {
        Map<String, Double> data = new HashMap<>();
        
        String query = "SELECT category, SUM(amount) as total FROM transactions WHERE type = '"+type+"' AND date BETWEEN '"+startDate+"' AND '"+endDate+"' AND user_id ="+userId+" GROUP BY category";

        try{
            ResultSet res = con.stm.executeQuery(query);
            while (res.next()) {
                data.put(res.getString("category"), 
                res.getDouble("total"));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }  

 
    
}
