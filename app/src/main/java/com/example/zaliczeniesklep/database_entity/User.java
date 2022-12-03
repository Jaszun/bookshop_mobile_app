package com.example.zaliczeniesklep.database_entity;

public class User extends DatabaseEntity{
    private String email;
    private String password;
    private String phoneNumber;
    private int userType; // 0 - normal | 1 - admin

    public User(){}

    public User(String email, String password, String phoneNumber, int userType) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }

    public User(int id, String email, String password, String phoneNumber, int userType) {
        super(id);
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
