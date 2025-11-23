package user.models;

import java.sql.*;
import java.time.LocalDate;

public class ActivitiesModel {
    public static void setOnline(Connection conn, String username) throws SQLException {
        String query = """
                UPDATE account
                SET status='A'
                WHERE username=?
                """;
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, username);
        st.execute();

        query = """
                INSERT INTO activities (user_id, last_login_date)
                SELECT user_id, ?
                FROM account_info
                WHERE username=?
                """;
        st = conn.prepareStatement(query);
        st.setDate(1, Date.valueOf(LocalDate.now()));
        st.setString(2, username);
        st.execute();
        st.close();
    }

    public static void setOffline(Connection conn, String username) throws SQLException {
        String query = """
                UPDATE account
                SET status='I'
                WHERE username=?
                """;
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, username);
        st.execute();
        st.close();
    }
}
