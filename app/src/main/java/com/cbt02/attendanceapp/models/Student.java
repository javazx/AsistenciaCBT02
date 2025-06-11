package com.cbt02.attendanceapp.models;

public class Student {
    private long id;
    private String fullName;
    private int grade;
    private String group;

    public Student(long id, String fullName, int grade, String group) {
        this.id = id;
        this.fullName = fullName;
        this.grade = grade;
        this.group = group;
    }
    // Getters y Setters
    public long getId() { return id; }
    public String getFullName() { return fullName; }
    public int getGrade() { return grade; }
    public String getGroup() { return group; }
}