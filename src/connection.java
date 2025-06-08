
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class connection {
	private static final String url = "jdbc:mysql://localhost:3306/financeTracker";
    private static final String username = "root";
    private static final String password = "Wishes@1234";
    
    Connection con;
   
    Statement stm;
    Statement stm1;
    Statement stm2;
    Statement stm3;
    
    connection(){
    try{
        Class.forName("com.mysql.cj.jdbc.Driver");
    }catch (ClassNotFoundException e){
        System.out.println(e.getMessage());
    }
    
		try {
			con = DriverManager.getConnection(url, username, password);
			 stm=con.createStatement();
			 stm1=con.createStatement();
			 stm2=con.createStatement();
			 stm3=con.createStatement();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
    
    }
}
