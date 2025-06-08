import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;

public class DebtView extends JPanel {
    private DebtController controller;
    private JPanel summaryPanel;
    private JPanel debtsPanel;
    private JPanel creditsPanel;

    public DebtView() {
        initializeUI();
    }

    public void setController(DebtController controller) {
        this.controller = controller;
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add buttons
        JButton addDebtBtn = new JButton("Add New Debt");
        addDebtBtn.setBackground(new Color(76,175,80));
        addDebtBtn.setFocusable(false);
        addDebtBtn.addActionListener(this::showAddDebtDialog);
        
        JButton addCreditBtn = new JButton("Add New Credit");
        addCreditBtn.setBackground(new Color(76,175,80));
        addCreditBtn.setFocusable(false);
        addCreditBtn.addActionListener(this::showAddCreditDialog);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addDebtBtn);
        buttonPanel.add(addCreditBtn);
   
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        add(buttonPanel, BorderLayout.NORTH);

        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Summary Panel
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new GridLayout(1, 3, 15, 15));
        summaryPanel.setMaximumSize(new Dimension(800, 100));
        summaryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(summaryPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Debts Panel (centered)
        debtsPanel = new JPanel();
        debtsPanel.setLayout(new BoxLayout(debtsPanel, BoxLayout.Y_AXIS));
        debtsPanel.setBorder(BorderFactory.createTitledBorder("Debts Owed"));
        debtsPanel.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));
        debtsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(debtsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Credits Panel (centered)
        creditsPanel = new JPanel();
        creditsPanel.setLayout(new BoxLayout(creditsPanel, BoxLayout.Y_AXIS));
        creditsPanel.setBorder(BorderFactory.createTitledBorder("Credit Given"));
        creditsPanel.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));
        creditsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(creditsPanel);

        // Center the content panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(contentPanel);
        centerPanel.add(Box.createVerticalGlue());
        
        add(centerPanel, BorderLayout.CENTER);
    }

    public void renderDebts(List<DebtModel.Debt> debts, List<DebtModel.Debt> credits, 
                          double totalDebt, double totalCredit, double netPosition) {
        renderSummary(totalDebt, totalCredit, netPosition);
        renderDebtList(debtsPanel, debts, false);
        renderDebtList(creditsPanel, credits, true);
        revalidate();
        repaint();
    }

    private void renderSummary(double totalDebt, double totalCredit, double netPosition) {
        summaryPanel.removeAll();
        summaryPanel.add(createSummaryCard("Total Debt", totalDebt, new Color(255, 235, 238), new Color(229, 57, 53)));
        summaryPanel.add(createSummaryCard("Total Credit", totalCredit, new Color(232, 245, 233), new Color(67, 160, 71)));
        
        Color netColor = netPosition >= 0 ? new Color(227, 242, 253) : new Color(255, 235, 238);
        Color netBorderColor = netPosition >= 0 ? new Color(30, 136, 229) : new Color(229, 57, 53);
        summaryPanel.add(createSummaryCard("Net Position", netPosition, netColor, netBorderColor));
    }

    private JPanel createSummaryCard(String title, double amount, Color bgColor, Color borderColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 4, borderColor),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        JLabel amountLabel = new JLabel(currencyFormat.format(amount));
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (title.equals("Net Position")) {
            amountLabel.setForeground(amount >= 0 ? new Color(46, 125, 50) : new Color(198, 40, 40));
        }

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(amountLabel);

        return card;
    }

    private void renderDebtList(JPanel container, List<DebtModel.Debt> items, boolean isCredit) {
        container.removeAll();

        if (items.isEmpty()) {
            JLabel emptyLabel = new JLabel("No " + (isCredit ? "credits" : "debts") + " found");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            container.add(emptyLabel);
            return;
        }

        for (DebtModel.Debt item : items) {
            if (isCredit) {
                container.add(createCreditCard(item));
            } else {
                container.add(createDebtCard(item));
            }
            container.add(Box.createRigidArea(new Dimension(0, 15)));
        }
    }
    
    private JPanel createDebtCard(DebtModel.Debt item) {
        return createItemCard(item, false); // false indicates it's a debt
    }

    private JPanel createCreditCard(DebtModel.Debt item) {
        return createItemCard(item, true); // true indicates it's a credit
    }

    private JPanel createItemCard(DebtModel.Debt item, boolean isCredit) {
    	 JPanel card = new JPanel();
    	    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    	    card.setBorder(BorderFactory.createCompoundBorder(
    	        BorderFactory.createMatteBorder(0, 0, 0, 4, 
    	            isCredit ? new Color(67, 160, 71) : new Color(229, 57, 53)),
    	        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    	    ));
    	    card.setBackground(isCredit ? new Color(232, 245, 233) : new Color(255, 235, 238));
    	    card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Header panel with name, interest rate, and delete button
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JPanel nameRatePanel = new JPanel();
        nameRatePanel.setLayout(new BoxLayout(nameRatePanel, BoxLayout.X_AXIS));
        nameRatePanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        JLabel rateLabel = new JLabel(item.getInterestRate() + "%");
        rateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rateLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 25)),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        
        nameRatePanel.add(nameLabel);
        nameRatePanel.add(Box.createHorizontalStrut(10));
        nameRatePanel.add(rateLabel);
        
        // Delete button
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        deleteBtn.setBackground(new Color(227,36,43));
        deleteBtn.setForeground(Color.black);
        deleteBtn.addActionListener(e -> {
                if (isCredit) {
                    controller.deleteCredit(item.getId());
                } else {
                    controller.deleteDebt(item.getId());
                }
            
        });
        
        headerPanel.add(nameRatePanel, BorderLayout.WEST);
        headerPanel.add(deleteBtn, BorderLayout.EAST);
        
        // Progress bar
        JPanel progressContainer = new JPanel();
        progressContainer.setLayout(new BoxLayout(progressContainer, BoxLayout.X_AXIS));
        progressContainer.setOpaque(false);
        progressContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(item.getProgressPercentage());
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(300, 10));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        progressBar.setBackground(new Color(224, 224, 224));
        progressBar.setForeground(isCredit ? new Color(67, 160, 71) : new Color(229, 57, 53));

        progressContainer.add(progressBar);

        // Amount details
        JPanel amountPanel = new JPanel();
        amountPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        amountPanel.setOpaque(false);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        JLabel paidLabel = new JLabel("Paid: " + currencyFormat.format(item.getPaidAmount()));
        JLabel remainingLabel = new JLabel("Remaining: " + currencyFormat.format(item.getRemainingAmount()));
        JLabel totalLabel = new JLabel("Total: " + currencyFormat.format(item.getTotalAmount()));

        amountPanel.add(paidLabel);
        amountPanel.add(remainingLabel);
        amountPanel.add(totalLabel);

        JPanel footerPanel = new JPanel(new BorderLayout());
        
        JLabel dueLabel = new JLabel("Due: " + item.getDueDate());
        dueLabel.setForeground(new Color(102, 102, 102));
        
        JButton paybackBtn = new JButton("Record Payment");
        paybackBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        paybackBtn.setBackground(new Color(100, 150, 100));
        paybackBtn.setForeground(Color.black);
        paybackBtn.setFocusable(false);
        paybackBtn.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        paybackBtn.setPreferredSize(new Dimension(paybackBtn.getPreferredSize().width, 20));
        paybackBtn.addActionListener(e -> showPaybackDialog(item.getId(), isCredit));
        
        footerPanel.add(dueLabel, BorderLayout.WEST);
        footerPanel.add(paybackBtn, BorderLayout.EAST);

        card.add(headerPanel);
        card.add(progressContainer);
        card.add(amountPanel);
        card.add(footerPanel);

        return card;
    }

    private void showPaybackDialog(int itemId, boolean isCredit) {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                                   "Record Payment", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel amountLabel = new JLabel("Payment Amount:");
        JTextField amountField = new JTextField();
        
        JLabel dateLabel = new JLabel("Payment Date:");
        JTextField dateField = new JTextField();
        // Auto-fill with today's date in YYYY-MM-DD format
        dateField.setText(java.time.LocalDate.now().toString());
        
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(new JLabel()); 
        formPanel.add(new JLabel()); 

        // Create smaller payment button
        JButton submitBtn = new JButton("Record Payback");
        submitBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        submitBtn.setBackground(new Color(100, 150, 100));
        submitBtn.setForeground(Color.black);
        
        submitBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String date = dateField.getText().trim();
                
                if (isCredit) {
                    controller.recordCreditPayment(itemId, amount);
                } else {
                    controller.recordDebtPayment(itemId, amount);
                }
                
                dialog.dispose();
            } catch (NumberFormatException | SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter a valid payment amount", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(100, 150, 100));
        cancelButton.setForeground(Color.black);
        cancelButton.setFocusable(false);
        
        cancelButton.addActionListener(e -> dialog.dispose());


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showAddDebtDialog(ActionEvent e) {
        showAddItemDialog(false);
    }

    private void showAddCreditDialog(ActionEvent e) {
        showAddItemDialog(true);
    }

    private void showAddItemDialog(boolean isCredit) {
        String title = isCredit ? "Add New Credit" : "Add New Debt";
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField dueDateField = new JTextField();
        JTextField interestField = new JTextField();

        formPanel.add(new JLabel(isCredit ? "Credit Name:" : "Debt Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Total Amount:"));
        formPanel.add(amountField);
        formPanel.add(new JLabel("Due Date (YYYY-MM-DD):"));
        formPanel.add(dueDateField);
        formPanel.add(new JLabel("Interest Rate (%):"));
        formPanel.add(interestField);

        JButton submitBtn = new JButton(isCredit ? "Add Credit" : "Add Debt");
        submitBtn.setBackground(new Color(76,175,80));
        submitBtn.addActionListener(ev -> {
            try {
                String name = nameField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String dueDate = dueDateField.getText();
                double interest = Double.parseDouble(interestField.getText());

                if (isCredit) {
                    controller.addCredit(name, amount, dueDate, interest);
                } else {
                    controller.addDebt(name, amount, dueDate, interest);
                }
                dialog.dispose();
            } catch (NumberFormatException | SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter valid numbers for amount and interest rate",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(76,175,80));
        cancelButton.setForeground(Color.black);
        cancelButton.setFocusable(false);
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }


    private void loadData() {
        if (controller != null) {
            controller.loadData();
        }
    }
}
