
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScheduledTransactionView extends JPanel {
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton generateButton;
    private JButton toggleButton;
    private JButton editButton;
    private JButton deleteButton;
    private JComboBox<String> timeRangeCombo;

    public ScheduledTransactionView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model
        String[] columns = {
            "Description", 
            "Category", 
            "Amount", 
            "Type",
            "Start Date", 
            "End Date", 
            "Frequency", 
            "Active",
            "Next Occurrence"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 2 -> Double.class;  // Amount
                    case 7 -> Boolean.class; // Active
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
        transactionTable.setFillsViewportHeight(true);
        
        // Set custom renderers
       transactionTable.setDefaultRenderer(Double.class, new CurrencyRenderer());
        transactionTable.setDefaultRenderer(Boolean.class, new BooleanRenderer());
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        
        // Create control panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Time Range:"));
        timeRangeCombo = new JComboBox<>(new String[]{
            "All", 
            "Active", 
            "This Month", 
            "Next Month", 
            "This Year"
        });
        filterPanel.add(timeRangeCombo);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Add");
        generateButton = new JButton("Generate");
        toggleButton = new JButton("Toggle");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        
        // Style buttons
        styleButton(addButton, new Color(76, 175, 80));
        styleButton(generateButton, new Color(76,175,80));
        styleButton(toggleButton, new Color(76,175,80));
        styleButton(editButton, new Color(76,175,80));
        styleButton(deleteButton, new Color(76,175,80));
        
        buttonPanel.add(addButton);
        buttonPanel.add(generateButton);
        buttonPanel.add(toggleButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        controlPanel.add(filterPanel, BorderLayout.WEST);
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        
    }
    
    private void styleButton(JButton button, Color color) {
    	
        button.setBackground(color);
        button.setForeground(Color.black);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
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
    
    // Custom renderer for boolean values
    private static class BooleanRenderer extends DefaultTableCellRenderer {
        public BooleanRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }
        
        @Override
        public void setValue(Object value) {
            // Handle cases where value might not be Boolean
            boolean status = false;
            if (value instanceof Boolean) {
                status = (Boolean)value;
            } else if (value instanceof String) {
                status = Boolean.parseBoolean((String)value);
            }
            setText(status ? "Yes" : "No");
        }
    }
    
   
    public void updateTransactionView(ScheduledTransaction transaction) {
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(transaction.getDescription()) &&
                tableModel.getValueAt(i, 4).equals(transaction.getStartDate().format(DateTimeFormatter.ISO_DATE))) {
                tableModel.removeRow(i);
                break;
            }
        }
        
       
        tableModel.addRow(new Object[]{
            transaction.getDescription(),
            transaction.getCategory(),
            transaction.getAmount(),
            transaction.getTransactionType(),
            transaction.getStartDate().format(DateTimeFormatter.ISO_DATE),
            transaction.getEndDate().format(DateTimeFormatter.ISO_DATE),
            transaction.getFrequencyDays() + " days",
            transaction.isActive(),
            calculateNextOccurrence(transaction)
        });
    }
    
 
    public void removeTransaction(ScheduledTransaction transaction) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(transaction.getDescription()) &&
                tableModel.getValueAt(i, 4).equals(transaction.getStartDate().format(DateTimeFormatter.ISO_DATE))) {
                tableModel.removeRow(i);
                return;
            }
        }
    }
    
    
    public void reloadTransactions(List<ScheduledTransaction> transactions) {
        tableModel.setRowCount(0);
        for (ScheduledTransaction t : transactions) {
            updateTransactionView(t);
        }
    }
    
    
    public String calculateNextOccurrence(ScheduledTransaction t) {
        LocalDate today = LocalDate.now();
        if (!t.isActive() || today.isAfter(t.getEndDate())) {
            return "N/A";
        }
        
        LocalDate next = t.getStartDate();
        while (next.isBefore(today)) {
            next = next.plusDays(t.getFrequencyDays());
        }
        
        return next.isAfter(t.getEndDate()) ? "Completed" : next.format(DateTimeFormatter.ISO_DATE);
    }
    
  
    
    public JButton getAddButton() {
        return addButton;
    }
    
    public JButton getGenerateButton() {
        return generateButton;
    }
    
    public JButton getToggleButton() {
        return toggleButton;
    }
    
    public JButton getEditButton() {
        return editButton;
    }
    
    public JButton getDeleteButton() {
        return deleteButton;
    }
    
    public JComboBox<String> getTimeRangeCombo() {
        return timeRangeCombo;
    }
    
    public JTable getTransactionTable() {
        return transactionTable;
    }
    
    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}







