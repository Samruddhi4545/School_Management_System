package com.example;

/**
 * Course.java
 *
 * Simple data class to hold course information.
 * Now includes methods to generate HTML representations of its data.
 */
public class Course {
    private String courseId;
    private String name;
    private int credits;

    /**
     * Constructor for the Course class.
     * @param courseId Unique course identifier.
     * @param name Full name of the course.
     * @param credits Number of credits the course is worth.
     */
    public Course(String courseId, String name, int credits) {
        this.courseId = courseId;
        this.name = name;
        this.credits = credits;
    }

    // --- Getters ---
    public String getCourseId() { return courseId; }
    public String getName() { return name; }
    public int getCredits() { return credits; }

    /**
     * Generates an HTML string representing the course details.
     * @return A string containing HTML content.
     */
    public String toHtmlString() {
        return String.format(
            "<span class='inline-block bg-blue-100 text-blue-800 text-xs font-semibold mr-2 px-2.5 py-0.5 rounded-full'>%s (%d credits) - ID: %s</span>",
            name, credits, courseId
        );
    }
    
    /**
     * Console display is removed in favor of HTML output for web environment.
     */
    public void displayInfo() {
        System.out.println("Display info is now done via toHtmlString().");
    }
}
