package com.example.uji_coba;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.print.PrinterJob;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.geometry.Bounds;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

public class ReceiptController implements Navigatable {

    @FXML
    private Label orderIdLabel;
    
    @FXML
    private Label kosanNameLabel;
    
    @FXML
    private Label userNameLabel;
    
    @FXML
    private Label tanggalSewaLabel;
    
    @FXML
    private Label durasiLabel;
    
    @FXML
    private Label hargaLabel;
    
    @FXML
    private Label totalLabel;
    
    @FXML
    private Label tanggalCetakLabel;
    
    @FXML
    private VBox receiptContainer;
    
    private Order order;
    private Kosan kosan;
    private NavigationService navigationService;
    
    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }
    
    public void setOrderData(Order order, Kosan kosan) {
        this.order = order;
        this.kosan = kosan;
        
        // Format currency
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        // Set order details
        orderIdLabel.setText("ORD" + String.format("%06d", order.getId_order()));
        kosanNameLabel.setText(order.getKosanName() != null ? order.getKosanName() : (kosan != null ? kosan.getNama_kos() : ""));
        userNameLabel.setText(order.getUserName() != null ? order.getUserName() : "");
        
        // Format date properly
        if (order.getTanggal_sewa() != null) {
            LocalDateTime dateTime = order.getTanggal_sewa().toLocalDateTime();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            tanggalSewaLabel.setText(dateTime.format(dateFormatter));
        }
        
        // Get duration from order (fixed bug - now uses actual duration)
        int durasi = order.getDurasi();
        double harga = kosan != null ? kosan.getHarga() : 0;
        double total = harga * durasi;
        
        durasiLabel.setText(durasi + " bulan");
        hargaLabel.setText(formatCurrency(harga));
        totalLabel.setText(formatCurrency(total));
        
        // Set current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        tanggalCetakLabel.setText("Dicetak pada: " + now.format(formatter));
    }
    
    private String formatCurrency(double amount) {
        NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("id", "ID"));
        return "Rp " + currencyFormat.format(amount);
    }
    
    @FXML
    private void handlePrint() {
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null && printerJob.showPrintDialog(receiptContainer.getScene().getWindow())) {
            
            // Get the default printer and create a proper page layout
            Printer printer = printerJob.getPrinter();
            PageLayout pageLayout = printer.createPageLayout(
                Paper.A4,
                PageOrientation.PORTRAIT,
                Printer.MarginType.DEFAULT
            );
            
            // Set the page layout
            printerJob.getJobSettings().setPageLayout(pageLayout);
            
            // Create a copy of the receipt container for printing
            VBox printContainer = createPrintableReceipt();
            
            // Calculate scaling to fit the page
            Bounds containerBounds = printContainer.getBoundsInLocal();
            double scaleX = pageLayout.getPrintableWidth() / containerBounds.getWidth();
            double scaleY = pageLayout.getPrintableHeight() / containerBounds.getHeight();
            double scale = Math.min(scaleX, scaleY) * 0.9; // 90% to ensure margins
            
            // Apply scaling if needed
            if (scale < 1.0) {
                Scale scaleTransform = new Scale(scale, scale);
                printContainer.getTransforms().add(scaleTransform);
            }
            
            // Print the container
            boolean success = printerJob.printPage(pageLayout, printContainer);
            if (success) {
                printerJob.endJob();
                System.out.println("Receipt printed successfully");
            } else {
                System.out.println("Failed to print receipt");
            }
        }
    }
    
    private VBox createPrintableReceipt() {
        VBox printContainer = new VBox();
        printContainer.setSpacing(15.0);
        printContainer.setStyle("-fx-background-color: white; -fx-padding: 20;");
        
        // Header
        VBox header = new VBox();
        header.setSpacing(10.0);
        header.setStyle("-fx-alignment: center;");
        
        Label brandLabel = new Label("MAMAKOS");
        brandLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label titleLabel = new Label("STRUK PEMESANAN KOSAN");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        Label subtitleLabel = new Label("Bukti Serah Terima");
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        header.getChildren().addAll(brandLabel, titleLabel, subtitleLabel);
        
        // Divider
        VBox divider1 = new VBox();
        divider1.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 0 0 1 0; -fx-padding: 5 0;");
        
        // Order Details
        VBox details = new VBox();
        details.setSpacing(8.0);
        
        details.getChildren().addAll(
            createDetailRow("No. Pesanan:", orderIdLabel.getText()),
            createDetailRow("Nama Kosan:", kosanNameLabel.getText()),
            createDetailRow("Penyewa:", userNameLabel.getText()),
            createDetailRow("Tanggal Sewa:", tanggalSewaLabel.getText()),
            createDetailRow("Durasi:", durasiLabel.getText()),
            createDetailRow("Harga/bulan:", hargaLabel.getText())
        );
        
        // Divider
        VBox divider2 = new VBox();
        divider2.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 0 0 1 0; -fx-padding: 5 0;");
        
        // Total
        VBox totalSection = new VBox();
        Label totalRowLabel = new Label("TOTAL BAYAR: " + totalLabel.getText());
        totalRowLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-alignment: center;");
        totalSection.getChildren().add(totalRowLabel);
        totalSection.setStyle("-fx-alignment: center;");
        
        // Divider
        VBox divider3 = new VBox();
        divider3.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 0 0 1 0; -fx-padding: 5 0;");
        
        // Footer
        VBox footer = new VBox();
        footer.setSpacing(5.0);
        footer.setStyle("-fx-alignment: center;");
        
        Label dateLabel = new Label(tanggalCetakLabel.getText());
        dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
        
        Label statusLabel = new Label("Status: DITERIMA");
        statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
        
        Label thankLabel = new Label("Terima kasih telah menggunakan layanan MAMAKOS");
        thankLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
        
        Label saveLabel = new Label("Simpan struk ini sebagai bukti pemesanan");
        saveLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
        
        footer.getChildren().addAll(dateLabel, statusLabel, thankLabel, saveLabel);
        
        printContainer.getChildren().addAll(
            header, divider1, details, divider2, totalSection, divider3, footer
        );
        
        return printContainer;
    }
    
    private VBox createDetailRow(String label, String value) {
        VBox row = new VBox();
        row.setSpacing(2.0);
        
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-weight: bold;");
        
        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c3e50;");
        
        row.getChildren().addAll(labelNode, valueNode);
        return row;
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) receiptContainer.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleBackToDashboard() {
        try {
            // Check if we have navigation service available
            if (navigationService != null) {
                // Navigate back to user dashboard
                navigationService.switchScene(View.USER_DASHBOARD);
            } else {
                // If no navigation service, try to get the main stage and navigate
                Stage currentStage = (Stage) receiptContainer.getScene().getWindow();
                
                // Create a new navigation service with the current stage
                NavigationService navService = new NavigationService(currentStage);
                navService.switchScene(View.USER_DASHBOARD);
            }
        } catch (Exception e) {
            System.err.println("Error navigating back to dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: just close the current window
            Stage stage = (Stage) receiptContainer.getScene().getWindow();
            stage.close();
        }
    }
    
    @FXML
    private void handleViewReceipt() {
        // Create a new window for detailed receipt view
        Stage detailStage = new Stage();
        detailStage.setTitle("Detail Struk Pemesanan");
        detailStage.setResizable(true);
        detailStage.setMinWidth(400);
        detailStage.setMinHeight(500);
        
        // Clone the current receipt container for the detail view
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/uji_coba/Receipt.fxml"));
            javafx.scene.Parent root = loader.load();
            ReceiptController controller = loader.getController();
            controller.setOrderData(order, kosan);
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            detailStage.setScene(scene);
            detailStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}