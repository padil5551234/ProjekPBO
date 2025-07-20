package com.example.uji_coba;

import com.example.uji_coba.User;
import com.example.uji_coba.DatabaseUtil;
import com.example.uji_coba.SessionManager;
import com.example.uji_coba.Navigatable;
import com.example.uji_coba.NavigationService;
import com.example.uji_coba.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserProfileController implements Navigatable {

    @FXML
    private ImageView profileImageView;

    @FXML
    private Button changeProfileImageButton;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    private User currentUser;
    private NavigationService navigationService;
    private String newProfileImagePath;

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
            phoneField.setText(currentUser.getPhone());
            String profileImagePath = currentUser.getProfileImagePath();
            if (profileImagePath != null && !profileImagePath.isEmpty()) {
                File file = new File(profileImagePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    profileImageView.setImage(image);
                }
            }
            // Make the profile image circular
            Circle clip = new Circle(profileImageView.getFitWidth() / 2, profileImageView.getFitHeight() / 2, profileImageView.getFitWidth() / 2);
            profileImageView.setClip(clip);
        }
    }

    @FXML
    void handleChangeProfileImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(changeProfileImageButton.getScene().getWindow());
        if (selectedFile != null) {
            newProfileImagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            profileImageView.setImage(image);
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        String newUsername = usernameField.getText();
        String newPhone = phoneField.getText();
        String newPassword = passwordField.getText();

        // Update logic here
        String sql = "UPDATE users SET username = ?, phone = ?";
        if (newPassword != null && !newPassword.isEmpty()) {
            sql += ", password = ?";
        }
        if (newProfileImagePath != null && !newProfileImagePath.isEmpty()) {
            sql += ", profile_image_path = ?";
        }
        sql += " WHERE id_user = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newUsername);
            pstmt.setString(2, newPhone);
            int paramIndex = 3;
            if (newPassword != null && !newPassword.isEmpty()) {
                pstmt.setString(paramIndex++, newPassword);
            }
            if (newProfileImagePath != null && !newProfileImagePath.isEmpty()) {
                pstmt.setString(paramIndex++, newProfileImagePath);
            }
            pstmt.setInt(paramIndex, currentUser.getId());

            pstmt.executeUpdate();

            // Update session
            currentUser.setUsername(newUsername);
            currentUser.setPhone(newPhone);
            if (newProfileImagePath != null && !newProfileImagePath.isEmpty()) {
                currentUser.setProfileImagePath(newProfileImagePath);
            }
            SessionManager.getInstance().setCurrentUser(currentUser);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            if (currentUser.getRole().equals("user")) {
                navigationService.switchScene(View.USER_DASHBOARD);
            } else if (currentUser.getRole().equals("seller")) {
                navigationService.switchScene(View.SELLER_DASHBOARD);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}