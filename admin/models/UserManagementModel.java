package admin.models;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// https://stackoverflow.com/questions/33085493/how-to-hash-a-password-with-sha-512-in-java/33085670#33085670
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class UserManagementModel{
    private int id;
    private String userName;
    private String fullName;
    private String address;
    private String dob; // Date of birth
    private String gender;
    private String email;
    private String createdDate;
    private String status;
    private static Connection conn = dbConnection.getConnection();

    public UserManagementModel(int id, String userName, String fullName, String address, 
                              String dob, String gender, String email, 
                              String createdDate, String status){
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.address = address;
        this.dob = dob;
        this.gender = gender;
        this.email = email;
        this.createdDate = createdDate;
        this.status = status;
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

    public String getAddress(){
        return address;
    }

    public String getDob(){
        return dob;
    }

    public String getGender(){
        return gender;
    }

    public String getEmail(){
        return email;
    }

    public String getCreatedDate(){
        return createdDate;
    }

    public String getStatus(){
        return status;
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

    public void setAddress(String address){
        this.address = address;
    }

    public void setDob(String dob){
        this.dob = dob;
    }

    public void setGender(String gender){
        this.gender = gender;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setCreatedDate(String createdDate){
        this.createdDate = createdDate;
    }

    public void setStatus(String status){
        this.status = status;
    }

    // hash function
    public static String get_SHA_256_SecurePassword(String passwordToHash) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    // fetching API
    public static List<UserManagementModel> getAllUsers(String comboType, String searchName, String comboStatus, String comboSort) throws SQLException{
        List<UserManagementModel> users = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        // query
        sql.append("""
                    select ai.user_id, ai.username, ui.fullname, ui.address, ui.dob, ui.gender, a.email, a.create_date, a.status
                    from account_info ai
                    join user_info ui on ui.id = ai.user_id
                    join account a on a.username = ai.username
                    where 1 = 1
                """);
        // logic sql filter and sort
        if(comboType != null && searchName != null && !searchName.trim().isEmpty()){
            if(comboType == "FULLNAME")
                sql.append(" and (lower(ui.fullname)) like lower(?) ");
            else
                sql.append(" and (lower(ai.username)) like lower(?) ");
        }

        if(comboStatus != null && comboStatus != "ALL"){
            sql.append(" and (lower(a.status)) like lower(?) ");
        }

        sql.append("""
                        order by""");
        if(comboSort != null){
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
        } else
            sql.append(" a.create_date DESC");
        st = conn.prepareStatement(sql.toString());
        int paramIndex = 1;
        if(searchName != null && !searchName.trim().isEmpty()){
            String searchPattern = "%" + searchName.trim() + "%";
            st.setString(paramIndex++, searchPattern);
        }

        if(comboStatus != null && comboStatus != "ALL"){
            String statusPattern = null;
            if(comboStatus == "ONLINE")
                statusPattern = "%" + "A" + "%";
            else if(comboStatus == "BLOCK")
                statusPattern = "%" + "L" + "%";
            st.setString(paramIndex++, statusPattern);
        }
        // fetch data 
        rs = st.executeQuery();
        while(rs.next()){
            UserManagementModel user = new UserManagementModel(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("fullname"),
                rs.getString("address"),
                rs.getString("dob"),
                rs.getString("gender"),
                rs.getString("email"),
                rs.getString("create_date"),
                rs.getString("status")
            );
            users.add(user);
        }

        // close resources
        if (rs != null) 
            rs.close();
        if(st != null) 
            st.close();
        return users;
    }

    public static boolean updatePassword(String id, String newPassword) throws SQLException{
        // hashpassword - co the bo sung
        String hashPassword = get_SHA_256_SecurePassword(newPassword);
        PreparedStatement st = null;
        String sql = """
                    update account 
                    set password = (?)
                    where username = (select username from account_info where user_id = (?))
                """;
        st = conn.prepareStatement(sql);
        int paramIndex = 1;
        if(hashPassword != null && !hashPassword.trim().isEmpty())
            st.setString(paramIndex++, hashPassword.trim());
        
        if(id != null && !id.trim().isEmpty())
            st.setLong(paramIndex++, Long.parseLong(id));

        int rowsAffected = st.executeUpdate(); 
        if(st != null)
            st.close();
        return rowsAffected > 0;
    }

    public static boolean deleteAccount(String id, String username) throws SQLException{
        PreparedStatement st = null;
        String sql = """
                    delete from account 
                    where username = (select username from account_info where user_id = (?))
                """;
        st = conn.prepareStatement(sql);
        if(id != null && !id.trim().isEmpty())
            st.setLong(1, Long.parseLong(id));

        int rowsAffected = st.executeUpdate();
        if(st != null)
            st.close();
        return rowsAffected > 0;
    }

    public static boolean updateLock(String id, String username) throws SQLException{
        PreparedStatement st = null;
        String sql = """
                    update account
                    set status = (?)
                    where username = (select username from account_info where user_id = (?))
                """;
        st = conn.prepareStatement(sql);
        int paramIndex = 1;
        st.setString(paramIndex++, "L");

        if(id != null && !id.trim().isEmpty())
            st.setLong(paramIndex++, Long.parseLong(id));

        int rowsAffected = st.executeUpdate();
        if(st != null)
            st.close();
        return rowsAffected > 0;
    }

    public static boolean updateInfoUser(String id, String fullname, String address, String email) throws SQLException{
        PreparedStatement st = null;
        StringBuilder sql = new StringBuilder();
        if(id == null || id.isEmpty())
            return false;
        if(fullname != null && !fullname.isEmpty()){
            sql.append("""
                        update user_info set fullname = (?) where id = (?)
                    """);
        }

        if(address != null && !address.isEmpty()){
            sql.append("""
                        update user_info set address = (?) where id = (?)
                    """);
        }

        if(email != null && !email.isEmpty()){
            sql.append("""
                        update account
                        set email = (?)
                        where username = (select username from account_info where user_id = (?))
                    """);
        }
        st = conn.prepareStatement(sql.toString());

        int paramIndex = 1;
        if(fullname != null && !fullname.isEmpty()){
            st.setString(paramIndex++, fullname);
            st.setLong(paramIndex++, Long.parseLong(id));
        }

        
        if(address != null && !address.isEmpty()){
            st.setString(paramIndex++, address);
            st.setLong(paramIndex++, Long.parseLong(id));
        }
    
        if(email != null && !email.isEmpty()){
            st.setString(paramIndex++, email);
            st.setLong(paramIndex++, Long.parseLong(id));
        }
        
        int rowsAffected = st.executeUpdate();
        return rowsAffected > 0;
    }

    private static boolean isUsernameExists(String username) throws SQLException{
        if(username == null || username.trim().isEmpty())
            return false;
        PreparedStatement st = null;
        String sql = "select count(*) from account where username = (?)";
        st = conn.prepareStatement(sql);
        st.setString(1, username);
        ResultSet rs = st.executeQuery();
        if(rs.next()){
            return rs.getInt(1) > 0;
        }
        return false;
    }

    private static long findingIdByUserName(String username) throws SQLException{
        if(username == null || username.trim().isEmpty())
            return -1;
        PreparedStatement st = null;
        String sql = "select user_id from account_info where username = (?)";
        st = conn.prepareStatement(sql);
        st.setString(1, username);
        ResultSet rs = st.executeQuery();
        if(rs.next()){
            return rs.getLong(1) ;
        }
        return -1;
    }

    public static boolean addUserAccount(String username, String password, String fullname, String email, String dob, String role) throws SQLException{
        if(isUsernameExists(username))
            return false;
        // hash password
        String hashPassword = get_SHA_256_SecurePassword(password);
        PreparedStatement st = null;
        StringBuilder sql = new StringBuilder();
        sql.append( """
                    insert into account (username, password, role, email, status) 
                    values (?, ?, ?, ?, 'I');
                """);

        if(role.equals("U")){
            sql.append("""
                        insert into account_info (username)
                        values (?);
                    """);
        }
        st = conn.prepareStatement(sql.toString());
        int paramIndex = 1;
        st.setString(paramIndex++, username);
        st.setString(paramIndex++, hashPassword);
        st.setString(paramIndex++, role);
        st.setString(paramIndex++, email);
        if(role.equals("U")){
            st.setString(paramIndex++, username);
        }
        st.executeUpdate();

        // query 2
        if(role.equals("U")){
            String sql2 = "insert into user_info (id, fullname, dob) values (?, ?, ?)";
            st = conn.prepareStatement(sql2);
            paramIndex = 1;
            st.setLong(paramIndex++, findingIdByUserName(username));
            st.setString(paramIndex++, fullname);
            st.setDate(paramIndex++, Date.valueOf(dob));
            st.execute();
        }
        return true;
    }
}