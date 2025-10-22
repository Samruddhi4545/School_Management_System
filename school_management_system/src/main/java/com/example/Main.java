package com.example; // Keep your existing package name

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application { // ⬅️ CRITICAL CHANGE: Extend Application

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Initialize the database before loading the UI
            DatabaseManager.initializeDatabase();
            
            // 2. Load the FXML file for the main application layout (The Dashboard)
            // ⬇️ NOTE: Path is corrected based on your screenshot structure ⬇️
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Dashboard.fxml"));
            Parent root = loader.load();

            // 3. Set up the primary stage (the main window)
            primaryStage.setTitle("School Management System");
            primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
            primaryStage.show();
            
        } catch (IOException e) {
            System.err.println("❌ ERROR: Could not load FXML file. Check path and file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * The main method now simply starts the JavaFX application lifecycle.
     */
    public static void main(String[] args) {
        // ⬅️ CRITICAL CHANGE: Calls the JavaFX launcher method
        launch(args);
        
        // The old CLI code (printMenu, scanner logic, etc.) is removed
        // because the GUI now handles all user interaction.
    }
}