package com.eleganzit.msafiridriver.activity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.model.PassengerData;
import com.eleganzit.msafiridriver.utils.GoogleService;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.eleganzit.msafiridriver.utils.MyLocation;
import com.eleganzit.msafiridriver.utils.SensorService;

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

public class OnboardPassengerListActivity extends AppCompatActivity {

    RecyclerView passengers;
    ProgressBar progressBar;
    TextView no_passenger;
    private String trip_id;
    SharedPreferences p_pref;
    SharedPreferences.Editor p_editor;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    LinearLayout end;
    ProgressDialog progressDialog;
    Intent mServiceIntent;
    private SensorService mSensorService;
    Handler handler;
    private String trip_lat,trip_lng,trip_lat2,trip_lng2,trip_status;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    ArrayList<String> userslist=new ArrayList<>();
    private String photoPath;
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;
    TextView tv_latitude, tv_longitude, tv_address,tv_area,tv_locality;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    Double latitude,longitude;
    Geocoder geocoder;
    private String last_lat,last_lng;
    ImageView reload_onboard_passengers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard_passenger_list);

        p_pref=getSharedPreferences("passenger_pref",Context.MODE_PRIVATE);
        p_editor=p_pref.edit();
        pref=getSharedPreferences("location_pref",MODE_PRIVATE);
        editor=pref.edit();
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();

        trip_status=p_pref.getString("trip_status","");
        Log.d("trip_status","activity on create"+trip_status+"");
        handler=new Handler();
        mSensorService = new SensorService(handler,pref,this);
        mServiceIntent = new Intent(OnboardPassengerListActivity.this, mSensorService.getClass());

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Bungee.slideRight(OnboardPassengerListActivity.this);
            }
        });

        photoPath=p_pref.getString("photoPath","").trim();
        Log.d("photoPathPP",""+photoPath);
        passengers=findViewById(R.id.onboard_passengers);
        progressBar=findViewById(R.id.progressBar);

        end=findViewById(R.id.end);
        no_passenger=findViewById(R.id.no_passenger);
        reload_onboard_passengers = findViewById(R.id.reload_onboard_passengers);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        passengers.setLayoutManager(layoutManager);
        geocoder = new Geocoder(this, Locale.getDefault());
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();

        reload_onboard_passengers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBoardPassengers();
            }
        });

        /*MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
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
        myLocation.getLocation(OnboardPassengerListActivity.this, locationResult);
*/
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

        trip_id=p_pref.getString("trip_id","");
        trip_lat=p_pref.getString("trip_lat","");
        trip_lng=p_pref.getString("trip_lng","");
        trip_lat2=p_pref.getString("trip_lat2","");
        trip_lng2=p_pref.getString("trip_lng2","");
        editor.putString("dest_lat",trip_lat2);
        editor.putString("dest_lng",trip_lng2);
        editor.commit();
        trip_status=p_pref.getString("trip_status","");
        end.setVisibility(View.GONE);
        getOnBoardPassengers();
        Log.d("trip_status","activity on resume"+trip_status+"");

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

                                p_editor.clear();
                                p_editor.commit();
                                if (isMyServiceRunning(mSensorService.getClass())) {
                                    editor.putString("action","stop");
                                    editor.commit();
                                    mServiceIntent = new Intent(OnboardPassengerListActivity.this, mSensorService.getClass()).putExtra("click","second");
                                    stopService(mServiceIntent);
                                }
                                Intent intent=new Intent(OnboardPassengerListActivity.this,NavHomeActivity.class).putExtra("from","passenger_list");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                //finish();


                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                //String sfrom_date=jsonObject1.getString("datetime");
                            }

                        }
                        else
                        {
                            Toast.makeText(OnboardPassengerListActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(OnboardPassengerListActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(OnboardPassengerListActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getOnBoardPassengers()
    {

        reload_onboard_passengers.setVisibility(View.GONE);
        progressDialog.show();

        progressBar.setVisibility(View.VISIBLE);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getOnBoardPassengers(trip_id, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                ArrayList<PassengerData> arrayList = new ArrayList<>();
                progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();

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

                                end.setVisibility(View.VISIBLE);

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
                                String name;
                                name=fname+" "+lname;
                                JSONObject jsonObject2=jsonObject1.getJSONObject("passanger");
                                JSONArray jsonArray1=jsonObject2.getJSONArray("data");

                                for (int j=0;j<jsonArray1.length();j++)
                                {
                                    JSONObject jsonObject3=jsonArray1.getJSONObject(j);
                                    String passanger_id = jsonObject3.getString("passanger_id");
                                    String passanger_name = jsonObject3.getString("passanger_name");
                                    String book_id = jsonObject3.getString("book_id");

                                    PassengerData passengerData=new PassengerData(id,passanger_id,rating,rstatus,passanger_name,lname,photo);
                                    arrayList.add(passengerData);
                                }


                            }
                            passengers.setAdapter(new PassengerAdapter(arrayList,OnboardPassengerListActivity.this));
                        }
                        else
                        {

                            no_passenger.setVisibility(View.VISIBLE);

                            passengers.setVisibility(View.GONE);
                            end.setVisibility(View.GONE);

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(OnboardPassengerListActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressBar.setVisibility(View.GONE);
                reload_onboard_passengers.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(OnboardPassengerListActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

            holder.p_name.setText(passengerData.getFname()+"");

            if (!isSelectedAll) holder.select_radioButton.setChecked(false);
            else holder.select_radioButton.setChecked(true);

            if(trip_status.equalsIgnoreCase("ongoing"))
            {
                holder.select_radioButton.setVisibility(View.GONE);
            }

            end.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
                    myLocation.getLocation(OnboardPassengerListActivity.this, locationResult);

                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(OnboardPassengerListActivity.this)) {

                            //If the draw over permission is not available open the settings screen
                            //to grant the permission.
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                        } else {

                        }*/
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
        Bungee.slideRight(OnboardPassengerListActivity.this);

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
                Toast.makeText(OnboardPassengerListActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
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
