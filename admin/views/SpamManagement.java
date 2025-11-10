package admin.views;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.time.LocalDate;
import java.util.*;

public class SpamManagement extends JPanel{
    
    private JTable userTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> sortCombo;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker; 
    private JButton buttonSearchName;
    private JButton buttonFilterDate;

    private ArrayList<JButton> deleteButtons;

    public SpamManagement(){
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        deleteButtons = new ArrayList<>();
        initComponents();
        loadSampleData();
    }

    private void initComponents(){
        JLabel titleLabel = new JLabel("Danh sách báo cáo spam");
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

        panel.add(row1);
        panel.add(row2);
        return panel;
    }

    private JScrollPane createTablePanel(){
        String[] columns = {
            "ID bị báo cáo", "Họ tên", "ID báo cáo", 
            "Thời gian", "Trạng thái", "Hành động"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        ActionButtonRenderer.setActionButtons(deleteButtons);
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 13));
        userTable.setRowHeight(45);
        userTable.setShowGrid(true);
        userTable.setGridColor(new Color(220, 220, 220));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        userTable.getTableHeader().setBackground(new Color(240, 240, 240));
        userTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        userTable.getColumnModel().getColumn(0).setPreferredWidth(120);   
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150);  
        userTable.getColumnModel().getColumn(2).setPreferredWidth(120);  
        userTable.getColumnModel().getColumn(3).setPreferredWidth(150);  
        userTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        userTable.getColumnModel().getColumn(5).setPreferredWidth(100);   
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        userTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        // Set custom renderer and editor for action column
        userTable.getColumnModel().getColumn(5).setCellRenderer(new ActionButtonRenderer());
        userTable.getColumnModel().getColumn(5).setCellEditor(new ActionButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        return scrollPane;
    } 

    private void loadSampleData() {
        Object[][] data = {
            {"user005", "Hoàng Minh Đức", "user023", "25/10/2024 14:30:15", "Chờ xử lý", ""},
            {"user012", "Lý Thu Lan", "user034", "26/10/2024 09:15:42", "Chờ xử lý", ""},
            {"user008", "Bùi Lan Hương", "user019", "26/10/2024 16:20:30","Đã khoá", ""},
            {"user015", "Trần Minh Tuấn", "user041", "27/10/2024 10:45:18", "Chờ xử lý", ""},
            {"user021", "Nguyễn Thị Mai", "user007", "27/10/2024 13:30:55", "Chờ xử lý", ""},
            {"user018", "Phạm Văn Hòa", "user052", "27/10/2024 15:15:22",  "Đã khoá", ""},
            {"user029", "Đỗ Thanh Hương", "user013", "28/10/2024 08:40:10", "Chờ xử lý", ""},
            {"user033", "Lê Quang Vinh", "user028", "28/10/2024 11:25:45", "Chờ xử lý", ""},
            {"user025", "Vũ Thị Hoa", "user045", "28/10/2024 14:50:33",  "Đã khoá", ""},
            {"user037", "Hoàng Văn Sơn", "user016","28/10/2024 16:10:20", "Chờ xử lý", ""}
        };
        
        for (Object[] row : data) {
            tableModel.addRow(row);

            JButton btn = createDeleteButton((String) row[0]);
            if ("Đã khoá".equalsIgnoreCase((String) row[4])) {
                btn.setEnabled(false);
                btn.setBackground(Color.GRAY);
                btn.setCursor(Cursor.getDefaultCursor());
            }

            deleteButtons.add(btn);
        }
    }
    
    private JButton createDeleteButton(String username) {
        JButton button = new JButton("Khóa");
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(244, 67, 54));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Thêm sự kiện click
        button.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(SpamManagement.this);
            int confirm = JOptionPane.showConfirmDialog(
                    parentWindow,
                    "Bạn có chắc chắn muốn khóa tài khoản '" + username + "'?",
                    "Xác nhận khóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                BlockUserByUsername(username);
                JOptionPane.showMessageDialog(
                        parentWindow,
                        "Đã khóa tài khoản '" + username + "' thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        return button;
    }

    private void BlockUserByUsername(String username) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object nameValue = tableModel.getValueAt(i, 0);
            if (nameValue != null && nameValue.equals(username)) {
                String status = (String) tableModel.getValueAt(i, 4);
                if ("Chờ xử lý".equalsIgnoreCase(status)) {
                    tableModel.setValueAt("Đã khóa", i, 4);

                    JButton btn = deleteButtons.get(i);
                    btn.setEnabled(false);
                    btn.setBackground(Color.GRAY);
                    btn.setCursor(Cursor.getDefaultCursor());
                }
                break;
            }
        }
    }    
    // Getters for Controller
    public JTable getUserTable() { return userTable; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getSearchField() { return searchField; }
    public JComboBox<String> getSortCombo() { return sortCombo; }
    public DatePicker getStartDatePicker() { return startDatePicker; }
    public DatePicker getEndDatePicker() { return endDatePicker; }
    public JButton getButtonSearchName() { return buttonSearchName; }
    public JButton getButtonFilterDate() { return buttonFilterDate; }
}