package com.ferid.app.classroom.model;

import java.io.Serializable;

/**
 * Created by ferid.cafer on 4/3/2015.
 */
public class Student implements Serializable {
    private int id;
    private String name;
    private boolean isPresent;
    private int classroomStudentId;
    private String dateTime;
    private int attendanceId;

    public Student() {
        id = 0;
        name = "";
        isPresent = false;
        classroomStudentId = 0;
        dateTime = "";
        attendanceId = 0;
    }

    public Student(String name) {
        id = 0;
        this.name = name;
        isPresent = false;
        classroomStudentId = 0;
        dateTime = "";
        attendanceId = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean isPresent) {
        this.isPresent = isPresent;
    }

    public int getClassroomStudentId() {
        return classroomStudentId;
    }

    public void setClassroomStudentId(int classroomStudentId) {
        this.classroomStudentId = classroomStudentId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }
}
