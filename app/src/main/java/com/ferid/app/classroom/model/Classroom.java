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
 * Created by ferid.cafer on 4/3/2015.
 */
public class Classroom implements Parcelable {
    private int id;
    private String name;
    private int studentNumber;

    public Classroom() {
        id = 0;
        name = "";
        studentNumber = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(studentNumber);
    }

    public static final Parcelable.Creator<Classroom> CREATOR
            = new Parcelable.Creator<Classroom>() {
        public Classroom createFromParcel(Parcel in) {
            return new Classroom(in);
        }

        public Classroom[] newArray(int size) {
            return new Classroom[size];
        }
    };

    private Classroom(Parcel in) {
        id = in.readInt();
        name = in.readString();
        studentNumber = in.readInt();
    }
}
