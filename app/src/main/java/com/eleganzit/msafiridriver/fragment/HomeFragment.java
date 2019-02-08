package com.eleganzit.msafiridriver.fragment;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganzit.msafiridriver.PersonalInfoActivity;
import com.eleganzit.msafiridriver.R;
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
import java.util.ArrayList;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private ShimmerFrameLayout shimmerFrameLayout;
    Animation pop_anim;
    RecyclerView upcoming;
    LinearLayout current, past;
    TextView active;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView no_trips;
    ArrayList<TripData> arrayList;
    LinearLayout lcontent;
    TextView trip_title;
    CardView card,upcoming_card,past_card;
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
        trip_title = v.findViewById(R.id.trip_title);
        card = v.findViewById(R.id.card);
        upcoming_card = v.findViewById(R.id.upcoming_card);
        past_card = v.findViewById(R.id.past_card);
        upcoming = v.findViewById(R.id.upcoming);
        lcontent = v.findViewById(R.id.lcontent);
        no_trips = v.findViewById(R.id.no_trips);
        current = v.findViewById(R.id.current);
        active = v.findViewById(R.id.active);
        past = v.findViewById(R.id.past);
        pop_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_anim);


        Log.d("whereeeee","oncreateview");


        return v;

    }

    @Override
    public void onResume() {
        super.onResume();

        upcoming.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInLeft)
                .duration(300)
                .repeat(0)
                .playOn(shimmerFrameLayout);

        shimmerFrameLayout.startShimmer();
        current.setBackgroundResource(R.drawable.tab_left_light_bg);
        past.setBackgroundResource(R.drawable.tab_right_dark_bg);
        no_trips.setVisibility(View.GONE);
        upcoming.setVisibility(View.GONE);
        trip_title.setText("Upcoming Trips");
        YoYo.with(Techniques.SlideInLeft)
                .duration(300)
                .repeat(0)
                .playOn(shimmerFrameLayout);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        card.setCardElevation(0);
        card.setMaxCardElevation(0);
        shimmerFrameLayout.startShimmer();
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
    }

    public void getDriverTrips(final String trip_type)
    {

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

                                TripData tripData=new TripData(trip_type,id,from_title,from_lat,from_lng,from_address,to_title,to_lat,to_lng,to_address,pickup_time,destination_time,statuss,trip_price);
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

            }

            @Override
            public void failure(RetrofitError error) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("errorrrr",""+error.getMessage());

            }
        });
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
