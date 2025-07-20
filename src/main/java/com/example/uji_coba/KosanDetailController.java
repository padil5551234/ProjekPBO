package com.example.uji_coba;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class KosanDetailController implements Navigatable {

    @FXML
    private Label namaKosanLabel;
    @FXML
    private FlowPane imageContainer;
    @FXML
    private Label deskripsiLabel;
    @FXML
    private Label alamatLabel;
    @FXML
    private Label hargaLabel;
    @FXML
    private Label jenisKosLabel;
    @FXML
    private Label fasilitasKamarLabel;
    @FXML
    private Label fiturLabel;
    @FXML
    private Label universitasLabel;
    @FXML
    private Button orderButton;

    private Kosan kosan;
    private NavigationService navigationService;

    public void setKosan(Kosan kosan) {
        this.kosan = kosan;
        updateView();
    }

    private void updateView() {
        namaKosanLabel.setText(kosan.getNama_kos());
        deskripsiLabel.setText(kosan.getDeskripsi());
        alamatLabel.setText(kosan.getAlamat());
        hargaLabel.setText("Rp " + kosan.getHarga() + " / bulan");
        jenisKosLabel.setText(kosan.getJenis_kos());
        fasilitasKamarLabel.setText(formatJsonList(kosan.getFasilitas_kamar()));
        fiturLabel.setText(formatJsonList(kosan.getFitur()));
        universitasLabel.setText(kosan.getUniversitas());

        imageContainer.getChildren().clear();
        if (kosan.getPath_gambar() != null && !kosan.getPath_gambar().isEmpty()) {
            List<String> imagePaths = Arrays.asList(kosan.getPath_gambar().split(","));
            for (String imagePath : imagePaths) {
                File file = new File(imagePath.trim());
                if (file.exists()) {
                    ImageView imageView = new ImageView(new Image(file.toURI().toString()));
                    imageView.setFitHeight(180);
                    imageView.setFitWidth(180);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);
                    
                    // Add styling to image
                    imageView.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.5, 0, 2);");
                    
                    // Wrap in a container for better styling
                    javafx.scene.layout.StackPane imageContainer = new javafx.scene.layout.StackPane();
                    imageContainer.getChildren().add(imageView);
                    imageContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0.5, 0, 2);");
                    
                    this.imageContainer.getChildren().add(imageContainer);
                }
            }
        }
        
        // If no images, show placeholder
        if (imageContainer.getChildren().isEmpty()) {
            javafx.scene.control.Label noImageLabel = new javafx.scene.control.Label("📷 Tidak ada foto tersedia");
            noImageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888; -fx-padding: 40;");
            imageContainer.getChildren().add(noImageLabel);
        }
    }

    private String formatJsonList(String jsonArray) {
        if (jsonArray == null || jsonArray.isEmpty() || "[]".equals(jsonArray)) {
            return "Tidak ada";
        }
        return jsonArray.replace("[", "").replace("]", "").replace("\"", "");
    }

    @FXML
    private void handleOrderClick() throws IOException {
        navigationService.switchScene(View.ORDER_KOS, kosan);
    }

    @FXML
    private void handleBackClick() throws IOException {
        navigationService.switchScene(View.USER_DASHBOARD);
    }

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }
}