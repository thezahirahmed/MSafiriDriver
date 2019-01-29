package com.eleganzit.msafiridriver.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.eleganzit.msafiridriver.activity.TripDetail;
import com.eleganzit.msafiridriver.adapter.TripAdapter;
import com.eleganzit.msafiridriver.adapter.UpcomingTripAdapter;
import com.eleganzit.msafiridriver.model.TripData;
import com.eleganzit.msafiridriver.subfragment.PayStatementFragment;
import com.eleganzit.msafiridriver.subfragment.TripHistoryFragment;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

import static android.content.Context.MODE_PRIVATE;


public class HistoryFragment extends Fragment{

    RecyclerView history;
    ArrayList<TripData> arrayList;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private ShimmerFrameLayout shimmerFrameLayout;
    private TextView no_history;
    int count_trip;
    TextView total_trip;
    RelativeLayout content;
    ProgressBar progressBar;
    public HistoryFragment() {
        // Required empty public constructor
    }



    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        history = v.findViewById(R.id.history);
        pref = getActivity().getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();
        shimmerFrameLayout = v.findViewById(R.id.shimmerLayout);
        no_history = v.findViewById(R.id.no_history);
        total_trip = v.findViewById(R.id.total_trip);
        progressBar = v.findViewById(R.id.progress);
        content = v.findViewById(R.id.content);

        NavHomeActivity.home_title.setText("Trip History");
        NavHomeActivity.active.setVisibility(View.GONE);
        NavHomeActivity.fab.setVisibility(View.GONE);

        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        history.setLayoutManager(layoutManager);

        getDriverTrips();
    }

    public void getDriverTrips()
    {
        content.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getDriverTrips(pref.getString("driver_id",""), "past", new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                arrayList = new ArrayList<>();
                content.setVisibility(View.VISIBLE);
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
                            no_history.setVisibility(View.GONE);
                            history.setVisibility(View.VISIBLE);

                                YoYo.with(Techniques.SlideInRight)
                                        .duration(300)
                                        .repeat(0)
                                        .playOn(history);

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
                                count_trip = jsonObject1.getInt("count_trip");

                                TripData tripData=new TripData("past",id,from_title,from_lat,from_lng,from_address,to_title,to_lat,to_lng,to_address,pickup_time,destination_time,statuss,trip_price);
                                arrayList.add(tripData);
                            }
                            history.setAdapter(new TripAdapter(arrayList,getActivity()));
                            total_trip.setText("You have made \n"+count_trip+" trips in the week.");
                        }
                        else
                        {

                            no_history.setVisibility(View.VISIBLE);

                                YoYo.with(Techniques.SlideInRight)
                                        .duration(300)
                                        .repeat(0)
                                        .playOn(no_history);

                            history.setVisibility(View.GONE);
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

    @Override
    public void onResume() {
        super.onResume();
        // Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();

    }

}
