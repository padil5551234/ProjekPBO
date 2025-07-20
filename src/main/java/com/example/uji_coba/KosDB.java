package com.example.uji_coba;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import com.example.uji_coba.DatabaseUtil;

public class KosDB {


    // Saran: Ganti nama file dan kelas ini menjadi "Kosan" agar lebih sesuai.
    // Kelas ini sekarang berfungsi sebagai Model (POJO) dan juga memiliki metode utilitas untuk DB.

    private String name;
    private String address;
    private String type; // "Pria", "Wanita", "Campur"
    private List<String> features;
    private double pricePerMonth;
    private List<String> imageUrls;
    private String sellerId;

    public KosDB(String name, String address, String type, List<String> features, double pricePerMonth, List<String> imageUrls, String sellerId) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.features = features;
        this.pricePerMonth = pricePerMonth;
        this.imageUrls = imageUrls;
        this.sellerId = sellerId;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getType() {
        return type;
    }

    public List<String> getFeatures() {
        return features;
    }

    public double getPricePerMonth() {
        return pricePerMonth;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public String getSellerId() {
        return sellerId;
    }
    // Metode statis untuk menambahkan data kosan baru ke database.
    // Melempar SQLException agar bisa ditangani oleh Controller.
    public static boolean addKos(int sellerId, String nama, String alamat, String jenis, double harga, String fiturJson, String imagePathsString) throws SQLException {
        String sql = "INSERT INTO kosan (id_user, nama_kos, alamat, jenis_kos, harga, fitur, path_gambar, tanggal_ditambahkan) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sellerId);
            pstmt.setString(2, nama);
            pstmt.setString(3, alamat);
            pstmt.setString(4, jenis);
            pstmt.setDouble(5, harga);
            pstmt.setString(6, fiturJson);
            pstmt.setString(7, imagePathsString);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}