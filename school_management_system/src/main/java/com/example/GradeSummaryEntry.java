package com.example;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Data Model for a single row in the Grade Summary Report Table.
 */
public class GradeSummaryEntry {
    
    private final StringProperty studentId;
    private final StringProperty studentName;
    private final DoubleProperty overallAverage;

    public GradeSummaryEntry(String studentId, String studentName, double overallAverage) {
        this.studentId = new SimpleStringProperty(studentId);
        this.studentName = new SimpleStringProperty(studentName);
        this.overallAverage = new SimpleDoubleProperty(overallAverage);
    }

    // --- Property Getters (Required for TableView binding) ---
    public StringProperty studentIdProperty() { return studentId; }
    public StringProperty studentNameProperty() { return studentName; }
    public DoubleProperty overallAverageProperty() { return overallAverage; }
    
    // --- Value Getters ---
    public String getStudentId() { return studentId.get(); }
    public String getStudentName() { return studentName.get(); }
    public double getOverallAverage() { return overallAverage.get(); }
}
