package admin.models;

import java.sql.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class AdminModel {
    private static Connection conn = null;
    public static Connection createConnection() throws IOException, SQLException{
        if (conn == null) {
            Properties prop = new Properties();
            FileReader fr = new FileReader("db.properties");
            prop.load(fr);
            String url = prop.getProperty("DB_URL");
            String user = prop.getProperty("DB_USER");
            String password = prop.getProperty("DB_PASSWORD");
            conn = DriverManager.getConnection(url, user, password);
        }
        return conn;
    }
}
