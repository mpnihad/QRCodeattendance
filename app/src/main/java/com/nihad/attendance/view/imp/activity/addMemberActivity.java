package com.nihad.attendance.view.imp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nihad.attendance.R;
import com.nihad.attendance.Utils.Services;
import com.nihad.attendance.database.repository.PrayerModelRepository;
import com.nihad.attendance.model.PrayerModel;

import java.io.ByteArrayOutputStream;

public class addMemberActivity extends AppCompatActivity {


    MaterialButton save,share,newUser;
    AppCompatImageButton whatsapp;
    PrayerModelRepository repository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        TextInputEditText name,phonenumber,address;
        ImageView qrCode;


        save=findViewById(R.id.save);
        share=findViewById(R.id.share);
        newUser=findViewById(R.id.newUser);
        address=findViewById(R.id.address);
        phonenumber=findViewById(R.id.phonenumber);
        name=findViewById(R.id.name);
        qrCode=findViewById(R.id.qrCode);
        whatsapp=findViewById(R.id.whatsapp);
        share.setVisibility(View.GONE);
        whatsapp.setVisibility(View.GONE);
        repository = new PrayerModelRepository(this);


        String extraname=getIntent().getStringExtra("name");
        if(extraname!=null)
        {
            name.setText(extraname);
        }

        String extraphone=getIntent().getStringExtra("phone");
        if(extraphone!=null)
        {
            phonenumber.setText(extraphone);
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(name.getText()==null||name.getText().equals(""))
                {
                    Toast.makeText(addMemberActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
                else if(phonenumber.getText()==null||phonenumber.getText().equals(""))
                {
                    Toast.makeText(addMemberActivity.this, "Please enter a Phone Number", Toast.LENGTH_SHORT).show();
                }

                else if(phonenumber.getText().length()<10)
                {
                    Toast.makeText(addMemberActivity.this, "Please enter a valid Phone Number", Toast.LENGTH_SHORT).show();
                }
                else {

                    PrayerModel prayerModel = new PrayerModel(name.getText().toString(), phonenumber.getText().toString(), address.getText().toString(), "", Services.getMacAddress(addMemberActivity.this));


                    MutableLiveData<PrayerModel> insertObservable=new MutableLiveData<>();
                    insertObservable.observe(addMemberActivity.this, new Observer<PrayerModel>() {
                        @Override
                        public void onChanged(PrayerModel prayerModel1) {

                            prayerModel.setMember_id(prayerModel1.getMember_id());
                            try {
                                String QRCode = generateQRCode_general((new Gson().toJson(prayerModel)), qrCode);
                                prayerModel.setQrCode(QRCode);

                                repository.updateMember(prayerModel);

                                share.setVisibility(View.VISIBLE);
                                whatsapp.setVisibility(View.VISIBLE);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    repository.insertMember(prayerModel,insertObservable);



                }

            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrCode.buildDrawingCache();
                Bitmap image = qrCode.getDrawingCache();


                Uri imgUri = getImageUri(addMemberActivity.this,image,name.getText().toString()+"_"+phonenumber.getText().toString());
//                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
//                whatsappIntent.setType("text/plain");
//                whatsappIntent.setPackage("com.whatsapp");
//                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
//                whatsappIntent.putExtra("jid", phonenumber.getText().toString() + "@s.whatsapp.net");
//                whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
//                whatsappIntent.setType("image/jpeg");
//                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
                sendIntent.putExtra("jid", "91"+phonenumber.getText().toString() + "@s.whatsapp.net");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Name: "+name.getText().toString()+" \nPhoneNumber: "+phonenumber.getText().toString());
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setPackage("com.whatsapp");
                sendIntent.setType("image/JPEG");



                try {
                    addMemberActivity.this.startActivity(sendIntent);

                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(addMemberActivity.this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrCode.buildDrawingCache();
                Bitmap image = qrCode.getDrawingCache();


                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_TEXT,"Name: "+name.getText().toString()+" \n PhoneNumber: "+phonenumber.getText().toString());
                share.putExtra(Intent.EXTRA_STREAM, getImageUri(addMemberActivity.this,image,name.getText().toString()+"_"+phonenumber.getText().toString()));
                startActivity(Intent.createChooser(share,"Share via"));

            }
        });


        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrCode.setBackground(null);

                name.setText("");
                share.setVisibility(View.GONE);
                whatsapp.setVisibility(View.GONE);
                address.setText("");
                phonenumber.setText("");
            }
        });
    }
    public Uri getImageUri(Context inContext, Bitmap inImage, String title) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, title, null);
        return Uri.parse(path);
    }



    private String generateQRCode_general(String data, ImageView img)throws WriterException {
        com.google.zxing.Writer writer = new QRCodeWriter();
//        String finaldata = Uri.encode(data, "utf-8");

        BitMatrix bm = writer.encode(data, BarcodeFormat.QR_CODE,512, 512);
        Bitmap ImageBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < 512; i++) {//width
            for (int j = 0; j < 512; j++) {//height
                ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK: Color.WHITE);
            }
        }

        if (ImageBitmap != null) {
            img.setImageBitmap(ImageBitmap);
        } else {
            Toast.makeText(getApplicationContext(), "Something went Wrong",
                    Toast.LENGTH_SHORT).show();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageBitmap.compress(Bitmap.CompressFormat.WEBP, 40, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }


}
