package com.example.uji_coba;

import com.example.uji_coba.UlasanDAO;
import com.example.uji_coba.Order;
import com.example.uji_coba.Ulasan;
import com.example.uji_coba.User;
import com.example.uji_coba.SessionManager;
import com.example.uji_coba.Navigatable;
import com.example.uji_coba.NavigationService;
import com.example.uji_coba.View;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.controlsfx.control.Rating;

import java.io.IOException;

public class UlasanController implements Navigatable {

    @FXML
    private Rating rating;

    @FXML
    private TextArea ulasanTextArea;

    @FXML
    private Button submitButton;

    private NavigationService navigationService;
    private Order order;

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @FXML
    private void handleSubmit() {
        UlasanDAO ulasanDAO = new UlasanDAO();
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            Ulasan ulasan = new Ulasan(0, order.getId_kos(), currentUser.getId(), (int) rating.getRating(), ulasanTextArea.getText(), null);
            ulasanDAO.addUlasan(ulasan);

            try {
                navigationService.switchScene(View.USER_DASHBOARD);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}