package com.nihad.attendance.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nihad.attendance.R;
import com.nihad.attendance.model.ReportData;
import com.nihad.attendance.model.TodaysAttendance;
import com.nihad.attendance.view.view.MainActivityCallback;

public class ReportListAdapter extends PagedListAdapter<ReportData, ReportListAdapter.MyViewHolder> {



    MainActivityCallback callback;



//    public ReportListAdapter(Context context1, ArrayList<TodaysAttendance> ReportModels, MainActivityCallback callback) {
//
//        try {
//            context = context1;
//            inflater = LayoutInflater.from(context1);
//            this.ReportModels = ReportModels;
//            this.callback = callback;
//
//
//        } catch (Exception e) {
//
//            Log.d("Error", e.toString());
//        }
//    }


    private static final DiffUtil.ItemCallback<ReportData>DIFF_CALLBACK = new DiffUtil.ItemCallback<ReportData>() {
        @Override
        public boolean areItemsTheSame(ReportData oldItem, ReportData newItem) {
            return oldItem.getTodayAttendance().getAttendance_id()==newItem.getTodayAttendance().getAttendance_id();
        }

        @Override
        public boolean areContentsTheSame(ReportData oldItem, ReportData newItem) {
            return oldItem.getPrayerModel().getName().equals(newItem.getPrayerModel().getName());
        }
    };

    public ReportListAdapter(MainActivityCallback callback) {
        super(DIFF_CALLBACK);
        this.callback=callback;

    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.repot_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;



    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.name.setText(getItem(position).getPrayerModel().getName());
        holder.phone.setText(getItem(position).getPrayerModel().getPhoneNo());
        holder.address.setText(getItem(position).getPrayerModel().getAddress());
        holder.fajar.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),getItem(position).getTodayAttendance().getFajar()==1?R.drawable.right:R.drawable.cross));
        holder.duhar.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),getItem(position).getTodayAttendance().getDuhar()==1?R.drawable.right:R.drawable.cross));
        holder.asr.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),getItem(position).getTodayAttendance().getAsr()==1?R.drawable.right:R.drawable.cross));
        holder.magrib.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),getItem(position).getTodayAttendance().getMagrib()==1?R.drawable.right:R.drawable.cross));
        holder.isha.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),getItem(position).getTodayAttendance().getIsha()==1?R.drawable.right:R.drawable.cross));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.repotView(getItem(position));

            }
        });


    }







    public class MyViewHolder extends RecyclerView.ViewHolder {

TextView name,phone,address;
ImageView fajar,duhar,asr,magrib,isha;
LinearLayout layout;



        public MyViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            phone=itemView.findViewById(R.id.phone);
            address=itemView.findViewById(R.id.address);
            fajar=itemView.findViewById(R.id.fajar);
            duhar=itemView.findViewById(R.id.duhar);
            asr=itemView.findViewById(R.id.asr);
            magrib=itemView.findViewById(R.id.magrib);
            isha=itemView.findViewById(R.id.isha);
            layout=itemView.findViewById(R.id.parentLayout);


        }


    }
}