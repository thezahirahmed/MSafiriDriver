package com.eleganzit.msafiridriver.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
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
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.VehicleDetailsActivity;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.eleganzit.msafiridriver.adapter.UpcomingTripAdapter;
import com.eleganzit.msafiridriver.model.TripData;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener  {
    private ShimmerFrameLayout shimmerFrameLayout;
    Animation pop_anim;
    RecyclerView upcoming;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout current, past;
    TextView active;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView no_trips;
    ImageView reload;
    ArrayList<TripData> arrayList;
    LinearLayout sort;
    RelativeLayout lcontent;
    TextView trip_title;
    CardView card,upcoming_card,past_card;
    String trip_type="current";
    ProgressDialog progressDialog;
    public HomeFragment() {
        // Required empty public constructor
    }


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        NavHomeActivity.home_title.setText("Home");
        NavHomeActivity.active.setVisibility(View.VISIBLE);


        shimmerFrameLayout = v.findViewById(R.id.shimmerLayout);
        pref = getActivity().getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();
        if(pref.getString("type","").equalsIgnoreCase("individual"))
        {
            NavHomeActivity.fab.setVisibility(View.VISIBLE);
        }
        else
        {
            NavHomeActivity.fab.setVisibility(View.GONE);
        }
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");

        trip_title = v.findViewById(R.id.trip_title);
        card = v.findViewById(R.id.card);
        upcoming_card = v.findViewById(R.id.upcoming_card);
        past_card = v.findViewById(R.id.past_card);
        upcoming = v.findViewById(R.id.upcoming);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                getDriverTrips(trip_type);
            }
        });


        lcontent = v.findViewById(R.id.lcontent);
        sort = v.findViewById(R.id.sort);
        no_trips = v.findViewById(R.id.no_trips);
        reload = v.findViewById(R.id.reload);
        current = v.findViewById(R.id.current);
        active = v.findViewById(R.id.active);
        past = v.findViewById(R.id.past);
        pop_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_anim);

        SharedPreferences p_pref=getActivity().getSharedPreferences("passenger_pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor p_editor=p_pref.edit();

        Log.d("trip_status","home fragment   "+p_pref.getString("trip_status","")+"");
        Log.d("whereeeee","oncreateview");


        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        getDriverdata();
        current.setBackgroundResource(R.drawable.tab_left_light_bg);
        past.setBackgroundResource(R.drawable.tab_right_dark_bg);
        no_trips.setVisibility(View.GONE);
        upcoming.setVisibility(View.GONE);
        trip_title.setText("Upcoming Trips");

        card.setCardElevation(0);
        card.setMaxCardElevation(0);
        getDriverTrips("current");
        Log.d("whereeeee","onResume");

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        active.startAnimation(pop_anim);
        Log.d("whereeeee","onViewCreated");
        String locale = getActivity().getResources().getConfiguration().locale.getDisplayName();
        String locale2 = java.util.Locale.getDefault().getDisplayName();

        Log.d("llocacleeeeee",locale+"      "+locale2);

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new Dialog(getActivity());
                dialog.setContentView(R.layout.sorting_dialog);
                final TextView date=dialog.findViewById(R.id.date);
                final TextView location=dialog.findViewById(R.id.location);

                date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Collections.sort(arrayList, new Comparator<TripData>() {
                            public int compare(TripData o1, TripData o2) {
                                if (o1.getPickup_time() == null || o2.getPickup_time() == null)
                                    return 0;
                                return o1.getPickup_time().compareTo(o2.getPickup_time());
                            }
                        });
                        upcoming.setAdapter(new UpcomingTripAdapter(pref.getString("type",""),arrayList, getActivity()));
                        dialog.dismiss();
                    }
                });

                location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Collections.sort(arrayList, new Comparator<TripData>() {
                            public int compare(TripData o1, TripData o2) {
                                if (o1.getFrom_address() == null || o2.getFrom_address() == null)
                                    return 0;
                                return o1.getFrom_address().compareTo(o2.getFrom_address());
                            }
                        });
                        upcoming.setAdapter(new UpcomingTripAdapter(pref.getString("type",""),arrayList, getActivity()));
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                if(arrayList.size()>0 && arrayList!=null)
                {
                    dialog.show();

                }
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        upcoming.setLayoutManager(layoutManager);
        upcoming.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {

                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(-1)) {
                    Log.d("atttttt","top");
                    card.setCardElevation(0);
                    card.setMaxCardElevation(0);
                }
                else
                {
                    Log.d("atttttt","not top");
                    card.setCardElevation(10);
                    card.setMaxCardElevation(10);
                }

            }

        });
        upcoming.setVisibility(View.GONE);

        upcoming.setItemAnimator(new DefaultItemAnimator());
        upcoming_card.setCardElevation(5);
        upcoming_card.setMaxCardElevation(5);
        past_card.setCardElevation(0);
        past_card.setMaxCardElevation(0);
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current.setBackgroundResource(R.drawable.tab_left_light_bg);
                past.setBackgroundResource(R.drawable.tab_right_dark_bg);
                no_trips.setVisibility(View.GONE);
                upcoming.setVisibility(View.GONE);
                upcoming_card.setCardElevation(5);
                upcoming_card.setMaxCardElevation(5);
                past_card.setCardElevation(0);
                past_card.setMaxCardElevation(0);
                YoYo.with(Techniques.SlideInLeft)
                        .duration(300)
                        .repeat(0)
                        .playOn(shimmerFrameLayout);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                trip_title.setText("Upcoming Trips");
                shimmerFrameLayout.startShimmer();
                getDriverTrips("current");
                trip_type="current";

            }
        });
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current.setBackgroundResource(R.drawable.tab_left_dark_bg);
                past.setBackgroundResource(R.drawable.tab_right_light_bg);
                no_trips.setVisibility(View.GONE);
                upcoming.setVisibility(View.GONE);
                upcoming_card.setCardElevation(0);
                upcoming_card.setMaxCardElevation(0);
                past_card.setCardElevation(5);
                past_card.setMaxCardElevation(5);
                YoYo.with(Techniques.SlideInRight)
                        .duration(300)
                        .repeat(0)
                        .playOn(shimmerFrameLayout);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                trip_title.setText("Past Trips");
                shimmerFrameLayout.startShimmer();
                getDriverTrips("past");
                trip_type="past";
            }
        });

        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active.startAnimation(pop_anim);

                if (active.getText().toString().equalsIgnoreCase("Go Online")) {
                    active.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green_dot, 0, 0, 0);
                    active.setText("Go Offline");

                } else if (active.getText().toString().equalsIgnoreCase("Go Offline")) {
                    active.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_dot, 0, 0, 0);
                    active.setText("Go Online");
                }
            }

        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDriverTrips(trip_type);
            }
        });

    }

    public void getDriverdata() {
        /*NavHomeActivity.active.setVisibility(View.GONE);
        NavHomeActivity.toolbar_progress.setVisibility(View.VISIBLE);*/
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getDriverdata(pref.getString("driver_id", ""), new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                /*NavHomeActivity.active.setVisibility(View.VISIBLE);
                NavHomeActivity.toolbar_progress.setVisibility(View.GONE);*/
                progressDialog.dismiss();
                final StringBuilder stringBuilder = new StringBuilder();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("dddddddstringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        JSONArray jsonArray = null;
                        String dstatus = "";
                        if (status.equalsIgnoreCase("1")) {
                            jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                dstatus = jsonObject1.getString("status");
                                editor.putString("dstatus", dstatus);

                                editor.commit();

                            }

                            if (pref.getString("dstatus", "").equalsIgnoreCase("active")) {
                                NavHomeActivity.active.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green_dot, 0, 0, 0);
                                NavHomeActivity.active.setText("Go Offline");
                                status = "active";
                            } else {
                                NavHomeActivity.active.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_dot, 0, 0, 0);
                                NavHomeActivity.active.setText("Go Online");
                                status = "deactive";
                            }


                        } else {

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(getActivity(), "" + stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                /*NavHomeActivity.active.setVisibility(View.VISIBLE);
                NavHomeActivity.toolbar_progress.setVisibility(View.GONE);*/
                progressDialog.dismiss();
//Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void getDriverTrips(final String trip_type)
    {
        no_trips.setVisibility(View.GONE);
        upcoming.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(true);
        YoYo.with(Techniques.SlideInLeft)
                .duration(300)
                .repeat(0)
                .playOn(shimmerFrameLayout);
        shimmerFrameLayout.startShimmer();
        reload.setVisibility(View.GONE);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getDriverTrips(pref.getString("driver_id",""), trip_type, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                arrayList = new ArrayList<>();
                YoYo.with(Techniques.SlideOutRight)
                        .duration(300)
                        .repeat(0)
                        .playOn(shimmerFrameLayout);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
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
                            no_trips.setVisibility(View.GONE);
                            upcoming.setVisibility(View.VISIBLE);
                            if(trip_type.equalsIgnoreCase("past"))
                            {
                                YoYo.with(Techniques.SlideInRight)
                                        .duration(300)
                                        .repeat(0)
                                        .playOn(upcoming);
                            }
                            else
                            {
                                YoYo.with(Techniques.SlideInLeft)
                                        .duration(300)
                                        .repeat(0)
                                        .playOn(upcoming);
                            }
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                String id = jsonObject1.getString("id");
                                String from_title = jsonObject1.getString("from_title");
                                String from_lat = jsonObject1.getString("from_lat");
                                String from_lng = jsonObject1.getString("from_lng");
                                String from_address = jsonObject1.getString("from_address");
                                String to_title = jsonObject1.getString("to_title");
                                String to_lat = jsonObject1.getString("to_lat");
                                String to_lng = jsonObject1.getString("to_lng");
                                String to_address = jsonObject1.getString("to_address");
                                String pickup_time = jsonObject1.getString("datetime");
                                String destination_time = jsonObject1.getString("end_datetime");
                                String statuss = jsonObject1.getString("status");
                                String trip_price = jsonObject1.getString("trip_price");
                                Date date = null;
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    date = format.parse(pickup_time);
                                    System.out.println(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                TripData tripData=new TripData(trip_type,id,from_title,from_lat,from_lng,from_address,to_title,to_lat,to_lng,to_address,pickup_time,date,destination_time,statuss,trip_price);
                                arrayList.add(tripData);

                            }
                            upcoming.setAdapter(new UpcomingTripAdapter(pref.getString("type",""),arrayList, getActivity()));
                        }
                        else
                        {

                            no_trips.setVisibility(View.VISIBLE);
                            if(trip_type.equalsIgnoreCase("past"))
                            {
                                YoYo.with(Techniques.SlideInRight)
                                        .duration(300)
                                        .repeat(0)
                                        .playOn(no_trips);
                            }
                            else
                            {
                                YoYo.with(Techniques.SlideInLeft)
                                        .duration(300)
                                        .repeat(0)
                                        .playOn(no_trips);
                            }

                            upcoming.setVisibility(View.GONE);
                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(getActivity(), ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                reload.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
//Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("errorrrr",""+error.getMessage());
                Toast.makeText(getActivity(), "Couldn't refresh trips", Toast.LENGTH_SHORT).show();
                reload.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public void onRefresh() {
        getDriverTrips(trip_type);

    }

   /* @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        LatLng loc2 = new LatLng(23.0262, 72.5242);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc2));

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
        googleMap.addMarker(new MarkerOptions().position(loc2));
    }*/
}
