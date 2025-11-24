package admin.models;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class LoginHistoryModel {
    private Connection conn;
    private ArrayList<HashMap<String, String>> history;
    public LoginHistoryModel(Connection conn) {
        history = new ArrayList<>();
        this.conn = conn;
    }

    public void loadLoginHistory() {
        try {
            history.clear();
            Statement st = conn.createStatement();
            String query = """
                    SELECT account_info.username, user_info.fullname, activities.last_login
                    FROM activities JOIN user_info ON activities.user_id=user_info.id
                                    JOIN account_info ON account_info.user_id=user_info.id
                    ORDER BY activities.last_login DESC;
                    """;
            ResultSet rs = st.executeQuery(query);
            String username, fullname;
            Timestamp date;
            while (rs.next()) {
                username = rs.getString("username");
                fullname = rs.getString("fullname");
                date = rs.getTimestamp("last_login");

                HashMap<String, String> row = new HashMap<>();
                row.put("username", username);
                row.put("fullname", fullname);
                row.put("loginDate", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date));
                history.add(row);
            }
            rs.close();
            st.close();
            
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void loadHistoryById(String id){
        try {
            history.clear();
            PreparedStatement st = null;
            String query = """
                    SELECT account_info.username, user_info.fullname, activities.last_login
                    FROM activities JOIN user_info ON activities.user_id=user_info.id
                                    JOIN account_info ON account_info.user_id=user_info.id
                    where activities.user_id = (?)
                    ORDER BY activities.last_login DESC;
                    """;
            st = conn.prepareStatement(query);
            if(id != null && !id.trim().isEmpty())
                st.setLong(1, Long.parseLong(id));
            ResultSet rs = st.executeQuery();
            String username, fullname;
            Timestamp date;
            while (rs.next()) {
                username = rs.getString("username");
                fullname = rs.getString("fullname");
                date = rs.getTimestamp("last_login");

                HashMap<String, String> row = new HashMap<>();
                row.put("username", username);
                row.put("fullname", fullname);
                row.put("loginDate", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date));
                history.add(row);
            }
            rs.close();
            st.close();
            
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    public ArrayList<HashMap<String, String>> getHistory() {
        return history;
    } 
}
