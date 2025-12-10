package admin.models;

import java.util.Properties;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnection{
    private static Connection conn;

    public static Connection createConDB() throws SQLException{
        Properties prop = new Properties();
        String DB_URL = null, DB_USER = null, DB_PASSWORD = null;
        try(FileReader fr = new FileReader("db.properties")){
            prop.load(fr);
            DB_URL = prop.getProperty("DB_URL");
            DB_USER = prop.getProperty("DB_USER");
            DB_PASSWORD = prop.getProperty("DB_PASSWORD");
        }catch(IOException e){
            System.out.println(e);
        }
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        return conn;
    }

    public static Connection getConnection(){
        return conn;
    }
}