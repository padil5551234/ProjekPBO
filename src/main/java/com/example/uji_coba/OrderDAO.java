package com.example.uji_coba;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public boolean createOrder(Order order) {
        String sql = "INSERT INTO orders (id_kos, id_user, tanggal_sewa, status, durasi) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getId_kos());
            pstmt.setInt(2, order.getId_user());
            pstmt.setTimestamp(3, order.getTanggal_sewa());
            pstmt.setString(4, order.getStatus());
            pstmt.setInt(5, order.getDurasi());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Order> getOrdersBySellerId(int sellerId) {
        String sql = "SELECT o.*, k.nama_kos, u.username FROM orders o " +
                "JOIN kosan k ON o.id_kos = k.id_kos " +
                "JOIN users u ON o.id_user = u.id_user " +
                "WHERE k.id_user = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sellerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(createOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> getOrdersByUserIdWithKosan(int userId) {
        String sql = "SELECT o.*, k.nama_kos, u.username FROM orders o " +
                "JOIN kosan k ON o.id_kos = k.id_kos " +
                "JOIN users u ON o.id_user = u.id_user " +
                "WHERE o.id_user = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(createOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id_order = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasActiveOrder(int kosanId, int userId) {
        String sql = "SELECT COUNT(*) FROM orders WHERE id_kos = ? AND id_user = ? AND status = 'pending'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, kosanId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isKosanAvailable(int kosanId) {
        String sql = "SELECT COUNT(*) FROM orders WHERE id_kos = ? AND status = 'approved'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, kosanId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Available if no accepted orders
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true; // Default to available if error
    }

    public boolean updateOrderStatusWithSewa(int orderId, String status) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Update order status
            String updateOrderSql = "UPDATE orders SET status = ? WHERE id_order = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateOrderSql)) {
                pstmt.setString(1, status);
                pstmt.setInt(2, orderId);
                pstmt.executeUpdate();
            }
            
            // If approved, create sewa record
            if ("approved".equals(status)) {
                String getOrderSql = "SELECT id_kos, id_user, tanggal_sewa FROM orders WHERE id_order = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(getOrderSql)) {
                    pstmt.setInt(1, orderId);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        int kosId = rs.getInt("id_kos");
                        int userId = rs.getInt("id_user");
                        Timestamp tanggalSewa = rs.getTimestamp("tanggal_sewa");
                        
                        // Insert into sewa table
                        String insertSewaSql = "INSERT INTO sewa (id_kos, id_user, tanggal_mulai, durasi_sewa, status) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement sewaPstmt = conn.prepareStatement(insertSewaSql)) {
                            sewaPstmt.setInt(1, kosId);
                            sewaPstmt.setInt(2, userId);
                            sewaPstmt.setDate(3, new java.sql.Date(tanggalSewa.getTime()));
                            sewaPstmt.setInt(4, 1); // Default 1 month
                            sewaPstmt.setString(5, "active");
                            sewaPstmt.executeUpdate();
                        }
                    }
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Order> getOrdersByKosanId(int kosanId) {
        String sql = "SELECT o.*, k.nama_kos, u.username FROM orders o " +
                "JOIN kosan k ON o.id_kos = k.id_kos " +
                "JOIN users u ON o.id_user = u.id_user " +
                "WHERE o.id_kos = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, kosanId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(createOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE id_order = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Order createOrderFromResultSet(ResultSet rs) throws SQLException {
        int durasi = 1; // Default value
        try {
            durasi = rs.getInt("durasi");
        } catch (SQLException e) {
            // Column might not exist in older database schema, use default
        }
        
        return new Order(
                rs.getInt("id_order"),
                rs.getInt("id_kos"),
                rs.getInt("id_user"),
                rs.getTimestamp("tanggal_sewa"),
                rs.getString("status"),
                rs.getString("nama_kos"),
                rs.getString("username"),
                durasi
        );
    }
}