package com.example;

import java.text.DecimalFormat;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class GradeSummaryReportController {

    @FXML private TableView<GradeSummaryEntry> reportTableView;
    
    // Columns bound to GradeSummaryEntry properties
    @FXML private TableColumn<GradeSummaryEntry, String> studentIdColumn;
    @FXML private TableColumn<GradeSummaryEntry, String> studentNameColumn;
    @FXML private TableColumn<GradeSummaryEntry, Double> overallAverageColumn;
    
    @FXML private Label statusLabel;
    
    private SchoolSystem schoolSystem = new SchoolSystem();

    @FXML
    public void initialize() {
        configureTableColumns();
        loadReportData();
    }
    
    private void configureTableColumns() {
        // Bind columns to the property methods of the GradeSummaryEntry class
        studentIdColumn.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty());
        studentNameColumn.setCellValueFactory(cellData -> cellData.getValue().studentNameProperty());
        overallAverageColumn.setCellValueFactory(cellData -> cellData.getValue().overallAverageProperty().asObject());

        // Format the average to two decimal places for display
        DecimalFormat df = new DecimalFormat("0.00");
        overallAverageColumn.setCellFactory(column -> new TableCell<GradeSummaryEntry, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(df.format(item));
                }
            }
        });
    }

    private void loadReportData() {
        // Fetch the summary records for ALL students
        List<GradeSummaryEntry> records = schoolSystem.getGradeSummaryForAll();
        
        // Populate the TableView
        ObservableList<GradeSummaryEntry> reportEntries = FXCollections.observableArrayList(records);
        reportTableView.setItems(reportEntries);
        
        statusLabel.setText("Grade summary loaded for " + records.size() + " students.");
    }
    
    @FXML
    @SuppressWarnings("unused")
    private void handleBack(ActionEvent event) {
        NavigationManager.switchScene(event, "/com/example/Dashboard.fxml", "School Management System");
    }
}