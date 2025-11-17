package admin.models;

import java.sql.*;
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
                    SELECT account_info.username, user_info.fullname, activities.last_login_date
                    FROM activities JOIN user_info ON activities.user_id=user_info.id
                                    JOIN account_info ON account_info.user_id=user_info.id
                    ORDER BY activities.last_login_date DESC;
                    """;
            ResultSet rs = st.executeQuery(query);
            String username, fullname;
            Date date;
            while (rs.next()) {
                username = rs.getString("username");
                fullname = rs.getString("fullname");
                date = rs.getDate("last_login_date");

                HashMap<String, String> row = new HashMap<>();
                row.put("username", username);
                row.put("fullname", fullname);
                row.put("loginDate", date.toString());
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
