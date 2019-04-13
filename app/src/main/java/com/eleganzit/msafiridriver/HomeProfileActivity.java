package com.eleganzit.msafiridriver;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.hzn.lib.EasyTransition;
import com.hzn.lib.EasyTransitionOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

public class HomeProfileActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView name, txtsubmit;
    FrameLayout add_photo;
    ImageView profile_pic, logout;
    LinearLayout submit;
    TableRow personal_info, bank, docs, vehicle_detail;
    private String photo = "";
    public static HomeProfileActivity profileActivity;
    RelativeLayout profile;
    ProgressDialog progressDialog;
    private String country_id = "";
    private String profile_status, docs_status, vechicle_status, bank_status, approvel;
    CircularProgressDrawable circularProgressDrawable;
    String from;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        from = getIntent().getStringExtra("from");

        profileActivity = this;
        profile_pic = findViewById(R.id.profile_pic);
        profile = findViewById(R.id.profile);

        logout = findViewById(R.id.logout);
        name = findViewById(R.id.name);
        submit = findViewById(R.id.submit);
        txtsubmit = findViewById(R.id.txtsubmit);
        personal_info = findViewById(R.id.personal_info);
        bank = findViewById(R.id.bank);
        docs = findViewById(R.id.docs);
        vehicle_detail = findViewById(R.id.vehicle_detail);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(4f);
        circularProgressDrawable.setCenterRadius(15f);
        circularProgressDrawable.start();


        final EasyTransitionOptions options =
                EasyTransitionOptions.makeTransitionOptions(
                        HomeProfileActivity.this,
                        profile);
        long transitionDuration = 600;
        EasyTransition.enter(
                this,
                transitionDuration,
                new DecelerateInterpolator(),
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // init other views after transition anim

                    }
                });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("apprdataaaa", "" + profile_status + "    " + docs_status + "    " + vechicle_status + "   " + bank_status);

                if (txtsubmit.getText().toString().equalsIgnoreCase("Submit for Approval")) {
                    /*if(photo.equalsIgnoreCase("") || photo.equalsIgnoreCase("http://itechgaints.com/M-safiri-API/user_uploads/no_profile.png"))
                    {
                        Toast.makeText(HomeProfileActivity.this, "Please select profile picture", Toast.LENGTH_SHORT).show();
                    }
                    else if(profile_status.equalsIgnoreCase("0"))
                    {
                        Toast.makeText(HomeProfileActivity.this, "Please fill up personal information", Toast.LENGTH_SHORT).show();
                    }
                    else if(docs_status.equalsIgnoreCase("0"))
                    {
                        Toast.makeText(HomeProfileActivity.this, "Please upload documents", Toast.LENGTH_SHORT).show();
                    }
                    else if(pref.getString("type","").equalsIgnoreCase("individual"))
                    {
                        if(vechicle_status.equalsIgnoreCase("0"))
                        {
                            Toast.makeText(HomeProfileActivity.this, "Please fill up vehicle details", Toast.LENGTH_SHORT).show();
                        }
                        else if(bank_status.equalsIgnoreCase("0"))
                        {
                            Toast.makeText(HomeProfileActivity.this, "Please fill up bank details", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if (profile_status.equalsIgnoreCase("1") && docs_status.equalsIgnoreCase("1") && bank_status.equalsIgnoreCase("1") && vechicle_status.equalsIgnoreCase("1"))
                    {*/
                    //call api
                    updateApprovalStatus();
                    /*}*/
                } else if (txtsubmit.getText().toString().equalsIgnoreCase("Waiting for Approval")) {

                } else {
                    onBackPressed();
                }

            }
        });
        personal_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeProfileActivity.this, PersonalInfoActivity.class));
                Bungee.slideLeft(HomeProfileActivity.this);

            }
        });
        bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeProfileActivity.this, BankAccountActivity.class));
                Bungee.slideLeft(HomeProfileActivity.this);

            }
        });
        docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeProfileActivity.this, DocumentsActivity.class));
                Bungee.slideLeft(HomeProfileActivity.this);


            }
        });
        vehicle_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeProfileActivity.this, VehicleDetailsActivity.class));
                Bungee.slideLeft(HomeProfileActivity.this);

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyTransition.startActivity(new Intent(HomeProfileActivity.this, ChoosePictureActivity.class), options);
                //startActivity(new Intent(ProfileActivity.this,ChoosePictureActivity.class),options.toBundle());
                //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

    }


    public void updateApprovalStatus() {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.updateApprovalStatus(pref.getString("driver_id", ""), "no",
                new retrofit.Callback<retrofit.client.Response>() {
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
                            Log.d("stringBuilder", "" + stringBuilder);
                            //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                            if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                                JSONObject jsonObject = new JSONObject("" + stringBuilder);
                                String status = jsonObject.getString("status");
                                JSONArray jsonArray = null;
                                if (status.equalsIgnoreCase("1")) {

                                    jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                        approvel = jsonObject1.getString("approvel");
                                        editor.putString("approvel", approvel);
                                        editor.commit();

                                        if (approvel.equalsIgnoreCase("yes")) {
                                            startActivity(new Intent(HomeProfileActivity.this, NavHomeActivity.class).putExtra("from","home"));
                                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_in_left);
                                            finish();
                                        } else {
                                            Toast.makeText(HomeProfileActivity.this, "Profile has been submitted for approval", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                } else {

                                }

                                // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(HomeProfileActivity.this, "" + stringBuilder, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(HomeProfileActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void getPersonalInfo() {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getPersonalInfo(pref.getString("driver_id", ""), new retrofit.Callback<retrofit.client.Response>() {
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
                    Log.d("dddddddstringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        JSONArray jsonArray = null;
                        if (status.equalsIgnoreCase("1")) {
                            jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                /*String fullname = jsonObject1.getString("fullname");
                                editor.putString("fullname", fullname);
                                String email = jsonObject1.getString("email");
                                editor.putString("email", email);*/
                                photo = jsonObject1.getString("photo");
                                editor.putString("photo", photo);
                                String street = jsonObject1.getString("street");
                                editor.putString("street", street);
                                String city = jsonObject1.getString("city");
                                editor.putString("city", city);
                                String state = jsonObject1.getString("state");
                                editor.putString("state", state);
                                String postal_code = jsonObject1.getString("postal_code");
                                editor.putString("postal_code", postal_code);
                                String country = jsonObject1.getString("country");
                                editor.putString("country", country);
                                String mobile_number = jsonObject1.getString("mobile_number");
                                editor.putString("mobile_number", mobile_number);
                                String dob = jsonObject1.getString("dob");
                                editor.putString("dob", dob);
                                String gender = jsonObject1.getString("gender");
                                editor.putString("gender", gender);
                                editor.commit();
                            }
                            Log.d("photooooooo", "photo " + photo);
                            Glide
                                    .with(HomeProfileActivity.this)
                                    .load(photo)
                                    .apply(new RequestOptions().placeholder(R.drawable.pr).centerCrop().circleCrop()).into(profile_pic);
                        } else {

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(HomeProfileActivity.this, "" + stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(HomeProfileActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void getApprovalStatus() {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getApprovalStatus(pref.getString("email", ""), new retrofit.Callback<retrofit.client.Response>() {
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
                    Log.d("dddddddstringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        JSONArray jsonArray = null;
                        if (status.equalsIgnoreCase("1")) {
                            getPersonalInfo();
                            jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                String fullname = jsonObject1.getString("fullname");
                                editor.putString("fullname", fullname);
                                String email = jsonObject1.getString("email");
                                editor.putString("email", email);
                                approvel = jsonObject1.getString("approvel");
                                editor.putString("approvel", approvel);

                                editor.commit();
                            }
                            Log.d("photooooooo", "photo " + photo);
                            /*personalInfo=pref.getString("personalInfo","");
                            documentsInfo=pref.getString("documentsInfo","");
                            vehicleInfo=pref.getString("vehicleInfo","");
                            bankInfo=pref.getString("bankInfo","");
                            approvel=pref.getString("approvel","");*/

                            if (approvel.equalsIgnoreCase("0")) {
                                Log.d("whereis", "appr no");

                                txtsubmit.setText("Submit for Approval");
                            } else if (approvel.equalsIgnoreCase("no")) {
                                Log.d("whereis", "appr no");

                                Log.d("whereis", "docs");
                                txtsubmit.setText("Waiting for Approval");

                            } else {
                                Log.d("whereis", "docs");
                                txtsubmit.setText("Go back");
                            }

                        } else {
                            Toast.makeText(HomeProfileActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                            submit.setEnabled(false);
                            submit.setClickable(false);
                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(HomeProfileActivity.this, "" + stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(HomeProfileActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor = pref.edit();

        if (pref.getString("type", "").equalsIgnoreCase("individual")) {
            vehicle_detail.setVisibility(View.VISIBLE);
            bank.setVisibility(View.VISIBLE);
        } else {
            vehicle_detail.setVisibility(View.GONE);
            bank.setVisibility(View.GONE);
        }

        getApprovalStatus();


        name.setText(pref.getString("fullname", ""));

    }

    @Override
    public void onBackPressed() {
        if (approvel != null) {
            if (approvel.equalsIgnoreCase("0") || approvel.equalsIgnoreCase("no")) {
                new AlertDialog.Builder(HomeProfileActivity.this).setTitle("Logout").setMessage("You will be logged out!")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.clear();
                                editor.commit();
                                editor.apply();
                                Intent intent = new Intent(HomeProfileActivity.this, SignInActivity.class).putExtra("from", "account");
                                startActivity(intent);
                                Bungee.slideLeft(HomeProfileActivity.this);

                                finish();
                                Bungee.slideRight(HomeProfileActivity.this);


                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
            else {
                super.onBackPressed();
                Bungee.slideRight(this);
            }
        } else {
            super.onBackPressed();
            Bungee.slideRight(this);
        }
    }
}
