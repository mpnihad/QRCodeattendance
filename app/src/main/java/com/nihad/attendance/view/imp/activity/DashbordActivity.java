package com.nihad.attendance.view.imp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nihad.attendance.BuildConfig;
import com.nihad.attendance.R;
import com.nihad.attendance.Utils.Services;
import com.nihad.attendance.Utils.SharedPrefConstants;
import com.nihad.attendance.database.repository.PrayerModelRepository;
import com.nihad.attendance.model.ReportData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import static com.nihad.attendance.Utils.SharedPrefConstants.SELECTED_PRAYER;
import static com.nihad.attendance.Utils.SharedPrefConstants.SEND_REPORT_STATUS;

public class DashbordActivity extends AppCompatActivity {
    private static final int MULTIPLE_PERMISSIONS = 10;

    ConstraintLayout addMemberBox,selectPrayerBox,scanMemberBox,reportBox,searchBox;

    MaterialButton addMemberButton,selectPrayerButton,scanButton,reportButton,searchButton,sendreport;

    Spinner selectedPrayer;

    Dialog loading;
    SharedPreferences sharedPreferences;
    PrayerModelRepository repository;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbord);

        addMemberBox=findViewById(R.id.addmemberbox);
        addMemberButton=findViewById(R.id.addmemberbutton);

        selectPrayerBox=findViewById(R.id.selectprayerbox);
        selectPrayerButton=findViewById(R.id.selectprayerbutton);

        scanMemberBox=findViewById(R.id.scanmemberbox);
        scanButton=findViewById(R.id.scanbutton);

        reportBox=findViewById(R.id.reportbox);
        reportButton=findViewById(R.id.reportbutton);

        searchBox=findViewById(R.id.searchbox);
        searchButton=findViewById(R.id.searchbutton);
        selectedPrayer=findViewById(R.id.selectedprayer);


        sendreport=findViewById(R.id.sendreport);

        selectedPrayer.setSelection(0);


        sharedPreferences = getSharedPreferences(SharedPrefConstants.PREF_CONST, Context.MODE_PRIVATE);


        repository = new PrayerModelRepository(this);

        MutableLiveData<Integer> mutableSendResponse=new MutableLiveData<>();


        MutableLiveData<ArrayList<ReportData>> mutableTodaysAttendance=new MutableLiveData<>();

        mutableSendResponse.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer!=0)
                {

                    Toast.makeText(DashbordActivity.this, "Succesfully Moved", Toast.LENGTH_SHORT).show();


                    exportDB();
                    cancelLoading();

                }
            }
        });
        mutableTodaysAttendance.observe(this, new Observer<ArrayList<ReportData>>() {
            @Override
            public void onChanged(ArrayList<ReportData> todayAttendances) {

                if(todayAttendances.size()==0)
                {
                    Toast.makeText(DashbordActivity.this, "No Updated Record Found", Toast.LENGTH_SHORT).show();
                }
                else {
                    createPdf(todayAttendances, mutableSendResponse);
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SEND_REPORT_STATUS, "1");

                sendreport.setVisibility(View.INVISIBLE);
                editor.apply();

            }
        });


        selectedPrayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(selectedPrayer.getSelectedItem().toString().equals("Fajar")&&sharedPreferences.getString(SELECTED_PRAYER,"Fajar").equals("Isha")&&sharedPreferences.getString(SEND_REPORT_STATUS,"0").equals("0"))
                {
                    sendreport.setVisibility(View.VISIBLE);
                    Toast.makeText(DashbordActivity.this, "Please Send Report to Continue", Toast.LENGTH_SHORT).show();
                    selectedPrayer.setSelection(0);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        sendreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initLoading(DashbordActivity.this);

                repository.getAlltodaysAttendance(mutableTodaysAttendance);

            }
        });
        addMemberBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(DashbordActivity.this,addMemberActivity.class);
                startActivity(intent);
            }
        });


        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(DashbordActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashbordActivity.this,SearchActivity.class);
                startActivity(intent);

            }
        });

        reportBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashbordActivity.this,ReportListActivity.class);
                startActivity(intent);

            }
        });
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashbordActivity.this,ReportListActivity.class);
                startActivity(intent);
            }
        });
        if(sharedPreferences.getString(SELECTED_PRAYER,"Fajar").equals("Isha")&&sharedPreferences.getString(SEND_REPORT_STATUS,"0").equals("0"))
        {
            sendreport.setVisibility(View.VISIBLE);
        }

        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(DashbordActivity.this,addMemberActivity.class);
                startActivity(intent);

            }
        });
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedPrayer.getSelectedItemPosition()==0)
                {
                    Toast.makeText(DashbordActivity.this, "Please Select a Prayer", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Intent intent=new Intent(DashbordActivity.this,ScanMemberActivity.class);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SELECTED_PRAYER, selectedPrayer.getSelectedItem().toString());
                    editor.putString(SEND_REPORT_STATUS, "0");

                    editor.apply();
                    startActivity(intent);
                }
            }
        });
        checkPermissions();

    }


    private void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ getPackageName() +"/databases/"+"prayer_db";
        String retreivedDBPAth = getDatabasePath("prayer_db").getPath();
