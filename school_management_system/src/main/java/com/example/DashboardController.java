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

    // --- Action Handlers ---

    @FXML
    private void handleManageStudents(ActionEvent event) {
        // TODO: Logic for opening the Student Management view will go here
        statusLabel.setText("Opening Student Management...");
        System.out.println("Manage Students button clicked.");
    }

    @FXML
    private void handleRecordGrades(ActionEvent event) {
        // TODO: Logic for opening the Grade Recording view will go here
        statusLabel.setText("Opening Grade Recording...");
        System.out.println("Record Grades button clicked.");
    }

    @FXML
    private void handleTrackAttendance(ActionEvent event) {
        // TODO: Logic for opening the Attendance view will go here
        statusLabel.setText("Opening Attendance Tracking...");
        System.out.println("Track Attendance button clicked.");
    }
}
