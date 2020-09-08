package com.nihad.attendance.view.imp.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
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
import com.nihad.attendance.viewmodel.ReportListModel;
import com.nihad.attendance.adapter.ReportListAdapter;
import com.nihad.attendance.adapter.ReportTotListAdapter;
import com.nihad.attendance.model.PrayerModel;
import com.nihad.attendance.model.ReportData;
import com.nihad.attendance.model.ReportTotData;
import com.nihad.attendance.model.TodaysAttendance;
import com.nihad.attendance.presenter.MainActivityPresenter;
import com.nihad.attendance.view.view.MainActivityCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class ReportListActivity extends AppCompatActivity implements
        MainActivityCallback {


    RecyclerView reportList;
    ProgressBar loading;
    private MainActivityPresenter presenter;
    private CompositeDisposable disposable;
    private ReportListAdapter mAdapter;
    private ReportTotListAdapter mTAdapter;
    int getStatus = 1;
    ReportListModel reportListModel;
    PagedList<TodaysAttendance> todaysAttendance;
    Toolbar mTopToolbar;
    TextInputEditText selectdate;
    LinearLayout heading;
    DatePickerDialog.OnDateSetListener date;

    MaterialButton share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);

        selectdate = findViewById(R.id.selectdate);
        heading = findViewById(R.id.heading);
        share = findViewById(R.id.share);
        setSupportActionBar(mTopToolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(2);

        mTopToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.menu_main_record) {
                    Intent intent = new Intent(ReportListActivity.this, RecordPage.class);
                    disposable.dispose();
                    startActivity(intent);
                } else if (item.getItemId() == R.id.menu_main_Camera) {
                    Intent intent = new Intent(ReportListActivity.this, camera_get.class);
                    disposable.dispose();
                    startActivity(intent);
                }

                return false;
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (getStatus == 2) {
                    createPdfT(mTAdapter.getCurrentList().snapshot());
                } else {
                    createPdf((ArrayList<ReportData>) mAdapter.getCurrentList().snapshot());
                }

            }
        });

        selectdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar myCalendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = null;
                date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        int month = monthOfYear + 1;
                        String dates = new SimpleDateFormat("dd/MM/yyyy").format(myCalendar.getTime());
                        getAttendance(dates);
                        selectdate.setText(dates);
                    }

                };

                datePickerDialog = new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMaxDate(myCalendar.getTimeInMillis() + 24 * 60 * 60 * 1000);

                datePickerDialog.show();
            }
        });
        presenter = new MainActivityPresenter(this);
        disposable = new CompositeDisposable();
        reportList = findViewById(R.id.reportlist);
        loading = findViewById(R.id.progressbar);


        reportListModel = ViewModelProviders.of(this).get(ReportListModel.class);
//        mAdapter = new ReportListAdapter(this);

        ArrayList<TodaysAttendance> todaysAttendances = new ArrayList<>();


        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reportList.setLayoutManager(layoutManager);
        mAdapter = new ReportListAdapter(this);
        reportList.setAdapter(mAdapter);

//        reportListModel.getAllFilim().observe(this, todaysAttendance -> {
//            Log.d("called", todaysAttendances.toString());
//            mAdapter.show_data(todaysAttendances); } );


