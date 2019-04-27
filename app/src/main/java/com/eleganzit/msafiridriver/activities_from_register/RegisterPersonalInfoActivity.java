package com.eleganzit.msafiridriver.activities_from_register;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.model.CountryData;
import com.eleganzit.msafiridriver.model.StateData;
import com.eleganzit.msafiridriver.utils.MyInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

public class RegisterPersonalInfoActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    ImageView save;
    Calendar calendar=Calendar.getInstance();
    EditText edfullname,edemail,edphone,edpassword,edbirth,edstreet,edcity,edstate,edpostal,edcountry;
    RadioGroup radioGroup;
    RadioButton radioButton,male,female;
    ArrayList<CountryData> countryArrayList2=new ArrayList<>();
    ArrayList<String> countryArrayList=new ArrayList<>();
    ArrayList<String> stateArrayList=new ArrayList<>();
    private String country_id="";
    boolean upcomingTripsAreDone=false;
    boolean currentTripIsDone=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Bungee.slideRight(RegisterPersonalInfoActivity.this);
            }
        });
        edfullname=findViewById(R.id.edfullname);
        edfullname.setText(pref.getString("fullname",""));
        edemail=findViewById(R.id.edemail);
        edemail.setText(pref.getString("email",""));
        edphone=findViewById(R.id.edphone);
        edpassword=findViewById(R.id.edpassword);
        edpassword.setText(pref.getString("password",""));
        edbirth=findViewById(R.id.edbirth);
        edstreet=findViewById(R.id.edstreet);
        edcity=findViewById(R.id.edcity);
        edstate=findViewById(R.id.edstate);
        edpostal=findViewById(R.id.edpostal);
        edcountry=findViewById(R.id.edcountry);
        radioGroup=findViewById(R.id.rg_gender);
        male=findViewById(R.id.rb_male);
        female=findViewById(R.id.rb_female);
        save=findViewById(R.id.save);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        getDriverdata();

        edstate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edcountry.getText().toString().equalsIgnoreCase("")|| country_id.equalsIgnoreCase("") || country_id.equalsIgnoreCase("0"))
                {
                    Toast.makeText(RegisterPersonalInfoActivity.this, "Please select country first", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    showStateDialog();
                }
            }
        });

        edcountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCountryDialog();
            }
        });

        edbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog=new DatePickerDialog(RegisterPersonalInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String mydate = "";
                        String smonth="";
                        String sday="";
                        if(month<10)
                        {
                            smonth="0"+(month+1);
                        }
                        else
                        {
                            smonth=""+(month+1);
                        }
                        if(dayOfMonth<10)
                        {
                            sday="0"+dayOfMonth;
                        }
                        else
                        {
                            sday=""+dayOfMonth;
                        }
                        mydate=year+"-"+smonth+"-"+sday;
                        edbirth.setText(mydate);
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
//
        edpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog=new Dialog(RegisterPersonalInfoActivity.this);
                dialog.setContentView(R.layout.dialog_layout);
                final EditText old=dialog.findViewById(R.id.oldpassword);
                final EditText neww=dialog.findViewById(R.id.newpassword);
                final EditText confirm=dialog.findViewById(R.id.cpassword);
                TextView ok=dialog.findViewById(R.id.ok);
                TextView cancel=dialog.findViewById(R.id.cancel);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if ((old.getText().toString().trim().equalsIgnoreCase("")))
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(0)
                                    .playOn(old);
                            old.setError("Please enter old Password");
                            old.requestFocus();
                        }
                        /*else if (!(old.getText().toString().trim().equalsIgnoreCase(pref.getString("password",""))))
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(0)
                                    .playOn(old);
                            old.setError("Wrong Password");
                            old.requestFocus();
                        }*/
                        else if (neww.getText().toString().trim().isEmpty())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(0)
                                    .playOn(neww);
                            neww.setError("Please enter new Password");
                            neww.requestFocus();
                        }
                        else if (!(confirm.getText().toString().equalsIgnoreCase(neww.getText().toString())))
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(0)
                                    .playOn(confirm);
                            confirm.setError("Password does not match");
                            confirm.requestFocus();
                        }
                        else {
                            changePassword(dialog,old,confirm.getText().toString(),old.getText().toString());

                        }

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = radioGroup.getCheckedRadioButtonId();

                radioButton =findViewById(selectedId);
                if (edfullname.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edfullname);
                    edfullname.setError("Please enter full name");
                    edfullname.requestFocus();
                }
                /*else if(edemail.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edemail);
                    edemail.setError("Email cannot be empty");
                    edemail.requestFocus();
                }*/
                else if(edphone.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edphone);
                    edphone.setError("Please enter phone number");
                    edphone.requestFocus();
                }
                else if(edbirth.getText().toString().isEmpty() || edbirth.getText().toString().equalsIgnoreCase("0000-00-00"))
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edbirth);
                    edbirth.setError("Please select birth date");
                    edbirth.requestFocus();
                }
                else if(edstreet.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edstreet);
                    edstreet.setError("Please enter street");
                    edstreet.requestFocus();
                }
                else if(edstreet.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edstreet);
                    edstreet.setError("Please enter street");
                    edstreet.requestFocus();
                }
                else if(edstreet.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edstreet);
                    edstreet.setError("Please enter street");
                    edstreet.requestFocus();
                }
                else if(edcity.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edcity);
                    edcity.setError("Please enter city");
                    edcity.requestFocus();
                }
                else if(edstate.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edstate);
                    edstate.setError("Please select state");
                    edstate.requestFocus();
                }
                else if(edpostal.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edpostal);
                    edpostal.setError("Please enter postal code");
                    edpostal.requestFocus();
                }
                else if(edcountry.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edcountry);
                    edcountry.setError("Please select country");
                    edcountry.requestFocus();
                }
                else {
                    updateDriverdata();
                    //getUpcomingTrips();

                }

            }
        });
    }

    public void getUpcomingTrips()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getDriverTrips(pref.getString("driver_id",""), "current", new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {

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
                            upcomingTripsAreDone=false;
                        }
                        else
                        {
                            upcomingTripsAreDone=true;
                        }
                        getCurrentTrip();

                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterPersonalInfoActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Log.d("errorrrr",""+error.getMessage());
                Toast.makeText(RegisterPersonalInfoActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void getCurrentTrip()
    {

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getDriverTrips(pref.getString("driver_id",""), "upcoming", new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {

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
                        progressDialog.dismiss();
                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {
                            currentTripIsDone=false;
                        }
                        else
                        {
                            currentTripIsDone=true;
                        }
                        if(upcomingTripsAreDone && currentTripIsDone)
                        {
                            //updateDriverdata();
                        }
                        else
                        {
                            //Toast.makeText(PersonalInfoActivity.this, "You cannot update profile if you have any trips remaining!", Toast.LENGTH_LONG).show();
                            new AlertDialog.Builder(RegisterPersonalInfoActivity.this).setMessage("You cannot update profile if you have any trips remaining!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }

                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterPersonalInfoActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Log.d("errorrrr",""+error.getMessage());
                Toast.makeText(RegisterPersonalInfoActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(RegisterPersonalInfoActivity.this);
    }

    public void getDriverdata()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getPersonalInfo(pref.getString("driver_id",""), new retrofit.Callback<retrofit.client.Response>() {
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
                        if(status.equalsIgnoreCase("1"))
                        {
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                String photo = jsonObject1.getString("photo");
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
                                country_id = jsonObject1.getString("country_id");
                                editor.putString("country_id", country_id);
                                String mobile_number = jsonObject1.getString("mobile_number");
                                editor.putString("mobile_number", mobile_number);
                                String dob = jsonObject1.getString("dob");
                                editor.putString("dob", dob);
                                String gender = jsonObject1.getString("gender");
                                editor.putString("gender", gender);
                                if(gender.equalsIgnoreCase("male"))
                                {
                                    male.setChecked(true);
                                }
                                else
                                {
                                    female.setChecked(true);
                                }
                                editor.commit();

                                edphone.setText(mobile_number);
                                if(dob.equalsIgnoreCase("0000-00-00"))
                                {
                                    edbirth.setText("");
                                }
                                else
                                {
                                    edbirth.setText(dob);
                                }

                                edstreet.setText(street);
                                edcity.setText(city);
                                edstate.setText(state);
                                edpostal.setText(postal_code);
                                edcountry.setText(country);

                            }
                        }
                        else
                        {
                            Toast.makeText(RegisterPersonalInfoActivity.this, "s 0"+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(RegisterPersonalInfoActivity.this, "sb null"+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {
                    Toast.makeText(RegisterPersonalInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterPersonalInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(RegisterPersonalInfoActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void changePassword(final Dialog dialog, final EditText old, String password, String old_password)
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.changePassword(pref.getString("driver_id",""),old_password,password, new retrofit.Callback<retrofit.client.Response>() {
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
                        String msg = jsonObject.getString("message");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                String password = jsonObject1.getString("password");
                                edpassword.setText(password);
                                editor.putString("password", password);

                                editor.commit();

                            }
                            Toast.makeText(RegisterPersonalInfoActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(0)
                                    .playOn(old);
                            old.setError("Wrong Password");
                            old.requestFocus();
                            //Toast.makeText(PersonalInfoActivity.this, msg+"", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {

                        Toast.makeText(RegisterPersonalInfoActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RegisterPersonalInfoActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void getCountry()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getCountry("",new retrofit.Callback<retrofit.client.Response>() {
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
                    Log.d("docsstringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {

                            String country_id;
                            String country;
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                country_id = jsonObject1.getString("country_id");
                                country = jsonObject1.getString("country");

                                CountryData countryData=new CountryData(country_id,country);

                                countryArrayList.add(countryData.getCountry());
                                countryArrayList2.add(countryData);

                            }
                            final ListAdapter adapter = new ArrayAdapter(RegisterPersonalInfoActivity.this, android.R.layout.simple_list_item_single_choice, android.R.id.text1, countryArrayList);

                            final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(RegisterPersonalInfoActivity.this, R.style.AlertDialogCustom));

                        /*builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });*/

                            builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    CountryData countryData=countryArrayList2.get(i);

                                    edcountry.setText(countryData.getCountry());

                                    RegisterPersonalInfoActivity.this.country_id=countryData.getCountry_id();
                                    edstate.setText("");
                                    getState(RegisterPersonalInfoActivity.this.country_id,dialogInterface);
                                }
                            });
                            builder.show();


                        }
                        else
                        {
                            Toast.makeText(RegisterPersonalInfoActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        }

                    }
                    else
                    {

                        Toast.makeText(RegisterPersonalInfoActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {
                    Toast.makeText(RegisterPersonalInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterPersonalInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();

                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(RegisterPersonalInfoActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void getState(String country_id, final DialogInterface dialogInterface)
    {
        if(stateArrayList.size()>0)
        {
            stateArrayList.clear();
        }
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getState(country_id,new retrofit.Callback<retrofit.client.Response>() {
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
                    Log.d("docsstringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {

                            String state_id;
                            String country_id;
                            String state;
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                state_id = jsonObject1.getString("state_id");
                                country_id = jsonObject1.getString("country_id");
                                state = jsonObject1.getString("state");

                                StateData stateData=new StateData(state_id,country_id,state);
                                stateArrayList.add(state);
                                if(dialogInterface!=null)
                                {
                                    dialogInterface.dismiss();
                                }

                            }

                        }
                        else
                        {
                            //Toast.makeText(RegisterPersonalInfoActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        }

                    }
                    else
                    {
                        Toast.makeText(RegisterPersonalInfoActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {
                    Toast.makeText(RegisterPersonalInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterPersonalInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();

                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(RegisterPersonalInfoActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }

    void showCountryDialog() {

        getCountry();

    }

    void showStateDialog() {

        final ListAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, android.R.id.text1, stateArrayList);

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));

       /* builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });*/

        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                edstate.setText(stateArrayList.get(i));
                dialogInterface.dismiss();
            }
        });
        builder.show();

    }

    public void updateDriverdata()
    {

        String[] strArray = edfullname.getText().toString().split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }

        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.updateDriverdata(pref.getString("driver_id",""), radioButton.getText().toString(),
                edbirth.getText().toString(), edstreet.getText().toString(), edcity.getText().toString(),
                edstate.getText().toString(), edpostal.getText().toString(), edcountry.getText().toString(),
                edphone.getText().toString(), builder.toString(), new retrofit.Callback<retrofit.client.Response>() {
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

                                String fullname = jsonObject1.getString("fullname");
                                editor.putString("fullname", fullname);
                                /*String email = jsonObject1.getString("email");
                                editor.putString("email", email);*/
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
                                editor.putString("personalInfo", "filled");
                                editor.commit();
                            }
                            Toast.makeText(RegisterPersonalInfoActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(RegisterPersonalInfoActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RegisterPersonalInfoActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }
}
