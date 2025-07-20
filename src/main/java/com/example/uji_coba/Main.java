package com.example.uji_coba;

import com.example.uji_coba.NavigationService;
import com.example.uji_coba.View;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        NavigationService navigationService = new NavigationService(stage);
        stage.setTitle("Mamikos - Cari Kosan Mudah");
        
        // Set more reasonable initial size
        stage.setWidth(1000);
        stage.setHeight(650);
        
        // Set minimum size to prevent content from being cut off
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        
        // Make window resizable for better responsiveness
        stage.setResizable(true);
        
        navigationService.switchScene(View.LOGIN);
        stage.show();
    }
}