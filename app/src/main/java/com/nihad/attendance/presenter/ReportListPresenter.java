package com.nihad.attendance.presenter;

import com.nihad.attendance.interator.ReportListInteractor;
import com.nihad.attendance.view.view.ReportListCallBack;

public class ReportListPresenter {
    private ReportListCallBack callback;
    private ReportListInteractor interactor;

    public ReportListPresenter(ReportListCallBack callback) {
        this.callback = callback;
    }


    public void getList(String[] characters) {

        interactor = new ReportListInteractor(callback);

        interactor.getList(characters);

    }
}
