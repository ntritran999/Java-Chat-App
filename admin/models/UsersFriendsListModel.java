package admin.models;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersFriendsListModel{
    private int id;
    private String userName;
    private String fullName;
    private String email;
    private String createdDate;
    private int directFriends;
    private int friendOfFriends;
    private static Connection conn = dbConnection.getConnection();

    public UsersFriendsListModel(int id, String userName, String fullName, String email, 
                            String createdDate, int directFriends, int friendOfFriends){
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.createdDate = createdDate;
        this.directFriends = directFriends;
        this.friendOfFriends = friendOfFriends;
    }

    public int getId(){
        return id;
    }

    public String getUserName(){
        return userName;
    }

    public String getFullName(){
        return fullName;
    }

    public String getEmail(){
        return email;
    }

    public String getCreatedDate(){
        return createdDate;
    }

    public int getDirectFriends(){
        return directFriends;
    }

    public int getFriendOfFriends(){
        return friendOfFriends;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setCreatedDate(String createdDate){
        this.createdDate = createdDate;
    }

    public void setDirectFriends(int directFriends){
        this.directFriends = directFriends;
    }

    public void setFriendOfFriends(int friendOfFriends){
        this.friendOfFriends = friendOfFriends;
    }

    public Object[] toTableRow() {
        return new Object[]{
            id, userName, fullName, email, createdDate, directFriends, friendOfFriends
        };
    }
    // fetching API
    public static List<UsersFriendsListModel> getAllUsers(String searchName, String sortBy, String filterOpertator, int friendsCount) throws SQLException{
        List<UsersFriendsListModel> users = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        // query
        sql.append("""  
                    with direct_friends_list as(
                        select  user1 , count(user2) as direct_count
                        from friend
                        group by user1
                    ),
                    friends_of_friends_list as(
                        select fl1.user1, count(distinct fl2.user2) as fof_count
                        from friend fl1
                        join friend fl2 on fl1.user2 = fl2.user1
                        where fl1.user1 != fl2.user2
                        group by fl1.user1
                    )

                    select ai.user_id, ai.username, ui.fullname, a.email, to_char(a.create_date, 'DD/MM/YYYY HH24:MI:SS') as created_date,
                        coalesce(df.direct_count, 0) as direct_friends, 
                        coalesce(fof.fof_count, 0) as friends_of_friends
                    from account_info ai
                    join user_info ui on ui.id = ai.user_id
                    join account a on a.username = ai.username
                    left join direct_friends_list df on ai.user_id = df.user1
                    left join friends_of_friends_list fof on ai.user_id = fof.user1
                    where 1 = 1 
                """);
        // logic sql cho filter and sort
        if(searchName != null && !searchName.trim().isEmpty()){
            sql.append(" and (lower(ai.username) like lower(?) or lower(ui.fullname) like lower(?)) ");
        }

        if((filterOpertator != null) && (friendsCount != -1)){
            sql.append("and coalesce(df.direct_count, 0) ").append(filterOpertator).append(" ? ");
        }

        sql.append("""
                        order by""");
        if(sortBy != null){
            switch (sortBy){
                case "NAME_ASC":
                    sql.append(" ui.fullname ASC");
                    break;
                case "NAME_DESC":
                    sql.append(" ui.fullname DESC");
                    break;
                case "DATE_ASC":
                    sql.append(" a.create_date ASC");
                    break;
                case "DATE_DESC":
                    sql.append(" a.create_date DESC");
                    break;
                case "FRIENDS_ASC":
                    sql.append(" direct_friends ASC");
                    break;
                case "FRIENDS_DESC":
                    sql.append(" direct_friends DESC");
                    break;
                default:
                    sql.append(" a.create_date DESC");
            }
        } else
            sql.append(" a.create_date DESC");
        
        st = conn.prepareStatement(sql.toString());
        int paramIndex = 1;
        if(searchName != null && !searchName.trim().isEmpty()){
            String searchPattern = "%" + searchName.trim() + "%";
            st.setString(paramIndex++, searchPattern);
            st.setString(paramIndex++, searchPattern);
        }

        if(filterOpertator != null && friendsCount != -1){
            st.setInt(paramIndex++, friendsCount);
        }
        // fetch data
        rs = st.executeQuery();
        while (rs.next()){
            UsersFriendsListModel user = new UsersFriendsListModel(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("fullname"),
                rs.getString("email"),
                rs.getString("created_date"),
                rs.getInt("direct_friends"),
                rs.getInt("friends_of_friends")
            );
            users.add(user);
        }

        // Close resources
        if (rs != null) 
            rs.close();
        if(st != null) 
            st.close();
        return users;
    }
}