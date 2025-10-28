package user.controllers;

import javax.swing.JOptionPane;

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
    }
}
