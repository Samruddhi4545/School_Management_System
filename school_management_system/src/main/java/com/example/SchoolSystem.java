package com.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Manager/Service class.
 * Handles the collection of 'Student' objects and implements all core business logic.
 */
public class SchoolSystem {

    // A central collection to store all students, using ID for quick lookup
    private Map<String, Student> students;

    // --- Constructor ---
    public SchoolSystem() {
        this.students = new HashMap<>();
        System.out.println("✅ School System initialized.");
    }

    // --- Student Management (CRUD - Create, Read) ---

    /**
     * Adds a new student object to the system's collection.
     */
    public void addStudent(Student newStudent) {
        // Check for duplicate ID
        if (students.containsKey(newStudent.getStudentId())) {
            System.out.println("❌ Error: Student with ID " + newStudent.getStudentId() + " already exists.");
            return;
        }
        students.put(newStudent.getStudentId(), newStudent);
        // The success message is printed in the Student constructor.
    }

    /**
     * Finds a student by their ID.
     */
    public Student findStudentById(String studentId) {
        return students.get(studentId);
    }

    /**
     * Returns a list of all students currently in the system.
     */
    public List<Student> getAllStudents() {
        // Return a new ArrayList for safety, preventing external modification of the core map.
        return new ArrayList<>(students.values());
    }

    // --- Grade Management ---

    /**
     * Records a score for a given subject for a specific student.
     */
    public void recordGrade(String studentId, String subject, int score) {
        Student s = findStudentById(studentId);
        if (s == null) {
            System.out.println("❌ Error: Student not found with ID: " + studentId);
            return;
        }
        
        // Basic validation
        if (score < 0 || score > 100) {
            System.out.println("❌ Error: Score must be between 0 and 100.");
            return;
        }

        s.addGrade(subject, score);
        System.out.println("✅ Grade recorded: " + subject + " (" + score + "%) for " + s.getName());
    }
    
    // --- Attendance Tracking ---
    
    /**
     * Records attendance for a specific student on a specific date.
     */
    public void recordAttendance(String studentId, LocalDate date, String status) {
        Student s = findStudentById(studentId);
        if (s == null) {
            System.out.println("❌ Error: Student not found with ID: " + studentId);
            return;
        }

        // Standardize status input (e.g., allow "P", "p", "Present")
        String finalStatus = status.trim().toUpperCase();
        if (!finalStatus.equals("PRESENT") && !finalStatus.equals("ABSENT") && !finalStatus.equals("LATE")) 
        {
            System.out.println("❌ Error: Invalid status. Use 'Present', 'Absent', or 'Late'.");
            return;
        }
        
        s.recordAttendance(date, finalStatus);
        System.out.println("✅ Attendance recorded for " + s.getName() + " on " + date + ": " + finalStatus);
    }

    // --- Reporting and Calculations ---
    
    /**
     * Calculates the average score across ALL subjects for a student.
     */
    public double calculateOverallAverage(String studentId) {
        Student s = findStudentById(studentId);
        if (s == null) return 0.0;

        List<Integer> allGrades = s.getGrades().values().stream()
            .flatMap(List::stream) // Flattens the List<List<Integer>> into a single List<Integer>
            .collect(Collectors.toList());

        if (allGrades.isEmpty()) {
            return 0.0;
        }

        // Calculate the sum of all scores and divide by the count
        int sum = allGrades.stream().mapToInt(Integer::intValue).sum();
        return (double) sum / allGrades.size();
    }
    
    /**
     * Calculates the average score for a SINGLE subject for a student.
     */
    public double calculateSubjectAverage(String studentId, String subject) {
        Student s = findStudentById(studentId);
        if (s == null) return 0.0;
        
        List<Integer> subjectGrades = s.getGrades().get(subject);
        
        if (subjectGrades == null || subjectGrades.isEmpty()) {
            return 0.0;
        }
        
        int sum = subjectGrades.stream().mapToInt(Integer::intValue).sum();
        return (double) sum / subjectGrades.size();
    }
}