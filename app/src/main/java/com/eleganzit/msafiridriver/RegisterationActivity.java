package com.eleganzit.msafiridriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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

public class RegisterationActivity extends AppCompatActivity {


    LinearLayout newaccount;
    LinearLayout individual,company;
    TextInputLayout edit_auth;
    EditText name,email,auth,password,confirm;
    ProgressDialog progressDialog;
    RelativeLayout main;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private String Token;
    private String devicetoken;
    private String type="individual";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_registeration);
        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();
        individual=findViewById(R.id.individual);
        main=findViewById(R.id.mainn);
        company=findViewById(R.id.company);
        edit_auth=findViewById(R.id.edit_auth);

        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        auth=findViewById(R.id.auth);
        password=findViewById(R.id.password);
        confirm=findViewById(R.id.confirm);
        newaccount=findViewById(R.id.newaccount);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        newaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(750)
                            .repeat(0)
                            .playOn(name);

                    name.setError("Please enter Full name");
                    name.requestFocus();

                }
                else if (!isValideEmail(email.getText().toString()))
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(email);
                    email.setError("Please enter valid Email");
                    email.requestFocus();

                }
                else if (password.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(password);
                    password.setError("Please enter your Password");
                    password.requestFocus();
                }
                else if (confirm.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(confirm);
                    confirm.setError("Please confirm your Password");
                    confirm.requestFocus();
                }
                else if (!(password.getText().toString().equalsIgnoreCase(confirm.getText().toString())))
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(confirm);
                    confirm.setError("Password doesn't match");
                    confirm.requestFocus();
                }
                else {
                    if(type.equalsIgnoreCase("company"))
                    {
                        if (auth.getText().toString().trim().isEmpty())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(750)
                                    .repeat(0)
                                    .playOn(auth);

                            auth.setError("Please enter Authorization code");
                            auth.requestFocus();

                        }
                        else
                        {
                            registerDriver();
                        }
                    }
                    else
                    {
                        registerDriver();
                    }

                }

            }
        });
        individual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                individual.setBackgroundResource(R.drawable.tab_left_light_bg);
                company.setBackgroundResource(R.drawable.tab_right_dark_bg);
                edit_auth.setVisibility(View.GONE);
                type="individual";

            }
        });
        company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                individual.setBackgroundResource(R.drawable.tab_left_dark_bg);
                company.setBackgroundResource(R.drawable.tab_right_light_bg);
                edit_auth.setVisibility(View.VISIBLE);
                type="company";
            }
        });
        /*TextView textView = findViewById(R.id.terms);
        SpannableString content = new SpannableString("Terms and conditions");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);*/
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                Token= FirebaseInstanceId.getInstance().getToken();
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
                    Toast.makeText(RegisterationActivity.this, "No token", Toast.LENGTH_SHORT).show();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });t.start();
    }

    public void registerDriver()
    {
        Log.d("rrrrrdataaaaaa",type+"   "+name.getText().toString()+"  "+email.getText().toString()+"  "+password.getText().toString()+"  "+auth.getText().toString()+"   "+Token);
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.registerDriver(type, name.getText().toString(), email.getText().toString(), password.getText().toString(),auth.getText().toString(), "android", Token, new retrofit.Callback<retrofit.client.Response>() {
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

                                String id = jsonObject1.getString("driver_id");
                                editor.putString("driver_id", id);
                                editor.putString("status", "loggedin");
                                String fullname = jsonObject1.getString("fullname");
                                editor.putString("fullname", fullname);
                                String email = jsonObject1.getString("email");
                                editor.putString("email", email);
                                editor.putString("password", password.getText().toString());
                                editor.putString("personalInfo", "empty");
                                editor.putString("documentsInfo", "empty");
                                editor.putString("vehicleInfo", "empty");
                                editor.putString("bankInfo", "empty");
                                editor.putString("approvel", "no");
                                editor.commit();
                            }

                            //Toast.makeText(RegistrationActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterationActivity.this, WelcomeActivity.class));
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            finish();
                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                        if (jsonObject.getString("status").equalsIgnoreCase("0")) {
                            GFMinimalNotification mCurrentNotification = GFMinimalNotification.make(main, "User already exist", GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR);
                            mCurrentNotification.setDirection(GFMinimalNotification.DIRECTION_TOP);
                            mCurrentNotification.setHelperImage(R.drawable.group_40);
                            mCurrentNotification.show();
                        }
                        if (jsonObject.getString("status").equalsIgnoreCase("2")) {
                            //Toast.makeText(RegistrationActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            GFMinimalNotification mCurrentNotification = GFMinimalNotification.make(main, jsonObject.getString("message"), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR);
                            mCurrentNotification.setDirection(GFMinimalNotification.DIRECTION_TOP);
                            mCurrentNotification.setHelperImage(R.drawable.group_40);
                            mCurrentNotification.show();
                        }
                    }
                    else
                    {

                        Toast.makeText(RegisterationActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RegisterationActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

    }
    public boolean isValideEmail(String Email) {

        String Email_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(Email_PATTERN);
        Matcher matcher = pattern.matcher(Email);
        return matcher.matches();
    }

}
