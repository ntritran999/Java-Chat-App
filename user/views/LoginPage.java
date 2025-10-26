package user.views;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;

import static user.views.Style.*;

public class LoginPage extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, createAccButton;

    public LoginPage() {
        this.setBackground(new Color(bgColor));

        JLabel logo = new JLabel("CHAT APP");
        logo.setForeground(Color.black);
        logo.setHorizontalAlignment(JLabel.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 64));
        logo.setIcon(new ImageIcon(getClass().getResource("/assets/images/chat-icon.png")));
        logo.setIconTextGap(20);
        
        JPanel main = new JPanel();
        main.setBackground(new Color(bgColorDark));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        GroupLayout mainLayout = new GroupLayout(main);
        main.setLayout(mainLayout);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(mainLayout.createSequentialGroup()
                .addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(mainLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(GroupLayout.Alignment.TRAILING, mainLayout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(createAccButton)
                                    .addGap(18, 18, 18)
                                    .addComponent(loginButton))
                                .addGroup(mainLayout.createSequentialGroup()
                                    .addComponent(passwordLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(18, 18, 18)
                                    .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 319, GroupLayout.PREFERRED_SIZE)))
                            .addGroup(GroupLayout.Alignment.TRAILING, mainLayout.createSequentialGroup()
                                .addComponent(usernameLabel, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, 319, GroupLayout.PREFERRED_SIZE))))
                    .addGroup(mainLayout.createSequentialGroup()
                        .addGap(150, 150, 150)
                        .addComponent(title)))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        mainLayout.setVerticalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(mainLayout.createSequentialGroup()
                .addGap(157, 157, 157)
                .addComponent(title)
                .addGap(72, 72, 72)
                .addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameLabel)
                    .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(createAccButton)
                    .addComponent(loginButton))
                .addContainerGap(470, Short.MAX_VALUE))
        );

        GroupLayout loginLayout = new GroupLayout(this);
        this.setLayout(loginLayout);
        loginLayout.setHorizontalGroup(
            loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(loginLayout.createSequentialGroup()
                .addComponent(main, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(200, 200, 200)
                .addComponent(logo, GroupLayout.PREFERRED_SIZE, 441, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(157, Short.MAX_VALUE))
        );
        loginLayout.setVerticalGroup(
            loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(main, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(loginLayout.createSequentialGroup()
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

    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getCreateAccButton() {
        return createAccButton;
    }

    
}
