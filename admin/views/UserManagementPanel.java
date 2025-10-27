package admin.views;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class UserManagementPanel extends JPanel{

    private JTable userTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    
    private JTextField searchField;
    private JComboBox<String> filterTypeCombo;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> sortCombo;
    
    private JButton btnAdd, btnEdit, btnDelete, btnLock, btnUnlock;
    private JButton btnResetPassword, btnLoginHistory, btnFriendList;
    private JButton btnSearch;
    
    public UserManagementPanel(){
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponents();
    }
    
    private void initComponents(){
        JLabel titleLabel = new JLabel("Quản lý danh sách người dùng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Filter and Search Panel
        JPanel filterPanel = createFilterPanel();
        
        // Table
        JScrollPane tableScrollPane = createTablePanel();
        
        // Pagination Panel
        JPanel paginationPanel = createPaginationPanel();
        
        // Top Panel (Title + Filters + Actions)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFilterPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        
        // Filter type label and combo
        JLabel filterLabel = new JLabel("Lọc theo:");
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        filterTypeCombo = new JComboBox<>(new String[]{
            "Tên đăng nhập", "Họ tên", "Email"
        });
        filterTypeCombo.setPreferredSize(new Dimension(150, 30));
        filterTypeCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        
        // Search field
        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(180, 30));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Status filter
        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        statusFilterCombo = new JComboBox<>(new String[]{
            "Tất cả", "Đang hoạt động", "Bị khoá"
        });
        statusFilterCombo.setPreferredSize(new Dimension(150, 30));
        statusFilterCombo.setFont(new Font("Arial", Font.PLAIN, 13));

        // Search button
        btnSearch = new JButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(100, 30));
        btnSearch.setFont(new Font("Arial", Font.PLAIN, 13));
        btnSearch.setBackground(new Color(100, 181, 246));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Sort
        JLabel sortLabel = new JLabel("Sắp xếp:");
        sortLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        sortCombo = new JComboBox<>(new String[]{
            "Tên (A-Z)", "Tên (Z-A)", "Ngày tạo (Mới nhất)", "Ngày tạo (Cũ nhất)"
        });
        sortCombo.setPreferredSize(new Dimension(140, 30));
        sortCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        
        // Add components to panel
        panel.add(filterLabel);
        panel.add(filterTypeCombo);
        panel.add(searchField);
        panel.add(statusLabel);
        panel.add(statusFilterCombo);
        panel.add(btnSearch);
        panel.add(sortLabel);
        panel.add(sortCombo);
        
        return panel;
    }
    
    private JScrollPane createTablePanel(){
        String[] columns = {
            "ID", "Tên đăng nhập", "Họ tên", "Địa chỉ", 
            "Ngày sinh", "Giới tính", "Email", "Ngày tạo", "Trạng thái"
        };
        
        tableModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        // Thêm dữ liệu mẫu vào tableModel
        for (int i = 1; i <= 50; i++) { // Thêm 50 hàng dữ liệu
            Object[] rowData = {
                i,
                "user" + i,
                "Họ Tên " + i,
                "Địa chỉ " + i,
                "01/01/2000",
                (i % 2 == 0) ? "Nam" : "Nữ",
                "user" + i + "@example.com",
                "01/01/2023",
                "Hoạt động"
            };
            tableModel.addRow(rowData);
        }
        
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
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        userTable.getColumnModel().getColumn(6).setPreferredWidth(180);
        userTable.getColumnModel().getColumn(7).setPreferredWidth(120);
        userTable.getColumnModel().getColumn(8).setPreferredWidth(120);
        
        // Add sorter
        // sorter = new TableRowSorter<>(tableModel);
        // userTable.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        return scrollPane;
    }
    
    private JPanel createPaginationPanel(){
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton prevButton = new JButton("◄ Trước");
        prevButton.setPreferredSize(new Dimension(100, 30));
        prevButton.setFont(new Font("Arial", Font.PLAIN, 12));
        prevButton.setFocusPainted(false);
        prevButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel pageLabel = new JLabel("Trang 1 / 5");
        pageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        pageLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        JButton nextButton = new JButton("Sau ►");
        nextButton.setPreferredSize(new Dimension(100, 30));
        nextButton.setFont(new Font("Arial", Font.PLAIN, 12));
        nextButton.setFocusPainted(false);
        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        panel.add(prevButton);
        panel.add(pageLabel);
        panel.add(nextButton);
        
        return panel;
    } 
}