//        presenter.getList(reportListModel.getRepository());


    }





    private void createPdfT(List<ReportTotData> todayAttendances) {


        try {

            String filepath = Environment.getExternalStorageDirectory().getPath() + "/Huda Musjid/sharedreport";
            File filePath = new File(filepath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            String pdfname = "/" + todayAttendances.get(0).getTotalAttendance().getT_date().replaceAll("/", "-") + ".pdf";
            ;
            String targetPdf = filePath + pdfname;


            Document document = new Document();


            PdfWriter.getInstance(document, new FileOutputStream(targetPdf));


            document.open();
            Font f = new Font(Font.FontFamily.HELVETICA, 25.0f, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Huda Musjid", f);
            Paragraph p1 = new Paragraph(c);
            Font paraFont = new Font(Font.FontFamily.COURIER);
            paraFont.setSize(24);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(paraFont);
            document.add(p1);


            Chunk c1 = new Chunk(todayAttendances.get(0).getTotalAttendance().getT_date(), f);
            Paragraph p2 = new Paragraph(c1);

            p2.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(p2);

            Chunk c2 = new Chunk(" ", f);
            Paragraph p3 = new Paragraph(c2);

            p3.setAlignment(Paragraph.ALIGN_LEFT);

            document.add(p3);

            PdfPTable table = new PdfPTable(10);
            int numberofcell = 10 * (todayAttendances.size() + 1);
            for (int i = 0; i < todayAttendances.size(); i++) {


                if (i == 0) {
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
                PdfPCell cell1 = new PdfPCell(new Phrase(todayAttendances.get(i).getTotalAttendance().getT_date() + " " + todayAttendances.get(i).getTotalAttendance().getT_time()));
                cell1.setColspan(2);
                table.addCell(cell1);

                PdfPCell cell = new PdfPCell(new Phrase(todayAttendances.get(i).getPrayerModel().getName() + "\n" + todayAttendances.get(i).getPrayerModel().getPhoneNo() + "\n" + todayAttendances.get(i).getPrayerModel().getAddress()));
                cell.setColspan(3);
                table.addCell(cell);


                Font tick = new Font(Font.FontFamily.ZAPFDINGBATS, 25.0f, Font.BOLD, BaseColor.BLACK);
                Chunk tickchunk = new Chunk("4", tick);
                Paragraph pTick = new Paragraph(tickchunk);

                Chunk crosschunk = new Chunk("7", tick);
                Paragraph pCross = new Paragraph(crosschunk);


                table.addCell(todayAttendances.get(i).getTotalAttendance().getT_fajar() == 1 ? pTick : pCross);

                table.addCell(todayAttendances.get(i).getTotalAttendance().getT_duhar() == 1 ? pTick : pCross);
                table.addCell(todayAttendances.get(i).getTotalAttendance().getT_asr() == 1 ? pTick : pCross);
                table.addCell(todayAttendances.get(i).getTotalAttendance().getT_magrib() == 1 ? pTick : pCross);
                table.addCell(todayAttendances.get(i).getTotalAttendance().getT_isha() == 1 ? pTick : pCross);

            }


            // Step 5
            document.add(table);
            // step 6
            document.close();


            sendmail(pdfname, filePath, todayAttendances.get(0).getTotalAttendance().getT_date());
//            cancelLoading();


        } catch (
                DocumentException e) {
            e.printStackTrace();
//            cancelLoading();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
//            cancelLoading();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }


    private void createPdf(ArrayList<ReportData> todayAttendances) {

        try {

            String filepath = Environment.getExternalStorageDirectory().getPath() + "/Huda Musjid/sharedreport";
            File filePath = new File(filepath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            String pdfname = "/" + todayAttendances.get(0).getTodayAttendance().getDate().replaceAll("/", "-") + ".pdf";
            ;
            String targetPdf = filePath + pdfname;


            Document document = new Document();


            PdfWriter.getInstance(document, new FileOutputStream(targetPdf));


            document.open();
            Font f = new Font(Font.FontFamily.HELVETICA, 25.0f, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Huda Musjid", f);
            Paragraph p1 = new Paragraph(c);
            Font paraFont = new Font(Font.FontFamily.COURIER);
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
            int numberofcell = 10 * (todayAttendances.size() + 1);
            for (int i = 0; i < todayAttendances.size(); i++) {


                if (i == 0) {
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
                PdfPCell cell1 = new PdfPCell(new Phrase(todayAttendances.get(i).getTodayAttendance().getDate() + " " + todayAttendances.get(i).getTodayAttendance().getTime()));
                cell1.setColspan(2);
                table.addCell(cell1);

                PdfPCell cell = new PdfPCell(new Phrase(todayAttendances.get(i).getPrayerModel().getName() + "\n" + todayAttendances.get(i).getPrayerModel().getPhoneNo() + "\n" + todayAttendances.get(i).getPrayerModel().getAddress()));
                cell.setColspan(3);
                table.addCell(cell);


                Font tick = new Font(Font.FontFamily.ZAPFDINGBATS, 25.0f, Font.BOLD, BaseColor.BLACK);
                Chunk tickchunk = new Chunk("4", tick);
                Paragraph pTick = new Paragraph(tickchunk);

                Chunk crosschunk = new Chunk("7", tick);
                Paragraph pCross = new Paragraph(crosschunk);


                table.addCell(todayAttendances.get(i).getTodayAttendance().getFajar() == 1 ? pTick : pCross);

                table.addCell(todayAttendances.get(i).getTodayAttendance().getDuhar() == 1 ? pTick : pCross);
                table.addCell(todayAttendances.get(i).getTodayAttendance().getAsr() == 1 ? pTick : pCross);
                table.addCell(todayAttendances.get(i).getTodayAttendance().getMagrib() == 1 ? pTick : pCross);
                table.addCell(todayAttendances.get(i).getTodayAttendance().getIsha() == 1 ? pTick : pCross);

            }


            // Step 5
            document.add(table);
            // step 6
            document.close();


            sendmail(pdfname, filePath, todayAttendances.get(0).getTodayAttendance().getDate());
//            cancelLoading();


        } catch (
                DocumentException e) {
            e.printStackTrace();
//            cancelLoading();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
//            cancelLoading();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendmail(String targetPdf, File filePath, String date) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = FileProvider.getUriForFile(ReportListActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(filePath, targetPdf));
        sharingIntent.setType("application/pdf");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }


    private void getAttendance(String s) {
        reportListModel.getAllReport(s).observe(this, new Observer<PagedList<ReportData>>() {
            @Override
            public void onChanged(@Nullable PagedList<ReportData> reportData) {

//                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//                reportList.setLayoutManager(layoutManager);
//                ReportListAdapter reportListAdapter = new ReportListAdapter(ReportListActivity.this, (ArrayList<TodaysAttendance>) todaysAttendances,ReportListActivity.this);
//                reportList.setAdapter(reportListAdapter);


                if (reportData.size() == 0) {
                    reportListModel.getAllReportofDate(s).observe(ReportListActivity.this, new Observer<PagedList<ReportTotData>>() {
                        @Override
                        public void onChanged(PagedList<ReportTotData> reportData) {

                            if (reportData.size() == 0) {
                                reportList.setVisibility(View.GONE);
                                share.setVisibility(View.GONE);
                                heading.setVisibility(View.GONE);
                            } else {
                                getStatus = 2;
                                mTAdapter = new ReportTotListAdapter(ReportListActivity.this);
                                reportList.setAdapter(mTAdapter);
                                mTAdapter.submitList(reportData);
                                reportList.setVisibility(View.VISIBLE);
                                share.setVisibility(View.VISIBLE);
                                heading.setVisibility(View.VISIBLE);
                            }
//                            ReportList = new PagedList.Builder<>(
//                                    prayerDatabase.daoAccess().getAllReportAttendance(date), pagedListConfig).build();
//
//
//                            for (int i = 0; i < reportData.size(); i++) {
//                                TodayAttendance todayAttendance=new TodayAttendance(reportData.get(i).getTotalAttendance().getT_attendance_id(),
//                                        reportData.get(i).getTotalAttendance().getT_date(),
//                                        reportData.get(i).getTotalAttendance().getT_time(),
//                                        reportData.get(i).getTotalAttendance().getT_member_id(),
//                                        reportData.get(i).getTotalAttendance().getT_fajar(),
//                                        reportData.get(i).getTotalAttendance().getT_duhar(),
//                                        reportData.get(i).getTotalAttendance().getT_asr(),
//                                        reportData.get(i).getTotalAttendance().getT_magrib(),
//                                        reportData.get(i).getTotalAttendance().getT_isha());
//
//                                reportData.get(i).setTodayAttendance(todayAttendance);


                        }
                    });
                } else {
                    getStatus = 1;
                    mAdapter.submitList(reportData);
                    reportList.setAdapter(mAdapter);
                }

            }
        });


        reportListModel.getcount(s).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer count) {

//                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//                reportList.setLayoutManager(layoutManager);
//                ReportListAdapter reportListAdapter = new ReportListAdapter(ReportListActivity.this, (ArrayList<TodaysAttendance>) todaysAttendances,ReportListActivity.this);
//                reportList.setAdapter(reportListAdapter);


                if (count == 0) {
                    visibleProgress(false);

                    reportList.setVisibility(View.GONE);
                    heading.setVisibility(View.GONE);
                    share.setVisibility(View.GONE);
                    Toast.makeText(ReportListActivity.this, "No Record Found", Toast.LENGTH_LONG).show();

                    reportListModel.getAllReportofDate(s).observe(ReportListActivity.this, new Observer<PagedList<ReportTotData>>() {
                        @Override
                        public void onChanged(PagedList<ReportTotData> reportData) {


                            if (reportData.size() == 0) {
                                reportList.setVisibility(View.GONE);
                                share.setVisibility(View.GONE);
                                heading.setVisibility(View.GONE);
                            } else {
                                mTAdapter = new ReportTotListAdapter(ReportListActivity.this);
                                reportList.setAdapter(mTAdapter);
                                getStatus = 2;
                                mTAdapter.submitList(reportData);
                                reportList.setVisibility(View.VISIBLE);
                                share.setVisibility(View.VISIBLE);
                                heading.setVisibility(View.VISIBLE);
                            }
//                            ReportList = new PagedList.Builder<>(
//                                    prayerDatabase.daoAccess().getAllReportAttendance(date), pagedListConfig).build();
//
//
//                            for (int i = 0; i < reportData.size(); i++) {
//                                TodayAttendance todayAttendance=new TodayAttendance(reportData.get(i).getTotalAttendance().getT_attendance_id(),
//                                        reportData.get(i).getTotalAttendance().getT_date(),
//                                        reportData.get(i).getTotalAttendance().getT_time(),
//                                        reportData.get(i).getTotalAttendance().getT_member_id(),
//                                        reportData.get(i).getTotalAttendance().getT_fajar(),
//                                        reportData.get(i).getTotalAttendance().getT_duhar(),
//                                        reportData.get(i).getTotalAttendance().getT_asr(),
//                                        reportData.get(i).getTotalAttendance().getT_magrib(),
//                                        reportData.get(i).getTotalAttendance().getT_isha());
//
//                                reportData.get(i).setTodayAttendance(todayAttendance);


                        }
                    });
                } else {
                    reportList.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);
                    heading.setVisibility(View.VISIBLE);
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public void visibleProgress(boolean b) {
        if (b) {
            loading.setVisibility(View.VISIBLE);


        } else {
            loading.setVisibility(View.GONE);
            reportList.setVisibility(View.VISIBLE);
//            share.setVisibility(View.VISIBLE);
            heading.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public SharedPreferences getSp() {
        return null;
    }

    @Override
    public void showList(ArrayList<TodaysAttendance> todaysAttendances) {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        reportList.setLayoutManager(layoutManager);
//        ReportListAdapter reportListAdapter = new ReportListAdapter(this);
//        reportList.setAdapter(reportListAdapter);

    }

    @Override
    public void repotViews(PrayerModel item) {

    }

    @Override
    public void makeToast(String message) {
        Toast.makeText(this, "Server Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void repotView(ReportData reportData) {
//        Intent intent=new Intent(this, ReportList.class);
//        String reportmodel_string=new Gson().toJson(todaysAttendance);
//        intent.putExtra("repot_details",reportmodel_string);
//        startActivity(intent);



    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public CompositeDisposable getdisposible() {
        return disposable;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    public class getdata extends AsyncTask {
        ArrayList<TodaysAttendance> Filim_List=null;

        @Override
        protected Object doInBackground(Object[] objects) {
            try {


            }
            catch ( Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }



        @Override
        protected void onPostExecute(Object o) {


            super.onPostExecute(o);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }

}

