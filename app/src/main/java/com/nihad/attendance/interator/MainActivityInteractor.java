package com.nihad.attendance.interator;

import android.util.Log;

import com.nihad.attendance.Utils.APIService;
import com.nihad.attendance.database.repository.PrayerModelRepository;
import com.nihad.attendance.model.TodaysAttendance;
import com.nihad.attendance.model.ResponseClass;
import com.nihad.attendance.view.view.MainActivityCallback;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivityInteractor {
    private MainActivityCallback callback;
    private APIService service;

    public MainActivityInteractor(MainActivityCallback callback) {
        this.callback = callback;
    }

//    public void getList(PrayerModelRepository repository) {
//
//
//
//
//        PrayerModelRepository ReportModelRepository;
//
//        callback.visibleProgress(true);
//
//
//
//        service = NetworkService.getService();
//
//        Call<JsonObject> call = service.Reportlist();
//
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//
//
//                String abc=response.body().toString();
//                if (response.isSuccessful()) {
//                    JsonObject jsonObject;
//                    ArrayList<TodaysAttendance> ReportModels=new ArrayList<>();
//                    JsonArray jsonArray = response.body().get("results").getAsJsonArray();
//
//                    for (int i=0;i<jsonArray.size();i++)
//                    {
//                        jsonObject=jsonArray.get(i).getAsJsonObject();
//                        TodaysAttendance ReportModel = new Gson().fromJson(jsonObject, TodaysAttendance.class);
//                        PrayerModelRepository ReportModelsRepository =new PrayerModelRepository(callback.getContext());
//                        if(repository!=null) {
//                            repository.insertTask(ReportModel);
//                        }
//                        ReportModels.add(ReportModel);
//                        callback.visibleProgress(false);
//                    }
//
//
//
//
//
//
//
//
//                    //callback.showList(ReportModels);
//                } else {
//                    callback.makeToast(response.body().get("message").toString().replaceAll("^\"|\"$", ""));
//
//                }
//                callback.visibleProgress(false);
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                callback.makeToast(call.toString());
//                callback.visibleProgress(false);
//            }
//        });
//    }

    public void getList(PrayerModelRepository repository) {
        callback.visibleProgress(true);
        service = NetworkService.getClient(callback.getContext()).create(APIService.class);



        callback.getdisposible().add(
                service.Reportlist()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<ResponseClass, ResponseClass>() {
                            @Override
                            public ResponseClass apply(ResponseClass responseClass) throws Exception {
                                // TODO - note about sort

                                return responseClass;
                            }
                        })
                        .subscribeWith(new DisposableSingleObserver<ResponseClass>() {
                            @Override
                            public void onSuccess(ResponseClass responseClass) {

                                if(repository!=null) {
                                    for (TodaysAttendance todaysAttendance : responseClass.getTodaysAttendances()) {

                                        repository.insertTask(todaysAttendance);
                                    }

                                }

//                                ArrayList<TodaysAttendance> ReportModels1 = new ArrayList<>(ReportModels);
                                callback.visibleProgress(false);
                            }


                            @Override
                            public void onError(Throwable e) {
                                Log.e("RxError", "onError: " + e.getMessage());
                               // showError(e);
                                callback.visibleProgress(false);
                            }
                        })
        );
    }



}