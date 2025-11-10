package admin.views;
import javax.swing.*;
import java.awt.*;

public class ActiveUsersChart extends JPanel{
    
    private JComboBox<String> yearCombo;
    private JButton buttonView;
    private Chart chartPanelActive;
    
    private String[] months = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", 
                                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
    // private int[] userData = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] userData = {30, 90, 70, 70, 80, 90, 50, 10, 20, 40, 70, 60};
    private int selectedYear = 2024;
    
    public ActiveUsersChart(){
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponents();
    }
    
    private void initComponents(){
        JPanel topPanel = createTopPanel();
        JPanel statsPanel = createStatisticsPanel();
        
        chartPanelActive = new Chart(40, 80, 80, 40);
        chartPanelActive.setPreferredSize(new Dimension(0, 500));
        chartPanelActive.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        chartPanelActive.setTitle("Số lượng người hoạt động năm 2024", "Số lượng người dùng", "Tháng");
        chartPanelActive.setData(userData);
        chartPanelActive.setColumnName(months);
        
        add(topPanel, BorderLayout.NORTH);
        add(chartPanelActive, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel(){
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Biểu đồ số lượng người hoạt động theo năm");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(Color.WHITE);
        
        JLabel yearLabel = new JLabel("Chọn năm:");
        yearLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        String[] years = new String[10];
        int currentYear = 2024;
        for(int i = 0; i < 10; i++)
            years[i] = String.valueOf(currentYear - i);
        
        yearCombo = new JComboBox<>(years);
        yearCombo.setPreferredSize(new Dimension(120, 35));
        yearCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        buttonView = new JButton("Xem biểu đồ");
        buttonView.setPreferredSize(new Dimension(140, 35));
        buttonView.setFont(new Font("Arial", Font.PLAIN, 13));
        buttonView.setBackground(new Color(33, 150, 243));
        buttonView.setForeground(Color.WHITE);
        buttonView.setFocusPainted(false);
        buttonView.setBorderPainted(false);
        buttonView.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        filterPanel.add(yearLabel);
        filterPanel.add(yearCombo);
        filterPanel.add(buttonView);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(filterPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Total Users Card
        JPanel totalCard = createStatCard("Tổng số người hoạt động", "0", new Color(33, 150, 243));
        
        // Average Card
        JPanel avgCard = createStatCard("Trung bình/tháng", "0", new Color(76, 175, 80));
        
        // Peak Month Card
        JPanel peakCard = createStatCard("Tháng cao nhất", "--", new Color(255, 152, 0));
        
        panel.add(totalCard);
        panel.add(avgCard);
        panel.add(peakCard);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color){
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        valuePanel.setBackground(Color.WHITE);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valuePanel.add(valueLabel);
        
        card.add(titleLabel);
        card.add(valuePanel);
        
        return card;
    }
}