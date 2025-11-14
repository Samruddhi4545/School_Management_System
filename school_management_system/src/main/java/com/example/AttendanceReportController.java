package com.example;

import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class AttendanceReportController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    
    @FXML private TableView<AttendanceReportEntry> reportTableView;
    
    // Columns bound to AttendanceReportEntry properties
    @FXML private TableColumn<AttendanceReportEntry, String> studentIdColumn;
    @FXML private TableColumn<AttendanceReportEntry, String> studentNameColumn;
    @FXML private TableColumn<AttendanceReportEntry, LocalDate> dateColumn;
    @FXML private TableColumn<AttendanceReportEntry, String> statusColumn;
    
    @FXML private Label statusLabel;
    
    private SchoolSystem schoolSystem = new SchoolSystem();

    @FXML
    public void initialize() {
        configureTableColumns();
        // Optional: Set a default date range
        endDatePicker.setValue(LocalDate.now());
        startDatePicker.setValue(LocalDate.now().minusDays(30)); 
    }
    
    private void configureTableColumns() {
        // Bind columns to the property methods of the AttendanceReportEntry class
        studentIdColumn.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty());
        studentNameColumn.setCellValueFactory(cellData -> cellData.getValue().studentNameProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
    }


    @FXML
    private void handleGenerateReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            statusLabel.setText("Error: Select a start date and end date.");
            new Alert(AlertType.ERROR, "Please select both a start date and an end date.").showAndWait();
            return;
        }
        
        if (startDate.isAfter(endDate)) {
            statusLabel.setText("Error: Start date cannot be after end date.");
            new Alert(AlertType.ERROR, "The start date must be before or equal to the end date.").showAndWait();
            return;
        }

        // Fetch records for ALL students
        List<AttendanceReportEntry> records = schoolSystem.getAttendanceReportForAll(startDate, endDate);
        
        // Populate the TableView
        ObservableList<AttendanceReportEntry> reportEntries = FXCollections.observableArrayList(records);
        reportTableView.setItems(reportEntries);
        
        statusLabel.setText("Class attendance report generated: " + records.size() + " total attendance records found between " + startDate + " and " + endDate + ".");
    }
    
    @FXML
    @SuppressWarnings("unused")
    private void handleBack(ActionEvent event) {
        NavigationManager.switchScene(event, "/com/example/Dashboard.fxml", "School Management System");
    }
}
