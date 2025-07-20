package com.example.uji_coba;

import com.example.uji_coba.Ulasan;
import com.example.uji_coba.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UlasanDAO {

    public List<Ulasan> getUlasanByKosId(int kosId) {
        String sql = "SELECT * FROM ulasan WHERE id_kos = ?";
        List<Ulasan> ulasanList = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, kosId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ulasanList.add(mapResultSetToUlasan(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ulasanList;
    }

    public boolean addUlasan(Ulasan ulasan) {
        String sql = "INSERT INTO ulasan (id_kos, id_user, rating, komentar) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ulasan.getId_kos());
            pstmt.setInt(2, ulasan.getId_user());
            pstmt.setInt(3, ulasan.getRating());
            pstmt.setString(4, ulasan.getKomentar());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Ulasan mapResultSetToUlasan(ResultSet rs) throws SQLException {
        return new Ulasan(
                rs.getInt("id_ulasan"),
                rs.getInt("id_kos"),
                rs.getInt("id_user"),
                rs.getInt("rating"),
                rs.getString("komentar"),
                rs.getTimestamp("tanggal")
        );
    }
}