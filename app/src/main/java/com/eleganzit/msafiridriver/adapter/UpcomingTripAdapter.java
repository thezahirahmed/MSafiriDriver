package com.eleganzit.msafiridriver.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.eleganzit.msafiridriver.PickupLocation;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.VehicleDetailsActivity;
import com.eleganzit.msafiridriver.activity.Home;
import com.eleganzit.msafiridriver.activity.HomePassengerListActivity;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.eleganzit.msafiridriver.activity.PassengerListActivity;
import com.eleganzit.msafiridriver.activity.TripDetail;
import com.eleganzit.msafiridriver.activity.TripDetail2;
import com.eleganzit.msafiridriver.model.TripData;
import com.eleganzit.msafiridriver.model.VehicleData;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.eleganzit.msafiridriver.utils.RobotoMediumTextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

/*
public class UpcomingTripAdapter extends RecyclerView.Adapter
{
    private static final int tenants_layout = 1;
    private static final int last_tenants_layout = 2;
    ArrayList<TripData> arrayList;
    Context context;
    ProgressDialog progressDialog;
    public UpcomingTripAdapter(ArrayList<TripData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {

        // If the current user is the sender of the message
        if(position==arrayList.size()-1){
            // If some other user sent the message
            return last_tenants_layout;
        }
        else
        {
            return tenants_layout;
        }

    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == tenants_layout) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.trip_layout, parent, false);
            return new UpcomingHolder(view);
        }
        else if (viewType == last_tenants_layout) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.last_trip_layout, parent, false);
            return new LastUpcomingHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final TripData tripData=arrayList.get(position);

        Log.d("position : size ",""+position+":"+arrayList.size());

        switch (holder.getItemViewType()) {
            case tenants_layout:
                ((UpcomingHolder) holder).bind(tripData);
                break;
            case last_tenants_layout:
                ((LastUpcomingHolder) holder).bind(tripData);

        }
    }

    private class UpcomingHolder extends RecyclerView.ViewHolder {
        LinearLayout trip_main,linearLayout;
        RobotoMediumTextView trip_time,from,to;
        UpcomingHolder(View itemView) {
            super(itemView);

            trip_main=itemView.findViewById(R.id.trip_main);
            linearLayout=itemView.findViewById(R.id.linearLayout);
            trip_time=itemView.findViewById(R.id.trip_time);
            from=itemView.findViewById(R.id.from);
            to=itemView.findViewById(R.id.to);
        }

        void bind(final TripData tripData) {

            trip_time.setText(parseDateToddMMyyyy2(tripData.getPickup_time()));
            from.setSelected(true);
            from.setText(tripData.getFrom_address());
            to.setSelected(true);
            to.setText(tripData.getTo_address());
            trip_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context,PickupLocation.class));

                }
            });
            trip_main.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(context).setMessage("Delete this trip?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getVehicledata(tripData.getId(),getAdapterPosition());
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                    return true;
                }
            });
            if(getAdapterPosition() %2 == 1)
            {
                linearLayout.setBackgroundColor(Color.parseColor("#FFECECEC"));
            }
            else
            {
                linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }

            // Format the stored timestamp into a readable String using method.
            //timeText.setText(message.getTime());
        }
    }

    private class LastUpcomingHolder extends RecyclerView.ViewHolder {
        LinearLayout trip_main,linearLayout;
        RobotoMediumTextView trip_time,from,to;
        LastUpcomingHolder(View itemView) {
            super(itemView);

            trip_main=itemView.findViewById(R.id.trip_main);
            linearLayout=itemView.findViewById(R.id.linearLayout);
            trip_time=itemView.findViewById(R.id.trip_time);
            from=itemView.findViewById(R.id.from);
            to=itemView.findViewById(R.id.to);

        }

        void bind(final TripData tripData) {
            trip_time.setText(parseDateToddMMyyyy2(tripData.getPickup_time()));
            from.setSelected(true);
            from.setText(tripData.getFrom_address());
            to.setSelected(true);
            to.setText(tripData.getTo_address());
            trip_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context,PickupLocation.class));

                }
            });
            trip_main.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(context).setMessage("Delete this trip?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getVehicledata(tripData.getId(),getAdapterPosition());
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                    return true;
                }
            });
            if(getAdapterPosition() %2 == 1)
            {
                linearLayout.setBackgroundColor(Color.parseColor("#FFECECEC"));
            }
            else
            {
                linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }

            // Format the stored timestamp into a readable String using method.

        }
    }

    public void getVehicledata(String id, final int position)
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.removeDrivertrip(id, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                progressDialog.dismiss();
                ArrayList<VehicleData> arrayList=new ArrayList<>();
                ArrayList<VehicleData> arrayList2=new ArrayList<>();
                final StringBuilder stringBuilder = new StringBuilder();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("stringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {
                            removeAt(position);
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(context, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();

                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    public String parseDateToddMMyyyy2(String time)   {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd MMM, yyyy  h:mm a";
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
    public void removeAt(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
        notifyItemChanged(position);
    }
}*/

