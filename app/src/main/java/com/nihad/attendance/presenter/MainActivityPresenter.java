package com.nihad.attendance.presenter;

import com.nihad.attendance.database.repository.PrayerModelRepository;
import com.nihad.attendance.interator.MainActivityInteractor;
import com.nihad.attendance.view.view.MainActivityCallback;

public class MainActivityPresenter {
    private MainActivityCallback callback;
    private MainActivityInteractor interactor;

    public MainActivityPresenter(MainActivityCallback callback) {
        this.callback = callback;
    }


    public void getList(PrayerModelRepository repository) {

        interactor = new MainActivityInteractor(callback);

            interactor.getList(repository);

    }


}
