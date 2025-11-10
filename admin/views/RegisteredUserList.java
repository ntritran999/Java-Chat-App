package admin.views;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class RegisteredUserList extends JPanel{

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> sortCombo;

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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);

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

        panel.add(filterLabel);
        panel.add(searchField);
        panel.add(btnSearch);
        panel.add(sortLabel);
        panel.add(sortCombo);
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

        // Thêm dữ liệu mẫu vào tableModel
        for (int i = 1; i <= 50; i++) { // Thêm 50 hàng dữ liệu
            Object[] rowData = {
                "user" + i,                 // Tên đăng nhập
                "Họ Tên " + i,              // Họ tên
                "user" + i + "@example.com",// Email
                "01/01/2023",               // Ngày tạo
                (i % 2 == 0) ? "Hoạt động" : "Bị khóa" // Trạng thái
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
        userTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        return scrollPane;
    }
}
