package user.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ConversationModel {
    private Connection conn;
    private String username;
    private ArrayList<HashMap<String, String>> conversations;
    public ConversationModel(Connection conn, String username) {
        this.conn = conn;
        this.username = username;
        conversations = new ArrayList<>();
        loadConversations();
    }

    private void loadConversations() {
        try {
            conversations.clear();
            String query = """
                    (
                        SELECT acc2.user_id id, user_info.fullname name, account.status status, 'friend' type, MAX(messages.time_sent) last_msg_time
                        FROM account_info acc1
                        JOIN message_user mu1 ON mu1.user_id=acc1.user_id
                        JOIN message_user mu2 ON mu2.message_id=mu1.message_id AND mu2.user_id!=acc1.user_id
                        JOIN account_info acc2 ON acc2.user_id=mu2.user_id
                        JOIN friend f ON (
                            (f.user1=acc1.user_id AND f.user2=acc2.user_id) OR
                            (f.user2=acc1.user_id AND f.user1=acc2.user_id)
                        )
                        JOIN user_info ON acc2.user_id=user_info.id
                        JOIN account ON account.username=acc2.username
                        JOIN messages ON mu1.message_id=messages.id
                        WHERE acc1.username=? AND mu1.group_id IS NULL
                        GROUP BY acc2.user_id, user_info.fullname, account.status
                        
                        UNION ALL
                        
                        SELECT acc2.user_id id, user_info.fullname name, account.status status, 'stranger' type, MAX(messages.time_sent) last_msg_time
                        FROM account_info acc1
                        JOIN message_user mu1 ON mu1.user_id=acc1.user_id
                        JOIN message_user mu2 ON mu2.message_id=mu1.message_id AND mu2.user_id!=acc1.user_id
                        JOIN account_info acc2 ON acc2.user_id=mu2.user_id
                        LEFT JOIN friend f ON (
                            (f.user1=acc1.user_id AND f.user2=acc2.user_id) OR
                            (f.user2=acc1.user_id AND f.user1=acc2.user_id)
                        )
                        JOIN user_info ON acc2.user_id=user_info.id
                        JOIN account ON account.username=acc2.username
                        JOIN messages ON mu1.message_id=messages.id
                        WHERE acc1.username=? AND mu1.group_id IS NULL AND f.user1 IS NULL
                        GROUP BY acc2.user_id, user_info.fullname, account.status

                        UNION ALL

                        SELECT mg.group_id id, mg.group_name name, '' status, 'group' type, COALESCE(MAX(messages.time_sent), mg.create_date) last_msg_time
                        FROM account_info acc1
                        JOIN group_member gm ON gm.user_id=acc1.user_id
                        JOIN message_group mg ON mg.group_id=gm.group_id
                        LEFT JOIN message_user mu1 ON mg.group_id=mu1.group_id AND mu1.user_id=acc1.user_id
                        LEFT JOIN messages ON messages.id=mu1.message_id
                        WHERE acc1.username=?
                        GROUP BY mg.group_id, mg.group_name, mg.create_date
                    )
                    ORDER BY last_msg_time DESC
                    """;
            PreparedStatement st = conn.prepareStatement(query);
            for (int i = 1; i <= 3; i++) {
                st.setString(i, username);
            }
            ResultSet rs = st.executeQuery();
            String id, name, status, type;
            while (rs.next()) {
                id = rs.getString("id");
                name = rs.getString("name");
                status = rs.getString("status");
                if (status.equals("A")) {
                    status = "Online";
                }
                else {
                    status = "Offline";
                }
                type = rs.getString("type");

                HashMap<String, String> row = new HashMap<>();
                row.put("id", id);
                row.put("name", name);
                row.put("status", status);
                row.put("type", type);
                conversations.add(row);
            }
            rs.close();
            st.close();
            
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public ArrayList<HashMap<String, String>> getConversations() {
        return conversations;
    }

    public boolean isIdInConversations(int id) {
        for (var conversation: conversations) {
            if (!conversation.get("type").equals("group") && Integer.valueOf(conversation.get("id")) == id) {
                return true;
            }
        }
        return false;
    }
}
