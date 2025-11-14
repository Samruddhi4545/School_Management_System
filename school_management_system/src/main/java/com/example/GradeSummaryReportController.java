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
    
    // Existing fixed columns
    @FXML private TableColumn<GradeSummaryEntry, String> studentIdColumn;
    @FXML private TableColumn<GradeSummaryEntry, String> studentNameColumn;
    
    // NEW SUBJECT COLUMNS ADDED HERE
    @FXML private TableColumn<GradeSummaryEntry, Integer> mathScoreColumn;
    @FXML private TableColumn<GradeSummaryEntry, Integer> scienceScoreColumn;
    @FXML private TableColumn<GradeSummaryEntry, Integer> socialScoreColumn; // History/Social
    @FXML private TableColumn<GradeSummaryEntry, Integer> englishScoreColumn;
    @FXML private TableColumn<GradeSummaryEntry, Integer> kannadaScoreColumn; // Art/Kannada
    // END NEW SUBJECT COLUMNS
    
    @FXML private TableColumn<GradeSummaryEntry, Double> overallAverageColumn;
    
    @FXML private Label statusLabel;
    
    private SchoolSystem schoolSystem = new SchoolSystem();

    @FXML
    public void initialize() {
        configureTableColumns();
        loadReportData();
    }
    
    private void configureTableColumns() {
        // Bind core columns
        studentIdColumn.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty());
        studentNameColumn.setCellValueFactory(cellData -> cellData.getValue().studentNameProperty());

        // Bind NEW subject columns to Integer properties
        // .asObject() is necessary to convert IntegerProperty to the generic <..., Integer> type
        mathScoreColumn.setCellValueFactory(cellData -> cellData.getValue().mathScoreProperty().asObject());
        scienceScoreColumn.setCellValueFactory(cellData -> cellData.getValue().scienceScoreProperty().asObject());
        socialScoreColumn.setCellValueFactory(cellData -> cellData.getValue().socialScoreProperty().asObject());
        englishScoreColumn.setCellValueFactory(cellData -> cellData.getValue().englishScoreProperty().asObject());
        kannadaScoreColumn.setCellValueFactory(cellData -> cellData.getValue().kannadaScoreProperty().asObject());

        // Bind average column
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