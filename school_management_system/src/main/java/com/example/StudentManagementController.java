package com.example;

import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class StudentManagementController {

    @FXML private TableView<Student> studentTableView;
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> gradeLevelColumn;
    
    // New Fixed Grade Columns
    @FXML private TableColumn<Student, Integer> mathColumn;
    @FXML private TableColumn<Student, Integer> scienceColumn;
    @FXML private TableColumn<Student, Integer> socialColumn;
    @FXML private TableColumn<Student, Integer> englishColumn;
    @FXML private TableColumn<Student, Integer> kannadaColumn;
    
    // New Calculated Grade Columns
    @FXML private TableColumn<Student, Integer> totalGradeColumn;
    @FXML private TableColumn<Student, Double> averageGradeColumn;

    @FXML private TextField studentIdField;
    @FXML private TextField nameField;
    @FXML private TextField gradeLevelField;
    @FXML@SuppressWarnings("unused")
    private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private SchoolSystem schoolSystem;
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        schoolSystem = new SchoolSystem();

        // 1. Configure Table Columns (Cell Value Factories)
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        gradeLevelColumn.setCellValueFactory(new PropertyValueFactory<>("gradeLevel"));
        
        // Configure new fixed grade columns to link to Student properties
        mathColumn.setCellValueFactory(new PropertyValueFactory<>("mathScore"));
        scienceColumn.setCellValueFactory(new PropertyValueFactory<>("scienceScore"));
        socialColumn.setCellValueFactory(new PropertyValueFactory<>("socialScore"));
        englishColumn.setCellValueFactory(new PropertyValueFactory<>("englishScore"));
        kannadaColumn.setCellValueFactory(new PropertyValueFactory<>("kannadaScore"));
        
        // Configure new calculated columns to link to Student properties
        totalGradeColumn.setCellValueFactory(new PropertyValueFactory<>("totalGrade"));
        averageGradeColumn.setCellValueFactory(new PropertyValueFactory<>("averageGrade"));

        // 2. Load Data
        loadStudentData();
        studentTableView.setItems(studentList);

        // 3. Setup Listener for Table Selection
        studentTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showStudentDetails(newSelection);
            } else {
                clearStudentDetails();
            }
        });
    }

    /**
     * Loads student data from the SchoolSystem into the ObservableList.
     */
    private void loadStudentData() {
        studentList.clear();
        studentList.addAll(schoolSystem.getAllStudents());
    }

    /**
     * Populates the detail fields with the selected student's information.
     */
    private void showStudentDetails(Student student) {
        studentIdField.setText(student.getStudentId());
        nameField.setText(student.getName());
        gradeLevelField.setText(student.getGradeLevel());

        // Disable ID field for update to prevent changing the key
        studentIdField.setDisable(true); 
    }

    /**
     * Clears the detail fields.
     */
    private void clearStudentDetails() {
        studentIdField.setText(null);
        nameField.setText(null);
        gradeLevelField.setText(null);
        studentIdField.setDisable(false); // Enable ID field for new entry
        studentTableView.getSelectionModel().clearSelection();
    }

    /**
     * Handles adding a new student to the system.
     */
    @FXML
    private void handleAddStudent(ActionEvent event) {
        String id = studentIdField.getText();
        String name = nameField.getText();
        String gradeLevel = gradeLevelField.getText();

        if (id == null || id.trim().isEmpty() || name == null || name.trim().isEmpty() || gradeLevel == null || gradeLevel.trim().isEmpty()) {
            new Alert(AlertType.ERROR, "Please fill in all student details.").showAndWait();
            statusLabel.setText("Error: Missing student information.");
            return;
        }

        Student newStudent = new Student(id.trim(), name.trim(), gradeLevel.trim());
        
        // Check if student already exists before adding
        if (schoolSystem.findStudentById(id.trim()) != null) {
            new Alert(AlertType.ERROR, "Student with ID " + id + " already exists.").showAndWait();
            statusLabel.setText("Error: Student ID already in use.");
            return;
        }

        schoolSystem.addStudent(newStudent);
        studentList.add(newStudent); // Add to the ObservableList to update the TableView
        clearStudentDetails();
        statusLabel.setText("Successfully added new student: " + newStudent.getName());
    }

    /**
     * Handles updating the details of the currently selected student.
     */
    @FXML
    private void handleUpdateStudent(ActionEvent event) {
        Student selectedStudent = studentTableView.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            new Alert(AlertType.ERROR, "Please select a student to update.").showAndWait();
            statusLabel.setText("Error: No student selected for update.");
            return;
        }

        String name = nameField.getText();
        String gradeLevel = gradeLevelField.getText();
        
        if (name == null || name.trim().isEmpty() || gradeLevel == null || gradeLevel.trim().isEmpty()) {
            new Alert(AlertType.ERROR, "Name and Grade Level must be filled.").showAndWait();
            statusLabel.setText("Error: Missing update information.");
            return;
        }
        
        // Update the model object properties
        selectedStudent.setName(name.trim());
        selectedStudent.setGradeLevel(gradeLevel.trim());
        
        // Persist changes to the database
        schoolSystem.updateStudent(selectedStudent);
        
        // Refresh the TableView to show the updated values
        studentTableView.getColumns().get(0).setVisible(false);
        studentTableView.getColumns().get(0).setVisible(true);

        clearStudentDetails();
        statusLabel.setText("Successfully updated student: " + selectedStudent.getName());
    }

    /**
     * Handles deleting the currently selected student.
     */
    @FXML
    private void handleDeleteStudent(ActionEvent event) {
        Student selectedStudent = studentTableView.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            new Alert(AlertType.ERROR, "Please select a student to delete.").showAndWait();
            statusLabel.setText("Error: No student selected for deletion.");
            return;
        }

        // Show confirmation dialog
        Alert confirmation = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete " + selectedStudent.getName() + "?", ButtonType.YES, ButtonType.NO);
        confirmation.setHeaderText("Confirm Deletion");
        
        // The JavaFX Alert returns an Optional<ButtonType>
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            String deletedStudentName = selectedStudent.getName();
            schoolSystem.deleteStudent(selectedStudent.getStudentId());
            
            // Remove the item directly from the list (which updates the TableView)
            studentList.remove(selectedStudent);
            
            clearStudentDetails();
            statusLabel.setText("Successfully deleted student: " + deletedStudentName);
        }
    }

    /**
     * Handles the action of clicking the 'Back to Dashboard' button.
     * Navigates the user back to the main dashboard view.
     */
    @FXML
    @SuppressWarnings("unused")
    private void handleBack(ActionEvent event) {
        NavigationManager.switchScene(event, "/com/example/Dashboard.fxml", "School Management System");
    }

    // Public getters for testing (Keep for consistency)
    public Button getUpdateButton() { return updateButton; }
    public Button getDeleteButton() { return deleteButton; }
    public Button getBackButton() { return backButton; }
}