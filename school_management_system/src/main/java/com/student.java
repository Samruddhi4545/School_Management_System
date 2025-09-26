package com;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class student implements Serializable {

    // --- FIELDS ---
    private String studentId;
    private String name;
    private String gradeLevel;
    private String Subjects;
    private Integer Total_Scores;

    // Grades: Key = Subject Name (e.g., "Math"), Value = List of scores (e.g., [95, 88])
    private Map<String, List<Integer>> grades;

    // Attendance: Key = Date, Value = Status (e.g., "Present", "Absent")
    private Map<LocalDate, String> attendanceRecords;

    // --- CONSTRUCTOR ---
    public student(String studentId, String name, String gradeLevel,String Subjects,Integer Total_Scores) {
        this.studentId = studentId;
        this.name = name;
        this.gradeLevel = gradeLevel;
        this.Subjects = Subjects;
        this.Total_Scores = Total_Scores;
        this.grades = new HashMap<>();
        this.attendanceRecords = new HashMap<>();
    }

    // --- CORE LOGIC METHODS ---

    /** Adds a score to the specified subject's list of grades. */
    public void addGrade(String subject, int score) {
        // If the subject isn't in the map yet, create a new list for it.
        this.grades.putIfAbsent(subject, new ArrayList<>());
        this.grades.get(subject).add(score);
    }

    /** Records an attendance status for a specific date. */
    public void markAttendance(LocalDate date, String status) {
        this.attendanceRecords.put(date, status);
    }

    // --- GETTERS AND SETTERS ---

    public String getStudentId() {
        return studentId;
    }

    // Note: We typically don't allow changing the ID after creation, 
    // but we can add a setter if needed. For now, we'll omit setStudentId().

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public Map<String, List<Integer>> getGrades() {
        // Returns the entire map of grades
        return grades;
    }

    // Note: We often omit setGrades() to enforce using addGrade() for proper logging/validation.

    public Map<LocalDate, String> getAttendanceRecords() {
        // Returns the entire map of attendance records
        return attendanceRecords;
    }
    
    // Note: We often omit setAttendanceRecords() to enforce using markAttendance().

    // --- SIMPLE DISPLAY METHOD ---
    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Grade: %s | Subjects: %d | Total Scores: %d", studentId, name, gradeLevel, grades.size(), grades.values().stream().mapToInt(List::size).sum());
    }
}