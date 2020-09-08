package com.nihad.attendance.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.nihad.attendance.BuildConfig;
import com.nihad.attendance.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Services {
    public static String getMacAddress(Context context) {

            try {
                org.json.JSONObject json = new org.json.JSONObject();
                android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                String device_id = null;
                if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                    device_id = tm.getDeviceId();
                }
                android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                String mac = wifi.getConnectionInfo().getMacAddress();
                json.put("mac", mac);
                if (TextUtils.isEmpty(device_id)) {
                    device_id = mac;
                }
                if (TextUtils.isEmpty(device_id)) {
                    device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                            android.provider.Settings.Secure.ANDROID_ID);
                }
                json.put("device_id", device_id);
                return json.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;



    }


    public static Dialog showProgressDialog(Context context) {

        Dialog warningDialog = new Dialog(context);
        warningDialog.setContentView(R.layout.layout_progress_dialog);


        warningDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        warningDialog.setCanceledOnTouchOutside(false);
        warningDialog.setCancelable(false);
        warningDialog.show();
        return warningDialog;
    }
    @SuppressLint("NewApi")
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    public static void exportDB(Context context) {
        // TODO Auto-generated method stub



        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "com.nihad.attendance"
                        + "//databases//" + "prayer_db.db";
                String backupDBPath = "/Huda Musjid/prayer_db_"+ new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime()) +".db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                FileChannel src = new FileInputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context, backupDB.toString(),
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }

}
