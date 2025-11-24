package user.models;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UpdateInfoDialogModel {
    private String fullname;
    private String gender;
    private String dob;
    private String address;
    private String email;
    private String newPassword;
    private String username;

    private Connection conn;

    public UpdateInfoDialogModel(Connection conn, String fullname, String gender, String dob, String address, String email, String newPassword, String username){
        this.fullname = fullname;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.email = email;
        this.newPassword = newPassword;
        this.username = username;
        this.conn = conn;
    }

    public UpdateInfoDialogModel(Connection conn, String newPassword, String username){
        this.newPassword = newPassword;
        this.username = username;
        this.conn = conn;
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

    // fetching api
    public boolean updateInfoToDb() throws SQLException{
        if(username == null || username.isEmpty())
            return false;
        PreparedStatement st = null;
        String sql1;
        StringBuilder sql2 = new StringBuilder();
        List<Object> params = new ArrayList<>();
        boolean hasUpdate = false;
        int rowAffected1, rowAffected2;
        rowAffected1 = rowAffected2 = -1;
        if(email != null && !email.isEmpty()){
            sql1 = "update account set email = (?) where username = (?)";
            st = conn.prepareStatement(sql1);
            st.setString(1, email);
            st.setString(2, username);
            rowAffected1 = st.executeUpdate();
        }


        sql2.append("update user_info set ");
        if(fullname != null && !fullname.isEmpty()){
            sql2.append("fullname = (?), ");
            hasUpdate = true;
            params.add(fullname);
        }

        if(gender != null && !gender.isEmpty()){
            sql2.append("gender = (?), ");
            hasUpdate = true;
            params.add(gender);
        }

        if(dob != null && !dob.isEmpty()){
            sql2.append("dob = (?), ");
            hasUpdate = true;
            params.add(Date.valueOf(dob));
        }

        if(address != null && !address.isEmpty()){
            sql2.append("address = (?), ");
            hasUpdate = true;
            params.add(address);
        }

        if(!hasUpdate) return false;
        sql2.setLength(sql2.length() - 2); // bo ,
        sql2.append( " where id = (select user_id from account_info where username = (?))");
        params.add(username);
        st = conn.prepareStatement(sql2.toString());
        for(int i = 0; i < params.size(); ++i){
            Object p = params.get(i);
            st.setObject(i + 1, p);    
        }

        rowAffected2 = st.executeUpdate();

        return (rowAffected1 > 0) || (rowAffected2 > 0); 
    }

    public int updatePasswordToDb() throws SQLException{
        if(username == null || username.isEmpty() || newPassword == null || newPassword.isEmpty())
            return 0;
        PreparedStatement st = null;
        String hassPassword = get_SHA_256_SecurePassword(newPassword);
        String sql = "update account set password = (?) where username = (?)";
        st = conn.prepareStatement(sql);
        st.setString(1, hassPassword);
        st.setString(2, username);
        int rowAffected = st.executeUpdate();
        if(rowAffected > 0)
            return 1;
        return 0;
    }

}
