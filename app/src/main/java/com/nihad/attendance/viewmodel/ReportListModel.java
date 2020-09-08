package com.nihad.attendance.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.nihad.attendance.database.repository.PrayerModelRepository;
import com.nihad.attendance.model.ReportData;
import com.nihad.attendance.model.ReportTotData;
import com.nihad.attendance.model.TodaysAttendance;


public class ReportListModel extends AndroidViewModel {
    private PrayerModelRepository repository;
    private LiveData<PagedList<TodaysAttendance>> allNotes;

    public ReportListModel(@NonNull Application application) {
        super(application);
        repository = new PrayerModelRepository(application);
        allNotes = repository.getTasks();
    }

    public PrayerModelRepository getRepository() {
        return repository;
    }

    public void insert(TodaysAttendance todaysAttendance) {
        repository.insertTask(todaysAttendance);
    }

    public void update(TodaysAttendance todaysAttendance) {
        repository.updateTask(todaysAttendance);
    }

    public void delete(TodaysAttendance todaysAttendance) {
        repository.deleteTask(todaysAttendance);
    }
    public LiveData<Integer> getcount(String date) {
        return repository.getcount(date);
    }



    public LiveData<PagedList<ReportData>> getAllReport(String date) {
        return repository.getAllReportAttendance(date);
    }

    public LiveData<PagedList<ReportTotData>> getAllReportofDate(String date) {
        return repository.getAllReporttotalAttendance(date);
    }


}