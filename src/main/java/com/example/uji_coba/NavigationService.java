package com.example.uji_coba;

import com.example.uji_coba.KosanDetailController;
import com.example.uji_coba.OrderKosController;
import com.example.uji_coba.Kosan;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationService {

    private final Stage stage;

    public NavigationService(Stage stage) {
        this.stage = stage;
    }

    public Object switchScene(View view) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uji_coba/" + view.getFileName()));
        Parent pane = loader.load();

        Object controller = loader.getController();
        if (controller instanceof Navigatable) {
            ((Navigatable) controller).setNavigationService(this);
        }

        if (controller instanceof UserDashboardController) {
            ((UserDashboardController) controller).refreshKosanList();
        }

        if (stage.getScene() == null) {
            Scene scene = new Scene(pane);
            scene.getStylesheets().add(getClass().getResource("/mamakos_styles.css").toExternalForm());
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(pane);
        }
        return controller;
    }

    public FXMLLoader switchScene(View view, Kosan kosan) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uji_coba/" + view.getFileName()));
        Parent pane = loader.load();

        Object controller = loader.getController();
        if (controller instanceof Navigatable) {
            ((Navigatable) controller).setNavigationService(this);
        }
        
        if (view == View.ORDER_KOS) {
            OrderKosController orderKosController = (OrderKosController) controller;
            orderKosController.initData(kosan);
        } else if (view == View.KOSAN_DETAIL) {
            KosanDetailController kosanDetailController = (KosanDetailController) controller;
            kosanDetailController.setKosan(kosan);
        }


        if (stage.getScene() == null) {
            Scene scene = new Scene(pane);
            scene.getStylesheets().add(getClass().getResource("/mamakos_styles.css").toExternalForm());
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(pane);
        }
        return loader;
    }
    
    public FXMLLoader switchScene(View view, Order order) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uji_coba/" + view.getFileName()));
        Parent pane = loader.load();

        Object controller = loader.getController();
        if (controller instanceof Navigatable) {
            ((Navigatable) controller).setNavigationService(this);
        }
        
        if (view == View.ULASAN) {
            UlasanController ulasanController = (UlasanController) controller;
            ulasanController.setOrder(order);
        }

        if (stage.getScene() == null) {
            Scene scene = new Scene(pane);
            scene.getStylesheets().add(getClass().getResource("/mamakos_styles.css").toExternalForm());
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(pane);
        }
        return loader;
    }
}