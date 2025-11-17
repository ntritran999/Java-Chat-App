package user.models;

import java.sql.*;
import java.time.LocalDate;

public class SpamModel {
    public static void sendReport(Connection conn, String reportedId, String sender) {
        try {
            String query = """
                    INSERT INTO spam_report (username, report_date, user_id)
                    VALUES (?, ?, ?)
                    """;
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, getReportedUsername(conn, reportedId));
            st.setDate(2, Date.valueOf(LocalDate.now()));
            st.setInt(3, getSenderId(conn, sender));
            st.execute();
            st.close();
            
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private static String getReportedUsername(Connection conn, String reportedId) throws SQLException {
        String username;
        String query = """
                SELECT username
                FROM account_info
                WHERE user_id=?
                """;
        PreparedStatement st = conn.prepareStatement(query);
        st.setInt(1, Integer.valueOf(reportedId));
        ResultSet rs = st.executeQuery();
        rs.next();
        username = rs.getString("username");
        rs.close();
        st.close();
        return username;
    }

    private static int getSenderId(Connection conn, String sender) throws SQLException {
        int id;
        String query = """
                SELECT user_id
                FROM account_info
                WHERE username=?
                """;
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, sender);
        ResultSet rs = st.executeQuery();
        rs.next();
        id = rs.getInt("user_id");
        rs.close();
        st.close();
        return id;
    }

}
