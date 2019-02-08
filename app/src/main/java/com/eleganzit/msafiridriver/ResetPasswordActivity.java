package com.eleganzit.msafiridriver;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganzit.msafiridriver.utils.MyInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText newpassword,cnewpassword;
    LinearLayout fp_submit;
    String driver_id;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reset_password);

        newpassword=findViewById(R.id.newpassword);
        cnewpassword=findViewById(R.id.cnewpassword);
        fp_submit=findViewById(R.id.fp_submit);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        driver_id=getIntent().getStringExtra("driver_id");

        fp_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newpassword.getText().toString().equalsIgnoreCase(""))
                {
                    YoYo.with(Techniques.Shake)
                            .duration(750)
                            .repeat(0)
                            .playOn(newpassword);
                    newpassword.setError("Please enter new password");
                    newpassword.requestFocus();
                }
                else if(cnewpassword.getText().toString().equalsIgnoreCase(""))
                {
                    YoYo.with(Techniques.Shake)
                            .duration(750)
                            .repeat(0)
                            .playOn(cnewpassword);
                    cnewpassword.setError("Please confirm password");
                    cnewpassword.requestFocus();
                }
                else if(!(newpassword.getText().toString().equalsIgnoreCase(cnewpassword.getText().toString())))
                {
                    YoYo.with(Techniques.Shake)
                            .duration(750)
                            .repeat(0)
                            .playOn(cnewpassword);
                    cnewpassword.setError("Password doesn't match");
                    cnewpassword.requestFocus();
                }
                else
                {
                    driverResetpassword();
                }
            }
        });

    }

    public void driverResetpassword()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.driverResetpassword(driver_id,cnewpassword.getText().toString(), new retrofit.Callback<retrofit.client.Response>() {
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

                            }
                            Toast.makeText(ResetPasswordActivity.this, "Password has been reset", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(ResetPasswordActivity.this, msg+"", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {

                        Toast.makeText(ResetPasswordActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ResetPasswordActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}
