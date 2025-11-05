package com.example;

import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.util.StringConverter;

public class AttendanceTrackingController {

    @FXML private ComboBox<Student> studentComboBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML@SuppressWarnings("unused")
    private Button recordButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private SchoolSystem schoolSystem;
    private ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        schoolSystem = new SchoolSystem();
        loadStudentData();

        // Set default date to today
        datePicker.setValue(LocalDate.now());

        // Populate status options
        statusComboBox.setItems(FXCollections.observableArrayList("PRESENT", "ABSENT", "LATE"));

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
    @SuppressWarnings("unused")
    private void handleRecordAttendance(ActionEvent event) {
        Student selectedStudent = studentComboBox.getSelectionModel().getSelectedItem();
        LocalDate date = datePicker.getValue();
        String status = statusComboBox.getSelectionModel().getSelectedItem();

        if (selectedStudent == null || date == null || status == null || status.isEmpty()) {
            new Alert(AlertType.ERROR, "Please select a student, date, and status.", ButtonType.OK).showAndWait();
            statusLabel.setText("Error: Missing information.");
            return;
        }

        schoolSystem.recordAttendance(selectedStudent.getStudentId(), date, status);
        statusLabel.setText("Successfully recorded attendance for " + selectedStudent.getName() + ".");

        // Clear fields for next entry
        studentComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleBack(ActionEvent event) {
        NavigationManager.switchScene(event, "/com/example/Dashboard.fxml", "School Management System");
    }

    public Button getRecordButton() {
        return recordButton;
    }

    public void setRecordButton(Button recordButton) {
        this.recordButton = recordButton;
    }

    public Button getBackButton() {
        return backButton;
    }

    public void setBackButton(Button backButton) {
        this.backButton = backButton;
    }

    public ObservableList<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(ObservableList<Student> studentList) {
        this.studentList = studentList;
    }
}