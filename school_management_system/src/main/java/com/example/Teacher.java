package com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Teacher.java
 *
 * Concrete class representing a teacher, inheriting from Person.
 * Now includes methods to generate HTML representations of its data.
 */
public class Teacher extends Person {
    private List<String> subjectsTaught;

    /**
     * Constructor for the Teacher class.
     * @param teacherId Unique teacher ID.
     * @param name Teacher's name.
     * @param email Teacher's email.
     */
    public Teacher(String teacherId, String name, String email) {
        super(teacherId, name, email);
        this.subjectsTaught = new ArrayList<>();
    }

    /**
     * Adds a subject to the teacher's list of subjects taught.
     * @param subject The name of the subject.
     */
    public void addSubject(String subject) {
        subjectsTaught.add(subject);
    }

    /**
     * Generates an HTML string representing the teacher's profile card.
     * @return A string containing HTML content.
     */
    public String toHtmlString() {
        StringBuilder html = new StringBuilder();
        
        // Tailwind classes for a clean card layout
        html.append("<div class='bg-gray-50 border-l-4 border-yellow-500 p-4 mb-4 rounded-lg shadow-md'>");
        html.append("<h4 class='text-lg font-semibold text-gray-800'>").append(getName()).append("</h4>");
        html.append("<p class='text-sm text-gray-600'>ID: ").append(getId()).append(" | ").append(getEmail()).append("</p>");
        
        String subjects = subjectsTaught.isEmpty() ? "No subjects assigned" : String.join(", ", subjectsTaught);
        
        html.append("<p class='mt-2 text-sm text-gray-700 font-medium'>Subjects: ");
        html.append("<span class='font-normal text-yellow-700'>").append(subjects).append("</span>");
        html.append("</p>");
        
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
