package com.nihad.attendance.database.dao;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;


import com.nihad.attendance.model.ReportData;
import com.nihad.attendance.model.ReportTotData;
import com.nihad.attendance.model.TodayAttendance;
import com.nihad.attendance.model.TodaysAttendance;
import com.nihad.attendance.model.PrayerModel;

import java.util.ArrayList;
import java.util.List;


@Dao
public interface DaoAccess {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertTask(TodaysAttendance Report_db_model);


    @Query("SELECT * FROM TodaysAttendance ORDER BY id desc")
    public abstract DataSource.Factory<Integer, TodaysAttendance> fetchAllTasks_group();



    @Query("SELECT * FROM TodaysAttendance WHERE id =:taskId")
    LiveData<TodaysAttendance> getTask_group(int taskId);


    @Update
    void updateTask(TodaysAttendance todaysAttendance);


    @Update
    int updateMember(PrayerModel prayerModel);


    @Delete
    void deleteTask(TodaysAttendance todaysAttendance);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long insertMember(PrayerModel prayerModel);

    @Query("SELECT * FROM TodayAttendance WHERE attendance_member_id =:member_id")
    List<TodayAttendance> getCountAttendance(int member_id);

    @Query("SELECT * FROM prayermodel WHERE prev_member_id =:member_id")
    PrayerModel countPrevMember(int member_id);


    @Query("SELECT * FROM prayermodel WHERE member_id =:member_id")
    PrayerModel getMember(int member_id);

    @Update
    int updateAttendance(TodayAttendance todayAttendance);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertAttendance(TodayAttendance todayAttendance);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertMemberafter(PrayerModel prayerModel);

    @Query("SELECT TodayAttendance.*, prayermodel.* FROM TodayAttendance INNER JOIN prayermodel ON prayermodel.member_id = TodayAttendance.attendance_member_id")
    List<ReportData> getAllTodayAttendance();

    @Query("SELECT TodayAttendance.*, prayermodel.* FROM TodayAttendance INNER JOIN prayermodel ON prayermodel.member_id = TodayAttendance.attendance_member_id WHERE TodayAttendance.date=:date")
    DataSource.Factory<Integer, ReportData> getAllReportAttendance(String date);

    @Query("SELECT COUNT(*) FROM TodayAttendance WHERE date=:date")
    LiveData<Integer> getCount(String date);

    @RawQuery()
    boolean  moveData(SupportSQLiteQuery simpleSQLiteQuery);

    @Query("DELETE FROM TodayAttendance ")
    int deleteData();

    @Query("SELECT TotalAttendance.*, prayermodel.* FROM TotalAttendance INNER JOIN prayermodel ON prayermodel.member_id = TotalAttendance.attendance_member_id WHERE TotalAttendance.t_date=:date")
    DataSource.Factory<Integer, ReportTotData> getAllReporttotalAttendance(String date);


    @Query("SELECT * FROM PrayerModel WHERE name like :search OR phoneNo like :search")
    DataSource.Factory<Integer, PrayerModel> getSearchMember(String search);
}
