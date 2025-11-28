package admin.models;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActiveUsersListModel {
    private long id;
    private String username;
    private int numOpen;
    private int numChat;
    private int numGroup;
    
    private Connection conn = dbConnection.getConnection();

    public ActiveUsersListModel(){}
    public ActiveUsersListModel(long id, String username, int numOpen, int numChat, int numGroup){
        this.id = id;
        this.username = username;
        this.numOpen = numOpen;
        this.numChat = numChat;
        this.numGroup = numGroup;
    }

    public long getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public int getNumOpen(){
        return numOpen;
    }

    public int getNumChat(){
        return numChat;
    }
    public int getNumGroup(){
        return numGroup;
    }

    // fetching data
    public List<ActiveUsersListModel> getAllUsers(String startDate, String endDate, String usernameSearch, String comboSort, String filterOp, int filterNum) throws SQLException{
        List<ActiveUsersListModel> users = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        sql.append(
            """
                    select ai.user_id, ai.username, count(distinct act.last_login) as act_count, count(distinct mu2.user_id) as num_chat, count(distinct gm.group_id) as group_count
                    from account_info ai
                    left join account a on a.username = ai.username
                    left join group_member gm on gm.user_id = ai.user_id
                    left join activities act on act.user_id = ai.user_id
                    left join message_user mu1 on mu1.user_id = ai.user_id
                    left join message_user mu2 on mu1.message_id = mu2.message_id and mu1.user_id <> mu2.user_id 
                    where 1 = 1 and mu1.group_id is null and mu2.group_id is null
                """
        );  
        if(startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty())
            sql.append(" and a.create_date >= ? and a.create_date <= ?");

        if(usernameSearch != null && !usernameSearch.isEmpty())
            sql.append(" and a.username = ?");

        sql.append(" group by ai.user_id, ai.username, a.create_date");
        
        if((filterOp != null) && (filterNum != -1))
            sql.append(" having coalesce(count(distinct act.last_login), 0) ").append(filterOp).append(" ?");

        sql.append(" order by");
        if(comboSort != null && !comboSort.isEmpty()){
            switch (comboSort){
                case "NAME_ASC":
                    sql.append(" ai.username ASC");
                    break;
                case "NAME_DESC":
                    sql.append(" ai.username DESC");
                    break;
                case "DATE_ASC":
                    sql.append(" a.create_date ASC");
                    break;
                case "DATE_DESC":
                    sql.append(" a.create_date DESC");
                    break;
                default:
                    sql.append(" a.create_date DESC");
            }
        }else
            sql.append(" a.create_date DESC");

        st = conn.prepareStatement(sql.toString());
        int paramIndex = 1;
        if(startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()){
            st.setDate(paramIndex++, Date.valueOf(startDate));
            st.setDate(paramIndex++, Date.valueOf(endDate));
        }

        if(usernameSearch != null && !usernameSearch.isEmpty())
            st.setString(paramIndex++, usernameSearch);

        if((filterOp != null) && (filterNum != -1))
            st.setInt(paramIndex++, filterNum);

        rs = st.executeQuery();
        while (rs.next()) {
            ActiveUsersListModel user = new ActiveUsersListModel(
                rs.getLong("user_id"),
                rs.getString("username"),
                rs.getInt("act_count"),
                rs.getInt("num_chat"),
                rs.getInt("group_count")
            );
            users.add(user);
        }
        return users;
    }
}
