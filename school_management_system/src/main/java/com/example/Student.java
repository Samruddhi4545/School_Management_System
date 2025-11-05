package com.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Model/Entity class for a Student.
 * MODIFIED to use JavaFX Properties for UI binding.
 */
public class Student {

    // --- JavaFX Properties (Required for TableView binding) ---
    private final StringProperty studentId = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty gradeLevel = new SimpleStringProperty();

    // --- Academic Records (Collections remain the same) ---
    private Map<String, List<Integer>> grades;
    private Map<LocalDate, String> attendanceRecords;

    // =======================================================
    // --- CONSTRUCTORS ---
    // =======================================================

    /**
     * Required default (no-argument) constructor for database loading (JDBC)
     * AND for creating empty objects to be populated via properties.
     */
    public Student() {
        this.grades = new HashMap<>();
        this.attendanceRecords = new HashMap<>();
    }

    /**
     * Standard constructor (Used when adding a student).
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Student(String studentId, String name, String gradeLevel) {
        this(); // Call default constructor to initialize maps
        // Set the property values
        setStudentId(studentId);
        setName(name);
        setGradeLevel(gradeLevel);
    }

    // =======================================================
    // --- GETTERS, SETTERS, and PROPERTY METHODS (CRITICAL FOR JAVAFX) ---
    // =======================================================

    // --- 1. Property Methods (New) ---
    // The TableView columns must use these property methods to bind data.

    public StringProperty studentIdProperty() {
        return studentId;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty gradeLevelProperty() {
        return gradeLevel;
    }

    // --- 2. Standard Getters/Setters (Modified to use properties) ---
    // These methods are needed for JDBC/SchoolSystem interaction.

    public String getStudentId() {
        return studentId.get();
    }

    public void setStudentId(String studentId) {
        this.studentId.set(studentId);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getGradeLevel() {
        return gradeLevel.get();
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel.set(gradeLevel);
    }

    // --- 3. Collection Getters (Unchanged) ---

    public Map<String, List<Integer>> getGrades() {
        return grades;
    }

    public Map<LocalDate, String> getAttendanceRecords() {
        return attendanceRecords;
    }

    // =======================================================
    // --- CORE METHODS (Unchanged) ---
    // =======================================================

    public void addGrade(String subject, int score) {
        this.grades.computeIfAbsent(subject, k -> new ArrayList<>()).add(score);
    }

    public void recordAttendance(LocalDate date, String status) {
        this.attendanceRecords.put(date, status);
    }

    // --- Utility Method (Unchanged) ---

    @Override
    public String toString() {
        return String.format("  Student ID: %s, Name: %s, Grade: %s, Grades Count: %d, Attendance Count: %d",
                getStudentId(), getName(), getGradeLevel(), getGradeCount(), attendanceRecords.size());
    }

    private int getGradeCount() {
        return grades.values().stream().mapToInt(List::size).sum();
    }
}