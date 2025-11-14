package com.example;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Model/Entity class for a Student.
 * UPDATED to use fixed subject scores and calculate total/average grades.
 */
public class Student {

    // --- JavaFX Properties (Required for TableView binding) ---
    private final StringProperty studentId = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty gradeLevel = new SimpleStringProperty();

    // --- Fixed Subject Grade Properties (New) ---
    private final IntegerProperty mathScore = new SimpleIntegerProperty(0);
    private final IntegerProperty scienceScore = new SimpleIntegerProperty(0);
    private final IntegerProperty socialScore = new SimpleIntegerProperty(0);
    private final IntegerProperty englishScore = new SimpleIntegerProperty(0);
    private final IntegerProperty kannadaScore = new SimpleIntegerProperty(0);

    // --- Calculated Grade Properties (New) ---
    private final ReadOnlyIntegerWrapper totalGrade = new ReadOnlyIntegerWrapper(0);
    private final ReadOnlyDoubleWrapper averageGrade = new ReadOnlyDoubleWrapper(0.0);

    // --- Academic Records (Attendance remains the same) ---
    private Map<LocalDate, String> attendanceRecords;


    // =======================================================
    // --- CONSTRUCTORS ---
    // =======================================================

    public Student() {
        // Grades map is no longer needed, replaced by fixed properties
        this.attendanceRecords = new HashMap<>();
        recalculateGrades(); // Initialize calculated properties
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Student(String studentId, String name, String gradeLevel) {
        this();
        this.studentId.set(studentId);
        this.name.set(name);
        this.gradeLevel.set(gradeLevel);
    }

    // =======================================================
    // --- CORE PROPERTY GETTERS/SETTERS (STANDARD) ---
    // =======================================================

    public String getStudentId() { return studentId.get(); }
    public StringProperty studentIdProperty() { return studentId; }
    public void setStudentId(String studentId) { this.studentId.set(studentId); }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public void setName(String name) { this.name.set(name); }

    public String getGradeLevel() { return gradeLevel.get(); }
    public StringProperty gradeLevelProperty() { return gradeLevel; }
    public void setGradeLevel(String gradeLevel) { this.gradeLevel.set(gradeLevel); }

    // =======================================================
    // --- FIXED SCORE GETTERS/SETTERS/PROPERTIES (NEW) ---
    // =======================================================

    public int getMathScore() { return mathScore.get(); }
    public IntegerProperty mathScoreProperty() { return mathScore; }
    public void setMathScore(int mathScore) {
        this.mathScore.set(mathScore);
        recalculateGrades();
    }

    public int getScienceScore() { return scienceScore.get(); }
    public IntegerProperty scienceScoreProperty() { return scienceScore; }
    public void setScienceScore(int scienceScore) {
        this.scienceScore.set(scienceScore);
        recalculateGrades();
    }

    public int getSocialScore() { return socialScore.get(); }
    public IntegerProperty socialScoreProperty() { return socialScore; }
    public void setSocialScore(int socialScore) {
        this.socialScore.set(socialScore);
        recalculateGrades();
    }

    public int getEnglishScore() { return englishScore.get(); }
    public IntegerProperty englishScoreProperty() { return englishScore; }
    public void setEnglishScore(int englishScore) {
        this.englishScore.set(englishScore);
        recalculateGrades();
    }

    public int getKannadaScore() { return kannadaScore.get(); }
    public IntegerProperty kannadaScoreProperty() { return kannadaScore; }
    public void setKannadaScore(int kannadaScore) {
        this.kannadaScore.set(kannadaScore);
        recalculateGrades();
    }

    // =======================================================
    // --- CALCULATED GRADE GETTERS/PROPERTIES (NEW) ---
    // =======================================================

    public int getTotalGrade() { return totalGrade.get(); }
    public IntegerProperty totalGradeProperty() { return totalGrade; }

    public double getAverageGrade() { return averageGrade.get(); }
    public DoubleProperty averageGradeProperty() { return averageGrade; }

    /**
     * Recalculates the Total Grade and Average Grade based on the 5 subject scores.
     */
    private void recalculateGrades() {
        int total = getMathScore() + getScienceScore() + getSocialScore() + getEnglishScore() + getKannadaScore();
        this.totalGrade.set(total);

        // Calculate average based on 5 subjects, assuming max 100 per subject
        double avg = (double) total / 5.0;
        this.averageGrade.set(avg);
    }

    // =======================================================
    // --- ATTENDANCE METHODS (Unchanged) ---
    // =======================================================

    public Map<LocalDate, String> getAttendanceRecords() {
        return attendanceRecords;
    }

    public void recordAttendance(LocalDate date, String status) {
        this.attendanceRecords.put(date, status);
    }

    // --- Utility Method (Updated for new scores) ---

    @Override
    public String toString() {
        return String.format("Student ID: %s, Name: %s, Grade: %s, Total Score: %d",
                getStudentId(), getName(), getGradeLevel(), getTotalGrade());
    }
}