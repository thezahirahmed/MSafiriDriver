package com.eleganzit.msafiridriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eleganzit.msafiridriver.uploadImage.CallAPiActivity;
import com.eleganzit.msafiridriver.uploadImage.GetResponse;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.github.gfranks.minimal.notification.GFMinimalNotification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

public class WelcomeActivity extends AppCompatActivity {

    LinearLayout create;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView username;
    ProgressDialog progressDialog;
    CallAPiActivity callAPiActivity;
    boolean uploadVehicleSuccess,uploadVehicleProfileSuccess,addBankdetailsSuccess,uploadDocumentSuccess;
    com.eleganzit.msafiridriver.uploadMultupleImage.CallAPiActivity callAPiActivity2;
    private String URLUPDATEVEHICLE= "http://itechgaints.com/M-safiri-API/drivervehiclePhoto";
    private String URLUPDATEUSER = "http://itechgaints.com/M-safiri-API/drvierDocument";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();
        create=findViewById(R.id.create);
        username=findViewById(R.id.username);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        callAPiActivity = new CallAPiActivity(this);
        callAPiActivity2 = new com.eleganzit.msafiridriver.uploadMultupleImage.CallAPiActivity(this);
        username.setText(pref.getString("fullname",""));

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,ProfileActivity.class).putExtra("from","welcome"));
                finish();
                //addBankdetails();

            }
        });

        //blankaddVehicledetail();

    }

    private void uploadVehicleProfile() {

        //progressDialog.show();

            HashMap<String, String> map = new HashMap<>();
            map.put("driver_id", pref.getString("driver_id", ""));
            callAPiActivity.doPostWithFiles(WelcomeActivity.this, map, URLUPDATEVEHICLE, "", "vehicle_profile", new GetResponse() {
                @Override
                public void onSuccessResult(JSONObject result) throws JSONException {
                    //progressDialog.dismiss();
                    uploadVehicleProfileSuccess=true;
                    progressDialog.dismiss();
                    String status = result.getString("status");
                    JSONArray jsonArray = null;
                    if(status.equalsIgnoreCase("1")) {
                        jsonArray = result.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            editor.putString("status", "loggedin");
                            editor.commit();

                        }
                        Log.d("result",""+result);

                    }
                    else
                    {
                        Toast.makeText(WelcomeActivity.this, ""+result.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailureResult(String message) {
                    //progressDialog.dismiss();
                    uploadVehicleProfileSuccess=false;
                    removeDriver();
                    Toast.makeText(WelcomeActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();
                    Log.d("messageeeeeeeeeee",message);

                }
            });


    }

    private void uploadDocument() {

        //progressDialog.show();

        HashMap<String, String> map = new HashMap<>();
        Log.d("iddddd",pref.getString("driver_id", ""));
        map.put("driver_id", pref.getString("driver_id", ""));
        map.put("blank", "1");
        map.put("driving_livence[]", "");
        map.put("address_proof[]", "");

        callAPiActivity2.doPost(WelcomeActivity.this, map, URLUPDATEUSER, new com.eleganzit.msafiridriver.uploadMultupleImage.GetResponse() {

            @Override
            public void onSuccesResult(JSONObject result) throws JSONException {
                progressDialog.dismiss();
                String status = result.getString("status");
                if(status.equalsIgnoreCase("1"))
                {
                   /* startActivity(new Intent(WelcomeActivity.this,ProfileActivity.class).putExtra("from","welcome"));
                    finish();*/

                    /*if(hasDocs.equalsIgnoreCase("yes"))
                    {*/
                        /*editor.putString("documentsInfo", "changed");
                        editor.commit();*/

                    /*}
                    else
                    {
                        *//*editor.putString("documentsInfo", "filled");
                        editor.commit();*//*
                    }
*/
                }
                else
                {
                    Toast.makeText(WelcomeActivity.this, ""+result.getString("message"), Toast.LENGTH_SHORT).show();
                }

                Log.d("messageeeeeeeeeee","succccccccessss"+status);
            }

            @Override
            public void onFailureResult(String message) {
                progressDialog.dismiss();
                if(message.equalsIgnoreCase("success"))
                {
                    startActivity(new Intent(WelcomeActivity.this,ProfileActivity.class).putExtra("from","welcome"));
                }
                else
                {
                    Toast.makeText(WelcomeActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();
                }
                Log.d("messageeeeeeeeeee",message);

            }
        });


    }

    public void removeDriver() {

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.removeDriver(pref.getString("driver_id", ""), new retrofit.Callback<retrofit.client.Response>() {
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

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        JSONArray jsonArray = null;
                        if (status.equalsIgnoreCase("1")) {
                            jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                            }
                            Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class).putExtra("from","account");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Bungee.slideLeft(WelcomeActivity.this);
                            Toast.makeText(WelcomeActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();

                            editor.clear();
                            editor.commit();
                            editor.apply();

                        } else {
                            Toast.makeText(WelcomeActivity.this, ""+message, Toast.LENGTH_SHORT).show();

                        }

                    } else {

                        Toast.makeText(WelcomeActivity.this, "" + stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(WelcomeActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }



    public void blankaddVehicledetail()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.blankaddVehicledetail(pref.getString("driver_id", ""), "1", "", "","", "", "", "",new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {

                //progressDialog.dismiss();
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
                        String message = jsonObject.getString("message");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {

                            //uploadVehicleProfile();

                            //addBankdetails();
                            //Toast.makeText(RegistrationActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            /*startActivity(new Intent(RegisterationActivity.this, WelcomeActivity.class));
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            finish();*/
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(WelcomeActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                        if (jsonObject.getString("status").equalsIgnoreCase("0")) {
                            Toast.makeText(WelcomeActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                        if (jsonObject.getString("status").equalsIgnoreCase("2")) {
                            Toast.makeText(WelcomeActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    }
                    else
                    {
                        progressDialog.dismiss();

                        Toast.makeText(WelcomeActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                removeDriver();
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(WelcomeActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadVehicle() {

        progressDialog.show();

        HashMap<String, String> map = new HashMap<>();
        Log.d("iddddd",pref.getString("driver_id", ""));
        map.put("driver_id", pref.getString("driver_id", ""));
        map.put("blank", "1");
        map.put("vehicle_name", "");
        map.put("vehicle_type", "");
        map.put("vehicle_number", "");
        map.put("seats", "");
        map.put("vehicle_photo[]", "");
        map.put("numberplate_photo[]", "");
        callAPiActivity2.doPost(WelcomeActivity.this, map, "http://itechgaints.com/M-safiri-API/addVehicledetail", new com.eleganzit.msafiridriver.uploadMultupleImage.GetResponse() {

            @Override
            public void onSuccesResult(JSONObject result) throws JSONException {

                uploadVehicleSuccess=true;
                String status = result.getString("status");
                if(status.equalsIgnoreCase("1"))
                {
                    uploadVehicleProfile();
                    Log.d("messageeeeeeeeeee","veh 1"+status);
                }
                else
                {
                    Log.d("messageeeeeeeeeee","veh 2"+status);
                    Toast.makeText(WelcomeActivity.this, ""+result.getString("message"), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailureResult(String message) {
                progressDialog.dismiss();
                uploadVehicleSuccess=false;

                if(message.equalsIgnoreCase("success"))
                {

                    Log.d("messageeeeeeeeeee","veh 3"+message);
                }
                else
                {
                    Toast.makeText(WelcomeActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();
                    removeDriver();
                    Log.d("messageeeeeeeeeee","veh 4"+message);
                }
                Log.d("messageeeeeeeeeee","veh 5"+message);

            }
        });


    }

    public void addBankdetails()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.addBankdetails(pref.getString("driver_id",""), "",
                "", "", "",
                "", "", "",
                "","","", "", new retrofit.Callback<retrofit.client.Response>() {
                    @Override
                    public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                        addBankdetailsSuccess=true;
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
                                    //uploadDocument();
                                    jsonArray = jsonObject.getJSONArray("data");
                                    for(int i=0;i<jsonArray.length();i++)
                                    {
                                        JSONObject jsonObject1=jsonArray.getJSONObject(i);


                                    }
                                    //Toast.makeText(BankAccountActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();

                                }
                                else
                                {
                                    Toast.makeText(WelcomeActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                            }
                            else
                            {

                                Toast.makeText(WelcomeActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            Toast.makeText(WelcomeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(WelcomeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        addBankdetailsSuccess=false;
                        //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                        Toast.makeText(WelcomeActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

                    }
                });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please complete your profile", Toast.LENGTH_SHORT).show();
    }


}
