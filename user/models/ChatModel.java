package user.models;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatModel {
    private Connection conn;
    private int userId, receiver;
    private ArrayList<HashMap<String, String>> chatHistory;
    public ChatModel(Connection conn, String username, int otherId, String type) {
        this.conn = conn;
        userId = findUserId(username);
        receiver = otherId;
        chatHistory = new ArrayList<>();

        if (type == "single") {
            loadChatHistory();
        }
    }

    public int findUserId(String username) {
        int id = -1;
        try {
            String query = """
                    SELECT user_id
                    FROM account_info
                    WHERE username=?
                    """;
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            rs.next();
            id = rs.getInt("user_id");
            rs.close();
            st.close();

            return id;
            
        } catch (Exception e) {
            System.out.println(e);
        }
        return id;
    }

    public int getUserId() {
        return userId;
    }

    private void loadChatHistory() {
        try {
            chatHistory.clear();
            String query = """
                    SELECT content, seen, sender 
                    FROM message_user mu1
                    JOIN message_user mu2 ON mu2.message_id=mu1.message_id
                    JOIN messages ON messages.id = mu1.message_id
                    WHERE mu1.user_id=? AND mu2.user_id=? AND mu1.group_id IS NULL
                    """;
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, userId);
            st.setInt(2, receiver);
            ResultSet rs = st.executeQuery();
            String content, seen, sender;
            while (rs.next()) {
                content = rs.getString("content");
                seen = rs.getBoolean("seen") ? "seen" : "unseen";
                sender = String.valueOf(rs.getInt("sender"));

                HashMap<String, String> row = new HashMap<>();
                row.put("content", content);
                row.put("seen", seen);
                row.put("sender", sender);
                chatHistory.add(row);
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void saveChat(String msg) {
        try {
            String query = """
                    INSERT INTO messages (content, time_sent, seen, sender)
                    VALUES (?, ?, FALSE, ?)
                    RETURNING id
                    """;
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, msg);
            st.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            st.setInt(3, userId);
            
            ResultSet rs = st.executeQuery();
            rs.next();
            int msgId = rs.getInt("id");

            query = """
                    INSERT INTO message_user (message_id, user_id)
                    VALUES (?, ?)
                    """;
            int ids[] = {userId, receiver};
            for (int id: ids) {
                st = conn.prepareStatement(query);
                st.setInt(1, msgId);
                st.setInt(2, id);
                st.execute();
            }

            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public ArrayList<HashMap<String, String>> getChatHistory() {
        return chatHistory;
    }
}
