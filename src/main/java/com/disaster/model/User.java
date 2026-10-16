package com.disaster.model;

public class User {

    private int userId;
    private String name;
    private String username;
    private String password;
    private String role;
    private Integer victimId;

    public User() {
    }

    public User(int userId, String name, String username,
                String password, String role) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(int userId, String name, String username,
                String password, String role, Integer victimId) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.victimId = victimId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getVictimId() {
        return victimId;
    }

    public void setVictimId(Integer victimId) {
        this.victimId = victimId;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", victimId=" + victimId +
                '}';
    }
}