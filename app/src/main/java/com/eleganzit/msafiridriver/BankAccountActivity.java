package com.eleganzit.msafiridriver;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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

public class BankAccountActivity extends AppCompatActivity {

    Calendar calendar=Calendar.getInstance();
    EditText edbank_name,edpayee,edaccount_number,edbirth,edifsc_code,edstreet1,edstreet2,edcity,edstate,edpostal,edcountry;
    ImageView save;
    ArrayList<CountryData> countryArrayList2=new ArrayList<>();
    ArrayList<String> countryArrayList=new ArrayList<>();
    ArrayList<String> stateArrayList=new ArrayList<>();
    private String country_id="";
    ProgressDialog progressDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private String data="0";
    private String approvel;
    boolean upcomingTripsAreDone=false;
    boolean currentTripIsDone=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account);
        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Bungee.slideRight(BankAccountActivity.this);
            }
        });
        edbank_name=findViewById(R.id.edbank_name);
        edpayee=findViewById(R.id.edpayee);
        edaccount_number=findViewById(R.id.edaccount_number);
        edbirth=findViewById(R.id.edbirth);
        edifsc_code=findViewById(R.id.edifsc_code);
        edstreet1=findViewById(R.id.edstreet1);
        edstreet2=findViewById(R.id.edstreet2);
        edcity=findViewById(R.id.edcity);
        edstate=findViewById(R.id.edstate);
        edpostal=findViewById(R.id.edpostal);
        edcountry=findViewById(R.id.edcountry);
        save=findViewById(R.id.save);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        getCountry();
        getBankdetails();
        edstate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edcountry.getText().toString().equalsIgnoreCase("")|| country_id.equalsIgnoreCase("") || country_id.equalsIgnoreCase("0"))
                {
                    Toast.makeText(BankAccountActivity.this, "Please select country first", Toast.LENGTH_SHORT).show();
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
                DatePickerDialog datePickerDialog=new DatePickerDialog(BankAccountActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edbank_name.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edbank_name);
                    edbank_name.setError("Bank name cannot be empty");
                    edbank_name.requestFocus();
                }
                else if(edpayee.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edpayee);
                    edpayee.setError("Payee cannot be empty");
                    edpayee.requestFocus();
                }
                else if(edaccount_number.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edaccount_number);
                    edaccount_number.setError("Account number cannot be empty");
                    edaccount_number.requestFocus();
                }
                else if(edifsc_code.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edifsc_code);
                    edifsc_code.setError("IFSC code cannot be empty");
                    edifsc_code.requestFocus();
                }
                else if(edstreet1.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edstreet1);
                    edstreet1.setError("Street cannot be empty");
                    edstreet1.requestFocus();
                }
                else if(edstreet2.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edstreet2);
                    edstreet2.setError("Street cannot be empty");
                    edstreet2.requestFocus();
                }
                else if(edcity.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edcity);
                    edcity.setError("City cannot be empty");
                    edcity.requestFocus();
                }
                else if(edstate.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edstate);
                    edstate.setError("State cannot be empty");
                    edstate.requestFocus();
                }
                else if(edpostal.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edpostal);
                    edpayee.setError("Postal code cannot be empty");
                    edpayee.requestFocus();
                }
                else if(edcountry.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edcountry);
                    edcountry.setError("Country cannot be empty");
                    edcountry.requestFocus();
                }
                else if(edbirth.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edbirth);
                    edbirth.setError("Birth date cannot be empty");
                    edbirth.requestFocus();
                }
                else
                {
                    /*if(data.equalsIgnoreCase("0"))
                    {
                        addBankdetails();
                    }
                    else
                    {*/
                    getUpcomingTrips();
                    //}

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
                        Toast.makeText(BankAccountActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BankAccountActivity.this, "Couldn't refresh trips", Toast.LENGTH_SHORT).show();

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
                            new AlertDialog.Builder(BankAccountActivity.this).setMessage("Are you sure you want to save the changes?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            updateBankdetails();
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            }).show();

                        }
                        else
                        {
                            //Toast.makeText(BankAccountActivity.this, "You cannot update profile if you have any trips remaining!", Toast.LENGTH_LONG).show();
                            new AlertDialog.Builder(BankAccountActivity.this).setMessage("You cannot update profile if you have any trips remaining!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                        Toast.makeText(BankAccountActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BankAccountActivity.this, "Couldn't refresh trips", Toast.LENGTH_SHORT).show();

            }
        });
    }
    
    public void updateApprovalStatus()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.updateApprovalStatus(pref.getString("driver_id",""), "0",
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


                                        Toast.makeText(BankAccountActivity.this, "Please submit your profile for approval", Toast.LENGTH_LONG).show();
                                        finish();
                                    }

                                }
                                else
                                {
                                    Toast.makeText(BankAccountActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                Toast.makeText(BankAccountActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(BankAccountActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void getBankdetails()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getBankdetails(pref.getString("driver_id",""), new retrofit.Callback<retrofit.client.Response>() {
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
                            data="1";
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                String bank_id = jsonObject1.getString("bank_id");

                                String bank_name = jsonObject1.getString("bank_name");

                                String bank_payee = jsonObject1.getString("bank_payee");

                                String bank_account = jsonObject1.getString("bank_account");

                                String bank_ifsc = jsonObject1.getString("bank_ifsc");

                                String street1 = jsonObject1.getString("street1");

                                String street2 = jsonObject1.getString("street2");

                                String city = jsonObject1.getString("city");

                                String state = jsonObject1.getString("state");

                                String postal_code = jsonObject1.getString("postal_code");

                                String country = jsonObject1.getString("country");

                                country_id = jsonObject1.getString("country_id");

                                String dob = jsonObject1.getString("birthday");

                                edbank_name.setText(bank_name);
                                edpayee.setText(bank_payee);
                                edaccount_number.setText(bank_account);
                                edifsc_code.setText(bank_ifsc);
                                edstreet1.setText(street1);
                                edstreet2.setText(street2);
                                edcity.setText(city);
                                edstate.setText(state);
                                edpostal.setText(postal_code);
                                edcountry.setText(country);
                                edbirth.setText(dob);

                                if(country_id.equalsIgnoreCase("") || country_id.equalsIgnoreCase("0"))
                                {
                                }
                                else
                                {
                                    getState(country_id);
                                }
                            }
                        }
                        else
                        {
                            data="0";
                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(BankAccountActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BankAccountActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void addBankdetails()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.addBankdetails(pref.getString("driver_id",""), edbank_name.getText().toString(),
                edpayee.getText().toString(), edaccount_number.getText().toString(), edifsc_code.getText().toString(),
                edstreet1.getText().toString(), edstreet2.getText().toString(), edcity.getText().toString(),
                edstate.getText().toString(),edpostal.getText().toString(),edcountry.getText().toString(), edbirth.getText().toString(), new retrofit.Callback<retrofit.client.Response>() {
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

                                        /*String bank_id = jsonObject1.getString("bank_id");
                                        editor.putString("bank_id", bank_id);
                                *//*String email = jsonObject1.getString("email");
                                editor.putString("email", email);*//*
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
                                        editor.commit();*/
                                        editor.putString("bankInfo","filled");
                                        editor.commit();
                                    }
                                    //Toast.makeText(BankAccountActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else
                                {

                                }

                                // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                            }
                            else
                            {

                                Toast.makeText(BankAccountActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(BankAccountActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void updateBankdetails()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.updateBankdetails(pref.getString("driver_id",""), edbank_name.getText().toString(),
                edpayee.getText().toString(), edaccount_number.getText().toString(), edifsc_code.getText().toString(),
                edstreet1.getText().toString(), edstreet2.getText().toString(), edcity.getText().toString(),
                edstate.getText().toString(),edpostal.getText().toString(),edcountry.getText().toString(), edbirth.getText().toString(), new retrofit.Callback<retrofit.client.Response>() {
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

                                        /*String bank_id = jsonObject1.getString("bank_id");
                                        editor.putString("bank_id", bank_id);
                                *//*String email = jsonObject1.getString("email");
                                editor.putString("email", email);*//*
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
                                        editor.commit();*/
                                        updateApprovalStatus();
                                    }

                                }
                                else
                                {

                                }

                                // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                            }
                            else
                            {

                                Toast.makeText(BankAccountActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(BankAccountActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

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

                        }
                        else
                        {

                        }

                    }
                    else
                    {

                        Toast.makeText(BankAccountActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BankAccountActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void getState(String country_id)
    {
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

                            }

                        }
                        else
                        {

                        }

                    }
                    else
                    {

                        Toast.makeText(BankAccountActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BankAccountActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    void showCountryDialog() {

        final ListAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, android.R.id.text1, countryArrayList);

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                CountryData countryData=countryArrayList2.get(i);

                edcountry.setText(countryData.getCountry());

                country_id=countryData.getCountry_id();
                edstate.setText("");
                getState(country_id);
            }
        });
        builder.show();

    }

    void showStateDialog() {

        final ListAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, android.R.id.text1, stateArrayList);

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                edstate.setText(stateArrayList.get(i));
            }
        });
        builder.show();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(BankAccountActivity.this);
    }
}
