package com.example.uji_coba;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OrderViewController implements Navigatable {

    @FXML
    private VBox orderContainer;
    
    @FXML
    private Button allOrdersButton;
    
    @FXML
    private Button pendingOrdersButton;
    
    @FXML
    private Button acceptedOrdersButton;
    
    @FXML
    private Button rejectedOrdersButton;

    private NavigationService navigationService;
    private OrderDAO orderDAO = new OrderDAO();
    private User currentUser;
    private Kosan kosan;
    private List<Order> allOrders;
    private String currentFilter = "all";

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            loadOrders();
            updateFilterButtons();
        }
    }

    public void setKosan(Kosan kosan) {
        this.kosan = kosan;
        loadOrders();
    }

    private void loadOrders() {
        if (kosan != null) {
            allOrders = orderDAO.getOrdersByKosanId(kosan.getId_kos());
        } else if ("seller".equals(currentUser.getRole())) {
            allOrders = orderDAO.getOrdersBySellerId(currentUser.getId());
        } else {
            allOrders = orderDAO.getOrdersByUserIdWithKosan(currentUser.getId());
        }
        
        System.out.println("Loading orders for user: " + currentUser.getId() + ", found " + allOrders.size() + " orders.");
        displayOrders();
    }
    
    private void displayOrders() {
        orderContainer.getChildren().clear();
        
        List<Order> filteredOrders = allOrders;
        
        // Apply filter
        if (!currentFilter.equals("all")) {
            filteredOrders = allOrders.stream()
                    .filter(order -> order.getStatus().equalsIgnoreCase(currentFilter))
                    .collect(Collectors.toList());
        }

        for (Order order : filteredOrders) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderCard.fxml"));
                VBox orderCard = loader.load();
                OrderCardController controller = loader.getController();

                Consumer<Order> onUlasanClick = (orderForUlasan) -> {
                    try {
                        navigationService.switchScene(View.ULASAN, orderForUlasan);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };

                controller.setData(order, this::loadOrders, onUlasanClick);
                orderContainer.getChildren().add(orderCard);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error loading OrderCard.fxml: " + e.getMessage());
            }
        }
    }
    
    private void updateFilterButtons() {
        if (allOrdersButton != null) {
            // Reset all button styles
            allOrdersButton.getStyleClass().removeAll("sidebar-button-selected");
            pendingOrdersButton.getStyleClass().removeAll("sidebar-button-selected");
            acceptedOrdersButton.getStyleClass().removeAll("sidebar-button-selected");
            rejectedOrdersButton.getStyleClass().removeAll("sidebar-button-selected");
            
            // Add selected style to current filter
            switch (currentFilter) {
                case "all":
                    allOrdersButton.getStyleClass().add("sidebar-button-selected");
                    break;
                case "pending":
                    pendingOrdersButton.getStyleClass().add("sidebar-button-selected");
                    break;
                case "accepted":
                    acceptedOrdersButton.getStyleClass().add("sidebar-button-selected");
                    break;
                case "rejected":
                    rejectedOrdersButton.getStyleClass().add("sidebar-button-selected");
                    break;
            }
        }
    }
    
    @FXML
    private void handleShowAllOrders() {
        currentFilter = "all";
        updateFilterButtons();
        displayOrders();
    }
    
    @FXML
    private void handleShowPendingOrders() {
        currentFilter = "pending";
        updateFilterButtons();
        displayOrders();
    }
    
    @FXML
    private void handleShowAcceptedOrders() {
        currentFilter = "accepted";
        updateFilterButtons();
        displayOrders();
    }
    
    @FXML
    private void handleShowRejectedOrders() {
        currentFilter = "rejected";
        updateFilterButtons();
        displayOrders();
    }

    @FXML
    private void handleBackClick() throws IOException {
        if ("seller".equals(currentUser.getRole())) {
            navigationService.switchScene(View.SELLER_DASHBOARD);
        } else {
            navigationService.switchScene(View.USER_DASHBOARD);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }
}