package com.nihad.attendance.view.view;

import android.content.Context;
import android.content.SharedPreferences;

import com.nihad.attendance.model.PrayerModel;
import com.nihad.attendance.model.ReportData;
import com.nihad.attendance.model.TodaysAttendance;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

public interface MainActivityCallback {
    void visibleProgress(boolean b);

    SharedPreferences getSp();

     void showList(ArrayList<TodaysAttendance> todaysAttendances);

    void makeToast(String message);

    void repotView(ReportData reportData);

    Context getContext();

    CompositeDisposable getdisposible();

    void repotViews(PrayerModel item);
}
