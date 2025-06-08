
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class ExportView extends JPanel {
    private JComboBox<String> typeComboBox;
    private JComboBox<String> periodComboBox;
    private JButton exportButton;
    
    public ExportView() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Main content panel with card-like appearance
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), // Fixed border creation
            BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Export Financial Data");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        cardPanel.add(titleLabel, gbc);
        
        // Add vertical space
        gbc.gridy = 1;
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)), gbc);
        
        // Report Type Row
        JLabel typeLabel = new JLabel("Report Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        cardPanel.add(typeLabel, gbc);
        
        typeComboBox = new JComboBox<>(new String[]{"Transaction Report", "Financial Summary"});
        typeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        typeComboBox.setPreferredSize(new Dimension(250, 40));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        cardPanel.add(typeComboBox, gbc);
        
        // Time Period Row
        JLabel periodLabel = new JLabel("Time Period:");
        periodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 3;
        cardPanel.add(periodLabel, gbc);
        
        periodComboBox = new JComboBox<>();
        periodComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        periodComboBox.setPreferredSize(new Dimension(250, 40));
        
        // Populate period combo box
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        for (int i = 0; i < 6; i++) {
            YearMonth month = YearMonth.from(now.minusMonths(i));
            periodComboBox.addItem(month.format(formatter));
        }
        
        gbc.gridx = 1;
        cardPanel.add(periodComboBox, gbc);
        
        // Add vertical space
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        cardPanel.add(Box.createRigidArea(new Dimension(0, 30)), gbc);
        
        // Export Button
        exportButton = new JButton("GENERATE PDF REPORT");
        exportButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        exportButton.setBackground(new Color(76,175,80));
        exportButton.setForeground(Color.black);
        exportButton.setFocusPainted(false);
        exportButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 100, 150)), 
            BorderFactory.createEmptyBorder(12, 40, 12, 40)
        ));
        
      
        exportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exportButton.setBackground(new Color(90, 150, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exportButton.setBackground(new Color(70, 130, 180));
            }
        });
        
        gbc.gridy = 5;
        cardPanel.add(exportButton, gbc);
        
        // Add the card panel to the main panel (centered)
        add(cardPanel);
    }
    
    public String getSelectedType() {
        return (String) typeComboBox.getSelectedItem();
    }
    
    public String getSelectedPeriod() {
        return (String) periodComboBox.getSelectedItem();
    }
    
    public void setExportListener(Runnable listener) {
        exportButton.addActionListener(e -> listener.run());
    }
}