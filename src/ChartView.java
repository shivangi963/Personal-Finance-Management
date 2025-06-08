import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class ChartView extends JPanel {
    private final ChartController controller;
    private final int userId;
    private final JTabbedPane tabbedPane;
    
    // UI Components
    private JComboBox<String> chartTypeCombo;
    private JComboBox<String> timeRangeCombo;
    private JButton applyButton;
    private JPanel controlPanel;
    
    
    private static final Color INCOME_COLOR = new Color(59, 181, 74);
    private static final Color EXPENSE_COLOR = new Color(230, 76, 60);
    private static final Color NET_COLOR = new Color(76,175,80);
    private static final Font CHART_FONT = new Font("SansSerif", Font.PLAIN, 8);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 10);


    public ChartView(ChartController controller,int userId) {
        this.controller = controller;
        this.userId = userId;
		this.tabbedPane = new JTabbedPane();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        // Create control panel
        controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Chart Options"));
        
        // Chart type selection
        chartTypeCombo = new JComboBox<>(new String[]{"All Charts", "Income Only", "Expenses Only", "Summary Only"});
        
        // Time range selection
        timeRangeCombo = new JComboBox<>(new String[]{
            "Last 7 Days", 
            "Last 30 Days", 
            "Last 90 Days", 
            "This Month", 
            "Last Month", 
            "This Year",
            "Custom Range"
        });
        
        // Apply button
        applyButton = new JButton("Apply");
        applyButton.setBackground(new Color(76,175,80));
        applyButton.setFocusable(false);
        applyButton.addActionListener(e -> updateCharts());
        
        // Add components to control panel
        controlPanel.add(new JLabel("Chart Type:"));
        controlPanel.add(chartTypeCombo);
        controlPanel.add(new JLabel("Time Range:"));
        controlPanel.add(timeRangeCombo);
        controlPanel.add(applyButton);
        
        // Create tabbed pane for charts
        tabbedPane.setPreferredSize(new Dimension(450, 300));
        
        // Add components to main panel
        add(controlPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        
        // Initial chart load
        updateCharts();
    }

    private void updateCharts() {
        try {
            String timeRange = (String) timeRangeCombo.getSelectedItem();
            String chartType = (String) chartTypeCombo.getSelectedItem();
            
            if (timeRange.equals("Custom Range")) {
                showCustomDateRangeDialog();
                return;
            }
            
            // Get data for selected time range
            Map<String, Map<String, Double>> data = controller.getCategoryData(convertTimeRangeToKey(timeRange), userId);
            
            tabbedPane.removeAll();
            
            // Show selected charts based on user choice
            if (chartType.equals("All Charts") || chartType.equals("Income Only")) {
                tabbedPane.add("Income", createIncomeChart(data));
            }
            if (chartType.equals("All Charts") || chartType.equals("Expenses Only")) {
                tabbedPane.add("Expenses", createExpenseChart(data));
            }
            if (chartType.equals("All Charts") || chartType.equals("Summary Only")) {
                tabbedPane.add("Summary", createSummaryChart(data));
            }
            updateChartsWithData(data);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading chart data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private String convertTimeRangeToKey(String timeRange) {
        switch (timeRange) {
            case "Last 7 Days": return "last_7";
            case "Last 30 Days": return "last_30";
            case "Last 90 Days": return "last_90";
            case "This Month": return "this_month";
            case "Last Month": return "last_month";
            case "This Year": return "this_year";
            case "Custom Range":
            	showCustomDateRangeDialog();
            	return "custom"; 
            default: return "last_30";
        }
    }

    private ChartPanel createIncomeChart(Map<String, Map<String, Double>> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        data.get("income").forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart("Income by Category",dataset,true, true, false);
        customizePieChart(chart, INCOME_COLOR);
        return createPieChartPanel(chart);
    }

    private ChartPanel createExpenseChart(Map<String, Map<String, Double>> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        data.get("expenses").forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart("Expenses by Category",dataset, true, true, false);
        customizePieChart(chart, EXPENSE_COLOR);
        return createPieChartPanel(chart);
    }

    private ChartPanel createSummaryChart(Map<String, Map<String, Double>> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        double income = data.get("income").values().stream().mapToDouble(Double::doubleValue).sum();
        double expenses = data.get("expenses").values().stream().mapToDouble(Double::doubleValue).sum();

        dataset.addValue(income, "Amount", "Income");
        dataset.addValue(expenses, "Amount", "Expenses");
        dataset.addValue(income - expenses, "Amount", "Net");

        JFreeChart chart = ChartFactory.createBarChart(
            "Financial Summary",
            "",
            "Amount",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );
        customizeBarChart(chart);
        return createBarChartPanel(chart);
    }

	private void customizePieChart(JFreeChart chart, Color baseColor) {
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint(0, baseColor);
        plot.setSectionPaint(1, baseColor.darker());
        plot.setSectionPaint(2, baseColor.brighter());
        
        plot.setLabelFont(CHART_FONT);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        plot.setSimpleLabels(true);
        plot.setInteriorGap(0.05);
        
        chart.getTitle().setFont(TITLE_FONT);
        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);
    }

    private void customizeBarChart(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = new BarRenderer();
        
        renderer.setSeriesPaint(0, INCOME_COLOR);
        renderer.setSeriesPaint(1, EXPENSE_COLOR);
        renderer.setSeriesPaint(2, NET_COLOR);
        
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(CHART_FONT);
        
        plot.setRenderer(renderer);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(NumberFormat.getCurrencyInstance());
        
        chart.getTitle().setFont(TITLE_FONT);
        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);
    }

    private ChartPanel createPieChartPanel(JFreeChart chart) {
        ChartPanel panel = new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
            	return new Dimension(200,200);
            	}
        };
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setMinimumDrawWidth(100);
        panel.setMaximumDrawWidth(500);
        panel.setMinimumDrawHeight(100);
        panel.setMaximumDrawHeight(350);
        return panel;
    }
    private ChartPanel createBarChartPanel(JFreeChart chart) {
        ChartPanel panel = new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
            	return new Dimension(400,250);
            	}
        };
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setMinimumDrawWidth(100);
        panel.setMaximumDrawWidth(500);
        panel.setMinimumDrawHeight(100);
        panel.setMaximumDrawHeight(350);
        return panel;
    }
    
    private void showCustomDateRangeDialog() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Select Date Range", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 180);
        dialog.setUndecorated(true);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField startDateField = new JTextField(LocalDate.now().minusDays(30).toString());
        JTextField endDateField = new JTextField(LocalDate.now().toString());
        
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        panel.add(startDateField);
        panel.add(new JLabel("End Date (YYYY-MM-DD):"));
        panel.add(endDateField);
        
        JButton okButton = new JButton("OK");
        okButton.setBackground(new Color(76,175,80));
        okButton.addActionListener(e -> {
            try {
                LocalDate startDate = LocalDate.parse(startDateField.getText());
                LocalDate endDate = LocalDate.parse(endDateField.getText());
                
                if (startDate.isAfter(endDate)) {
                    JOptionPane.showMessageDialog(dialog, "Start date must be before end date");
                    return;
                }
       
                updateChartsWithCustomRange(startDate, endDate);
                dialog.dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use YYYY-MM-DD");
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(76,175,80));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void updateChartsWithCustomRange(LocalDate startDate, LocalDate endDate) {
        try {
            Map<String, Map<String, Double>> data = controller.getCustomDateRangeData(
                startDate, endDate, userId);
            updateChartsWithData(data);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading data for selected range: " + e.getMessage(),
                "Data Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateChartsWithData(Map<String, Map<String, Double>> data) {
        String chartType = (String) chartTypeCombo.getSelectedItem();
        tabbedPane.removeAll();
        
        if (chartType.equals("All Charts") || chartType.equals("Income Only")) {
            tabbedPane.add("Income", createIncomeChart(data));
        }
        if (chartType.equals("All Charts") || chartType.equals("Expenses Only")) {
            tabbedPane.add("Expenses", createExpenseChart(data));
        }
        if (chartType.equals("All Charts") || chartType.equals("Summary Only")) {
            tabbedPane.add("Summary", createSummaryChart(data));
        }
    }
}
