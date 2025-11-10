package admin.views;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

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
        JButton buttonR = createStyledButton("Tải lại", new Color(100, 181, 246));
        panel1.add(buttonR);

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


        // Thêm dữ liệu giả
        Object[][] fakeData = {
            {"user1", "Nguyễn Văn A", "10:00 AM"},
            {"user2", "Trần Thị B", "10:30 AM"},
            {"user3", "Lê Văn C", "11:00 AM"},
            {"user4", "Phạm Thị D", "11:30 AM"},
            {"user5", "Vũ Văn E", "12:00 PM"}
        };

        for(Object[] row : fakeData){
            tableModel.addRow(row); // Thêm từng hàng vào mô hình
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
}