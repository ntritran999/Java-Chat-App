package user.models;

import java.sql.*;
import java.util.ArrayList;

public class PersonSearchModel {
    private Connection conn;
    private String input, username;
    private ArrayList<String> results;
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
                    SELECT account.username, block.user2
                    FROM account
                    JOIN account_info AS find_ai ON find_ai.username=account.username
                    LEFT JOIN block ON block.user1=find_ai.user_id
                    JOIN account_info AS cur_ai ON cur_ai.username=?
                    WHERE account.username!=? AND account.username ILIKE ? AND (block.user2 IS NULL OR cur_ai.user_id!=block.user2)
                    """;
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, username);
            st.setString(2, username);
            st.setString(3, input + "%");
            ResultSet rs = st.executeQuery();

            boolean foundByUsername = false;
            String name;
            while (rs.next()) {
                if (!foundByUsername) {
                    foundByUsername = true;
                }
                name = rs.getString("username");
                results.add(name);
            }

            if (!foundByUsername) {
                query = """
                        SELECT user_info.fullname
                        FROM user_info
                        JOIN account_info AS find_ai ON find_ai.user_id=user_info.id
                        LEFT JOIN block ON block.user1=find_ai.user_id
                        JOIN account_info AS cur_ai ON cur_ai.username=?
                        WHERE find_ai.username!=? AND user_info.fullname ILIKE ? AND (block.user2 IS NULL OR cur_ai.user_id!=block.user2)
                        """;
                st = conn.prepareStatement(query);
                st.setString(1, username);
                st.setString(2, username);
                st.setString(3, input + "%");
                rs = st.executeQuery();

                while (rs.next()) {
                    name = rs.getString("fullname");
                    results.add(name);
                }
            }

            rs.close();
            st.close();
            
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    public ArrayList<String> getResults() {
        return results;
    } 
}
