package com.nihad.attendance.interator;

import android.content.Context;
import android.util.Log;

import com.nihad.attendance.Utils.APIService;
import com.nihad.attendance.view.view.camera_getCallBack;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class camera_getInteractor {

    private APIService service;

    public camera_getInteractor(camera_getCallBack callback) {

    }


    public static Map<File,String> getOutputMediaFile(int type, Context applicationContext) {

        Map<File, String> map = new HashMap<File, String>();
        File mediaStorageDir = null;

        mediaStorageDir=applicationContext.getFilesDir();

        if (!mediaStorageDir.exists()) {

            if (!mediaStorageDir.mkdirs()) {



                return null;
            }
        }


        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = df.format(c.getTime());

        File mediaFile;

        if (type == 1) {


            mediaFile = new File(mediaStorageDir.getPath() + File.separator + formattedDate + ".JPEG");

            Log.d("hello", "getOutputMediaFile: "+mediaFile.getPath());

        } else {
            return null;
        }

        map.put(mediaFile,mediaFile.getPath());
        return map;
    }
}