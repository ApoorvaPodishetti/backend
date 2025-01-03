package com.Hashing.demo.model;

public class UserRegistrationRequest {
    private Users user;
    private String role;

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserRegistrationRequest{" +
                "user=" + user +
                ", role='" + role + '\'' +
                '}';
    }
}
