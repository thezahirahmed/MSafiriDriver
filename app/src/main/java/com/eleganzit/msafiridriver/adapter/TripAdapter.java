package com.eleganzit.msafiridriver.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.battleent.ribbonviews.RibbonTag;
import com.eleganzit.msafiridriver.PickupLocation;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.activity.TripDetail;
import com.eleganzit.msafiridriver.model.TripData;
import com.eleganzit.msafiridriver.utils.RobotoMediumTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import spencerstudios.com.bungeelib.Bungee;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder>
{
    ArrayList<TripData> arrayList;
    Context context;
    ProgressDialog progressDialog;
    public TripAdapter(ArrayList<TripData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        if(context!=null)
        {
            progressDialog=new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_history,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(v);

        return myViewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final TripData tripData=arrayList.get(position);
        holder.trip_time.setText(parseDateToddMMyyyy2(tripData.getStr_pickup_time()));
        holder.from.setSelected(true);
        holder.from.setText(tripData.getFrom_title());
        holder.to.setSelected(true);
        holder.to.setText(tripData.getTo_title());
        holder.trip_price.setSelected(true);
        holder.trip_price.setText("KSh "+tripData.getTrip_price());

        if(tripData.getStatuss().equalsIgnoreCase("deactive"))
        {
            holder.trip_status.setTagText("Completed");
            holder.trip_status.setRibbonColor(Color.parseColor("#cd2b9314"));
        }
        else if(tripData.getStatuss().equalsIgnoreCase("cancel"))
        {
            holder.trip_status.setTagText("Cancelled");
            holder.trip_status.setRibbonColor(Color.parseColor("#cdda0b23"));
        }
        else
        {
            holder.trip_status.setVisibility(View.GONE);
        }


        holder.trip_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TripDetail.class).putExtra("id",tripData.getId()+"");
                context.startActivity(intent);
                Bungee.slideLeft(context);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public String parseDateToddMMyyyy2(String time)   {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd/MM/yy 'at' hh:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout trip_main;
        TextView trip_time,from,to,trip_price;
        ImageView more;
        RibbonTag trip_status;
        public MyViewHolder(View itemView) {
            super(itemView);
            trip_main=itemView.findViewById(R.id.trip_main);
            trip_time=itemView.findViewById(R.id.trip_time);
            from=itemView.findViewById(R.id.from);
            to=itemView.findViewById(R.id.to);
            trip_price=itemView.findViewById(R.id.trip_price);
            trip_status=itemView.findViewById(R.id.trip_status);


        }

    }

    public void removeAt(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
    }

}