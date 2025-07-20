package com.example.uji_coba;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseSchemaChecker {
    public static void main(String[] args) {
        checkDatabaseSchema();
    }
    
    public static void checkDatabaseSchema() {
        try (Connection conn = DatabaseUtil.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            System.out.println("=== DATABASE TABLES ===");
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("Table: " + tableName);
                
                // Get columns for each table
                ResultSet columns = metaData.getColumns(null, null, tableName, "%");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    System.out.println("  - " + columnName + " (" + columnType + ")");
                }
                System.out.println();
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}