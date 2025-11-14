package user.models;

import java.sql.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class UserModel {
    private Connection conn;
    public UserModel() {
        try {
            Properties prop = new Properties();
            FileReader fr = new FileReader("db.properties");
            prop.load(fr);
            String url = prop.getProperty("DB_URL");
            String user = prop.getProperty("DB_USER");
            String password = prop.getProperty("DB_PASSWORD");
            conn = DriverManager.getConnection(url, user, password);
        } catch (IOException | SQLException e) {
            conn = null;
        }
    }

    public Connection getConn() {
        return conn;
    }
}