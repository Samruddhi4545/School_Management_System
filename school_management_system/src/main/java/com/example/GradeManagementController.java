package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.List;

public class GradeManagementController {

    @FXML private ComboBox<Student> studentComboBox;
    // Updated FXML controls for fixed subjects
    @FXML private TextField mathField;
    @FXML private TextField scienceField;
    @FXML private TextField socialField;
    @FXML private TextField englishField;
    @FXML private TextField kannadaField;
    
    @FXML private Button saveButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private SchoolSystem schoolSystem;
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        schoolSystem = new SchoolSystem();
        loadStudentData();

        // Configure the ComboBox to display student names
        studentComboBox.setConverter(new StringConverter<Student>() {
            @Override
            public String toString(Student student) {
                return student == null ? null : student.getName() + " (" + student.getStudentId() + ")";
            }

            @Override
            public Student fromString(String string) {
                return null; // Not needed for a non-editable ComboBox
            }
        });

        studentComboBox.setItems(studentList);
        
        // Add listener to pre-fill scores when a student is selected
        studentComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                prefillGrades(newSelection);
            } else {
                clearGradeFields();
            }
        });
        
        // Add number format validation to fields
        setupGradeFieldValidation(mathField);
        setupGradeFieldValidation(scienceField);
        setupGradeFieldValidation(socialField);
        setupGradeFieldValidation(englishField);
        setupGradeFieldValidation(kannadaField);
    }
    
    /**
     * Loads student data from the database and populates the ComboBox.
     */
    private void loadStudentData() {
        studentList.clear();
        studentList.addAll(schoolSystem.getAllStudents());
    }
    
    /**
     * Fills the grade text fields with the selected student's current scores.
     */
    private void prefillGrades(Student student) {
        mathField.setText(String.valueOf(student.getMathScore()));
        scienceField.setText(String.valueOf(student.getScienceScore()));
        socialField.setText(String.valueOf(student.getSocialScore()));
        englishField.setText(String.valueOf(student.getEnglishScore()));
        kannadaField.setText(String.valueOf(student.getKannadaScore()));
        statusLabel.setText("Scores loaded for " + student.getName() + ". Edit and Save to update.");
    }
    
    /**
     * Clears all grade input fields.
     */
    private void clearGradeFields() {
        mathField.clear();
        scienceField.clear();
        socialField.clear();
        englishField.clear();
        kannadaField.clear();
    }
    
    /**
     * Restricts a TextField to only allow integer input (0-100).
     */
    private void setupGradeFieldValidation(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (!field.getText().isEmpty()) {
                try {
                    int score = Integer.parseInt(field.getText());
                    if (score < 0 || score > 100) {
                        field.setStyle("-fx-border-color: red;");
                    } else {
                        field.setStyle(null);
                    }
                } catch (NumberFormatException e) {
                    field.setStyle("-fx-border-color: red;");
                }
            } else {
                field.setStyle(null);
            }
        });
    }

    /**
     * Handles the saving of all 5 fixed subject grades for the selected student.
     */
    @FXML
    private void handleSaveGrades(ActionEvent event) {
        Student selectedStudent = studentComboBox.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            new Alert(AlertType.ERROR, "Please select a student.").showAndWait();
            statusLabel.setText("Error: No student selected.");
            return;
        }

        List<TextField> fields = Arrays.asList(mathField, scienceField, socialField, englishField, kannadaField);
        int[] scores = new int[5];

        try {
            for (int i = 0; i < 5; i++) {
                String text = fields.get(i).getText();
                if (text == null || text.trim().isEmpty()) {
                    new Alert(AlertType.ERROR, "All 5 grade fields must be filled.").showAndWait();
                    statusLabel.setText("Error: Missing a grade value.");
                    return;
                }
                
                int score = Integer.parseInt(text.trim());
                if (score < 0 || score > 100) {
                    new Alert(AlertType.ERROR, "All grades must be between 0 and 100.").showAndWait();
                    statusLabel.setText("Error: Grade out of range (0-100).");
                    return;
                }
                scores[i] = score;
            }

            // Save all 5 scores in a single DB operation
            schoolSystem.recordGrade(
                selectedStudent.getStudentId(),
                scores[0], scores[1], scores[2], scores[3], scores[4]
            );
            
            // Reload students to update the StudentManagement table view (if it were open)
            // For now, we update the local model for immediate feedback in this view
            selectedStudent.setMathScore(scores[0]);
            selectedStudent.setScienceScore(scores[1]);
            selectedStudent.setSocialScore(scores[3]);
            selectedStudent.setEnglishScore(scores[2]);
            selectedStudent.setKannadaScore(scores[4]);


            statusLabel.setText("Successfully saved all 5 grades for " + selectedStudent.getName() + ".");

        } catch (NumberFormatException e) {
            new Alert(AlertType.ERROR, "All grades must be valid whole numbers.").showAndWait();
            statusLabel.setText("Error: One or more grades is not a number.");
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleBack(ActionEvent event) {
        NavigationManager.switchScene(event, "/com/example/Dashboard.fxml", "School Management System");
    }

    // Public getters for testing (Keep for consistency)
    public Button getSaveButton() { return saveButton; }
    public Button getBackButton() { return backButton; }
    public ObservableList<Student> getStudentList() { return studentList; }
}