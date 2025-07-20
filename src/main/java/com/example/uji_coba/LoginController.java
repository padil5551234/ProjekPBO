package com.example.uji_coba;

import com.example.uji_coba.User;
import com.example.uji_coba.DatabaseUtil;
import com.example.uji_coba.PasswordUtil;
import com.example.uji_coba.SessionManager;
import com.example.uji_coba.Navigatable;
import com.example.uji_coba.NavigationService;
import com.example.uji_coba.View;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class LoginController implements Navigatable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private CheckBox rememberMeCheckBox;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink signUpLink;
    @FXML
    private Label errorLabel;

    private NavigationService navigationService;
    private Preferences prefs;

    public void initialize() {
        prefs = Preferences.userNodeForPackage(LoginController.class);
        usernameField.setText(prefs.get("username", ""));
        if (!usernameField.getText().isEmpty()) {
            rememberMeCheckBox.setSelected(true);
        }
        errorLabel.setText(""); // Clear error label on init
    }

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @FXML
    private void handleSignUpLink(ActionEvent event) throws IOException {
        navigationService.switchScene(View.REGISTRATION);
    }

    @FXML
    private void handleLoginButton(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username dan password harus diisi.");
            return;
        }

        loginButton.setDisable(true);
        errorLabel.setText("Mencoba masuk...");

        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                try (Connection conn = DatabaseUtil.getConnection()) {
                    String query = "SELECT * FROM users WHERE username = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, username);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String storedHashedPassword = rs.getString("password");
                        if (PasswordUtil.checkPassword(password, storedHashedPassword)) {
                            return new User(
                                    rs.getInt("id_user"),
                                    rs.getString("username"),
                                    storedHashedPassword,
                                    rs.getString("phone"),
                                    rs.getString("role"),
                                    rs.getTimestamp("created_at"),
                                    rs.getString("profile_image_path")
                            );
                        }
                    }
                }
                return null; // Return null if login fails
            }
        };

        loginTask.setOnSucceeded(workerStateEvent -> {
            User user = loginTask.getValue();
            if (user != null) {
                SessionManager.getInstance().setCurrentUser(user);

                if (rememberMeCheckBox.isSelected()) {
                    prefs.put("username", username);
                } else {
                    prefs.remove("username");
                }

                try {
                    switch (user.getRole()) {
                        case "seller":
                            navigationService.switchScene(View.SELLER_DASHBOARD);
                            break;
                        case "user":
                            navigationService.switchScene(View.USER_DASHBOARD);
                            break;
                        case "admin":
                            navigationService.switchScene(View.ADMIN_DASHBOARD);
                            break;
                    }
                } catch (IOException e) {
                    errorLabel.setText("Gagal memuat halaman selanjutnya.");
                }
            } else {
                errorLabel.setText("Username atau password salah.");
            }
            loginButton.setDisable(false);
        });

        loginTask.setOnFailed(workerStateEvent -> {
            errorLabel.setText("Terjadi kesalahan pada database.");
            loginButton.setDisable(false);
        });

        new Thread(loginTask).start();
    }
}