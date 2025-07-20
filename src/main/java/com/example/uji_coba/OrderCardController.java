package com.example.uji_coba;

import com.example.uji_coba.OrderDAO;
import com.example.uji_coba.Order;
import com.example.uji_coba.SessionManager;
import com.example.uji_coba.KosanDAO;
import com.example.uji_coba.Kosan;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

public class OrderCardController {

    @FXML
    private Button acceptButton;

    @FXML
    private Button rejectButton;

    @FXML
    private Button ulasanButton;
    
    @FXML
    private Button receiptButton;
    
    @FXML
    private Button deleteButton;

    private Order order;
    private Runnable onOrderUpdate;
    private Consumer<Order> onUlasanClick;

    @FXML
    private Label kosanNameLabel;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label tanggalSewaLabel;

    @FXML
    private Label statusLabel;
    
    @FXML
    private Label durasiLabel;
    
    @FXML
    private Label totalLabel;

    public void setData(Order order, Runnable onOrderUpdate, Consumer<Order> onUlasanClick) {
        this.order = order;
        this.onOrderUpdate = onOrderUpdate;
        this.onUlasanClick = onUlasanClick;

        kosanNameLabel.setText(order.getKosanName());
        userNameLabel.setText("Penyewa: " + order.getUserName());
        tanggalSewaLabel.setText(order.getTanggal_sewa().toString());
        
        // Set status with appropriate styling
        String status = order.getStatus();
        statusLabel.setText(status.toUpperCase());
        
        // Apply status-specific styling
        statusLabel.getStyleClass().removeAll("order-status-pending", "order-status-accepted", "order-status-rejected");
        switch (status.toLowerCase()) {
            case "pending":
                statusLabel.getStyleClass().add("order-status-pending");
                break;
            case "approved":
                statusLabel.getStyleClass().add("order-status-accepted");
                break;
            case "rejected":
                statusLabel.getStyleClass().add("order-status-rejected");
                break;
        }
        
        // Set duration and total (assuming 1 month for now)
        int durasi = 1; // This should come from order data
        durasiLabel.setText(durasi + " bulan");
        
        // Get kosan price and calculate total
        KosanDAO kosanDAO = new KosanDAO();
        Kosan kosan = kosanDAO.getKosanById(order.getId_kos());
        double total = kosan != null ? kosan.getHarga() * durasi : 0;
        totalLabel.setText(formatCurrency(total));

        // Hide buttons based on user role and status
        if (SessionManager.getInstance().getCurrentUser().getRole().equals("seller")) {
            if ("pending".equalsIgnoreCase(order.getStatus())) {
                acceptButton.setVisible(true);
                rejectButton.setVisible(true);
                receiptButton.setVisible(false);
                deleteButton.setVisible(false);
            } else {
                acceptButton.setVisible(false);
                rejectButton.setVisible(false);
                receiptButton.setVisible("approved".equalsIgnoreCase(order.getStatus()));
                // Show delete button for completed orders (approved/rejected)
                deleteButton.setVisible(!"pending".equalsIgnoreCase(order.getStatus()));
            }
            ulasanButton.setVisible(false);
        } else {
            acceptButton.setVisible(false);
            rejectButton.setVisible(false);
            receiptButton.setVisible("approved".equalsIgnoreCase(order.getStatus()));
            deleteButton.setVisible(false);
            if ("approved".equalsIgnoreCase(order.getStatus())) {
                ulasanButton.setVisible(true);
            } else {
                ulasanButton.setVisible(false);
            }
        }
    }
    
    private String formatCurrency(double amount) {
        NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("id", "ID"));
        return "Rp " + currencyFormat.format(amount);
    }

    @FXML
    private void handleAccept() {
        updateOrderStatus("approved");
    }

    @FXML
    private void handleUlasan() {
        if (onUlasanClick != null) {
            onUlasanClick.accept(order);
        }
    }

    @FXML
    private void handleReject() {
        updateOrderStatus("rejected");
    }
    
    @FXML
    private void handleShowReceipt() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Receipt.fxml"));
            Parent root = loader.load();
            
            ReceiptController receiptController = loader.getController();
            
            // Get kosan data
            KosanDAO kosanDAO = new KosanDAO();
            Kosan kosan = kosanDAO.getKosanById(order.getId_kos());
            
            receiptController.setOrderData(order, kosan);
            
            Stage receiptStage = new Stage();
            receiptStage.setTitle("Struk Pemesanan - " + order.getKosanName());
            receiptStage.setScene(new Scene(root));
            receiptStage.initModality(Modality.APPLICATION_MODAL);
            receiptStage.setResizable(false);
            receiptStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading receipt window: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        // Show confirmation dialog
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Riwayat Pesanan");
        alert.setContentText("Apakah Anda yakin ingin menghapus riwayat pesanan ini?");
        
        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            OrderDAO orderDAO = new OrderDAO();
            boolean success = orderDAO.deleteOrder(order.getId_order());
            if (success && onOrderUpdate != null) {
                onOrderUpdate.run();
            }
        }
    }

    private void updateOrderStatus(String status) {
        OrderDAO orderDAO = new OrderDAO();
        boolean success = orderDAO.updateOrderStatusWithSewa(order.getId_order(), status);
        if (success && onOrderUpdate != null) {
            onOrderUpdate.run();
        }
    }
}