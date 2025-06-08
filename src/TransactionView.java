
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TransactionView extends JPanel {
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton deleteButton;
    private JComboBox<String> categoryFilter;
    private int userId;
    private List<Transaction> transactions;
    int id;

    public TransactionView(int userId) {
        this.userId = userId;
        this.transactions = new ArrayList<>();
        initializeUI();
        loadTransactions();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model
        String[] columns = {"Date", "Description", "Category", "Amount", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 3 -> Double.class;  
                    default -> String.class;
                };
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table
        transactionTable = new JTable(tableModel);
        transactionTable.setAutoCreateRowSorter(true);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setDefaultRenderer(Double.class, new CurrencyRenderer());
        JScrollPane scrollPane = new JScrollPane(transactionTable);

        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Category:"));
        categoryFilter = new JComboBox<>();
        categoryFilter.addItem("All Categories");
        categoryFilter.addActionListener(e ->filterTransactions());
        filterPanel.add(categoryFilter);
        

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Add Transaction");
        addButton.setBackground(new Color(76,175,80));
        addButton.setForeground(Color.black);
        addButton.setFocusable(false);
        deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(76,175,80));
        deleteButton.setForeground(Color.black);
        deleteButton.setFocusable(false);
        
        addButton.addActionListener(this::showAddTransactionDialog);
        deleteButton.addActionListener(this::deleteSelectedTransaction);
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        // Add components to main panel
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    
    public void addTransaction(Transaction transaction) {
         tableModel.addRow(transaction.toTableRow());
     }


    public void loadTransactions() {
        tableModel.setRowCount(0);
        transactions.clear();
        connection con =new connection();
        
        try {
            String query1 = "SELECT transaction_id, date, description, category, amount, type FROM Transactions WHERE user_id ="+userId+" ORDER BY date DESC ;";
            
         
            Set<String> categories = new HashSet<>();
            categories.add("All Categories");
            ResultSet res = con.stm.executeQuery(query1);
            
            while (res.next()) {
                int id= res.getInt("transaction_id");
                LocalDate date = res.getDate("date").toLocalDate();
                String description = res.getString("description");
                String category = res.getString("category");
                double amount = res.getDouble("amount");
                String type = res.getString("type");
                
                transactions.add(new Transaction(id, date, description, category, amount, type));
                categories.add(category);
                
                tableModel.addRow(new Object[]{
                    date.format(DateTimeFormatter.ISO_DATE),
                    description,
                    category,
                    amount,
                    type 
                });
            }
            
            // Update category filter
            categoryFilter.removeAllItems();
            categoryFilter.addItem("All Categories");
            
            for (String category : categories) {
                if (!category.equalsIgnoreCase("All Categories")) {
                    categoryFilter.addItem(category);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading transactions: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        public CurrencyRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
        }
        
        @Override
        public void setValue(Object value) {
            if (value instanceof Number) {
                setText(String.format("$%.2f", ((Number)value).doubleValue()));
            } else {
                setText(value != null ? value.toString() : "");
            }
        }
    }

    private void showAddTransactionDialog(ActionEvent e) {
        AddTransactionDialog dialog = new AddTransactionDialog((JFrame)SwingUtilities.getWindowAncestor(this), userId);
        dialog.setVisible(true);
        
        if (dialog.wasConfirmed()) {
            loadTransactions(); // Refresh the table
        }
    }

    private void deleteSelectedTransaction(ActionEvent e) {
        int selectedRow = transactionTable.getSelectedRow();      	
    	int modelRow = transactionTable.convertRowIndexToModel(selectedRow);
    	
        if (selectedRow >= 0 && modelRow < transactions.size()) {   
        	Transaction transaction = transactions.get(modelRow);
        	int id=transaction.getId();
        	
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this transaction?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            connection con = new connection();
            
            if (confirm == JOptionPane.YES_OPTION) { 
                 
                try { 
                    String query2 = "DELETE FROM Transactions WHERE transaction_id ="+id+";";
                    
                    int affectedRows = con.stm1.executeUpdate(query2);
                    if (affectedRows > 0) {
                        loadTransactions(); // Refresh the table 
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Error deleting transaction",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a transaction to delete",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void filterTransactions() {
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) transactionTable.getRowSorter();
        
        if (selectedCategory == null || "All Categories".equals(selectedCategory)) {
            // Show all transactions
            sorter.setRowFilter(null);
        } else {
            // Filter by selected category (column 2 is the category column)
            RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter("^" + selectedCategory + "$", 2);
            sorter.setRowFilter(rowFilter);
        }
    }
    
    
    public List<Transaction> getTransactionsForExport(String period) throws Exception {
    	connection conn =new connection();
    	List<Transaction> transaction = new ArrayList<>();
    	
        YearMonth yearMonth = YearMonth.parse(period, DateTimeFormatter.ofPattern("MMMM yyyy"));
        LocalDate startDay=yearMonth.atDay(1);
        LocalDate endDay =yearMonth.plusMonths(1).atDay(1);
        
        String query = "SELECT * FROM Transactions WHERE user_id ="+userId+" AND date >='"+Date.valueOf(startDay)+"' AND date <'"+Date.valueOf(endDay)+"' ORDER BY date DESC";
        
        try {       
            
            ResultSet res = conn.stm.executeQuery(query);
            while (res.next()) {
                
                int id= res.getInt("transaction_id");
                LocalDate date = res.getDate("date").toLocalDate();
                String description = res.getString("description");
                double amount = res.getDouble("amount");
                String category = res.getString("category");
                String type = res.getString("type");
                transaction.add(new Transaction(id, date, description, category, amount, type));
            }
            
        }
        catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading transactions: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        return transaction;
    }
    
    
    public Map<String, Double> getSummaryForExport(String period) throws Exception {
        connection conn =new connection();
        Map<String, Double> summary= new HashMap<>();;
    	
        YearMonth yearMonth= YearMonth.parse(period, DateTimeFormatter.ofPattern("MMMM yyyy"));
        LocalDate startDay=yearMonth.atDay(1);
        LocalDate endDay =yearMonth.plusMonths(1).atDay(1);
        
        String query = "SELECT category, SUM(amount) as total FROM Transactions WHERE user_id  ="+userId+" AND date >='"+Date.valueOf(startDay)+"' AND date <'"+Date.valueOf(endDay)+"' GROUP BY category";
        
        try  {

            ResultSet res = conn.stm.executeQuery(query);
            while (res.next()) {
                summary.put(res.getString("category"), res.getDouble("total"));
            }
           
        }catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading transactions: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        return summary;
    }
}
    

