package admin.views;

import javax.swing.*;
import javax.swing.table.*;

import admin.controllers.UserManagementController;

import java.awt.*;
import java.awt.event.*;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.time.LocalDate;
import java.util.function.Function;

public class UserManagement extends JPanel{

    private JTable userTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> filterTypeCombo;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> sortCombo;

    private JButton btnSearch;
    private JButton btnAdd, btnUpdate;
    private java.util.List<JButton> actionButtons = new java.util.ArrayList<>();

    public UserManagement(){
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

        btnUpdate = new JButton("Cập nhật");
        btnUpdate.setPreferredSize(new Dimension(90, 30));
        btnUpdate.setBackground(new Color(33, 150, 243));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));

        insertAndSave.add(btnAdd);
        insertAndSave.add(btnUpdate);

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



    private JPanel createFilterPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        panel.setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Lọc theo:");
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        filterTypeCombo = new JComboBox<>(new String[]{
                "Tên đăng nhập", "Họ tên"
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

        sortCombo.setPreferredSize(new Dimension(100, 30));
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
        String[] columns ={
            "ID", "Tên đăng nhập", "Họ tên", "Địa chỉ",
            "Ngày sinh", "Giới tính", "Email", "Ngày tạo", "Trạng thái", "Hành động"
        };

        tableModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return column == 9;
            }
        };

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

    public void updateActionButtonRenderer() {
        ActionButtonRenderer.setActionButtons(actionButtons);
        userTable.repaint();
    }

    public JButton createActionButton(UserManagementController umController, String username, int rowIndex){
        JButton button = new JButton("Khác");
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(100, 181, 246)); // xanh nhẹ
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> showUserPopup(umController, rowIndex));
        return button;
    }

    // Hiển thị popup 
    private JPanel createRow(JComponent... components){
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        for (JComponent c : components) {
            row.add(c);
        }
        return row;
    }

    public void showCreateAccountUI(UserManagementController umController){
        Window parentWindow = SwingUtilities.getWindowAncestor(UserManagement.this);
        JDialog dialog = new JDialog(parentWindow, "Thêm tài khoản", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(700, 420);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        JTextField usernameField = new JTextField(20);
        mainPanel.add(createRow(new JComponent[]{ usernameLabel, usernameField }));

        JLabel passLabel = new JLabel("Mật khẩu:");
        JPasswordField passwordField = new JPasswordField(20);
        mainPanel.add(createRow(new JComponent[]{ passLabel, passwordField }));

        JLabel dobLabel = new JLabel("Ngày sinh:");
        DatePickerSettings dobS = new DatePickerSettings();
        dobS.setFormatForDatesCommonEra("dd/MM/yyyy");
        DatePicker dobDatePicker = new DatePicker(dobS);
        dobDatePicker.setDateToToday();
        mainPanel.add(createRow(new JComponent[]{ dobLabel, dobDatePicker }));

        JLabel fullLabel = new JLabel("Họ và tên:");
        JTextField fullNameField = new JTextField(20);
        mainPanel.add(createRow(new JComponent[]{ fullLabel, fullNameField }));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        mainPanel.add(createRow(new JComponent[]{ emailLabel, emailField }));

        JLabel roleLabel = new JLabel("Phân quyền:");
        JRadioButton adminRadio = new JRadioButton("Admin");
        JRadioButton userRadio = new JRadioButton("User");
        userRadio.setSelected(true);

        ButtonGroup groupRole = new ButtonGroup();
        groupRole.add(adminRadio);
        groupRole.add(userRadio);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rolePanel.add(adminRadio);
        rolePanel.add(userRadio);

        mainPanel.add(createRow(new JComponent[]{ roleLabel, rolePanel }));

        JButton btnAddAccount = createPopupButton("Thêm tài khoản", new Color(33, 150, 243), e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String fullname = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String dob = dobDatePicker.getDate().toString();
            String role = adminRadio.isSelected() ? "A" : "U";

            if(username == null || username.isEmpty() || password == null || password.isEmpty() || fullname == null || fullname.isEmpty() || 
            email == null || email.isEmpty() || dob == null || dob.isEmpty() || role == null || role.isEmpty())
                JOptionPane.showMessageDialog(dialog, "Không thêm được tài khoản");

            umController.handleCreateAccount(username, password, fullname, email, dob, role, dialog);

            usernameField.setText("");
            passwordField.setText("");
            fullNameField.setText("");
            emailField.setText("");
        });

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(btnAddAccount, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }


    public void showUpdateUI(UserManagementController umController){
        Window parentWindow = SwingUtilities.getWindowAncestor(UserManagement.this);
        JDialog dialog = new JDialog(parentWindow, "Cập nhật thông tin", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(520, 250);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setLayout(new BorderLayout());

        JPanel idFPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        JLabel idLabel = new JLabel("ID user: ");
        JTextField idField = new JTextField(15);
        idField.setPreferredSize(new Dimension(180, 28));
        idFPanel.add(idLabel);
        idFPanel.add(idField);

        JPanel fullNameFPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        JLabel fullNameLabel = new JLabel("Họ tên: ");
        JTextField fullNameField = new JTextField(15);
        fullNameField.setPreferredSize(new Dimension(180, 28));
        fullNameFPanel.add(fullNameLabel);
        fullNameFPanel.add(fullNameField);

        JPanel addressFPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        JLabel addressLabel = new JLabel("Địa chỉ: ");
        JTextField addressField = new JTextField(15);
        addressField.setPreferredSize(new Dimension(180, 28));
        addressFPanel.add(addressLabel);
        addressFPanel.add(addressField);

        JPanel emailFPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        JLabel emailLabel = new JLabel("Email: ");
        JTextField emailField = new JTextField(15);
        emailField.setPreferredSize(new Dimension(180, 28));
        emailFPanel.add(emailLabel);
        emailFPanel.add(emailField);
        JPanel mainForm = new JPanel();
        mainForm.add(fullNameFPanel);
        mainForm.add(addressFPanel);
        mainForm.add(emailFPanel);

        JButton btnUpdatePassword = createPopupButton("Cập nhật", new Color(33, 150, 243), e ->{
            String id = new String(idField.getText().trim());
            String fullname = new String(fullNameField.getText().trim());
            String address = new String(addressField.getText().trim());
            String email = new String(emailField.getText().trim());
            umController.handleUpdateInfoUser(id, fullname, address, email, dialog);
            idField.setText("");
            fullNameField.setText("");
            addressField.setText("");
            emailField.setText("");
        });
        dialog.add(idFPanel, BorderLayout.NORTH);
        dialog.add(mainForm, BorderLayout.CENTER);
        dialog.add(btnUpdatePassword, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showUserPopup(UserManagementController umController, int row){
        Window parentWindow = SwingUtilities.getWindowAncestor(UserManagement.this);
        String id = userTable.getValueAt(row, 0).toString();
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

        JButton btnUpdatePassword = createPopupButton("Cập nhật mật khẩu", new Color(33, 150, 243), e ->{
            String newPass = new String(passwordField.getPassword());
            umController.handleUpdatePassword(id, username, newPass, dialog);
            passwordField.setText("");
        });
        btnUpdatePassword.setPreferredSize(new Dimension(150, 28));
        passwordPanel.add(btnUpdatePassword);

        // ====== Các nút chức năng khác ======
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        buttonPanel.add(createPopupButton("Xóa", new Color(110, 112, 116), e ->{
            umController.handleDeleteUser(id, username, dialog);
        }));
        
        if(status.charAt(0) != 'B'){
            buttonPanel.add(createPopupButton("Khóa", new Color(244, 67, 54), e ->{
                umController.handleLockUser(id, username, dialog);    
            }));
        }else{
            buttonPanel.add(createPopupButton("Mở khóa", new Color(76, 175, 80), e ->
                    JOptionPane.showMessageDialog(dialog, "Đã mở khóa tài khoản " + username)));
        }

        buttonPanel.add(createPopupButton("Danh sách bạn bè", new Color(100, 181, 246), e ->{
            umController.handleShowFriendsList(username, dialog);
        }));

        buttonPanel.add(createPopupButton("Lịch sử đăng nhập", new Color(255, 152, 0), e ->{
            umController.handleShowLoginHistory(id, username, dialog);
        }));

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

    public JTable getUserTable(){
        return userTable;
    }

    public DefaultTableModel getTableModel(){
        return tableModel;
    }

    public JTextField getSearchField(){
        return searchField;
    }

    public JComboBox<String> getFilterTypeCombo(){
        return filterTypeCombo;
    }

    public JComboBox<String> getStatusFilterCombo(){
        return statusFilterCombo;
    }

    public JComboBox<String> getSortCombo(){
        return sortCombo;
    }

    public JButton getBtnSearch(){
        return btnSearch;
    }

    public JButton getBtnAdd(){
        return btnAdd;
    }

    public JButton getBtnUpdate(){
        return btnUpdate;
    }
    public java.util.List<JButton> getActionButtonManageView(){
        return actionButtons;
    }
}
