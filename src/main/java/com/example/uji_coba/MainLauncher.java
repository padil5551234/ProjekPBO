package com.example.uji_coba;

import com.example.uji_coba.Main;
import javafx.application.Application;

public class MainLauncher {
    public static void main(String[] args) {
        // Run database setup before launching the application
        DatabaseSetup.createUlasanTable();
        DatabaseSetup.addDurasiColumnToOrders();
        
        Application.launch(Main.class, args);
    }
}