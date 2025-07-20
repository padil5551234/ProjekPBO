package com.example.uji_coba;

import com.example.uji_coba.DatabaseUtil;
import com.example.uji_coba.PasswordUtil;
import com.example.uji_coba.Navigatable;
import com.example.uji_coba.NavigationService;
import com.example.uji_coba.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;

public class RegistrasiController implements Navigatable {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button signUpAsSellerButton;

    @FXML
    private Button signUpAsUserButton;

    @FXML
    private Hyperlink kembaliButton;

    private NavigationService navigationService;

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    private void registerUser(String role) {
        String username = usernameField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Tidak Cocok", "Password dan konfirmasi tidak sama.");
            return;
        }

        if (username.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Semua field wajib diisi.");
            return;
        }

        if (!phone.matches("\\d{10,15}")) {
            showAlert(Alert.AlertType.ERROR, "Format Nomor Salah", "Gunakan angka saja (10–15 digit).");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Cek apakah username sudah ada
            String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.ERROR, "Username Sudah Ada", "Silakan gunakan username lain.");
                return;
            }

            // Insert user baru
            String sql = "INSERT INTO users (username, password, phone, role) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, PasswordUtil.hashPassword(password)); // HASHED password
            stmt.setString(3, phone);
            stmt.setString(4, role);
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Registrasi Berhasil", "Akun " + role + " berhasil dibuat!");

            // Kembali ke halaman login
            navigationService.switchScene(View.LOGIN);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "FXML Error", e.getMessage());
        }
    }


    @FXML
    private void handleSignUpAsSeller() {
        registerUser("seller");
    }

    @FXML
    private void handleSignUpAsUser() {
        registerUser("user");
    }

    @FXML
    private void handleCancelRegistrasi(ActionEvent event) throws IOException {
        navigationService.switchScene(View.LOGIN);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}