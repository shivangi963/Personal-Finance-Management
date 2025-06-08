import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditScheduledTransactionDialog extends JDialog {
    private boolean confirmed = false;
    private JTextField descriptionField;
    private JComboBox<String> categoryCombo;
    private JTextField amountField;
    private JComboBox<String> typeCombo;
    private JTextField startDateField;
    private JTextField endDateField;
    private JSpinner frequencySpinner;
    private JCheckBox activeCheckbox;

    public EditScheduledTransactionDialog(JFrame parent, String title, ScheduledTransaction transaction) {
        super(parent, title, true);
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        initComponents();
        populateFields(transaction);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Description
        descriptionField = new JTextField();
        mainPanel.add(new JLabel("Description:"));
        mainPanel.add(descriptionField);

        // Category
        String[] categories = {"Housing", "Food", "Transportation", "Bills", "Entertainment", "Healthcare","Shopping" ,"Salary", "Other"};
        categoryCombo = new JComboBox<>(categories);
        mainPanel.add(new JLabel("Category:"));
        mainPanel.add(categoryCombo);

        // Amount
        amountField = new JTextField();
        mainPanel.add(new JLabel("Amount:"));
        mainPanel.add(amountField);

        // Type
        String[] types = {"Expense", "Income"};
        typeCombo = new JComboBox<>(types);
        mainPanel.add(new JLabel("Type:"));
        mainPanel.add(typeCombo);

        // Start Date
        startDateField = new JTextField();
        mainPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        mainPanel.add(startDateField);

        // End Date (optional)
        endDateField = new JTextField();
        mainPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        mainPanel.add(endDateField);

        // Frequency (in days)
        frequencySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        mainPanel.add(new JLabel("Frequency (days):"));
        mainPanel.add(frequencySpinner);

        // Active status
        activeCheckbox = new JCheckBox("Active");
        mainPanel.add(new JLabel("Status:"));
        mainPanel.add(activeCheckbox);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("Save");
        okButton.setBackground(new Color(76,175,80));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(76,175,80));

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

    private void populateFields(ScheduledTransaction transaction) {
        descriptionField.setText(transaction.getDescription());
        categoryCombo.setSelectedItem(transaction.getCategory());
        amountField.setText(String.valueOf(transaction.getAmount()));
        typeCombo.setSelectedItem(transaction.getTransactionType());
        startDateField.setText(transaction.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        endDateField.setText(transaction.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        frequencySpinner.setValue(transaction.getFrequencyDays());
        activeCheckbox.setSelected(transaction.isActive());
    }

    private boolean validateInputs() {
        try {
            // Validate amount
            Double.parseDouble(amountField.getText());
            
            // Validate dates
            LocalDate.parse(startDateField.getText());
            LocalDate.parse(endDateField.getText());            
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage(),"Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Getters for dialog results
    public boolean isConfirmed() {
        return confirmed;
    }

    public String getDescription() {
        return descriptionField.getText();
    }

    public String getCategory() {
        return (String) categoryCombo.getSelectedItem();
    }

    public double getAmount() {
        return Double.parseDouble(amountField.getText());
    }

    public String getTransactionType() {
        return (String) typeCombo.getSelectedItem();
    }

    public LocalDate getStartDate() {
        return LocalDate.parse(startDateField.getText());
    }

    public LocalDate getEndDate() {
        return LocalDate.parse(endDateField.getText());
    }

    public int getFrequencyDays() {
        return (int) frequencySpinner.getValue();
    }

    public boolean isActive() {
        return activeCheckbox.isSelected();
    }
    
    public void updateTransaction(ScheduledTransaction transaction) {
        transaction.setDescription(descriptionField.getText());
        transaction.setCategory((String)categoryCombo.getSelectedItem());
        transaction.setAmount(Double.parseDouble(amountField.getText()));
        transaction.setTransactionType((String)typeCombo.getSelectedItem());
        transaction.setStartDate(LocalDate.parse(startDateField.getText()));
        transaction.setEndDate(endDateField.getText().isEmpty() ?null : LocalDate.parse(endDateField.getText()));
        transaction.setFrequencyDays((Integer)frequencySpinner.getValue());
    }
}
