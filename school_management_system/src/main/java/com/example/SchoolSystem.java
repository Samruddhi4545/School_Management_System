package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Manager/Service class, refactored to use JDBC for permanent data storage.
 * No longer uses an in-memory Map for students.
 */
public class SchoolSystem {

    // --- Constructor ---
    public SchoolSystem() {
        System.out.println(" School System Manager ready.");
    }

    // --- Student Management (CREATE) ---

    public void addStudent(Student newStudent) {
        if (findStudentById(newStudent.getStudentId()) != null) {
            System.out.println("❌ Error: Student with ID " + newStudent.getStudentId() + " already exists.");
            return;
        }

        String sql = "INSERT INTO students(id, name, grade_level) VALUES(?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStudent.getStudentId());
            pstmt.setString(2, newStudent.getName());
            pstmt.setString(3, newStudent.getGradeLevel());
            pstmt.executeUpdate();
            
            System.out.println("✅ Student saved to DB: " + newStudent.getName());

        } catch (SQLException e) {
            System.err.println("❌ Error saving student to database: " + e.getMessage());
        }
    }

    // --- Student Management (READ) ---

    /**
     * Finds a student by their ID and loads ALL related data (grades, attendance) from the DB.
     */
    public Student findStudentById(String studentId) {
        String sqlStudent = "SELECT * FROM students WHERE id = ?";
        Student s = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlStudent)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                s = new Student();
                s.setStudentId(rs.getString("id"));
                s.setName(rs.getString("name"));
                s.setGradeLevel(rs.getString("grade_level"));
                
                // Load all related data
                loadStudentGrades(conn, s);
                loadStudentAttendance(conn, s);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error finding student: " + e.getMessage());
        }
        return s;
    }

    /**
     * Returns a list of all students currently in the database (summary view).
     * FIX: Calls helper methods to load all related data to ensure correct counts are displayed.
     */
    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<>();
        String sql = "SELECT id, name, grade_level FROM students";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // 1. Load basic info
                Student s = new Student();
                s.setStudentId(rs.getString("id"));
                s.setName(rs.getString("name"));
                s.setGradeLevel(rs.getString("grade_level"));
                
                // 2. LOAD ALL RELATED RECORDS (FIX for summary count bug)
                loadStudentGrades(conn, s);       
                loadStudentAttendance(conn, s);   
                
                studentList.add(s);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting all students: " + e.getMessage());
        }
        return studentList;
    }
    
    // --- Grade Management (CREATE/UPDATE) ---

    public void recordGrade(String studentId, String subject, int score) {
        if (findStudentById(studentId) == null) { 
            System.out.println("❌ Error: Student not found with ID: " + studentId);
            return;
        }
        
        if (score < 0 || score > 100) {
            System.out.println("❌ Error: Score must be between 0 and 100.");
            return;
        }

        String sql = "INSERT INTO grades(student_id, subject, score) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, subject);
            pstmt.setInt(3, score);
            pstmt.executeUpdate();

            System.out.println("✅ Grade recorded in DB: " + subject + " (" + score + "%) for ID " + studentId);

        } catch (SQLException e) {
            System.err.println("❌ Error recording grade: " + e.getMessage());
        }
    }
    
    // --- Attendance Tracking (CREATE/UPDATE) ---
    
    public void recordAttendance(String studentId, LocalDate date, String status) {
        if (findStudentById(studentId) == null) {
            System.out.println("❌ Error: Student not found with ID: " + studentId);
            return;
        }

        String finalStatus = status.trim().toUpperCase();
        if (!finalStatus.equals("PRESENT") && !finalStatus.equals("ABSENT") && !finalStatus.equals("LATE")) 
        {
            System.out.println("❌ Error: Invalid status. Use 'Present', 'Absent', or 'Late'.");
            return;
        }
        
        // INSERT OR REPLACE updates the status if the student/date combination already exists
        String sql = "INSERT OR REPLACE INTO attendance(student_id, date, status) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, date.toString());
            pstmt.setString(3, finalStatus);
            pstmt.executeUpdate();

            System.out.println("✅ Attendance recorded in DB for ID " + studentId + " on " + date + ": " + finalStatus);

        } catch (SQLException e) {
            System.err.println("❌ Error recording attendance: " + e.getMessage());
        }
    }
    
    // --- PRIVATE HELPER METHODS FOR LOADING RELATED DATA ---
    
    private void loadStudentGrades(Connection conn, Student s) throws SQLException {
        String sql = "SELECT subject, score FROM grades WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.getStudentId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                s.addGrade(rs.getString("subject"), rs.getInt("score"));
            }
        }
    }

    private void loadStudentAttendance(Connection conn, Student s) throws SQLException {
        String sql = "SELECT date, status FROM attendance WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.getStudentId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                s.recordAttendance(LocalDate.parse(rs.getString("date")), rs.getString("status"));
            }
        }
    }

    // --- Reporting and Calculations ---
    
    public double calculateOverallAverage(String studentId) {
        Student s = findStudentById(studentId);
        if (s == null) return 0.0;

        List<Integer> allGrades = s.getGrades().values().stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());

        if (allGrades.isEmpty()) return 0.0;
        int sum = allGrades.stream().mapToInt(Integer::intValue).sum();
        return (double) sum / allGrades.size();
    }
    
    public double calculateSubjectAverage(String studentId, String subject) {
        Student s = findStudentById(studentId);
        if (s == null) return 0.0;
        
        List<Integer> subjectGrades = s.getGrades().get(subject);
        
        if (subjectGrades == null || subjectGrades.isEmpty()) return 0.0;
        
        int sum = subjectGrades.stream().mapToInt(Integer::intValue).sum();
        return (double) sum / subjectGrades.size();
    }
}