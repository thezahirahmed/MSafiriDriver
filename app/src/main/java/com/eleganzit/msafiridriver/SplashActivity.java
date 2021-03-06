package com.eleganzit.msafiridriver;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.eleganzit.msafiridriver.utils.ServerFailureDialog;
import com.google.firebase.iid.FirebaseInstanceId;

import io.fabric.sdk.android.Fabric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

public class SplashActivity extends AppCompatActivity {
    ImageView logo;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Animation flyout1, flyin1;
    String devicetoken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Bungee.slideLeft(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

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
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();


        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor = pref.edit();
        flyin1 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.flyin1);
        flyout1 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.flyout1);

        logo = findViewById(R.id.logo);

        if (pref.getString("status", "").equalsIgnoreCase("loggedin")) {
            logo.startAnimation(flyin1);

            flyin1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Log.d("preffff",pref.getString("status","")+"   tt");

                    getDriverdata();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    logo.startAnimation(flyout1);

                    flyout1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            Intent i = new Intent(SplashActivity.this, SignInActivity.class).putExtra("from", "splash");

                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                            finish();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            }, 3200);
        }

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                logo.startAnimation(flyout1);

                flyout1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        Intent i = new Intent(SplashActivity.this, SignInActivity.class).putExtra("from","splash");

                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        },3200);*/

    }

    public void getDriverdata() {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.loginDriver(pref.getString("email", ""), pref.getString("password", ""), "android","", new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                final StringBuilder stringBuilder = new StringBuilder();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("dddddddstringBuilder", "sssss " + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        JSONArray jsonArray = null;
                        if (status.equalsIgnoreCase("1")) {
                            jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                String approvel = jsonObject1.getString("approvel");

                                if (approvel.equalsIgnoreCase("yes")) {

                                    loggedinDriver();
                                     /*
                                     Intent i = new Intent(SplashActivity.this, SignInActivity.class).putExtra("from","splash");

                                     startActivity(i);
                                     overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                                     finish();*/

                                } else {

                                    updateToken();

                                }
                                editor.putString("approvel", approvel);
                                editor.commit();

                            }
                        } else {
                            logo.startAnimation(flyout1);

                            flyout1.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {

                                    Intent i = new Intent(SplashActivity.this, SignInActivity.class).putExtra("from", "splash");

                                    startActivity(i);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                    finish();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            Toast.makeText(SplashActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    } else {

                        logo.startAnimation(flyout1);

                        flyout1.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                Intent i = new Intent(SplashActivity.this, SignInActivity.class).putExtra("from", "splash");

                                startActivity(i);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                finish();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        if (stringBuilder.toString().equalsIgnoreCase("null") || stringBuilder == null) {
                            Toast.makeText(SplashActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SplashActivity.this, "" + stringBuilder, Toast.LENGTH_SHORT).show();
                        }

                    }


                } catch (IOException e) {

                    Log.d("dddddddstringBuilder","IOException "+e.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("dddddddstringBuilder","JSONException "+e.getMessage());
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(SplashActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();
                //ServerFailureDialog.alertDialog(SplashActivity.this);
                logo.startAnimation(flyout1);
                if(pref.getString("status","").equalsIgnoreCase("loggedin"))
                {
                    if(pref.getString("approvel","").equalsIgnoreCase("yes"))
                    {
                        flyout1.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                loggedinDriver();

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                    }

                }
                else
                {
                    flyout1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            Intent i = new Intent(SplashActivity.this, SignInActivity.class).putExtra("from", "splash");

                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                            finish();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                //Toast.makeText(SplashActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void loggedinDriver() {

        Log.d("devicetoken", "" + devicetoken);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.loginDriver(pref.getString("email", ""), pref.getString("password", ""), "android",devicetoken, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {

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
                        if (status.equalsIgnoreCase("1")) {
                            logo.startAnimation(flyout1);

                            flyout1.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {

                                    startActivity(new Intent(SplashActivity.this, NavHomeActivity.class).putExtra("from", "splash"));
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                    finish();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });

                        } else {

                        }

                    } else {
                        if (stringBuilder.toString().equalsIgnoreCase("null") || stringBuilder == null) {
                            Toast.makeText(SplashActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SplashActivity.this, "" + stringBuilder, Toast.LENGTH_SHORT).show();
                        }

                    }


                } catch (IOException e) {
                    Toast.makeText(SplashActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SplashActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(SplashActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();
                //ServerFailureDialog.alertDialog(SplashActivity.this);
                //Toast.makeText(SplashActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SplashActivity.this, SignInActivity.class).putExtra("from","this");
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            }
        });
    }

    public void updateToken() {

        Log.d("devicetoken", "" + devicetoken);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.loginDriver(pref.getString("email", ""), pref.getString("password", ""), "android",devicetoken, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {

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
                        if (status.equalsIgnoreCase("1")) {
                            logo.startAnimation(flyout1);

                            flyout1.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {

                                    Intent i = new Intent(SplashActivity.this, ProfileActivity.class).putExtra("from", "splash");

                                    startActivity(i);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                    finish();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                        } else {

                        }

                    } else {
                        if (stringBuilder.toString().equalsIgnoreCase("null") || stringBuilder == null) {
                            Toast.makeText(SplashActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SplashActivity.this, "" + stringBuilder, Toast.LENGTH_SHORT).show();
                        }

                    }


                } catch (IOException e) {
                    Toast.makeText(SplashActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SplashActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(SplashActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();
                //ServerFailureDialog.alertDialog(SplashActivity.this);
                //Toast.makeText(SplashActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SplashActivity.this, SignInActivity.class).putExtra("from","this");
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            }
        });
    }


}
