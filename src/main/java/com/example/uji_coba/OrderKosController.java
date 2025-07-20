package com.example.uji_coba;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.text.NumberFormat;
import java.util.Locale;

public class OrderKosController implements Navigatable {

    @FXML
    private Label kosanNameLabel;
    @FXML
    private Label kosanPriceLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private DatePicker sewaDatePicker;
    @FXML
    private TextField durasiField;
    @FXML
    private TextArea catatanArea;
    @FXML
    private Button submitOrderButton;
    @FXML
    private Button backButton;

    private Kosan kosan;
    private NavigationService navigationService;
    private OrderDAO orderDAO = new OrderDAO();

    public void initData(Kosan kosan) {
        this.kosan = kosan;
        kosanNameLabel.setText(kosan.getNama_kos());
        kosanPriceLabel.setText("Harga: " + formatCurrency(kosan.getHarga()) + "/bulan");
        
        // Add listener to duration field to update total
        durasiField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateTotal();
        });
        
        // Initialize total
        updateTotal();
    }
    
    private void updateTotal() {
        try {
            int durasi = Integer.parseInt(durasiField.getText().trim());
            if (durasi <= 0) {
                durasi = 1;
                durasiField.setText("1");
            }
            double total = kosan.getHarga() * durasi;
            totalLabel.setText("Total: " + formatCurrency(total));
        } catch (NumberFormatException e) {
            totalLabel.setText("Total: " + formatCurrency(kosan.getHarga()));
        }
    }
    
    private String formatCurrency(double amount) {
        NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("id", "ID"));
        return "Rp " + currencyFormat.format(amount);
    }

    @FXML
    private void handleSubmitOrder() {
        LocalDate sewaDate = sewaDatePicker.getValue();
        if (sewaDate == null) {
            showAlert("Error", "Silakan pilih tanggal mulai sewa.", Alert.AlertType.ERROR);
            return;
        }
        
        // Validate duration
        int durasi;
        try {
            durasi = Integer.parseInt(durasiField.getText().trim());
            if (durasi <= 0) {
                showAlert("Error", "Durasi sewa harus lebih dari 0 bulan.", Alert.AlertType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Durasi sewa harus berupa angka.", Alert.AlertType.ERROR);
            return;
        }
        
        // Check if date is not in the past
        if (sewaDate.isBefore(LocalDate.now())) {
            showAlert("Error", "Tanggal sewa tidak boleh di masa lalu.", Alert.AlertType.ERROR);
            return;
        }

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert("Error", "Anda harus login untuk menyewa.", Alert.AlertType.ERROR);
            return;
        }

        // Check if user already has a pending order for this kosan
        if (orderDAO.hasActiveOrder(kosan.getId_kos(), currentUser.getId())) {
            showAlert("Error", "Anda sudah memiliki pesanan yang sedang menunggu konfirmasi untuk kosan ini.", Alert.AlertType.ERROR);
            return;
        }

        // Check if kosan is still available
        if (!orderDAO.isKosanAvailable(kosan.getId_kos())) {
            showAlert("Error", "Maaf, kosan ini sudah tidak tersedia.", Alert.AlertType.ERROR);
            return;
        }

        Order newOrder = new Order(0, kosan.getId_kos(), currentUser.getId(), Timestamp.valueOf(sewaDate.atStartOfDay()), "pending", durasi);
        boolean success = orderDAO.createOrder(newOrder);

        if (success) {
            // Show success alert with option to view receipt
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sukses");
            alert.setHeaderText("Pengajuan pemesanan berhasil!");
            alert.setContentText("Detail Pemesanan:\n" +
                    "- Kosan: " + kosan.getNama_kos() + "\n" +
                    "- Tanggal Mulai: " + sewaDate.toString() + "\n" +
                    "- Durasi: " + durasi + " bulan\n" +
                    "- Total: " + formatCurrency(kosan.getHarga() * durasi) + "\n\n" +
                    "Mohon tunggu konfirmasi dari penjual.\n" +
                    "Klik 'Lihat Struk' untuk melihat struk pemesanan.");
            
            // Add custom buttons
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(
                new javafx.scene.control.ButtonType("Lihat Struk"),
                new javafx.scene.control.ButtonType("Kembali ke Dashboard")
            );
            
            java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            
            if (result.isPresent() && result.get().getText().equals("Lihat Struk")) {
                // Show receipt
                showReceipt(newOrder, kosan, durasi);
            } else {
                try {
                    navigationService.switchScene(View.USER_DASHBOARD);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showAlert("Gagal", "Gagal mengajukan pemesanan. Silakan coba lagi.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBackClick() throws IOException {
        navigationService.switchScene(View.KOSAN_DETAIL, kosan);
    }
    
    @FXML
    private void handleBackToDashboard() throws IOException {
        navigationService.switchScene(View.USER_DASHBOARD);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showReceipt(Order order, Kosan kosan, int durasi) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("Receipt.fxml"));
            javafx.scene.Parent receiptRoot = loader.load();
            
            ReceiptController receiptController = loader.getController();
            
            // Set the duration in the order before passing to receipt
            order.setDurasi(durasi);
            receiptController.setOrderData(order, kosan);
            
            javafx.stage.Stage receiptStage = new javafx.stage.Stage();
            receiptStage.setTitle("Struk Pemesanan - MAMAKOS");
            receiptStage.setScene(new javafx.scene.Scene(receiptRoot));
            receiptStage.setResizable(false);
            receiptStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            receiptStage.showAndWait();
            
            // After receipt is closed, go back to dashboard
            navigationService.switchScene(View.USER_DASHBOARD);
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal menampilkan struk: " + e.getMessage(), Alert.AlertType.ERROR);
            try {
                navigationService.switchScene(View.USER_DASHBOARD);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }
}