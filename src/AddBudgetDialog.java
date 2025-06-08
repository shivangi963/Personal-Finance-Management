

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AddBudgetDialog extends JDialog {
    private boolean confirmed = false;
    private JTextField categoryField;
    private JTextField amountField;
    private JComboBox<String> periodTypeCombo;
    private JFormattedTextField startDateField;
    private JFormattedTextField endDateField;

    public AddBudgetDialog(JFrame parent) {
        super(parent, "Add New Budget", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Category
        categoryField = new JTextField();
        mainPanel.add(new JLabel("Category:"));
        mainPanel.add(categoryField);

        // Amount
        amountField = new JTextField();
        mainPanel.add(new JLabel("Amount Limit:"));
        mainPanel.add(amountField);

        // Period type
        periodTypeCombo = new JComboBox<>(new String[]{"WEEKLY", "MONTHLY", "CUSTOM"});
        periodTypeCombo.addActionListener(e -> updateDateFields());
        mainPanel.add(new JLabel("Period Type:"));
        mainPanel.add(periodTypeCombo);

        // Start date
        startDateField = new JFormattedTextField(LocalDate.now());
        mainPanel.add(new JLabel("Start Date:"));
        mainPanel.add(startDateField);

        // End date
        endDateField = new JFormattedTextField(LocalDate.now().plusDays(7));
        endDateField.setEnabled(false);
        mainPanel.add(new JLabel("End Date:"));
        mainPanel.add(endDateField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            if (validateInputs()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateDateFields() {
        String periodType = (String) periodTypeCombo.getSelectedItem();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate;
        
        switch (periodType) {
            case "WEEKLY":
                endDate = startDate.plusDays(6);
                break;
            case "MONTHLY":
                endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                break;
            case "CUSTOM":
                // Leave as is
                break;
        }
        
        startDateField.setValue(startDate);
        endDateField.setValue(endDate);
        endDateField.setEnabled("CUSTOM".equals(periodType));
    }

    private boolean validateInputs() {
        try {
            Double.parseDouble(amountField.getText());
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount", 
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Getters for dialog results
    public boolean isConfirmed() { return confirmed; }
    public String getCategory() { return categoryField.getText(); }
    public double getAmountLimit() { return Double.parseDouble(amountField.getText()); }
    public String getPeriodType() { return (String) periodTypeCombo.getSelectedItem(); }
    public LocalDate getStartDate() { return (LocalDate) startDateField.getValue(); }
    public LocalDate getEndDate() { return (LocalDate) endDateField.getValue(); }
}
