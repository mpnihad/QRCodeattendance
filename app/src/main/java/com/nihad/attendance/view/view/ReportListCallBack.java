package com.nihad.attendance.view.view;

import android.content.Context;

import java.util.ArrayList;

public interface ReportListCallBack {
    void visibleProgress(boolean b);


    void showList(ArrayList<String> names);

    void makeToast(String message);


    Context getContext();
}
