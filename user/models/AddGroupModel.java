package user.models;

import java.sql.*;
import java.time.LocalDate;

public class AddGroupModel {
    public static String getUsernameFromId(Connection conn, int id) throws SQLException {
        String username = "";
        String query = """
                SELECT username
                FROM account_info
                WHERE user_id=?
                """;
        PreparedStatement st = conn.prepareStatement(query);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        rs.next();
        username = rs.getString("username");
        rs.close();
        st.close();

        return username;
    }

    public static void createGroup(Connection conn, String groupName, String[] memList, String admin) throws SQLException {
        String query = """
                INSERT INTO message_group (group_name, create_date)
                VALUES (?, ?)
                RETURNING group_id
                """;
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, groupName);
        st.setDate(2, Date.valueOf(LocalDate.now()));
        ResultSet rs = st.executeQuery();
        rs.next();
        int group_id = rs.getInt("group_id");
        for(String mem : memList) {
            boolean isAdmin = mem.equals(admin);
            addMember(conn, mem, group_id, isAdmin);
        }
        rs.close();
        st.close();
    }

    public static void addMember(Connection conn, String mem, int groupId, boolean isAdmin) throws SQLException {
        String query = """
                    INSERT INTO group_member (user_id, group_id, join_date, admin)
                    SELECT user_id, ?, ?, ?
                    FROM account_info
                    WHERE username=?
                    """;
        PreparedStatement st = conn.prepareStatement(query);
        st.setInt(1, groupId);
        st.setDate(2, Date.valueOf(LocalDate.now()));
        st.setBoolean(3, isAdmin);
        st.setString(4, mem);
        st.execute();
        st.close();
    }
}
