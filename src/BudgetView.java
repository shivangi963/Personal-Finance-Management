
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BudgetView extends JPanel {
    private BudgetController controller;
    private JPanel budgetsPanel;

    public BudgetView() {
        initializeUI();
    }

    public void setController(BudgetController controller) {
        this.controller = controller;
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 240, 245));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 245));
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JButton addButton = new JButton("+ Add Budget");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setForeground(Color.black);
        addButton.setBackground(new Color(76,175,80));
        addButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        addButton.setFocusPainted(false);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> showAddBudgetDialog());
        headerPanel.add(addButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Budgets panel
        budgetsPanel = new JPanel();
        budgetsPanel.setLayout(new BoxLayout(budgetsPanel, BoxLayout.Y_AXIS));
        budgetsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        budgetsPanel.setBackground(new Color(240, 240, 245));
        
        JScrollPane scrollPane = new JScrollPane(budgetsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(240, 240, 245));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void showAddBudgetDialog() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Add New Budget", true);
        dialog.setLayout(new BorderLayout());
        dialog.setMinimumSize(new Dimension(400, 300));
        dialog.setUndecorated(true);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Category
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Category:"), gbc);
        JTextField categoryField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(categoryField, gbc);
        
        // Budget Limit
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Budget Limit:"), gbc);
        JTextField limitField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(limitField, gbc);
        
        // Duration
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Duration:"), gbc);
        JComboBox<String> durationCombo = new JComboBox<>(new String[]{"1 Month", "1 Week", "Other"});
        gbc.gridx = 1;
        panel.add(durationCombo, gbc);
        
        // Date Panel 
        JPanel datePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        datePanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        datePanel.setOpaque(false);
        
        JLabel startDateLabel = new JLabel("Start Date:");
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startDateEditor);
        
        JLabel endDateLabel = new JLabel("End Date:");
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        endDateSpinner.setEditor(endDateEditor);
        
        datePanel.add(startDateLabel);
        datePanel.add(startDateSpinner);
        datePanel.add(endDateLabel);
        datePanel.add(endDateSpinner);
        
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(datePanel, gbc);
        datePanel.setVisible(false);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton createButton = new JButton("Create");
        createButton.setForeground(Color.black);
        createButton.setBackground(new Color(76,175,80));
        createButton.addActionListener(e -> {
            try {
                String category = categoryField.getText().trim();
                double limit = Double.parseDouble(limitField.getText());
                String duration = (String) durationCombo.getSelectedItem();
                
                LocalDate startDate = null;
                LocalDate endDate = null;
                
                if ("1 Month".equals(duration)) {
                    startDate = LocalDate.now();
                    endDate = startDate.plusMonths(1);
                } else if ("1 Week".equals(duration)) {
                    startDate = LocalDate.now();
                    endDate = startDate.plusWeeks(1);
                } else if ("Other".equals(duration)) {
                    startDate = ((java.util.Date)startDateSpinner.getValue()).toInstant()
                                      .atZone(java.time.ZoneId.systemDefault())
                                      .toLocalDate();
                    endDate = ((java.util.Date)endDateSpinner.getValue()).toInstant()
                                      .atZone(java.time.ZoneId.systemDefault())
                                      .toLocalDate();
                    
                    if (endDate.isBefore(startDate)) {
                        throw new IllegalArgumentException("End date cannot be before start date");
                    }
                }
                
                if (category.isEmpty() || limit <= 0) {
                    throw new IllegalArgumentException("Please enter valid category and amount");
                }
                
                controller.addBudget(category, limit, duration, startDate, endDate);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setForeground(Color.black);
        cancelButton.setBackground(new Color(76,175,80));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        // Duration change listener
        durationCombo.addActionListener(e -> {
            String selection = (String) durationCombo.getSelectedItem();
            datePanel.setVisible("Other".equals(selection));
            dialog.pack();
        });
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    public void showEditBudgetDialog(Budget budget) {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Edit Budget", true);
        dialog.setLayout(new BorderLayout());
        dialog.setMinimumSize(new Dimension(450, 300));
        dialog.setUndecorated(true);
        
        // Main content panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Category (display only)
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        JLabel categoryLabel = new JLabel(budget.getCategory());
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(categoryLabel, gbc);
        
        // Budget Limit
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Budget Limit:"), gbc);
        gbc.gridx = 1;
        JTextField limitField = new JTextField(String.format("%.2f", budget.getBudgetLimit()), 15);
        panel.add(limitField, gbc);
        
        // Duration
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Duration:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> durationCombo = new JComboBox<>(new String[]{"1 Month", "1 Week", "Other"});
        durationCombo.setSelectedItem(budget.getDuration());
        panel.add(durationCombo, gbc);
        
        
        JPanel datePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        datePanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        datePanel.setOpaque(false);
        
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startDateEditor);
        if (budget.getStartDate() != null) {
            startDateSpinner.setValue(Date.from(budget.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        endDateSpinner.setEditor(endDateEditor);
        if (budget.getEndDate() != null) {
            endDateSpinner.setValue(Date.from(budget.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        datePanel.add(new JLabel("Start Date:"));
        datePanel.add(startDateSpinner);
        datePanel.add(new JLabel("End Date:"));
        datePanel.add(endDateSpinner);
        datePanel.setVisible("Other".equals(budget.getDuration()));
        
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(datePanel, gbc);
        
        // Button Panel (now fixed at bottom and won't be hidden)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setForeground(Color.black);
        cancelButton.setBackground(new Color(76,175,80));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = new JButton("Save");
        saveButton.setForeground(Color.black);
        saveButton.setBackground(new Color(76,175,80));
        saveButton.addActionListener(e -> {
            try {
                double newLimit = Double.parseDouble(limitField.getText());
                String duration = (String) durationCombo.getSelectedItem();
                
                LocalDate startDate = null;
                LocalDate endDate = null;
                
                if ("1 Month".equals(duration)) {
                    startDate = LocalDate.now();
                    endDate = startDate.plusMonths(1);
                } else if ("1 Week".equals(duration)) {
                    startDate = LocalDate.now();
                    endDate = startDate.plusWeeks(1);
                } else if ("Other".equals(duration)) {
                    startDate = ((Date)startDateSpinner.getValue()).toInstant()
                                      .atZone(ZoneId.systemDefault())
                                      .toLocalDate();
                    endDate = ((Date)endDateSpinner.getValue()).toInstant()
                                      .atZone(ZoneId.systemDefault())
                                      .toLocalDate();
                    
                    if (endDate.isBefore(startDate)) {
                        throw new IllegalArgumentException("End date cannot be before start date");
                    }
                }
                
                controller.updateBudget(
                    budget.getBudgetId(),
                    newLimit,
                    duration,
                    startDate,
                    endDate
                );
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
                
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH); 
        
    
        durationCombo.addActionListener(e -> {
            String selection = (String) durationCombo.getSelectedItem();
            datePanel.setVisible("Other".equals(selection));
            dialog.pack(); 
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void displayBudgets(List<Budget> budgets) {
        budgetsPanel.removeAll();
        
        if (budgets.isEmpty()) {
            JLabel noBudgetsLabel = new JLabel("No budgets set up yet");
            noBudgetsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noBudgetsLabel.setForeground(new Color(120, 120, 120));
            noBudgetsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noBudgetsLabel.setBorder(new EmptyBorder(50, 0, 0, 0));
            budgetsPanel.add(noBudgetsLabel);
        } else {
            for (Budget budget : budgets) {
                budgetsPanel.add(new BudgetPanel(budget, controller));
                budgetsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }
        
        budgetsPanel.revalidate();
        budgetsPanel.repaint();
    }
    
  public void showBudgetExceedWarning(Budget budget, double newAmount) {
  JOptionPane.showMessageDialog(this,
          "Warning: This transaction will exceed your budget for " + budget.getCategory() + "!\n" +
          "Budget: $" + budget.getBudgetLimit() + "\n" +
          "After transaction: $" + (budget.getSpentAmount() + newAmount) + "/$" + budget.getBudgetLimit(),
          "Budget Exceeded", JOptionPane.WARNING_MESSAGE);
}



  private class BudgetPanel extends JPanel {
	    private Budget budget;
	    private static final int PANEL_WIDTH = (int)(Toolkit.getDefaultToolkit().getScreenSize().width * 0.8);
	    
	    public BudgetPanel(Budget budget, BudgetController controller) {
	        this.budget = budget;
	        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 
	        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),new EmptyBorder(12, 15, 12, 15)));
	        setMaximumSize(new Dimension(PANEL_WIDTH, 150));
	        setBackground(Color.WHITE);
	        setAlignmentX(Component.CENTER_ALIGNMENT);

	        JPanel topRow = new JPanel(new BorderLayout());
	        topRow.setBackground(Color.WHITE);
	        topRow.setMaximumSize(new Dimension(PANEL_WIDTH, 30));
	        
	        JLabel categoryLabel = new JLabel(budget.getCategory());
	        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
	        topRow.add(categoryLabel, BorderLayout.WEST);
	        
	        JLabel statusLabel = new JLabel(budget.getStatus());
	        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
	        statusLabel.setForeground(budget.isExceeded() ? Color.RED : new Color(50, 150, 50));
	        topRow.add(statusLabel, BorderLayout.EAST);
	        
	        add(topRow);
	        add(Box.createRigidArea(new Dimension(0, 8)));
	        
	     
	        JPanel amountsRow = new JPanel(new BorderLayout());
	        amountsRow.setBackground(Color.WHITE);
	        amountsRow.setMaximumSize(new Dimension(PANEL_WIDTH, 25));
	        
	        double residual = budget.getBudgetLimit() - budget.getSpentAmount();
	        JLabel residualLabel = new JLabel(String.format("Remaining: $%.2f", residual > 0 ? residual : 0));
	        residualLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
	        residualLabel.setForeground(residual >= 0 ? new Color(50, 150, 50) : Color.RED);
	        amountsRow.add(residualLabel, BorderLayout.WEST);
	        
	        JLabel totalLabel = new JLabel(String.format("Total: $%.2f", budget.getBudgetLimit()));
	        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
	        amountsRow.add(totalLabel, BorderLayout.EAST);
	        
	        add(amountsRow);
	        add(Box.createRigidArea(new Dimension(0, 8)));
	        
	       
	        JPanel progressContainer = new JPanel();
	        progressContainer.setLayout(new BoxLayout(progressContainer, BoxLayout.Y_AXIS));
	        progressContainer.setBackground(Color.WHITE);
	        progressContainer.setMaximumSize(new Dimension(PANEL_WIDTH, 40));
	        
	      
	        JLabel percentageLabel = new JLabel(String.format("%.0f%%", budget.getPercentage()));
	        percentageLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
	        percentageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	        progressContainer.add(percentageLabel);
	        progressContainer.add(Box.createRigidArea(new Dimension(0, 4)));
	        
	        
	        JPanel progressBarRow = new JPanel();
	        progressBarRow.setLayout(new BoxLayout(progressBarRow, BoxLayout.X_AXIS));
	        progressBarRow.setBackground(Color.WHITE);
	        progressBarRow.setMaximumSize(new Dimension(PANEL_WIDTH, 20));
	        
	        
	        if (budget.getStartDate() != null) {
	            JLabel startDateLabel = new JLabel(budget.getStartDate().toString());
	            startDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
	            progressBarRow.add(startDateLabel);
	            progressBarRow.add(Box.createRigidArea(new Dimension(10, 0)));
	        }
	        
	       
	        JPanel progressBarPanel = new ProgressBarPanel(budget.getSpentAmount(), budget.getBudgetLimit());
	        progressBarPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 12));
	        progressBarRow.add(progressBarPanel);
	        
	      
	        if (budget.getEndDate() != null) {
	            progressBarRow.add(Box.createRigidArea(new Dimension(10, 0)));
	            JLabel endDateLabel = new JLabel(budget.getEndDate().toString());
	            endDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
	            progressBarRow.add(endDateLabel);
	        }
	        
	        progressContainer.add(progressBarRow);
	        add(progressContainer);
	        add(Box.createRigidArea(new Dimension(0, 10)));
	        
	      
	        JPanel buttonRow = new JPanel();
	        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
	        buttonRow.setBackground(Color.WHITE);
	        buttonRow.setMaximumSize(new Dimension(PANEL_WIDTH, 40));
	        buttonRow.setBorder(new EmptyBorder(10, 0, 0, 0)); 
	        
	        
	        buttonRow.add(Box.createHorizontalGlue());
	        
	        // Edit button
	        JButton editButton = new JButton("Edit");
	       	editButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
	        editButton.setPreferredSize(new Dimension(80, 28));
	       
	        styleButton(editButton);
	        editButton.addActionListener(e -> controller.onEditButtonClicked(budget));
	        buttonRow.add(editButton);
	        
	        buttonRow.add(Box.createRigidArea(new Dimension(10, 0)));
	        
	        // Delete button
	        JButton deleteButton = new JButton("Delete");
	        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
	        deleteButton.setPreferredSize(new Dimension(80, 28));
	        styleButton(deleteButton, new Color(76,175,80));
	        deleteButton.addActionListener(e -> {
	            
	                controller.deleteBudget(budget.getBudgetId());
	            
	        });
	        buttonRow.add(deleteButton);
	        
	        add(buttonRow);
	    }
    
	    
	    private void styleButton(JButton button) {
	        styleButton(button, new Color(227,36,43));
	    }
	    private void styleButton(JButton button, Color color) {
	    button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
	    button.setForeground(Color.WHITE);
	    button.setBackground(color);
	    button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	    button.setFocusPainted(false);
	    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

	    button.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseEntered(MouseEvent e) {
	            button.setBackground(color.darker());
	        }
	        
	        @Override
	        public void mouseExited(MouseEvent e) {
	            button.setBackground(color);
	        }
	    });
	    }
  }

  private class ProgressBarPanel extends JPanel {
	    private double spent;
	    private double limit;
	    
	    public ProgressBarPanel(double spent, double limit) {
	        this.spent = spent;
	        this.limit = limit;
	        setPreferredSize(new Dimension(100, 12));
	        setMaximumSize(new Dimension(Short.MAX_VALUE, 12));
	        setOpaque(false); // Important for visibility
	    }
	    
	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        
	        int width = getWidth();
	        int height = getHeight();
	        
	      
	        g.setColor(new Color(230, 230, 230));
	        g.fillRoundRect(0, 0, width, height, height, height);
	        
	    
	        double progress = (limit > 0) ? Math.min(spent / limit, 1) : 0;
	        int progressWidth = (int) (width * progress);
	        
	        if (progressWidth > 0) {
	            Graphics2D g2d = (Graphics2D) g;
	            
	            Color startColor = spent > limit ? new Color(255, 100, 100) : new Color(100, 200, 100);
	            Color endColor = spent > limit ? new Color(200, 50, 50) : new Color(50, 150, 50);
	            GradientPaint gradient = new GradientPaint(0, 0, startColor, progressWidth, 0, endColor);
	            
	            g2d.setPaint(gradient);
	          
	            RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
	                    0, 0, progressWidth, height, height, height);
	          
	            Shape oldClip = g2d.getClip();
	            g2d.clip(roundedRectangle);
	            g2d.fill(roundedRectangle);
	            g2d.setClip(oldClip);
	        }
	       
	        g.setColor(new Color(180, 180, 180));
	        g.drawRoundRect(0, 0, width - 1, height - 1, height, height);
	    }
  }
}
   

