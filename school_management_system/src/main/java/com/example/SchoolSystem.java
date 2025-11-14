package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Manager/Service class, refactored to use JDBC for permanent data storage.
 * Handles all database operations for students, grades, and attendance.
 */
public class SchoolSystem {

    // --- Constructor ---
    public SchoolSystem() {
        System.out.println(" School System Manager ready.");
    }

    // --- Student Management (CREATE) ---

    public void addStudent(Student newStudent) {
        if (findStudentById(newStudent.getStudentId()) != null) {
            System.out.println(" Error: Student with ID " + newStudent.getStudentId() + " already exists.");
            return;
        }

        String sql = "INSERT INTO students(id, name, grade_level) VALUES(?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStudent.getStudentId());
            pstmt.setString(2, newStudent.getName());
            pstmt.setString(3, newStudent.getGradeLevel());
            pstmt.executeUpdate();
            
            System.out.println("Student saved to DB: " + newStudent.getName());

        } catch (SQLException e) {
            System.err.println(" Error adding student: " + e.getMessage());
        }
    }

    // --- Student Management (READ) ---

    public Student findStudentById(String studentId) {
        Student student = null;
        String sql = "SELECT id, name, grade_level FROM students WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                student = new Student(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("grade_level")
                );
                // Load associated data for this student (grades and attendance)
                loadStudentGrades(conn, student);
                loadStudentAttendance(conn, student);
            }

        } catch (SQLException e) {
            System.err.println(" Error finding student: " + e.getMessage());
        }
        return student;
    }

    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<>();
        Map<String, Student> studentMap = new HashMap<>(); // To hold students for easy lookup

        String sqlStudents = "SELECT id, name, grade_level FROM students";
        String sqlGrades = "SELECT student_id, subject, score FROM grades";
        String sqlAttendance = "SELECT student_id, date, status FROM attendance";

        try (Connection conn = DatabaseManager.getConnection()) {
            
            // 1. Load all students
            try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlStudents)) {
                while (rs.next()) {
                    Student s = new Student(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("grade_level")
                    );
                    studentList.add(s);
                    studentMap.put(s.getStudentId(), s);
                }
            }

            // 2. Load all grades and assign to students
            try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlGrades)) {
                while (rs.next()) {
                    Student s = studentMap.get(rs.getString("student_id"));
                    if (s != null) {
                        s.addGrade(rs.getString("subject"), rs.getInt("score"));
                    }
                }
            }

            // 3. Load all attendance and assign to students
            try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlAttendance)) {
                while (rs.next()) {
                    Student s = studentMap.get(rs.getString("student_id"));
                    if (s != null) {
                        s.recordAttendance(LocalDate.parse(rs.getString("date")), rs.getString("status"));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println(" Error loading all students: " + e.getMessage());
        }
        return studentList;
    }

    // --- Student Management (UPDATE) ---

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
            System.err.println(" Error updating student: " + e.getMessage());
        }
    }

    // --- Student Management (DELETE) ---

    public void deleteStudent(String studentId) {
        String sqlDeleteStudent = "DELETE FROM students WHERE id = ?";
        String sqlDeleteGrades = "DELETE FROM grades WHERE student_id = ?";
        String sqlDeleteAttendance = "DELETE FROM attendance WHERE student_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Delete Grades
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteGrades)) {
                pstmt.setString(1, studentId);
                pstmt.executeUpdate();
            }

            // 2. Delete Attendance
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteAttendance)) {
                pstmt.setString(1, studentId);
                pstmt.executeUpdate();
            }

            // 3. Delete Student
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteStudent)) {
                pstmt.setString(1, studentId);
                pstmt.executeUpdate();
            }

            conn.commit(); // Commit all changes
            System.out.println("Student, grades, and attendance deleted for ID: " + studentId);

        } catch (SQLException e) {
            System.err.println("Transaction failed: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Connection close failed: " + closeEx.getMessage());
                }
            }
        }
    }
    
    // --- Grade and Attendance Recording ---

    public void recordGrade(String studentId, String subject, int score) {
        // Simple insert: no check for duplicates on subject/score as a student can have multiple grades
        String sql = "INSERT INTO grades(student_id, subject, score) VALUES(?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, subject);
            pstmt.setInt(3, score);
            pstmt.executeUpdate();
            
            // Also update the in-memory Student object if it exists (optional, but good practice)
            Student s = findStudentById(studentId); 
            if (s != null) {
                s.addGrade(subject, score);
            }

        } catch (SQLException e) {
            System.err.println(" Error recording grade: " + e.getMessage());
        }
    }

    public void recordAttendance(String studentId, LocalDate date, String status) {
        // Use INSERT OR REPLACE to update an existing attendance record (Primary Key is student_id + date)
        String sql = "INSERT OR REPLACE INTO attendance(student_id, date, status) VALUES(?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, date.toString()); // LocalDate is stored as YYYY-MM-DD string
            pstmt.setString(3, status);
            pstmt.executeUpdate();
            
            // Also update the in-memory Student object if it exists
            Student s = findStudentById(studentId);
            if (s != null) {
                s.recordAttendance(date, status);
            }

        } catch (SQLException e) {
            System.err.println(" Error recording attendance: " + e.getMessage());
        }
    }
    
    // --- EXISTING METHOD for Single Student Attendance Report ---
    public Map<LocalDate, String> getAttendanceForMonth(String studentId, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, String> monthlyRecords = new HashMap<>();
        String sql = "SELECT date, status FROM attendance WHERE student_id = ? AND date BETWEEN ? AND ? ORDER BY date ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, startDate.toString());
            pstmt.setString(3, endDate.toString());
            
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                monthlyRecords.put(
                    LocalDate.parse(rs.getString("date")),
                    rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching attendance for month: " + e.getMessage());
        }
        return monthlyRecords;
    }
    
    // --- UPDATED: NEW METHOD for ALL Student Attendance Report (4) ---
    public List<AttendanceReportEntry> getAttendanceReportForAll(LocalDate startDate, LocalDate endDate) {
        List<AttendanceReportEntry> reportList = new ArrayList<>();
        
        // SQL JOIN to get student name along with attendance status
        String sql = """
                     SELECT s.id, s.name, a.date, a.status 
                     FROM students s
                     JOIN attendance a ON s.id = a.student_id
                     WHERE a.date BETWEEN ? AND ? 
                     ORDER BY s.name ASC, a.date ASC
                     """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate.toString());
            pstmt.setString(2, endDate.toString());
            
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reportList.add(new AttendanceReportEntry(
                    rs.getString("id"),
                    rs.getString("name"),
                    LocalDate.parse(rs.getString("date")),
                    rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all student attendance report: " + e.getMessage());
        }
        return reportList;
    }


    // --- Helper methods to load data into the in-memory Student object ---

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

    // --- UPDATED: NEW METHOD for Grade Summary Report (5) ---
    public List<GradeSummaryEntry> getGradeSummaryForAll() {
        List<GradeSummaryEntry> summaryList = new ArrayList<>();
        
        // 1. Get all students (This ensures their grades map is populated, even if empty)
        List<Student> allStudents = getAllStudents(); 
        
        // 2. Iterate and calculate the average for each
        for (Student student : allStudents) {
            // Reuses the existing calculateOverallAverage method
            double average = calculateOverallAverage(student.getStudentId());
            
            summaryList.add(new GradeSummaryEntry(
                student.getStudentId(),
                student.getName(),
                average
            ));
        }
        
        return summaryList;
    }
}