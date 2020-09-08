package com.nihad.attendance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseClass {

    @SerializedName("results")
    @Expose
    private List<TodaysAttendance> todaysAttendances = null;

    public List<TodaysAttendance> getTodaysAttendances() {
        return todaysAttendances;
    }

    public void setTodaysAttendances(List<TodaysAttendance> students) {
        this.todaysAttendances = students;
    }

}