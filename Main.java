import javax.swing.SwingUtilities;

import user.controllers.UserController;
import user.models.User;
import user.views.UserFrame;

public class Main {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        SwingUtilities.invokeLater(() -> {
            UserController uc = new UserController(new User(), new UserFrame());
            uc.useLoginPage();
        });
    }
}
