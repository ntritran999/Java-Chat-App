package user.models;
// https://stackoverflow.com/questions/33085493/how-to-hash-a-password-with-sha-512-in-java/33085670#33085670
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import admin.models.dbConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignupModel {
    private String fullName;
    private String email;
    private String gender;
    private String dob;
    private String address;
    private String username;
    private String password;

    private static Connection conn = dbConnection.getConnection();

    public SignupModel(String fullName, String email, String gender, String dob, String address, String username, String password){
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.username = username;
        this.password = password;
    }

    public String getFullName(){
        return fullName;
    }

    public String getEmail(){
        return email;
    }

    public String getGender(){
        return gender;
    }

    public String dob(){
        return dob;
    }

    public String getAddress(){
        return address;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
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
    private long findingIdByUserName(String username) throws SQLException{
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

    public int signUpAccount() throws SQLException{
        if(username == null || username.isEmpty() || password == null 
            || password.isEmpty())
            return -1;

        PreparedStatement st = null;
        String sql1 = "select username from account where username = ?";
        st = conn.prepareStatement(sql1);
        int paramIndex = 1;
        st.setString(paramIndex++, username);
        ResultSet rs = st.executeQuery();
        String existsUsername = null;
        while (rs.next()) {
            existsUsername = rs.getString("username");
        }
        if(existsUsername == null){
            paramIndex = 1;
            String hashPassword = get_SHA_256_SecurePassword(password);
            String sql2 = """
                        insert into account (username, password, role, email, status)
                        values (?, ?, 'U', ?, 'I');

                        insert into account_info (username)
                        values (?);
                    """;
            st = conn.prepareStatement(sql2);
            st.setString(paramIndex++, username);
            st.setString(paramIndex++, hashPassword);
            st.setString(paramIndex++, email);
            st.setString(paramIndex++, username);
            
            int rowsAffected = st.executeUpdate();
            if(rowsAffected > 0){
                paramIndex = 1;
                long id = findingIdByUserName(username);
                if(id == -1)
                    return -1;
                
                String sql3 = """
                            insert into user_info (id, fullname, address, gender, dob)
                            values (?, ?, ?, ?, ?)
                        """;
                st = conn.prepareStatement(sql3);
                st.setLong(paramIndex++, id);
                st.setString(paramIndex++, fullName);
                st.setString(paramIndex++, address);
                st.setString(paramIndex++, gender);
                st.setDate(paramIndex++, Date.valueOf(dob));

                int finalSuccess = st.executeUpdate();
                if(finalSuccess > 0)
                    return 1;
                return -2;
            }
        }
        return 0;
        
    }
}
