import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;


public class dashboard extends JFrame {
	
	 private int user_id;
	 JPanel mainPanel,dashboardView, recentTransactionsView ;
	 TransactionView transactionView;
	 ScheduledTransactionView scheduledView;
	 ScheduledTransactionController scheduledController;
	 Budget budget;
	 BudgetView budgetView;
	 BudgetController budgetController;
	 Chart chart;
	 ChartView chartView;
	 ChartController chartController;
	 ArrayList<Transaction> transactions;
	 DebtModel debtModel ;
	 DebtView debtView ;
	 DebtController debtController;
	 SavingsGoal savingsGoalModel;
	 SavingsGoalView savingsView;
	 SavingsGoalController savingsController;
	 ExportController exportController;
	 ExportView exportView;
	 Export export ;
	 double incomeSum;
	 double expenseSum;
	 double balance;
	 
    public dashboard(int user_id) {
    	
    	this.user_id=user_id;
    	this.chartController = new ChartController(new Chart());
        setTitle("Dashboard " );
        setExtendedState(JFrame.MAXIMIZED_BOTH);
     //   setUndecorated(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel with card layout for switching views
        mainPanel = new JPanel(new CardLayout());
        
        transactions = new ArrayList<>();
        
        
        // Create different views
        dashboardView = createDashboardView(user_id);
        transactionView = new TransactionView(user_id);
        budgetView = new BudgetView(); // Create view first without controller
        budgetController = new BudgetController(user_id, budgetView); 
        debtModel = new DebtModel(user_id);
        debtView =new DebtView();
        debtController = new DebtController(debtModel, debtView);
        scheduledView = new ScheduledTransactionView();
        scheduledController = new ScheduledTransactionController(scheduledView,transactionView,user_id); 
        export = new Export(transactionView);
        exportView = new ExportView();
        setupExportListener();
        exportController = new ExportController(export);
        chart=new Chart();
        chartController = new ChartController(chart);
        chartView = new ChartView(chartController,user_id);       
        debtView.setController(debtController);
        savingsGoalModel = new SavingsGoal();
        savingsView = new SavingsGoalView();
        savingsController = new SavingsGoalController(user_id,transactionView);
        savingsView.setController(savingsController);

        
        // Add views to card layout
        mainPanel.add(dashboardView, "dashboard");
        mainPanel.add(transactionView, "transactions");
        mainPanel.add(budgetView, "budget");
        mainPanel.add(debtView,"debts");
        mainPanel.add(scheduledView,"schedule");
        mainPanel.add(exportView,"exports");
        mainPanel.add(chartView,"charts");
        mainPanel.add(savingsView,"savings");
        
        // Create navigation sidebar
        JPanel sidebar = createSidebar(mainPanel);
        
        // Set layout
        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        
        
    }
    
