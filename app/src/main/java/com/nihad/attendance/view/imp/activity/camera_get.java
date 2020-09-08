package com.nihad.attendance.view.imp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.nihad.attendance.R;
import com.nihad.attendance.viewmodel.camera_getViewModel;
import com.nihad.attendance.view.view.camera_getCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;

public class camera_get extends AppCompatActivity implements camera_getCallBack {

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    @BindView(R.id.btn_start_capture)
    Button btnStartCapture;

    @BindView(R.id.preview)
    ImageView preview;

    private static int RESULT_IMAGE_CLICK = 1;
    String datas;
    camera_getViewModel camera_getViewModel;

    Uri cameraImageUri;
    @BindView(R.id.bottomBlurView)
    View bottomBlurView;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_get);
        ButterKnife.bind(this);

        camera_getViewModel = ViewModelProviders.of(this).get(camera_getViewModel.class);






        checkPermissions();

        btnStartCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<Uri, String> map = new HashMap<Uri, String>();

                map = camera_getViewModel.getOutputMediaFileUri(1);
                for (Map.Entry<Uri, String> entry : map.entrySet()) {
                    cameraImageUri = entry.getKey();
                    datas = entry.getValue();
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                startActivityForResult(intent, RESULT_IMAGE_CLICK);
            }
        });

    }


    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };


    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(camera_get.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                            .show();
                }

            }
            return;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_IMAGE_CLICK) {


                File dest = new File(datas);

                Bitmap bitmap = BitmapFactory.decodeFile(datas);
                try {
                    FileOutputStream out = new FileOutputStream(dest);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Log.e("Image Name", datas);

//                Bitmap myBitmap = BitmapFactory.decodeFile(datas);

                preview.setImageBitmap(bitmap);


            }


        }
    }

    @Override
    public Context getContext() {
        return camera_get.this;
    }

    @Override
    public void storeDatas(String path) {
        datas = path;
    }
}
