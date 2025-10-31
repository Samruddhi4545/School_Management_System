package com.example;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class GradeManagementController {

    @FXML private ComboBox<Student> studentComboBox;
    @FXML private TextField subjectField;
    @FXML private TextField gradeField;
    @FXML private Button saveButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private SchoolSystem schoolSystem;
    private ObservableList<Student> studentList = FXCollections.observableArrayList();

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
    }

    private void loadStudentData() {
        studentList.setAll(schoolSystem.getAllStudents());
    }

    @FXML
    private void handleSaveGrade(ActionEvent event) {
        Student selectedStudent = studentComboBox.getSelectionModel().getSelectedItem();
        String subject = subjectField.getText();
        String grade = gradeField.getText();

        if (selectedStudent == null || subject.trim().isEmpty() || grade.trim().isEmpty()) {
            new Alert(AlertType.ERROR, "Please select a student and enter both subject and grade.").showAndWait();
            statusLabel.setText("Error: Missing information.");
            return;
        }

        try {
            int score = Integer.parseInt(grade);

            if (score < 0 || score > 100) {
                new Alert(AlertType.ERROR, "Grade must be a number between 0 and 100.").showAndWait();
                statusLabel.setText("Error: Invalid grade value.");
                return;
            }

            schoolSystem.recordGrade(selectedStudent.getStudentId(), subject, score);
            statusLabel.setText("Successfully saved grade for " + selectedStudent.getName() + ".");

            // Clear fields for next entry
            studentComboBox.getSelectionModel().clearSelection();
            subjectField.clear();
            gradeField.clear();
        } catch (NumberFormatException e) {
            new Alert(AlertType.ERROR, "Grade must be a valid number.").showAndWait();
            statusLabel.setText("Error: Grade is not a number.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("School Management System");
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load dashboard.");
        }
    }
}