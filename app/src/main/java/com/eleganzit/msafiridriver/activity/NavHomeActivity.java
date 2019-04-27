package com.eleganzit.msafiridriver.activity;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.eleganzit.msafiridriver.HomeProfileActivity;
import com.eleganzit.msafiridriver.PersonalInfoActivity;
import com.eleganzit.msafiridriver.PickupLocation;
import com.eleganzit.msafiridriver.ProfileActivity;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.SignInActivity;
import com.eleganzit.msafiridriver.SplashActivity;
import com.eleganzit.msafiridriver.fragment.AccountFragment;
import com.eleganzit.msafiridriver.fragment.HistoryFragment;
import com.eleganzit.msafiridriver.fragment.HomeFragment;
import com.eleganzit.msafiridriver.fragment.TripFragment;
import com.eleganzit.msafiridriver.lib.RobotoMediumTextView;
import com.eleganzit.msafiridriver.utils.MovableFloatingActionButton;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.eleganzit.msafiridriver.utils.SensorService;
import com.infideap.drawerbehavior.AdvanceDrawerLayout;
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
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

public class NavHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RobotoMediumTextView user_name;
    public static CircleImageView profile_image;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public static TextView home_title, active;
    private String photo, name;
    Animation pop_anim, pop_in, pop_out;
    public static ImageView fab;
    AdvanceDrawerLayout drawer;
    private String status = "deactive";
    public static ProgressBar toolbar_progress;
    RelativeLayout prog_rel;
    ProgressDialog progressDialog;
    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(NavHomeActivity.this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        setContentView(R.layout.activity_main2);

        home_title = findViewById(R.id.home_title);
        SharedPreferences p_pref = getSharedPreferences("passenger_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor p_editor = p_pref.edit();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait");
        Log.d("frommmmm", getIntent().getStringExtra("from") + "   " + getIntent().getStringExtra("title"));

        if (getIntent().getStringExtra("from").equalsIgnoreCase("notification")) {

            if (getIntent().getStringExtra("type").equalsIgnoreCase("go_online")) {
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.go_online_dialog);
                final TextView title, go_online;
                final ProgressBar btn_progress;

                title = dialog.findViewById(R.id.message);
                go_online = dialog.findViewById(R.id.go_online);
                btn_progress = dialog.findViewById(R.id.btn_progress);

                title.setText(getIntent().getStringExtra("content"));

                go_online.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        status = "active";
                        updateDriverstatus();
                        dialog.dismiss();
                        getIntent().removeExtra("from");
                        getIntent().removeExtra("type");
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.show();

            }

        }


        Log.d("trip_status", "home activity   " + p_pref.getString("trip_status", "") + "");
        active = findViewById(R.id.active);
        prog_rel = findViewById(R.id.prog_rel);
        pop_anim = AnimationUtils.loadAnimation(this, R.anim.pop_anim);
        pop_in = AnimationUtils.loadAnimation(this, R.anim.pop_in);
        pop_out = AnimationUtils.loadAnimation(this, R.anim.pop_out);
        active.startAnimation(pop_anim);
        ViewTreeObserver vto = active.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    active.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    active.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width = active.getMeasuredWidth() + 10;
                int height = active.getMeasuredHeight();
                Log.d("wwwwwhhhhh", "" + width + " - " + convertPixelsToDp(width, NavHomeActivity.this) + "     " + height + " - " + convertPixelsToDp(height, NavHomeActivity.this));
                prog_rel.getLayoutParams().width = width;
                prog_rel.getLayoutParams().height = height;
                prog_rel.requestLayout();
            }
        });
        fab = findViewById(R.id.fab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar_progress = findViewById(R.id.toolbar_progress);
        Drawable progressDrawable = toolbar_progress.getIndeterminateDrawable().mutate();
        progressDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        toolbar_progress.setProgressDrawable(progressDrawable);
        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor = pref.edit();

        Log.d("preffff", name + "");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NavHomeActivity.this, PickupLocation.class).putExtra("from", "add"));
                Bungee.slideLeft(NavHomeActivity.this);

            }
        });
        Log.d("statusssss", "" + pref.getString("dstatus", ""));
        if (pref.getString("dstatus", "").equalsIgnoreCase("active")) {
            active.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green_dot, 0, 0, 0);
            active.setText("Go Offline");
            status = "active";
        } else {
            active.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_dot, 0, 0, 0);
            active.setText("Go Online");
            status = "deactive";
        }

        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                active.startAnimation(pop_in);
                if (active.getText().toString().equalsIgnoreCase("Go Online")) {

                    status = "active";
                    updateDriverstatus();

                } else if (active.getText().toString().equalsIgnoreCase("Go Offline")) {
                    status = "deactive";
                    updateDriverstatus();

                }
            }

        });