//        String backupDBPath = "/storage/extSdCard/mydatabase";
        String backupDBPath = "/Huda Musjid/Database/prayer_db_"+ new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime()) +".db";

        String filepath = Environment.getExternalStorageDirectory().getPath() + "/Huda Musjid/Database";
        File filePath = new File(filepath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        File retrievedDB = new File(retreivedDBPAth);

        Log.d("PATHS", " CurrentDB=" +
                currentDBPath + "\n\t" + currentDB.getPath() +
                "\n\tExists=" + String.valueOf(currentDB.exists()) +
                "\nBackup=" + backupDBPath + "\n\t" + backupDB.getPath() +
                "\n\tExists=" + String.valueOf(backupDB.exists()) +
                "\nRetrieved DB=" + retreivedDBPAth + "\n\t" + retrievedDB.getPath() +
                "\n\tExists=" + String.valueOf(retrievedDB.exists())
        );

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            //destination.transferFrom(source, 0, source.size());
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch(IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Err:"+e, Toast.LENGTH_LONG).show();
        }
    }
    private void createPdf(ArrayList<ReportData> todayAttendances, MutableLiveData<Integer> mutableSendResponse) {

        try {

            String filepath = Environment.getExternalStorageDirectory().getPath() + "/Huda Musjid";
            File filePath = new File(filepath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            String pdfname="/"+ new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime())+".pdf";
;
            String targetPdf = filePath+pdfname;


        Document document = new Document();


            PdfWriter.getInstance(document, new FileOutputStream(targetPdf));


        document.open();
            Font f = new Font(Font.FontFamily.HELVETICA, 25.0f, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Huda Musjid", f);
            Paragraph p1 = new Paragraph(c);
            Font paraFont= new Font(Font.FontFamily.COURIER);
            paraFont.setSize(24);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(paraFont);
            document.add(p1);


            Chunk c1 = new Chunk(todayAttendances.get(0).getTodayAttendance().getDate(), f);
            Paragraph p2 = new Paragraph(c1);

            p2.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(p2);

            Chunk c2 = new Chunk(" ", f);
            Paragraph p3 = new Paragraph(c2);

            p3.setAlignment(Paragraph.ALIGN_LEFT);

            document.add(p3);

            PdfPTable table = new PdfPTable(10);
        int numberofcell=10*(todayAttendances.size()+1);
        for(int i=0;i<todayAttendances.size() ; i++){


            if(i==0)
            {
                Font f1 = new Font(Font.FontFamily.HELVETICA, 14.0f, Font.BOLD, BaseColor.BLACK);
                Chunk ch = new Chunk("Date", f1);
                Paragraph h1 = new Paragraph(ch);
                PdfPCell cell_h1 = new PdfPCell(h1);
                cell_h1.setColspan(2);
                table.addCell(cell_h1);

                Chunk ch1 = new Chunk("Name", f1);

                Paragraph h2 = new Paragraph(ch1);
                PdfPCell cell_h2 = new PdfPCell(h2);
                cell_h2.setColspan(3);
                table.addCell(cell_h2);



                Chunk ch2 = new Chunk("Fajar", f1);
                Paragraph h3 = new Paragraph(ch2);
                table.addCell(h3);


                Chunk ch3 = new Chunk("Duhar", f1);
                Paragraph h4 = new Paragraph(ch3);
                table.addCell(h4);


                Chunk ch4 = new Chunk("Asr", f1);
                Paragraph h5 = new Paragraph(ch4);
                table.addCell(h5);


                Chunk ch5 = new Chunk("Magrib", f1);
                Paragraph h6 = new Paragraph(ch5);
                table.addCell(h6);


                Chunk ch6 = new Chunk("Isha", f1);
                Paragraph h7 = new Paragraph(ch6);
                table.addCell(h7);
            }
            PdfPCell cell1 = new PdfPCell(new Phrase(todayAttendances.get(i).getTodayAttendance().getDate()+" "+todayAttendances.get(i).getTodayAttendance().getTime()));
            cell1.setColspan(2);
            table.addCell(cell1);

            PdfPCell cell = new PdfPCell(new Phrase(todayAttendances.get(i).getPrayerModel().getName()+"\n"+todayAttendances.get(i).getPrayerModel().getPhoneNo()+"\n"+todayAttendances.get(i).getPrayerModel().getAddress()));
            cell.setColspan(3);
            table.addCell(cell);





            Font tick = new Font(Font.FontFamily.ZAPFDINGBATS, 25.0f, Font.BOLD, BaseColor.BLACK);
            Chunk tickchunk = new Chunk("4", tick);
            Paragraph pTick = new Paragraph(tickchunk);

            Chunk crosschunk = new Chunk("7", tick);
            Paragraph pCross = new Paragraph(crosschunk);




            table.addCell(todayAttendances.get(i).getTodayAttendance().getFajar()==1?pTick:pCross);

            table.addCell(todayAttendances.get(i).getTodayAttendance().getDuhar()==1?pTick:pCross);
            table.addCell(todayAttendances.get(i).getTodayAttendance().getAsr()==1?pTick:pCross);
            table.addCell(todayAttendances.get(i).getTodayAttendance().getMagrib()==1?pTick:pCross);
            table.addCell(todayAttendances.get(i).getTodayAttendance().getIsha()==1?pTick:pCross);

        }



        // Step 5
        document.add(table);
        // step 6
        document.close();

        repository.moveData(mutableSendResponse);
            sendmail(pdfname,filePath,todayAttendances.get(0).getTodayAttendance().getDate());
//            cancelLoading();


        } catch (
    DocumentException e) {
            e.printStackTrace();
            cancelLoading();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            cancelLoading();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendmail(String targetPdf, File filePath, String date) {
        String[] mailto = {"islam4peoples@gmail.com"};

//        Uri uri = Uri.fromFile();
        Uri uri = FileProvider.getUriForFile(DashbordActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(filePath,targetPdf ));
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "HUDA MUSJID ATTENDANCE REPORT DATE("+date+")");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "HUDA MUSJID ATTENDANCE REPORT DATE("+date+")");
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Send email using:"));
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
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
    protected void onRestart() {
        sharedPreferences = getSharedPreferences(SharedPrefConstants.PREF_CONST, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(SELECTED_PRAYER,"Fajar").equals("Isha")&&sharedPreferences.getString(SEND_REPORT_STATUS,"0").equals("0"))
        {
            sendreport.setVisibility(View.VISIBLE);
        }
        super.onRestart();
    }

    @Override
    protected void onResume() {
        sharedPreferences = getSharedPreferences(SharedPrefConstants.PREF_CONST, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(SELECTED_PRAYER,"Fajar").equals("Isha")&&sharedPreferences.getString(SEND_REPORT_STATUS,"0").equals("0"))
        {
            sendreport.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }
    public void initLoading(Context context) {
        loading = Services.showProgressDialog(context);
    }
    public void cancelLoading() {
        if (loading != null) {
            loading.cancel();
            loading = null;
        }
    }

}
