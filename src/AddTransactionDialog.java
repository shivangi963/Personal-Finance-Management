

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public class AddTransactionDialog extends JDialog {
	private int id;
    private JTextField descriptionField;
    private JTextField amountField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> typeCombo;
    private JSpinner dateSpinner;
    private boolean confirmed = false;
    LocalDate transactionDate;
    Object dateValue;
    

    public AddTransactionDialog(JFrame parent,int user_id) {
        super(parent, "Add New Transaction", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
	setUndecorated(true);
        setLayout(new GridLayout(6, 2, 10, 10));

        // Date field
        add(new JLabel("Date:"));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setValue(new Date());
        add(dateSpinner);
        dateValue = dateSpinner.getValue();
        if (dateValue == null) {
            throw new IllegalArgumentException("Date cannot be empty");
        }

        // Description field
        add(new JLabel("Description:"));
        descriptionField = new JTextField();
        add(descriptionField);

        // Amount field
        add(new JLabel("Amount:"));
        amountField = new JTextField();
        add(amountField);

        // Category dropdown
        add(new JLabel("Category:"));
        categoryCombo = new JComboBox<>(new String[]{"Food", "Transport", "Entertainment", "Bills", "Housing", "Shopping","Healthcare", "Salary", "Others"});
        categoryCombo.setEditable(true);
        add(categoryCombo);

        // Type dropdown (Income/Expense)
        add(new JLabel("Type:"));
        typeCombo = new JComboBox<>(new String[]{"Expense", "Income"});
        add(typeCombo);

        // Buttons
        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(76,175,80));
        addButton.setForeground(Color.black);
        addButton.setFocusable(false);
        
        
        addButton.addActionListener(e -> {
            confirmed = true;
            
            
        LocalDate transactionDate = ((java.util.Date) dateValue).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
             String description= descriptionField.getText();
           String category= (String) categoryCombo.getSelectedItem();
          Double amount= Double.parseDouble(amountField.getText());
          String type = (String) typeCombo.getSelectedItem();

            connection conn=new connection();
  String query="insert into Transactions(user_id, amount, type, category, date, description) values("+user_id+","+amount+",'"+type+"','"+category+"','"+transactionDate+"','"+description+"'); ";    
           
  try {
		int affectedRows = conn.stm.executeUpdate(query);	
		 if (affectedRows > 0) {
			 dispose();
           } 
           else {
        	   JOptionPane.showMessageDialog(null,"Invalid input.Try again");
           }
	} catch (SQLException e1) {
		
		e1.printStackTrace();
	}
            
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(76,175,80));
        cancelButton.setForeground(Color.black);
        cancelButton.setFocusable(false);
        
        cancelButton.addActionListener(e -> dispose());

        add(addButton);
        add(cancelButton);
    }
    public boolean wasConfirmed() {
        return confirmed;
    }
    
    
    
    public Transaction getTransaction() {
        if (!confirmed) return null;
        
        try {
            return new Transaction(
            		0,
         transactionDate = ((java.util.Date) dateValue).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                descriptionField.getText(),
                (String) categoryCombo.getSelectedItem(),
                Double.parseDouble(amountField.getText()),
                (String) typeCombo.getSelectedItem()
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
 

}
