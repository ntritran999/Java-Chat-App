package user.controllers;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import user.models.*;
import user.views.*;

public class UserController {
    private User user;
    private UserFrame userFrame;
    
    public UserController(User user, UserFrame userFrame) {
        this.user = user;
        this.userFrame = userFrame;
    }

    public void useLoginPage() {
        LoginPage lp = new LoginPage();
        lp.getLoginButton().addActionListener(e -> {
            JOptionPane.showMessageDialog(lp, "Đăng nhập thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        });
        lp.getCreateAccButton().addActionListener(e -> {
            JOptionPane.showMessageDialog(lp, "Đăng nhập thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        });
        userFrame.setContentPane(lp);
    }

    public void useSignUpPage() {
        userFrame.setContentPane(new SignupPage());
    }

    public void useChatPage() {
        ChatPage cp = new ChatPage();
        userFrame.setContentPane(cp);
        JPanel listPanel = cp.getListPanel();
        for (int i = 0; i < 2; i++) { // TO-DO: Change later with actual data
            ChatPage.FriendPanel fp = cp.new FriendPanel("Some name", "Online");
            listPanel.add(fp);
            fp.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getSource() == fp) {
                        JOptionPane.showMessageDialog(null, "Clicked success");
                    }
                }
            });
            fp.getRemoveFriendButton().addActionListener(e -> {
                JOptionPane.showMessageDialog(null, "None");
            });
            fp.getBlockButton().addActionListener(e -> {
                JOptionPane.showMessageDialog(null, "None");
            });
        }

        cp.getSendButton().addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Clicked");
        });;
    }
}