/*
        toolbar.bringToFront();
*/
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_ham, getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavHomeActivity.this, HomeProfileActivity.class).putExtra("from", "home");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(NavHomeActivity.this,
                                profile_image,
                                ViewCompat.getTransitionName(profile_image));
                startActivity(intent, options.toBundle());


            }
        });

        profile_image = headerview.findViewById(R.id.profile_image);


        user_name = headerview.findViewById(R.id.user_name);

        HomeFragment homeFrag = new HomeFragment();
        getSupportFragmentManager().beginTransaction()//.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.container, homeFrag, "TAG")
                .commit();
        navigationView.setNavigationItemSelectedListener(this);
        drawer.setViewScale(Gravity.START, 0.9f);
        drawer.setRadius(Gravity.START, 15);
        drawer.setViewElevation(Gravity.START, 20);

    }

    public void updateDriverstatus() {
        active.setVisibility(View.GONE);
        toolbar_progress.setVisibility(View.VISIBLE);

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.updateDriverstatus(pref.getString("driver_id", ""), status, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                toolbar_progress.setVisibility(View.GONE);
                active.setVisibility(View.VISIBLE);
                active.startAnimation(pop_out);
                if (status.equalsIgnoreCase("active")) {
                    active.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green_dot, 0, 0, 0);

                    active.setText("Go Offline");

                } else {
                    active.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_dot, 0, 0, 0);

                    active.setText("Go Online");
                }
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

                                String dstatus = jsonObject1.getString("status");
                                //Toast.makeText(NavHomeActivity.this, ""+dstatus, Toast.LENGTH_SHORT).show();
                                editor.putString("dstatus", dstatus);

                                editor.commit();
                            }

                        } else {

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(NavHomeActivity.this, "" + stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                active.setVisibility(View.VISIBLE);
                active.startAnimation(pop_out);

                toolbar_progress.setVisibility(View.GONE);
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(NavHomeActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void logoutOfflineDriver() {
        active.setVisibility(View.GONE);
        toolbar_progress.setVisibility(View.VISIBLE);
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.updateDriverstatus(pref.getString("driver_id", ""), status, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                toolbar_progress.setVisibility(View.GONE);
                active.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
                active.startAnimation(pop_out);
                if (status.equalsIgnoreCase("active")) {
                    active.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green_dot, 0, 0, 0);

                    active.setText("Go Offline");

                } else {
                    active.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_dot, 0, 0, 0);

                    active.setText("Go Online");
                }
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

                                String dstatus = jsonObject1.getString("status");
                                //Toast.makeText(NavHomeActivity.this, ""+dstatus, Toast.LENGTH_SHORT).show();

                            }

                            editor.clear();
                            editor.commit();
                            editor.apply();
                            SharedPreferences p_pref=getSharedPreferences("passenger_pref", Context.MODE_PRIVATE);
                            SharedPreferences.Editor p_editor=p_pref.edit();
                            SharedPreferences pref=getSharedPreferences("location_pref",MODE_PRIVATE);
                            SharedPreferences.Editor editor=pref.edit();

                            p_editor.clear();
                            p_editor.commit();
                            p_editor.apply();

                            editor.clear();
                            editor.commit();
                            editor.apply();

                            Intent intent = new Intent(NavHomeActivity.this, SignInActivity.class).putExtra("from","account");
                            startActivity(intent);
                            Bungee.slideLeft(NavHomeActivity.this);


                            finish();
                            Bungee.slideRight(NavHomeActivity.this);



                        } else {

                            Toast.makeText(NavHomeActivity.this, "Please try again", Toast.LENGTH_SHORT).show();

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(NavHomeActivity.this, "" + stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                active.setVisibility(View.VISIBLE);
                active.startAnimation(pop_out);
                progressDialog.dismiss();
                toolbar_progress.setVisibility(View.GONE);
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(NavHomeActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void getDriverdata() {
       /* active.setVisibility(View.GONE);
        toolbar_progress.setVisibility(View.VISIBLE);
*/
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getDriverdata(pref.getString("driver_id", ""), new retrofit.Callback<retrofit.client.Response>() {
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
                        JSONArray jsonArray = null;
                        String dstatus = "";
                        if (status.equalsIgnoreCase("1")) {
                            jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                String fullname = jsonObject1.getString("fullname");
                                editor.putString("fullname", fullname);
                                String email = jsonObject1.getString("email");
                                editor.putString("email", email);
                                String driver_id = jsonObject1.getString("driver_id");
                                editor.putString("driver_id", driver_id);
                                photo = jsonObject1.getString("photo");
                                editor.putString("photo", photo);
                                String vehicle_profile = jsonObject1.getString("vehicle_profile");
                                editor.putString("vehicle_profile", vehicle_profile);
                                String vehicle_name = jsonObject1.getString("vehicle_name");
                                editor.putString("vehicle_name", vehicle_name);
                                dstatus = jsonObject1.getString("online_status");
                                editor.putString("dstatus", dstatus);

                                editor.commit();

                            }
                            /*toolbar_progress.setVisibility(View.GONE);
                            active.setVisibility(View.VISIBLE);
                            active.startAnimation(pop_out);
                            if(dstatus.equalsIgnoreCase("active"))
                            {
                                active.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green_dot, 0, 0, 0);

                                active.setText("Go Offline");

                            }
                            else
                            {
                                active.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_dot, 0, 0, 0);

                                active.setText("Go Online");
                            }*/
                            Glide
                                    .with(getApplicationContext())
                                    .load(photo)
                                    .apply(new RequestOptions().placeholder(R.drawable.pr).centerCrop().circleCrop())
                                    .into(profile_image);
                        } else {

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(NavHomeActivity.this, "" + stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(NavHomeActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDriverdata();
        photo = pref.getString("photo", "");
        name = pref.getString("fullname", "");
        Log.d("idddddddd", pref.getString("driver_id", "") + "");
        user_name.setText("" + name);

        Glide
                .with(this)
                .load(photo).apply(new RequestOptions().placeholder(R.drawable.pr))
                .into(profile_image);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Handle the camera action
            HomeFragment homeFrag = new HomeFragment();
            getSupportFragmentManager().beginTransaction()//.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.container, homeFrag, "TAG")
                    .commit();
        } else if (id == R.id.nav_trip) {

            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {

                                TripFragment tripFrag = new TripFragment();
                                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                        .replace(R.id.container, tripFrag, "TAG")
                                        .addToBackStack(null)
                                        .commit();

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
                            Toast.makeText(NavHomeActivity.this, "Error occurred! ", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .onSameThread()
                    .check();


        } else if (id == R.id.nav_history) {
            HistoryFragment historyFrag = new HistoryFragment();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.container, historyFrag, "TAG")
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_account) {
            AccountFragment accountFrag = new AccountFragment();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.container, accountFrag, "TAG")
                    .addToBackStack(null)
                    .commit();
        } else if(id == R.id.nav_logout) {
            new android.app.AlertDialog.Builder(NavHomeActivity.this).setTitle("Logout").setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            status = "deactive";

                            logoutOfflineDriver();

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();

        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
}

