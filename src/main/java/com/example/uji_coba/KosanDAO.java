package com.example.uji_coba;

import com.example.uji_coba.Kosan;
import com.example.uji_coba.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KosanDAO {

    public List<Kosan> getAllKosan() {
        String sql = "SELECT k.*, COALESCE(AVG(u.rating), 0) as rating, " +
                     "(SELECT u.komentar FROM ulasan u WHERE u.id_kos = k.id_kos ORDER BY u.tanggal DESC LIMIT 1) as last_comment, " +
                     "(SELECT COUNT(*) FROM orders o WHERE o.id_kos = k.id_kos AND o.status = 'approved') as is_rented " +
                     "FROM kosan k LEFT JOIN ulasan u ON k.id_kos = u.id_kos GROUP BY k.id_kos";
        List<Kosan> kosanList = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Kosan kosan = mapResultSetToKosan(rs);
                kosan.setRating(rs.getDouble("rating"));
                kosan.setLastComment(rs.getString("last_comment"));
                // Add all kosan - let users see all available listings
                // The rental status can be checked when making orders
                kosanList.add(kosan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kosanList;
    }

    public List<Kosan> filterKosan(String searchQuery, String minPrice, String maxPrice, String university) {
        StringBuilder sql = new StringBuilder("SELECT k.*, COALESCE(AVG(u.rating), 0) as rating, " +
                "(SELECT u.komentar FROM ulasan u WHERE u.id_kos = k.id_kos ORDER BY u.tanggal DESC LIMIT 1) as last_comment, " +
                "(SELECT COUNT(*) FROM orders o WHERE o.id_kos = k.id_kos AND o.status = 'approved') as is_rented " +
                "FROM kosan k LEFT JOIN ulasan u ON k.id_kos = u.id_kos WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql.append(" AND (k.nama_kos LIKE ? OR k.alamat LIKE ?)");
            params.add("%" + searchQuery + "%");
            params.add("%" + searchQuery + "%");
        }

        if (minPrice != null && !minPrice.isEmpty()) {
            try {
                sql.append(" AND k.harga >= ?");
                params.add(Double.parseDouble(minPrice));
            } catch (NumberFormatException e) {
                // Ignore if price is not a valid number
            }
        }

        if (maxPrice != null && !maxPrice.isEmpty()) {
            try {
                sql.append(" AND k.harga <= ?");
                params.add(Double.parseDouble(maxPrice));
            } catch (NumberFormatException e) {
                // Ignore if price is not a valid number
            }
        }

        if (university != null && !university.isEmpty()) {
            sql.append(" AND k.universitas LIKE ?");
            params.add("%" + university + "%");
        }

        sql.append(" GROUP BY k.id_kos");

        List<Kosan> kosanList = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                Kosan kosan = mapResultSetToKosan(rs);
                kosan.setRating(rs.getDouble("rating"));
                kosan.setLastComment(rs.getString("last_comment"));
                // Add all kosan - let users see all available listings
                // The rental status can be checked when making orders
                kosanList.add(kosan);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kosanList;
    }

    private Kosan mapResultSetToKosan(ResultSet rs) throws SQLException {
        return new Kosan(
                rs.getInt("id_kos"),
                rs.getInt("id_user"),
                rs.getString("nama_kos"),
                rs.getString("alamat"),
                rs.getString("jenis_kos"),
                rs.getInt("harga"),
                rs.getString("fitur"),
                rs.getString("path_gambar"),
                rs.getString("universitas"),
                rs.getString("deskripsi"),
                rs.getString("fasilitas_kamar"),
                rs.getTimestamp("tanggal_ditambahkan")
        );
    }
    public Kosan getKosanById(int kosanId) {
        String sql = "SELECT * FROM kosan WHERE id_kos = ?";
        Kosan kosan = null;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, kosanId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                kosan = new Kosan(
                        rs.getInt("id_kos"),
                        rs.getInt("id_user"),
                        rs.getString("nama_kos"),
                        rs.getString("alamat"),
                        rs.getString("jenis_kos"),
                        rs.getInt("harga"),
                        rs.getString("fitur"),
                        rs.getString("path_gambar"),
                        rs.getString("universitas"),
                        rs.getString("deskripsi"),
                        rs.getString("fasilitas_kamar"),
                        rs.getTimestamp("tanggal_ditambahkan")
                );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kosan;
    }
public boolean addKos(int sellerId, String nama, String alamat, String jenis, int harga, String fiturJson, String imagePathsString, String deskripsi, String universitas, String fasilitasKamarJson) throws SQLException {
        String sql = "INSERT INTO kosan (id_user, nama_kos, alamat, jenis_kos, harga, fitur, path_gambar, deskripsi, universitas, fasilitas_kamar) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sellerId);
            pstmt.setString(2, nama);
            pstmt.setString(3, alamat);
            pstmt.setString(4, jenis);
            pstmt.setInt(5, harga);
            pstmt.setString(6, fiturJson);
            pstmt.setString(7, imagePathsString);
            pstmt.setString(8, deskripsi);
            pstmt.setString(9, universitas);
            pstmt.setString(10, fasilitasKamarJson);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}