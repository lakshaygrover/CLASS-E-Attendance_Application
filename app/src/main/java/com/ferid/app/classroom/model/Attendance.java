/*
 * Copyright (C) 2015 Ferid Cafer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
