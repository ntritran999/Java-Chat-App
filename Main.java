import java.sql.SQLException;

import javax.swing.SwingUtilities;

import user.controllers.UserController;
import user.models.UserModel;
import user.views.UserFrame;

public class Main {
    public static void main(String[] args) throws SQLException {
        System.setProperty("sun.java2d.uiScale", "1.0");
        SwingUtilities.invokeLater(() -> {
            UserController uc = new UserController(new UserModel(), new UserFrame());
            uc.useLoginPage();
        });
    }
}
