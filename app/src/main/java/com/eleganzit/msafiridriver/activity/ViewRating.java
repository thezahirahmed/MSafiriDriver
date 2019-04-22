package com.eleganzit.msafiridriver.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eleganzit.msafiridriver.DocumentsActivity;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.eleganzit.msafiridriver.utils.RobotoMediumTextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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
import java.util.Date;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

public class ViewRating extends AppCompatActivity {


    ImageView profile_pic;
    RobotoMediumTextView txtusername;
    RatingBar ratingBar;
    TextView txtreview,no_comment;
    private String trip_id,user_id,photo,username,review,ratings;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rating);
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        profile_pic=findViewById(R.id.profile_pic);
        txtusername=findViewById(R.id.username);
        ratingBar=findViewById(R.id.ratingBar);
        txtreview=findViewById(R.id.review);
        progress=findViewById(R.id.progress);
        no_comment=findViewById(R.id.no_comment);

        trip_id=getIntent().getStringExtra("trip_id");
        user_id=getIntent().getStringExtra("user_id");
        photo=getIntent().getStringExtra("photo");
        username=getIntent().getStringExtra("username");
        review=getIntent().getStringExtra("review");
        ratings=getIntent().getStringExtra("ratings");

        Log.d("dataaeee",photo+"  "+username+"   "+review+"   "+ratings);

        Glide
                .with(this)
                .load(photo)
                .apply(new RequestOptions().placeholder(R.drawable.pr).centerCrop().circleCrop())
                .into(profile_pic);

        txtusername.setText(username);

        ratingBar.setRating(Float.parseFloat(ratings+""));

        getReviews();

    }

    public void getReviews()
    {
        progress.setVisibility(View.VISIBLE);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getReview(trip_id,user_id, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {

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

                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                String comments = jsonObject1.getString("comments");
                                if(comments.equalsIgnoreCase(""))
                                {
                                    no_comment.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    no_comment.setVisibility(View.GONE);
                                    txtreview.setText(comments);
                                }

                            }

                        }
                        else
                        {

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(ViewRating.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ViewRating.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(ViewRating.this);
    }
}
