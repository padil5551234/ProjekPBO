package com.example.uji_coba;

import com.example.uji_coba.SessionManager;
import com.example.uji_coba.Navigatable;
import com.example.uji_coba.NavigationService;
import com.example.uji_coba.View;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class AdminController implements Navigatable {

    @FXML
    private Button logoutButton;

    private NavigationService navigationService;

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    // Fungsi untuk logout
    @FXML
    private void handleLogout() throws IOException {
        SessionManager.getInstance().clearSession();
        navigationService.switchScene(View.LOGIN);
    }
}