package user.models;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingWorker;

public class GroupSettingModel {
    public static void changeGroupName(Connection conn, int groupId, String newGroupName) throws SQLException {
        String query = """
                UPDATE message_group
                SET group_name=?
                WHERE group_id=?
                """;
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, newGroupName);
        st.setInt(2, groupId);
        st.execute();
        st.close();
    } 

    public static void addMember(Connection conn, int groupId, String member) throws SQLException {
        String query = """
                    SELECT username
                    FROM account_info ai
                    JOIN user_info ui ON ui.id=ai.user_id
                    WHERE (username=? OR fullname=?) AND ai.user_id NOT IN (SELECT user_id
                                                                        FROM group_member
                                                                        WHERE group_id=?)
                    """;
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, member);
        st.setString(2, member);
        st.setInt(3, groupId);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            String username = rs.getString("username");
            AddGroupModel.addMember(conn, username, groupId, false);
        }
        rs.close();
        st.close();
    }

    public static void setAdmin(Connection conn, int groupId, String member) throws SQLException {
        String query = """
                UPDATE group_member
                SET admin=TRUE
                WHERE group_id=? AND user_id=(SELECT gm.user_id
                                            FROM group_member gm
                                            JOIN account_info ai ON ai.user_id=gm.user_id
                                            WHERE gm.group_id=? AND ai.username=?)
                """;
        PreparedStatement st = conn.prepareStatement(query);
        st.setInt(1, groupId);
        st.setInt(2, groupId);
        st.setString(3, member);
        st.execute();
        st.close();
    }

    public static void removeMember(Connection conn, int groupId, String member, String username) throws SQLException {
        String query = """
                    SELECT gm.admin
                    FROM group_member gm
                    JOIN account_info ai ON ai.user_id=gm.user_id
                    WHERE gm.group_id=? AND ai.username=?
                    """;
        PreparedStatement st = conn.prepareStatement(query);
        st.setInt(1, groupId);
        st.setString(2, username);
        
        ResultSet rs = st.executeQuery();
        rs.next();
        boolean isAdmin = rs.getBoolean("admin");
        if (isAdmin) {
            query = """
                    DELETE FROM group_member
                    WHERE group_id=? AND user_id=(SELECT gm.user_id
                                            FROM group_member gm
                                            JOIN account_info ai ON ai.user_id=gm.user_id
                                            WHERE gm.group_id=? AND ai.username=?)
                    """;
            st = conn.prepareStatement(query);
            st.setInt(1, groupId);
            st.setInt(2, groupId);
            st.setString(3, member);
            st.execute();
        }

        rs.close();
        st.close();
    }
}
