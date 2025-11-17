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
        String DB_PASSWORD = null;
        try(FileReader fr = new FileReader("db.properties")){
            prop.load(fr);
            DB_PASSWORD = prop.getProperty("DB_PASSWORD");
        }catch(IOException e){
            System.out.println(e);
        }
        String pathCon = "jdbc:postgresql://db.ubhgafkushcvjjpjqwxq.supabase.co:5432/postgres?user=postgres&password=" + DB_PASSWORD;
        conn = DriverManager.getConnection(pathCon);
        return conn;
    }

    public static Connection getConnection(){
        return conn;
    }
}