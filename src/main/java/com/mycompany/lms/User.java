package com.mycompany.lms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    protected int id;
    protected String username;
    protected String password; 
    protected String email;
    protected boolean isActive;

    public User(int id, String username, String password, String email, boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.isActive = isActive;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public boolean isActive() { return isActive; }

    public void addUser(String username, String password, String email, String role, boolean isActive) {
        String sql = "INSERT INTO users (username, password, email, role, isActive) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); 
            pstmt.setString(3, email);
            pstmt.setString(4, role);
            pstmt.setBoolean(5, isActive);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public boolean validateLogin(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ? AND isActive = TRUE";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return password.equals(storedPassword); // In real applications, use hashed password comparison
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


    // Additional common methods...
}
