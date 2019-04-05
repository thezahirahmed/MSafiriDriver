package com.eleganzit.msafiridriver;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganzit.msafiridriver.activity.Home;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class SignInActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    TextView forgot,registertxt,signinwith;
    Button signbtn,facebookbtn,googlebtn;
    ImageView logo;
    RelativeLayout main;
    Animation flyin1,flyout1,flyout2,flyout3,flyout4,flyout5,flyout6,flyout7;
    EditText email,password;
    LinearLayout progressBar;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String devicetoken;

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_in);
        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();

        //Toast.makeText(this, ""+isOnline(this), Toast.LENGTH_SHORT).show();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        Log.d("preffff",pref.getString("status","")+"   tt");
        if(pref.getString("status","").equalsIgnoreCase("loggedin"))
        {
            if(pref.getString("approvel","").equalsIgnoreCase("yes"))
            {
                startActivity(new Intent(SignInActivity.this,NavHomeActivity.class));

                finish();
            }

        }
        main=findViewById(R.id.lmain);

        forgot=findViewById(R.id.forgot);
        email=findViewById(R.id.edemail);
        signbtn=findViewById(R.id.signbtn);
        password=findViewById(R.id.edpassword);
        signinwith=findViewById(R.id.signinwith);
        googlebtn=findViewById(R.id.googlebtn);
        facebookbtn=findViewById(R.id.facebookbtn);
        progressBar = findViewById(R.id.progressBar);


        flyin1 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyin1);

        flyout1 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyout1);
        flyout2 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyout2);
        flyout3 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyout3);
        flyout4 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyout4);
        flyout5 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyout5);
        flyout6 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyout6);
        flyout7 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyout7);

        if(getIntent().getStringExtra("from").equalsIgnoreCase("this"))
        {
            GFMinimalNotification mCurrentNotification = GFMinimalNotification.make(main, "Wrong email or password", GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR);
            mCurrentNotification.setDirection(GFMinimalNotification.DIRECTION_TOP);
            mCurrentNotification.setHelperImage(R.drawable.group_40);
            mCurrentNotification.show();
        }

        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                String Token= FirebaseInstanceId.getInstance().getToken();
                if (Token!=null)
                {
                    Log.d("Rrrrrmytokenn", Token);

                    devicetoken=Token;
                    StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().build();
                    StrictMode.setThreadPolicy(threadPolicy);
                    try {
                        JSONObject jsonObject=new JSONObject(Token);
                        Log.d("mytoken", jsonObject.getString("token"));
                        //devicetoken=jsonObject.getString("token");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //getLoginBoth(Token);

                }
                else
                {
                    Looper.prepare();
                    Log.d("No token","No token");
                    //Toast.makeText(SignInActivity.this, "No token", Toast.LENGTH_SHORT).show();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });t.start();

        signbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isValideEmail(email.getText().toString()) && password.getText().toString().trim().isEmpty()) {
                    YoYo.with(Techniques.Shake)
                            .duration(750)
                            .repeat(0)
                            .playOn(email);
                    YoYo.with(Techniques.Shake)
                            .duration(750)
                            .repeat(0)
                            .playOn(password);
                    email.setError("Please enter valid Email");
                    email.requestFocus();
                    password.setError("Please enter your Password");


                } else if (!isValideEmail(email.getText().toString())) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(email);
                    email.setError("Please enter valid Email");
                    email.requestFocus();

                } else if (password.getText().toString().trim().isEmpty()) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(password);
                    password.setError("Please enter your Password");
                    password.requestFocus();
                } else {
                    if (isOnline(SignInActivity.this)) {
                        email.startAnimation(flyout2);
                        password.startAnimation(flyout3);
                        signbtn.startAnimation(flyout4);
                        forgot.startAnimation(flyout5);
                        signinwith.startAnimation(flyout6);
                        facebookbtn.startAnimation(flyout7);
                        googlebtn.startAnimation(flyout7);
                        registertxt.startAnimation(flyout7);


                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.startAnimation(flyin1);

                        flyout3.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                YoYo.with(Techniques.Bounce)
                                        .duration(700)
                                        .repeat(8)
                                        .playOn(logo);
                            /*progress.startAnimation(flyin1);
                            progress.setVisibility(View.VISIBLE);
                            animationDrawable.start();*/

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                if (isOnline(SignInActivity.this)) {
                                    if (devicetoken == null) {
                                        Thread t = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String Token = FirebaseInstanceId.getInstance().getToken();
                                                if (Token != null) {
                                                    Log.d("Rrrrrmytokenn", Token);

                                                    devicetoken = Token;
                                                    StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().build();
                                                    StrictMode.setThreadPolicy(threadPolicy);
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(Token);
                                                        Log.d("mytoken", jsonObject.getString("token"));
                                                        //devicetoken=jsonObject.getString("token");

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    //getLoginBoth(Token);

                                                } else {
                                                    Looper.prepare();
                                                    Log.d("No token", "No token");
                                                    //Toast.makeText(SignInActivity.this, "No token", Toast.LENGTH_SHORT).show();
                                                }
                                                try {
                                                    loginDriver();
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        t.start();


                                    } else {
                                        loginDriver();
                                    }

                                } else {
                                    Toast.makeText(SignInActivity.this, "Please check you Internet connection", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(SignInActivity.this, "Please check you Internet connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        registertxt=findViewById(R.id.registertxt);
        logo = findViewById(R.id.logo);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,ForgotPasswordActivity.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
        registertxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,RegisterationActivity.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

            }
        });

       /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                TextView textView = new TextView(getApplicationContext());
                builder.setTitle("Alow M-Safari to access your location");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton("Allow", null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        }*/


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {

                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }
    public void loginDriver()
    {

        Log.d("devicetoken",""+devicetoken);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.loginDriver(email.getText().toString(), password.getText().toString(),devicetoken, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                logo.startAnimation(flyout1);

                progressBar.setVisibility(View.GONE);
                progressBar.startAnimation(flyout2);
                final StringBuilder stringBuilder = new StringBuilder();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("llllllstringBuilder", "" + stringBuilder);
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

                                String id = jsonObject1.getString("driver_id");
                                editor.putString("driver_id", id);
                                editor.putString("status", "loggedin");
                                String fullname = jsonObject1.getString("fullname");
                                editor.putString("fullname", fullname);
                                //String vehicle_name = jsonObject1.getString("vehicle_name");
                                //editor.putString("vehicle_name", vehicle_name);
                                String email = jsonObject1.getString("email");
                                editor.putString("email", email);
                                String type = jsonObject1.getString("type");
                                editor.putString("type", type);
                                //String photo = jsonObject1.getString("photo");
                                //String vehicle_profile = jsonObject1.getString("vehicle_profile");
                                //editor.putString("photo", photo);
                                //editor.putString("vehicle_profile", vehicle_profile);
                                editor.putString("password", password.getText().toString());
                                String dstatus = jsonObject1.getString("status");
                                editor.putString("dstatus", dstatus);
                                String approvel = jsonObject1.getString("approvel");
                                editor.putString("approvel", approvel);
                                editor.commit();

                                if(approvel.equalsIgnoreCase("yes"))
                                {

                                    startActivity(new Intent(SignInActivity.this,NavHomeActivity.class));

                                    finish();
                                }
                                else
                                {

                                    startActivity(new Intent(SignInActivity.this,ProfileActivity.class).putExtra("from","signin"));

                                    finish();
                                }
                            }

                        }
                        else
                        {
                            logo.startAnimation(flyout1);
                                        /*progress.startAnimation(flyout1);
                                        animationDrawable.stop();*/
                            progressBar.setVisibility(View.GONE);
                            progressBar.startAnimation(flyout2);
                            Intent i = new Intent(SignInActivity.this, SignInActivity.class).putExtra("from","this");
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            finish();
                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                       /* if (jsonObject.getString("status").equalsIgnoreCase("0")) {
                            Toast.makeText(SignInActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            GFMinimalNotification mCurrentNotification = GFMinimalNotification.make(main, jsonObject.getString("message"), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR);
                            mCurrentNotification.setDirection(GFMinimalNotification.DIRECTION_TOP);
                            mCurrentNotification.setHelperImage(R.drawable.group_40);
                            mCurrentNotification.show();
                        }
                        if (jsonObject.getString("status").equalsIgnoreCase("2")) {
                            Toast.makeText(SignInActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            GFMinimalNotification mCurrentNotification = GFMinimalNotification.make(main, jsonObject.getString("message"), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR);
                            mCurrentNotification.setDirection(GFMinimalNotification.DIRECTION_TOP);
                            mCurrentNotification.setHelperImage(R.drawable.group_40);
                            mCurrentNotification.show();
                        }*/
                    }
                    else
                    {

                        Toast.makeText(SignInActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {
                    Toast.makeText(SignInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void failure(RetrofitError error) {
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(SignInActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                logo.startAnimation(flyout1);
                                        /*progress.startAnimation(flyout1);
                                        animationDrawable.stop();*/
                progressBar.setVisibility(View.GONE);
                progressBar.startAnimation(flyout2);
                Intent i = new Intent(SignInActivity.this, SignInActivity.class).putExtra("from","this");
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Animation flyin1 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyin1);
        final Animation flyin2 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyin2);
        final Animation flyin3 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyin3);
        final Animation flyin4 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyin4);
        final Animation flyin5 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyin5);
        final Animation flyin6 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyin6);
        final Animation flyin7 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.flyin7);

        logo.startAnimation(flyin1);
        email.startAnimation(flyin2);
        password.startAnimation(flyin3);
        signbtn.startAnimation(flyin4);
        forgot.startAnimation(flyin5);
        signinwith.startAnimation(flyin6);
        facebookbtn.startAnimation(flyin7);
        googlebtn.startAnimation(flyin7);
        registertxt.startAnimation(flyin7);


    }
    public boolean isValideEmail(String Email) {

        String Email_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(Email_PATTERN);
        Matcher matcher = pattern.matcher(Email);
        return matcher.matches();
    }
}
