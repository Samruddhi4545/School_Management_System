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
    private Label statusLabel; 

    @FXML
    @SuppressWarnings("unused")
    private void handleManageStudents(ActionEvent event) {
        statusLabel.setText("Opening Student Management...");
        NavigationManager.switchScene(event, "/com/example/StudentManagement.fxml", "Student Management");
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleRecordGrades(ActionEvent event) {
        statusLabel.setText("Opening Grade Recording...");
        NavigationManager.switchScene(event, "/com/example/GradeManagement.fxml", "Grade Management");
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleTrackAttendance(ActionEvent event) {
        statusLabel.setText("Opening Attendance Tracking...");
        NavigationManager.switchScene(event, "/com/example/AttendanceTracking.fxml", "Attendance Tracking");
    }

    // Handler for button 4 (Attendance Report)
    @FXML
    @SuppressWarnings("unused")
    private void handleViewAttendanceReport(ActionEvent event) {
        statusLabel.setText("Opening Attendance Report...");
        NavigationManager.switchScene(event, "/com/example/AttendanceReport.fxml", "Class Attendance Report");
    }
    
    // Handler for button 5 (Grade Summary Report)
    @FXML
    @SuppressWarnings("unused")
    private void handleViewGradeSummaryReport(ActionEvent event) {
        statusLabel.setText("Opening Grade Summary Report...");
        NavigationManager.switchScene(event, "/com/example/GradeSummaryReport.fxml", "Grade Summary Report");
    }
}