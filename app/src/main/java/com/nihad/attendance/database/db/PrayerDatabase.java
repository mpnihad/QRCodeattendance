package com.nihad.attendance.database.db;



import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.nihad.attendance.Utils.Converters;
import com.nihad.attendance.database.dao.DaoAccess;
import com.nihad.attendance.model.PrayerModel;
import com.nihad.attendance.model.TodayAttendance;
import com.nihad.attendance.model.TodaysAttendance;
import com.nihad.attendance.model.TotalAttendance;


@Database(entities = {PrayerModel.class, TodaysAttendance.class, TodayAttendance.class, TotalAttendance.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class PrayerDatabase extends RoomDatabase {

    public abstract DaoAccess daoAccess();


    private static final Object sLock = new Object();

    private static PrayerDatabase INSTANCE;





}

