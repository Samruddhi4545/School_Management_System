package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the connection to the SQLite database and ensures the necessary tables exist.
 * UPDATED to use fixed columns for grades.
 */
public class DatabaseManager {

    // The connection string for the SQLite database file
    private static final String URL = "jdbc:sqlite:school.db";

    /**
     * Gets a connection to the SQLite database.
     * @return A valid Connection object.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Initializes the database by creating all necessary tables if they don't already exist.
     * Note: If the schema changes significantly, you may need to manually delete school.db
     * or implement a proper migration strategy for existing installations.
     */
    public static void initializeDatabase() {
        // SQL statements to create tables
        String sqlStudents = """
                            CREATE TABLE IF NOT EXISTS students (
                            id TEXT PRIMARY KEY,
                            name TEXT NOT NULL,
                            grade_level TEXT NOT NULL
                            );""";

        // Grades table UPDATED to fixed 5 subjects (scores stored as INTEGER out of 100)
        String sqlGrades = """
                        CREATE TABLE IF NOT EXISTS grades (
                            student_id TEXT PRIMARY KEY,
                            math_score INTEGER NOT NULL DEFAULT 0,
                            science_score INTEGER NOT NULL DEFAULT 0,
                            social_score INTEGER NOT NULL DEFAULT 0,
                            english_score INTEGER NOT NULL DEFAULT 0,
                            kannada_score INTEGER NOT NULL DEFAULT 0,
                            FOREIGN KEY (student_id) REFERENCES students (id)
                        );"""; // PRIMARY KEY is now on student_id, ensuring one record per student

        // Attendance table remains the same
        String sqlAttendance = """
                            CREATE TABLE IF NOT EXISTS attendance (
                                student_id TEXT NOT NULL,
                                date TEXT NOT NULL,
                                status TEXT NOT NULL,
                                PRIMARY KEY (student_id, date),
                                FOREIGN KEY (student_id) REFERENCES students (id)
                                );"""; 
        
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            // Execute SQL statements to create tables
            stmt.execute(sqlStudents);
            stmt.execute(sqlGrades);
            stmt.execute(sqlAttendance);
            
            System.out.println("Database 'school.db' initialized and tables verified (Fixed Grades Schema).");
            
        } catch (SQLException e) {
            System.err.println(" Error initializing database: " + e.getMessage());
        }
    }
}