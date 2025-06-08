import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class signup  extends JFrame implements ActionListener{
	
	JLabel label1,label2,label3,label4,label5,label6,label7;
	RoundButton button1,button2;
	RoundedTextField textField1,textField2;
	RoundPasswordField passwordField1,passwordField2;
	
	signup(){
		
        label1 = new JLabel("SIGN UP...");
        label1.setForeground(Color.black);
        label1.setFont(new Font("AvantGarde", Font.PLAIN, 30));
        label1.setBounds(265,50,200,40);
        add(label1);

        label2 = new JLabel("Already have an account?");
        label2.setForeground(Color.black);
        label2.setFont(new Font("AvantGarde", Font.PLAIN, 18));
        label2.setBounds(180,100,250,20);
        add(label2);
        
        button1 = new RoundButton("Login");
        button1.setFont(new Font("AvantGarde", Font.PLAIN,16));
        button1.setForeground(Color.black);
        button1.setBackground(new Color(76, 175, 80));
        button1.setBounds(410,100,80,25);
        button1.setOpaque(false);
        button1.addActionListener(this);
        add(button1);
        
        label3 = new JLabel("Email:");
        label3.setFont(new Font("Ralway", Font.PLAIN,18 ));
        label3.setForeground(Color.black);
        label3.setBounds(165,160,375,30);
        add(label3);

        textField1= new RoundedTextField(25);
        textField1.setBounds(165,190,375,40);
        textField1.setBackground(Color.LIGHT_GRAY);
        textField1.setFont(new Font("Arial", Font.BOLD,16));
        add(textField1);
        
        label4 = new JLabel("User Name:");
        label4.setFont(new Font("Ralway", Font.PLAIN,18 ));
        label4.setForeground(Color.black);
        label4.setBounds(165,260,375,30);
        add(label4);

        textField2= new RoundedTextField(25);
        textField2.setBounds(165,290,375,40);
        textField2.setBackground(Color.LIGHT_GRAY);
        textField2.setFont(new Font("Arial", Font.BOLD,16));
        add(textField2);
		
        label5 = new JLabel("Password: ");
        label5.setFont(new Font("Ralway", Font.PLAIN, 18));
        label5.setForeground(Color.black);
        label5.setBounds(165,350,375,30);
        add(label5);

        passwordField1 = new  RoundPasswordField(10);
        passwordField1.setBounds(165,380,375,40);
        passwordField1.setBackground(Color.LIGHT_GRAY);
        passwordField1.setFont(new Font("Arial", Font.BOLD, 16));
        add(passwordField1);

        
        label6 = new JLabel(" Confirm Password: ");
        label6.setFont(new Font("Ralway", Font.PLAIN, 18));
        label6.setForeground(Color.black);
        label6.setBounds(165,440,375,30);
        add(label6);

        passwordField2 = new  RoundPasswordField(10);
        passwordField2.setBounds(165,470,375,40);
        passwordField2.setBackground(Color.LIGHT_GRAY);
        passwordField2.setFont(new Font("Arial", Font.BOLD, 16));
        add(passwordField2);
        
        label7 = new JLabel("Password is not the same");
        label7.setFont(new Font("Ralway", Font.PLAIN, 12));
        label7.setForeground(Color.red);
        label7.setBounds(400,500,375,30);
        label7.setVisible(false);
        add(label7);
        
        button2 = new RoundButton("SIGN UP");
        button2.setFont(new Font("AvantGarde", Font.PLAIN,20));
        button2.setForeground(Color.black);
        button2.setBackground(new Color(76, 175, 80));
        button2.setBounds(250,560,200,30);
        button2.setOpaque(false);
        button2.addActionListener(this);
        add(button2);
		
		
	    getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        setSize(750,750);
        setUndecorated(true);
        setLocation(380,30);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
		
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String email=textField1.getText();
		String username=textField2.getText();
		String password=passwordField1.getText();
		
		if(e.getSource()==button1) {
			setVisible(false);
			new login();
		}
		
		
		if(e.getSource()==button2) {
			
			char[] password1 = passwordField1.getPassword();
	        char[] confirmPassword = passwordField2.getPassword();
	        
	        if (!java.util.Arrays.equals(password1, confirmPassword)) {
	        	label7.setVisible(true);
	            passwordField1.setText("");
	            passwordField2.setText("");
	            return;
	        }
			
        		connection con=new connection();
    		
        		
    			String query= "INSERT INTO users (username, password, email) VALUES ('"+username+"','"+password+"','"+email+"');";
    			
    			
    			try {
    				int affectedRows = con.stm.executeUpdate(query);	
    				 if (affectedRows > 0) {
    				        new login();
    			            setVisible(false);
    		             } 
    		             else {
    		            	 textField1.setText("");
    		            	 textField2.setText("");
    		            	 passwordField1.setText("");
    		            	 passwordField2.setText("");
    		             	
    		             }
    			} catch (SQLException e1) {
    				
    				e1.printStackTrace();
    			}	
		}	
	}
}
