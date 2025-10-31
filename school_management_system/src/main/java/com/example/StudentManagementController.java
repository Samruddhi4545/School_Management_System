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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class StudentManagementController {

    @FXML private TableView<Student> studentTableView;
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> gradeLevelColumn;
    @FXML private TextField studentIdField;
    @FXML private TextField nameField;
    @FXML private TextField gradeLevelField;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private SchoolSystem schoolSystem;
    private ObservableList<Student> studentList = FXCollections.observableArrayList();

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        schoolSystem = new SchoolSystem();

        // Set up the columns in the table
        idColumn.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        gradeLevelColumn.setCellValueFactory(cellData -> cellData.getValue().gradeLevelProperty());

        // Load student data
        loadStudentData();

        // Add listener for table selection changes
        studentTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showStudentDetails(newValue));
    }

    private void loadStudentData() {
        studentList.setAll(schoolSystem.getAllStudents());
        studentTableView.setItems(studentList);
        statusLabel.setText("Loaded " + studentList.size() + " students.");
    }

    private void showStudentDetails(Student student) {
        if (student != null) {
            studentIdField.setText(student.getStudentId());
            nameField.setText(student.getName());
            gradeLevelField.setText(student.getGradeLevel());
            studentIdField.setEditable(false); // ID should not be changed
        } else {
            // Clear fields if no student is selected
            studentIdField.clear();
            nameField.clear();
            gradeLevelField.clear();
            studentIdField.setEditable(true);
        }
    }

    @FXML
    private void handleAddStudent(ActionEvent event) {
        String studentId = studentIdField.getText();
        String name = nameField.getText();
        String gradeLevel = gradeLevelField.getText();

        if (studentId.isEmpty() || name.isEmpty() || gradeLevel.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR, "All fields must be filled.", ButtonType.OK);
            alert.showAndWait();
            statusLabel.setText("Error: All fields are required.");
            return;
        }

        // Optional: Check if student ID already exists
        if (schoolSystem.findStudentById(studentId) != null) {
            Alert alert = new Alert(AlertType.ERROR, "A student with this ID already exists.", ButtonType.OK);
            alert.showAndWait();
            statusLabel.setText("Error: Student ID already exists.");
            return;
        }

        Student newStudent = new Student(studentId, name, gradeLevel);
        schoolSystem.addStudent(newStudent);

        // --- IMPROVEMENT: Refresh table and select the new student ---
        // 1. Refresh the underlying list with new data
        studentList.setAll(schoolSystem.getAllStudents());

        // 2. Find the newly added student in the list
        // We can do this by streaming and filtering, or a simple loop.
        Student studentToSelect = studentList.stream()
            .filter(s -> s.getStudentId().equals(studentId))
            .findFirst()
            .orElse(null); // Should always be found

        // 3. Select and scroll to the new student in the TableView
        studentTableView.getSelectionModel().select(studentToSelect);
        studentTableView.scrollTo(studentToSelect);

        statusLabel.setText("Successfully added student: " + name);
    }

    @FXML
    private void handleUpdateStudent(ActionEvent event) {
        Student selectedStudent = studentTableView.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            Alert alert = new Alert(AlertType.WARNING, "No student selected. Please select a student from the table to update.", ButtonType.OK);
            alert.showAndWait();
            statusLabel.setText("Warning: No student selected for update.");
            return;
        }

        String name = nameField.getText();
        String gradeLevel = gradeLevelField.getText();

        if (name.isEmpty() || gradeLevel.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR, "Name and Grade Level fields cannot be empty.", ButtonType.OK);
            alert.showAndWait();
            statusLabel.setText("Error: Required fields are empty.");
            return;
        }

        Student updatedStudent = new Student(selectedStudent.getStudentId(), name, gradeLevel);
        schoolSystem.updateStudent(updatedStudent);

        loadStudentData(); // Refresh the table
        statusLabel.setText("Successfully updated student: " + name);
    }

    @FXML
    private void handleDeleteStudent(ActionEvent event) {
        Student selectedStudent = studentTableView.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            Alert alert = new Alert(AlertType.WARNING, "No student selected. Please select a student to delete.", ButtonType.OK);
            alert.showAndWait();
            statusLabel.setText("Warning: No student selected for deletion.");
            return;
        }

        Alert confirmation = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete " + selectedStudent.getName() + "?", ButtonType.YES, ButtonType.NO);
        confirmation.setHeaderText("Confirm Deletion");
        confirmation.showAndWait();

        if (confirmation.getResult() == ButtonType.YES) {
            schoolSystem.deleteStudent(selectedStudent.getStudentId());
            loadStudentData(); // Refresh the table
            statusLabel.setText("Successfully deleted student: " + selectedStudent.getName());
        }
    }

    /**
     * Handles the action of clicking the 'Back to Dashboard' button.
     * Navigates the user back to the main dashboard view.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Dashboard.fxml"));
            Parent root = loader.load();

            // Get the current stage from the button and set the new scene
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("School Management System");

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load dashboard.");
            Alert alert = new Alert(AlertType.ERROR, "Failed to load the dashboard view.", ButtonType.OK);
            alert.showAndWait();
        }
    }
}