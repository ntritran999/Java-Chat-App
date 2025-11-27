package admin.views;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.time.LocalDate;


public class RegisteredUserList extends JPanel{

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> sortCombo;

    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private JButton buttonFilterDate;

    private JButton btnSearch;

    public RegisteredUserList(){
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponents();
    }

    private void initComponents(){
        JLabel titleLabel = new JLabel("Danh sách người dùng đăng ký mới");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel filterLabel = createFilterPanel();
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(filterLabel, BorderLayout.CENTER);
        
        JScrollPane tableScrollPane = createTablePanel();
        // Layout
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


        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        row2.setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Tên đăng nhập");
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(180, 30));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));

        btnSearch = new JButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(100, 30));
        btnSearch.setFont(new Font("Arial", Font.PLAIN, 13));
        btnSearch.setBackground(new Color(100, 181, 246));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel sortLabel = new JLabel("Sắp xếp:");
        sortLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        sortCombo = new JComboBox<>(new String[]{
        "Tên (A-Z)", "Tên (Z-A)", "Ngày tạo (Mới nhất)", "Ngày tạo (Cũ nhất)"
        });
        sortCombo.setPreferredSize(new Dimension(130, 30));
        sortCombo.setFont(new Font("Arial", Font.PLAIN, 13));

        row2.add(filterLabel);
        row2.add(searchField);
        row2.add(btnSearch);
        row2.add(sortLabel);
        row2.add(sortCombo);

        panel.add(row1);
        panel.add(row2);
        return panel;
    }

    private JScrollPane createTablePanel(){
        String[] columns = {
            "Tên đăng nhập", "Họ tên", "Email", "Ngày tạo", "Trạng thái"
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
        // userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        userTable.getTableHeader().setBackground(new Color(240, 240, 240));
        userTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        // Set column widths
        userTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        return scrollPane;
    }

    public JButton getButtonFilterDate() {
        return buttonFilterDate;
    }

    public DatePicker getStartDatePicker() {
        return startDatePicker;
    }

    public DatePicker getEndDatePicker() {
        return endDatePicker;
    }

    public JButton getBtnSearch() {
        return btnSearch;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JComboBox<String> getSortCombo() {
        return sortCombo;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getUserTable(){
        return userTable;
    }

}
