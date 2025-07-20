package com.example.uji_coba;

import java.sql.Timestamp;

public class Order {
    private int id_order;
    private int id_kos;
    private int id_user;
    private Timestamp tanggal_sewa;
    private String status;
    private String kosanName;
    private String userName;
    private int durasi; // Add duration field

    public Order(int id_order, int id_kos, int id_user, Timestamp tanggal_sewa, String status) {
        this.id_order = id_order;
        this.id_kos = id_kos;
        this.id_user = id_user;
        this.tanggal_sewa = tanggal_sewa;
        this.status = status;
        this.durasi = 1; // Default duration
    }

    // Constructor with kosanName and userName
    public Order(int id_order, int id_kos, int id_user, Timestamp tanggal_sewa, String status, String kosanName, String userName) {
        this.id_order = id_order;
        this.id_kos = id_kos;
        this.id_user = id_user;
        this.tanggal_sewa = tanggal_sewa;
        this.status = status;
        this.kosanName = kosanName;
        this.userName = userName;
        this.durasi = 1; // Default duration
    }

    // Constructor with duration
    public Order(int id_order, int id_kos, int id_user, Timestamp tanggal_sewa, String status, int durasi) {
        this.id_order = id_order;
        this.id_kos = id_kos;
        this.id_user = id_user;
        this.tanggal_sewa = tanggal_sewa;
        this.status = status;
        this.durasi = durasi;
    }

    // Constructor with all fields including duration
    public Order(int id_order, int id_kos, int id_user, Timestamp tanggal_sewa, String status, String kosanName, String userName, int durasi) {
        this.id_order = id_order;
        this.id_kos = id_kos;
        this.id_user = id_user;
        this.tanggal_sewa = tanggal_sewa;
        this.status = status;
        this.kosanName = kosanName;
        this.userName = userName;
        this.durasi = durasi;
    }

    public int getId_order() {
        return id_order;
    }

    public void setId_order(int id_order) {
        this.id_order = id_order;
    }

    public int getId_kos() {
        return id_kos;
    }

    public void setId_kos(int id_kos) {
        this.id_kos = id_kos;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public Timestamp getTanggal_sewa() {
        return tanggal_sewa;
    }

    public void setTanggal_sewa(Timestamp tanggal_sewa) {
        this.tanggal_sewa = tanggal_sewa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKosanName() {
        return kosanName;
    }

    public void setKosanName(String kosanName) {
        this.kosanName = kosanName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getDurasi() {
        return durasi;
    }

    public void setDurasi(int durasi) {
        this.durasi = durasi;
    }
}