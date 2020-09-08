package com.nihad.attendance.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


import java.io.Serializable;

@Entity()
public class TodayAttendance  implements Serializable {



    @PrimaryKey(autoGenerate = true)
    private int attendance_id;

    private String date;
    private String time;

    @ColumnInfo(name = "attendance_member_id")
    private int member_id;

    private int fajar;
    private int duhar;
    private int asr;
    private int magrib;

    public TodayAttendance( String date, String time, int member_id) {

        this.date = date;
        this.time = time;
        this.member_id = member_id;

    }



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAttendance_id() {
        return attendance_id;
    }

    public void setAttendance_id(int attendance_id) {
        this.attendance_id = attendance_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMember_id() {
        return member_id;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
    }

    public int getFajar() {
        return fajar;
    }

    public void setFajar(int fajar) {
        this.fajar = fajar;
    }

    public int getDuhar() {
        return duhar;
    }

    public void setDuhar(int duhar) {
        this.duhar = duhar;
    }

    public int getAsr() {
        return asr;
    }

    public void setAsr(int asr) {
        this.asr = asr;
    }

    public int getMagrib() {
        return magrib;
    }

    public void setMagrib(int magrib) {
        this.magrib = magrib;
    }

    public int getIsha() {
        return isha;
    }

    public void setIsha(int isha) {
        this.isha = isha;
    }

    private int isha;


}
