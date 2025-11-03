package admin.views;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class UserManagement extends JPanel {

    private JTable userTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> filterTypeCombo;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> sortCombo;

    private JButton btnSearch;
    private JButton btnAdd, btnSave;
    private java.util.List<JButton> actionButtons = new java.util.ArrayList<>();
    private JPasswordField passwordField;

    public UserManagement() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents(){
        JLabel titleLabel = new JLabel("Quản lý danh sách người dùng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel insertAndSave = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        insertAndSave.setBackground(Color.WHITE);
        btnAdd = new JButton("Thêm");
        btnAdd.setPreferredSize(new Dimension(90, 30));
        btnAdd.setBackground(new Color(76, 175, 80));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave = new JButton("Lưu");
        btnSave.setPreferredSize(new Dimension(90, 30));
        btnSave.setBackground(new Color(33, 150, 243));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        insertAndSave.add(btnAdd);
        insertAndSave.add(btnSave);

        JPanel filterPanel = createFilterPanel();
        JScrollPane tableScrollPane = createTablePanel();

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);

        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        insertAndSave.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        topPanel.add(titleLabel);
        topPanel.add(insertAndSave);
        topPanel.add(filterPanel);

        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }



    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        panel.setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Lọc theo:");
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        filterTypeCombo = new JComboBox<>(new String[]{
                "Tên đăng nhập", "Họ tên", "Email"
        });
        filterTypeCombo.setPreferredSize(new Dimension(150, 30));
        filterTypeCombo.setFont(new Font("Arial", Font.PLAIN, 13));


        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(180, 30));
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        statusFilterCombo = new JComboBox<>(new String[]{
                "Tất cả", "Đang hoạt động", "Bị khoá"
        });
        statusFilterCombo.setPreferredSize(new Dimension(150, 30));
        statusFilterCombo.setFont(new Font("Arial", Font.PLAIN, 13));

        btnSearch = new JButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(80, 30));
        btnSearch.setBackground(new Color(100, 181, 246));
        btnSearch.setForeground(Color.WHITE);

        JLabel sortLabel = new JLabel("Sắp xếp:");
        sortLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        sortCombo = new JComboBox<>(new String[]{
                "Tên (A-Z)", "Tên (Z-A)", "Ngày tạo (Mới nhất)", "Ngày tạo (Cũ nhất)"
        });

        sortCombo.setPreferredSize(new Dimension(130, 30));
        sortCombo.setFont(new Font("Arial", Font.PLAIN, 13));
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
            "Ngày sinh", "Giới tính", "Email", "Ngày tạo", "Trạng thái", "Hành động"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9;
            }
        };

        // Dữ liệu mẫu
        for (int i = 1; i <= 20; i++) {
            Object[] rowData = {
                i, "user" + i, "Họ Tên " + i, "Địa chỉ " + i,
                "01/01/2000", (i % 2 == 0) ? "Nam" : "Nữ",
                "user" + i + "@example.com", "01/01/2023",
                (i % 3 == 0) ? "Bị khoá" : "Hoạt động", ""
            };
            tableModel.addRow(rowData);

            JButton btn = createActionButton((String) rowData[1], i - 1);
            actionButtons.add(btn);
        }

        ActionButtonRenderer.setActionButtons(actionButtons);
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 13));
        userTable.setRowHeight(45);
        userTable.setShowGrid(true);
        userTable.setGridColor(new Color(220, 220, 220));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        userTable.getTableHeader().setBackground(new Color(240, 240, 240));
        userTable.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Renderer và Editor cho cột hành động
        userTable.getColumnModel().getColumn(9).setCellRenderer(new ActionButtonRenderer());
        userTable.getColumnModel().getColumn(9).setCellEditor(new ActionButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        return scrollPane;
    }

    private JButton createActionButton(String username, int rowIndex){
        JButton button = new JButton("Khác");
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(100, 181, 246)); // xanh nhẹ
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> showUserPopup(rowIndex));
        return button;
    }

    // Hiển thị popup 
    private void showUserPopup(int row){
        Window parentWindow = SwingUtilities.getWindowAncestor(UserManagement.this);
        String username = userTable.getValueAt(row, 1).toString();
        String fullName = userTable.getValueAt(row, 2).toString();
        String email = userTable.getValueAt(row, 6).toString();
        String status = userTable.getValueAt(row, 8).toString();
        JDialog dialog = new JDialog(parentWindow, "Chi tiết người dùng", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(520, 250);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setLayout(new BorderLayout());

        // Thông tin người dùng
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(new JLabel("Tên đăng nhập:"));
        infoPanel.add(new JLabel(username));
        infoPanel.add(new JLabel("Họ tên:"));
        infoPanel.add(new JLabel(fullName));
        infoPanel.add(new JLabel("Email:"));
        infoPanel.add(new JLabel(email));
        infoPanel.add(new JLabel("Trạng thái:"));
        infoPanel.add(new JLabel(status));

        // Ô nhập mật khẩu + nút cập nhật
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        JPasswordField passwordField = new JPasswordField(15); // độ dài ký tự, tự co giãn
        passwordField.setPreferredSize(new Dimension(180, 28));
        passwordPanel.add(passwordField);

        JButton btnUpdatePassword = createPopupButton("Cập nhật mật khẩu", new Color(33, 150, 243), e -> {
            String newPass = new String(passwordField.getPassword());
            if(newPass.isEmpty()){
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập mật khẩu mới!");
            }else{
                JOptionPane.showMessageDialog(dialog, "Đã cập nhật mật khẩu mới cho " + username);
            }
        });
        btnUpdatePassword.setPreferredSize(new Dimension(150, 28));
        passwordPanel.add(btnUpdatePassword);

        // ====== Các nút chức năng khác ======
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        buttonPanel.add(createPopupButton("Xóa", new Color(110, 112, 116), e ->
            JOptionPane.showMessageDialog(dialog, "Đã xóa tài khoản " + username)));
        
        if(status.charAt(0) != 'B'){
            buttonPanel.add(createPopupButton("Khóa", new Color(244, 67, 54), e ->
                    JOptionPane.showMessageDialog(dialog, "Đã khóa tài khoản " + username)));
        }else{
            buttonPanel.add(createPopupButton("Mở khóa", new Color(76, 175, 80), e ->
                    JOptionPane.showMessageDialog(dialog, "Đã mở khóa tài khoản " + username)));
        }

        buttonPanel.add(createPopupButton("Danh sách bạn bè", new Color(100, 181, 246), e ->
                JOptionPane.showMessageDialog(dialog, "Hiển thị danh sách bạn bè của " + username)));

        buttonPanel.add(createPopupButton("Lịch sử đăng nhập", new Color(255, 152, 0), e ->
                JOptionPane.showMessageDialog(dialog, "Xem lịch sử đăng nhập của " + username)));

        dialog.add(infoPanel, BorderLayout.NORTH);
        dialog.add(passwordPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }


    private JButton createPopupButton(String text, Color bgColor, ActionListener listener){
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        return button;
    }
}
