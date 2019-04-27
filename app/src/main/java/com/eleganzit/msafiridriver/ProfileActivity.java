package com.eleganzit.msafiridriver;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.eleganzit.msafiridriver.activities_from_register.RegisterBankAccountActivity;
import com.eleganzit.msafiridriver.activities_from_register.RegisterChoosePictureActivity;
import com.eleganzit.msafiridriver.activities_from_register.RegisterDocumentsActivity;
import com.eleganzit.msafiridriver.activities_from_register.RegisterPersonalInfoActivity;
import com.eleganzit.msafiridriver.activities_from_register.RegisterVehicleDetailsActivity;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.hzn.lib.EasyTransition;
import com.hzn.lib.EasyTransitionOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

import static com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView name,txtsubmit;
    FrameLayout add_photo;
    ImageView profile_pic,logout;
    LinearLayout submit;
    TableRow personal_info,bank,docs,vehicle_detail;
    private String photo="";
    public static ProfileActivity profileActivity;
    RelativeLayout profile;
    ProgressDialog progressDialog;
    private String country_id="";
    private String profile_status,docs_status,vechicle_status,bank_status,approvel;
    CircularProgressDrawable circularProgressDrawable;
    String from;
    ImageView personal_info_status,docs_info_status,vehicle_info_status,bank_info_status;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        from=getIntent().getStringExtra("from");

        profileActivity=this;
        profile_pic=findViewById(R.id.profile_pic);
        profile=findViewById(R.id.profile);

        logout=findViewById(R.id.logout);
        name=findViewById(R.id.name);
        submit=findViewById(R.id.submit);
        txtsubmit=findViewById(R.id.txtsubmit);
        personal_info=findViewById(R.id.personal_info);
        personal_info_status=findViewById(R.id.personal_info_status);

        bank=findViewById(R.id.bank);
        bank_info_status=findViewById(R.id.bank_info_status);

        docs=findViewById(R.id.docs);
        docs_info_status=findViewById(R.id.docs_info_status);

        vehicle_detail=findViewById(R.id.vehicle_detail);
        vehicle_info_status=findViewById(R.id.vehicle_info_status);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(4f);
        circularProgressDrawable.setCenterRadius(15f);
        circularProgressDrawable.start();


        final EasyTransitionOptions options =
                EasyTransitionOptions.makeTransitionOptions(
                        ProfileActivity.this,
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


                Log.d("apprdataaaa",photo+"    "+profile_status+"    "+docs_status+"    "+vechicle_status+"   "+bank_status);

                if(txtsubmit.getText().toString().equalsIgnoreCase("Submit for Approval"))
                {
                    if(photo.equalsIgnoreCase("") || photo.equalsIgnoreCase("http://itechgaints.com/M-safiri-API/user_uploads/no_profile.png"))
                    {
                        Toast.makeText(ProfileActivity.this, "Please select profile picture", Toast.LENGTH_SHORT).show();
                    }
                    else if(profile_status.equalsIgnoreCase("0"))
                    {
                        Toast.makeText(ProfileActivity.this, "Please fill up personal information", Toast.LENGTH_SHORT).show();
                    }
                    else if(docs_status.equalsIgnoreCase("0"))
                    {
                        Toast.makeText(ProfileActivity.this, "Please upload documents", Toast.LENGTH_SHORT).show();
                    }
                    else if(pref.getString("type","").equalsIgnoreCase("individual"))
                    {
                        if(vechicle_status.equalsIgnoreCase("0"))
                        {
                            Toast.makeText(ProfileActivity.this, "Please fill up vehicle details", Toast.LENGTH_SHORT).show();
                        }
                        else if(bank_status.equalsIgnoreCase("0"))
                        {
                            Toast.makeText(ProfileActivity.this, "Please fill up bank details", Toast.LENGTH_SHORT).show();
                        }
                        else if (profile_status.equalsIgnoreCase("1") && docs_status.equalsIgnoreCase("1") && bank_status.equalsIgnoreCase("1") && vechicle_status.equalsIgnoreCase("1"))
                        {
                            //call api
                            updateApprovalStatus();
                        }
                    }
                    else if (profile_status.equalsIgnoreCase("1") && docs_status.equalsIgnoreCase("1"))
                    {
                        //call api
                        updateApprovalStatus();
                    }
                }
                else if(txtsubmit.getText().toString().equalsIgnoreCase("Waiting for Approval"))
                {

                }
                else
                {
                    startActivity(new Intent(ProfileActivity.this, NavHomeActivity.class).putExtra("from","profile"));
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_in_left);
                    finish();
                }

            }
        });

        personal_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pref.getString("approvel","").equalsIgnoreCase("no"))
                {
                    new AlertDialog.Builder(ProfileActivity.this).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setMessage("Please wait for approval or contact admin for further process.").show();
                }
                else {

                    startActivity(new Intent(ProfileActivity.this, RegisterPersonalInfoActivity.class));
                    Bungee.slideLeft(ProfileActivity.this);
                }

            }
        });

        bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pref.getString("approvel","").equalsIgnoreCase("no"))
                {
                    new AlertDialog.Builder(ProfileActivity.this).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setMessage("Please wait for approval or contact admin for further process.").show();
                }
                else {
                    startActivity(new Intent(ProfileActivity.this,RegisterBankAccountActivity.class));
                    Bungee.slideLeft(ProfileActivity.this);
                }

            }
        });

        docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pref.getString("approvel","").equalsIgnoreCase("no"))
                {
                    new AlertDialog.Builder(ProfileActivity.this).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setMessage("Please wait for approval or contact admin for further process.").show();
                }
                else {
                    startActivity(new Intent(ProfileActivity.this,RegisterDocumentsActivity.class));
                    Bungee.slideLeft(ProfileActivity.this);


                }

            }
        });

        vehicle_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pref.getString("approvel","").equalsIgnoreCase("no"))
                {
                    new AlertDialog.Builder(ProfileActivity.this).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setMessage("Please wait for approval or contact admin for further process.").show();
                }
                else {
                    startActivity(new Intent(ProfileActivity.this,RegisterVehicleDetailsActivity.class));
                    Bungee.slideLeft(ProfileActivity.this);

                }

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pref.getString("approvel","").equalsIgnoreCase("no"))
                {
                    new AlertDialog.Builder(ProfileActivity.this).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setMessage("Please wait for approval or contact admin for further process.").show();
                }
                else {
                    EasyTransition.startActivity(new Intent(ProfileActivity.this,RegisterChoosePictureActivity.class), options);

                }
                //startActivity(new Intent(ProfileActivity.this,ChoosePictureActivity.class),options.toBundle());
                //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

    }

    public void updateApprovalStatus()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.updateApprovalStatus(pref.getString("driver_id",""), "no",
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
                                if(status.equalsIgnoreCase("1"))
                                {

                                    jsonArray = jsonObject.getJSONArray("data");
                                    for(int i=0;i<jsonArray.length();i++)
                                    {
                                        JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                        approvel=jsonObject1.getString("approvel");
                                        editor.putString("approvel", approvel);
                                        editor.commit();

                                        if(approvel.equalsIgnoreCase("yes"))
                                        {
                                            startActivity(new Intent(ProfileActivity.this, NavHomeActivity.class).putExtra("from","profile"));
                                            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_in_left);
                                            finish();
                                        }
                                        else
                                        {
                                            txtsubmit.setText("Waiting for Approval");
                                            Toast.makeText(ProfileActivity.this, "Profile has been submitted for approval", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                                else
                                {
                                    Toast.makeText(ProfileActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                            }
                            else
                            {

                                Toast.makeText(ProfileActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProfileActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

                    }
                });
    }

    public void getPersonalInfo()
    {
        //progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getPersonalInfo(pref.getString("driver_id",""), new retrofit.Callback<retrofit.client.Response>() {
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
                        if(status.equalsIgnoreCase("1"))
                        {
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

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
                            Log.d("photooooooo","photo "+photo);
                            Glide
                                    .with(ProfileActivity.this)
                                    .asBitmap()
                                    .apply(new RequestOptions().override(200, 200).placeholder(R.drawable.pr).centerCrop().circleCrop().format(PREFER_ARGB_8888).diskCacheStrategy(DiskCacheStrategy.ALL))
                                    .load(photo)
                                    .thumbnail(.1f)
                                    .into(profile_pic);
                        }
                        else
                        {

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(ProfileActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {
                    Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(ProfileActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void getApprovalStatus()
    {
        Glide
                .with(ProfileActivity.this)
                .asBitmap()
                .apply(new RequestOptions().override(200, 200).placeholder(R.drawable.pr).centerCrop().circleCrop().format(PREFER_ARGB_8888).diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(pref.getString("photo",""))
                .thumbnail(.1f)
                .into(profile_pic);

        //progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getApprovalStatus(pref.getString("email",""), new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {

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
                        if(status.equalsIgnoreCase("1"))
                        {
                            getPersonalInfo();
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                String fullname = jsonObject1.getString("fullname");
                                editor.putString("fullname", fullname);
                                String email = jsonObject1.getString("email");
                                editor.putString("email", email);
                                approvel = jsonObject1.getString("approvel");
                                editor.putString("approvel", approvel);

                                editor.commit();
                            }
                            Log.d("photooooooo","photo "+photo);
                            /*personalInfo=pref.getString("personalInfo","");
                            documentsInfo=pref.getString("documentsInfo","");
                            vehicleInfo=pref.getString("vehicleInfo","");
                            bankInfo=pref.getString("bankInfo","");
                            approvel=pref.getString("approvel","");*/

                            if(approvel.equalsIgnoreCase("0") || profile_status.equalsIgnoreCase("0") || bank_status.equalsIgnoreCase("0") || docs_status.equalsIgnoreCase("0") || vechicle_status.equalsIgnoreCase("0"))
                            {
                                Log.d("whereis","appr no");

                                txtsubmit.setText("Submit for Approval");
                            }
                            else if(approvel.equalsIgnoreCase("no"))
                            {
                                Log.d("whereis","appr no");

                                    Log.d("whereis","docs");
                                    txtsubmit.setText("Waiting for Approval");

                            }
                            else
                            {
                                    Log.d("whereis","docs");
                                    txtsubmit.setText("Go to Home");
                            }

                        }
                        else
                        {
                            Toast.makeText(ProfileActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                            submit.setEnabled(false);
                            submit.setClickable(false);
                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(ProfileActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {
                    Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(ProfileActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void getEmptyFields()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getEmptyFields(pref.getString("driver_id",""), new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                personal_info_status.setVisibility(View.VISIBLE);
                bank_info_status.setVisibility(View.VISIBLE);
                docs_info_status.setVisibility(View.VISIBLE);
                vehicle_info_status.setVisibility(View.VISIBLE);

                getApprovalStatus();
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
                        if(status.equalsIgnoreCase("1"))
                        {
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                profile_status = jsonObject1.getString("profile_status");
                                editor.putString("profile_status", profile_status);
                                bank_status = jsonObject1.getString("bank_status");
                                editor.putString("bank_status", bank_status);
                                vechicle_status = jsonObject1.getString("vechicle_status");
                                editor.putString("vechicle_status", vechicle_status);
                                docs_status = jsonObject1.getString("docs_status");
                                editor.putString("docs_status", docs_status);

                                editor.commit();
                            }
                            Log.d("photooooooo","photo "+photo);

                            if(profile_status.equalsIgnoreCase("0"))
                            {
                                personal_info_status.setImageResource(R.mipmap.red_alert);
                            }
                            else
                            {
                                personal_info_status.setImageResource(R.mipmap.green_check);
                            }
                            if(docs_status.equalsIgnoreCase("0"))
                            {
                                docs_info_status.setImageResource(R.mipmap.red_alert);
                            }
                            else
                            {
                                docs_info_status.setImageResource(R.mipmap.green_check);
                            }
                            if(vechicle_status.equalsIgnoreCase("0"))
                            {
                                vehicle_info_status.setImageResource(R.mipmap.red_alert);
                            }
                            else
                            {
                                vehicle_info_status.setImageResource(R.mipmap.green_check);
                            }
                            if(bank_status.equalsIgnoreCase("0"))
                            {
                                bank_info_status.setImageResource(R.mipmap.red_alert);
                            }
                            else
                            {
                                bank_info_status.setImageResource(R.mipmap.green_check);
                            }

                        }
                        else
                        {
                            Toast.makeText(ProfileActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(ProfileActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {
                    Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(ProfileActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();

        Glide
                .with(ProfileActivity.this)
                .asBitmap()
                .apply(new RequestOptions().override(200, 200).placeholder(R.drawable.pr).centerCrop().circleCrop().format(PREFER_ARGB_8888).diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(pref.getString("photo",""))
                .thumbnail(.1f)
                .into(profile_pic);

        if(pref.getString("type","").equalsIgnoreCase("company"))
        {
            vehicle_detail.setVisibility(View.GONE);
            bank.setVisibility(View.GONE);
        }
        else
        {
            vehicle_detail.setVisibility(View.VISIBLE);
            bank.setVisibility(View.VISIBLE);
        }
        Dexter.withActivity(ProfileActivity.this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                            //getUpcomingTrips();

                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(ProfileActivity.this, "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();

        getEmptyFields();

        name.setText(pref.getString("fullname",""));

    }

    private void showSettingsDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onBackPressed() {

        if(from.equalsIgnoreCase("splash") || from.equalsIgnoreCase("signin") || from.equalsIgnoreCase("welcome"))
        {

                new AlertDialog.Builder(ProfileActivity.this).setTitle("Logout").setMessage("You will be logged out!")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.clear();
                                editor.commit();
                                editor.apply();
                                Intent intent = new Intent(ProfileActivity.this, SignInActivity.class).putExtra("from","account");
                                startActivity(intent);
                                Bungee.slideLeft(ProfileActivity.this);

                                finish();
                                Bungee.slideRight(ProfileActivity.this);


                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


        }
        else
        {
            super.onBackPressed();
            Bungee.slideRight(this);
        }
    }
}
