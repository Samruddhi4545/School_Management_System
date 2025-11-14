package com.example; // Keep your existing package name

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    // Increased WIDTH to accommodate the new columns in Student Management View
    private static final int WIDTH = 1100; 
    private static final int HEIGHT = 650;

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void start(Stage primaryStage) {
        try {
            // 1. Initialize the database before loading the UI
            DatabaseManager.initializeDatabase();
            
            // 2. Load the FXML file for the main application layout (The Dashboard)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Dashboard.fxml"));
            Parent root = loader.load();

            // 3. Set up the primary stage (the main window)
            primaryStage.setTitle("School Management System");
            // Use the new, larger dimensions
            primaryStage.setScene(new Scene(root, WIDTH, HEIGHT)); 
            primaryStage.show();
            
        } catch (IOException e) {
            System.err.println("ERROR: Could not load FXML file. Check path and file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * The main method now simply starts the JavaFX application lifecycle.
     */
    public static void main(String[] args) {
        launch(args);
    }
}