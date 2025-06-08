

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ScheduledTransactionController {
    private final ScheduledTransactionView view;
    private final List<ScheduledTransaction> scheduledTransactions;
    private final TransactionView mainTransactionController;
    int user_id;

    public ScheduledTransactionController(ScheduledTransactionView view,TransactionView mainTransactionController,int user_id) {
        this.view = view;
        this.user_id=user_id;
        this.mainTransactionController = mainTransactionController;
        this.scheduledTransactions = new ArrayList<>();
        initializeController();
        loadInitialTransaction();
        
    }

    private void initializeController() {
        // Set up button listeners
        view.getAddButton().addActionListener(this::handleAddTransaction);
        view.getEditButton().addActionListener(this::handleEditTransaction);
        view.getDeleteButton().addActionListener(this::handleDeleteTransaction);
        view.getToggleButton().addActionListener(this::handleToggleActive);
        view.getGenerateButton().addActionListener(this::handleGenerateTransactions);
        view.getTimeRangeCombo().addActionListener(this::handleFilterChange);

        // Set up table selection listener
        view.getTransactionTable().getSelectionModel().addListSelectionListener(e -> {
            updateButtonStates();
        });
    }

    private void handleAddTransaction(ActionEvent e) {
    	AddScheduledTransactionDialog dialog = new AddScheduledTransactionDialog((JFrame)SwingUtilities.getWindowAncestor(view),"Add Scheduled Transaction");
        
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            ScheduledTransaction newTransaction = createTransactionFromDialog(dialog);
            scheduledTransactions.add(newTransaction);
            view.updateTransactionView(newTransaction);
            persistTransaction(newTransaction); 
        }
    }

    private void handleEditTransaction(ActionEvent e) {
        int selectedRow = view.getTransactionTable().getSelectedRow();
        if (selectedRow >= 0) {
            ScheduledTransaction selected = scheduledTransactions.get(selectedRow);
            
            EditScheduledTransactionDialog dialog = new EditScheduledTransactionDialog((JFrame)SwingUtilities.getWindowAncestor(view),"Edit Transaction", selected);
            
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                updateTransactionFromDialog(selected, dialog);
                view.updateTransactionView(selected);
                persistTransaction(selected); 
            }
        }
    }

    private void handleDeleteTransaction(ActionEvent e) {
        int selectedRow = view.getTransactionTable().getSelectedRow();
        if (selectedRow >= 0) {
                ScheduledTransaction toRemove = scheduledTransactions.get(selectedRow);
                scheduledTransactions.remove(selectedRow);
                view.removeTransaction(toRemove);
                deleteTransaction(toRemove); // Remove from DB
            
        }
    }

    private void handleToggleActive(ActionEvent e) {
        int selectedRow = view.getTransactionTable().getSelectedRow();
        if (selectedRow >= 0) {
            ScheduledTransaction transaction = scheduledTransactions.get(selectedRow);
            transaction.setActive(!transaction.isActive());
            view.updateTransactionView(transaction);
            persistTransaction(transaction); // Update in DB
        }
    }

    private void handleGenerateTransactions(ActionEvent e) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(1);
        
        
        for (ScheduledTransaction st : scheduledTransactions) {
            if (st.isActive()) {
                List<Transaction> generated = st.generateTransactions(startDate, endDate);
                for (Transaction transaction : generated) {
                mainTransactionController.addTransaction(transaction);
            }
            }
        }
        
        JOptionPane.showMessageDialog(
            view,
            "Generated transactions for the next month",
            "Generation Complete",
            JOptionPane.INFORMATION_MESSAGE
        );
    }


    
    private void handleFilterChange(ActionEvent e) {
        String filter = (String)view.getTimeRangeCombo().getSelectedItem();
        List<ScheduledTransaction> filtered = new ArrayList<>();
        
        switch (filter) {
            case "Active":
                scheduledTransactions.stream()
                    .filter(ScheduledTransaction::isActive)
                    .forEach(filtered::add);
                break;
                
            case "This Month":
                LocalDate today = LocalDate.now();
                scheduledTransactions.stream()
                    .filter(t -> t.getStartDate().getMonth() == today.getMonth())
                    .forEach(filtered::add);
                break;
                
            default:
                filtered.addAll(scheduledTransactions);
        }
        
        view.reloadTransactions(filtered);
    }

    private void updateButtonStates() {
        boolean hasSelection = view.getTransactionTable().getSelectedRow() >= 0;
        view.getEditButton().setEnabled(hasSelection);
        view.getDeleteButton().setEnabled(hasSelection);
        view.getToggleButton().setEnabled(hasSelection);
    }

    // Helper methods
    private ScheduledTransaction createTransactionFromDialog(AddScheduledTransactionDialog dialog) {
        return new ScheduledTransaction(	
        	dialog.getId(),	
            dialog.getDescription(),
            dialog.getCategory(),
            dialog.getAmount(),
            dialog.getTransactionType(),
            dialog.getStartDate(),
            dialog.getEndDate(),
            dialog.getFrequencyDays()
        );
    }

    private void updateTransactionFromDialog(ScheduledTransaction transaction,EditScheduledTransactionDialog dialog) {
        transaction.setDescription(dialog.getDescription());
        transaction.setCategory(dialog.getCategory());
        transaction.setAmount(dialog.getAmount());
        transaction.setTransactionType(dialog.getTransactionType());
        transaction.setStartDate(dialog.getStartDate());
        transaction.setEndDate(dialog.getEndDate());
        transaction.setFrequencyDays(dialog.getFrequencyDays());
    }

   
    private void persistTransaction(ScheduledTransaction transaction) {
    	
    	connection conn=new connection();
    	
    	int id=transaction.getId();
    	String description=transaction.getDescription();
    	String category= transaction.getCategory();
        Double amount= transaction.getAmount();
        String transactionType= transaction.getTransactionType();
        Date startDate= Date.valueOf(transaction.getStartDate());
        Date endDate= transaction.getEndDate() != null ? Date.valueOf(transaction.getEndDate()) : null;
        int frequency= transaction.getFrequencyDays();
        Boolean isActive= transaction.isActive();
        Date next_occurence=Date.valueOf(view.calculateNextOccurrence(transaction));
    	
        
    String query1="INSERT INTO scheduledTransaction(user_id,description, category, amount, type, startDate, endDate, frequency, is_active, next_occurence) VALUES ("+user_id+",'"+description+"','"+category+"',"+amount+",'"+transactionType+"','"+startDate+"','"+endDate+"',"+frequency+","+isActive+",'"+next_occurence+"');";                                           
    
    String query2="UPDATE scheduledTransaction SET description='"+description+"',category='"+category+"', amount="+amount+",type='"+transactionType+"',startDate='"+startDate+"',endDate='"+endDate+"', frequency="+frequency+",next_occurence='"+next_occurence+"',is_active="+isActive+" WHERE transaction_id="+id+";";
    try {
    	
    	if (transaction.getId()==0) {
		int affectedRows = conn.stm.executeUpdate(query1,Statement.RETURN_GENERATED_KEYS);
		
		if (affectedRows > 0) {
		  try (ResultSet res = conn.stm.getGeneratedKeys()) {
              if (res.next()) {
                  transaction.setId(res.getInt(1));
              }
          }
    	}
    }
    	else {
    		int affectedRows = conn.stm1.executeUpdate(query2);
    		if (affectedRows == 0) {
                System.out.println("No rows affected - transaction may not exist");
            }
    	}

	} catch (SQLException e1) {
		
		e1.printStackTrace();
	}
    
    
    }
    
    private void deleteTransaction(ScheduledTransaction transaction) {
        connection conn=new connection();
        int id=transaction.getId();
        
        String query3 = "DELETE FROM scheduledTransaction WHERE transaction_id="+id+";";
        
        try {
        	conn.stm2.executeUpdate(query3);
        }
        catch(SQLException e) {
        	 e.printStackTrace();
        }
    }
    
    private void loadInitialTransaction() {
    try {
        connection conn = new connection();
        
        ResultSet res1 = conn.stm3.executeQuery("SELECT * FROM scheduledTransaction where user_id="+user_id+";");
        
        while (res1.next()) {
        	ScheduledTransaction transaction = new ScheduledTransaction( 
        		res1.getInt("transaction_id"),
                res1.getString("description"),
                res1.getString("category"),
                res1.getDouble("amount"),
                res1.getString("type"),
                res1.getDate("startDate").toLocalDate(),
                res1.getDate("endDate") != null ? res1.getDate("endDate").toLocalDate() : null,
                res1.getInt("frequency")
            );
            transaction.setActive(res1.getBoolean("is_active"));
            scheduledTransactions.add(transaction);
        }
        view.reloadTransactions(scheduledTransactions);
    } catch (SQLException e) {
    	 e.printStackTrace();
    }
    
    } 
}

