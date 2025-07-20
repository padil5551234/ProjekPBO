package com.example.uji_coba;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {
    public static void main(String[] args) {
        createUlasanTable();
        addDurasiColumnToOrders();
    }
    
    public static void createUlasanTable() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS ulasan (
                id INT AUTO_INCREMENT PRIMARY KEY,
                id_kos INT NOT NULL,
                id_user INT NOT NULL,
                rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
                komentar TEXT,
                tanggal_ulasan TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_kos) REFERENCES kosan(id_kos) ON DELETE CASCADE,
                FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE
            )
            """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table 'ulasan' created successfully or already exists.");
            
        } catch (SQLException e) {
            System.err.println("Error creating ulasan table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void addDurasiColumnToOrders() {
        String addColumnSQL = "ALTER TABLE orders ADD COLUMN IF NOT EXISTS durasi INT DEFAULT 1";
        String updateExistingSQL = "UPDATE orders SET durasi = 1 WHERE durasi IS NULL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Add the column if it doesn't exist
            stmt.executeUpdate(addColumnSQL);
            System.out.println("Column 'durasi' added to orders table successfully or already exists.");
            
            // Update existing records
            stmt.executeUpdate(updateExistingSQL);
            System.out.println("Existing orders updated with default duration.");
            
        } catch (SQLException e) {
            System.err.println("Error adding durasi column to orders table: " + e.getMessage());
            e.printStackTrace();
        }
    }
}