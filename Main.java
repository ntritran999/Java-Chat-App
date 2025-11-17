import java.sql.SQLException;

import javax.swing.SwingUtilities;

import user.controllers.UserController;
import user.models.User;
import user.views.UserFrame;
import admin.models.dbConnection;

public class Main {
    public static void main(String[] args) throws SQLException {
        dbConnection.createConDB();
        System.setProperty("sun.java2d.uiScale", "1.5");
        SwingUtilities.invokeLater(() -> {
            UserController uc = new UserController(new User(), new UserFrame());
            uc.useLoginPage();
        });
    }
}
