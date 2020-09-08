package com.nihad.attendance.view.imp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nihad.attendance.R;
import com.nihad.attendance.Utils.ExistingMemberAction;
import com.nihad.attendance.Utils.SharedPrefConstants;
import com.nihad.attendance.adapter.MemberAdapter;
import com.nihad.attendance.databinding.LayoutExistingMemberBinding;
import com.nihad.attendance.model.PrayerModel;
import com.nihad.attendance.model.ReportData;
import com.nihad.attendance.model.TodaysAttendance;
import com.nihad.attendance.view.view.MainActivityCallback;
import com.nihad.attendance.viewmodel.ExistingDialogViewModel;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity  implements MainActivityCallback {

    
    LayoutExistingMemberBinding binding;
    ExistingDialogViewModel existingDialogViewModel;

    CompositeDisposable disposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        binding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.layout_existing_member, null, false);

        setContentView(binding.getRoot());

//        binding = DataBindingUtil.inflate(LayoutInflater.from(SearchActivity.this), R.layout.layout_existing_member, null, false);


        existingDialogViewModel = new ViewModelProvider(this).get(ExistingDialogViewModel.class);


        binding.setExistingDialogViewModel(existingDialogViewModel);


        binding.addNewData.setVisibility(View.INVISIBLE);
        binding.close.setVisibility(View.INVISIBLE);

        existingDialogViewModel.getAction().observe(SearchActivity.this, new Observer<ExistingMemberAction>() {
            @Override
            public void onChanged(@io.reactivex.annotations.Nullable final ExistingMemberAction action) {
                if (action.getAction() == ExistingMemberAction.SEARCH_CLEAR_LIST) {
                    MemberAdapter adapter = (MemberAdapter) binding.rvMemberList.getAdapter();


                    if (action.getName().equals("name")) {
                        final EditText mSearchSrcTextView;

                        mSearchSrcTextView = (EditText) binding.etSearch.findViewById(R.id.search_src_text);


                        mSearchSrcTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                        mSearchSrcTextView.setKeyListener(DigitsKeyListener.getInstance("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890 ."));


                    } else if (action.getName().equals("memberid")) {
                        final TextView mSearchSrcTextView;

                        mSearchSrcTextView = (TextView) binding.etSearch.findViewById(R.id.search_src_text);

                        mSearchSrcTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(14)});
                    } else if (action.getName().equals("phonenumber")) {
                        final TextView mSearchSrcTextView;

                        mSearchSrcTextView = (TextView) binding.etSearch.findViewById(R.id.search_src_text);

                        mSearchSrcTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                    }
                }
                else  if(action.getAction()==ExistingMemberAction.SEARCH_SUCCESSFULL)
                {
                    MemberAdapter adapter = (MemberAdapter) binding.rvMemberList.getAdapter();
                    action.getPrayerModels().observe(SearchActivity.this, new Observer<PagedList<PrayerModel>>() {
                        @Override
                        public void onChanged(PagedList<PrayerModel> prayerModels) {



                            prayerModels.addWeakCallback(prayerModels.snapshot(), new PagedList.Callback() {
                                @Override
                                public void onChanged(int position, int count) {
                                    if(count==0)
                                    {
                                        binding.addNewData.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        binding.addNewData.setVisibility(View.GONE);
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
                        binding.etSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

//        TextView et = (TextView) binding.etSearch.findViewById(R.id.search_src_text);
//        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});


        setupListAdapter(binding.rvMemberList);

        binding.addNewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchActivity.this,addMemberActivity.class);
                startActivity(intent);
            }
        });

    }
    private void setupListAdapter(RecyclerView listPeople) {
        MemberAdapter adapter = new MemberAdapter(SearchActivity.this);
        listPeople.setAdapter(adapter);
        listPeople.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

    }

    @Override
    protected void onResume() {

        if(existingDialogViewModel!=null){
            existingDialogViewModel.search(binding.etSearch.getQuery().toString());
        }



        super.onResume();
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
    public Context getContext() {
        return null;
    }

    @Override
    public CompositeDisposable getdisposible() {
        return null;
    }

    @Override
    public void repotViews(PrayerModel item) {
        Intent intent=new Intent(SearchActivity.this,editMemberActivity.class);
        intent.putExtra(SharedPrefConstants.PERSONALDETAILS,new Gson().toJson(item));
        startActivity(intent);

    }
}
