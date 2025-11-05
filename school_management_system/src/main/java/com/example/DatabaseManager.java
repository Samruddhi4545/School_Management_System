package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the connection to the SQLite database and ensures the necessary tables exist.
 */
public class DatabaseManager {

    // The connection string for the SQLite database file
    private static final String URL = "jdbc:sqlite:school.db";

    /**
     * Gets a connection to the SQLite database.
     * @return A valid Connection object.
     */
    public static Connection getConnection() throws SQLException {
        // DriverManager will create the 'school.db' file if it doesn't exist.
        return DriverManager.getConnection(URL);
    }

    /**
     * Initializes the database by creating all necessary tables if they don't already exist.
     */
    public static void initializeDatabase() {
        // SQL statements to create tables
        String sqlStudents = """
                            CREATE TABLE IF NOT EXISTS students (
                            id TEXT PRIMARY KEY,
                            name TEXT NOT NULL,
                            grade_level TEXT NOT NULL
                            );""";

        // Grades table linked to students using a Foreign Key (student_id)
        String sqlGrades = """
                        CREATE TABLE IF NOT EXISTS grades (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            student_id TEXT NOT NULL,
                            subject TEXT NOT NULL,
                            score INTEGER NOT NULL,
                            FOREIGN KEY (student_id) REFERENCES students (id)
                        );""";

        // Attendance table linked to students using a Foreign Key (student_id)
        // Note: INSERT OR REPLACE is used in SchoolSystem for simpler date-based updates
        String sqlAttendance = """
                            CREATE TABLE IF NOT EXISTS attendance (
                                student_id TEXT NOT NULL,
                                date TEXT NOT NULL,
                                status TEXT NOT NULL,
                                PRIMARY KEY (student_id, date),
                                FOREIGN KEY (student_id) REFERENCES students (id)
                               );""" // Stored as YYYY-MM-DD
        // PRESENT, ABSENT, LATE
        ;

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            // Execute SQL statements to create tables
            stmt.execute(sqlStudents);
            stmt.execute(sqlGrades);
            stmt.execute(sqlAttendance);
            
            System.out.println("Database 'school.db' initialized and tables verified.");
            
        } catch (SQLException e) {
            System.err.println(" Error initializing database: " + e.getMessage());
        }
    }
}