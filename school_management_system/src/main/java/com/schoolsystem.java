package com;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Class name changed to lowercase as requested
public class schoolsystem {

    // --- FIELDS ---
    // Stores all student objects in memory for the CLI phase.
    private List<student> students;

    // --- CONSTRUCTOR ---
    public schoolsystem() {
        this.students = new ArrayList<>();
        // Using lowercase constructor and type for student
        addStudent(new student("1001", "Alice Johnson", "10"));
        addStudent(new student("1002", "Bob Smith", "11"));
        
        // Using lowercase type and method for student
        student alice = findStudentById("1001");
        if (alice != null) {
            alice.addGrade("Math", 95);
            alice.addGrade("Math", 88);
            alice.addGrade("Science", 92);
            alice.markAttendance(LocalDate.now(), "Present");
        }
    }

    // --- CRUD OPERATIONS FOR STUDENTS ---

    /** Adds a new student to the system after checking for duplicate IDs. */
    // Using lowercase type for student
    public void addStudent(student student) {
        if (findStudentById(student.getStudentId()) == null) {
            this.students.add(student);
            System.out.println(" student added successfully: " + student.getName());
        } else {
            System.out.println(" Error: student with ID " + student.getStudentId() + " already exists.");
        }
    }

    /** Finds and returns a student object by their unique ID. */
    // Using lowercase return type for student
    public student findStudentById(String studentId) {
        // Using lowercase type for student
        for (student student : students) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }
        return null; // Return null if not found
    }

    /** Returns the entire list of students. */
    // Using lowercase return type for List<student>
    public List<student> getAllStudents() {
        return students;
    }
    
    // --- FEATURE INTEGRATION (Delegating to student class) ---

    /** Records a grade for a student in a specific subject. */
    public void recordGrade(String studentId, String subject, int score) {
        // Using lowercase type for student
        student student = findStudentById(studentId);
        if (student != null) {
            student.addGrade(subject, score); // Calls method in student.java
            System.out.println("Grade recorded for " + student.getName() + " in " + subject);
        } else {
            System.out.println(" student not found with ID: " + studentId);
        }
    }

    /** Records attendance for a student on a specific date. */
    public void recordAttendance(String studentId, LocalDate date, String status) {
        // Using lowercase type for student
        student student = findStudentById(studentId);
        if (student != null) {
            student.markAttendance(date, status); // Calls method in student.java
            System.out.println("Attendance marked for " + student.getName() + " on " + date + ": " + status);
        } else {
            System.out.println(" student not found with ID: " + studentId);
        }
    }

    // --- GRADE CALCULATION LOGIC ---

    /** Calculates the overall average grade across all subjects for a student. */
    public double calculateOverallAverage(String studentId) {
        // Using lowercase type for student
        student student = findStudentById(studentId);
        if (student == null) {
            System.out.println("student not found with ID: " + studentId);
            return 0.0;
        }

        Map<String, List<Integer>> grades = student.getGrades();
        if (grades.isEmpty()) {
            return 0.0;
        }

        // Calculation logic remains the same
        long totalScoresCount = grades.values().stream().mapToLong(List::size).sum();
        
        double totalScoresSum = grades.values().stream().flatMap(List::stream) .mapToInt(Integer::intValue).sum();
        
        if (totalScoresCount == 0) {
            return 0.0;
        }

        return totalScoresSum / totalScoresCount;
    }

    /** Calculates the average grade for a specific subject for a student. */
    public double calculateSubjectAverage(String studentId, String subject) {
        // Using lowercase type for student
        student student = findStudentById(studentId);
        if (student == null) {
            System.out.println(" student not found with ID: " + studentId);
            return 0.0;
        }

        List<Integer> subjectGrades = student.getGrades().get(subject);
        if (subjectGrades == null || subjectGrades.isEmpty()) {
            System.out.println(" No grades found for " + subject + " for " + student.getName());
            return 0.0;
        }

        // Calculation logic remains the same
        double sum = subjectGrades.stream().mapToInt(Integer::intValue).sum();
        
        return sum / subjectGrades.size();
    }
}