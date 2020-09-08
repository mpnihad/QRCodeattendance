package com.nihad.attendance.database.repository;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.room.Room;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.nihad.attendance.Utils.ExistingMemberAction;
import com.nihad.attendance.database.db.PrayerDatabase;
import com.nihad.attendance.model.ReportData;
import com.nihad.attendance.model.ReportTotData;
import com.nihad.attendance.model.TodayAttendance;
import com.nihad.attendance.model.TodaysAttendance;
import com.nihad.attendance.model.PrayerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PrayerModelRepository {

    private String DB_NAME = "prayer_db";

    private PrayerDatabase prayerDatabase;


    public MutableLiveData<ExistingMemberAction> getExistingMemberActionMutableLiveData() {
        return existingMemberActionMutableLiveData;
    }

    public void setExistingMemberActionMutableLiveData(MutableLiveData<ExistingMemberAction> existingMemberActionMutableLiveData) {
        this.existingMemberActionMutableLiveData = existingMemberActionMutableLiveData;
    }

    MutableLiveData<ExistingMemberAction> existingMemberActionMutableLiveData;

    public PrayerModelRepository(Context context) {
        prayerDatabase = Room.databaseBuilder(context, PrayerDatabase.class, DB_NAME)
                .build();

         existingMemberActionMutableLiveData=new MutableLiveData<>();
    }


    public void insertTask(String title,
                           String description) {

      //  insertTask(title, description, false, null);
    }

    public void insertTask1(TodaysAttendance Report_db_Model) {


//        note.setTitle(title);
//        note.setDescription(description);
//        note.setCreatedAt(AppUtils.getCurrentDateTime());
//        note.setModifiedAt(AppUtils.getCurrentDateTime());
//        note.setEncrypt(encrypt);
//
//
//        if(encrypt) {
//            note.setPassword(AppUtils.generateHash(password));
//        } else note.setPassword(null);

        insertTask(Report_db_Model);
    }


    public void insertTask(final TodaysAttendance Report_db_Model) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    prayerDatabase.daoAccess().insertTask(Report_db_Model);
                }catch (Exception e){

                }
                return null;
            }
        }.execute();
    }
