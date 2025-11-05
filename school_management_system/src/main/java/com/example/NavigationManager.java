package com.example;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * A utility class to handle scene switching and navigation within the JavaFX application.
 * This centralizes navigation logic to avoid code duplication in controllers.
 */
public class NavigationManager {

    /**
     * Switches the current scene to the specified FXML view.
     *
     * @param event The ActionEvent from the control that triggered the navigation (e.g., a button click).
     * @param fxmlFile The path to the FXML file to load (e.g., "/com/example/Dashboard.fxml").
     * @param title The title to set for the new scene.
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static void switchScene(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading view: " + fxmlFile, ButtonType.OK).showAndWait();
        }
    }
}