public class UpcomingTripAdapter extends RecyclerView.Adapter<UpcomingTripAdapter.MyViewHolder>
{
    ArrayList<TripData> arrayList;
    Context context;
    ProgressDialog progressDialog;
    String type;
    String iid,from_title,from_lat,from_lng,from_address,to_title,to_lat,to_lng,to_address,pickup_time,
            destination_time, statuss,trip_price;
    public UpcomingTripAdapter(String type,ArrayList<TripData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.type = type;
        if(context!=null)
        {
            progressDialog=new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }

    }

    public void swap(ArrayList<TripData> datas)
    {
        arrayList.clear();
        arrayList.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_layout,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(v);

        return myViewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final TripData tripData=arrayList.get(position);

        if(tripData.getTrip_type().equalsIgnoreCase("past") || type.equalsIgnoreCase("company"))
        {
            holder.more.setVisibility(View.GONE);
        }
        else
        {
            holder.more.setVisibility(View.VISIBLE);
        }
        holder.trip_time.setText(parseDateToddMMyyyy2(tripData.getStr_pickup_time()));
        holder.from.setSelected(true);
        holder.from.setText(tripData.getFrom_address());
        holder.to.setSelected(true);
        holder.to.setText(tripData.getTo_address());
        /*holder.trip_main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){

                    // Do what you want
                    holder.more.setImageResource(R.drawable.ic_more);
                    return true;
                }
                else if(event.getAction() == MotionEvent.ACTION_DOWN){

                    // Do what you want
                    holder.more.setImageResource(R.drawable.ic_more_dark);
                    return true;
                }
                return false;
            }
        });*/
        holder.trip_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tripData.getTrip_type().equalsIgnoreCase("past"))
                {

                    Intent intent = new Intent(context, TripDetail.class).putExtra("id",tripData.getId()+"");
                    context.startActivity(intent);
                    Bungee.slideLeft(context);
                }
                else if(type.equalsIgnoreCase("company"))
                {

                    SharedPreferences p_pref;
                    SharedPreferences.Editor p_editor;
                    p_pref=context.getSharedPreferences("passenger_pref",Context.MODE_PRIVATE);
                    p_editor=p_pref.edit();
                    p_editor.putString("trip_id",tripData.getId()+"");
                    p_editor.commit();
                    context.startActivity(new Intent(context,HomePassengerListActivity.class).putExtra("trip_id",tripData.getId()+""));
                    Bungee.slideLeft(context);
                }
                else
                {
                    final Dialog dialog=new Dialog(context);
                    dialog.setContentView(R.layout.trip_options_layout);
                    final TextView update=dialog.findViewById(R.id.update);
                    final TextView delete=dialog.findViewById(R.id.delete);
                    final View divider_delete=dialog.findViewById(R.id.divider_delete);
                    final TextView passengers=dialog.findViewById(R.id.passengers);
                    final TextView delay=dialog.findViewById(R.id.delay);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date mDate = sdf.parse(tripData.getStr_pickup_time());
                        long tripMilliseconds = mDate.getTime();
                        Calendar c = Calendar.getInstance();
                        System.out.println("Current time => "+c.getTime());
                        long currentMilliseconds =c.getTime().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = df.format(c.getTime());

                        long timeGap=(tripMilliseconds-currentMilliseconds);
                        long twoHours=7140003;
                        if(timeGap>=twoHours)
                        {
                            //Toast.makeText(context, diff+"   is equal or greater", Toast.LENGTH_SHORT).show();
                            delete.setVisibility(View.VISIBLE);
                            divider_delete.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            //Toast.makeText(context, diff+"   is lesser", Toast.LENGTH_SHORT).show();
                            delete.setVisibility(View.GONE);
                            divider_delete.setVisibility(View.GONE);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(context,PickupLocation.class).putExtra("id",tripData.getId()+"").putExtra("from","update"));
                            Bungee.slideLeft(context);
                            dialog.dismiss();
                        }
                    });

                    delay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            delayTrip(tripData.getId(),position,holder);
                        }
                    });

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final Dialog dialog2=new Dialog(context,
                                    R.style.Theme_Dialog);
                            dialog2.setContentView(R.layout.reason_dialog);
                            final EditText edreason=dialog2.findViewById(R.id.ed_reason);
                            TextView ok=dialog2.findViewById(R.id.ok);
                            TextView cancel=dialog2.findViewById(R.id.cancel);

                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if(edreason.getText().toString().isEmpty())
                                    {
                                        edreason.requestFocus();
                                        edreason.setError("Please give the reason");
                                        //Toast.makeText(context, "Please give the reason", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        removeDrivertrip(tripData.getId(),position,edreason.getText().toString());
                                        dialog.dismiss();
                                        dialog2.dismiss();
                                    }

                                }
                            });

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog2.dismiss();
                                    dialog.dismiss();
                                }
                            });

                            dialog2.show();
                            /*dialog.dismiss();
                            new AlertDialog.Builder(context).setMessage("Cancel this trip?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();*/

                        }
                    });

                    passengers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            SharedPreferences p_pref;
                            SharedPreferences.Editor p_editor;
                            p_pref=context.getSharedPreferences("passenger_pref",Context.MODE_PRIVATE);
                            p_editor=p_pref.edit();
                            p_editor.putString("trip_id",tripData.getId()+"");
                            p_editor.commit();
                            context.startActivity(new Intent(context,HomePassengerListActivity.class).putExtra("trip_id",tripData.getId()+""));
                            Bungee.slideLeft(context);

                            dialog.dismiss();
                        }
                    });

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                }

            }
        });

        /*holder.trip_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context).setMessage("Delete this trip?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeDrivertrip(tripData.getId(),holder.getAdapterPosition());
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
                return true;
            }
        });*/
        if(position %2 == 1)
        {
            holder.linearLayout.setBackgroundColor(Color.parseColor("#FFECECEC"));
        }
        else
            {
            holder.linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout trip_main,linearLayout;
        RobotoMediumTextView trip_time,from,to;
        ImageView more;
        public MyViewHolder(View itemView) {
            super(itemView);
            trip_main=itemView.findViewById(R.id.trip_main);
            linearLayout=itemView.findViewById(R.id.linearLayout);
            trip_time=itemView.findViewById(R.id.trip_time);
            from=itemView.findViewById(R.id.from);
            to=itemView.findViewById(R.id.to);
            more=itemView.findViewById(R.id.more);


        }

    }

    public void removeAt(int position) {
        if(arrayList.size()==1)
        {
            arrayList.remove(position);
            notifyDataSetChanged();
        }
        else
        {
            arrayList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, arrayList.size());
        }

    }
    public String parseDateToddMMyyyy2(String time)   {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd MMM, yyyy  h:mm a";
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

    public void delayTrip(String id, final int position, final MyViewHolder holder)
    {

        if(context!=null) {
            progressDialog.show();
        }
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.delayTrip(id, "yes", new retrofit.Callback<retrofit.client.Response>() {
                    @Override
                    public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                        if(context!=null) {
                            progressDialog.dismiss();
                        }
                        final StringBuilder stringBuilder = new StringBuilder();
                        try {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line);
                            }
                            Log.d("stringBuilder", "" + stringBuilder);
                            //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                            if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {
                                TripData tripData = null;
                                JSONObject jsonObject = new JSONObject("" + stringBuilder);
                                String status = jsonObject.getString("status");
                                String message = jsonObject.getString("message");
                                if(status.equalsIgnoreCase("1"))
                                {
                                    Log.d("wwwwwwwwwww", "status 1" );

                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for(int i=0;i<jsonArray.length();i++)
                                    {
                                        Log.d("wwwwwwwwwww", "forrr" );

                                        JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                        iid = jsonObject1.getString("id");
                                        from_title = jsonObject1.getString("from_title");
                                        from_lat = jsonObject1.getString("from_lat");
                                        from_lng = jsonObject1.getString("from_lng");
                                        from_address = jsonObject1.getString("from_address");
                                        to_title = jsonObject1.getString("to_title");
                                        to_lat = jsonObject1.getString("to_lat");
                                        to_lng = jsonObject1.getString("to_lng");
                                        to_address = jsonObject1.getString("to_address");
                                        pickup_time = jsonObject1.getString("datetime");
                                        destination_time = jsonObject1.getString("end_datetime");
                                        statuss = jsonObject1.getString("status");
                                        /*if(jsonObject1.getString("trip_price")!=null)
                                        {
                                            trip_price = jsonObject1.getString("trip_price");
                                        }*/

                                        /*Date date = null;
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        try {
                                            date = format.parse(pickup_time);
                                            System.out.println(date);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }*/
                                        tripData=new TripData("upcoming",iid,from_title,from_lat,from_lng,from_address,to_title,to_lat,to_lng,to_address,pickup_time,null,destination_time,statuss,trip_price);

                                    }
                                    Toast.makeText(context, "Trip delayed by 30 min", Toast.LENGTH_SHORT).show();

                                    arrayList.set(position, tripData);
                                    notifyItemChanged(position);

                                }
                                else
                                {
                                    Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                                }

                            }
                            else
                            {

                                Toast.makeText(context, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                            }


                        } catch (IOException e) {

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if(context!=null) {
                            progressDialog.dismiss();
                        }
                        //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void removeDrivertrip(String id, final int position,String reason)
    {
        if(context!=null) {
            progressDialog.show();
        }
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.cancelTrip(id,"cancel",reason, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                if(context!=null) {
                    progressDialog.dismiss();
                }
                ArrayList<VehicleData> arrayList=new ArrayList<>();
                ArrayList<VehicleData> arrayList2=new ArrayList<>();
                final StringBuilder stringBuilder = new StringBuilder();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("stringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {
                            removeAt(position);
                            Toast.makeText(context, "Trip cancelled", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                        else
                        {

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(context, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if(context!=null) {
                    progressDialog.dismiss();
                }
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
