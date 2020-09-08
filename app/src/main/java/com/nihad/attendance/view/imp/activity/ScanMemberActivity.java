package com.nihad.attendance.view.imp.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.nihad.attendance.R;
import com.nihad.attendance.Utils.ExistingMemberAction;
import com.nihad.attendance.Utils.Services;
import com.nihad.attendance.Utils.SharedPrefConstants;
import com.nihad.attendance.viewmodel.ExistingDialogViewModel;
import com.nihad.attendance.adapter.MemberAdapter;
import com.nihad.attendance.database.repository.PrayerModelRepository;
import com.nihad.attendance.databinding.LayoutExistingMemberBinding;
import com.nihad.attendance.model.PrayerModel;
import com.nihad.attendance.model.ReportData;
import com.nihad.attendance.model.TodayAttendance;
import com.nihad.attendance.model.TodaysAttendance;
import com.nihad.attendance.view.view.MainActivityCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ScanMemberActivity extends AppCompatActivity implements MainActivityCallback {


    private static final String TAG = "ScanMemberActivity";
    SharedPreferences sharedPreferences;
    String selectedPrayer;
    PrayerModelRepository repository;
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    String macAddress;
    ImageButton restart;

    Button search,cancel;
    Dialog ExistingDialog;
    ExistingDialogViewModel existingDialogViewModel;
    LayoutExistingMemberBinding bindingDialog;

    CompositeDisposable disposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_member);

        repository = new PrayerModelRepository(this);
        sharedPreferences = getSharedPreferences(SharedPrefConstants.PREF_CONST, Context.MODE_PRIVATE);
        selectedPrayer = sharedPreferences.getString(SharedPrefConstants.SELECTED_PRAYER, "0");
        if (selectedPrayer.equals("0")) {
            Toast.makeText(this, "Something Went Wrong(Please Select a prayer)", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        restart = findViewById(R.id.restart);


        search = findViewById(R.id.search);
        cancel = findViewById(R.id.cancel);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ExistingDialog = new Dialog(ScanMemberActivity.this);
                bindingDialog = DataBindingUtil.inflate(LayoutInflater.from(ScanMemberActivity.this), R.layout.layout_existing_member, null, false);


                existingDialogViewModel = new ViewModelProvider(ScanMemberActivity.this).get(ExistingDialogViewModel.class);
                ExistingDialog.setContentView(bindingDialog.getRoot());
                ExistingDialog.getWindow().setLayout((int) (ScanMemberActivity.this.getResources().getDisplayMetrics().widthPixels * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
                ExistingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                bindingDialog.setExistingDialogViewModel(existingDialogViewModel);
                ExistingDialog.setCanceledOnTouchOutside(true);

                existingDialogViewModel.getAction().observe(ScanMemberActivity.this, new Observer<ExistingMemberAction>() {
                            @Override
                            public void onChanged(@io.reactivex.annotations.Nullable final ExistingMemberAction action) {
                                if (action.getAction() == ExistingMemberAction.SEARCH_CLEAR_LIST) {
                                    MemberAdapter adapter = (MemberAdapter) bindingDialog.rvMemberList.getAdapter();


                                    if (action.getName().equals("name")) {
                                        final EditText mSearchSrcTextView;

                                        mSearchSrcTextView = (EditText) bindingDialog.etSearch.findViewById(R.id.search_src_text);


                                        mSearchSrcTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                                        mSearchSrcTextView.setKeyListener(DigitsKeyListener.getInstance("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890 ."));


                                    } else if (action.getName().equals("memberid")) {
                                        final TextView mSearchSrcTextView;

                                        mSearchSrcTextView = (TextView) bindingDialog.etSearch.findViewById(R.id.search_src_text);

                                        mSearchSrcTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(14)});
                                    } else if (action.getName().equals("phonenumber")) {
                                        final TextView mSearchSrcTextView;

                                        mSearchSrcTextView = (TextView) bindingDialog.etSearch.findViewById(R.id.search_src_text);

                                        mSearchSrcTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                                    }
                                }
                                else  if(action.getAction()==ExistingMemberAction.SEARCH_SUCCESSFULL)
                                {
                                    MemberAdapter adapter = (MemberAdapter) bindingDialog.rvMemberList.getAdapter();
                                   action.getPrayerModels().observe(ScanMemberActivity.this, new Observer<PagedList<PrayerModel>>() {
                                       @Override
                                       public void onChanged(PagedList<PrayerModel> prayerModels) {



                                           prayerModels.addWeakCallback(prayerModels.snapshot(), new PagedList.Callback() {
                                               @Override
                                               public void onChanged(int position, int count) {
                                                   if(count==0)
                                                   {
                                                       bindingDialog.addNewData.setVisibility(View.VISIBLE);
                                                   }
                                                   else
                                                   {
                                                       bindingDialog.addNewData.setVisibility(View.GONE);
                                                   }
                                               }

                                               @Override
                                               public void onInserted(int position, int count) {

                                               }

                                               @Override
                                               public void onRemoved(int position, int count) {

                                               }
                                           });

                                           if(adapter!=null) {


                                               adapter.submitList(prayerModels);
                                           }
                                       }
                                   });
                                }
                            }
                        });


                Observable<String> observableQueryText = Observable
                        .create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {

                                // Listen for text input into the SearchView
                                bindingDialog.etSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String query) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(final String newText) {
                                        if (!emitter.isDisposed()) {
                                            emitter.onNext(newText); // Pass the query to the emitter
                                        }
                                        return false;
                                    }
                                });
                            }
                        })
                        .debounce(500, TimeUnit.MILLISECONDS) // Apply Debounce() operator to limit requests
                        .subscribeOn(Schedulers.io());

                // Subscribe an Observer

                observableQueryText.subscribe(new io.reactivex.Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(String s) {

                        if (!(s.isEmpty())) {
                            existingDialogViewModel.search(s);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

//        TextView et = (TextView) bindingDialog.etSearch.findViewById(R.id.search_src_text);
//        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                ExistingDialog.setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {

                                ExistingDialog.cancel();
                            }
                        }
                );
                bindingDialog.close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        ExistingDialog.cancel();
                    }
                });
                setupListAdapter(bindingDialog.rvMemberList);

                bindingDialog.addNewData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ScanMemberActivity.this,addMemberActivity.class);

                        if(existingDialogViewModel.searchBy==1) {
                            intent.putExtra("name", bindingDialog.etSearch.getQuery().toString());
                        }
                        else
                        {
                            intent.putExtra("phone", bindingDialog.etSearch.getQuery().toString());
                        }
                        startActivity(intent);
                    }
                });
                ExistingDialog.show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        capture = new CaptureManager(this, barcodeScannerView);

        capture.onResume();
        barcodeScannerView.decodeSingle(callback);
