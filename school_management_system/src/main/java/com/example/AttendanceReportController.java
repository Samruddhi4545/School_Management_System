package com.example;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AttendanceReportController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    
    // TableView is now parameterized with the new model
    @FXML private TableView<AttendanceReportEntry> reportTableView;
    
    // Only ONE fixed column for student name
    @FXML private TableColumn<AttendanceReportEntry, String> studentNameColumn;
    
    @FXML private Label statusLabel;
    
    private SchoolSystem schoolSystem = new SchoolSystem();

    @FXML
    public void initialize() {
        // Set up the fixed column binding
        studentNameColumn.setCellValueFactory(cellData -> cellData.getValue().studentNameProperty());

        // Optional: Set a default date range
        endDatePicker.setValue(LocalDate.now());
        startDatePicker.setValue(LocalDate.now().minusDays(30)); 
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
        
        // 1. Fetch pivoted data using the new method
        List<AttendanceReportEntry> records = schoolSystem.getPivotedAttendanceReport(startDate, endDate);
        
        // 2. Clear previous dynamic columns before adding new ones
        // Remove all columns except the first one (Student Name)
        if (reportTableView.getColumns().size() > 1) {
            reportTableView.getColumns().remove(1, reportTableView.getColumns().size());
        } else if (reportTableView.getColumns().isEmpty()) {
            // Re-add studentNameColumn if it was somehow cleared (safe check)
            reportTableView.getColumns().add(studentNameColumn);
        }

        // 3. Generate the list of dates (columns) in the range
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        List<LocalDate> dateColumns = Stream.iterate(startDate, date -> date.plusDays(1))
                                            .limit(days)
                                            .collect(Collectors.toList());
        
        // 4. Create dynamic columns for each date
        for (LocalDate date : dateColumns) {
            TableColumn<AttendanceReportEntry, String> dateColumn = new TableColumn<>(date.toString());
            dateColumn.setPrefWidth(80);
            
            // Custom CellValueFactory to look up attendance status by date in the entry's map
            dateColumn.setCellValueFactory(cellData -> {
                // Look up status in the map, use "-" if no record is found for that date
                String status = cellData.getValue().getAttendanceData().getOrDefault(date, "-");
                // The SimpleStringProperty is necessary for JavaFX binding
                return new SimpleStringProperty(status);
            });
            
            reportTableView.getColumns().add(dateColumn);
        }

        // 5. Populate the TableView
        ObservableList<AttendanceReportEntry> reportEntries = FXCollections.observableArrayList(records);
        reportTableView.setItems(reportEntries);
        
        statusLabel.setText("Class attendance report generated: " + records.size() + " total students listed across " + days + " days.");
    }
    
    @FXML
    @SuppressWarnings("unused")
    private void handleBack(ActionEvent event) {
        NavigationManager.switchScene(event, "/com/example/Dashboard.fxml", "School Management System");
    }
}