//    public void insertMember(final PrayerModel prayerModel) {
//        final Long[] id = new Long[1];
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                try {
//                    prayerDatabase.daoAccess().insertMember(prayerModel);
//                }catch (Exception e){
//
//                }
//                return null;
//            }
//        }.execute();
//    }

    public void updateTask(final TodaysAttendance Report_db_Model) {
//        note.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                prayerDatabase.daoAccess().updateTask(Report_db_Model);
                return null;
            }
        }.execute();
    }

    public void deleteTask(final int id) {
        final LiveData<TodaysAttendance> task = getTask(id);
        if(task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    prayerDatabase.daoAccess().deleteTask(task.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public void deleteTask(final TodaysAttendance Report_db_Model) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                prayerDatabase.daoAccess().deleteTask(Report_db_Model);
                return null;
            }
        }.execute();
    }

    public LiveData<TodaysAttendance> getTask(int id) {
        return prayerDatabase.daoAccess().getTask_group(id);
    }

    public LiveData<PagedList<TodaysAttendance>> getTasks() {

        PagedList.Config pagedListConfig=(new PagedList.Config.Builder()).setEnablePlaceholders(true)
                .setPrefetchDistance(10)
                .setPageSize(20).build();

        LiveData<PagedList<TodaysAttendance>> ReportList;
        ReportList = new LivePagedListBuilder<>(
                prayerDatabase.daoAccess().fetchAllTasks_group(), pagedListConfig).build();
        return ReportList;
    }


    public LiveData<Integer> getcount(String date) {
        return prayerDatabase.daoAccess().getCount(date);
    }

    public LiveData<Long> insertMember(final PrayerModel prayerModel, MutableLiveData<PrayerModel> insertObservable) {

        MutableLiveData<Long> id=new MutableLiveData<>();
        Observable.fromCallable(new Callable<PrayerModel>() {
            @Override
            public PrayerModel call() throws Exception {
                Long id=  prayerDatabase.daoAccess().insertMember(prayerModel);
                if(id == -1)
                {

                    PrayerModel prayerModel1=prayerDatabase.daoAccess().getMember(prayerModel.getMember_id());
                    return prayerModel1;
                }
                else
                {
                    prayerModel.setMember_id(id.intValue());
                    return prayerModel;
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<PrayerModel>() {
            @Override
            public void accept(PrayerModel prayerModel1) throws Exception {
//                id.postValue(aLong);

                    insertObservable.setValue(prayerModel1);

            }
        });

return id;

    }


    public void updateMember(final PrayerModel prayerModel) {

        MutableLiveData<Long> id=new MutableLiveData<>();

        Observable.fromCallable(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                return prayerDatabase.daoAccess().updateMember(prayerModel);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {

            }
        });
//        Observable.fromCallable(new Callable<void>() {
//            @Override
//            public void call() throws Exception {
//
//            }
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<void>() {
//            @Override
//            public void accept(void aLong) throws Exception {
////                id.postValue(aLong);
//            }
//        });


    }

    public TodayAttendance countAttendance(int member_id, MutableLiveData<TodayAttendance> attendanceObservable) {
         TodayAttendance liveData = null;
        Observable.fromCallable(new Callable<List<TodayAttendance>>() {

            @Override
            public List<TodayAttendance> call() throws Exception {
                return  prayerDatabase.daoAccess().getCountAttendance(member_id);

//
//                return todaysattendance;
            }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<TodayAttendance>>() {
            @Override
            public void accept(List<TodayAttendance> todayAttendance) throws Exception {
                List<TodayAttendance> todaysattendance=todayAttendance;
                if (todaysattendance.size() == 0) {
                    TodayAttendance todayAttendances  = new TodayAttendance("", "-1", member_id);
                    todayAttendances.setMember_id(member_id);
                    todaysattendance.add(todayAttendances);
                }
                attendanceObservable.setValue(todayAttendance.get(0));

            }

        });

         return liveData;
    }




    public void checkPrevMember(int member_id, String macaddress, MutableLiveData<PrayerModel> checkprevObservable) {

        Observable.fromCallable(new Callable<PrayerModel>() {

            @Override
            public PrayerModel call() throws Exception {
                PrayerModel prayerModel=prayerDatabase.daoAccess().countPrevMember(member_id);
                if(prayerModel==null)
                {
                    prayerModel=new PrayerModel("","","","","");
                    prayerModel.setMember_id(-1);
                }
                return prayerModel;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<PrayerModel>() {
            @Override
            public void accept(PrayerModel prayerModel) throws Exception {

                checkprevObservable.setValue(prayerModel);
            }

        });

    }

    public void updateAttendance(TodayAttendance todayAttendance) {



        Observable.fromCallable(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                return prayerDatabase.daoAccess().updateAttendance(todayAttendance);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {

            }
        });

    }

    public void insertAttendance(TodayAttendance todayAttendance) {

        MutableLiveData<Long> id=new MutableLiveData<>();
        Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return  prayerDatabase.daoAccess().insertAttendance(todayAttendance);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                id.postValue(aLong);
            }
        });
    }


    public LiveData<Long> insertMemberafter(PrayerModel prayerModel) {

        MutableLiveData<Long> id=new MutableLiveData<>();
        Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return  prayerDatabase.daoAccess().insertMemberafter(prayerModel);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                id.postValue(aLong);
            }
        });

        return id;

    }

    public void getAlltodaysAttendance(MutableLiveData<ArrayList<ReportData>> mutableTodaysAttendance) {


        Observable.fromCallable(new Callable<List<ReportData>>() {

            @Override
            public List<ReportData> call() throws Exception {
                return  prayerDatabase.daoAccess().getAllTodayAttendance();

//
//                return todaysattendance;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ReportData>>() {
            @Override
            public void accept(List<ReportData> todayAttendance) throws Exception {

                mutableTodaysAttendance.setValue((ArrayList<ReportData>) todayAttendance);

            }

        });

    }

    public LiveData<PagedList<ReportData>> getAllReportAttendance(String date) {

        PagedList.Config pagedListConfig=(new PagedList.Config.Builder()).setEnablePlaceholders(true)
                .setPrefetchDistance(10)
                .setPageSize(20).build();

        LiveData<PagedList<ReportData>> ReportList;

        ReportList = new LivePagedListBuilder<>(
                prayerDatabase.daoAccess().getAllReportAttendance(date), pagedListConfig).build();
        return ReportList;



    }

    public LiveData<PagedList<ReportTotData>> getAllReporttotalAttendance(String date) {

        PagedList.Config pagedListConfig=(new PagedList.Config.Builder()).setEnablePlaceholders(true)
                .setPrefetchDistance(10)
                .setPageSize(20).build();

        LiveData<PagedList<ReportTotData>> ReportList;

        ReportList = new LivePagedListBuilder<>(
                prayerDatabase.daoAccess().getAllReporttotalAttendance(date), pagedListConfig).build();
        return ReportList;



    }

    public void moveData(MutableLiveData<Integer> mutableSendResponse) {
        Observable.fromCallable(new Callable<Boolean >() {

            @Override
            public Boolean  call() throws Exception {
                return  prayerDatabase.daoAccess().moveData(new SimpleSQLiteQuery("INSERT INTO TotalAttendance SELECT * FROM TodayAttendance ;"));

//
//                return todaysattendance;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean todayAttendance) throws Exception {




                Observable.fromCallable(new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        return prayerDatabase.daoAccess().deleteData();

//
//                return todaysattendance;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer deletedcount) throws Exception {




                        mutableSendResponse.setValue(deletedcount);

                    }

                });




            }

        });
    }


    public void getSearchMember(String s, MutableLiveData<ExistingMemberAction> action) {

        PagedList.Config pagedListConfig=(new PagedList.Config.Builder()).setEnablePlaceholders(true)
                .setPrefetchDistance(10)
                .setPageSize(20).build();

        LiveData<PagedList<PrayerModel>> pagedListPrayerModelLiveData;

        pagedListPrayerModelLiveData = new LivePagedListBuilder<>(
                prayerDatabase.daoAccess().getSearchMember(s), pagedListConfig).build();


        action.postValue(new ExistingMemberAction(ExistingMemberAction.SEARCH_SUCCESSFULL,pagedListPrayerModelLiveData));



    }

    public void updateMember(PrayerModel prayerModel, MutableLiveData<PrayerModel> insertObservable) {
        MutableLiveData<Long> id=new MutableLiveData<>();

        Observable.fromCallable(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return prayerDatabase.daoAccess().updateMember(prayerModel);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer id) throws Exception {

                insertObservable.setValue(new PrayerModel("-99","","","",""));
            }
        });
    }
}

