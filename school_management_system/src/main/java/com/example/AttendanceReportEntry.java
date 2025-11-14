package com.example;

import java.time.LocalDate;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Data Model for a single row in the PIVOTED Class Attendance Report Table.
 * Holds student details and a map of Date -> Status, allowing for dynamic date columns.
 */
public class AttendanceReportEntry {
    
    private final StringProperty studentId;
    private final StringProperty studentName;
    // Map<Date, Status> to hold attendance for the entire date range
    private final Map<LocalDate, String> attendanceData; 

    public AttendanceReportEntry(String studentId, String studentName, Map<LocalDate, String> attendanceData) {
        this.studentId = new SimpleStringProperty(studentId);
        this.studentName = new SimpleStringProperty(studentName);
        this.attendanceData = attendanceData;
    }

    // --- Property Getters (For fixed columns: ID and Name) ---
    public StringProperty studentIdProperty() { return studentId; }
    public StringProperty studentNameProperty() { return studentName; }
    
    // --- Data Getter (Used by dynamic columns in the Controller) ---
    public Map<LocalDate, String> getAttendanceData() { return attendanceData; }

    public String getStudentId() { return studentId.get(); }
    public String getStudentName() { return studentName.get(); }
}