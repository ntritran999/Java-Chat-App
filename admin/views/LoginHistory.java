package admin.views;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.ArrayList;

public class LoginHistory extends JPanel{
    
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;

    public LoginHistory(){
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents(){
        JLabel titleLabel = new JLabel("Danh sách đăng nhập");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));


        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRefresh = createStyledButton("Tải lại", new Color(100, 181, 246));
        panel1.add(btnRefresh);

        JScrollPane tableScrollPane = createTablePanel();

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(panel1, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);   
    }


    private JScrollPane createTablePanel(){
        String[] columns = {
            "Tên đăng nhập", "Họ tên", "Thời gian đăng nhập"
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

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        return scrollPane;
    }

    private JButton createStyledButton(String text, Color color){
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 35));
        return button;
    }

    public void addRefreshEvent(ActionListener l) {
        btnRefresh.addActionListener(l);
    }

    public void reloadTableModel(ArrayList<HashMap<String, String>> data) {
        tableModel.setRowCount(0);
        for(HashMap<String, String> map : data) {
            Object[] row = {map.get("username"), map.get("fullname"), map.get("loginDate")};
            tableModel.addRow(row);
        }
        tableModel.fireTableDataChanged();
    }
}