package admin.models;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class RegisteredUserListModel {
    private String username;
    private String fullName;
    private String email;
    private String dateCreated;
    private String status;
    private Connection conn = dbConnection.getConnection();

    public RegisteredUserListModel(){}

    public RegisteredUserListModel(String username, String fullName, String email, String dateCreated, String status){
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.dateCreated = dateCreated;
        this.status = status;
    }

    public String getUserName(){
        return username;
    }
    
    public String getFullName(){
        return fullName;
    }
    
    public String getEmail(){
        return email;
    }
    
    public String getDateCreated(){
        return dateCreated;
    }
    
    public String getStatus(){
        return status;
    }
    
    // fetching data
    public List<RegisteredUserListModel>  getAllUsers(String startDate, String endDate, String usernameSearch, String comboSort) throws SQLException{
        List<RegisteredUserListModel> users = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        sql.append("""
                    select a.username, ui.fullname, a.email, a.create_date, a.status
                    from user_info ui
                    left join account_info ai on ai.user_id = ui.id
                    left join account a on a.username = ai.username
                    where 1 = 1
                """);

        if(startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty())
            sql.append(" and a.create_date >= ? and a.create_date <= ?");

        if(usernameSearch != null && !usernameSearch.isEmpty())
            sql.append(" and a.username = ?");
        
        sql.append(" order by");
        if(comboSort != null && !comboSort.isEmpty()){
            switch (comboSort){
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

        if(usernameSearch != null && !usernameSearch.isEmpty()){
            st.setString(paramIndex++, usernameSearch);
        }

        rs = st.executeQuery();
        while (rs.next()) {
            RegisteredUserListModel user = new RegisteredUserListModel(
                rs.getString("username"),
                rs.getString("fullname"),
                rs.getString("email"),
                rs.getString("create_date"),
                rs.getString("status")
            );
            users.add(user);
        }
        return users;
    }
    
}
