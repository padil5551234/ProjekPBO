package com.example.uji_coba;

import java.sql.Timestamp;
import java.util.List;

public class Kosan {
    private int id_kos;
    private int id_user;
    private String nama_kos;
    private String alamat;
    private String jenis_kos;
    private int harga;
    private String fitur;
    private String path_gambar;
    private String universitas;
    private String deskripsi;
    private String fasilitas_kamar;
    private Timestamp tanggal_ditambahkan;
    private double rating;
    private String lastComment;
    private String status;

    public Kosan(int id, String nama, String alamat, int jumlah_kamar, String tipe, String status, String foto, int id_user) {
        this.id_kos = id;
        this.nama_kos = nama;
        this.alamat = alamat;
        this.jenis_kos = tipe;
        this.status = status;
        this.path_gambar = foto;
        this.id_user = id_user;
    }

    public Kosan(int id_kos, int id_user, String nama_kos, String alamat, String jenis_kos, int harga, String fitur, String path_gambar, String universitas, String deskripsi, String fasilitas_kamar, Timestamp tanggal_ditambahkan) {
        this.id_kos = id_kos;
        this.id_user = id_user;
        this.nama_kos = nama_kos;
        this.alamat = alamat;
        this.jenis_kos = jenis_kos;
        this.harga = harga;
        this.fitur = fitur;
        this.path_gambar = path_gambar;
        this.universitas = universitas;
        this.deskripsi = deskripsi;
        this.fasilitas_kamar = fasilitas_kamar;
        this.tanggal_ditambahkan = tanggal_ditambahkan;
    }

    // Getters and Setters
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

    public String getNama_kos() {
        return nama_kos;
    }

    public String getNama() {
        return nama_kos;
    }

    public void setNama_kos(String nama_kos) {
        this.nama_kos = nama_kos;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getJenis_kos() {
        return jenis_kos;
    }

    public void setJenis_kos(String jenis_kos) {
        this.jenis_kos = jenis_kos;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public String getFitur() {
        return fitur;
    }

    public void setFitur(String fitur) {
        this.fitur = fitur;
    }

    public String getPath_gambar() {
        return path_gambar;
    }

    public String getFoto() {
        return path_gambar;
    }

    public void setPath_gambar(String path_gambar) {
        this.path_gambar = path_gambar;
    }

    public String getUniversitas() {
        return universitas;
    }

    public void setUniversitas(String universitas) {
        this.universitas = universitas;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getFasilitas_kamar() {
        return fasilitas_kamar;
    }

    public void setFasilitas_kamar(String fasilitas_kamar) {
        this.fasilitas_kamar = fasilitas_kamar;
    }

    public Timestamp getTanggal_ditambahkan() {
        return tanggal_ditambahkan;
    }

    public void setTanggal_ditambahkan(Timestamp tanggal_ditambahkan) {
        this.tanggal_ditambahkan = tanggal_ditambahkan;
    }
    
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getLastComment() {
        return lastComment;
    }

    public void setLastComment(String lastComment) {
        this.lastComment = lastComment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}