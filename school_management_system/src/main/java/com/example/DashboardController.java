package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller class for the main Dashboard view.
 * Handles navigation and initial button actions.
 */
public class DashboardController {

    @FXML
    private Label statusLabel; // This links to the Label in the FXML

    @FXML
    private void handleManageStudents(ActionEvent event) {
        statusLabel.setText("Opening Student Management...");
        NavigationManager.switchScene(event, "/com/example/StudentManagement.fxml", "Student Management");
    }

    @FXML
    private void handleRecordGrades(ActionEvent event) {
        statusLabel.setText("Opening Grade Recording...");
        NavigationManager.switchScene(event, "/com/example/GradeManagement.fxml", "Grade Management");
    }

    @FXML
    private void handleTrackAttendance(ActionEvent event) {
        statusLabel.setText("Opening Attendance Tracking...");
        NavigationManager.switchScene(event, "/com/example/AttendanceTracking.fxml", "Attendance Tracking");
    }
}
