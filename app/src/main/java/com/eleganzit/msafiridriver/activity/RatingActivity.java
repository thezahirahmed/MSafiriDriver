package com.eleganzit.msafiridriver.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.eleganzit.msafiridriver.ProfileActivity;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.VehicleDetailsActivity;
import com.eleganzit.msafiridriver.adapter.UpcomingTripAdapter;
import com.eleganzit.msafiridriver.model.RatingsData;
import com.eleganzit.msafiridriver.model.VehicleData;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.eleganzit.msafiridriver.utils.RobotoMediumTextView;
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

public class RatingActivity extends Fragment {

    RecyclerView recyclerview;
    ArrayList<RatingsData> getTenantses=new ArrayList<>();
    private ShimmerFrameLayout shimmerFrameLayout;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ProgressBar progress_bar;
    RatingBar ratingBar;
    RobotoMediumTextView rating_stars,no_ratings;
    TextView lifetime,rated,fourstar;
    ImageView reload;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.activity_rating, container, false);
        recyclerview=v.findViewById(R.id.recyclerview);
        shimmerFrameLayout = (ShimmerFrameLayout) v.findViewById(R.id.shimmerLayout);
        pref = getActivity().getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();
        ratingBar=v.findViewById(R.id.ratingBar);
        rating_stars=v.findViewById(R.id.rating_stars);
        lifetime=v.findViewById(R.id.lifetime);
        rated=v.findViewById(R.id.rated);
        fourstar=v.findViewById(R.id.fourstar);
        no_ratings=v.findViewById(R.id.no_ratings);
        progress_bar=v.findViewById(R.id.progress_bar);
        reload = v.findViewById(R.id.reload);

        return v;
    }

    public void getratingDetails()
    {
        shimmerFrameLayout.startShimmer();
        reload.setVisibility(View.GONE);
        //progress_bar.setVisibility(View.VISIBLE);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        Log.d("lodfjhfdgsdf",pref.getString("driver_id","")+"");
        myInterface.getratingDetails(pref.getString("driver_id",""), new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                //progress_bar.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                reload.setVisibility(View.GONE);

                final StringBuilder stringBuilder = new StringBuilder();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("rrrrrstringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {
                            String avg_rating,lifitimeTrip,ratedtrip,sfourstar,status2,trip_id = null,user_id = null,rating = null,trip_status = null,datetime = null,fname = null,lname = null,photo = null;

                            JSONArray jsonArray2 = null;
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                avg_rating = jsonObject1.getString("avg_rating");
                                //ratingBar.setNumStars(Double.parseDouble(avg_rating));
                                ratingBar.setRating(Float.parseFloat(avg_rating));
                                lifitimeTrip = jsonObject1.getString("lifitimeTrip");
                                sfourstar = jsonObject1.getString("fourstar");
                                ratedtrip = jsonObject1.getString("ratedtrip");
                                rating_stars.setText(avg_rating+" stars");
                                lifetime.setText(lifitimeTrip);
                                rated.setText(ratedtrip);
                                fourstar.setText(sfourstar);
                                JSONObject ratedusers=jsonObject1.getJSONObject("ratedusers");
                                status2=ratedusers.getString("status");
                                if(status2.equalsIgnoreCase("1"))
                                {
                                    jsonArray2 = ratedusers.getJSONArray("datauser");
                                    for(int j=0;j<jsonArray2.length();j++)
                                    {
                                        JSONObject injsonObject=jsonArray2.getJSONObject(j);
                                        trip_id=injsonObject.getString("trip_id");
                                        user_id=injsonObject.getString("user_id");
                                        rating=injsonObject.getString("rating");
                                        trip_status=injsonObject.getString("status");
                                        datetime=injsonObject.getString("datetime");
                                        fname=injsonObject.getString("fname");
                                        lname=injsonObject.getString("lname");
                                        photo=injsonObject.getString("photo");

                                        Log.d("datanameeee",fname+"    "+lname);

                                        RatingsData ratingsData=new RatingsData(trip_id,user_id,rating,trip_status,datetime,fname,lname,photo,"");
                                        getTenantses.add(ratingsData);
                                    }

                                    recyclerview.setAdapter(new TenantsAdapter(getTenantses,getActivity()));
                                }
                                else
                                {
                                    no_ratings.setVisibility(View.VISIBLE);
                                }


                            }

                        }
                        else
                        {
                            no_ratings.setVisibility(View.VISIBLE);
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
               // progress_bar.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                reload.setVisibility(View.VISIBLE);

                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        recyclerview.setLayoutManager(layoutManager);
        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerview.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerview.addItemDecoration(dividerItemDecoration);*/
        getratingDetails();
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getratingDetails();
            }
        });

    }

    private class TenantsAdapter extends RecyclerView.Adapter<TenantsAdapter.MyViewHolder>
    {
        ArrayList<RatingsData> arrayList;
        Context context;

        public TenantsAdapter(ArrayList<RatingsData> arrayList, Context context)
        {
            this.arrayList = arrayList;
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(context).inflate(R.layout.ratingrowlayout,parent,false);

            MyViewHolder myViewHolder=new MyViewHolder(v);

            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            final RatingsData ratingsData=arrayList.get(position);
            Glide
                    .with(getActivity())
                    .load(ratingsData.getPhoto())
                    .apply(new RequestOptions().placeholder(R.drawable.pr).centerCrop().circleCrop())
                    .into(holder.user_photo);

            holder.username.setText(ratingsData.getFname()+" "+ratingsData.getLname());
           // holder.user_ratingbar.setNumStars(Integer.parseInt(ratingsData.getRating()));
            holder.user_ratingbar.setRating(Float.parseFloat(ratingsData.getRating()+""));
            holder.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        startActivity(new Intent(getActivity(),ViewRating.class)
                                .putExtra("trip_id",ratingsData.getTrip_id())
                                .putExtra("user_id",ratingsData.getUser_id())
                                .putExtra("photo",ratingsData.getPhoto())
                                .putExtra("username",ratingsData.getFname()+" "+ratingsData.getLname())
                                .putExtra("ratings",ratingsData.getRating())
                                .putExtra("review",ratingsData.getReview()));
                        Bungee.slideLeft(context);

                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public  class MyViewHolder extends RecyclerView.ViewHolder {

            LinearLayout main;
            ImageView user_photo;
            RobotoMediumTextView username;
            RatingBar user_ratingbar;
            public MyViewHolder(View itemView) {
                super(itemView);
                main=itemView.findViewById(R.id.main);
                user_photo=itemView.findViewById(R.id.user_photo);
                username=itemView.findViewById(R.id.username);
                user_ratingbar=itemView.findViewById(R.id.user_ratingBar);

            }
        }
    }
}
