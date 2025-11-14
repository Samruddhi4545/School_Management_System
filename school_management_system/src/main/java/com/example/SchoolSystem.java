package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Manager/Service class, refactored to use JDBC for permanent data storage.
 * UPDATED for fixed 5-subject grading.
 */
public class SchoolSystem {

    // --- Constructor ---
    public SchoolSystem() {
        System.out.println(" School System Manager ready.");
    }

    // --- Student Management (CREATE/READ/UPDATE/DELETE) ---

    // ... (addStudent, updateStudent, deleteStudent remain unchanged) ...

    public void addStudent(Student newStudent) {
        if (findStudentById(newStudent.getStudentId()) != null) {
            System.out.println(" Error: Student with ID " + newStudent.getStudentId() + " already exists.");
            return;
        }

        String sqlStudent = "INSERT INTO students(id, name, grade_level) VALUES(?, ?, ?)";
        String sqlGrades = "INSERT INTO grades(student_id) VALUES(?)"; // Insert initial grade record

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert into students table
            try (PreparedStatement pstmt = conn.prepareStatement(sqlStudent)) {
                pstmt.setString(1, newStudent.getStudentId());
                pstmt.setString(2, newStudent.getName());
                pstmt.setString(3, newStudent.getGradeLevel());
                pstmt.executeUpdate();
            }

            // 2. Insert initial record into grades table
            try (PreparedStatement pstmt = conn.prepareStatement(sqlGrades)) {
                pstmt.setString(1, newStudent.getStudentId());
                pstmt.executeUpdate();
            }

            conn.commit(); // Commit transaction
            System.out.println("Student and initial grade record saved to DB: " + newStudent.getName());

        } catch (SQLException e) {
            System.err.println("SQL ERROR adding student: " + e.getMessage());
        }
    }

    public void updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, grade_level = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getGradeLevel());
            pstmt.setString(3, student.getStudentId());
            pstmt.executeUpdate();

            System.out.println("Student updated in DB: " + student.getName());

        } catch (SQLException e) {
            System.err.println("SQL ERROR updating student: " + e.getMessage());
        }
    }

    public void deleteStudent(String studentId) {
        // Delete records in grades, attendance, then students (due to foreign key constraints)
        String sqlGrades = "DELETE FROM grades WHERE student_id = ?";
        String sqlAttendance = "DELETE FROM attendance WHERE student_id = ?";
        String sqlStudent = "DELETE FROM students WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement pstmt = conn.prepareStatement(sqlGrades)) {
                pstmt.setString(1, studentId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sqlAttendance)) {
                pstmt.setString(1, studentId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sqlStudent)) {
                pstmt.setString(1, studentId);
                pstmt.executeUpdate();
            }

            conn.commit();
            System.out.println("Student and all related records deleted from DB: ID " + studentId);

        } catch (SQLException e) {
            System.err.println("SQL ERROR deleting student: " + e.getMessage());
        }
    }
    
    // ... (getAllStudents remains unchanged) ...
    
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id, name, grade_level FROM students";
        
        try (Connection conn = DatabaseManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student s = new Student(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("grade_level")
                );
                // Load additional data (grades and attendance)
                loadStudentGrades(conn, s);
                loadStudentAttendance(conn, s);
                students.add(s);
            }
            
        } catch (SQLException e) {
            System.err.println("SQL ERROR retrieving students: " + e.getMessage());
        }
        return students;
    }

    public Student findStudentById(String studentId) {
        // Uses the list loading mechanism to find a single student efficiently for UI purposes
        return getAllStudents().stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .findFirst()
                .orElse(null);
    }
    
    // --- Grade Management (CREATE/UPDATE) ---

    /**
     * Records all 5 fixed subject grades for a student in a single operation (INSERT/UPDATE).
     * Since the grades table uses student_id as the primary key, we use REPLACE.
     */
    public void recordGrade(String studentId, int math, int science, int english, int history, int art) {
        String sql = """
            INSERT OR REPLACE INTO grades(student_id, math_score, science_score, social_score,english_score, kannada_score)
            VALUES(?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setInt(2, math);
            pstmt.setInt(3, science);
            pstmt.setInt(4, social);
            pstmt.setInt(5, english);
            pstmt.setInt(6, kannada);
            pstmt.executeUpdate();
            
            System.out.println("Fixed grades recorded/updated for student ID: " + studentId);

        } catch (SQLException e) {
            System.err.println("SQL ERROR recording grades: " + e.getMessage());
        }
    }
    
    /**
     * Loads the 5 fixed subject grades from the DB into the Student object's properties.
     */
    private void loadStudentGrades(Connection conn, Student s) throws SQLException {
        String sql = "SELECT math_score, science_score, social_score, english_score, kannada_score FROM grades WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.getStudentId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Set the fixed properties on the Student object
                s.setMathScore(rs.getInt("math_score"));
                s.setScienceScore(rs.getInt("science_score"));
                s.setSocialScore(rs.getInt("social_score"));
                s.setEnglishScore(rs.getInt("english_score"));
                s.setKannadaScore(rs.getInt("kannada_score"));
            }
            // If rs.next() is false, the scores remain 0 (from Student constructor default)
        }
    }

    // --- Attendance Management (CREATE) ---

    public void recordAttendance(String studentId, LocalDate date, String status) {
        // Use INSERT OR REPLACE to allow updating an existing attendance record for the same day
        String sql = "INSERT OR REPLACE INTO attendance(student_id, date, status) VALUES(?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, date.toString()); // Convert LocalDate to String (YYYY-MM-DD)
            pstmt.setString(3, status);
            pstmt.executeUpdate();
            
            System.out.println("Attendance recorded/updated for student ID: " + studentId);

        } catch (SQLException e) {
            System.err.println("SQL ERROR recording attendance: " + e.getMessage());
        }
    }

    // --- Data Loading for Attendance ---

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
    
    // --- Reporting and Calculations (Removed/simplified as logic is now in Student.java) ---
    // The previous methods like calculateOverallAverage are now handled directly by Student.getAverageGrade()
    // We can keep a simplified version if needed, but for now, rely on the Student model's properties.
}