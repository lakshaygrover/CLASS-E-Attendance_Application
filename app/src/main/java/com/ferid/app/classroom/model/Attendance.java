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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ferid.cafer on 4/6/2015.
 */
public class Attendance implements Parcelable {
    private int id;
    private int classroomId;
    private int studentId;

    private int present;
    private int presencePercentage;

    private String dateTime;
    private String classroomName;
    private String studentName;


    public Attendance() {
        id = 0;
        classroomId = 0;
        studentId = 0;

        present = 0;
        presencePercentage = 0;

        dateTime = "";
        classroomName = "";
        studentName = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public int getPresencePercentage() {
        return presencePercentage;
    }

    public void setPresencePercentage(int presencePercentage) {
        this.presencePercentage = presencePercentage;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(classroomId);
        dest.writeInt(studentId);

        dest.writeInt(present);
        dest.writeInt(presencePercentage);

        dest.writeString(dateTime);
        dest.writeString(classroomName);
        dest.writeString(studentName);
    }

    public static final Parcelable.Creator<Attendance> CREATOR
            = new Parcelable.Creator<Attendance>() {
        public Attendance createFromParcel(Parcel in) {
            return new Attendance(in);
        }

        public Attendance[] newArray(int size) {
            return new Attendance[size];
        }
    };

    private Attendance(Parcel in) {
        id = in.readInt();
        classroomId = in.readInt();
        studentId = in.readInt();

        present = in.readInt();
        presencePercentage = in.readInt();

        dateTime = in.readString();
        classroomName = in.readString();
        studentName = in.readString();
    }
}
