import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class login extends JFrame implements ActionListener {
	RoundedPanel panel;
	JLabel label1, label2, label3,label4;

   
    RoundButton button1,button2;
    RoundedTextField textField1;
    RoundPasswordField passwordField1;
    
    login(){
    	
    	ImageIcon imge1 = new ImageIcon(getClass().getClassLoader().getResource("icon/img2.png"));
        Image imge2 = imge1.getImage().getScaledInstance(100,100,Image.SCALE_DEFAULT);
        JLabel imge = new JLabel(new ImageIcon(imge2));
        imge.setBounds(440,40,100,100);
        add(imge);
        
        
        
        label1 = new JLabel("USER AUTHENTICATION");
        label1.setForeground(Color.black);
        label1.setFont(new Font("AvantGarde", Font.BOLD, 26));
        label1.setBounds(320,160,325,40);
        add(label1);

        label2 = new JLabel("Email:");
        label2.setFont(new Font("Ralway", Font.PLAIN,18 ));
        label2.setForeground(Color.black);
        label2.setBounds(275,200,375,30);
        add(label2);

        textField1= new RoundedTextField(25);
        textField1.setBounds(275,230,375,30);
        textField1.setBackground(Color.LIGHT_GRAY);
        textField1.setFont(new Font("Arial", Font.BOLD,14));
        add(textField1);

        label3 = new JLabel("Password: ");
        label3.setFont(new Font("Ralway", Font.PLAIN, 18));
        label3.setForeground(Color.black);
        label3.setBounds(275,260,375,30);
        add(label3);

        passwordField1 = new  RoundPasswordField(80);
        passwordField1.setBounds(275,290,375,30);
        passwordField1.setBackground(Color.LIGHT_GRAY);
        passwordField1.setFont(new Font("Arial", Font.BOLD, 14));
        add(passwordField1);
        
        button1 = new RoundButton("SIGN IN");
        button1.setFont(new Font("Arial", Font.BOLD,15));
        button1.setForeground(Color.WHITE);
        button1.setBackground(new Color(76, 175, 80));
        button1.setBounds(250,345,460,25);
        button1.setOpaque(false);
        button1.addActionListener(this);
        add(button1);
        
        
        panel =new RoundedPanel();
        panel.setBackground(Color.WHITE);
        panel.setBounds(230, 150, 500, 235);
        add(panel);
        
        label4 = new JLabel("NEW USER !");
        label4.setFont(new Font("Ralway", Font.BOLD, 13));
        label4.setForeground(Color.BLACK);
        label4.setBounds(440,420,80,30);
        add(label4);
        
        button2 = new RoundButton("REGISTER");
        button2.setFont(new Font("Arial", Font.BOLD,15));
        button2.setForeground(Color.BLACK);
        button2.setBackground(new Color(76, 175, 80));
        button2.setBounds(405,450,150,40);
        button2.setOpaque(false);
       button2.addActionListener(this);
        add(button2);
        
    	
    	ImageIcon img1 = new ImageIcon(getClass().getClassLoader().getResource("icon/white.jpg"));
        Image img2 = img1.getImage().getScaledInstance(1000,600,Image.SCALE_DEFAULT);
        JLabel img = new JLabel(new ImageIcon(img2));
        img.setBounds(0,0,1000,600);
        add(img);

    	
    	    
    	    setLayout(null);
	        setSize(1000,550);
	        setLocation(250,125);
	        setUndecorated(true);
	        setVisible(true);
	
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==button1) {
			connection con=new connection();
			
			String email=textField1.getText();
			String password=passwordField1.getText();
			

			String userid_query="select user_id from users where email = '"+email+"' and password = '"+password+"';";
			
			try {
	
				ResultSet res1=con.stm.executeQuery(userid_query);

				
				if(res1.next()){
	                int user_id=res1.getInt("user_id");
	                
	                new dashboard(user_id).setVisible(true);
	                setVisible(false);   
	            }else {
				    JOptionPane.showMessageDialog(null,"Incorrect Email or Password");
				}
				
			} catch (SQLException e1) { 
				e1.printStackTrace();
			}			
		}
		if(e.getSource()==button2) {
			setVisible(false);
			new signup();			
		}
	}	
	
    
    public static void main(String[] args) {
    	new login();
    }

}
