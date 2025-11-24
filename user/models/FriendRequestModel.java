package user.models;
import admin.models.dbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.postgresql.core.SqlCommand;

public class FriendRequestModel {
    private String username;
    private String request;
    private Connection conn = dbConnection.getConnection();
    public FriendRequestModel(String username){
        this.username = username;
    }

    public FriendRequestModel(String username, String request){
        this.username = username;
        this.request = request;
    }

    // fetching api
    public List<String> fetchingFriendRequest() throws SQLException{
        List<String> res = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        String sql = """
                    select ai.username
                    from friend_request fr
                    left join account_info ai on ai.user_id = fr.request
                    where fr.user_id = (select user_id from account_info where username = ?)
                """;
        st = conn.prepareStatement(sql);
        st.setString(1, username);
        rs = st.executeQuery();
        while (rs.next()) {
            res.add(rs.getString(1));
        }
        return res;
    }
    public boolean updateDeclineToDb() throws SQLException{
        long idUser1 = 0;
        long idUser2 = 0;
        int rowAffected = 0;
        PreparedStatement st = null;
        String sqlId = """
                    select fr.user_id, fr.request
                    from friend_request fr
                    join account_info ai1 on fr.user_id = ai1.user_id
                    join account_info ai2 on fr.request = ai2.user_id
                    where ai1.username = ? and ai2.username = ?
                """;
        st = conn.prepareStatement(sqlId);
        st.setString(1, username);
        st.setString(2, request);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            idUser1 = rs.getLong(1);
            idUser2 = rs.getLong(2);
        }
        if(idUser1 == 0 || idUser2 == 0)
            return false;

        String sql = "delete from friend_request where user_id = ? and request = ?";
        st = conn.prepareStatement(sql);
        st.setLong(1, idUser1);
        st.setLong(2, idUser2);
        rowAffected = st.executeUpdate();

        if(rowAffected < 0)
            return false;
        return true;
    }

    public boolean updateAcceptedToDb() throws SQLException{
        long idUser1 = 0; 
        long idUser2 = 0;
        int rowsAffected1, rowsAffected2;
        PreparedStatement st = null;
        String sqlId = """
                    select fr.user_id, fr.request
                    from friend_request fr
                    join account_info ai1 on fr.user_id = ai1.user_id
                    join account_info ai2 on fr.request = ai2.user_id
                    where ai1.username = ? and ai2.username = ?
                """;
        st = conn.prepareStatement(sqlId);
        st.setString(1, username);
        st.setString(2, request);
        String sql1 = """
                    insert into friend (user1, user2) values (?, ?);
                    insert into friend (user1, user2) values (?, ?);
                """;
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            idUser1 = rs.getLong(1);
            idUser2 = rs.getLong(2);
        }

        if(idUser1 == 0 || idUser2 == 0)
            return false;
        st = conn.prepareStatement(sql1);
        st.setLong(1, idUser1);
        st.setLong(2, idUser2);

        st.setLong(3, idUser2);
        st.setLong(4, idUser1);
        rowsAffected1 = st.executeUpdate();

        if(rowsAffected1 <= 0)
            return false;

        String sql2 = "delete from friend_request where user_id = ? and request = ?";
        st = conn.prepareStatement(sql2);
        st.setLong(1, idUser1);
        st.setLong(2, idUser2);
        rowsAffected2 = st.executeUpdate();

        if(rowsAffected2 < 0)
            return false;
        return true;
    }
}
