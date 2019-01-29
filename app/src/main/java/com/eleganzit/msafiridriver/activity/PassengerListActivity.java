package com.eleganzit.msafiridriver.activity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganzit.msafiridriver.PersonalInfoActivity;
import com.eleganzit.msafiridriver.PickupLocation;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.adapter.PassengerAdapter;
import com.eleganzit.msafiridriver.adapter.UpcomingTripAdapter;
import com.eleganzit.msafiridriver.model.PassengerData;
import com.eleganzit.msafiridriver.model.TripData;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.eleganzit.msafiridriver.utils.SensorService;
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
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

public class PassengerListActivity extends AppCompatActivity {


    RecyclerView passengers;
    ProgressBar progress;
    TextView no_passenger;
    private String trip_id;
    SharedPreferences p_pref;
    SharedPreferences.Editor p_editor;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    LinearLayout start;
    ProgressDialog progressDialog;
    TextView trip_text;
    Intent mServiceIntent;
    private SensorService mSensorService;
    Handler handler;
    private String trip_lat,trip_lng,trip_lat2,trip_lng2,trip_status;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    ArrayList<String> userslist=new ArrayList<>();
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_list);


        p_pref=getSharedPreferences("passenger_pref",Context.MODE_PRIVATE);
        p_editor=p_pref.edit();
        pref=getSharedPreferences("location_pref",MODE_PRIVATE);
        editor=pref.edit();

        handler=new Handler();
        mSensorService = new SensorService(handler,pref);
        mServiceIntent = new Intent(PassengerListActivity.this, mSensorService.getClass());

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Bungee.slideRight(PassengerListActivity.this);
            }
        });

        passengers=findViewById(R.id.passengers);
        trip_text=findViewById(R.id.trip_text);
        start=findViewById(R.id.start);
        progress=findViewById(R.id.progress);
        no_passenger=findViewById(R.id.no_passenger);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");

        trip_id=getIntent().getStringExtra("trip_id");

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        passengers.setLayoutManager(layoutManager);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


    @Override
    protected void onDestroy() {

        //stopService(mServiceIntent);

        Log.i("wherreeeeeee", "onDestroy!");
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();

        trip_id=p_pref.getString("trip_id","");
        trip_lat=p_pref.getString("trip_lat","");
        trip_lng=p_pref.getString("trip_lng","");
        trip_lat2=p_pref.getString("trip_lat2","");
        trip_lng2=p_pref.getString("trip_lng2","");
        trip_status=p_pref.getString("trip_status","");
        if(trip_status.equalsIgnoreCase("ongoing"))
        {
            trip_text.setText("End Trip");
            passengers.setVisibility(View.GONE);
        }
        else
        {
            trip_text.setText("Start Trip");
        }

        from=getIntent().getStringExtra("from");

        getPassengers();
    }

    public boolean isGoogleMapsInstalled()
    {
        try
        {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            if(info.enabled)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    public void addPassengers(final String tstatus, final PassengerAdapter.MyViewHolder holder)
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.onboardUserlist(trip_id, userslist, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                progressDialog.dismiss();
                final StringBuilder stringBuilder = new StringBuilder();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("trrrrstringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {
                            updateTripStatus(tstatus,holder);
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String trip_status=jsonObject1.getString("status");
                            }

                        }
                        else
                        {
                            Toast.makeText(PassengerListActivity.this, message+"", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(PassengerListActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PassengerListActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateTripStatus(String tstatus, final PassengerAdapter.MyViewHolder holder)
    {
        if(tstatus.equalsIgnoreCase("active"))
        {
            tstatus="ongoing";
        }
        final String finalTstatus = tstatus;

        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);

        myInterface.updateTripStatus(trip_id, tstatus, new retrofit.Callback<retrofit.client.Response>() {
                    @Override
                    public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                        progressDialog.dismiss();
                        final StringBuilder stringBuilder = new StringBuilder();
                        try {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line);
                            }
                            Log.d("trrrrstringBuilder", "" + stringBuilder);
                            //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                            if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                                JSONObject jsonObject = new JSONObject("" + stringBuilder);
                                String status = jsonObject.getString("status");
                                String message = jsonObject.getString("message");
                                JSONArray jsonArray = null;
                                if(status.equalsIgnoreCase("1"))
                                {
                                    if(finalTstatus.equalsIgnoreCase("ongoing"))
                                    {
                                        holder.select_radioButton.setVisibility(View.GONE);
                                        trip_text.setText("End Trip");
                                        p_editor.putString("trip_status","ongoing");
                                        p_editor.commit();

                                        if (!isMyServiceRunning(mSensorService.getClass())) {
                                            editor.putString("action","start");
                                            editor.putString("dest_lat",trip_lat2);
                                            editor.putString("dest_lng",trip_lng2);
                                            editor.commit();
                                            mServiceIntent = new Intent(PassengerListActivity.this, mSensorService.getClass()).putExtra("click","first");
                                            //Check if the application has draw over other apps permission or not?
                                            //This permission is by default available for API<23. But for API > 23
                                            //you have to ask for the permission in runtime.
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(PassengerListActivity.this)) {

                                                //If the draw over permission is not available open the settings screen
                                                //to grant the permission.
                                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                        Uri.parse("package:" + getPackageName()));
                                                startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                                            } else {
                                                startService(mServiceIntent);

                                            }


                                        }

                                        if(!isGoogleMapsInstalled())
                                        {
                                            //Toast.makeText(PassengerListActivity.this, "not installed or is disabled", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                    Uri.parse("http://maps.google.com/maps/dir/"+trip_lat+","+trip_lng+"/"+trip_lat2+","+trip_lng2));//Uri.parse("http://maps.google.com/maps?saddr="+trip_lat+","+trip_lng+"&daddr="+trip_lat2+","+trip_lng2)
                                            startActivity(intent);
                                            finish();
                                        }
                                        else
                                        {
                                            //Toast.makeText(PassengerListActivity.this, "installed", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                    Uri.parse("google.navigation:q="+trip_lat2+","+trip_lng2));//Uri.parse("http://maps.google.com/maps?saddr="+trip_lat+","+trip_lng+"&daddr="+trip_lat2+","+trip_lng2)
                                            startActivity(intent);
                                            finish();
                                        }

                                    }
                                    else
                                    {
                                        if (isMyServiceRunning(mSensorService.getClass())) {
                                            editor.putString("action","stop");
                                            editor.commit();
                                            mServiceIntent = new Intent(PassengerListActivity.this, mSensorService.getClass()).putExtra("click","second");
                                            stopService(mServiceIntent);
                                        }
                                        finish();
                                    }

                                    jsonArray = jsonObject.getJSONArray("data");
                                    for(int i=0;i<jsonArray.length();i++)
                                    {
                                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                        //String sfrom_date=jsonObject1.getString("datetime");
                                    }

                                }
                                else
                                {
                                    Toast.makeText(PassengerListActivity.this, "Trip already exist", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else
                            {
                                Toast.makeText(PassengerListActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PassengerListActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getPassengers()
    {
        progress.setVisibility(View.VISIBLE);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getPassengers(trip_id, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                ArrayList<PassengerData> arrayList = new ArrayList<>();
                progress.setVisibility(View.GONE);

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
                            passengers.setVisibility(View.VISIBLE);
                            if(from.equalsIgnoreCase("home"))
                            {
                                start.setVisibility(View.GONE);
                            }
                            else
                            {
                                start.setVisibility(View.VISIBLE);
                            }

                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                String id = jsonObject1.getString("id");
                                String user_id = jsonObject1.getString("user_id");
                                String rating = jsonObject1.getString("rating");
                                String rstatus = jsonObject1.getString("status");
                                String fname = jsonObject1.getString("fname");
                                String lname = jsonObject1.getString("lname");
                                String photo = jsonObject1.getString("photo");

                                PassengerData passengerData=new PassengerData(id,user_id,rating,rstatus,fname,lname,photo);
                                arrayList.add(passengerData);
                            }
                            passengers.setAdapter(new PassengerAdapter(arrayList,PassengerListActivity.this));
                        }
                        else
                        {

                            no_passenger.setVisibility(View.VISIBLE);

                            passengers.setVisibility(View.GONE);
                            start.setVisibility(View.GONE);

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(PassengerListActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progress.setVisibility(View.GONE);

                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(PassengerListActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("errorrrr",""+error.getMessage());

            }
        });
    }

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

            MyViewHolder myViewHolder=new PassengerAdapter.MyViewHolder(v);

            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            final PassengerData passengerData=arrayList.get(position);
            Glide
                    .with(context)
                    .load(passengerData.getPhoto()).apply(new RequestOptions().placeholder(R.drawable.pr))
                    .into(holder.p_photo);

            holder.p_name.setText(passengerData.getFname()+" "+passengerData.getLname());

            if(trip_status.equalsIgnoreCase("ongoing"))
            {
                holder.select_radioButton.setVisibility(View.GONE);
            }

            holder.select_radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        userslist.add(passengerData.getUser_id());
                    }
                    else
                    {
                        userslist.remove(position);
                    }
                }
            });


            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(PassengerListActivity.this)) {

                        //If the draw over permission is not available open the settings screen
                        //to grant the permission.
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                    } else {
                        if(trip_text.getText().toString().equalsIgnoreCase("start trip"))
                        {
                            if(userslist.size()>0)
                            {
                                addPassengers("active",holder);
                            }
                            else
                            {
                                Toast.makeText(PassengerListActivity.this, "Please select passengers", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }


                    if(trip_text.getText().toString().equalsIgnoreCase("end trip"))
                    {
                        updateTripStatus("deactive",null);
                    }


                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public  class MyViewHolder extends RecyclerView.ViewHolder {

            LinearLayout radio;
            CircleImageView p_photo;
            TextView p_name;
            CheckBox select_radioButton;

            public MyViewHolder(View itemView) {
                super(itemView);
                radio=itemView.findViewById(R.id.radio);
                p_photo=itemView.findViewById(R.id.p_photo);
                p_name=itemView.findViewById(R.id.p_name);
                select_radioButton=itemView.findViewById(R.id.select_radioButton);

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(PassengerListActivity.this);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //Check if the permission is granted or not.
            // Settings activity never returns proper value so instead check with following method
            if (Settings.canDrawOverlays(this)) {
                /*if (!isMyServiceRunning(mSensorService.getClass())) {
                    mServiceIntent = new Intent(PassengerListActivity.this, mSensorService.getClass()).putExtra("click","first");
                    startService(mServiceIntent);
                }*/
                Toast.makeText(PassengerListActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
