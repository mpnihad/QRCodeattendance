package com.nihad.attendance.model;

import androidx.room.Embedded;

public class ReportTotData {
    @Embedded
    PrayerModel prayerModel;

    public PrayerModel getPrayerModel() {
        return prayerModel;
    }

    public void setPrayerModel(PrayerModel prayerModel) {
        this.prayerModel = prayerModel;
    }

    public TotalAttendance getTotalAttendance() {
        return totalAttendance;
    }

    public void setTotalAttendance(TotalAttendance totalAttendance) {
        this.totalAttendance = totalAttendance;
    }

    @Embedded
    TotalAttendance totalAttendance;
}

