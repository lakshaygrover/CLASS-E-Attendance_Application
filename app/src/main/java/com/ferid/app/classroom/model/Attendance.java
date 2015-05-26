package com.ferid.app.classroom.model;

import java.io.Serializable;

/**
 * Created by ferid.cafer on 4/6/2015.
 */
public class Attendance implements Serializable {
    private int id;
    private int present;
    private String dateTime;
    private String classroomName;
    private String studentName;
    private int classroomId;
    private int studentId;
    private int presencePercentage;

    public Attendance() {
        id = 0;
        present = 0;
        dateTime = "";
        classroomName = "";
        studentName = "";
        classroomId = 0;
        studentId = 0;
        presencePercentage = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(int classroomId) {
        this.classroomId = classroomId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getPresencePercentage() {
        return presencePercentage;
    }

    public void setPresencePercentage(int presencePercentage) {
        this.presencePercentage = presencePercentage;
    }
}
