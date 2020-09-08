package com.nihad.attendance.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


import java.io.Serializable;

@Entity()
public class TotalAttendance  implements Serializable {




    @PrimaryKey(autoGenerate = true)
    private int t_attendance_id;

    private String t_date;
    private String t_time;

    @ColumnInfo(name = "attendance_member_id")
    private int t_member_id;

    public TotalAttendance(String t_date, String t_time, int t_member_id) {
        this.t_date = t_date;
        this.t_time = t_time;
        this.t_member_id = t_member_id;
    }

    private int t_fajar;
    private int t_duhar;
    private int t_asr;
    private int t_magrib;


    private int t_isha;

    public int getT_attendance_id() {
        return t_attendance_id;
    }

    public void setT_attendance_id(int t_attendance_id) {
        this.t_attendance_id = t_attendance_id;
    }

    public String getT_date() {
        return t_date;
    }

    public void setT_date(String t_date) {
        this.t_date = t_date;
    }

    public String getT_time() {
        return t_time;
    }

    public void setT_time(String t_time) {
        this.t_time = t_time;
    }

    public int getT_member_id() {
        return t_member_id;
    }

    public void setT_member_id(int t_member_id) {
        this.t_member_id = t_member_id;
    }

    public int getT_fajar() {
        return t_fajar;
    }

    public void setT_fajar(int t_fajar) {
        this.t_fajar = t_fajar;
    }

    public int getT_duhar() {
        return t_duhar;
    }

    public void setT_duhar(int t_duhar) {
        this.t_duhar = t_duhar;
    }

    public int getT_asr() {
        return t_asr;
    }

    public void setT_asr(int t_asr) {
        this.t_asr = t_asr;
    }

    public int getT_magrib() {
        return t_magrib;
    }

    public void setT_magrib(int t_magrib) {
        this.t_magrib = t_magrib;
    }

    public int getT_isha() {
        return t_isha;
    }

    public void setT_isha(int t_isha) {
        this.t_isha = t_isha;
    }
}
