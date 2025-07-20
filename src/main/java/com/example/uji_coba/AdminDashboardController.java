package com.example.uji_coba;

import com.example.uji_coba.Kosan;
import com.example.uji_coba.User;
import com.example.uji_coba.DatabaseUtil;
import com.example.uji_coba.SessionManager;
import com.example.uji_coba.Navigatable;
import com.example.uji_coba.NavigationService;
import com.example.uji_coba.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;
import java.text.DecimalFormat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardController implements Navigatable {

    @FXML
    private Button dashboardButton;
    @FXML
    private Button kosButton;
    @FXML
    private Button tenantButton;
    @FXML
    private Button userButton;
    @FXML
    private Button reportButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private AnchorPane contentArea;
    @FXML
    private VBox contentBox;
    @FXML
    private ScrollPane scrollPane;

    private User currentUser;
    private NavigationService navigationService;

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
        }
        loadDashboardView();
    }

    @FXML
    private void handleDashboardButton(ActionEvent event) {
        loadDashboardView();
    }

    @FXML
    private void handleKosButton(ActionEvent event) {
        loadKosView();
    }

    @FXML
    private void handleTenantButton(ActionEvent event) {
        loadTenantView();
    }

    @FXML
    private void handleUserButton(ActionEvent event) {
        loadUserView();
    }

    @FXML
    private void handleReportButton(ActionEvent event) {
        // Implement report view loading logic here
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) {
        SessionManager.getInstance().clearSession();
        try {
            navigationService.switchScene(View.LOGIN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDashboardView() {
        contentBox.getChildren().clear();
        contentBox.setPadding(new Insets(20));
        contentBox.setSpacing(20);

        int totalKos = getTotalCount("kosan");
        int totalTenants = getTotalCount("users WHERE role = 'user'");
        int totalUsers = getTotalCount("users");

        HBox summaryBox = new HBox(20);
        summaryBox.getChildren().addAll(
                createSummaryCard("Kos", String.valueOf(totalKos), "logo.png"),
                createSummaryCard("Tenant", String.valueOf(totalTenants), "logo.png"),
                createSummaryCard("User", String.valueOf(totalUsers), "logo.png")
        );
        contentBox.getChildren().add(summaryBox);
    }

    private int getTotalCount(String tableName) {
        String query = "SELECT COUNT(*) FROM " + tableName;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private VBox createSummaryCard(String title, String count, String iconName) {
        VBox card = new VBox(10);
        card.getStyleClass().add("summary-card");
        card.setPadding(new Insets(20));
        card.setPrefSize(180, 120);

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/example/uji_coba/images/" + iconName)));
        icon.setFitHeight(40);
        icon.setFitWidth(40);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("summary-title");

        Label countLabel = new Label(count);
        countLabel.getStyleClass().add("summary-count");

        card.getChildren().addAll(icon, titleLabel, countLabel);
        return card;
    }

    private void loadKosView() {
        contentBox.getChildren().clear();
        List<Kosan> kosanList = getAllKosan();
        for (Kosan kosan : kosanList) {
            contentBox.getChildren().add(createKosCard(kosan));
        }
    }

    private void loadTenantView() {
        contentBox.getChildren().clear();
        List<User> tenantList = getAllUsersByRole("user");
        for (User tenant : tenantList) {
            contentBox.getChildren().add(createUserCard(tenant));
        }
    }

    private void loadUserView() {
        contentBox.getChildren().clear();
        List<User> userList = getAllUsers();
        for (User user : userList) {
            contentBox.getChildren().add(createUserCard(user));
        }
    }

    private List<Kosan> getAllKosan() {
        List<Kosan> kosanList = new ArrayList<>();
        String query = "SELECT * FROM kosan";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
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

    private List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                userList.add(new User(
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at"),
                        rs.getString("profile_image_path")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    private List<User> getAllUsersByRole(String role) {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    userList.add(new User(
                            rs.getInt("id_user"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("phone"),
                            rs.getString("role"),
                            rs.getTimestamp("created_at"),
                            rs.getString("profile_image_path")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    private HBox createKosCard(Kosan kosan) {
        HBox card = new HBox(20);
        card.getStyleClass().add("kos-card");
        card.setPadding(new Insets(15));
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        ImageView imageView = new ImageView();
        try {
            Image image = new Image("file:" + kosan.getPath_gambar(), 80, 80, true, true);
            imageView.setImage(image);
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/com/example/uji_coba/images/logo.png")));
        }

        VBox infoBox = new VBox(5);
        Label nameLabel = new Label(kosan.getNama_kos());
        nameLabel.getStyleClass().add("kos-name");

        Label addressLabel = new Label(kosan.getAlamat());
        Label statusLabel = new Label(kosan.getJenis_kos()); // Or any other relevant status
        statusLabel.getStyleClass().add("status-label");

        infoBox.getChildren().addAll(nameLabel, addressLabel, statusLabel);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        HBox.setHgrow(buttonBox, javafx.scene.layout.Priority.ALWAYS);

        Button detailButton = new Button("Detail");
        detailButton.getStyleClass().add("detail-button");
        detailButton.setOnAction(e -> showKosDetail(kosan));

        Button deleteButton = new Button("Hapus");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> deleteKos(kosan));

        buttonBox.getChildren().addAll(detailButton, deleteButton);
        card.getChildren().addAll(imageView, infoBox, buttonBox);
        return card;
    }

    private HBox createUserCard(User user) {
        HBox card = new HBox(20);
        card.getStyleClass().add("user-card");
        card.setPadding(new Insets(15));
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/example/uji_coba/images/logo.png")));
        imageView.setFitHeight(60);
        imageView.setFitWidth(60);

        VBox infoBox = new VBox(5);
        Label usernameLabel = new Label(user.getUsername());
        usernameLabel.getStyleClass().add("user-name");

        Label roleLabel = new Label("Role: " + user.getRole());
        infoBox.getChildren().addAll(usernameLabel, roleLabel);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        HBox.setHgrow(buttonBox, javafx.scene.layout.Priority.ALWAYS);

        Button detailButton = new Button("Detail");
        detailButton.getStyleClass().add("detail-button");
        detailButton.setOnAction(e -> showUserDetail(user));

        Button deleteButton = new Button("Hapus");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> deleteUser(user));

        buttonBox.getChildren().addAll(detailButton, deleteButton);
        card.getChildren().addAll(imageView, infoBox, buttonBox);
        return card;
    }

    private void showKosDetail(Kosan kosan) {
        contentBox.getChildren().clear();
        VBox detailView = new VBox(20);
        detailView.setPadding(new Insets(20));
        detailView.getStyleClass().add("kos-card");

        Label nameLabel = new Label(kosan.getNama_kos());
        nameLabel.getStyleClass().add("kos-name");

        VBox details = new VBox(10);
        details.getChildren().addAll(
            createDetailRow("Pemilik", getUsernameById(kosan.getId_user())),
            createDetailRow("Lokasi", kosan.getAlamat()),
            createDetailRow("Tipe", kosan.getJenis_kos()),
            createDetailRow("Harga", "Rp " + new DecimalFormat("#,###").format(kosan.getHarga()) + "/bulan")
        );

        VBox laporanBox = new VBox(5);
        Label laporanHeader = new Label("Laporan");
        laporanHeader.setFont(new Font("System Bold", 16));
        Label laporanContent = new Label("Belum ada laporan");
        laporanBox.getChildren().addAll(laporanHeader, laporanContent);

        Button deleteButton = new Button("Hapus");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> deleteKos(kosan));

        detailView.getChildren().addAll(nameLabel, details, laporanBox, deleteButton);
        contentBox.getChildren().add(detailView);
    }

    private void deleteKos(Kosan kosan) {
        if (showConfirmationAlert("Hapus Kos", "Apakah Anda yakin ingin menghapus kos '" + kosan.getNama_kos() + "'? Semua data sewa terkait juga akan dihapus.")) {
            try (Connection conn = DatabaseUtil.getConnection()) {
                conn.setAutoCommit(false);
                
                try {
                    // First delete related records in sewa table
                    String deleteSewaQuery = "DELETE FROM sewa WHERE id_kos = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteSewaQuery)) {
                        stmt.setInt(1, kosan.getId_kos());
                        stmt.executeUpdate();
                    }
                    
                    // Then delete related records in ulasan table if exists
                    String deleteUlasanQuery = "DELETE FROM ulasan WHERE id_kos = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteUlasanQuery)) {
                        stmt.setInt(1, kosan.getId_kos());
                        stmt.executeUpdate();
                    } catch (SQLException e) {
                        // Ignore if ulasan table doesn't exist or no foreign key
                    }
                    
                    // Finally delete the kos record
                    String deleteKosQuery = "DELETE FROM kosan WHERE id_kos = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteKosQuery)) {
                        stmt.setInt(1, kosan.getId_kos());
                        stmt.executeUpdate();
                    }
                    
                    conn.commit();
                    showSuccessAlert("Berhasil", "Kos berhasil dihapus!");
                } catch (SQLException e) {
                    conn.rollback();
                    showErrorAlert("Error", "Gagal menghapus kos: " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                showErrorAlert("Error", "Gagal terhubung ke database: " + e.getMessage());
                e.printStackTrace();
            }
            loadKosView();
        }
    }

    private void showUserDetail(User user) {
        contentBox.getChildren().clear();
        VBox detailView = new VBox(20);
        detailView.setPadding(new Insets(20));
        detailView.getStyleClass().add("user-card");

        Label nameLabel = new Label(user.getUsername());
        nameLabel.getStyleClass().add("user-name");

        VBox details = new VBox(10);
        details.getChildren().addAll(
            createDetailRow("Username", user.getUsername()),
            createDetailRow("No. HP", user.getPhone()),
            createDetailRow("Role", user.getRole())
        );

        Button deleteButton = new Button("Hapus");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> deleteUser(user));

        detailView.getChildren().addAll(nameLabel, details, deleteButton);
        contentBox.getChildren().add(detailView);
    }

    private void deleteUser(User user) {
        if (showConfirmationAlert("Hapus Pengguna", "Apakah Anda yakin ingin menghapus pengguna '" + user.getUsername() + "'? Semua data sewa dan kos terkait juga akan dihapus.")) {
            try (Connection conn = DatabaseUtil.getConnection()) {
                conn.setAutoCommit(false);
                
                try {
                    // First delete related records in sewa table where user is the tenant
                    String deleteSewaQuery = "DELETE FROM sewa WHERE id_user = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteSewaQuery)) {
                        stmt.setInt(1, user.getId());
                        stmt.executeUpdate();
                    }
                    
                    // Delete related records in ulasan table if exists
                    String deleteUlasanQuery = "DELETE FROM ulasan WHERE id_user = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteUlasanQuery)) {
                        stmt.setInt(1, user.getId());
                        stmt.executeUpdate();
                    } catch (SQLException e) {
                        // Ignore if ulasan table doesn't exist or no foreign key
                    }
                    
                    // Delete kos owned by this user (and their related sewa records)
                    String getKosQuery = "SELECT id_kos FROM kosan WHERE id_user = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(getKosQuery)) {
                        stmt.setInt(1, user.getId());
                        try (ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                int kosId = rs.getInt("id_kos");
                                
                                // Delete sewa records for this kos
                                String deleteKosSewaQuery = "DELETE FROM sewa WHERE id_kos = ?";
                                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteKosSewaQuery)) {
                                    deleteStmt.setInt(1, kosId);
                                    deleteStmt.executeUpdate();
                                }
                                
                                // Delete ulasan records for this kos
                                String deleteKosUlasanQuery = "DELETE FROM ulasan WHERE id_kos = ?";
                                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteKosUlasanQuery)) {
                                    deleteStmt.setInt(1, kosId);
                                    deleteStmt.executeUpdate();
                                } catch (SQLException e) {
                                    // Ignore if ulasan table doesn't exist
                                }
                            }
                        }
                    }
                    
                    // Delete kos owned by this user
                    String deleteKosQuery = "DELETE FROM kosan WHERE id_user = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteKosQuery)) {
                        stmt.setInt(1, user.getId());
                        stmt.executeUpdate();
                    }
                    
                    // Finally delete the user record
                    String deleteUserQuery = "DELETE FROM users WHERE id_user = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteUserQuery)) {
                        stmt.setInt(1, user.getId());
                        stmt.executeUpdate();
                    }
                    
                    conn.commit();
                    showSuccessAlert("Berhasil", "Pengguna berhasil dihapus!");
                } catch (SQLException e) {
                    conn.rollback();
                    showErrorAlert("Error", "Gagal menghapus pengguna: " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                showErrorAlert("Error", "Gagal terhubung ke database: " + e.getMessage());
                e.printStackTrace();
            }
            loadUserView();
        }
    }

    private HBox createDetailRow(String label, String value) {
        HBox row = new HBox(10);
        Label labelText = new Label(label + ":");
        labelText.setPrefWidth(100);
        Label valueText = new Label(value);
        row.getChildren().addAll(labelText, valueText);
        return row;
    }

    private String getUsernameById(int userId) {
        String query = "SELECT username FROM users WHERE id_user = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    private boolean showConfirmationAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait().filter(response -> response == javafx.scene.control.ButtonType.OK).isPresent();
    }
    
    private void showSuccessAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showErrorAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}