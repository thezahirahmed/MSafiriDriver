package com.eleganzit.msafiridriver.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.eleganzit.msafiridriver.utils.GoogleService;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.eleganzit.msafiridriver.utils.MyLocation;
import com.eleganzit.msafiridriver.utils.SensorService;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;
import spencerstudios.com.bungeelib.Bungee;

public class PassengerListActivity extends AppCompatActivity {

    RelativeLayout top;
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
    private String photoPath;
    CheckBox select_allcheck;
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;
    TextView tv_latitude, tv_longitude, tv_address,tv_area,tv_locality;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    Double latitude,longitude;
    Geocoder geocoder;
    private String last_lat,last_lng;
    ImageView reload_passengers,reload_onboard_passengers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_list);

        p_pref=getSharedPreferences("passenger_pref",Context.MODE_PRIVATE);
        p_editor=p_pref.edit();
        pref=getSharedPreferences("location_pref",MODE_PRIVATE);
        editor=pref.edit();
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();

        trip_status=p_pref.getString("trip_status","");
        Log.d("trip_status","activity on create"+trip_status+"");
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

        photoPath=p_pref.getString("photoPath","").trim();
        Log.d("photoPathPP",""+photoPath);
        top=findViewById(R.id.top);
        passengers=findViewById(R.id.passengers);
        trip_text=findViewById(R.id.trip_text);
        start=findViewById(R.id.start);
        progress=findViewById(R.id.progress);
        no_passenger=findViewById(R.id.no_passenger);
        select_allcheck=findViewById(R.id.select_allcheck);
        reload_passengers = findViewById(R.id.reload_passengers);
        reload_onboard_passengers = findViewById(R.id.reload_onboard_passengers);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");

        trip_id=getIntent().getStringExtra("trip_id");

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        passengers.setLayoutManager(layoutManager);
        geocoder = new Geocoder(this, Locale.getDefault());
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();

        reload_passengers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPassengers();
            }
        });

        reload_onboard_passengers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBoardPassengers();
            }
        });

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){

                if(location==null)
                {

                }
                else
                {
                    Location destLocation=new Location("newlocation");
                    double lat=Double.parseDouble(trip_lat2);
                    double lng=Double.parseDouble(trip_lng2);
                    destLocation.setLatitude(lat);
                    destLocation.setLongitude(lng);
                    last_lat=String.valueOf(location.getLatitude());
                    last_lng=String.valueOf(location.getLongitude());
                    float distance = location.distanceTo(destLocation) /1000;
                    Log.i("wherreeeeeeemyloc", "distance between "+ last_lat+" and  "+last_lng);
                    //Log.i("wherreeeeeee lllllll", ""+ location.getLatitude()+"   "+location.getLongitude());

                }

            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(PassengerListActivity.this, locationResult);


/*
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boolean_permission) {

                    if (mPref.getString("service", "").matches("")) {
                        medit.putString("service", "service").commit();

                        Intent intent = new Intent(getApplicationContext(), GoogleService.class);
                        startService(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Service is already running", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enable the gps", Toast.LENGTH_SHORT).show();
                }

            }
        });

        fn_permission();*/

        /*start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               *//* if (boolean_permission) {

                    if (mPref.getString("service", "").matches("")) {
                        medit.putString("service", "service").commit();

                        Intent intent = new Intent(getApplicationContext(), GoogleService.class);
                        startService(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Service is already running", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enable the gps", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(PassengerListActivity.this, "click", Toast.LENGTH_SHORT).show();
*//*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(PassengerListActivity.this)) {

                    //If the draw over permission is not available open the settings screen
                    //to grant the permission.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                } else {
                    if(trip_text.getText().toString().equalsIgnoreCase("start trip"))
                    {
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

                }


                if(trip_text.getText().toString().equalsIgnoreCase("end trip"))
                {
                    updateDeactiveStatus("deactive",null);
                    p_editor.putBoolean("firstTime",false);
                    p_editor.commit();
                }


            }
        });*/

        //fn_permission();
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(PassengerListActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(PassengerListActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION

                        },
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));

            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String cityName = addresses.get(0).getAddressLine(0);
                String stateName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);


            } catch (IOException e1) {
                e1.printStackTrace();
            }

