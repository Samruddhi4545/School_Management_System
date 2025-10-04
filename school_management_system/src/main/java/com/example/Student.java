package com.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Model/Entity class for a Student.
 * Manages personal information, grades, and attendance records.
 */
public class Student {

    // --- Student Personal Information ---
    private String studentId;
    private String name;
    private String gradeLevel;

    // --- Academic Records (Grades) ---
    // Key: Subject Name (e.g., "Math")
    // Value: List of scores for that subject
    private Map<String, List<Integer>> grades;

    // --- Attendance Records ---
    // Key: Date of attendance
    // Value: Status (e.g., "Present", "Absent", "Late")
    private Map<LocalDate, String> attendanceRecords;

    // --- Constructor ---
    public Student(String studentId, String name, String gradeLevel) {
        this.studentId = studentId;
        this.name = name;
        this.gradeLevel = gradeLevel;
        // Initialize the collections
        this.grades = new HashMap<>();
        this.attendanceRecords = new HashMap<>();
        System.out.println("✅ Student added: " + name + " (ID: " + studentId + ")");
    }

    // --- Getters and Setters (Encapsulation) ---

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    // Setter is usually needed if student details can be updated
    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public Map<String, List<Integer>> getGrades() {
        return grades;
    }

    public Map<LocalDate, String> getAttendanceRecords() {
        return attendanceRecords;
    }

    // --- Core Methods for Records ---

    /**
     * Adds a score to a specific subject's grade list.
     */
    public void addGrade(String subject, int score) {
        // Ensure the subject key exists in the map
        this.grades.computeIfAbsent(subject, k -> new ArrayList<>()).add(score);
    }

    /**
     * Records attendance status for a specific date.
     */
    public void recordAttendance(LocalDate date, String status) {
        this.attendanceRecords.put(date, status);
    }

    // --- Utility Method ---

    /**
     * Override toString for a clean summary output in the CLI.
     */
    @Override
    public String toString() {
        return String.format("  Student ID: %s, Name: %s, Grade: %s, Grades Count: %d, Attendance Count: %d",studentId, name, gradeLevel, getGradeCount(), attendanceRecords.size());
    }
    
    // Helper method for toString
    private int getGradeCount() {
        return grades.values().stream().mapToInt(List::size).sum();
    }
}