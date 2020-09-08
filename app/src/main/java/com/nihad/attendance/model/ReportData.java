package com.nihad.attendance.model;

import androidx.room.Embedded;

public class ReportData {
    @Embedded
    PrayerModel prayerModel;

    public PrayerModel getPrayerModel() {
        return prayerModel;
    }

    public void setPrayerModel(PrayerModel prayerModel) {
        this.prayerModel = prayerModel;
    }

    public TodayAttendance getTodayAttendance() {
        return todayAttendance;
    }

    public void setTodayAttendance(TodayAttendance todayAttendance) {
        this.todayAttendance = todayAttendance;
    }

    @Embedded
    TodayAttendance todayAttendance;
}

