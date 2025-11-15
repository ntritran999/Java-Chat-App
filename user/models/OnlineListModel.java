package user.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class OnlineListModel {
    private Connection conn;
    private String username;
    private ArrayList<HashMap<String, String>> onlines;
    public OnlineListModel(Connection conn, String username) {
        onlines = new ArrayList<>();
        this.conn = conn;
        this.username = username;
        loadOnlines();
    }

    private void loadOnlines() {
        try {
            onlines.clear();
            String query = """
                    SELECT fullname,user_info.id
                    FROM account_info acc1
                    JOIN friend f ON (acc1.user_id=f.user1 OR acc1.user_id=f.user2)
                    JOIN account_info acc2 ON acc2.user_id=(CASE 
                                                                WHEN acc1.user_id=f.user1 THEN f.user2
                                                                ELSE f.user1
                                                                END)
                    JOIN account ON acc2.username=account.username
                    JOIN user_info ON acc2.user_id=user_info.id
                    WHERE acc1.username=? and account.status='A'
                    """;
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            String fullName, userId;
            while (rs.next()) {
                fullName = rs.getString("fullname");
                userId = rs.getString("id");

                HashMap<String, String> row = new HashMap<>();
                row.put("fullname", fullName);
                row.put("id", userId);
                onlines.add(row);
            }
            rs.close();
            st.close();
            
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    public ArrayList<HashMap<String, String>> getOnlines() {
        return onlines;
    } 
}