    private JPanel createSidebar(JPanel mainPanel) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(76,175,80));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // User profile section
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.X_AXIS));
        profilePanel.setBackground(new Color(76,175,80));
        profilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        profilePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Load profile icon (using a default if not found)
        ImageIcon profileIcon = new ImageIcon(getClass().getResource("/icon/profile.png"));
    
            // Scale the profile icon
            Image img = profileIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            profileIcon = new ImageIcon(img);
        
        
        JLabel iconLabel = new JLabel(profileIcon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
      String username="";
      
      try {
      	connection conn=new connection();
      	String query="Select username from users where user_id="+user_id+";";
      	
      	ResultSet res = conn.stm.executeQuery(query);
      	while(res.next()) {
      	username=res.getString("username");
      	}
      	
       } catch (SQLException e) {
  	    e.printStackTrace();
        }
      
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(Color.WHITE);
        
        profilePanel.add(iconLabel);
        profilePanel.add(usernameLabel);
        
        // Add profile panel to sidebar
        sidebar.add(profilePanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        ImageIcon dashboardIcon = new ImageIcon(getClass().getResource("/icon/overview.png"));
        ImageIcon transactionsIcon = new ImageIcon(getClass().getResource("/icon/transaction.png"));
        ImageIcon scheduledIcon = new ImageIcon(getClass().getResource("/icon/scheduled.png"));
        ImageIcon budgetIcon = new ImageIcon(getClass().getResource("/icon/budget.png"));
        ImageIcon chartIcon = new ImageIcon(getClass().getResource("/icon/chart.png"));
        ImageIcon debtIcon = new ImageIcon(getClass().getResource("/icon/debt.png"));
        ImageIcon savingIcon = new ImageIcon(getClass().getResource("/icon/savings.png"));
        ImageIcon exportIcon = new ImageIcon(getClass().getResource("/icon/export.png"));
        ImageIcon logoutIcon = new ImageIcon(getClass().getResource("/icon/logout.png"));
       
        // Navigation buttons
        JButton dashboardBtn = createNavButton("Overview", "dashboard", mainPanel,dashboardIcon);
        JButton transactionsBtn = createNavButton("Transactions", "transactions", mainPanel,transactionsIcon);
        JButton budgetBtn = createNavButton("Budgets", "budget", mainPanel,budgetIcon);
        JButton scheduleBtn = createNavButton("Scheduled Transaction","schedule", mainPanel,scheduledIcon);
        JButton chartsBtn = createNavButton("Charts", "charts", mainPanel,chartIcon);
        JButton debtsBtn = createNavButton("Debts", "debts", mainPanel,debtIcon);
        JButton savingBtn = createNavButton("Saving Goals", "savings", mainPanel,savingIcon);
        JButton exportBtn = createNavButton("Export", "exports", mainPanel,exportIcon);
        JButton logoutBtn = createNavButton("Logout", "logout", mainPanel,logoutIcon);             
        
        // Add components
        sidebar.add(dashboardBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(transactionsBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(budgetBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(scheduleBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(debtsBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(chartsBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(savingBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(exportBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutBtn);
        
        return sidebar;
    }
    
    private JButton createNavButton(String text, String cardName, JPanel mainPanel, ImageIcon originalIcon) {
        // Scale the icon to appropriate size (24x24)
        ImageIcon scaledIcon = null;
        if (originalIcon != null) {
            Image image = originalIcon.getImage();
            Image scaledImage = image.getScaledInstance(23, 23, Image.SCALE_SMOOTH);
            scaledIcon = new ImageIcon(scaledImage);
        }

        JButton button = new JButton(text, scaledIcon);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Set fixed size with proper insets
        button.setPreferredSize(new Dimension(220, 40));
        button.setMaximumSize(new Dimension(220, 40));
        button.setMinimumSize(new Dimension(220, 40));
        
        // Style the button
        button.setFont(new Font("Arial", Font.BOLD, 14)); 
        button.setBackground(new Color(76,175,80));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
                
        // Set padding and borders
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)),  // Bottom border
            BorderFactory.createEmptyBorder(8, 15, 8, 10)  // Top, left, bottom, right padding
        ));
        
        // Icon and text positioning
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setIconTextGap(12);  // Space between icon and text
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Action listener
        button.addActionListener(e -> {
            if (cardName.equals("logout")) {
                this.dispose();
                new login().setVisible(true);
            } else {
                CardLayout cl = (CardLayout) mainPanel.getLayout();
                cl.show(mainPanel, cardName);
            }
        });
        
        return button;
    }

    private JPanel createDashboardView(int user_id) { 	
    	JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainContentPanel.setBackground(new Color(240, 240, 240));

        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerWrapper.setBackground(new Color(240, 240, 240));
        
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        
        connection con=new connection();
        
   String query1="select sum(amount) as total_income from Transactions where type='Income' AND user_id ='"+user_id+"' AND MONTH(date) ="+month+" AND YEAR(date) ="+year+";";
   String query2="select sum(amount) as total_expense from Transactions where type='Expense' AND user_id ='"+user_id+"' AND MONTH(date) ="+month+" AND YEAR(date) ="+year+";"; 
   String query3="select (sum(CASE WHEN type = 'Income' THEN amount ELSE 0 END)- sum(CASE WHEN type = 'Expense' THEN amount ELSE 0 END)) as savings FROM Transactions WHERE user_id ='"+user_id+"' AND MONTH(date) ="+month+" AND YEAR(date) ="+year+";";
        ResultSet res2;
         ResultSet res3;
         ResultSet res4;
        
		try {
			res2 = con.stm.executeQuery(query1);
			res3 = con.stm1.executeQuery(query2);
			res4 = con.stm2.executeQuery(query3);
			
			if(res2.next() ) {
			incomeSum = res2.getDouble("total_income");
			
			}else {
			incomeSum=0.0;
			}
			if(res3.next()) {
				expenseSum = res3.getDouble("total_expense");
			}else {
				expenseSum=0.0;
			}
			if(res4.next()) {
				balance = res4.getDouble("savings");
			}else {
				balance=0.0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String value1=Double.toString(balance);	
		String value2=Double.toString(incomeSum);	
		String value3=Double.toString(expenseSum);	
		
        // Top summary cards
		JPanel summaryPanel = new JPanel();
	    summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.X_AXIS));
	    summaryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
	    summaryPanel.setBackground(new Color(240, 240, 240));
	    
	    JPanel summaryCards = new JPanel(new GridLayout(1, 3, 20, 20));
	    summaryCards.setMaximumSize(new Dimension(1200, 180));
	    summaryCards.add(createSummaryCard("Total Balance", value1, Color.GREEN));
	    summaryCards.add(createSummaryCard("Monthly Income", value2, new Color(0, 150, 136)));
	    summaryCards.add(createSummaryCard("Monthly Expenses", value3, new Color(219, 68, 55)));
	    
	    summaryPanel.add(summaryCards);
	    centerWrapper.add(summaryPanel);
	    centerWrapper.add(Box.createRigidArea(new Dimension(0, 30)));
        
	 // 2. First row of charts (pie chart and recent budgets) - centered
	    JPanel firstChartRow = new JPanel();
	    firstChartRow.setLayout(new BoxLayout(firstChartRow, BoxLayout.X_AXIS));
	    firstChartRow.setAlignmentX(Component.CENTER_ALIGNMENT);
	    
	    JPanel pieChartPanel = createChartContainerPanel(createPieChartPanel(), "Monthly Expenses");
	    pieChartPanel.setPreferredSize(new Dimension(600, 400));
	    
	    JPanel recentBudgetsPanel = createChartContainerPanel(createRecentBudgetsPanel(user_id), "Recent Budgets");
	    recentBudgetsPanel.setPreferredSize(new Dimension(600, 400));
	    
	    firstChartRow.add(pieChartPanel);
	    firstChartRow.add(Box.createRigidArea(new Dimension(30, 0)));
	    firstChartRow.add(recentBudgetsPanel);
	    
	    centerWrapper.add(firstChartRow);
	    centerWrapper.add(Box.createRigidArea(new Dimension(0, 30)));

	    // 3. Second row of charts (bar chart and 7-day chart) - centered
	    JPanel secondChartRow = new JPanel();
	    secondChartRow.setLayout(new BoxLayout(secondChartRow, BoxLayout.X_AXIS));
	    secondChartRow.setAlignmentX(Component.CENTER_ALIGNMENT);
	    
	    JPanel barChartPanel = createChartContainerPanel(createBarChartPanel(), "Income vs Expenses");
	    barChartPanel.setPreferredSize(new Dimension(600, 400));
	    
	    JPanel sevenDayChartPanel = createChartContainerPanel(createSevenDayChartPanel(user_id), "7-Day Trend");
	    sevenDayChartPanel.setPreferredSize(new Dimension(600, 400));
	    
	    secondChartRow.add(barChartPanel);
	    secondChartRow.add(Box.createRigidArea(new Dimension(30, 0)));
	    secondChartRow.add(sevenDayChartPanel);
	    
	    centerWrapper.add(secondChartRow);
	    centerWrapper.add(Box.createRigidArea(new Dimension(0, 30)));

	    // 4. Third row (cash flow and recent transactions) - centered
	    JPanel thirdRow = new JPanel();
	    thirdRow.setLayout(new BoxLayout(thirdRow, BoxLayout.X_AXIS));
	    thirdRow.setAlignmentX(Component.CENTER_ALIGNMENT);
	    
	    JPanel cashFlowPanel = createChartContainerPanel(createCashFlowPanel(user_id), "Cash Flow");
	    cashFlowPanel.setPreferredSize(new Dimension(600, 400));
	    
	    JPanel recentTransactions = createChartContainerPanel(recentTransactionsPanel(), "Recent Transactions");
	    recentTransactions.setPreferredSize(new Dimension(600, 400));
	    
	    thirdRow.add(cashFlowPanel);
	    thirdRow.add(Box.createRigidArea(new Dimension(30, 0)));
	    thirdRow.add(recentTransactions);
	    
	    centerWrapper.add(thirdRow);
	    
	    // Add center wrapper to main content
	    mainContentPanel.add(centerWrapper);

	    // Create scroll pane with improved scrolling
	    JScrollPane scrollPane = new JScrollPane(mainContentPanel);
	    scrollPane.setBorder(BorderFactory.createEmptyBorder());
	    scrollPane.getVerticalScrollBar().setUnitIncrement(20);
	    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    
	    // Final container panel
	    JPanel containerPanel = new JPanel(new BorderLayout());
	    containerPanel.add(scrollPane, BorderLayout.CENTER);
	    
	    return containerPanel;
	}

	// Helper method to create consistent chart containers
	private JPanel createChartContainerPanel(JPanel chartPanel, String title) {
	    RoundedPanel container = new RoundedPanel();
	    container.setLayout(new BorderLayout());
	    container.setBorder(BorderFactory.createCompoundBorder(
	        BorderFactory.createTitledBorder(
	            BorderFactory.createLineBorder(new Color(200, 200, 200)),
	            title,
	            TitledBorder.CENTER,
	            TitledBorder.TOP,
	            new Font("Arial", Font.BOLD, 14),
	            Color.BLACK
	        ),
	        BorderFactory.createEmptyBorder(15, 15, 15, 15)
	    ));
	    container.setBackground(Color.WHITE);
	    container.add(chartPanel, BorderLayout.CENTER);
	    return container;
	}
    
    private JPanel createSummaryCard(String title,String value, Color color) {
    	
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.BLACK);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLabel);
        
        return card;
    }

    private JPanel createPieChartPanel() {
    	
    	 DefaultPieDataset dataset = new DefaultPieDataset();
    	    dataset.setValue("Loading...", 1);
    	    
    	    if (chartController == null) {
    	        return createErrorPanel("Chart controller not initialized");
    	    }
    	    
            try {
            // Get data from controller
            Map<String, Map<String, Double>> data = chartController.getCategoryData("this_month", user_id);
            dataset.clear();
            
            // Create dataset with actual expense data
            
            if (data != null && data.get("expenses") != null && !data.get("expenses").isEmpty()) {
                data.get("expenses").forEach(dataset::setValue);
            } else {
                dataset.setValue("No Data", 1);
            }

            // Create chart with no title (we'll add custom title later)
            JFreeChart chart = ChartFactory.createPieChart(
                null, // No title
                dataset, 
                false, // No legend
                true,  // Tooltips
                false
            );

            // Customize chart
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlineVisible(false);
            plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
            plot.setSimpleLabels(true);
            plot.setInteriorGap(0.05); // Smaller pie chart
            
            // Set custom colors
            int i = 0;
            Color[] colors = {
                new Color(79, 129, 189), 
                new Color(192, 80, 77), 
                new Color(155, 187, 89), 
                new Color(128, 100, 162),
                new Color(75, 172, 198), 
                new Color(247, 150, 70)
            };
            
            for (Object key : dataset.getKeys()) {
                if (i < colors.length) {
                    plot.setSectionPaint(key.toString(), colors[i]);
                    i++;
                }
            }

            // Create panel with custom title and padding
            RoundedPanel panel = new RoundedPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel title = new JLabel("Monthly Expenses", SwingConstants.CENTER);
            title.setFont(new Font("SansSerif", Font.BOLD, 12));
            panel.add(title, BorderLayout.NORTH);
            
            ChartPanel chartPanel = new ChartPanel(chart) {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(350, 250); // Smaller size
                }
            };
            chartPanel.setBackground(Color.WHITE);
            panel.add(chartPanel, BorderLayout.CENTER);
            
            return panel;
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorPanel("Error loading expense data");
        }
    }

    private JPanel createBarChartPanel() {
    	  DefaultCategoryDataset dataset = new DefaultCategoryDataset();
          dataset.addValue(0, "Amount", "Loading...");
          
          if (chartController == null) {
              return createErrorPanel("Chart controller not initialized");
          }
    	
        try {
            // Get data from controller
            Map<String, Map<String, Double>> data = chartController.getCategoryData("this_month", user_id);
            
            // Create dataset
          
            
            dataset.clear();
            
            double totalIncome = data != null && data.get("income") != null ? 
                data.get("income").values().stream().mapToDouble(Double::doubleValue).sum() : 0;
            double totalExpenses = data != null && data.get("expenses") != null ? 
                data.get("expenses").values().stream().mapToDouble(Double::doubleValue).sum() : 0;
            
            dataset.addValue(totalIncome, "Amount", "Income");
            dataset.addValue(totalExpenses, "Amount", "Expenses");
            dataset.addValue(totalIncome - totalExpenses, "Amount", "Net");

            // Create chart with no title
            JFreeChart chart = ChartFactory.createBarChart(
                null, // No title
                "",
                "Amount ",
                dataset,
                PlotOrientation.VERTICAL,
                false, // No legend
                true,  // Tooltips
                false
            );

            // Customize chart
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            
            // Custom renderer
            BarRenderer renderer = new BarRenderer();
            renderer.setSeriesPaint(0, new Color(50, 150, 50)); 
            renderer.setSeriesPaint(1, new Color(200, 50, 50)); 
            renderer.setSeriesPaint(2, new Color(50, 50, 200)); 
            
            // Value labels
            renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getCurrencyInstance()));
            renderer.setBaseItemLabelsVisible(true);
            renderer.setBaseItemLabelFont(new Font("SansSerif", Font.PLAIN, 10));
            
            plot.setRenderer(renderer);
            
            // Format Y-axis
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setNumberFormatOverride(NumberFormat.getCurrencyInstance());
            plot.getRangeAxis().setUpperMargin(0.15);
            
            // Format X-axis
            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
            plot.getDomainAxis().setCategoryMargin(0.2);

            // Create panel with custom title and padding
            RoundedPanel panel = new RoundedPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel title = new JLabel("Income vs Expenses (Monthly)", SwingConstants.CENTER);
            title.setFont(new Font("SansSerif", Font.BOLD, 12));
            panel.add(title, BorderLayout.NORTH);
            
            ChartPanel chartPanel = new ChartPanel(chart) {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(350, 250); 
                }
            };
            chartPanel.setBackground(Color.WHITE);
            panel.add(chartPanel, BorderLayout.CENTER);
            
            return panel;
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorPanel("Error loading summary data");
        }
    }

    private JPanel createErrorPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel errorLabel = new JLabel(message, SwingConstants.CENTER);
        errorLabel.setForeground(Color.RED);
        panel.add(errorLabel, BorderLayout.CENTER);
        return panel;
    }
 
    private JPanel recentTransactionsPanel() {
    	
    	Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        RoundedPanel panel = new RoundedPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));
        panel.setBackground(Color.WHITE);
        
        // Sample data - replace with your actual transaction data
        String[] columns = {"Date", "Description", "Category", "Amount","Type"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 3 -> Double.class;  // Amount
                    default -> String.class;
                };
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };        
        JTable table = new JTable(tableModel);
        tableModel.setRowCount(0);
        connection con =new connection();
        
        try {
            String query1 = "SELECT  date, description, category, amount, type FROM Transactions WHERE user_id ="+user_id+" AND MONTH(date) ="+month+" AND YEAR(date) ="+year+" ORDER BY date DESC ;";
                    
            ResultSet res = con.stm.executeQuery(query1);
            
            while (res.next()) {

                LocalDate date = res.getDate("date").toLocalDate();
                String description = res.getString("description");
                String category = res.getString("category");
                double amount = res.getDouble("amount");
                String type = res.getString("type");
                
                
                tableModel.addRow(new Object[]{
                    date.format(DateTimeFormatter.ISO_DATE),
                    description,
                    category,
                    Double.valueOf(amount),
                    type 
                });
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading transactions: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        JLabel title = new JLabel("Transactions", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(title, BorderLayout.NORTH);
       JScrollPane scrollPane = new JScrollPane(table);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSevenDayChartPanel(int user_id) {
        RoundedPanel panel = new RoundedPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        try {
            // Get data for last 7 days
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            connection con =new connection();
            
            String query = "SELECT DATE(date) as day, " +
                           "SUM(CASE WHEN type = 'Income' THEN amount ELSE 0 END) as income, " +
                           "SUM(CASE WHEN type = 'Expense' THEN amount ELSE 0 END) as expense " +
                           "FROM Transactions " +
                           "WHERE user_id ="+user_id+" AND date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                           "GROUP BY DATE(date) ORDER BY DATE(date)";
            
            ResultSet res = con.stm.executeQuery(query);
            
            // Fill with empty data first
            LocalDate today = LocalDate.now();
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                String dayName = date.format(DateTimeFormatter.ofPattern("E")); // Short day name
                dataset.addValue(0, "Income", dayName);
                dataset.addValue(0, "Expense", dayName);
            }
            
            // Update with actual data
            while (res.next()) {
                LocalDate date = res.getDate("day").toLocalDate();
                String dayName = date.format(DateTimeFormatter.ofPattern("E"));
                double income = res.getDouble("income");
                double expense = res.getDouble("expense");
                
                dataset.addValue(income, "Income", dayName);
                dataset.addValue(expense, "Expense", dayName);
            }
            
            // Create chart
            JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Day", 
                "Amount", 
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
            );
            
            // Customize chart
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(50, 150, 50)); // Green for income
            renderer.setSeriesPaint(1, new Color(200, 50, 50)); // Red for expenses
            
            // Add value labels
            renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            renderer.setBaseItemLabelsVisible(true);
            
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setNumberFormatOverride(NumberFormat.getCurrencyInstance());
            plot.getRangeAxis().setUpperMargin(0.15);
            
            ChartPanel chartPanel = new ChartPanel(chart);
            panel.add(chartPanel, BorderLayout.CENTER);
            
        } catch (SQLException e) {
            e.printStackTrace();
            panel.add(new JLabel("Error loading 7-day data", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        JLabel title = new JLabel("Last 7 days", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(title, BorderLayout.NORTH);
        
        return panel;
    }

    
    private JPanel createRecentBudgetsPanel(int user_id) {
        RoundedPanel panel = new RoundedPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Recent Budgets"));
        panel.setBackground(Color.WHITE);
        
        JLabel title = new JLabel("Budgets", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(title, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Reduced padding
        contentPanel.setBackground(Color.WHITE);
        try {
    	connection conn=new connection();
    	
        String query = "SELECT b.category, b.budget_limit, b.startDate, b.endDate, " +
                     "(SELECT COALESCE(SUM(t.amount), 0) FROM Transactions t " +
                     "WHERE t.user_id = "+user_id+" AND t.category = b.category AND t.type = 'Expense' " +
                     "AND t.date BETWEEN b.startDate AND b.endDate) as used " +
                     "FROM Budgets b WHERE b.user_id = "+user_id+" ORDER BY b.budget_id DESC LIMIT 4";
        
       
        ResultSet res = conn.stm.executeQuery(query);
        
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");
            
            while (res.next()) {
                String category = res.getString("category");
                double budgetLimit = res.getDouble("budget_limit");
                double used = res.getDouble("used");
                LocalDate startDate = res.getDate("startDate").toLocalDate();
                LocalDate endDate = res.getDate("endDate").toLocalDate();
                double remaining = budgetLimit - used;
                int percentage = (int) ((used / budgetLimit) * 100);
                
                // Create compact budget item panel
                JPanel budgetPanel = new JPanel(new BorderLayout(3, 3)); // Reduced gaps
                budgetPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3)); // Smaller padding
                budgetPanel.setBackground(Color.WHITE);
                budgetPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80)); // Fixed height
                
                // Top row - category and date (single line)
                JPanel topPanel = new JPanel(new BorderLayout());
                topPanel.setBackground(Color.WHITE);
                
                JLabel categoryLabel = new JLabel(category);
                categoryLabel.setFont(new Font("Arial", Font.BOLD, 11)); // Smaller font
                
                JLabel dateLabel = new JLabel(startDate.format(dateFormatter) + "-" + endDate.format(dateFormatter));
                dateLabel.setFont(new Font("Arial", Font.PLAIN, 9)); // Smaller font
                dateLabel.setForeground(Color.GRAY);
                
                topPanel.add(categoryLabel, BorderLayout.WEST);
                topPanel.add(dateLabel, BorderLayout.EAST);
                budgetPanel.add(topPanel, BorderLayout.NORTH);
                
                // Compact progress bar
                JProgressBar progressBar = new JProgressBar(0, 100) {
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(super.getPreferredSize().width, 12); // Slimmer bar
                    }
                };
                progressBar.setValue(percentage);
                progressBar.setStringPainted(true);
                progressBar.setString(percentage + "%");
                progressBar.setFont(new Font("Arial", Font.PLAIN, 9)); // Smaller font
                
                // Set color based on usage
                if (percentage > 90) {
                    progressBar.setForeground(new Color(220, 53, 69)); // Red
                } else if (percentage > 70) {
                    progressBar.setForeground(new Color(255, 193, 7)); // Orange
                } else {
                    progressBar.setForeground(new Color(40, 167, 69)); // Green
                }
                
                progressBar.setBackground(new Color(230, 230, 230));
                budgetPanel.add(progressBar, BorderLayout.CENTER);
                
                // Bottom row - amounts (single line compact)
                JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 2, 2)); // Tight grid
                bottomPanel.setBackground(Color.WHITE);
                
                bottomPanel.add(createCompactAmountLabel("Used", used));
                bottomPanel.add(createCompactAmountLabel("Limit", budgetLimit));
                
                JLabel remainingLabel = createCompactAmountLabel("Left", remaining);
                remainingLabel.setForeground(remaining < (budgetLimit * 0.1) ? Color.RED : 
                                          (remaining < (budgetLimit * 0.3) ? Color.ORANGE : Color.GREEN));
                bottomPanel.add(remainingLabel);
                
                budgetPanel.add(bottomPanel, BorderLayout.SOUTH);
                
                // Add minimal spacing between items
                if (contentPanel.getComponentCount() > 0) {
                    contentPanel.add(Box.createRigidArea(new Dimension(0, 8))); // Reduced spacing
                }
                contentPanel.add(budgetPanel);
            }
            
            if (contentPanel.getComponentCount() == 0) {
                contentPanel.add(new JLabel("No budgets found", SwingConstants.CENTER));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            contentPanel.add(new JLabel("Error loading budgets", SwingConstants.CENTER));
        }
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JLabel createCompactAmountLabel(String prefix, double amount) {
        JLabel label = new JLabel(String.format("%s: $%.2f", prefix, amount), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 9)); // Very compact font
        return label;
    }
    
    private JPanel createCashFlowPanel(int user_id) {
        RoundedPanel panel = new RoundedPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Cash Flow"));
        panel.setBackground(Color.WHITE);
        
        JLabel title = new JLabel("Cash flow(Transaction)", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(title, BorderLayout.NORTH);
        
        // Main content panel with grid layout for perfect alignment
        JPanel contentPanel = new JPanel(new GridLayout(4, 3, 5, 10)); // 4 rows, 3 columns
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(Color.WHITE);

        // Column headers
        contentPanel.add(createHeaderLabel("Period"));
        contentPanel.add(createHeaderLabel("  Income   "));
        contentPanel.add(createHeaderLabel("  Expense  "));
        contentPanel.add(createHeaderLabel("Net"));

        try {
            // Today's data
            addCashFlowRow(contentPanel, "Today", user_id, "DAY");
            
            // Weekly data
            addCashFlowRow(contentPanel, "This Week", user_id, "WEEK");
            
            // Monthly data
            addCashFlowRow(contentPanel, "This Month", user_id, "MONTH");
            
        } catch (SQLException e) {
            e.printStackTrace();
            panel.add(new JLabel("Error loading cash flow data", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private void addCashFlowRow(JPanel panel, String period, int user_id, String rangeType) throws SQLException {
        // Get data
    	
    	connection conn=new connection();
        String query = "SELECT " +
            "SUM(CASE WHEN type = 'Income' THEN amount ELSE 0 END) as income, " +
            "SUM(CASE WHEN type = 'Expense' THEN amount ELSE 0 END) as expense " +
            "FROM Transactions WHERE user_id ="+user_id+" AND ";
        
        if ("DAY".equals(rangeType)) {
            query += "DATE(date) = CURRENT_DATE()";
        } else if ("WEEK".equals(rangeType)) {
            query += "date >= DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY)";
        } else {
            query += "MONTH(date) = MONTH(CURRENT_DATE()) AND YEAR(date) = YEAR(CURRENT_DATE())";
        }
        
       
        ResultSet rs = conn.stm.executeQuery(query);
        
        if (rs.next()) {
            double income = rs.getDouble("income");
            double expense = rs.getDouble("expense");
            double net = income - expense;
            
            panel.add(createPeriodLabel(period));
            panel.add(createValueLabel(income, true));
            panel.add(createValueLabel(expense, false));
            panel.add(createValueLabel(net, net >= 0));
        }
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return label;
    }

    private JLabel createPeriodLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        return label;
    }

    private JLabel createValueLabel(double value, boolean positive) {
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        JLabel label = new JLabel(currency.format(value), SwingConstants.RIGHT);
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        label.setForeground(positive ? new Color(0, 100, 0) : new Color(150, 0, 0));
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        return label;
    }
   

    private void setupExportListener() {
        exportView.setExportListener(() -> {
            String selectedType = exportView.getSelectedType();
            String selectedPeriod = exportView.getSelectedPeriod();
            
            try {
                // Show file chooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save PDF");
                String fileName = selectedType.toLowerCase().replace(" ", "_") + "_" + 
                                 selectedPeriod.replace(" ", "_") + ".pdf";
                fileChooser.setSelectedFile(new File(fileName));
                
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    String filePath = file.getAbsolutePath();
                    
                    // Delegate export logic to controller
                    if (selectedType.equals("Transaction Report")) {
                        exportController.exportTransactions(selectedPeriod, filePath);
                    } else {
                        exportController.exportSummary(selectedPeriod, filePath);
                    }
                    
                    // Show success message
                    JOptionPane.showMessageDialog(this,
                        "Export completed successfully!\nSaved to: " + filePath,
                        "Export Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Return to dashboard view
                    CardLayout cl = (CardLayout) mainPanel.getLayout();
                    cl.show(mainPanel, "dashboard");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Export failed: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    } 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new dashboard(10).setVisible(true);
        });
    }
}