package com.example.uji_coba;

import java.sql.Timestamp;

public class Ulasan {
    private int id_ulasan;
    private int id_kos;
    private int id_user;
    private int rating;
    private String komentar;
    private Timestamp tanggal;

    public Ulasan(int id_ulasan, int id_kos, int id_user, int rating, String komentar, Timestamp tanggal) {
        this.id_ulasan = id_ulasan;
        this.id_kos = id_kos;
        this.id_user = id_user;
        this.rating = rating;
        this.komentar = komentar;
        this.tanggal = tanggal;
    }

    // Getters and Setters
    public int getId_ulasan() {
        return id_ulasan;
    }

    public void setId_ulasan(int id_ulasan) {
        this.id_ulasan = id_ulasan;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public Timestamp getTanggal() {
        return tanggal;
    }

    public void setTanggal(Timestamp tanggal) {
        this.tanggal = tanggal;
    }
}