package admin.views;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.time.LocalDate;
import java.util.*;

public class ActiveUsersList extends JPanel{
    private JTable userTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> sortCombo;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private JButton buttonSearchName;
    private JButton buttonFilterDate;

    // filter friend
    private JComboBox<String> filterOperator;
    private JTextField actionCountField;
    private JButton buttonFilter;
    private JButton buttonClearFilter;

    public ActiveUsersList(){
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        loadSampleData();
    }

    private void initComponents(){
        JLabel titleLabel = new JLabel("Danh sách người dùng hoạt động");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel filterPanel = createFilterPanel();

        JScrollPane tableScrollPane = createTablePanel();

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    private JPanel createFilterPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // row1
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        row1.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Lọc theo thời gian"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel startDate = new JLabel("Từ ngày");
        DatePickerSettings startS = new DatePickerSettings();
        startS.setFormatForDatesCommonEra("dd/MM/yyyy");
        startS.setFontValidDate(new Font("Arial", Font.PLAIN, 13));
        startS.setFontInvalidDate(new Font("Arial", Font.PLAIN, 13));

        startDatePicker = new DatePicker(startS);
        startDatePicker.setDateToToday();
        startDatePicker.setDate(LocalDate.now().minusDays(7));
        startDatePicker.getComponentDateTextField().setPreferredSize(new Dimension(150, 30));
        startDatePicker.getComponentDateTextField().setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel endDate = new JLabel("Đến ngày");
        DatePickerSettings endS = new DatePickerSettings();
        endS.setFormatForDatesCommonEra("dd/MM/yyyy");
        endS.setFontValidDate(new Font("Arial", Font.PLAIN, 13));
        endS.setFontInvalidDate(new Font("Arial", Font.PLAIN, 13));

        endDatePicker = new DatePicker(endS);
        endDatePicker.setDateToToday(); 
        endDatePicker.getComponentDateTextField().setPreferredSize(new Dimension(150, 30));
        endDatePicker.getComponentDateTextField().setFont(new Font("Arial", Font.PLAIN, 13));

        buttonFilterDate = new JButton("Lọc theo ngày");
        buttonFilterDate.setPreferredSize(new Dimension(150, 30));
        buttonFilterDate.setFont(new Font("Arial", Font.PLAIN, 13));
        buttonFilterDate.setBackground(new Color(33, 150, 243));
        buttonFilterDate.setForeground(Color.WHITE);
        buttonFilterDate.setFocusPainted(false);
        buttonFilterDate.setBorderPainted(false);
        buttonFilterDate.setCursor(new Cursor(Cursor.HAND_CURSOR));

        row1.add(startDate);
        row1.add(startDatePicker);
        row1.add(endDate);
        row1.add(endDatePicker);
        row1.add(buttonFilterDate);

        // row2
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        row2.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Lọc theo tên đăng nhập");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        searchField = new JTextField(25);
        searchField.setPreferredSize(new Dimension(250, 30));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));

        buttonSearchName = new JButton("Tìm kiếm");
        buttonSearchName.setPreferredSize(new Dimension(120, 30));
        buttonSearchName.setFont(new Font("Arial", Font.PLAIN, 13));
        buttonSearchName.setBackground(new Color(100, 181, 246));
        buttonSearchName.setForeground(Color.WHITE);
        buttonSearchName.setFocusPainted(false);
        buttonSearchName.setBorderPainted(false);
        buttonSearchName.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel sortLabel = new JLabel("Sắp xếp:");
        sortLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        sortCombo = new JComboBox<>(new String[]{
            "Thời gian (Mới nhất)",
            "Thời gian (Cũ nhất)",
            "Tên đăng nhập (A-Z)",
            "Tên đăng nhập (Z-A)"
        });
        sortCombo.setPreferredSize(new Dimension(220, 30));
        sortCombo.setFont(new Font("Arial", Font.PLAIN, 13));

        row2.add(searchLabel);
        row2.add(searchField);
        row2.add(buttonSearchName);
        row2.add(sortLabel);
        row2.add(sortCombo);

        // row 3
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        row3.setBackground(Color.WHITE);
        row3.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Lọc theo số lượng hoạt động"),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        JLabel filterLabel = new JLabel("Điều kiện:");
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        filterOperator = new JComboBox<>(new String[]{
            "Bằng (=)",
            "Lớn hơn (>)",
            "Nhỏ hơn (<)",
            "Lớn hơn hoặc bằng (≥)",
            "Nhỏ hơn hoặc bằng (≤)"
        });
        filterOperator.setPreferredSize(new Dimension(180, 30));
        filterOperator.setFont(new Font("Arial", Font.PLAIN, 13));

        actionCountField = new JTextField(10);
        actionCountField.setPreferredSize(new Dimension(100, 30));
        actionCountField.setFont(new Font("Arial", Font.PLAIN, 14));
        actionCountField.setHorizontalAlignment(JTextField.CENTER);

        JLabel actionLabel = new JLabel("hoạt động");
        actionLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        buttonFilter = new JButton("Áp dụng");
        buttonFilter.setPreferredSize(new Dimension(110, 30));
        buttonFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        buttonFilter.setBackground(new Color(76, 175, 80));
        buttonFilter.setForeground(Color.WHITE);
        buttonFilter.setFocusPainted(false);
        buttonFilter.setBorderPainted(false);
        buttonFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonClearFilter = new JButton("Xoá lọc");
        buttonClearFilter.setPreferredSize(new Dimension(110, 30));
        buttonClearFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        buttonClearFilter.setBackground(new Color(244, 67, 54));
        buttonClearFilter.setForeground(Color.WHITE);
        buttonClearFilter.setFocusPainted(false);
        buttonClearFilter.setBorderPainted(false);
        buttonClearFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));

        row3.add(filterLabel);
        row3.add(filterOperator);
        row3.add(actionCountField);
        row3.add(actionLabel);
        row3.add(buttonFilter);
        row3.add(buttonClearFilter);

        panel.add(row1);
        panel.add(row2);
        panel.add(row3);
        return panel;
    }

    private JScrollPane createTablePanel(){
        String[] columns = {
            "STT", "Tên đăng nhập", "Số lần mở", "Số người chat", "Số nhóm chat"
        };
        tableModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 13));
        userTable.setRowHeight(40);
        userTable.setShowGrid(true);
        userTable.setGridColor(new Color(220, 220, 220));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        userTable.getTableHeader().setBackground(new Color(240, 240, 240));
        userTable.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Set column width
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);   
        userTable.getColumnModel().getColumn(1).setPreferredWidth(130); 
        userTable.getColumnModel().getColumn(2).setPreferredWidth(180); 
        userTable.getColumnModel().getColumn(3).setPreferredWidth(220);  
        userTable.getColumnModel().getColumn(4).setPreferredWidth(150);  
        
        // Align center number 
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        userTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        return scrollPane;
    }

    private void loadSampleData() {
        Object[][] data = {
            {1, "user001", 120, 15, 5},
            {2, "user002", 98, 10, 4},
            {3, "user003", 76, 8, 3},
            {4, "user004", 142, 18, 6},
            {5, "user005", 160, 20, 7},
            {6, "user006", 54, 5, 2},
            {7, "user007", 115, 12, 5},
            {8, "user008", 134, 16, 6},
            {9, "user009", 68, 7, 3},
            {10, "user010", 150, 19, 6},
            {11, "user011", 82, 9, 3},
            {12, "user012", 127, 14, 5}
        };
        
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }
}