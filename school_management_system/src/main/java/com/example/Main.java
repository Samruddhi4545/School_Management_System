package com.example;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Initialize the Scanner for user input
        Scanner scanner = new Scanner(System.in);
        
        // -------------------------------------------------------------------
        // --- NEW CODE START: DATABASE INITIALIZATION ---
        // -------------------------------------------------------------------
        
        // 2. CALL THE INITIALIZER: This ensures the school.db file and tables exist.
        DatabaseManager.initializeDatabase();
        
        // -------------------------------------------------------------------
        // --- NEW CODE END ---
        // -------------------------------------------------------------------
        
        // Initialize the core system manager
        // This is now safe, as the SchoolSystem will rely on the initialized DB
        SchoolSystem system = new SchoolSystem();
        
        System.out.println("=============================================");
        System.out.println("  SCHOOL MANAGEMENT SYSTEM (CLI Version)");
        System.out.println("=============================================");

        boolean running = true;
        while (running) {
            printMenu();
            
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline
                
                switch (choice) {
                    case 1:
                        addStudent(system, scanner);
                        break;
                    case 2:
                        recordGrade(system, scanner);
                        break;
                    case 3:
                        recordAttendance(system, scanner);
                        break;
                    case 4:
                        viewStudentDetails(system, scanner);
                        break;
                    case 5:
                        viewAllStudents(system);
                        break;
                    case 6:
                        System.out.println(" Exiting System. Data saved in school.db.");
                        running = false;
                        break;
                    default:
                        System.out.println(" Invalid choice. Please select a number from the menu.");
                }
            } else {
                System.out.println(" Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
            }
            System.out.println("---------------------------------------------");
        }
        scanner.close();
    }

    // --- MENU DISPLAY ---
    private static void printMenu() {
        System.out.println("\nSelect an action:");
        System.out.println("1. Add New Student");
        System.out.println("2. Record Grade");
        System.out.println("3. Record Attendance");
        System.out.println("4. View Student Details & Averages");
        System.out.println("5. View All Students (Summary)");
        System.out.println("6. Exit");
        System.out.print("Enter choice: ");
    }

    // --- MENU HANDLERS (No changes needed here as they call methods in SchoolSystem) ---

    private static void addStudent(SchoolSystem system, Scanner scanner) {
        System.out.print("Enter Student id:");
        String id = scanner.nextLine();
        System.out.print("Enter Student Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Grade Level: ");
        String grade = scanner.nextLine();

        // Create a new Student object
        Student newStudent = new Student(id, name, grade);
        // system.addStudent now saves to the database
        system.addStudent(newStudent);
    }

    private static void recordGrade(SchoolSystem system, Scanner scanner) {
        System.out.print("Enter Student ID to record grade: ");
        String id = scanner.nextLine();
        System.out.print("Enter Subject Name (e.g., Math, History): ");
        String subject = scanner.nextLine();

        System.out.print("Enter Score (0-100): ");
        if (scanner.hasNextInt()) {
            int score = scanner.nextInt();
            scanner.nextLine();
            // system.recordGrade now saves to the database
            system.recordGrade(id, subject, score);
        } else {
            System.out.println(" Invalid score entered.");
            scanner.nextLine();
        }
    }

    private static void recordAttendance(SchoolSystem system, Scanner scanner) {
        System.out.print("Enter Student ID to mark attendance: ");
        String id = scanner.nextLine();
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();
        System.out.print("Enter Status (Present/Absent/Late): ");
        String status = scanner.nextLine();

        try {
            LocalDate date = LocalDate.parse(dateStr);
            // system.recordAttendance now saves to the database
            system.recordAttendance(id, date, status);
        } catch (DateTimeParseException e) {
            System.out.println(" Invalid date format. Please use YYYY-MM-DD.");
        }
    }

    private static void viewStudentDetails(SchoolSystem system, Scanner scanner) {
        System.out.print("Enter Student ID to view details: ");
        String id = scanner.nextLine();
        // system.findStudentById now loads data from the database
        Student foundStudent = system.findStudentById(id);

        if (foundStudent != null) {
            System.out.println("\n--- Student Details ---");
            System.out.println(foundStudent); // Uses the Student's toString method
            
            // Calculate and display averages
            double overallAvg = system.calculateOverallAverage(id);
            System.out.printf("  Overall Average Score: %.2f%%\n", overallAvg);
            
            // Display subject averages
            Map<String, List<Integer>> grades = foundStudent.getGrades();
            if (!grades.isEmpty()) {
                System.out.println("  Subject Averages:");
                for (String subject : grades.keySet()) {
                    double subjectAvg = system.calculateSubjectAverage(id, subject);
                    System.out.printf("    - %s: %.2f%%\n", subject, subjectAvg);
                }
            } else {
                System.out.println("  No grades recorded yet.");
            }
            
        } else {
            System.out.println(" Student not found with ID: " + id);
        }
    }
    
    private static void viewAllStudents(SchoolSystem system) {
        // system.getAllStudents now loads data from the database
        List<Student> allStudents = system.getAllStudents();
        if (allStudents.isEmpty()) {
            System.out.println("No students registered in the system yet.");
            return;
        }

        System.out.println("\n--- All Registered Students (Summary) ---");
        for (Student s : allStudents) {
            System.out.println(s);
        }
    }
}