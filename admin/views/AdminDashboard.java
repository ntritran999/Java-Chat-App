package admin.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.formdev.flatlaf.FlatLightLaf;

import admin.controllers.*;

public class AdminDashboard extends JFrame{
    
    private JPanel sidebarPanel;
    private JPanel contentPanel;    
    private AdminDashboard self;
    private JButton[] menuButtons; 
    public AdminDashboard(){
        setTitle("Admin Dashboard");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // center the app
        setResizable(false); // unable to resize the app

        self=  this;
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents(){
        // Main layout
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Sidebar
        sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);
        
        // Content area
        UserManagement viewUserManagementStart = new UserManagement();
        new UserManagementController(viewUserManagementStart, self);
        contentPanel = viewUserManagementStart;
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel(){
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 60));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        
        JLabel title = new JLabel("Admin");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title);
        
        return panel;
    }
    
    private JPanel createSidebarPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setPreferredSize(new Dimension(330, 0));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        
        // Sections
        addSidebarSection(panel, new String[]{
            // Quản lý
            "Quản lý danh sách người dùng",
            "Danh sách người dùng và số lượng bạn bè",
            // Báo cáo
            "Danh sách đăng nhập",
            "Danh sách các nhóm chat",
            "Danh sách báo cáo spam",
            "Danh sách người dùng đăng ký mới",
            "Danh sách người dùng hoạt động",
            // Thống kê
            "Biểu đồ số lượng người đăng ký mới theo năm",
            "Biểu đồ số lượng người hoạt động theo năm",
        });
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }

    private JLabel addSectionLabel(String title){
        JLabel sectionLabel = new JLabel(title);
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        return sectionLabel;
    }
    
    private void addSidebarSection(JPanel panel, String[] menuItems){
        panel.add(addSectionLabel("Quản lý"));

        JButton[] buttons = new JButton[menuItems.length];
        menuButtons = buttons;
        // Menu items
        for(int i = 0; i < menuItems.length; ++i){
            if(i == 2)
                panel.add(addSectionLabel("Báo cáo"));
            else if(i == 7)
                panel.add(addSectionLabel("Thống kê"));

            buttons[i] = createMenuButton(menuItems[i], buttons);
            if(i == 0)
                buttons[i].setBackground(new Color(32, 198, 198));
            panel.add(buttons[i]);
        }
    }
    
    private JButton createMenuButton(String text, JButton[] buttons){
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setMaximumSize(new Dimension(330, 40));
        button.setPreferredSize(new Dimension(330, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent e) {
                if(!button.getBackground().equals(new Color(32, 198, 198))) 
                    button.setBackground(new Color(230, 235, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(!button.getBackground().equals(new Color(32, 198, 198))) 
                    button.setBackground(new Color(245, 245, 245));
            }

            @Override
            public void mouseClicked(MouseEvent e){
                for(JButton btn : buttons)
                    btn.setBackground(new Color(245, 245, 245));

                button.setBackground(new Color(32, 198, 198));
                switch (text){
                    case "Quản lý danh sách người dùng":
                        contentPanel.removeAll();
                        UserManagement viewUserManagement = new UserManagement();
                        new UserManagementController(viewUserManagement, self);
                        contentPanel.add(viewUserManagement);
                        break;
                    case "Danh sách đăng nhập":
                        contentPanel.removeAll();
                        LoginHistoryController lhController = new LoginHistoryController();
                        contentPanel.add(lhController.createLoginHistory());
                        break;
                    case "Biểu đồ số lượng người hoạt động theo năm":
                        contentPanel.removeAll();
                        contentPanel.add(new ActiveUsersChart());
                        break;
                    case "Biểu đồ số lượng người đăng ký mới theo năm":
                        contentPanel.removeAll();
                        contentPanel.add(new RegisteredUserChart());
                        break;
                    case "Danh sách người dùng đăng ký mới":
                        contentPanel.removeAll();
                        contentPanel.add(new RegisteredUserList());
                        break;
                    case "Danh sách người dùng và số lượng bạn bè":
                        contentPanel.removeAll();
                        UsersFriendsList viewUsersFriendsList = new UsersFriendsList();
                        new UsersFriendsListController(viewUsersFriendsList);
                        contentPanel.add(viewUsersFriendsList);
                        break;
                    case "Danh sách báo cáo spam":
                        contentPanel.removeAll();
                        SpamController spamController = new SpamController();
                        contentPanel.add(spamController.createSpamManagement());
                        break;
                    case "Danh sách người dùng hoạt động":
                        contentPanel.removeAll();
                        contentPanel.add(new ActiveUsersList());
                        break;
                    case "Danh sách các nhóm chat":
                        contentPanel.removeAll();
                        GroupChatController gcController = new GroupChatController();
                        contentPanel.add(gcController.createChatList());
                        break;
                    default:
                        break;
                }
                contentPanel.revalidate(); // Cập nhật lại giao diện
                contentPanel.repaint(); // Vẽ lại contentPanel
            }
        });
        return button;
    }
    
    public void showUsersFriendsList(String username, UsersFriendsList view) {
        for(JButton btn : menuButtons){
            btn.setBackground(new Color(245, 245, 245));
        }

        for(JButton btn : menuButtons){
            if (btn.getText().equals("Danh sách người dùng và số lượng bạn bè")) {
                btn.setBackground(new Color(32, 198, 198));
                break;
            }
        }
        contentPanel.removeAll();
        contentPanel.add(view);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showHistoryList(String id, LoginHistoryController controller) {
        for(JButton btn : menuButtons){
            btn.setBackground(new Color(245, 245, 245));
        }

        for(JButton btn : menuButtons){
            if (btn.getText().equals("Danh sách đăng nhập")) {
                btn.setBackground(new Color(32, 198, 198));
                break;
            }
        }
        contentPanel.removeAll();        
        contentPanel.add(controller.createLoginHistoryId(id));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args){
        FlatLightLaf.setup();
        new AdminDashboard();
    }
}