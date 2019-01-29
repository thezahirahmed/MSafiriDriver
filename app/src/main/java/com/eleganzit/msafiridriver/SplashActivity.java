package com.eleganzit.msafiridriver;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.eleganzit.msafiridriver.utils.MyInterface;

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
    Animation flyout1,flyin1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bungee.slideLeft(this);
        ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();
        flyin1 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.flyin1);
        flyout1 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.flyout1);

        logo=findViewById(R.id.logo);

        if(pref.getString("status","").equalsIgnoreCase("loggedin"))
        {
            logo.startAnimation(flyin1);

            flyin1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    getDriverdata();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        else
        {
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
            },3200);
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

    public void getDriverdata()
    {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getDriverdata(pref.getString("driver_id",""), new retrofit.Callback<retrofit.client.Response>() {
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
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                String approvel = jsonObject1.getString("approvel");

                                if(approvel.equalsIgnoreCase("yes"))
                                {
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
                                else
                                {
                                    logo.startAnimation(flyout1);

                                    flyout1.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {

                                            Intent i = new Intent(SplashActivity.this, ProfileActivity.class).putExtra("from","splash");

                                            startActivity(i);
                                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                                            finish();
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                }
                                editor.putString("approvel", approvel);
                                editor.commit();

                            }
                        }
                        else
                        {
                            Toast.makeText(SplashActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

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
                        Toast.makeText(SplashActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
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
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(SplashActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
