package com.nihad.attendance.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;

import com.nihad.attendance.BuildConfig;
import com.nihad.attendance.interator.camera_getInteractor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class camera_getViewModel  extends AndroidViewModel {


    private Application application;


    camera_getInteractor camera_getInteractor;
    public camera_getViewModel(@NonNull Application application) {
        super(application);
        this.application=application;


    }

    public Map<Uri,String> getOutputMediaFileUri(int type) {

        Map<Uri, String> map = new HashMap<Uri, String>();
        Map<File, String> map_return = new HashMap<File, String>();
        map_return=camera_getInteractor.getOutputMediaFile(type,application.getApplicationContext());

        File file=null;
        String path="";
        for (Map.Entry<File,String> entry : map_return.entrySet()) {
           file=entry.getKey();
           path=entry.getValue();
        }
        Uri uri= FileProvider.getUriForFile(application.getApplicationContext(),
                BuildConfig.APPLICATION_ID +
                        ".fileprovider", file// Over here
                );
        map.put(uri,path);
        return map;
    }



}
