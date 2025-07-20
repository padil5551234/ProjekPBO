package com.example.uji_coba;

import com.example.uji_coba.KosanDAO;
import com.example.uji_coba.Kosan;
import com.example.uji_coba.SessionManager;
import com.example.uji_coba.Navigatable;
import com.example.uji_coba.NavigationService;
import com.example.uji_coba.View;
import com.example.uji_coba.Order;
import com.example.uji_coba.OrderDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UserDashboardController implements Navigatable {

    @FXML
    private Button dashboardButton;
    @FXML
    private Button cariButton;
    @FXML
    private Button sewaButton;
    @FXML
    private Button struksButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button profileButton;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<Kosan> kosanListView;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private ComboBox<String> jenisKosFilter;
    @FXML
    private ComboBox<String> hargaRangeFilter;
    @FXML
    private ComboBox<String> universitasFilter;
    @FXML
    private Button searchButton;
    @FXML
    private Button resetFilterButton;
    @FXML
    private VBox kosanContainer;


    private NavigationService navigationService;
    private List<Kosan> allKosanList = new ArrayList<>();
    private final ObservableList<Kosan> kosanList = FXCollections.observableArrayList();


    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    public void initialize() {
        System.out.println("UserDashboardController initialized. Loading all kosan...");
        setupListView();
        setupSortComboBox();
        setupFilterComboBoxes();
        loadAllKosan();
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void setupListView() {
        kosanListView.setItems(kosanList);
        kosanListView.setCellFactory(new Callback<ListView<Kosan>, ListCell<Kosan>>() {
            @Override
            public ListCell<Kosan> call(ListView<Kosan> listView) {
                return new KosanCardCell();
            }
        });
    }

    private class KosanCardCell extends ListCell<Kosan> {
        private Parent card;
        private KosanCardController controller;

        public KosanCardCell() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uji_coba/" + View.KOSAN_CARD.getFileName()));
                card = loader.load();
                controller = loader.getController();
            } catch (IOException e) {
                System.err.println("Failed to load KosanCard.fxml!");
                e.printStackTrace();
            }
        }

        @Override
        protected void updateItem(Kosan kosan, boolean empty) {
            super.updateItem(kosan, empty);
            if (empty || kosan == null) {
                setText(null);
                setGraphic(null);
            } else if (kosan.getId_kos() == 0) {
                // This is a loading or error message
                setText(kosan.getNama_kos());
                setGraphic(null);
            } else {
                setText(null);
                controller.setData(kosan, UserDashboardController.this::handleKosanCardClick);
                setGraphic(card);
            }
        }
    }


    private void setupSortComboBox() {
        sortComboBox.setItems(FXCollections.observableArrayList("Harga Terendah", "Harga Tertinggi", "Nama A-Z", "Nama Z-A"));
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                sortKosan(newVal);
            }
        });
        sortComboBox.getSelectionModel().selectFirst();
    }

    private void setupFilterComboBoxes() {
        // Setup Jenis Kos Filter
        jenisKosFilter.setItems(FXCollections.observableArrayList("Semua", "Pria", "Wanita", "Campur"));
        jenisKosFilter.getSelectionModel().selectFirst();
        jenisKosFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Setup Harga Range Filter
        hargaRangeFilter.setItems(FXCollections.observableArrayList(
            "Semua", "< 500.000", "500.000 - 1.000.000", "1.000.000 - 2.000.000", "> 2.000.000"
        ));
        hargaRangeFilter.getSelectionModel().selectFirst();
        hargaRangeFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Setup Universitas Filter - will be populated after loading kosan data
        universitasFilter.setItems(FXCollections.observableArrayList("Semua"));
        universitasFilter.getSelectionModel().selectFirst();
        universitasFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void populateUniversitasFilter() {
        List<String> universities = allKosanList.stream()
            .map(Kosan::getUniversitas)
            .filter(univ -> univ != null && !univ.trim().isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        universities.add(0, "Semua");
        universitasFilter.setItems(FXCollections.observableArrayList(universities));
        universitasFilter.getSelectionModel().selectFirst();
    }

    private void applyFilters() {
        List<Kosan> filteredList = new ArrayList<>(allKosanList);

        // Apply search filter
        String searchQuery = searchField.getText();
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String lowerCaseQuery = searchQuery.toLowerCase();
            filteredList = filteredList.stream()
                .filter(kosan -> kosan.getNama_kos().toLowerCase().contains(lowerCaseQuery) ||
                               kosan.getAlamat().toLowerCase().contains(lowerCaseQuery) ||
                               (kosan.getUniversitas() != null && kosan.getUniversitas().toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());
        }

        // Apply jenis kos filter
        String jenisFilter = jenisKosFilter.getValue();
        if (jenisFilter != null && !jenisFilter.equals("Semua")) {
            filteredList = filteredList.stream()
                .filter(kosan -> kosan.getJenis_kos().equalsIgnoreCase(jenisFilter))
                .collect(Collectors.toList());
        }

        // Apply harga range filter
        String hargaFilter = hargaRangeFilter.getValue();
        if (hargaFilter != null && !hargaFilter.equals("Semua")) {
            filteredList = filteredList.stream()
                .filter(kosan -> matchesHargaRange(kosan.getHarga(), hargaFilter))
                .collect(Collectors.toList());
        }

        // Apply universitas filter
        String univFilter = universitasFilter.getValue();
        if (univFilter != null && !univFilter.equals("Semua")) {
            filteredList = filteredList.stream()
                .filter(kosan -> kosan.getUniversitas() != null && kosan.getUniversitas().equalsIgnoreCase(univFilter))
                .collect(Collectors.toList());
        }

        // Apply sorting
        String sortOrder = sortComboBox.getValue();
        if (sortOrder != null) {
            Comparator<Kosan> comparator = null;
            switch (sortOrder) {
                case "Harga Terendah":
                    comparator = Comparator.comparing(Kosan::getHarga);
                    break;
                case "Harga Tertinggi":
                    comparator = Comparator.comparing(Kosan::getHarga).reversed();
                    break;
                case "Nama A-Z":
                    comparator = Comparator.comparing(Kosan::getNama_kos);
                    break;
                case "Nama Z-A":
                    comparator = Comparator.comparing(Kosan::getNama_kos).reversed();
                    break;
            }
            if (comparator != null) {
                filteredList.sort(comparator);
            }
        }

        kosanList.setAll(filteredList);
    }

    private boolean matchesHargaRange(int harga, String range) {
        switch (range) {
            case "< 500.000":
                return harga < 500000;
            case "500.000 - 1.000.000":
                return harga >= 500000 && harga <= 1000000;
            case "1.000.000 - 2.000.000":
                return harga >= 1000000 && harga <= 2000000;
            case "> 2.000.000":
                return harga > 2000000;
            default:
                return true;
        }
    }

    private void sortKosan(String sortOrder) {
        applyFilters();
    }

    private void loadAllKosan() {
        // Only show loading message in the ListView, don't clear the entire container
        kosanList.clear();
        kosanList.add(new Kosan(0, 0, "Memuat data kosan...", "", "", 0, "", "", "", "", "", null));

        Task<List<Kosan>> loadKosanTask = new Task<>() {
            @Override
            protected List<Kosan> call() throws Exception {
                KosanDAO kosanDAO = new KosanDAO();
                return kosanDAO.getAllKosan();
            }
        };

        loadKosanTask.setOnSucceeded(event -> {
            allKosanList = loadKosanTask.getValue();
            System.out.println("Loaded " + allKosanList.size() + " kosan from the database.");
            populateUniversitasFilter();
            applyFilters();
        });

        loadKosanTask.setOnFailed(event -> {
            kosanList.clear();
            kosanList.add(new Kosan(0, 0, "Gagal memuat data kosan.", "", "", 0, "", "", "", "", "", null));
        });

        new Thread(loadKosanTask).start();
    }

    private void searchKosan(String query) {
        List<Kosan> filteredList;
        if (query == null || query.isEmpty()) {
            filteredList = new ArrayList<>(allKosanList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            filteredList = allKosanList.stream()
                .filter(kosan -> kosan.getNama_kos().toLowerCase().contains(lowerCaseQuery) ||
                                 kosan.getAlamat().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
        }
        kosanList.setAll(filteredList);
    }

    private void handleKosanCardClick(Kosan kosan) {
        try {
            navigationService.switchScene(View.KOSAN_DETAIL, kosan);
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }

    public void refreshKosanList() {
        loadAllKosan();
    }

    @FXML
    void handleDashboardClick(ActionEvent event) {
        updateButtonStyles("dashboardButton");
        // Show kosan list (default view) - don't clear the container as it contains search/filter components
        loadAllKosan();
    }

    @FXML
    void handleCariClick(ActionEvent event) {
        updateButtonStyles("cariButton");
        // Show kosan list (default view) - don't clear the container as it contains search/filter components
        applyFilters();
    }

    @FXML
    void handleSearchClick(ActionEvent event) {
        applyFilters();
    }

    @FXML
    void handleResetFilters(ActionEvent event) {
        searchField.clear();
        jenisKosFilter.getSelectionModel().selectFirst();
        hargaRangeFilter.getSelectionModel().selectFirst();
        universitasFilter.getSelectionModel().selectFirst();
        sortComboBox.getSelectionModel().selectFirst();
        applyFilters();
    }

    @FXML
    void handleSewaClick(ActionEvent event) {
        updateButtonStyles("sewaButton");
        try {
            navigationService.switchScene(View.SEWA_KOSAN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleStruksClick(ActionEvent event) {
        updateButtonStyles("struksButton");
        showApprovedOrders();
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
    void handleProfileClick(ActionEvent event) {
        updateButtonStyles("profileButton");
        try {
            navigationService.switchScene(View.USER_PROFILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateButtonStyles(String activeButton) {
        // Reset all button styles
        dashboardButton.getStyleClass().removeAll("sidebar-button-selected");
        cariButton.getStyleClass().removeAll("sidebar-button-selected");
        sewaButton.getStyleClass().removeAll("sidebar-button-selected");
        struksButton.getStyleClass().removeAll("sidebar-button-selected");
        profileButton.getStyleClass().removeAll("sidebar-button-selected");

        // Set active button style
        switch (activeButton) {
            case "dashboardButton":
                dashboardButton.getStyleClass().add("sidebar-button-selected");
                break;
            case "cariButton":
                cariButton.getStyleClass().add("sidebar-button-selected");
                break;
            case "sewaButton":
                sewaButton.getStyleClass().add("sidebar-button-selected");
                break;
            case "struksButton":
                struksButton.getStyleClass().add("sidebar-button-selected");
                break;
            case "profileButton":
                profileButton.getStyleClass().add("sidebar-button-selected");
                break;
        }
    }

    private void showApprovedOrders() {
        kosanContainer.getChildren().clear();
        
        // Add header
        Label headerLabel = new Label("Struk Belanja Saya");
        headerLabel.getStyleClass().add("header-title");
        kosanContainer.getChildren().add(headerLabel);

        // Add loading message
        Label loadingLabel = new Label("Memuat struk belanja...");
        kosanContainer.getChildren().add(loadingLabel);

        // Load approved orders in background
        Task<List<Order>> loadOrdersTask = new Task<>() {
            @Override
            protected List<Order> call() throws Exception {
                OrderDAO orderDAO = new OrderDAO();
                User currentUser = SessionManager.getInstance().getCurrentUser();
                if (currentUser == null) {
                    return new ArrayList<>();
                }
                int currentUserId = currentUser.getId();
                List<Order> allOrders = orderDAO.getOrdersByUserIdWithKosan(currentUserId);
                
                // Filter only approved orders
                return allOrders.stream()
                    .filter(order -> "approved".equals(order.getStatus()))
                    .collect(Collectors.toList());
            }
        };

        loadOrdersTask.setOnSucceeded(event -> {
            List<Order> approvedOrders = loadOrdersTask.getValue();
            kosanContainer.getChildren().clear();
            kosanContainer.getChildren().add(headerLabel);

            if (approvedOrders.isEmpty()) {
                Label noOrdersLabel = new Label("Belum ada pesanan yang disetujui.");
                noOrdersLabel.getStyleClass().add("no-data-label");
                kosanContainer.getChildren().add(noOrdersLabel);
            } else {
                // Create ListView for approved orders
                ListView<Order> approvedOrdersList = new ListView<>();
                approvedOrdersList.getStyleClass().add("list-view");
                approvedOrdersList.setItems(FXCollections.observableArrayList(approvedOrders));
                
                // Set custom cell factory for order items
                approvedOrdersList.setCellFactory(new Callback<ListView<Order>, ListCell<Order>>() {
                    @Override
                    public ListCell<Order> call(ListView<Order> listView) {
                        return new ApprovedOrderCell();
                    }
                });

                VBox.setVgrow(approvedOrdersList, javafx.scene.layout.Priority.ALWAYS);
                kosanContainer.getChildren().add(approvedOrdersList);
            }
        });

        loadOrdersTask.setOnFailed(event -> {
            kosanContainer.getChildren().clear();
            kosanContainer.getChildren().add(headerLabel);
            Label errorLabel = new Label("Gagal memuat data struk belanja.");
            errorLabel.getStyleClass().add("error-label");
            kosanContainer.getChildren().add(errorLabel);
        });

        new Thread(loadOrdersTask).start();
    }

    private class ApprovedOrderCell extends ListCell<Order> {
        @Override
        protected void updateItem(Order order, boolean empty) {
            super.updateItem(order, empty);
            if (empty || order == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox orderCard = createOrderCard(order);
                setGraphic(orderCard);
            }
        }
    }

    private VBox createOrderCard(Order order) {
        VBox card = new VBox();
        card.getStyleClass().add("order-card");
        card.setSpacing(10);
        card.setPadding(new Insets(15));

        // Order info
        Label orderIdLabel = new Label("Pesanan #ORD" + String.format("%06d", order.getId_order()));
        orderIdLabel.getStyleClass().add("order-id-label");

        Label kosanNameLabel = new Label("Kosan: " + order.getKosanName());
        kosanNameLabel.getStyleClass().add("kosan-name-label");

        Label dateLabel = new Label("Tanggal: " + order.getTanggal_sewa().toString().substring(0, 10));
        dateLabel.getStyleClass().add("date-label");

        Label statusLabel = new Label("Status: DITERIMA");
        statusLabel.getStyleClass().add("status-approved-label");

        // Button to view receipt
        Button viewReceiptButton = new Button("Lihat Struk");
        viewReceiptButton.getStyleClass().add("view-receipt-button");
        viewReceiptButton.setOnAction(e -> showReceipt(order));

        HBox buttonContainer = new HBox();
        buttonContainer.getChildren().add(viewReceiptButton);
        buttonContainer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        card.getChildren().addAll(orderIdLabel, kosanNameLabel, dateLabel, statusLabel, buttonContainer);
        return card;
    }

    private void showReceipt(Order order) {
        try {
            // Get kosan details
            KosanDAO kosanDAO = new KosanDAO();
            Kosan kosan = kosanDAO.getKosanById(order.getId_kos());

            // Load receipt view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uji_coba/Receipt.fxml"));
            Parent root = loader.load();
            
            ReceiptController receiptController = loader.getController();
            receiptController.setNavigationService(navigationService);
            receiptController.setOrderData(order, kosan);

            // Create new stage for receipt
            Stage receiptStage = new Stage();
            receiptStage.setTitle("Struk Pemesanan - " + order.getKosanName());
            receiptStage.setScene(new Scene(root));
            receiptStage.setResizable(true);
            receiptStage.setMinWidth(600);
            receiptStage.setMinHeight(700);
            receiptStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load receipt view: " + e.getMessage());
        }
    }
}