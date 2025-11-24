package user.models;
// https://stackoverflow.com/questions/33085493/how-to-hash-a-password-with-sha-512-in-java/33085670#33085670
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;


public class LoginPageModel {
    private String username;
    private String password;
    private String email;
    private int authen; // 0(loi hoac ko co account), 1(user), 2(admin) 

    private Connection conn;

    public LoginPageModel(Connection conn, String username, String password){
        this.username = username;
        this.password = password; 
        this.conn = conn;
    }

    public LoginPageModel(Connection conn){
        this.conn = conn;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public int getAuthen(){
        return authen;
    }

    public String getEmail(){
        return email;
    }

    public void setUserName(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setAuthen(int authen){
        this.authen = authen;
    }

    // hash and create random password fucntions
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

    public static String generatePassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                     + "abcdefghijklmnopqrstuvwxyz"
                     + "0123456789";

        Random rand = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }

        return sb.toString();
    }

    // fetching api
    public Integer authenticateAccount() throws SQLException{
        if(username == null || username.trim().isEmpty() || password == null 
            || password.trim().isEmpty())
            return 0;
        String hassPassword = get_SHA_256_SecurePassword(password);
        PreparedStatement st = null;
        String sql1 = "select password from account where username = ?";
        st = conn.prepareStatement(sql1);
        st.setString(1, username);
        ResultSet rs = st.executeQuery();
        String passwordReal = null;
        while (rs.next()) {
            passwordReal = rs.getString("password");
        }
        if(passwordReal != null){
            if(!hassPassword.equals(passwordReal))
                return 0;
            
            String sql2 = "select role from account where username =  ?";
            st = conn.prepareStatement(sql2);
            st.setString(1, username);
            rs = st.executeQuery();
            String r = null;
            while (rs.next()) {
                r = rs.getString("role");
            }
            if(r == null)
                return 0;
            else if(r.equals("U"))
                return 1;
            else if(r.equals("A")){
                return 2;
            }
        }

        return 0;
    }

    public boolean checkingExistsResetAccountPassword() throws SQLException{
        PreparedStatement st = null;
        String sql = "select username, email from account where username = ?";
        st = conn.prepareStatement(sql);
        st.setString(1, username);
        ResultSet rs = st.executeQuery();
        String usernameDb = null;
        while(rs.next()){
            usernameDb = rs.getString(1);
            email = rs.getString(2);
        }
        if(usernameDb != null){
            password = generatePassword(9);
            String sql2 = "update account set password = (?) where username = ?";
            st = conn.prepareStatement(sql2);
            st.setString(1, get_SHA_256_SecurePassword(password));
            st.setString(2, usernameDb);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        }
        return false;
    }
}
