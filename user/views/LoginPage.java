package user.views;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import java.awt.CardLayout;
import static user.views.Style.*;

public class LoginPage extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, createAccButton, forgetPasswordButton;
    
    private JTextField usernameForResetField;
    private JButton resetPasswordBtn;
    private JPanel main, loginPanel, resetPanel;
    public LoginPage() {
        setBackground(new Color(bgColor));

        JLabel logo = new JLabel("CHAT APP");
        logo.setForeground(Color.black);
        logo.setHorizontalAlignment(JLabel.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 64));
        logo.setIcon(new ImageIcon(getClass().getResource("/assets/images/chat-logo.png")));
        logo.setIconTextGap(20);

        loginPanel = new JPanel();
        loginPanel.setBackground(new Color(bgColorDark));
        buildLoginLayout();
        resetPanel = new JPanel();
        resetPanel.setBackground(new Color(bgColorDark));
        buildResetLayout();
        
        main = new JPanel();
        main.setBackground(new Color(bgColorDark));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        main.setLayout(new CardLayout()); // Nhớ import java.awt.CardLayout;
        main.add(loginPanel, "login");
        main.add(resetPanel, "reset");
        showLogin();
        
        GroupLayout mainLayout = new GroupLayout(this);
        setLayout(mainLayout);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(mainLayout.createSequentialGroup()
                .addComponent(main, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(200, 200, 200)
                .addComponent(logo, GroupLayout.PREFERRED_SIZE, 441, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(157, Short.MAX_VALUE))
        );
        mainLayout.setVerticalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(main, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainLayout.createSequentialGroup()
                .addGap(275, 275, 275)
                .addComponent(logo)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public void addLoginButtonEvent(ActionListener l) {
        loginButton.addActionListener(l);
    }

    public void addCreateAccButtonEvent(ActionListener l) {
        createAccButton.addActionListener(l);
    }

   private void buildLoginLayout() {
        JLabel title = new JLabel("ĐĂNG NHẬP");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(textColor));

        JLabel usernameLabel = new JLabel("Tài khoản: ");
        usernameLabel.setForeground(new Color(textColor));
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Mật khẩu: ");
        passwordLabel.setForeground(new Color(textColor));
        passwordField = new JPasswordField();

        loginButton = new JButton("Đăng nhập");
        createAccButton = new JButton("Tạo tài khoản mới");
        forgetPasswordButton = new JButton("Quên mật khẩu?");
        forgetPasswordButton.addActionListener(e -> showReset());

        GroupLayout loginLayout = new GroupLayout(loginPanel);
        loginPanel.setLayout(loginLayout);
        loginLayout.setHorizontalGroup(
            loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(loginLayout.createSequentialGroup()
                .addGroup(loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(loginLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addGroup(loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(GroupLayout.Alignment.TRAILING, loginLayout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(forgetPasswordButton)
                                    .addGap(18, 18, 18)
                                    .addComponent(createAccButton)
                                    .addGap(18, 18, 18)
                                    .addComponent(loginButton))
                                .addGroup(loginLayout.createSequentialGroup()
                                    .addComponent(passwordLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(18, 18, 18)
                                    .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 319, GroupLayout.PREFERRED_SIZE)))
                            .addGroup(GroupLayout.Alignment.TRAILING, loginLayout.createSequentialGroup()
                                .addComponent(usernameLabel, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, 319, GroupLayout.PREFERRED_SIZE))))
                    .addGroup(loginLayout.createSequentialGroup()
                        .addGap(150, 150, 150)
                        .addComponent(title)))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        loginLayout.setVerticalGroup(
            loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(loginLayout.createSequentialGroup()
                .addGap(157, 157, 157)
                .addComponent(title)
                .addGap(72, 72, 72)
                .addGroup(loginLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameLabel)
                    .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(loginLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(loginLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(forgetPasswordButton)
                    .addComponent(createAccButton)
                    .addComponent(loginButton))
                .addContainerGap(470, Short.MAX_VALUE))
        );
    }

    private void buildResetLayout() {
        JLabel title = new JLabel("RESET MẬT KHẨU");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(textColor));

        JLabel usernameLabel = new JLabel("Tài khoản: ");
        usernameLabel.setForeground(new Color(textColor));
        usernameForResetField = new JTextField();

        resetPasswordBtn = new JButton("Reset");
        JButton returnBtn = new JButton("Quay lại");
        returnBtn.addActionListener(e -> showLogin());

        GroupLayout resetLayout = new GroupLayout(resetPanel);
        resetPanel.setLayout(resetLayout);
        resetLayout.setHorizontalGroup(
            resetLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(resetLayout.createSequentialGroup()
                .addGroup(resetLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(resetLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(resetLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addGroup(resetLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(GroupLayout.Alignment.TRAILING, resetLayout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(returnBtn)
                                    .addGap(18, 18, 18)
                                    .addComponent(resetPasswordBtn)))
                            .addGroup(GroupLayout.Alignment.TRAILING, resetLayout.createSequentialGroup()
                                .addComponent(usernameLabel, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(usernameForResetField, GroupLayout.PREFERRED_SIZE, 319, GroupLayout.PREFERRED_SIZE))))
                    .addGroup(resetLayout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(title)))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        resetLayout.setVerticalGroup(
            resetLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(resetLayout.createSequentialGroup()
                .addGap(157, 157, 157)
                .addComponent(title)
                .addGap(72, 72, 72)
                .addGroup(resetLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameLabel)
                    .addComponent(usernameForResetField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(resetLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(returnBtn)
                    .addComponent(resetPasswordBtn))
                .addContainerGap(470, Short.MAX_VALUE))
        );
    }
    
    public void showLogin() {
        ((CardLayout)main.getLayout()).show(main, "login");
    }

    private void showReset() {
        ((CardLayout)main.getLayout()).show(main, "reset");
    }

    public void addResetPasswordButtonEvent(ActionListener l) {
        resetPasswordBtn.addActionListener(l);
    }

    public JTextField getUsernameForReset() {
        return usernameForResetField;
    }

    public void showResetFail(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Reset thất bại", JOptionPane.ERROR_MESSAGE);
    }

    public void showResetSuccess() {
        JOptionPane.showMessageDialog(this, "Mật khẩu sẽ được gửi về email của bạn", "Reset thành công", JOptionPane.INFORMATION_MESSAGE);
    } 

    public void showLoginFail(){
        JOptionPane.showMessageDialog(this, "Tên đăng nhập hay mặt khẩu không đúng", "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
