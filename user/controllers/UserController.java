package user.controllers;

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
        userFrame.setVisible(false);
        userFrame.setContentPane(new LoginPage());
        userFrame.setVisible(true);
    }

    public void useSignUpPage() {
        userFrame.setVisible(false);
        userFrame.setContentPane(new SignupPage());
        userFrame.setVisible(true);
    }
}
