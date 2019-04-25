package com.eleganzit.msafiridriver.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eleganzit.msafiridriver.MapActivity;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.activity.OnboardPassengerListActivity;
import com.eleganzit.msafiridriver.activity.PassengerListActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by fabio on 30/01/2016.
 */
public class SensorService extends Service implements GoogleApiClient.ConnectionCallbacks
{
    public int counter = 0;
    private LocationRequest mLocationClient;
    private WindowManager mWindowManager;
    private View mChatHeadView;
    String intent_data;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    SharedPreferences p_pref;
    SharedPreferences.Editor p_editor;
    String intent_action;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5445;
    Activity activity;
    private Handler handler; // Handler used to execute code on the UI thread
    private String trip_id,last_lat,last_lng;

    public SensorService(Handler handler, SharedPreferences pref, Activity activity) {
        super();
        Log.i("wherreeeeeee", "here I am!");
        this.handler = handler;
        this.pref = pref;
        this.activity = activity;
    }

    public SensorService()
    {

    }


    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentLocationMarker;
    private Location currentLocation;
    private boolean firstTimeFlag = true;

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() == null)
            {
                Log.d("where","locationResult.getLastLocation() == null");

                return;
            }
            currentLocation = locationResult.getLastLocation();
            last_lat=String.valueOf(currentLocation.getLatitude()).trim();
            last_lng=String.valueOf(currentLocation.getLongitude()).trim();
            Log.d("wherelll","onLocationResult   "+last_lat+"     "+last_lng);

            if (firstTimeFlag) {
                Log.d("where","firstTimeFlag && googleMap != null   "+currentLocation);

                firstTimeFlag = false;
            }
        }
    };



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1,new Notification());
        //Inflate the chat head layout we created

    }

    private void startCurrentLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        Log.d("where","startCurrentLocationUpdates");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                return;
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        p_pref=getSharedPreferences("passenger_pref",Context.MODE_PRIVATE);
        p_editor=p_pref.edit();
        pref=getSharedPreferences("location_pref",MODE_PRIVATE);
        editor=pref.edit();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        startCurrentLocationUpdates();
        intent_action = pref.getString("action", "");
        trip_id = p_pref.getString("trip_id", "").trim();

        Log.d("trip_statusiddd","id ->"+trip_id+"");

        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.layout_chat_head, null);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the chat head position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 250;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        if(intent!=null)
        {
            intent_data=intent.getStringExtra("click");
            Log.i("wherreeeeeee", "onStartCommand  "+trip_id+"   "+intent_data);
            Log.d("whereeereee","intent_data  "+intent_data);
            if(intent_data!=null)
            {
                if(intent_data.equalsIgnoreCase("first"))
                {
                    Log.d("whereeereee","first");
                    if(intent_action.equalsIgnoreCase("start")) {
                        Log.d("whereeereee","start");
                        mWindowManager.addView(mChatHeadView, params);
                    }
                }
                else
                {
                    Log.d("whereeereee","elsee");
                    mWindowManager.removeViewImmediate(mChatHeadView);
                }
            }

        }

        //Set the close button.
        ImageView closeButton = (ImageView) mChatHeadView.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close the service and remove the chat head from the window
                mWindowManager.removeViewImmediate(mChatHeadView);
                //stopSelf();
            }
        });

        //Drag and move chat head using user's touch action.
        final ImageView chatHeadImage = (ImageView) mChatHeadView.findViewById(R.id.chat_head_profile_iv);
        chatHeadImage.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private static final int MAX_CLICK_DURATION = 200;
            private long startClickTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        Log.d("lastttaction","before  down  "+lastAction+"");
                        lastAction = event.getAction();
                        Log.d("lastttaction","after   down   "+lastAction+"");
                        //Toast.makeText(SensorService.this, "down", Toast.LENGTH_SHORT).show();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        Log.d("lastttaction","before  up  "+lastAction+"");
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if(clickDuration < MAX_CLICK_DURATION) {
                            //click event has occurred
                            //Toast.makeText(SensorService.this, "Ending trip", Toast.LENGTH_SHORT).show();
                            //Open the chat conversation click.
                            AlertDialog.Builder builder=new AlertDialog.Builder(getApplicationContext()).setMessage("Do you want to end the trip?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                   /* Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+last_lat+","+last_lng));
                                    startActivity(intent1);
*/
                                    Intent intent = new Intent(SensorService.this, OnboardPassengerListActivity.class).putExtra("from","map");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    //Toast.makeText(SensorService.this, "up", Toast.LENGTH_SHORT).show();
                                    //close the service and remove the chat heads
                                    mWindowManager.removeViewImmediate(mChatHeadView);
                                    //stopSelf();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            final AlertDialog alertDialog = builder.create();

                            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                            alertDialog.show();

                        }
                        if (lastAction == MotionEvent.ACTION_DOWN) {

                        }
                        lastAction = event.getAction();
                        Log.d("lastttaction","after  up  "+lastAction+"");
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        Log.d("lastttaction","before  move  "+lastAction+"");
                        mWindowManager.updateViewLayout(mChatHeadView, params);
                        lastAction = event.getAction();
                        Log.d("lastttaction","after  move  "+lastAction+"");
                        //Toast.makeText(SensorService.this, "moved", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
        startTimer();
        /* handler.postDelayed(new Runnable() {
            public void run() {
                Toast.makeText(context, "location update", Toast.LENGTH_SHORT).show();         // this method will contain your almost-finished HTTP calls
                handler.postDelayed(this, FIVE_SECONDS);
            }
        }, FIVE_SECONDS);*/
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        intent_action=pref.getString("action","");
        Log.i("wherreeeeeee", "sensor service ondestroy!  "+intent_action);
        /*if(intent_action.equalsIgnoreCase("stop"))
        {
            mWindowManager.removeViewImmediate(mChatHeadView);
        }*/
        Intent broadcastIntent = new Intent(this, MyLocationReceiver.class);
        sendBroadcast(broadcastIntent);
        stoptimertask();
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);

        fusedLocationProviderClient = null;

    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 10000); //


    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

                Location crntLocation=new Location("crntlocation");
                crntLocation.setLatitude(23.011783);
                crntLocation.setLongitude(72.523117);

                //Toast.makeText(SensorService.this, "location update", Toast.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    public void run() {
                        updatelastLocation();

                        /*MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                            @Override
                            public void gotLocation(Location location){

                                if(location==null)
                                {

                                }
                                else
                                {
                                    Location destLocation=new Location("newlocation");
                                    double lat=Double.parseDouble(pref.getString("dest_lat",""));
                                    double lng=Double.parseDouble(pref.getString("dest_lng",""));
                                    destLocation.setLatitude(lat);
                                    destLocation.setLongitude(lng);
                                    *//*last_lat=String.valueOf(location.getLatitude());
                                    last_lng=String.valueOf(location.getLongitude());*//*
                                    float distance = location.distanceTo(destLocation) /1000;
                                    Log.i("wherreeeeeee lllllll", "distance between "+ location.getLatitude()+" and  "+location.getLongitude()+" is "+ distance);
                                    //Log.i("wherreeeeeee lllllll", ""+ location.getLatitude()+"   "+location.getLongitude());

                                }

                            }
                        };
                        MyLocation myLocation = new MyLocation();
                        myLocation.getLocation(SensorService.this, locationResult);
*/
                        //Toast.makeText(SensorService.this, "location update", Toast.LENGTH_SHORT).show();
                    }
                },10000);
            }
        };

    }

    public void updatelastLocation()
    {

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);

        myInterface.lastLocation(trip_id, last_lat, last_lng, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                final StringBuilder stringBuilder = new StringBuilder();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("lastlllstringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {

                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                //String sfrom_date=jsonObject1.getString("datetime");
                            }

                        }
                        else
                        {
                            Toast.makeText(SensorService.this, ""+message, Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(SensorService.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(SensorService.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}