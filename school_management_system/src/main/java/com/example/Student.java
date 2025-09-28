package com.example;

import java.util.HashMap;
import java.util.Map;

/**
 * Student.java
 *
 * Concrete class representing a student, inheriting from Person.
 * Now includes methods to generate HTML representations of its data.
 */
public class Student extends Person {
    // Map to store grades: Key = Course Name, Value = Grade (e.g., 4.0 for A)
    private Map<String, Double> grades;

    /**
     * Constructor for the Student class.
     * @param studentId Unique student ID.
     * @param name Student's name.
     * @param email Student's email.
     */
    public Student(String studentId, String name, String email) {
        super(studentId, name, email);
        this.grades = new HashMap<>();
    }

    /**
     * Adds or updates a grade for a specific course.
     * @param courseName The name of the course.
     * @param grade The numeric grade achieved.
     */
    public void recordGrade(String courseName, double grade) {
        grades.put(courseName, grade);
        // Note: For a web app, status messages would often be returned, not printed.
        // We keep the print for demonstration of the action happening.
        System.out.println("-> Grade " + grade + " recorded for " + getName() + " in " + courseName);
    }

    /**
     * Calculates the student's Grade Point Average (GPA).
     * @return The calculated GPA, or 0.0 if no grades exist.
     */
    public double calculateGPA() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double totalGradePoints = 0.0;
        for (double grade : grades.values()) {
            totalGradePoints += grade;
        }
        return totalGradePoints / grades.size();
    }

    /**
     * Generates an HTML string representing the student's academic report card.
     * @return A string containing HTML content.
     */
    public String toHtmlString() {
        StringBuilder html = new StringBuilder();
        double gpa = calculateGPA();
        
        // Tailwind classes for a structured card layout
        html.append("<div class='bg-white shadow-xl rounded-lg p-6 mb-6 border-t-4 border-indigo-500'>");
        html.append("<h3 class='text-xl font-bold text-gray-800 mb-2'>").append(getName()).append(" (ID: ").append(getId()).append(")</h3>");
        html.append("<p class='text-sm text-gray-500 mb-4'>").append(getEmail()).append("</p>");
        
        html.append("<div class='flex justify-between items-center mb-4 p-3 bg-indigo-50 rounded-lg'>");
        html.append("<span class='text-md font-semibold text-indigo-700'>GPA</span>");
        html.append(String.format("<span class='text-2xl font-extrabold text-indigo-600'>%.2f</span>", gpa));
        html.append("</div>");

        if (!grades.isEmpty()) {
            html.append("<h4 class='font-semibold text-gray-700 mb-2'>Course Grades:</h4>");
            html.append("<ul class='list-disc pl-5 space-y-1'>");
            for (Map.Entry<String, Double> entry : grades.entrySet()) {
                // Determine color based on grade (simple logic for visualization)
                String color = entry.getValue() >= 3.5 ? "text-green-600" : (entry.getValue() >= 2.5 ? "text-yellow-600" : "text-red-600");
                html.append("<li class='text-gray-600 flex justify-between'>");
                html.append("<span>").append(entry.getKey()).append("</span>");
                html.append("<span class='font-mono font-bold ").append(color).append("'>").append(entry.getValue()).append("</span>");
                html.append("</li>");
            }
            html.append("</ul>");
        } else {
            html.append("<p class='text-gray-500 italic'>No grades recorded yet.</p>");
        }
        
        html.append("</div>");
        return html.toString();
    }
    
    // Abstract method implementation is now delegated to the HTML output helper.
    @Override
    public void displayInfo() {
        // Console display is removed in favor of HTML output for web environment
        System.out.println("Display info is now done via toHtmlString().");
    }
}
