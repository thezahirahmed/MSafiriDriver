package com.eleganzit.msafiridriver.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.eleganzit.msafiridriver.ChoosePictureActivity;
import com.eleganzit.msafiridriver.PickupLocation;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.model.TripData;
import com.eleganzit.msafiridriver.model.VehicleData;
import com.eleganzit.msafiridriver.utils.RobotoMediumTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VehicleImagesAdapter extends RecyclerView.Adapter<VehicleImagesAdapter.MyViewHolder>
{
    ArrayList<String> arrayList;
    Context context;

    public VehicleImagesAdapter(ArrayList<String> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_img_layout,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        Glide
                .with(context)
                .load(arrayList.get(position))
                .into(holder.image);

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAt(position);
            }
        });

    }
    public void removeAt(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image,remove;
        public MyViewHolder(View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.image);
            remove=itemView.findViewById(R.id.remove);

        }

    }

}
