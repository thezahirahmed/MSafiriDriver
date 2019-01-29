package com.eleganzit.msafiridriver.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.eleganzit.msafiridriver.model.PassengerData;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.MyViewHolder>
{
    ArrayList<PassengerData> arrayList;
    Context context;

    public PassengerAdapter(ArrayList<PassengerData> arrayList, Context context)
    {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.route_history_layout,parent,false);

        MyViewHolder myViewHolder=new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        PassengerData passengerData=arrayList.get(position);
        Glide
                .with(context)
                .load(passengerData.getPhoto()).apply(new RequestOptions().placeholder(R.drawable.pr))
                .into(holder.p_photo);

        holder.p_name.setText(passengerData.getFname()+" "+passengerData.getLname());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout radio;
        CircleImageView p_photo;
        TextView p_name;
        public MyViewHolder(View itemView) {
            super(itemView);
            radio=itemView.findViewById(R.id.radio);
            p_photo=itemView.findViewById(R.id.p_photo);
            p_name=itemView.findViewById(R.id.p_name);

        }
    }
}