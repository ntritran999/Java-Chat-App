package admin.views;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.table.DefaultTableCellRenderer; // align center

public class UsersFriendsList extends JPanel {
    
    private JTextField searchNameField;
    private JButton btnSearch;
    private JComboBox<String> sortCombo;

    // Direct friends
    private JComboBox<String> filterOperator;
    private JTextField friendsCountField;
    private JButton btnFilter;
    private JButton btnClearFilter;
    
    private JTable userTable;
    private DefaultTableModel tableModel;

    
    public UsersFriendsList() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponents();
    }
    
    private void initComponents() {
        JLabel titleLabel = new JLabel("Xem danh sách người dùng và số lượng bạn bè");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        

        JPanel filterPanel = createFilterPanel();
        JScrollPane tableScrollPane = createTablePanel();
        
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createFilterPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        
        // Search by name and Sort
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        row1.setBackground(Color.WHITE);
        
        JLabel searchLabel = new JLabel("Tìm theo tên:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        searchNameField = new JTextField(25);
        searchNameField.setPreferredSize(new Dimension(250, 30));
        searchNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setPreferredSize(new Dimension(120, 30));
        btnSearch.setFont(new Font("Arial", Font.PLAIN, 13));
        btnSearch.setBackground(new Color(100, 181, 246));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel sortLabel = new JLabel("Sắp xếp:");
        sortLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        sortCombo = new JComboBox<>(new String[]{
            "Tên (A-Z)",
            "Tên (Z-A)",
            "Thời gian tạo (Mới nhất)",
            "Thời gian tạo (Cũ nhất)",
            "Số bạn trực tiếp (Nhiều nhất)",
            "Số bạn trực tiếp (Ít nhất)"
        });
        sortCombo.setPreferredSize(new Dimension(220, 30));
        sortCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        
        row1.add(searchLabel);
        row1.add(searchNameField);
        row1.add(btnSearch);
        row1.add(Box.createHorizontalStrut(20));
        row1.add(sortLabel);
        row1.add(sortCombo);
        
        // Filter by friends count
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        row2.setBackground(Color.WHITE);
        row2.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Lọc theo số lượng bạn trực tiếp"),
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
        
        friendsCountField = new JTextField(10);
        friendsCountField.setPreferredSize(new Dimension(100, 30));
        friendsCountField.setFont(new Font("Arial", Font.PLAIN, 14));
        friendsCountField.setHorizontalAlignment(JTextField.CENTER);
        
        JLabel friendsLabel = new JLabel("bạn bè");
        friendsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        btnFilter = new JButton("Áp dụng");
        btnFilter.setPreferredSize(new Dimension(110, 30));
        btnFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        btnFilter.setBackground(new Color(76, 175, 80));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFocusPainted(false);
        btnFilter.setBorderPainted(false);
        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnClearFilter = new JButton("Xoá lọc");
        btnClearFilter.setPreferredSize(new Dimension(110, 30));
        btnClearFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        btnClearFilter.setBackground(new Color(244, 67, 54));
        btnClearFilter.setForeground(Color.WHITE);
        btnClearFilter.setFocusPainted(false);
        btnClearFilter.setBorderPainted(false);
        btnClearFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        row2.add(filterLabel);
        row2.add(filterOperator);
        row2.add(friendsCountField);
        row2.add(friendsLabel);
        row2.add(btnFilter);
        row2.add(btnClearFilter);
        
        mainPanel.add(row1);
        mainPanel.add(row2);
        return mainPanel;
    }
    
    private JScrollPane createTablePanel() {
        String[] columns = {
            "ID", "Tên đăng nhập", "Họ tên", "Email", 
            "Ngày tạo", "Bạn bè trực tiếp", "Bạn của bạn"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
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
        userTable.getColumnModel().getColumn(5).setPreferredWidth(140);  
        userTable.getColumnModel().getColumn(6).setPreferredWidth(140);  
        
        // Align center number 
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        userTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        return scrollPane;
    }

    public JTextField getSearchNameField(){
        return searchNameField;
    }

    public JButton getBtnSearch(){
        return btnSearch;
    }
    
    public JComboBox<String> getSortCombo(){
        return sortCombo;
    }

      public JComboBox<String> getFilterOperator(){
        return filterOperator;
    }
    
    public JTextField getFriendsCountField(){
        return friendsCountField;
    }
    
    public JButton getBtnFilter(){
        return btnFilter;
    }
    
    public JButton getBtnClearFilter(){
        return btnClearFilter;
    }

    public JTable getUserTable(){
        return userTable;
    }  
    
    public DefaultTableModel getTableModel(){
        return tableModel;
    } 
}