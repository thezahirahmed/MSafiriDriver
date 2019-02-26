package com.eleganzit.msafiridriver;

import android.animation.Animator;

import com.eleganzit.msafiridriver.model.CountryData;
import com.eleganzit.msafiridriver.model.SampleSearchModel;
import com.eleganzit.msafiridriver.utils.CustomDateTimePicker;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganzit.msafiridriver.activity.Home;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.eleganzit.msafiridriver.activity.SaveRouteActivity;
import com.eleganzit.msafiridriver.fragment.TripFragment;
import com.eleganzit.msafiridriver.utils.DirectionsJSONParser;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

import static android.view.Window.FEATURE_NO_TITLE;

public class PickupLocation extends AppCompatActivity implements OnMapReadyCallback {
    MapView mapView;
    MarkerOptions a,b;
    Marker m1,m2;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorr;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Button pickupcontinue,pickupcontinue1,pickupcontinue2;
    ProgressDialog progressDialog;
    ImageView timeline;
    LinearLayout lin1,lin2,lin4;
    NestedScrollView lin3;
    ArrayList<String> getTenantses=new ArrayList<>();
    EditText pickup_location,pickup_date,pickup_time,destination_location,destination_date,destination_time;
    Calendar calendar=Calendar.getInstance();
    private String lat,lng,lat2,lng2;
    RelativeLayout progress_bar;
    GoogleMap googleMap;
    String dest_mydate = "";
    String pick_mydate = "";
    String dest_mytime = "";
    String pick_mytime = "";
    String dest_final_date = "";
    String pick_final_date = "";
    String from_title = "";
    String to_title = "";
    public String from_address = "";
    String to_address = "";
    TextView distance_time,from_date,from_location,to_date,to_location;
    private String miles;
    private double dmiles;
    private String distance;
    LinearLayout layoutBottom;
    RelativeLayout content;
    private String id;
    ProgressBar progress;
    private String date_is;
    public static final String inputFormat = "HH:mm";
    CustomDateTimePicker custom;
    ArrayList<SampleSearchModel> arrayList=new ArrayList<>();
    private String compareStringOne;

    SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);
    private String your_date_is_outdated;

    @Override
    protected void onResume() {
        super.onResume();
        if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("ar"))
        {
            Log.d("cdateeeeee",convertToArabic(dest_mydate+""));
            Log.d("cdateeeeee",convertToArabic(dest_mydate));

        }
        Log.d("cdateeeeee",Locale.getDefault().getLanguage()+"");

    }

    public String checkDate(String str1,String str2)
    {
        String str = "greater";
        Log.d("dateeeeed",str1+"   "+str2);

        try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm a");

            Date date1 = formatter.parse(str1);

            Date date2 = formatter.parse(str2);

            if (date1.before(date2))
            {
                System.out.println("date2 is Greater than my date1");
                str="greater";
                return str;
            }
            else
            {
                str="lesser";
                return str;
            }

        }catch (ParseException e1){
            e1.printStackTrace();
        }

        return str;
    }

    private void compareDates(String compareStringOne){
        Calendar now = Calendar.getInstance();
        Log.d("compppppp","innnn");

        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);

        /*date = parseDate(hour + ":" + minute);
        dateCompareOne = parseDate(compareStringOne);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        Date Time = null;
        Date CurrentTime= null;
        try {
            Time = dateFormat.parse(compareStringOne);
            CurrentTime = dateFormat.parse(dateFormat.format(new Date()));

        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (CurrentTime.before(Time))
        {
            Log.d("compppppp","selected future time");
        }
        else
        {
            Log.d("compppppp","pleasae select future time");

        }*/
        /*if ( dateCompareOne.before( date )) {
            //yada yada
            Log.d("compppppp","beforree current");
        }
        if ( dateCompareOne.after( date )) {
            //yada yada
            Log.d("compppppp","after current");
        }*/
    }

    private Date parseDate(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {

            Log.d("compppppp","eee "+e.getMessage());

            return new Date(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);

        setContentView(R.layout.activity_pickup_location);
        sharedPreferences=getSharedPreferences("pref",MODE_PRIVATE);
        editorr=sharedPreferences.edit();
        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        layoutBottom= findViewById(R.id.layoutBottom);
        /*YoYo.with(Techniques.SlideInRight)
                .duration(1000)
                .repeat(0)
                .playOn(layoutBottom);*/
        /*pref=getSharedPreferences("tripdetails",MODE_PRIVATE);
        editor=pref.edit();*/
        content= findViewById(R.id.content);
        mapView= (MapView) findViewById(R.id.map);
        //tenants=findViewById(R.id.recycler);
        progress_bar=findViewById(R.id.progress_bar);
        progress=findViewById(R.id.progress);
        timeline=findViewById(R.id.timeline);
        pickup_location=findViewById(R.id.pickup_location);
        pickup_date=findViewById(R.id.pickup_date);
        pickup_time=findViewById(R.id.pickup_time);
        destination_location=findViewById(R.id.destination_location);
        destination_date=findViewById(R.id.destination_date);
        destination_time=findViewById(R.id.destination_time);
        distance_time=findViewById(R.id.distance_time);
        from_date=findViewById(R.id.from_date);
        from_location=findViewById(R.id.from_location);
        from_location.setSelected(true);
        to_date=findViewById(R.id.to_date);
        to_location=findViewById(R.id.to_location);
        to_location.setSelected(true);
        ImageView home=findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(PickupLocation.this, Home.class));
                finish();
                Bungee.slideRight(PickupLocation.this);

            }
        });
        /////
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(PickupLocation.this, LinearLayoutManager.VERTICAL,false);

        pickupcontinue=findViewById(R.id.pickupcontinue);
        pickupcontinue1=findViewById(R.id.pickupcontinue1);
        pickupcontinue2=findViewById(R.id.pickupcontinue2);

        lin1=findViewById(R.id.lin1);
        lin2=findViewById(R.id.lin2);
        lin3=findViewById(R.id.lin3);

        getLocations();

        pickupcontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pickup_location.getText().toString().equalsIgnoreCase(""))
                {
                    pickup_location.requestFocus();
                    pickup_location.setError("Please select location");
                }
                else if(pickup_date.getText().toString().equalsIgnoreCase(""))
                {
                    pickup_date.requestFocus();
                    pickup_date.setError("Please select date");
                }
                else if(pickup_time.getText().toString().equalsIgnoreCase(""))
                {
                    pickup_time.requestFocus();
                    pickup_time.setError("Please select time");
                }
                else
                {
                    lin1.setVisibility(View.GONE);
                    pickupcontinue.setVisibility(View.GONE);
                    pickupcontinue1.setVisibility(View.VISIBLE);
                    lin2.setVisibility(View.VISIBLE);
                    timeline.setImageResource(R.drawable.wizard2);
                    pickupcontinue.setEnabled(false);
                }

            }
        });
        pickupcontinue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(destination_location.getText().toString().equalsIgnoreCase(""))
                {
                    destination_location.requestFocus();
                    destination_location.setError("Please select location");
                }
                else if(destination_date.getText().toString().equalsIgnoreCase(""))
                {
                    destination_date.requestFocus();
                    destination_date.setError("Please select date");
                }
                else if(destination_time.getText().toString().equalsIgnoreCase(""))
                {
                    destination_time.requestFocus();
                    destination_time.setError("Please select time");
                }
                else
                    {
                        pickupcontinue1.setEnabled(false);
                        if(getIntent().getStringExtra("from").equalsIgnoreCase("update"))
                        {
                            updateDrivertrip(googleMap);
                        }
                        else
                        {
                            if(date_is.equalsIgnoreCase("lesser"))
                            {
                                Toast.makeText(PickupLocation.this, "Please select the future hours", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                setDrivertrip(googleMap);
                            }
                        }

                }
            }
        });

        pickupcontinue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+lat+","+lng+"&daddr="+lat2+","+lng2));
                startActivity(intent);*/
                finish();
                        /*startActivity(new Intent(PickupLocation.this,Home.class));
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        finish();*/

            }
        });

        pickup_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pickup_location.setEnabled(false);

                new SimpleSearchDialogCompat(PickupLocation.this, "Search",
                        "Search for pickup", null, arrayList,
                        new SearchResultListener<SampleSearchModel>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog,
                                                   SampleSearchModel item, int position) {
                                pickup_location.setText(item.getTitle());
                                lat=""+item.getLat();
                                lng=""+item.getLng();

                                Log.d("latttt",""+lat);
                                Log.d("latttt",""+lng);

                                googleMap.getUiSettings().setAllGesturesEnabled(false);
                                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                                googleMap.getUiSettings().setAllGesturesEnabled(true);
                                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PickupLocation.this,R.raw.style_json));

                                LatLng loc2=new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc2));

                                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.location_green);
                                Bitmap b=bitmapdraw.getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 70, 70, false);

                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
                                a = new MarkerOptions().position(loc2).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Pickup Location");
                                if(getIntent().getStringExtra("from").equalsIgnoreCase("update"))
                                {

                                }
                                else
                                {
                                    m1 =googleMap.addMarker(a);
                                }
                                m1.setPosition(loc2);
                                //DrawMarker.getInstance(this).draw(googleMap, loc2, BitmapDescriptorFactory.fromBitmap(smallMarker), "Pickup Location");

                                from_title=item.getTitle();
                                from_address=item.getTitle();
                                dialog.dismiss();
                            }
                        }).show();
                //startActivityForResult(new Intent(PickupLocation.this,LocationSearch.class).putExtra("from","pickup_lin1"),1);
                /*Intent intent = null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(PickupLocation.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, 1);*/
            }
        });

        pickup_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog=new DatePickerDialog(PickupLocation.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String smonth="";
                        String sday="";
                        if(month<10)
                        {
                            smonth="0"+(month+1);
                        }
                        else
                        {
                            smonth=""+(month+1);
                        }
                        if(dayOfMonth<10)
                        {
                            sday="0"+dayOfMonth;
                        }
                        else
                        {
                            sday=""+dayOfMonth;
                        }
                        pick_mydate=year+"-"+smonth+"-"+sday;
                        pickup_date.setText(pick_mydate);
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        pickup_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(PickupLocation.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        if(selectedMinute<10)
                        {
                            pick_mytime=selectedHour+":0"+selectedMinute+":00";
                        }
                        else
                        {
                            pick_mytime=selectedHour+":"+selectedMinute+":00";
                        }
                        Log.d("compppppp","set "+compareStringOne);

                        String am_pm = "";
                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        datetime.set(Calendar.MINUTE, selectedMinute);

                            if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                                am_pm = "AM";
                            else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                                am_pm = "PM";
                            String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+"";
                            if(selectedMinute<10)
                            {
                                compareStringOne=pickup_date.getText().toString()+" "+selectedHour+":0"+datetime.get(Calendar.MINUTE);
                            }
                            else
                            {
                                compareStringOne=pickup_date.getText().toString()+" "+selectedHour+":"+datetime.get(Calendar.MINUTE);
                            }
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date strDate = null;
                            try {
                                strDate = formatter.parse(compareStringOne);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Log.d("timeessss",""+new Date().getTime() +"     "+ strDate.getTime());
                            if (new Date().getTime() > strDate.getTime()) {
                                your_date_is_outdated = "Please select the future hours";
                                Toast.makeText(PickupLocation.this, " "+your_date_is_outdated, Toast.LENGTH_SHORT).show();
                            }
                            else{
                                your_date_is_outdated = "valid";
                                if(selectedMinute<10)
                                {
                                    pickup_time.setText(strHrsToShow+":0"+datetime.get(Calendar.MINUTE)+" "+am_pm);

                                }
                                else
                                {
                                    pickup_time.setText(strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm);

                                }
                            }



                        pick_final_date=pick_mydate+" "+pick_mytime;

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        destination_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //destination_location.setEnabled(false);

                new SimpleSearchDialogCompat(PickupLocation.this, "Search",
                        "Search for destination", null, arrayList,
                        new SearchResultListener<SampleSearchModel>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog,
                                                   SampleSearchModel item, int position) {
                                destination_location.setText(item.getTitle());
                                lin1.setVisibility(View.GONE);
                                pickupcontinue.setVisibility(View.GONE);
                                pickupcontinue1.setVisibility(View.VISIBLE);
                                lin2.setVisibility(View.VISIBLE);
                                timeline.setImageResource(R.drawable.wizard2);

                                lat2=""+item.getLat();
                                lng2=""+item.getLng();

                                Log.d("latttt",""+lat2);
                                Log.d("latttt",""+lng2);

                                googleMap.getUiSettings().setAllGesturesEnabled(false);
                                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                                googleMap.getUiSettings().setAllGesturesEnabled(true);
                                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PickupLocation.this,R.raw.style_json));

                                to_title=item.getTitle();
                                to_address=item.getTitle();
                                dialog.dismiss();
                            }
                        }).show();

                //startActivityForResult(new Intent(PickupLocation.this,LocationSearch.class).putExtra("from","pickup_lin1"),2);
                /*Intent intent = null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(PickupLocation.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, 2);*/
            }
        });

        destination_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long timeInMilliseconds=0;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date mDate = sdf.parse(pick_mydate);
                    timeInMilliseconds = mDate.getTime();
                }
                catch (ParseException  e)
                {
                    e.printStackTrace();
                }
                DatePickerDialog datePickerDialog=new DatePickerDialog(PickupLocation.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String smonth="";
                        String sday="";
                        if(month<10)
                        {
                            smonth="0"+(month+1);
                        }
                        else
                        {
                            smonth=""+(month+1);
                        }
                        if(dayOfMonth<10)
                        {
                            sday="0"+dayOfMonth;
                        }
                        else
                        {
                            sday=""+dayOfMonth;
                        }
                        dest_mydate=year+"-"+smonth+"-"+sday;

                        destination_date.setText(dest_mydate);
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(timeInMilliseconds);
                datePickerDialog.show();
            }
        });

        destination_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(PickupLocation.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        if(selectedMinute<10)
                        {
                            dest_mytime=selectedHour+":0"+selectedMinute+":00";
                            //compareStringOne=selectedHour+":"+selectedMinute;
                        }
                        else
                        {
                            dest_mytime=selectedHour+":"+selectedMinute+":00";
                            //compareStringOne=selectedHour+":"+selectedMinute;

                        }
                        Log.d("compppppp","set "+compareStringOne);

                        compareDates(compareStringOne);
                        String am_pm = "";
                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        datetime.set(Calendar.MINUTE, selectedMinute);

                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";
                        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+"";
                        Log.d("dated",pickup_date.getText().toString()+" "+pickup_time.getText().toString()+"    ----      "+destination_date.getText().toString()+" "+strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm);
                        String compareStringOne2;

                        if(selectedMinute<10)
                        {
                            compareStringOne2=destination_date.getText().toString()+" "+selectedHour+":0"+datetime.get(Calendar.MINUTE);
                        }
                        else
                        {
                            compareStringOne2=destination_date.getText().toString()+" "+selectedHour+":"+datetime.get(Calendar.MINUTE);
                        }
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date strDate = null;
                        Date strDate2 = null;

                        try {
                            strDate = formatter.parse(compareStringOne);
                            strDate2 = formatter.parse(compareStringOne2);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Log.d("timeessss",""+new Date().getTime() +"     "+ strDate2.getTime());
                        if (strDate.getTime() > strDate2.getTime()) {
                            your_date_is_outdated = "invalid";
                            Toast.makeText(PickupLocation.this, "Please select the future hours", Toast.LENGTH_SHORT).show();
                            destination_time.setText("");
                            date_is="lesser";
                        }
                        else {
                            date_is="greater";
                            //Toast.makeText(PickupLocation.this, "Valid", Toast.LENGTH_SHORT).show();
                            if(selectedMinute<10)
                            {
                                destination_time.setText(strHrsToShow+":0"+datetime.get(Calendar.MINUTE)+" "+am_pm);
                            }
                            else
                            {
                                destination_time.setText(strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm);
                            }
                        }
                        /*
                        if(selectedMinute<10)
                        {
                            if(checkDate(pickup_date.getText().toString()+" "+pickup_time.getText().toString(),destination_date.getText().toString()+" "+strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm).equalsIgnoreCase("greater"))
                            {
                                date_is="greater";
                                destination_time.setText(strHrsToShow+":0"+datetime.get(Calendar.MINUTE)+" "+am_pm);
                            }
                            else
                            {
                                date_is="lesser";
                                destination_time.setText("");
                            }
                            Log.d("dateissss",date_is+"  ");

                        }
                        else
                        {
                            if(checkDate(pickup_date.getText().toString()+" "+pickup_time.getText().toString(),destination_date.getText().toString()+" "+strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm).equalsIgnoreCase("greater"))
                            {
                                date_is="greater";
                                destination_time.setText(strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm);
                            }
                            else
                            {
                                date_is="lesser";
                                destination_time.setText("");
                            }
                            Log.d("dateissss",date_is+"  ");
                        }*/
                        dest_final_date=dest_mydate+" "+dest_mytime;

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        mapView.getMapAsync(this);
        if(mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

            if(getIntent().getStringExtra("from").equalsIgnoreCase("update"))
            {
                id=getIntent().getStringExtra("id");
                getTrip();
            }

    }

    public void getLocations()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.Triplocations("",new retrofit.Callback<retrofit.client.Response>() {
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
                    Log.d("docsstringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {

                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                String loc_id = jsonObject1.getString("id");
                                String address = jsonObject1.getString("address");
                                String latitude = jsonObject1.getString("latitude");
                                String longitude = jsonObject1.getString("longitude");

                                SampleSearchModel sampleSearchModel=new SampleSearchModel(address,latitude,longitude);

                                arrayList.add(sampleSearchModel);
                            }

                        }
                        else
                        {

                        }

                    }
                    else
                    {

                        Toast.makeText(PickupLocation.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PickupLocation.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void getTrip()
    {
        content.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getTrip(id, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {

                progress.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);

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

                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                String id = jsonObject1.getString("trip_id");
                                String sfrom_title = jsonObject1.getString("from_title");
                                String from_lat = jsonObject1.getString("from_lat");
                                String from_lng = jsonObject1.getString("from_lng");
                                String sfrom_address = jsonObject1.getString("from_address");
                                String sto_title = jsonObject1.getString("to_title");
                                String to_lat = jsonObject1.getString("to_lat");
                                String to_lng = jsonObject1.getString("to_lng");
                                String sto_address = jsonObject1.getString("to_address");
                                String spickup_time = jsonObject1.getString("datetime");
                                String sdestination_time = jsonObject1.getString("end_datetime");
                                String statuss = jsonObject1.getString("status");
                                lat=from_lat;
                                lng=from_lng;
                                lat2=to_lat;
                                lng2=to_lng;
                                from_title=sfrom_title;
                                from_address=sfrom_address;
                                to_title=sto_title;
                                to_address=sto_address;
                                pick_final_date=spickup_time;
                                dest_final_date=sdestination_time;
                                String inputPattern = "yyyy-MM-dd HH:mm:ss";
                                String outputPattern = "yyyy-MM-dd";
                                SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                                SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                                String tinputPattern = "yyyy-MM-dd HH:mm:ss";
                                String toutputPattern = "HH:mm a";
                                SimpleDateFormat tinputFormat = new SimpleDateFormat(tinputPattern);
                                SimpleDateFormat toutputFormat = new SimpleDateFormat(toutputPattern);

                                Date pdate = null;
                                String pstr = null;
                                Date ddate = null;
                                String dstr = null;
                                Date ptime = null;
                                String ptstr = null;
                                Date dtime = null;
                                String dtstr = null;

                                try {
                                    pdate = inputFormat.parse(spickup_time);
                                    pstr = outputFormat.format(pdate);
                                    ddate = inputFormat.parse(sdestination_time);
                                    dstr = outputFormat.format(ddate);

                                    ptime = tinputFormat.parse(spickup_time);
                                    ptstr = toutputFormat.format(ptime);
                                    dtime = tinputFormat.parse(sdestination_time);
                                    dtstr = toutputFormat.format(dtime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                pickup_location.setText(sfrom_title);
                                pickup_date.setText(pstr);
                                pickup_time.setText(ptstr);
                                destination_location.setText(sto_title);
                                destination_date.setText(dstr);
                                destination_time.setText(dtstr);

                                LatLng loc=new LatLng(Double.parseDouble(from_lat),Double.parseDouble(from_lng));
                                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.location_green);
                                Bitmap bm=bitmapdraw.getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(bm, 70, 70, false);
                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
                                //DrawMarker.getInstance(this).draw(googleMap, loc2, BitmapDescriptorFactory.fromBitmap(smallMarker), "Pickup Location");
                                a = new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Pickup Location");
                                m1 =googleMap.addMarker(a);

                                LatLng loc2=new LatLng(Double.parseDouble(to_lat),Double.parseDouble(to_lng));
                                BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.location_red);
                                Bitmap b2=bitmapdraw2.getBitmap();
                                Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, 70, 70, false);

                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
                                //DrawMarker.getInstance(this).draw(googleMap, loc2, BitmapDescriptorFactory.fromBitmap(smallMarker), "Pickup Location");
                                b = new MarkerOptions().position(loc2).icon(BitmapDescriptorFactory.fromBitmap(smallMarker2)).title("Destination Location");
                                m2 =googleMap.addMarker(b);
                                String url = getDirectionsUrl(loc, loc2);
                            /*Polyline linee = googleMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng)), new LatLng(Double.parseDouble(lat2),Double.parseDouble(lng2)))
                                    .width(5)
                                    .color(Color.RED));*/
                                DownloadTask downloadTask = new DownloadTask();
                                // Start downloading json data from Google Directions API
                                downloadTask.execute(url);
                            }

                        }
                        else
                        {

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(PickupLocation.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progress.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(PickupLocation.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public String convertToArabic(String value)
    {
        String newValue = (((((((((((value+"")
                .replaceAll("1", "١")).replaceAll("2", "٢"))
                .replaceAll("3", "٣")).replaceAll("4", "٤"))
                .replaceAll("5", "٥")).replaceAll("6", "٦"))
                .replaceAll("7", "٧")).replaceAll("8", "٨"))
                .replaceAll("9", "٩")).replaceAll("0", "٠"));
        return newValue;
    }



    @Override
    public void onBackPressed() {

       /* if(lin3.getVisibility()==View.VISIBLE)
        {
           *//* lin1.setVisibility(View.GONE);
            lin3.setVisibility(View.GONE);
            pickupcontinue.setVisibility(View.GONE);
            pickupcontinue2.setVisibility(View.GONE);
            lin2.setVisibility(View.VISIBLE);
            pickupcontinue1.setVisibility(View.VISIBLE);
            pickupcontinue1.setEnabled(true);

            timeline.setImageResource(R.drawable.wizard2);*//*
        }
        else */if(lin2.getVisibility()==View.VISIBLE)
        {
            lin2.setVisibility(View.GONE);
            lin3.setVisibility(View.GONE);
            pickupcontinue1.setVisibility(View.GONE);
            pickupcontinue2.setVisibility(View.GONE);
            lin1.setVisibility(View.VISIBLE);
            pickupcontinue.setVisibility(View.VISIBLE);
            pickupcontinue.setEnabled(true);

            timeline.setImageResource(R.drawable.wizard1);
        }
        else if(lin3.getVisibility()==View.VISIBLE)
        {
           /* lin1.setVisibility(View.GONE);
            lin3.setVisibility(View.GONE);
            pickupcontinue.setVisibility(View.GONE);
            pickupcontinue2.setVisibility(View.GONE);
            lin2.setVisibility(View.VISIBLE);
            pickupcontinue1.setVisibility(View.VISIBLE);
            pickupcontinue1.setEnabled(true);

            timeline.setImageResource(R.drawable.wizard2);*/
        }
        else
        {
            super.onBackPressed();
            Bungee.slideRight(PickupLocation.this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        MapsInitializer.initialize(getApplicationContext());
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PickupLocation.this,R.raw.style_json));

        LatLng loc2=new LatLng(23.0262,72.5242);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc2));

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
        this.googleMap=googleMap;

        //drewRoute(googleMap,lat,lng,lat2,lng2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == 1)
        {
            Place place = PlacePicker.getPlace(data, this);
            String toastMsg = String.format("%s", place.getName());

            pickup_location.setEnabled(true);

            LatLng latLng=place.getLatLng();
            lat=""+latLng.latitude;
            lng=""+latLng.longitude;
            Log.d("latttt",""+toastMsg);
            Log.d("latttt",""+lat);
            Log.d("latttt",""+lng);
            Log.d("latttt",""+place.getAddress());

            pickup_location.setText(place.getName());
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PickupLocation.this,R.raw.style_json));

            LatLng loc2=new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc2));

            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.location_green);
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 70, 70, false);

            googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
            a = new MarkerOptions().position(loc2).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Pickup Location");
            if(getIntent().getStringExtra("from").equalsIgnoreCase("update"))
            {

            }
            else
            {
                m1 =googleMap.addMarker(a);
            }
            m1.setPosition(loc2);
            //DrawMarker.getInstance(this).draw(googleMap, loc2, BitmapDescriptorFactory.fromBitmap(smallMarker), "Pickup Location");

            from_title=place.getName().toString();
            from_address=place.getAddress().toString();
        }
        if(resultCode == RESULT_OK && requestCode == 2)
        {
            destination_location.setEnabled(true);
            lin1.setVisibility(View.GONE);
            pickupcontinue.setVisibility(View.GONE);
            pickupcontinue1.setVisibility(View.VISIBLE);
            lin2.setVisibility(View.VISIBLE);
            timeline.setImageResource(R.drawable.wizard2);
            Place place = PlacePicker.getPlace(data, this);
            String toastMsg = String.format("%s", place.getName());
            LatLng latLng=place.getLatLng();
            lat2=""+latLng.latitude;
            lng2=""+latLng.longitude;
            Log.d("latttt",""+toastMsg);
            Log.d("latttt",""+lat2);
            Log.d("latttt",""+lng2);
            Log.d("latttt",""+place.getAddress());
            destination_location.setText(place.getName());
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PickupLocation.this,R.raw.style_json));

            to_title=place.getName().toString();
            to_address=place.getAddress().toString();
            //setDrivertrip(googleMap);

        }
        if(resultCode == RESULT_CANCELED)
        {
            pickup_location.setEnabled(true);
            destination_location.setEnabled(true);

        }

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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

    public double convertKmsToMiles(double kms){
        double miles = 0.621371 * kms;
        return miles;
    }

    public void setDrivertrip(final GoogleMap googleMap)
    {
        lin2.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);

        Log.d("dataaaaaa",pref.getString("driver_id","")+"   "+from_title+"  "+lat+"  "+lng+"  "+from_address+"   "+to_title+"  "+lat2+"  "+lng2+"  "+to_address+"  "+pick_final_date+"  "+dest_final_date);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.setDrivertrip(pref.getString("driver_id",""), from_title, lat, lng,from_address,
                to_title, lat2, lng2, to_address, pick_final_date, dest_final_date, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {

                progress_bar.setVisibility(View.GONE);
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

                        Double latt,lngg,latt2,lngg2;
                        latt=Double.parseDouble(lat);
                        lngg=Double.parseDouble(lng);
                        latt2=Double.parseDouble(lat2);
                        lngg2=Double.parseDouble(lng2);

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {

                            LatLng loc2=new LatLng(Double.parseDouble(lat2),Double.parseDouble(lng2));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc2));

                            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.location_red);
                            Bitmap bm=bitmapdraw.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(bm, 70, 70, false);

                            //DrawMarker.getInstance(this).draw(googleMap, loc2, R.drawable.location_red, "Destination Location");

                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
                            b = new MarkerOptions().position(loc2).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Destination Location");
                            m2 =googleMap.addMarker(b);

                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String sfrom_date=jsonObject1.getString("datetime");
                                String sto_date=jsonObject1.getString("end_datetime");
                                String from_address=jsonObject1.getString("from_address");
                                String to_address=jsonObject1.getString("to_address");
                                String calculate_time=jsonObject1.getString("calculate_time");

                                String[] distance=calculate_time.split(":");

                                distance_time.setText(new DecimalFormat("##.##").format(convertKmsToMiles(distance(latt,lngg,latt2,lngg2)))+" miles - "+distance[0]+" hours "+distance[1]+" mins");
                                from_date.setText(parseDateToddMMyyyy2(sfrom_date));
                                from_location.setText(from_address);
                                to_date.setText(parseDateToddMMyyyy2(sto_date));
                                to_location.setText(to_address);
                            }

                            pickupcontinue1.setVisibility(View.GONE);
                            pickupcontinue2.setVisibility(View.VISIBLE);
                            lin3.setVisibility(View.VISIBLE);
                            timeline.setImageResource(R.drawable.wizard3);
                            //drewRoute(googleMap,lat,lng,lat2,lng2);
                            LatLng origin=new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                            LatLng destination=new LatLng(Double.parseDouble(lat2),Double.parseDouble(lng2));
                            String url = getDirectionsUrl(origin, destination);
                            /*Polyline linee = googleMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng)), new LatLng(Double.parseDouble(lat2),Double.parseDouble(lng2)))
                                    .width(5)
                                    .color(Color.RED));*/
                            DownloadTask downloadTask = new DownloadTask();
                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);
                            //distance_time.setText(miles+" - "+distance);
                            //Toast.makeText(RegistrationActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            pickupcontinue1.setEnabled(true);
                            Toast.makeText(PickupLocation.this, "Trip already exist", Toast.LENGTH_SHORT).show();
                            lin2.setVisibility(View.VISIBLE);
                        }

                    }
                    else
                    {

                        Toast.makeText(PickupLocation.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progress_bar.setVisibility(View.GONE);
                lin2.setVisibility(View.VISIBLE);
                pickupcontinue1.setEnabled(true);

                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(PickupLocation.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateDrivertrip(final GoogleMap googleMap)
    {
        lin2.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);

        Log.d("dataaaaaa",pref.getString("driver_id","")+"   "+from_title+"  "+lat+"  "+lng+"  "+from_address+"   "+to_title+"  "+lat2+"  "+lng2+"  "+to_address+"  "+pick_final_date+"  "+dest_final_date);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.updateDrivertrip(id, pickup_location.getText().toString(), lat, lng,from_address,
                destination_location.getText().toString(), lat2, lng2, to_address, pick_final_date, dest_final_date, new retrofit.Callback<retrofit.client.Response>() {
                    @Override
                    public void success(retrofit.client.Response response, retrofit.client.Response response2) {

                        progress_bar.setVisibility(View.GONE);
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

                                Double latt,lngg,latt2,lngg2;
                                latt=Double.parseDouble(lat);
                                lngg=Double.parseDouble(lng);
                                latt2=Double.parseDouble(lat2);
                                lngg2=Double.parseDouble(lng2);

                                JSONObject jsonObject = new JSONObject("" + stringBuilder);
                                String status = jsonObject.getString("status");
                                String message = jsonObject.getString("message");
                                JSONArray jsonArray = null;
                                if(status.equalsIgnoreCase("1"))
                                {

                                    LatLng loc2=new LatLng(Double.parseDouble(lat2),Double.parseDouble(lng2));
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc2));

                                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.location_red);
                                    Bitmap bm=bitmapdraw.getBitmap();
                                    Bitmap smallMarker = Bitmap.createScaledBitmap(bm, 70, 70, false);

                                    //DrawMarker.getInstance(this).draw(googleMap, loc2, R.drawable.location_red, "Destination Location");

                                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
                                    b = new MarkerOptions().position(loc2).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Destination Location");
                                    m2.setPosition(loc2);

                                    jsonArray = jsonObject.getJSONArray("data");
                                    for(int i=0;i<jsonArray.length();i++)
                                    {
                                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                        String sfrom_date=jsonObject1.getString("datetime");
                                        String sto_date=jsonObject1.getString("end_datetime");
                                        String from_address=jsonObject1.getString("from_address");
                                        String to_address=jsonObject1.getString("to_address");
                                        String calculate_time=jsonObject1.getString("calculate_time");

                                        String[] distance=calculate_time.split(":");

                                        //distance_time.setText(new DecimalFormat("##.##").format(convertKmsToMiles(distance(latt,lngg,latt2,lngg2)))+" miles - "+distance[0]+" hours "+distance[1]+" mins");
                                        from_date.setText(parseDateToddMMyyyy2(sfrom_date));
                                        from_location.setText(from_address);
                                        to_date.setText(parseDateToddMMyyyy2(sto_date));
                                        to_location.setText(to_address);
                                    }

                                    pickupcontinue1.setVisibility(View.GONE);
                                    pickupcontinue2.setVisibility(View.VISIBLE);
                                    lin3.setVisibility(View.VISIBLE);
                                    timeline.setImageResource(R.drawable.wizard3);
                                    //drewRoute(googleMap,lat,lng,lat2,lng2);
                                    LatLng origin=new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                                    LatLng destination=new LatLng(Double.parseDouble(lat2),Double.parseDouble(lng2));
                                    String url = getDirectionsUrl(origin, destination);
                            /*Polyline linee = googleMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng)), new LatLng(Double.parseDouble(lat2),Double.parseDouble(lng2)))
                                    .width(5)
                                    .color(Color.RED));*/
                                    DownloadTask downloadTask = new DownloadTask();
                                    // Start downloading json data from Google Directions API
                                    downloadTask.execute(url);
                                    //distance_time.setText(miles+" - "+distance);
                                    //Toast.makeText(RegistrationActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                                }
                                else
                                {
                                    pickupcontinue1.setEnabled(true);
                                    Toast.makeText(PickupLocation.this, "Trip already exist", Toast.LENGTH_SHORT).show();
                                    lin2.setVisibility(View.VISIBLE);
                                }

                            }
                            else
                            {

                                Toast.makeText(PickupLocation.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                            }


                        } catch (IOException e) {

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progress_bar.setVisibility(View.GONE);
                        lin2.setVisibility(View.VISIBLE);
                        pickupcontinue1.setEnabled(true);

                        //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                        Toast.makeText(PickupLocation.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = new ArrayList();
            PolylineOptions lineOptions = new PolylineOptions();
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.parseColor("#4885ed"));
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+"&key=AIzaSyD3t0-rMDn9zvQbzXqOEu1EUV9lssGSPjg";

        return url;
    }

    /*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void circularRevealActivity() {

        int cx = rootLayout.getWidth() * 12/ 10;
        int cy = rootLayout.getHeight() * 12/ 10;

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(500);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }*/
    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("Exception", data);

            JSONObject jsonObject=new JSONObject(""+data);
            JSONArray jsonArray=jsonObject.getJSONArray("routes");

            for (int i=0;i<jsonArray.length();i++)
            {

                JSONObject childObject=jsonArray.getJSONObject(i);

                JSONArray jsonArray1=childObject.getJSONArray("legs");
                for (int j=0;j<jsonArray1.length();j++)
                {
                    JSONObject jsonObject1=jsonArray1.getJSONObject(i);
                    Log.d("Exceptiondataaa",jsonObject1.getJSONObject("distance").getString("text"));
                    Log.d("Exceptiondataaa",jsonObject1.getJSONObject("duration").getString("text"));
                    String[] str=jsonObject1.getJSONObject("distance").getString("text").split(" ");

                    Log.d("Exceptiondataaa",""+convertKmsToMiles(Double.parseDouble(str[0]))+" miles");
                    miles = convertKmsToMiles(Double.parseDouble(str[0]))+"";
                    dmiles = convertKmsToMiles(Double.parseDouble(str[0]));
                    distance = jsonObject1.getJSONObject("duration").getString("text");
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //distance_time.setText(new DecimalFormat("##.##").format(dmiles)+" miles - "+distance);
                }
            });

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}