/*
            tv_latitude.setText(latitude+"");
            tv_longitude.setText(longitude+"");
            tv_address.getText();*/
            //Toast.makeText(context, "lat "+latitude+" lng "+longitude, Toast.LENGTH_SHORT).show();


        }
    };


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
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {

        //stopService(mServiceIntent);
        medit.clear();
        medit.apply();
        medit.commit();
        Log.i("wherreeeeeee", "onDestroy!");
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));

        if(select_allcheck.isChecked())
        {
            select_allcheck.setChecked(false);
        }

        trip_id=p_pref.getString("trip_id","");
        trip_lat=p_pref.getString("trip_lat","");
        trip_lng=p_pref.getString("trip_lng","");
        trip_lat2=p_pref.getString("trip_lat2","");
        trip_lng2=p_pref.getString("trip_lng2","");
        editor.putString("dest_lat",trip_lat2);
        editor.putString("dest_lng",trip_lng2);
        editor.commit();
        trip_status=p_pref.getString("trip_status","");
        Log.d("trip_status","activity on resume"+trip_status+"");
        if(trip_status.equalsIgnoreCase("ongoing"))
        {
            getOnBoardPassengers();
            //Toast.makeText(PassengerListActivity.this, ""+trip_status, Toast.LENGTH_SHORT).show();
            trip_text.setText("End Trip");
            passengers.setVisibility(View.GONE);
        }
        else {
            getPassengers();
            //Toast.makeText(PassengerListActivity.this, ""+trip_status, Toast.LENGTH_SHORT).show();
            trip_text.setText("Start Trip");
        }

        from=getIntent().getStringExtra("from");
        Log.d("fromwhereee",""+from+"");

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
        Log.d("uuuuuuuuuuu",userslist+"");
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<userslist.size();i++)
        {
            Log.d("productsssssssss",userslist.get(i)+"");
            if (i==userslist.size()-1)
            {
                sb.append(userslist.get(i)).append("");
            }
            else {
                sb.append(userslist.get(i)).append(",");

            }
        }

        Log.d("productsssssssss",sb+"");
        myInterface.onboardUserlist(trip_id, sb.toString(), new retrofit.Callback<retrofit.client.Response>() {
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
                            updateActiveStatus(tstatus,holder);
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

    public void updateActiveStatus(String tstatus, final PassengerAdapter.MyViewHolder holder)
    {
        if(tstatus.equalsIgnoreCase("active"))
        {
            tstatus="ongoing";
        }
        final String finalTstatus = tstatus;

        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);

        TypedFile map_ss=new TypedFile("multipart/form-data",new File(photoPath+""));

        myInterface.updateTripStatus(trip_id, tstatus, map_ss,new retrofit.Callback<retrofit.client.Response>() {
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

                                    Log.d("finalTstatus",finalTstatus+" <<<<");
                                    if(finalTstatus.equalsIgnoreCase("ongoing"))
                                    {
                                        Log.d("trip_latsss","lnggggggg  "+trip_lat+"   "+trip_lng);

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
                                                    Uri.parse("http://maps.google.com/maps/dir?saddr="+trip_lat+","+trip_lng+"&daddr="+trip_lat2+","+trip_lng2));//Uri.parse("http://maps.google.com/maps?saddr="+trip_lat+","+trip_lng+"&daddr="+trip_lat2+","+trip_lng2)
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
                                            /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                    Uri.parse("http://maps.google.com/maps/dir/"+23.0350+","+72.5293+"/"+23.0120+","+72.5108));//Uri.parse("http://maps.google.com/maps?saddr="+trip_lat+","+trip_lng+"&daddr="+trip_lat2+","+trip_lng2)
                                            startActivity(intent);
                                            finish();*/
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
                                    Toast.makeText(PassengerListActivity.this, ""+message, Toast.LENGTH_SHORT).show();
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

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

    public void updateDeactiveStatus(String tstatus, final PassengerAdapter.MyViewHolder holder)
    {
        if(tstatus.equalsIgnoreCase("active"))
        {
            tstatus="ongoing";
        }
        final String finalTstatus = tstatus;

        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);

        myInterface.updateTripStatus(trip_id, tstatus,new retrofit.Callback<retrofit.client.Response>() {
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
                                p_editor.clear();
                                p_editor.commit();
                                if (isMyServiceRunning(mSensorService.getClass())) {
                                    editor.putString("action","stop");
                                    editor.commit();
                                    mServiceIntent = new Intent(PassengerListActivity.this, mSensorService.getClass()).putExtra("click","second");
                                    stopService(mServiceIntent);
                                }
                                Intent intent=new Intent(PassengerListActivity.this,NavHomeActivity.class).putExtra("from","passenger_list");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                //finish();
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
                            Toast.makeText(PassengerListActivity.this, ""+message, Toast.LENGTH_SHORT).show();
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
        top.setVisibility(View.GONE);
        reload_passengers.setVisibility(View.GONE);

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);

        Log.d("ppppppppstringBuilder", "" + trip_id);

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
                    Log.d("ppppppppstringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {
                            if(from.equalsIgnoreCase("trip"))
                            {
                                if(trip_status.equalsIgnoreCase("ongoing"))
                                {
                                    top.setVisibility(View.GONE);
                                }
                                else
                                {
                                    top.setVisibility(View.VISIBLE);
                                }
                            }
                            else
                            {
                                top.setVisibility(View.GONE);
                            }

                            passengers.setVisibility(View.VISIBLE);
                            /*if(from.equalsIgnoreCase("home"))
                            {
                                start.setVisibility(View.GONE);
                                top.setVisibility(View.GONE);
                            }
                            else
                            {
                                start.setVisibility(View.VISIBLE);
                                top.setVisibility(View.VISIBLE);
                            }
*/
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
                           // start.setVisibility(View.GONE);

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
                reload_passengers.setVisibility(View.VISIBLE);
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(PassengerListActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("errorrrr",""+error.getMessage());

            }
        });
    }

    public void getOnBoardPassengers()
    {
        top.setVisibility(View.GONE);
        reload_onboard_passengers.setVisibility(View.GONE);

        progress.setVisibility(View.VISIBLE);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getOnBoardPassengers(trip_id, new retrofit.Callback<retrofit.client.Response>() {
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
                    Log.d("ppppppppstringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {
                            passengers.setVisibility(View.VISIBLE);

                                start.setVisibility(View.VISIBLE);

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
                reload_onboard_passengers.setVisibility(View.VISIBLE);
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
        boolean isSelectedAll;

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

            select_allcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked)
                    {
                        selectAll(true);
                    }
                    else
                    {
                        selectAll(false);
                    }
                }
            });

            if (!isSelectedAll) holder.select_radioButton.setChecked(false);
            else holder.select_radioButton.setChecked(true);

            if(trip_status.equalsIgnoreCase("ongoing") || from.equalsIgnoreCase("update") || from.equalsIgnoreCase("home"))
            {
                holder.select_radioButton.setVisibility(View.GONE);
            }

            int total_passengers=Integer.parseInt(passengerData.getId());

            holder.select_radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        userslist.add(passengerData.getUser_id());
                        //Toast.makeText(context, "added "+passengerData.getFname()+" "+passengerData.getLname()+" passengers", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        userslist.remove(passengerData.getUser_id());
                        //Toast.makeText(context, "removed "+passengerData.getFname()+" "+passengerData.getLname()+" passengers", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //Toast.makeText(PassengerListActivity.this, "here", Toast.LENGTH_SHORT).show();


            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*Geofence geofence = new Geofence.Builder()
                            .setRequestId("1") // Geofence ID
                            .setCircularRegion( Double.valueOf(last_lat), Double.valueOf(last_lng), 100) // defining fence region
                            .setExpirationDuration( 5000 ) // expiring date
                            // Transition types that it should look for
                            .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT )
                            .build();

                    GeofencingRequest request = new GeofencingRequest.Builder()
                            // Notification to trigger when the Geofence is created
                            .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                            .addGeofence( geofence ) // add a Geofence
                            .build();
*/
                    MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                        @Override
                        public void gotLocation(Location location){

                            if(location==null)
                            {

                            }
                            else
                            {
                                Location destLocation=new Location("newlocation");
                                double lat=Double.parseDouble(trip_lat2);
                                double lng=Double.parseDouble(trip_lng2);
                                destLocation.setLatitude(lat);
                                destLocation.setLongitude(lng);
                                last_lat=String.valueOf(location.getLatitude());
                                last_lng=String.valueOf(location.getLongitude());
                                float distance = location.distanceTo(destLocation) /1000;
                                Log.i("wherreeeeeeemyloc", "distance between "+ last_lat+" and  "+last_lng);
                                //Log.i("wherreeeeeee lllllll", ""+ location.getLatitude()+"   "+location.getLongitude());

                            }

                        }
                    };
                    MyLocation myLocation = new MyLocation();
                    myLocation.getLocation(PassengerListActivity.this, locationResult);


                        /*if (boolean_permission) {

                        if (mPref.getString("service", "").matches("")) {
                            medit.putString("service", "service").commit();

                            Intent intent = new Intent(getApplicationContext(), GoogleService.class);
                            startService(intent);

                        } else {
                            Toast.makeText(getApplicationContext(), "Service is already running", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enable the gps", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(PassengerListActivity.this, "click", Toast.LENGTH_SHORT).show();
*/
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

                                    /*if (distance(Double.valueOf(last_lat), Double.valueOf(last_lng), Double.valueOf(trip_lat2), Double.valueOf(trip_lng2)) < 0.1) { // if distance < 0.1 miles we take locations as equal*/
                                        //do what you want to do...
                                        addPassengers("active",holder);
                                    /*}
                                    else
                                    {
                                        new AlertDialog.Builder(context).setMessage("First reach at the pickup location!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        }).show();
                                    }*/

                                }
                                else
                                {
                                    Toast.makeText(PassengerListActivity.this, "Please select passengers", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }


                        if(trip_text.getText().toString().equalsIgnoreCase("end trip"))
                        {

                           locationResult = new MyLocation.LocationResult(){
                                @Override
                                public void gotLocation(Location location){

                                    if(location==null)
                                    {

                                    }
                                    else
                                    {
                                        Location destLocation=new Location("newlocation");
                                        double lat=Double.parseDouble(trip_lat2);
                                        double lng=Double.parseDouble(trip_lng2);
                                        destLocation.setLatitude(lat);
                                        destLocation.setLongitude(lng);
                                        last_lat=String.valueOf(location.getLatitude());
                                        last_lng=String.valueOf(location.getLongitude());
                                        float distance = location.distanceTo(destLocation) /1000;
                                        Log.i("wherreeeeeeemyloc", "distance between "+ last_lat+" and  "+last_lng);
                                        //Log.i("wherreeeeeee lllllll", ""+ location.getLatitude()+"   "+location.getLongitude());

                                    }

                                }
                            };
                            myLocation = new MyLocation();
                            myLocation.getLocation(PassengerListActivity.this, locationResult);


                            /*if (distance(Double.valueOf(last_lat), Double.valueOf(last_lng), Double.valueOf(trip_lat2), Double.valueOf(trip_lng2)) < 0.1) {*/

                                updateDeactiveStatus("deactive",null);
                                p_editor.putBoolean("firstTime",false);
                                p_editor.commit();
                           /* }
                            else
                            {
                                new AlertDialog.Builder(context).setMessage("First reach at the pickup location!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                            }
*/
                        }


                }
            });

        }

        public void selectAll(boolean status){
            Log.e("onClickSelectAll","yes");
            isSelectedAll=status;
            notifyDataSetChanged();
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
                        "Please allow the Draw over other app permission.",
                        Toast.LENGTH_LONG).show();

                //finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
