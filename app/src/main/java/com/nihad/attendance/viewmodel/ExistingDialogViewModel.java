package com.nihad.attendance.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.RadioGroup;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.PropertyChangeRegistry;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


import com.nihad.attendance.R;
import com.nihad.attendance.Utils.ExistingMemberAction;
import com.nihad.attendance.Utils.SharedPrefConstants;
import com.nihad.attendance.database.repository.PrayerModelRepository;

import io.reactivex.disposables.CompositeDisposable;

public class ExistingDialogViewModel extends AndroidViewModel implements Observable {

    PrayerModelRepository reposotory;
    public int searchBy=1;


    public ObservableBoolean condition =new ObservableBoolean();
    public ObservableInt inputtype =new ObservableInt();
    TextWatcher textWatcherSearch;
    SharedPreferences sharedPreferences;


    public void setQuery(String query) {
        this.query.set(query);
        registry.notifyChange(this, com.nihad.attendance.BR.query);

    }


    @BindingAdapter("query")
    public static void setQuery(SearchView searchView, String queryText) {
        searchView.setQuery(queryText, false);
    }
    ObservableField<String> query=new ObservableField<>();

    @Bindable
    public String getQuery() {
        return query.get();
    }

    public ExistingDialogViewModel(Application application){
        super(application);


        reposotory=new PrayerModelRepository(application.getApplicationContext());
        this.textWatcherSearch=getSearch();
        sharedPreferences = application.getSharedPreferences(SharedPrefConstants.PREF_CONST, Context.MODE_PRIVATE);

        CompositeDisposable disposable=new CompositeDisposable();
        inputtype.set(InputType.TYPE_CLASS_TEXT);


    }


    public MutableLiveData<ExistingMemberAction> getAction()
    {
        return reposotory.getExistingMemberActionMutableLiveData();
    }

    public void radioChanged(RadioGroup radioGroup, int id) {

        if(id== R.id.name)
        {
            condition.set(true);
            inputtype.set(InputType.TYPE_CLASS_TEXT);
            setQuery("");
            getAction().setValue(new ExistingMemberAction(ExistingMemberAction.SEARCH_CLEAR_LIST,"name"));
            searchBy=1;
        }
     else if(id==R.id.phone_number)
        {
            condition.set(true);
            inputtype.set(InputType.TYPE_CLASS_NUMBER);
            setQuery("");
            getAction().setValue(new ExistingMemberAction(ExistingMemberAction.SEARCH_CLEAR_LIST,"phonenumber"));
            searchBy=2;
        }

    }






    public TextWatcher getSearch() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do some thing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //do some thing


            }

            @Override
            public void afterTextChanged(Editable s) {




            }
        };
    }

    public void search(String query) {

        reposotory.getSearchMember("%"+query+"%",getAction());


    }



    PropertyChangeRegistry registry = new PropertyChangeRegistry();

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.add(callback);

    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.remove(callback);
    }

}
