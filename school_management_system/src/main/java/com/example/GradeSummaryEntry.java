package com.example;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Data Model for a single row in the Grade Summary Report Table.
 * UPDATED to include five subject scores for detailed reporting.
 */
public class GradeSummaryEntry {
    
    // --- Core Student Properties ---
    private final StringProperty studentId;
    private final StringProperty studentName;
    
    // --- Subject Score Properties (NEW) ---
    private final IntegerProperty mathScore;
    private final IntegerProperty scienceScore;
    private final IntegerProperty socialScore; 
    private final IntegerProperty englishScore;
    private final IntegerProperty kannadaScore; 
    
    // --- Calculated Property ---
    private final DoubleProperty overallAverage;

    /**
     * Full constructor to initialize all properties, including the five subject scores.
     */
    public GradeSummaryEntry(String studentId, String studentName, double overallAverage,
                             int math, int science, int social, int english, int kannada) {
        
        this.studentId = new SimpleStringProperty(studentId);
        this.studentName = new SimpleStringProperty(studentName);
        this.overallAverage = new SimpleDoubleProperty(overallAverage);
        
        // Initialize new subject score properties
        this.mathScore = new SimpleIntegerProperty(math);
        this.scienceScore = new SimpleIntegerProperty(science);
        this.socialScore = new SimpleIntegerProperty(social);
        this.englishScore = new SimpleIntegerProperty(english);
        this.kannadaScore = new SimpleIntegerProperty(kannada);
    }

    // =======================================================
    // --- PROPERTY ACCESSORS (Required by JavaFX Controller) ---
    // =======================================================
    
    public StringProperty studentIdProperty() { return studentId; }
    public StringProperty studentNameProperty() { return studentName; }
    public DoubleProperty overallAverageProperty() { return overallAverage; }
    
    // New Subject Score Accessors for JavaFX binding
    public IntegerProperty mathScoreProperty() { return mathScore; }
    public IntegerProperty scienceScoreProperty() { return scienceScore; }
    public IntegerProperty socialScoreProperty() { return socialScore; }
    public IntegerProperty englishScoreProperty() { return englishScore; }
    public IntegerProperty kannadaScoreProperty() { return kannadaScore; }
    
    // --- Value Getters ---
    public String getStudentId() { return studentId.get(); }
    public String getStudentName() { return studentName.get(); }
    public double getOverallAverage() { return overallAverage.get(); }
}