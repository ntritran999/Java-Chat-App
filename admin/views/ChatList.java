package admin.views;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.*;


public class ChatList extends JPanel{
    
    private JTable userTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> sortCombo;
    private JButton buttonSearchName;

    private java.util.List<JButton> functionButtons = new java.util.ArrayList<>();;

    public ChatList(){
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        //loadSampleData();
    }

    private void initComponents(){
        JLabel titleLabel = new JLabel("Danh sách các nhóm chat");
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);

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

        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(buttonSearchName);
        panel.add(sortLabel);
        panel.add(sortCombo);
        return panel;
    }

    private JScrollPane createTablePanel(){
        String[] columns = {
            "STT", "Tên nhóm chat", "Thời gian", "Hoạt động"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        for (int i = 1; i <= 20; i++) {
            Object[] rowData = {
                i,
                "nhóm " + i,
                String.format("%02d/%02d/2024 %02d:%02d:%02d",
                    (i % 28) + 1,           // ngày (1–28)
                    (i % 12) + 1,           // tháng (1–12)
                    (i * 3) % 24,           // giờ (0–23)
                    (i * 7) % 60,           // phút (0–59)
                    (i * 13) % 60           // giây (0–59)
                ),
            };

            tableModel.addRow(rowData);

            JButton btn = createActionButton((String) rowData[1], i - 1);
            functionButtons.add(btn);
        }
        ActionButtonRenderer.setActionButtons(functionButtons);
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 13));
        userTable.setRowHeight(45);
        userTable.setShowGrid(true);
        userTable.setGridColor(new Color(220, 220, 220));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        userTable.getTableHeader().setBackground(new Color(240, 240, 240));
        userTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        userTable.getColumnModel().getColumn(0).setPreferredWidth(20);   
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150);  
        userTable.getColumnModel().getColumn(2).setPreferredWidth(120);  
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        userTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        userTable.getColumnModel().getColumn(3).setCellRenderer(new ActionButtonRenderer());
        userTable.getColumnModel().getColumn(3).setCellEditor(new ActionButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        return scrollPane;

    }

    // Hiển thị popup 
    private void showUserPopup(int row){
        Window parentWindow = SwingUtilities.getWindowAncestor(ChatList.this);
        String chatName = userTable.getValueAt(row, 1).toString();
        String timeCreate = userTable.getValueAt(row, 2).toString();
        JDialog dialog = new JDialog(parentWindow, "Chi tiết nhóm chat", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(520, 200);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setLayout(new BorderLayout());

        // Thông tin người dùng
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(new JLabel("Tên nhóm:"));
        infoPanel.add(new JLabel(chatName));
        infoPanel.add(new JLabel("Thời gian tạo:"));
        infoPanel.add(new JLabel(timeCreate));

        // ====== Các nút chức năng khác ======
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        buttonPanel.add(createPopupButton("Danh sách thành viên", new Color(110, 112, 116), e -> {
            // Tạo dữ liệu bảng giả
            String[] memberColumns = {"STT", "Tên thành viên", "Trạng thái"};
            DefaultTableModel memberModel = new DefaultTableModel(memberColumns, 0);

            for (int i = 1; i <= 10; i++) {
                memberModel.addRow(new Object[]{i, "Thành viên " + i, (i % 2 == 0) ? "Online" : "Offline"});
            }

            JTable memberTable = new JTable(memberModel);
            memberTable.setRowHeight(30);
            JScrollPane scrollPane = new JScrollPane(memberTable);

            // Tạo JDialog mới để hiển thị bảng
            JDialog dialog1 = new JDialog(
                SwingUtilities.getWindowAncestor(buttonPanel),
                "Danh sách thành viên",
                Dialog.ModalityType.APPLICATION_MODAL
            );
            dialog1.getContentPane().add(scrollPane);
            dialog1.setSize(400, 300);
            dialog1.setLocationRelativeTo(buttonPanel);
            dialog1.setVisible(true);
        }));

        buttonPanel.add(createPopupButton("Danh sách admin", new Color(100, 181, 246), e -> {
            // Tạo dữ liệu bảng giả
            String[] memberColumns = {"STT", "Tên admin", "Trạng thái"};
            DefaultTableModel memberModel = new DefaultTableModel(memberColumns, 0);

            for (int i = 1; i <= 10; i++) {
                memberModel.addRow(new Object[]{i, "Thành viên " + i, (i % 2 == 0) ? "Online" : "Offline"});
            }

            JTable memberTable = new JTable(memberModel);
            memberTable.setRowHeight(30);
            JScrollPane scrollPane = new JScrollPane(memberTable);

            JDialog dialog1 = new JDialog(
                SwingUtilities.getWindowAncestor(buttonPanel),
                "Danh sách thành viên",
                Dialog.ModalityType.APPLICATION_MODAL
            );
            dialog1.getContentPane().add(scrollPane);
            dialog1.setSize(400, 300);
            dialog1.setLocationRelativeTo(buttonPanel);
            dialog1.setVisible(true);
        }));

        dialog.add(infoPanel, BorderLayout.NORTH);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JButton createActionButton(String username, int rowIndex){
        JButton button = new JButton("Khác");
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(100, 181, 246)); 
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> showUserPopup(rowIndex));
        return button;
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