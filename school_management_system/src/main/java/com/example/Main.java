package com.example;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SchoolManagementSystem.java
 *
 * The main application class for managing students, teachers, and courses.
 * Refactored to generate a single HTML string for web display instead of console output.
 */
public class Main {

    private List<Student> students;
    private List<Teacher> teachers;
    private List<Course> courses;

    /**
     * Constructor initializes the lists.
     */
    public Main() {
        this.students = new ArrayList<>();
        this.teachers = new ArrayList<>();
        this.courses = new ArrayList<>();
    }

    // --- Management Methods ---

    public void addStudent(Student student) {
        students.add(student);
        System.out.println("\n[SYSTEM] Added Student: " + student.getName());
    }

    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
        System.out.println("[SYSTEM] Added Teacher: " + teacher.getName());
    }

    public void addCourse(Course course) {
        courses.add(course);
        System.out.println("[SYSTEM] Added Course: " + course.getName());
    }

    /**
     * Finds a student by name and records a grade.
     * @param studentName The name of the student.
     * @param courseName The name of the course.
     * @param grade The grade to record.
     */
    public void recordGrade(String studentName, String courseName, double grade) {
        Optional<Student> studentOpt = students.stream()
            .filter(s -> s.getName().equalsIgnoreCase(studentName))
            .findFirst();

        if (studentOpt.isPresent()) {
            studentOpt.get().recordGrade(courseName, grade);
        } else {
            System.err.println("[ERROR] Student '" + studentName + "' not found to record grade.");
        }
    }

    /**
     * Generates a single, comprehensive HTML report string.
     * This string simulates the output served to the localhost web page.
     * @return A String containing the full, styled HTML report body.
     */
    public String generateHtmlReport() {
        StringBuilder html = new StringBuilder();
        
        // --- Report Header ---
        html.append("<div class='p-8 bg-white shadow-2xl rounded-xl'>");
        html.append("<h1 class='text-4xl font-extrabold text-gray-900 mb-2 border-b-4 border-indigo-500 pb-2'>School Management System</h1>");
        html.append("<p class='text-gray-600 mb-8'>Real-time System Status and Reports</p>");

        // --- COURSES SECTION ---
        html.append("<h2 class='text-2xl font-bold text-indigo-700 mb-4 mt-6'>Courses Offered (").append(courses.size()).append(")</h2>");
        html.append("<div class='flex flex-wrap gap-2 mb-8 p-4 border rounded-lg bg-gray-50'>");
        courses.forEach(course -> html.append(course.toHtmlString()));
        html.append("</div>");

        // --- TEACHERS SECTION ---
        html.append("<h2 class='text-2xl font-bold text-yellow-700 mb-4'>Faculty (").append(teachers.size()).append(")</h2>");
        html.append("<div class='grid md:grid-cols-2 gap-4 mb-8'>");
        teachers.forEach(teacher -> html.append(teacher.toHtmlString()));
        html.append("</div>");
        
        // --- STUDENTS SECTION ---
        html.append("<h2 class='text-2xl font-bold text-green-700 mb-4'>Student Academic Reports (").append(students.size()).append(")</h2>");
        html.append("<div class='grid md:grid-cols-2 gap-6'>");
        students.forEach(student -> html.append(student.toHtmlString()));
        html.append("</div>");
        
        html.append("</div>"); // Close main container
        
        return html.toString();
    }

    /**
     * Main method to demonstrate the system functionality and generate the final HTML string.
     */
    public static void main(String[] args) {
        Main sms = new Main();

        // 1. Setup Data
        Course math = new Course("C101", "Advanced Calculus", 4);
        Course history = new Course("C202", "World History", 3);

        Teacher mrSmith = new Teacher("T001", "Mr. Smith", "smith@school.edu");
        mrSmith.addSubject("Advanced Calculus");
        mrSmith.addSubject("Algebra");
        
        Teacher msJones = new Teacher("T002", "Ms. Jones", "jones@school.edu");
        msJones.addSubject("Literature");

        Student alice = new Student("S001", "Alice Johnson", "alice@student.edu");
        Student bob = new Student("S002", "Bob Williams", "bob@student.edu");

        // 2. Add to System
        sms.addCourse(math);
        sms.addCourse(history);
        sms.addTeacher(mrSmith);
        sms.addTeacher(msJones);
        sms.addStudent(alice);
        sms.addStudent(bob);

        // 3. Perform Operations
        System.out.println("\n--- RECORDING GRADES (Actions that would occur on the server) ---");
        sms.recordGrade("Alice Johnson", math.getName(), 4.0); // A
        sms.recordGrade("Alice Johnson", history.getName(), 3.5); // B+
        sms.recordGrade("Bob Williams", math.getName(), 3.0); // B
        
        // 4. Generate the HTML report string
        String htmlReport = sms.generateHtmlReport();
        
        // In a real server environment, this string would be sent to the client.
        // For demonstration, we print the first 200 characters and the last 50.
        System.out.println("\n--- GENERATED HTML REPORT STRING (200 chars preview) ---");
        System.out.println(htmlReport.substring(0, Math.min(200, htmlReport.length())) + "...");
    }
}
