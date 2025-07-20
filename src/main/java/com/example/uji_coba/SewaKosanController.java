package com.example.uji_coba;

import com.example.uji_coba.OrderDAO;
import com.example.uji_coba.Order;
import com.example.uji_coba.User;
import com.example.uji_coba.SessionManager;
import com.example.uji_coba.Navigatable;
import com.example.uji_coba.NavigationService;
import com.example.uji_coba.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class SewaKosanController implements Navigatable {

    @FXML
    private Button dashboardButton;
    @FXML
    private Button cariButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button backButton;

    @FXML
    private VBox sewaContainer;

    private OrderDAO orderDAO;
    private NavigationService navigationService;

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    public void initialize() {
        orderDAO = new OrderDAO();
        loadSewaKosan();
    }

    private void loadSewaKosan() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            List<Order> orderList;
            if ("seller".equals(currentUser.getRole())) {
                orderList = orderDAO.getOrdersBySellerId(currentUser.getId());
            } else {
                orderList = orderDAO.getOrdersByUserIdWithKosan(currentUser.getId());
            }
            displaySewa(orderList);
        }
    }

    private void displaySewa(List<Order> orderList) {
        sewaContainer.getChildren().clear();
        if (orderList.isEmpty()) {
            sewaContainer.getChildren().add(new Label("Anda belum pernah menyewa kosan."));
        } else {
            for (Order order : orderList) {
                String kosanName = (order.getKosanName() != null) ? order.getKosanName() : "Kosan tidak ditemukan";

                VBox sewaBox = new VBox(5);
                sewaBox.getStyleClass().add("sewa-box");

                Label namaKosLabel = new Label(kosanName);
                namaKosLabel.getStyleClass().add("kos-name");

                Label statusLabel = new Label("Status: " + order.getStatus());
                statusLabel.getStyleClass().add("status-label");

                Label tanggalLabel = new Label("Tanggal Sewa: " + order.getTanggal_sewa().toString());
                tanggalLabel.getStyleClass().add("tanggal-label");

                sewaBox.getChildren().addAll(namaKosLabel, statusLabel, tanggalLabel);

                if ("seller".equals(SessionManager.getInstance().getCurrentUser().getRole())) {
                    HBox actionButtons = new HBox(10);
                    Button acceptButton = new Button("Terima");
                    acceptButton.getStyleClass().add("accept-button");
                    acceptButton.setOnAction(e -> {
                        orderDAO.updateOrderStatus(order.getId_order(), "accepted");
                        loadSewaKosan();
                    });

                    Button rejectButton = new Button("Tolak");
                    rejectButton.getStyleClass().add("reject-button");
                    rejectButton.setOnAction(e -> {
                        orderDAO.updateOrderStatus(order.getId_order(), "rejected");
                        loadSewaKosan();
                    });

                    actionButtons.getChildren().addAll(acceptButton, rejectButton);
                    sewaBox.getChildren().add(actionButtons);
                }

                sewaContainer.getChildren().add(sewaBox);
            }
        }
    }

    @FXML
    void handleDashboardClick(ActionEvent event) {
        // Already on this page
    }

    @FXML
    void handleCariClick(ActionEvent event) {
        try {
            navigationService.switchScene(View.USER_DASHBOARD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleLogoutClick(ActionEvent event) {
        SessionManager.getInstance().clearSession();
        try {
            navigationService.switchScene(View.LOGIN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleBackClick(ActionEvent event) {
        try {
            navigationService.switchScene(View.USER_DASHBOARD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}