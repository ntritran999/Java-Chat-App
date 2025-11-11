package admin.models;

import java.sql.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class AdminModel {
    public static Connection createConnection() throws IOException, SQLException{
        Properties prop = new Properties();
        FileReader fr = new FileReader("db.properties");
        prop.load(fr);
        String url = prop.getProperty("DB_URL");
        String user = prop.getProperty("DB_USER");
        String password = prop.getProperty("DB_PASSWORD");
        return DriverManager.getConnection(url, user, password);
    }
}
