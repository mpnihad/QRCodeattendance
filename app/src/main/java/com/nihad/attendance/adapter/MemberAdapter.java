package com.nihad.attendance.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nihad.attendance.R;
import com.nihad.attendance.model.PrayerModel;
import com.nihad.attendance.model.ReportData;
import com.nihad.attendance.model.TodaysAttendance;
import com.nihad.attendance.view.view.MainActivityCallback;

import java.io.ByteArrayOutputStream;

public class MemberAdapter extends PagedListAdapter<PrayerModel, MemberAdapter.MyViewHolder> {



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


    private static final DiffUtil.ItemCallback<PrayerModel>DIFF_CALLBACK = new DiffUtil.ItemCallback<PrayerModel>() {
        @Override
        public boolean areItemsTheSame(PrayerModel oldItem, PrayerModel newItem) {
            return oldItem.getMember_id()==newItem.getMember_id();
        }

        @Override
        public boolean areContentsTheSame(PrayerModel oldItem, PrayerModel newItem) {
            return oldItem.getPhoneNo().equals(newItem.getPhoneNo())&&oldItem.getName().equals(newItem.getName())&&oldItem.getAddress().equals(newItem.getAddress());
        }
    };

    public MemberAdapter(MainActivityCallback callback) {
        super(DIFF_CALLBACK);
        this.callback=callback;

    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;



    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.name.setText(getItem(position).getName());
        holder.phone.setText(getItem(position).getPhoneNo());
        holder.address.setText(getItem(position).getAddress());
        try {
            PrayerModel prayerModel=getItem(position);
            prayerModel.setQrCode("");
            generateQRCode_general((new Gson().toJson(prayerModel)), holder.qrCode);

//            notifyItemChanged(position);
        } catch (WriterException e) {
            e.printStackTrace();
        }


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.repotViews(getItem(position));

            }
        });


    }


    private void generateQRCode_general(String data, ImageView img)throws WriterException {
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
            Toast.makeText(img.getContext(), "Something went Wrong",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void setMutable() {

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name,phone,address;
        ImageView qrCode;
        LinearLayout layout;



        public MyViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            phone=itemView.findViewById(R.id.phone);
            address=itemView.findViewById(R.id.address);
            qrCode=itemView.findViewById(R.id.qrCode);

            layout=itemView.findViewById(R.id.parentLayout);


        }


    }
}