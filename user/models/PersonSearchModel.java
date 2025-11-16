package user.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class PersonSearchModel {
    private Connection conn;
    private String input, username;
    private ArrayList<HashMap<String, String>> results;
    public PersonSearchModel(Connection conn, String input, String username) {
        results = new ArrayList<>();
        this.conn = conn;
        this.input = input;
        this.username = username;
        loadResults();
    }

    private void loadResults() {
        try {
            results.clear();
            String query = """
                    SELECT account.username, find_ai.user_id
                    FROM account
                    JOIN account_info find_ai ON find_ai.username=account.username
                    LEFT JOIN block ON block.user1=find_ai.user_id
                    JOIN account_info cur_ai ON cur_ai.username=?
                    WHERE account.username!=? AND account.username ILIKE ? AND (block.user2 IS NULL OR cur_ai.user_id!=block.user2)
                    """;
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, username);
            st.setString(2, username);
            st.setString(3, input + "%");
            ResultSet rs = st.executeQuery();

            boolean foundByUsername = false;
            String name, id;
            while (rs.next()) {
                if (!foundByUsername) {
                    foundByUsername = true;
                }
                name = rs.getString("username");
                id = rs.getString("user_id");
                HashMap<String, String> row = new HashMap<>();
                row.put("name", name);
                row.put("id", id);
                results.add(row);
            }

            if (!foundByUsername) {
                query = """
                        SELECT user_info.fullname, user_info.id
                        FROM user_info
                        JOIN account_info find_ai ON find_ai.user_id=user_info.id
                        LEFT JOIN block ON block.user1=find_ai.user_id
                        JOIN account_info cur_ai ON cur_ai.username=?
                        WHERE find_ai.username!=? AND user_info.fullname ILIKE ? AND (block.user2 IS NULL OR cur_ai.user_id!=block.user2)
                        """;
                st = conn.prepareStatement(query);
                st.setString(1, username);
                st.setString(2, username);
                st.setString(3, input + "%");
                rs = st.executeQuery();

                while (rs.next()) {
                    name = rs.getString("fullname");
                    id = rs.getString("id");
                    HashMap<String, String> row = new HashMap<>();
                    row.put("name", name);
                    row.put("id", id);
                    results.add(row);
                }
            }

            rs.close();
            st.close();
            
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    public ArrayList<HashMap<String, String>> getResults() {
        return results;
    } 
}
