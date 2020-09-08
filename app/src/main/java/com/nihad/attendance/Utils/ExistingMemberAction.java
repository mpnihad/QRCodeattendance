package com.nihad.attendance.Utils;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.nihad.attendance.model.PrayerModel;

import java.util.List;

public class ExistingMemberAction {


    public static final int NO_DATA_MEMBERSEARCH = 10;
    public static final int SEARCH_CLEAR_LIST = 12;
    public static final int SEARCH_SUCCESSFULL = 13;

    int action;
    String name;

    public LiveData<PagedList<PrayerModel>> getPrayerModels() {
        return prayerModels;
    }

    public void setPrayerModels(LiveData<PagedList<PrayerModel>> prayerModels) {
        this.prayerModels = prayerModels;
    }

    LiveData<PagedList<PrayerModel>> prayerModels;
    public ExistingMemberAction(int action, LiveData<PagedList<PrayerModel>> prayerModels) {
        this.action=action;
        this.prayerModels=prayerModels;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExistingMemberAction(int action, String name) {
        this.action=action;
        this.name=name;

    }
}
