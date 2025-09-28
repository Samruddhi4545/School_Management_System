package com.example;

/**
 * Person.java
 *
 * Abstract base class for all individuals in the school system (Students and Teachers).
 * Demonstrates abstraction and common properties.
 */
public abstract class Person {
    private String id;
    private String name;
    private String email;

    /**
     * Constructor for the Person class.
     * @param id Unique identifier for the person.
     * @param name Full name of the person.
     * @param email Email address of the person.
     */
    public Person(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // --- Abstract method ---
    /**
     * Abstract method that must be implemented by subclasses (Student and Teacher)
     * to display their specific details.
     */
    public abstract void displayInfo();
}
