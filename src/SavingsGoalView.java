
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SavingsGoalView extends JPanel {
    private SavingsGoalController controller;
    private JPanel goalsPanel;
    private JButton addButton;
    private JScrollPane scrollPane;

    public SavingsGoalView() {
        initializeUI();
    }

    public void setController(SavingsGoalController controller) {
        this.controller = controller;
        loadSavingsGoals();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        // Main content panel with scroll
        goalsPanel = new JPanel();
        goalsPanel.setLayout(new BoxLayout(goalsPanel, BoxLayout.Y_AXIS));
        goalsPanel.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(goalsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        addButton = createStyledButton("Add New Goal");
        buttonPanel.add(addButton);
        
        add(buttonPanel, BorderLayout.SOUTH);

        // Add event listeners
        addButton.addActionListener(this::showAddGoalDialog);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground( new Color(76, 175, 80)); 
        button.setForeground(Color.black);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void loadSavingsGoals() {
        goalsPanel.removeAll();
        
        if (controller == null) return;
        
        List<SavingsGoal> goals = controller.getUserSavingsGoals();
        
        if (goals != null && !goals.isEmpty()) {
            for (SavingsGoal goal : goals) {
                goalsPanel.add(createGoalPanel(goal));
                goalsPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacing between goals
            }
        } else {
            JLabel emptyLabel = new JLabel("No savings goals found. Click 'Add New Goal' to create one.");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            goalsPanel.add(emptyLabel);
        }
        
        goalsPanel.revalidate();
        goalsPanel.repaint();
    }

    private JPanel createGoalPanel(SavingsGoal goal) {
        JPanel goalPanel = new JPanel();
        goalPanel.setLayout(new BoxLayout(goalPanel, BoxLayout.Y_AXIS));
        goalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        goalPanel.setBackground(Color.WHITE);
        goalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        goalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Goal name
        JLabel nameLabel = new JLabel(goal.getGoalName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        goalPanel.add(nameLabel);

        // Progress bar
        double progress = controller.calculateProgress(goal);
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) progress);
        progressBar.setStringPainted(true);
        progressBar.setString(String.format("%.1f%%", progress));
        progressBar.setFont(new Font("Arial", Font.BOLD, 12));
        progressBar.setForeground(getProgressColor(progress));
        progressBar.setPreferredSize(new Dimension(500, 25));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        goalPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        goalPanel.add(progressBar);

        // Details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(2, 2, 10, 5));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Current amount
        JLabel currentLabel = new JLabel("Saved: " + String.format("$%.2f", goal.getCurrentAmount()));
        currentLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Target amount
        JLabel targetLabel = new JLabel("Target: " + String.format("$%.2f", goal.getTargetAmount()));
        targetLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Target date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JLabel dateLabel = new JLabel("Target Date: " + dateFormat.format(goal.getTargetDate()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Days remaining (optional)
        long daysRemaining = (goal.getTargetDate().getTime() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
        JLabel daysLabel = new JLabel("Days Remaining: " + (daysRemaining > 0 ? daysRemaining : "Past due"));
        daysLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        detailsPanel.add(currentLabel);
        detailsPanel.add(targetLabel);
        detailsPanel.add(dateLabel);
        detailsPanel.add(daysLabel);
        goalPanel.add(detailsPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton addFundsButton = createSmallButton("Add Funds");
        addFundsButton.setBackground( new Color(76, 175, 80));
        JButton editButton = createSmallButton("Edit");
        editButton.setBackground(new Color(230, 76, 60));
        JButton deleteButton = createSmallButton("Delete");
        deleteButton.setBackground( new Color(76, 175, 80));

        addFundsButton.addActionListener(e -> showAddFundsDialog(goal));
        editButton.addActionListener(e -> showEditGoalDialog(goal));
        deleteButton.addActionListener(e -> deleteGoal(goal));

        buttonPanel.add(addFundsButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        goalPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        goalPanel.add(buttonPanel);

        return goalPanel;
    }

    private JButton createSmallButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        return button;
    }

    private Color getProgressColor(double progress) {
        if (progress < 30) {
            return new Color(220, 53, 69); // Red
        } else if (progress < 70) {
            return new Color(255, 193, 7); // Yellow
        } else {
            return new Color(40, 167, 69); // Green
        }
    }

   private void showAddGoalDialog(ActionEvent e) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nameField = new JTextField();
        JTextField targetAmountField = new JTextField();
        JTextField currentAmountField = new JTextField("0");
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        
        panel.add(new JLabel("Goal Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Target Amount:"));
        panel.add(targetAmountField);
        panel.add(new JLabel("Current Amount:"));
        panel.add(currentAmountField);
        panel.add(new JLabel("Target Date (YYYY-MM-DD):"));
        panel.add(dateField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Savings Goal", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String goalName = nameField.getText();
                double targetAmount = Double.parseDouble(targetAmountField.getText());
                double currentAmount = Double.parseDouble(currentAmountField.getText());
                Date targetDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText());
                
                if (controller.createSavingsGoal(goalName, targetAmount, currentAmount, targetDate)) {
                    loadSavingsGoals();
                    JOptionPane.showMessageDialog(this, "Goal added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add goal.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditGoalDialog(SavingsGoal goal) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nameField = new JTextField(goal.getGoalName());
        JTextField targetAmountField = new JTextField(String.valueOf(goal.getTargetAmount()));
        JTextField currentAmountField = new JTextField(String.valueOf(goal.getCurrentAmount()));
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(goal.getTargetDate()));
        
        panel.add(new JLabel("Goal Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Target Amount:"));
        panel.add(targetAmountField);
        panel.add(new JLabel("Current Amount:"));
        panel.add(currentAmountField);
        panel.add(new JLabel("Target Date (YYYY-MM-DD):"));
        panel.add(dateField);
        
        int result = JOptionPane.showConfirmDialog(
            this, panel, "Edit Savings Goal", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                goal.setGoalName(nameField.getText());
                goal.setTargetAmount(Double.parseDouble(targetAmountField.getText()));
                goal.setCurrentAmount(Double.parseDouble(currentAmountField.getText()));
                goal.setTargetDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText()));
                
                if (controller.updateSavingsGoal(goal)) {
                    loadSavingsGoals();
                    JOptionPane.showMessageDialog(this, "Goal updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update goal.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteGoal(SavingsGoal goal) {
        int confirm = JOptionPane.showConfirmDialog(
            this, "Are you sure you want to delete this goal?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteSavingsGoal(goal.getGoalId())) {
                loadSavingsGoals();
                JOptionPane.showMessageDialog(this, "Goal deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete goal.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddFundsDialog(SavingsGoal goal) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Amount to add to '" + goal.getGoalName() + "':"), BorderLayout.NORTH);
        JTextField amountField = new JTextField();
        panel.add(amountField, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(
            this, panel, "Add Funds to Goal", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (controller.addToSavingsGoal(goal.getGoalId(), amount)) {
                    loadSavingsGoals();
                    JOptionPane.showMessageDialog(this, "Funds added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add funds.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
