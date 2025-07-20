package com.example.uji_coba;

import com.example.uji_coba.User;
import com.example.uji_coba.KosanDAO;
import com.example.uji_coba.SessionManager;
import com.example.uji_coba.Navigatable;
import com.example.uji_coba.NavigationService;
import com.example.uji_coba.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddKosController implements Navigatable {

    @FXML
    private TextField namaKosField;
    @FXML
    private TextArea alamatKosArea;
    @FXML
    private ToggleGroup jenisKosGroup;
    @FXML
    private FlowPane imagePreviewContainer;
    @FXML
    private CheckBox acCheckBox;
    @FXML
    private CheckBox kipasAnginCheckBox;
    @FXML
    private CheckBox laundryCheckBox;
    @FXML
    private CheckBox dapurCheckBox;
    @FXML
    private CheckBox kamarMandiCheckBox;
    @FXML
    private CheckBox parkirMotorCheckBox;
    @FXML
    private CheckBox wifiCheckBox;
    @FXML
    private CheckBox mejaBelajarCheckBox;
    @FXML
    private TextArea deskripsiArea;
    @FXML
    private TextField universitasField;
    @FXML
    private TextField hargaKosField;
    @FXML
    private Button simpanButton;

    private List<String> selectedImagePaths = new ArrayList<>();
    private NavigationService navigationService;
    private KosanDAO kosanDAO;
    private Runnable onSaveSuccessCallback;

    public void setOnSaveSuccessCallback(Runnable onSaveSuccessCallback) {
        this.onSaveSuccessCallback = onSaveSuccessCallback;
    }

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
        this.kosanDAO = new KosanDAO();
    }

    @FXML
    private void handleTambahGambar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Kosan");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(simpanButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                Path imageDir = Paths.get("images");
                if (!Files.exists(imageDir)) {
                    Files.createDirectories(imageDir);
                }
                String uniqueFileName = UUID.randomUUID().toString() + "_" + selectedFile.getName();
                Path destinationPath = imageDir.resolve(uniqueFileName);
                Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                selectedImagePaths.add(destinationPath.toString());
                ImageView imageView = new ImageView(new Image(destinationPath.toUri().toString()));
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                imageView.setPreserveRatio(true);
                imagePreviewContainer.getChildren().add(imageView);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Gagal menyimpan gambar.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleSimpan() {
        String nama = namaKosField.getText().trim();
        String alamat = alamatKosArea.getText().trim();
        String deskripsi = deskripsiArea.getText().trim();
        String universitas = universitasField.getText().trim();
        RadioButton selectedRadio = (RadioButton) jenisKosGroup.getSelectedToggle();
        String jenis = selectedRadio != null ? selectedRadio.getText() : "";
        String hargaStr = hargaKosField.getText().trim();

        if (nama.isEmpty() || alamat.isEmpty() || jenis.isEmpty() || hargaStr.isEmpty() || selectedImagePaths.isEmpty() || deskripsi.isEmpty()) {
            showAlert("Input Tidak Lengkap", "Harap isi semua field, termasuk deskripsi, dan pilih setidaknya satu gambar.", Alert.AlertType.WARNING);
            return;
        }

        int harga;
        try {
            harga = Integer.parseInt(hargaStr);
        } catch (NumberFormatException e) {
            showAlert("Format Harga Salah", "Harga harus berupa angka.", Alert.AlertType.ERROR);
            return;
        }

        List<String> features = new ArrayList<>();
        if (acCheckBox.isSelected()) features.add("AC");
        if (kipasAnginCheckBox.isSelected()) features.add("Kipas Angin");
        if (laundryCheckBox.isSelected()) features.add("Laundry");
        if (dapurCheckBox.isSelected()) features.add("Dapur");
        if (kamarMandiCheckBox.isSelected()) features.add("Kamar Mandi Dalam");
        if (parkirMotorCheckBox.isSelected()) features.add("Parkir Motor");
        if (wifiCheckBox.isSelected()) features.add("Wifi");
        if (mejaBelajarCheckBox.isSelected()) features.add("Meja Belajar");
        String fiturJson = "[\"" + String.join("\", \"", features) + "\"]";

        List<String> fasilitasKamar = new ArrayList<>();
        if (acCheckBox.isSelected()) fasilitasKamar.add("AC");
        if (kipasAnginCheckBox.isSelected()) fasilitasKamar.add("Kipas Angin");
        if (kamarMandiCheckBox.isSelected()) fasilitasKamar.add("Kamar Mandi Dalam");
        if (mejaBelajarCheckBox.isSelected()) fasilitasKamar.add("Meja Belajar");
        String fasilitasKamarJson = "[\"" + String.join("\", \"", fasilitasKamar) + "\"]";


        User seller = SessionManager.getInstance().getCurrentUser();
        if (seller == null) {
            showAlert("Error", "Tidak ada user yang login. Silakan login kembali.", Alert.AlertType.ERROR);
            return;
        }
        int sellerId = seller.getId();
        String imagePathsString = String.join(",", selectedImagePaths);

        try {
            boolean isSuccess = kosanDAO.addKos(sellerId, nama, alamat, jenis, harga, fiturJson, imagePathsString, deskripsi, universitas, fasilitasKamarJson);
            if (isSuccess) {
                showAlert("Sukses", "Data kosan berhasil disimpan!", Alert.AlertType.INFORMATION);
                clearForm();
                if (onSaveSuccessCallback != null) {
                    onSaveSuccessCallback.run();
                }
            } else {
                showAlert("Gagal", "Gagal menyimpan data kosan ke database.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Error", "Terjadi kesalahan: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void clearForm() {
        namaKosField.clear();
        alamatKosArea.clear();
        deskripsiArea.clear();
        universitasField.clear();
        if (jenisKosGroup.getSelectedToggle() != null) {
            jenisKosGroup.getSelectedToggle().setSelected(false);
        }
        acCheckBox.setSelected(false);
        kipasAnginCheckBox.setSelected(false);
        laundryCheckBox.setSelected(false);
        dapurCheckBox.setSelected(false);
        kamarMandiCheckBox.setSelected(false);
        parkirMotorCheckBox.setSelected(false);
        wifiCheckBox.setSelected(false);
        mejaBelajarCheckBox.setSelected(false);
        hargaKosField.clear();
        selectedImagePaths.clear();
        imagePreviewContainer.getChildren().clear();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleDashboardButton(ActionEvent event) throws IOException {
        navigationService.switchScene(View.SELLER_DASHBOARD);
    }

    @FXML
    private void handleTambahKosButton(ActionEvent event) throws IOException {
        // Already on this page
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) throws IOException {
        SessionManager.getInstance().clearSession();
        navigationService.switchScene(View.LOGIN);
    }

    @FXML
    private void handleBackClick() throws IOException {
        navigationService.switchScene(View.SELLER_DASHBOARD);
    }

    @FXML
    private void handleOrderKosanClick(ActionEvent event) throws IOException {
        navigationService.switchScene(View.ORDER_KOS);
    }
}