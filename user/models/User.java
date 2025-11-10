package user.models;

import java.util.Date;

public class User {
    private String usename, password, fullName, address, gender, email;
    private Date birthDay;
    
    public String getUsename() {
        return usename;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setUsename(String usename) {
        this.usename = usename;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }
}
