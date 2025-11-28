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
                    SELECT messages.id, content, seen, sender 
                    FROM message_user mu1
                    JOIN message_user mu2 ON mu2.message_id=mu1.message_id
                    JOIN messages ON messages.id = mu1.message_id
                    WHERE mu1.user_id=? AND mu2.user_id=? AND mu1.group_id IS NULL
                    """;
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, userId);
            st.setInt(2, receiver);
            ResultSet rs = st.executeQuery();
            String msgId, content, seen, sender;
            while (rs.next()) {
                msgId = rs.getString("id");
                content = rs.getString("content");
                seen = rs.getBoolean("seen") ? "seen" : "unseen";
                sender = String.valueOf(rs.getInt("sender"));

                HashMap<String, String> row = new HashMap<>();
                row.put("msgId", msgId);
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

    public int saveChat(String msg) {
        int msgId = -1;
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
            msgId = rs.getInt("id");

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
        return msgId;
    }

    public void delChat(int msgId) {
        try {
            String query = """
                    DELETE FROM message_user
                    WHERE message_id=? AND user_id=?
                    """;
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, msgId);
            st.setInt(2, userId);
            st.execute();
            st.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void delAllChat(Connection conn, String username, int receiver, String type) {
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
            int userId = rs.getInt("user_id");
            
            if (type.equals("single")) {
                query = """
                        DELETE FROM message_user
                        WHERE user_id=? AND message_id IN (SELECT message_id
                                                        FROM message_user
                                                        WHERE user_id=? AND group_id IS NULL)
                        """;
            }
            st = conn.prepareStatement(query);
            st.setInt(1, userId);
            st.setInt(2, receiver);
            st.execute();

            rs.close();
            st.close();
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static HashMap<String, String> findTextFromAll(Connection conn, String username, String text) {
        HashMap<String, String> res = new HashMap<>();
        try {
            String query = """
                    SELECT id
                    FROM messages
                    WHERE content LIKE ?
                    """;
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, "%" + text + "%");
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                int msgId = rs.getInt("id");
                query = """
                        SELECT mu2.user_id, mu2.group_id
                        FROM message_user mu1
                        JOIN account_info ai ON ai.user_id=mu1.user_id
                        JOIN message_user mu2 ON mu1.message_id=mu2.message_id AND mu2.user_id!=ai.user_id
                        WHERE ai.username=? AND mu1.message_id=?
                        """;
                st = conn.prepareStatement(query);
                st.setString(1, username);
                st.setInt(2, msgId);
                rs = st.executeQuery();
                if (rs.next()) {
                    int groupId = rs.getInt("group_id");
                    int receiver = 0;
                    String type = "";
                    if (rs.wasNull()) {
                        receiver = rs.getInt("user_id");
                        type = "single";
                    }
                    else {
                        receiver = groupId;
                        type = "multiple";
                    }
                    res.put("receiver", String.valueOf(receiver));
                    res.put("type", type);
                    res.put("name", findName(conn, receiver, type));
                    return res;
                }
            }

            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return res;
    }

    private static String findName(Connection conn, int id, String type) {
        String name = "";
        try {
            String query;
            if (type.equals("single")) {
                query = """
                        SELECT fullname
                        FROM account_info 
                        JOIN user_info ON account_info.user_id=user_info.id
                        WHERE user_info.id=?
                        """;
            }
            else {
                query = """
                        SELECT group_name
                        FROM message_group
                        WHERE group_id=?
                        """;
            }
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            rs.next();
            if (type.equals("single")) {
                name = rs.getString("fullname");
            }
            else {
                name = rs.getString("group_name");
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return name;
    }

    public ArrayList<HashMap<String, String>> getChatHistory() {
        return chatHistory;
    }
}
