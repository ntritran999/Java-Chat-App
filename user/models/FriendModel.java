package user.models;
import admin.models.dbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FriendModel {
    private String username;
    private long idOther;
    private Connection conn;
    
    public FriendModel(String username, long idOther, Connection conn){
        this.conn = conn;
        this.username = username;
        this.idOther = idOther;
    }
    
    public FriendModel(String username, Connection conn){
        this.conn = conn;
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    public long getIdOther(){
        return idOther;
    }
    // fetching api
    public long queryIdByUsername(String userName) throws SQLException{
        PreparedStatement st = null;
        String sql = """
                    select user_id
                    from account_info
                    where username = ?
                """;
        st = conn.prepareStatement(sql);
        st.setString(1, userName);
        ResultSet rs = st.executeQuery();
        rs.next();
        long id = rs.getLong("user_id");
        rs.close();
        st.close();
        return id;
    }

    public boolean sendFriendRequest() throws SQLException{
        PreparedStatement st = null;
        // check valid
        String tmp = """
                    select user_id from friend_request where user_id = ? and request = ?
                """;
        st = conn.prepareStatement(tmp);
        st.setLong(1, idOther);
        st.setLong(2, queryIdByUsername(username));
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            long id = -1;
            rs.getLong("user_id");
            if(id == -1)
                return false;
        }
        // send request
        String sql = """
                    insert into friend_request
                    values (?, ?)
                """;
        st = conn.prepareStatement(sql);
        st.setLong(1, idOther);
        st.setLong(2, queryIdByUsername(username));

        int rowAffected = st.executeUpdate();
        return rowAffected > 0;
    }

    public boolean unFriendRelation() throws SQLException{
        PreparedStatement st = null;
        String sql =  """
                    delete from friend where user1 = ? and user2 = ?;
                    delete from friend where user1 = ? and user2 = ?
                """;
        st = conn.prepareStatement(sql);
        st.setLong(1, queryIdByUsername(username));
        st.setLong(2, idOther);
        st.setLong(3, idOther);
        st.setLong(4, queryIdByUsername(username));
        int rowAffected = st.executeUpdate();
        return rowAffected > 0;
    }

    public boolean blockFriend() throws SQLException{
        PreparedStatement st = null;
        String sql = """
                    insert into block 
                    values (?, ?)
                """;
        st = conn.prepareStatement(sql);
        st.setLong(1, queryIdByUsername(username));
        st.setLong(2, idOther);
        int rowAffected = st.executeUpdate();
        return (rowAffected > 0) && unFriendRelation();
    }

    public ArrayList<List<String>> getFriendList() throws SQLException {
        ArrayList<List<String>> user = new ArrayList<>();
        PreparedStatement st = null;
        String sql = """
                    select ui.fullname, ui.id, a.status
                    from user_info ui
                    join friend f1 on f1.user2 = ui.id and f1.user1 = (select user_id from account_info where username = ?)
                    join account a on a.username = (select username from account_info where user_id = ui.id)
                """;
        st = conn.prepareStatement(sql);
        st.setString(1, username);
        ResultSet rs = st.executeQuery();
        String fullName, userId, userStatus;
        while(rs.next()){
            fullName = rs.getString("fullname");
            userId = rs.getString("id");
            userStatus = rs.getString("status");
            switch (userStatus) {
                case "A":
                    userStatus = "Online";
                    break;
                case "I":
                    userStatus = "Offline";
                    break;
                case "L":
                    userStatus = "Bị khoá tài khoản";
                    break;
                default:
                    userStatus = "";
                    break;
            }
            List<String> row = new ArrayList<>();
            row.add(fullName);
            row.add(userId);
            row.add(userStatus);
            user.add(row);
        }
        rs.close();
        st.close();
        return user;
    } 

}
