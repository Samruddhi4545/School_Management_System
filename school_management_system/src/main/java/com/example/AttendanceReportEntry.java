package com.example;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Data Model for a single row in the Class Attendance Report Table.
 * Combines Student details with a single Attendance record.
 */
public class AttendanceReportEntry {
    
    private final StringProperty studentId;
    private final StringProperty studentName;
    private final ObjectProperty<LocalDate> date;
    private final StringProperty status;

    public AttendanceReportEntry(String studentId, String studentName, LocalDate date, String status) {
        this.studentId = new SimpleStringProperty(studentId);
        this.studentName = new SimpleStringProperty(studentName);
        this.date = new SimpleObjectProperty<>(date);
        this.status = new SimpleStringProperty(status);
    }

    // --- Property Getters (Required for TableView binding) ---
    public StringProperty studentIdProperty() { return studentId; }
    public StringProperty studentNameProperty() { return studentName; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty statusProperty() { return status; }
    
    // --- Value Getters ---
    public String getStudentId() { return studentId.get(); }
    public String getStudentName() { return studentName.get(); }
    public LocalDate getDate() { return date.get(); }
    public String getStatus() { return status.get(); }
}
