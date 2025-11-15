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
                    SELECT user_info.fullname,user_info.id
                    FROM account AS acc1 
                    JOIN account_info AS acc_info1 ON acc1.username=acc_info1.username
                    JOIN friend ON acc_info1.user_id=friend.user1
                    JOIN account_info AS acc_info2 ON friend.user2=acc_info2.user_id
                    JOIN account AS acc2 ON acc_info2.username=acc2.username
                    JOIN user_info ON acc_info2.user_id=user_info.id
                    WHERE acc1.username=? and acc2.status='A'
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
