package com.example;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller class for the main Dashboard view.
 * Handles navigation and initial button actions.
 */
public class DashboardController {

    @FXML
    private Label statusLabel; // This links to the Label in the FXML

    @FXML
    private Button manageStudentsButton; // Add fx:id to this button in Dashboard.fxml

    // --- Action Handlers ---

    @FXML
    private void handleManageStudents(ActionEvent event) {
        statusLabel.setText("Opening Student Management...");
        try {
            // Load the student management FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/StudentManagement.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event source (the button)
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Set the new scene on the current stage
            stage.setScene(new Scene(root));
            stage.setTitle("Student Management");
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load student management view.");
        }
    }

    @FXML
    private void handleRecordGrades(ActionEvent event) {
        statusLabel.setText("Opening Grade Recording...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/GradeManagement.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event source (the button)
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Grade Management");
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load grade management view.");
        }
    }

    @FXML
    private void handleTrackAttendance(ActionEvent event) {
        statusLabel.setText("Opening Attendance Tracking...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/AttendanceTracking.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event source (the button)
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Attendance Tracking");
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load attendance tracking view.");
        }
    }
}
