package com.example.uji_coba;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Utility class to create or restore admin account for the system.
 * This class provides methods to add an admin user to the database.
 */
public class AdminAccountSetup {
    
    // Default admin credentials
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    private static final String DEFAULT_ADMIN_PHONE = "08123456789";
    
    public static void main(String[] args) {
        System.out.println("=== Admin Account Setup ===");
        
        // Check if admin already exists
        if (adminExists()) {
            System.out.println("Admin account already exists!");
            System.out.println("Username: " + DEFAULT_ADMIN_USERNAME);
            return;
        }
        
        // Create new admin account
        if (createAdminAccount()) {
            System.out.println("Admin account created successfully!");
            System.out.println("Username: " + DEFAULT_ADMIN_USERNAME);
            System.out.println("Password: " + DEFAULT_ADMIN_PASSWORD);
            System.out.println("Phone: " + DEFAULT_ADMIN_PHONE);
            System.out.println("\nPlease change the default password after first login for security.");
        } else {
            System.out.println("Failed to create admin account!");
        }
    }
    
    /**
     * Check if an admin account already exists in the database
     * @return true if admin exists, false otherwise
     */
    public static boolean adminExists() {
        String query = "SELECT COUNT(*) FROM users WHERE role = 'admin'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking admin existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Create a new admin account with default credentials
     * @return true if account created successfully, false otherwise
     */
    public static boolean createAdminAccount() {
        return createAdminAccount(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD, DEFAULT_ADMIN_PHONE);
    }
    
    /**
     * Create a new admin account with custom credentials
     * @param username Admin username
     * @param password Admin password (will be hashed)
     * @param phone Admin phone number
     * @return true if account created successfully, false otherwise
     */
    public static boolean createAdminAccount(String username, String password, String phone) {
        // Check if username already exists
        if (usernameExists(username)) {
            System.out.println("Username '" + username + "' already exists!");
            return false;
        }
        
        String hashedPassword = PasswordUtil.hashPassword(password);
        String insertQuery = "INSERT INTO users (username, password, phone, role, created_at) VALUES (?, ?, ?, 'admin', ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, phone);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating admin account: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Check if a username already exists in the database
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    private static boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Reset admin password to default
     * @param username Admin username
     * @return true if password reset successfully, false otherwise
     */
    public static boolean resetAdminPassword(String username) {
        return resetAdminPassword(username, DEFAULT_ADMIN_PASSWORD);
    }
    
    /**
     * Reset admin password to specified password
     * @param username Admin username
     * @param newPassword New password
     * @return true if password reset successfully, false otherwise
     */
    public static boolean resetAdminPassword(String username, String newPassword) {
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        String updateQuery = "UPDATE users SET password = ? WHERE username = ? AND role = 'admin'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            
            stmt.setString(1, hashedPassword);
            stmt.setString(2, username);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Admin password reset successfully for user: " + username);
                return true;
            } else {
                System.out.println("No admin user found with username: " + username);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error resetting admin password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * List all admin accounts in the system
     */
    public static void listAdminAccounts() {
        String query = "SELECT id_user, username, phone, created_at FROM users WHERE role = 'admin'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("=== Admin Accounts ===");
            boolean hasAdmins = false;
            while (rs.next()) {
                hasAdmins = true;
                System.out.println("ID: " + rs.getInt("id_user"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("Created: " + rs.getTimestamp("created_at"));
                System.out.println("---");
            }
            
            if (!hasAdmins) {
                System.out.println("No admin accounts found!");
            }
            
        } catch (SQLException e) {
            System.err.println("Error listing admin accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }
}