package com.cbt02.attendanceapp.models;

public class AttendanceRecord {
    private String studentName;
    private int grade;
    private String group;
    private String date;
    private String entryTime;
    private String exitTime;

    public AttendanceRecord(String studentName, int grade, String group, String date, String entryTime, String exitTime) {
        this.studentName = studentName;
        this.grade = grade;
        this.group = group;
        this.date = date;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
    }
    // Getters
    public String getStudentName() { return studentName; }
    public int getGrade() { return grade; }
    public String getGroup() { return group; }
    public String getDate() { return date; }
    public String getEntryTime() { return entryTime; }
    public String getExitTime() { return exitTime; }
}