//        barcodeScannerView.decodeContinuous(callback);
       macAddress= Services.getMacAddress(ScanMemberActivity.this);

       restart.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               finish();
               startActivity(getIntent());
           }
       });

    }

    private void setupListAdapter(RecyclerView listPeople) {
        MemberAdapter adapter = new MemberAdapter(ScanMemberActivity.this);
        listPeople.setAdapter(adapter);
        listPeople.setLayoutManager(new LinearLayoutManager(ScanMemberActivity.this));

    }


    private BarcodeCallback callback = new BarcodeCallback() {

        @Override
        public void barcodeResult(BarcodeResult result) {


            Log.d("Hello", "barcodeResult: " + result.getResult().getText().toString());
//            Crashlytics.log("Aadhar card notsupported","Error message");
//            Bundle params = new Bundle();
//            params.putString("aadharcode", result.getResult().getText().toString());
//
//            FirebaseAnalytics.getInstance(getActivity()).logEvent("aadhar_Data", params);
//            FirebaseCrashlytics.getInstance().log(result.getResult().getText().toString());


            if (result.getResult().getText().contains("\"member_id\":")) {

                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(ScanMemberActivity.this, notification);
                r.play();

                Gson gson = new Gson();
                PrayerModel prayerModel = gson.fromJson(result.getResult().getText(), PrayerModel.class);
                Log.d(TAG, "barcodeResult: " + prayerModel.getAddress());


                addAttendance(prayerModel);
            }
            else
            {
                barcodeScannerView.decodeSingle(callback);
            }
        }


        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }


    };

    private void addAttendance(PrayerModel prayerModel) {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm");

        final MutableLiveData<TodayAttendance> attendanceObservable =new MutableLiveData<>();
        final MutableLiveData<PrayerModel> insertObservable =new MutableLiveData<>();
        final MutableLiveData<PrayerModel> checkprevObservable =new MutableLiveData<>();


        checkprevObservable.observe(ScanMemberActivity.this, new Observer<PrayerModel>() {
            @Override
            public void onChanged(PrayerModel prayerModel1) {
                if (prayerModel1.getMember_id()!=-1) {
                    Log.d(TAG, "onChanged: Already added member " + new Gson().toJson(prayerModel1));
//

//                                    repository.insertAttendance();

                    repository.countAttendance(prayerModel1.getMember_id(),attendanceObservable);


                } else {
                    PrayerModel prayerModelfinal = new PrayerModel(prayerModel.getName(), prayerModel.getPhoneNo(), prayerModel.getAddress(), prayerModel.getQrCode(),prayerModel.getMacAddress());

                    prayerModelfinal.setPrev_member_id(prayerModel.getMember_id());


                    repository.insertMemberafter(prayerModelfinal).observe(ScanMemberActivity.this, new Observer<Long>() {
                        @Override
                        public void onChanged(Long id) {

                            repository.countAttendance(id.intValue(), attendanceObservable);


//                                                repository.countAttendance(id.intValue()).observe(ScanMemberActivity.this, new Observer<Integer>() {
//                                                    @Override
//                                                    public void onChanged(@Nullable Integer count) {
//
//                                                        Log.d(TAG, "onChanged: Count" + count.toString());
//                                                        if (count != null) {
//
//
//                                                            TodayAttendance todayAttendance = new TodayAttendance(dateFormat.format(calendar.getTime()), timeformat.format(calendar.getTime()), prayerModel.getMember_id()
//                                                            );
//                                                            if (selectedPrayer.equals("Fajar"))
//                                                                todayAttendance.setFajar(1);
//                                                            if (selectedPrayer.equals("Duhar"))
//                                                                todayAttendance.setDuhar(1);
//                                                            if (selectedPrayer.equals("Asr"))
//                                                                todayAttendance.setAsr(1);
//                                                            if (selectedPrayer.equals("Magrib"))
//                                                                todayAttendance.setMagrib(1);
//                                                            if (selectedPrayer.equals("Isha"))
//                                                                todayAttendance.setIsha(1);
//
//                                                            todayAttendance.setAttendance_id(count);
//                                                            repository.updateAttendance(todayAttendance);
//                                                        } else {
//                                                            TodayAttendance todayAttendance = new TodayAttendance(dateFormat.format(calendar.getTime()), timeformat.format(calendar.getTime()), prayerModel.getMember_id()
//                                                            );
//
//                                                            if (selectedPrayer.equals("Fajar"))
//                                                                todayAttendance.setFajar(1);
//                                                            if (selectedPrayer.equals("Duhar"))
//                                                                todayAttendance.setDuhar(1);
//                                                            if (selectedPrayer.equals("Asr"))
//                                                                todayAttendance.setAsr(1);
//                                                            if (selectedPrayer.equals("Magrib"))
//                                                                todayAttendance.setMagrib(1);
//                                                            if (selectedPrayer.equals("Isha"))
//                                                                todayAttendance.setIsha(1);
//                                                            repository.insertAttendance(todayAttendance);
//                                                        }
//
//
//                                                    }
//                                                });
                        }
                    });
                }



            }
        });
        attendanceObservable.observe(ScanMemberActivity.this, new Observer<TodayAttendance>() {
            @Override
            public void onChanged(@Nullable TodayAttendance count) {

//                                                Log.d(TAG, "onChanged: Count" + count.toString());
                if (!(count.getTime().equals("-1"))) {


                    TodayAttendance todayAttendance = new TodayAttendance(dateFormat.format(calendar.getTime()), timeformat.format(calendar.getTime()), prayerModel.getMember_id());
                    if (selectedPrayer.equals("Fajar"))
                        count.setFajar(1);
                    if (selectedPrayer.equals("Duhar"))
                        count.setDuhar(1);
                    if (selectedPrayer.equals("Asr"))
                        count.setAsr(1);
                    if (selectedPrayer.equals("Magrib"))
                        count.setMagrib(1);
                    if (selectedPrayer.equals("Isha"))
                        count.setIsha(1);

                    todayAttendance.setAttendance_id(count.getAttendance_id());
                    repository.updateAttendance(count);
                } else {

                    TodayAttendance todayAttendance = new TodayAttendance(dateFormat.format(calendar.getTime()), timeformat.format(calendar.getTime()), count.getMember_id()
                    );

                    if (selectedPrayer.equals("Fajar"))
                        todayAttendance.setFajar(1);
                    if (selectedPrayer.equals("Duhar"))
                        todayAttendance.setDuhar(1);
                    if (selectedPrayer.equals("Asr"))
                        todayAttendance.setAsr(1);
                    if (selectedPrayer.equals("Magrib"))
                        todayAttendance.setMagrib(1);
                    if (selectedPrayer.equals("Isha"))
                        todayAttendance.setIsha(1);
                    repository.insertAttendance(todayAttendance);
                }

                Toast.makeText(ScanMemberActivity.this, "Successfully scanned \n " + prayerModel.getName() + " (" + prayerModel.getPhoneNo() + ")", Toast.LENGTH_LONG).show();


            }
        });

        insertObservable.observe(ScanMemberActivity.this, new Observer<PrayerModel>() {
            @Override
            public void onChanged(PrayerModel prayerModel1) {

                if(prayerModel1.getMacAddress().equals(prayerModel.getMacAddress()))
                {
                    repository.countAttendance(prayerModel1.getMember_id(), attendanceObservable);
                }
                else {
                    repository.checkPrevMember(prayerModel.getMember_id(),prayerModel.getMacAddress(), checkprevObservable);
                }
//                            repository.countAttendance(prayerModel1.getMember_id(), attendanceObservable);





            }
        });

        repository.insertMember(prayerModel,insertObservable);

        Runnable mRunnable;
        Handler mHandler = new Handler();

        mRunnable = new Runnable() {

            @Override
            public void run() {

                barcodeScannerView.decodeSingle(callback);
            }
        };
        mHandler.postDelayed(mRunnable, 2 * 1000);
    }


    @Override
    public void onBackPressed() {
        capture.onPause();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        capture.onPause();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        capture.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDestroy();
    }


    @Override
    public void visibleProgress(boolean b) {

    }

    @Override
    public SharedPreferences getSp() {
        return null;
    }

    @Override
    public void showList(ArrayList<TodaysAttendance> todaysAttendances) {

    }

    @Override
    public void makeToast(String message) {

    }

    @Override
    public void repotView(ReportData reportData) {

    }
    @Override
    public void repotViews(PrayerModel prayerModel) {

        if(prayerModel.getMember_id()==-100)
        {
            Toast.makeText(this, "Add new Data clicked", Toast.LENGTH_SHORT).show();
        }
        else {
            addAttendance(prayerModel);
        }
        if(ExistingDialog!=null)
        {
            if(ExistingDialog.isShowing())
            {
                ExistingDialog.cancel();
            }
        }

    }

    @Override
    public Context getContext() {
        return null;
    }



    @Override
    protected void onResume() {

        capture.onResume();
        if(ExistingDialog!=null) {
            existingDialogViewModel.search(bindingDialog.etSearch.getQuery().toString());
        }
        super.onResume();
    }

    @Override
    public CompositeDisposable getdisposible() {
        return null;
    }
}
