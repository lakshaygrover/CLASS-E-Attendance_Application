/*
 * Copyright (C) 2016 Ferid Cafer
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

/**
 * Created by ferid.cafer on 4/3/2015.
 */
public class Student {
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
