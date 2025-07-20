package com.example.uji_coba;

public enum View {
    LOGIN("login2.fxml"),
    REGISTRATION("registrasi.fxml"),
    ADMIN_DASHBOARD("AdminDashboard.fxml"),
    USER_DASHBOARD("UserDashboard.fxml"),
    SELLER_DASHBOARD("DashboardSeller.fxml"),
    ADD_KOS("AddKos.fxml"),
    KOSAN_DETAIL("KosanDetail.fxml"),
    ORDER_KOS("OrderKos.fxml"),
    SEWA_KOSAN("SewaKosan.fxml"),
    USER_PROFILE("UserProfile.fxml"),
    KOSAN_CARD("KosanCard.fxml"),
    ORDER_CARD("OrderCard.fxml"),
    ORDER_VIEW("OrderView.fxml"),
    ULASAN("Ulasan.fxml");

    private final String fileName;

    View(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}