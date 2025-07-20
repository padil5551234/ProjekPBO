package com.example.uji_coba;

import com.example.uji_coba.Kosan;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import javafx.scene.control.Button;

public class KosanCardController {

    @FXML
    private AnchorPane cardPane;

    @FXML
    private ImageView kosanImage;

    @FXML
    private Label namaKosLabel;

    @FXML
    private Label hargaLabel;

    @FXML
    private Label jenisKosLabel;

    @FXML
    private Label alamatLabel;

    @FXML
    private Button viewOrderButton;

    private Kosan kosan;
    private Consumer<Kosan> onClickListener;
    private Consumer<Kosan> onViewOrderClickListener;
    private static final Image DEFAULT_IMAGE = new Image(KosanCardController.class.getResourceAsStream("/com/example/uji_coba/images/logo.png"));


    public void setData(Kosan kosan, Consumer<Kosan> onCardClick) {
        this.setData(kosan, onCardClick, null);
    }

    public void setData(Kosan kosan, Consumer<Kosan> onCardClick, Consumer<Kosan> onViewOrderClick) {
        this.kosan = kosan;
        this.onClickListener = onCardClick;
        this.onViewOrderClickListener = onViewOrderClick;

        namaKosLabel.setText(kosan.getNama_kos());
        hargaLabel.setText("Rp. " + new DecimalFormat("#,###").format(kosan.getHarga()) + "/Bulan");
        jenisKosLabel.setText(kosan.getJenis_kos());
        alamatLabel.setText(kosan.getAlamat());

        // Load image efficiently
        loadKosanImage(kosan.getPath_gambar());


        // Clip the image to have rounded corners
        Rectangle clip = new Rectangle(
                kosanImage.getFitWidth(), kosanImage.getFitHeight()
        );
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        kosanImage.setClip(clip);

        if (viewOrderButton != null) {
            viewOrderButton.setVisible(onViewOrderClickListener != null);
        }
    }

    private void loadKosanImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                try (InputStream is = new FileInputStream(file)) {
                    // Load image in background with specified size to save memory
                    Image image = new Image(is, 150, 150, true, true);
                    kosanImage.setImage(image);
                    return;
                } catch (Exception e) {
                    System.err.println("Failed to load image: " + imagePath);
                    e.printStackTrace();
                }
            }
        }
        kosanImage.setImage(DEFAULT_IMAGE);
    }

    @FXML
    void handleCardClick(MouseEvent event) {
        if (onClickListener != null) {
            onClickListener.accept(kosan);
        }
    }

    @FXML
    void handleViewOrderClick(MouseEvent event) {
        if (onViewOrderClickListener != null) {
            onViewOrderClickListener.accept(kosan);
        }
    }
}