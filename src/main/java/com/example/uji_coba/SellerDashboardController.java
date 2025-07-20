package com.example.uji_coba;

import com.example.uji_coba.OrderDAO;
import com.example.uji_coba.Kosan;
import com.example.uji_coba.Order;
import com.example.uji_coba.User;
import com.example.uji_coba.DatabaseUtil;
import com.example.uji_coba.SessionManager;
import com.example.uji_coba.Navigatable;
import com.example.uji_coba.NavigationService;
import com.example.uji_coba.View;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SellerDashboardController implements Navigatable {

    @FXML
    private Button dashboardButton;
    @FXML
    private Button tambahKosanButton;
    @FXML
    private Button orderKosanButton;
    @FXML
    private Button logoutButton;
    @FXML
    private VBox kosListContainer;
    @FXML
    private BorderPane mainPane;
    @FXML
    private Label welcomeLabel;
    @FXML
    private ImageView profileImageView;

    private NavigationService navigationService;

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }


    @FXML
    public void initialize() {
        // Set the initial selected button style
        dashboardButton.getStyleClass().add("sidebar-button-selected");
        loadUserData();
        handleDashboardClick();
    }

    private void loadUserData() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
            String profileImagePath = currentUser.getProfileImagePath();
            if (profileImagePath != null && !profileImagePath.isEmpty()) {
                File file = new File(profileImagePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    profileImageView.setImage(image);
                }
            }
        }
    }

    @FXML
    private void handleProfileClick() {
        // Implement profile picture update logic here
    }

    private void loadKosList() {
        kosListContainer.getChildren().clear();
        Label loadingLabel = new Label("Memuat data kosan...");
        kosListContainer.getChildren().add(loadingLabel);

        Task<List<Kosan>> loadKosTask = new Task<>() {
            @Override
            protected List<Kosan> call() throws Exception {
                return getKosanForCurrentUser();
            }
        };

        loadKosTask.setOnSucceeded(event -> {
            kosListContainer.getChildren().clear();
            List<Kosan> kosanList = loadKosTask.getValue();
            if (kosanList.isEmpty()) {
                Label noKosLabel = new Label("Anda belum menambahkan data kosan.");
                noKosLabel.getStyleClass().add("no-kos-label");
                kosListContainer.getChildren().add(noKosLabel);
            } else {
                for (Kosan kos : kosanList) {
                    kosListContainer.getChildren().add(createKosCard(kos));
                }
            }
        });

        loadKosTask.setOnFailed(event -> {
            kosListContainer.getChildren().clear();
            Label errorLabel = new Label("Gagal memuat data kosan.");
            kosListContainer.getChildren().add(errorLabel);
        });

        new Thread(loadKosTask).start();
    }

    private AnchorPane createKosCard(Kosan kos) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uji_coba/KosanCard.fxml"));
            AnchorPane card = loader.load();
            KosanCardController controller = loader.getController();
            controller.setData(kos, this::handleKosanCardClick, this::handleViewOrderClick);
            return card;
        } catch (IOException e) {
            return new AnchorPane(); // Return an empty pane on error
        }
    }

    private void handleViewOrderClick(Kosan kosan) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uji_coba/OrderView.fxml"));
            AnchorPane orderView = loader.load();
            OrderViewController controller = loader.getController();
            controller.setKosan(kosan);
            mainPane.setCenter(orderView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Kosan> getKosanForCurrentUser() {
        List<Kosan> kosanList = new ArrayList<>();
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.out.println("No user logged in.");
            return kosanList;
        }
        int sellerId = currentUser.getId();

        String sql = "SELECT * FROM kosan WHERE id_user = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sellerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                kosanList.add(new Kosan(
                        rs.getInt("id_kos"),
                        rs.getInt("id_user"),
                        rs.getString("nama_kos"),
                        rs.getString("alamat"),
                        rs.getString("jenis_kos"),
                        rs.getInt("harga"),
                        rs.getString("fitur"),
                        rs.getString("path_gambar"),
                        rs.getString("universitas"),
                        rs.getString("deskripsi"),
                        rs.getString("fasilitas_kamar"),
                        rs.getTimestamp("tanggal_ditambahkan")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kosanList;
    }

    @FXML
    private void handleDashboardClick() {
        updateSelectedButton(dashboardButton);
        loadKosList();
    }


   @FXML
     private void handleTambahKosanClick() {
         updateSelectedButton(tambahKosanButton);
         try {
             FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uji_coba/" + View.ADD_KOS.getFileName()));
             BorderPane tambahKosanView = loader.load();
             AddKosController controller = loader.getController();
             controller.setNavigationService(navigationService);
             controller.setOnSaveSuccessCallback(() -> {
                 mainPane.setCenter(kosListContainer);
                 handleDashboardClick();
             });
             mainPane.setCenter(tambahKosanView);
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

    private void handleKosanCardClick(Kosan kosan) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(View.KOSAN_DETAIL.getFileName()));
            BorderPane detailView = loader.load();
            KosanDetailController controller = loader.getController();
            controller.setNavigationService(navigationService);
            controller.setKosan(kosan);
            mainPane.setCenter(detailView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOrderKosanClick() {
        updateSelectedButton(orderKosanButton);
        loadOrderList();
    }

    private void loadOrderList() {
        kosListContainer.getChildren().clear();
        Label loadingLabel = new Label("Memuat data pesanan...");
        kosListContainer.getChildren().add(loadingLabel);

        Task<List<Order>> loadOrdersTask = new Task<>() {
            @Override
            protected List<Order> call() {
                OrderDAO orderDAO = new OrderDAO();
                User currentUser = SessionManager.getInstance().getCurrentUser();
                if (currentUser != null) {
                    return orderDAO.getOrdersBySellerId(currentUser.getId());
                }
                return new ArrayList<>();
            }
        };

        loadOrdersTask.setOnSucceeded(event -> {
            kosListContainer.getChildren().clear();
            List<Order> orderList = loadOrdersTask.getValue();
            if (orderList.isEmpty()) {
                Label noOrderLabel = new Label("Tidak ada pesanan.");
                noOrderLabel.getStyleClass().add("no-kos-label");
                kosListContainer.getChildren().add(noOrderLabel);
            } else {
                for (Order order : orderList) {
                    kosListContainer.getChildren().add(createOrderCard(order));
                }
            }
        });

        loadOrdersTask.setOnFailed(event -> {
            kosListContainer.getChildren().clear();
            Label errorLabel = new Label("Gagal memuat data pesanan.");
            kosListContainer.getChildren().add(errorLabel);
        });

        new Thread(loadOrdersTask).start();
    }

    private AnchorPane createOrderCard(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uji_coba/OrderCard.fxml"));
            AnchorPane card = loader.load();
            OrderCardController controller = loader.getController();
            controller.setData(order, this::loadOrderList, null);
            return card;
        } catch (IOException e) {
            e.printStackTrace();
            return new AnchorPane();
        }
    }

    private void updateSelectedButton(Button selectedButton) {
        List<Button> buttons = Arrays.asList(dashboardButton, tambahKosanButton, orderKosanButton);
        for (Button button : buttons) {
            if (button.equals(selectedButton)) {
                button.getStyleClass().add("sidebar-button-selected");
            } else {
                button.getStyleClass().remove("sidebar-button-selected");
            }
        }
    }

    @FXML
    private void handleLogoutClick() throws IOException {
        // Clear the current user session
        SessionManager.getInstance().clearSession();

        // Redirect to login2.fxml
        navigationService.switchScene(View.LOGIN);
    }
}