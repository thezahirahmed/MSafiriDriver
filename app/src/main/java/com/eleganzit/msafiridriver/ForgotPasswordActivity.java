package com.eleganzit.msafiridriver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganzit.msafiridriver.utils.RobotoMediumTextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ForgotPasswordActivity extends AppCompatActivity {


    RobotoMediumTextView fp_signin;
    EditText fp_email;
    LinearLayout fp_signbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_forgot_password);

        fp_signin=findViewById(R.id.fp_signin);
        fp_signbtn=findViewById(R.id.fp_signbtn);
        fp_email=findViewById(R.id.fp_email);

        fp_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fp_signbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValideEmail(fp_email.getText().toString()))
                {
                    YoYo.with(Techniques.Shake)
                            .duration(750)
                            .repeat(0)
                            .playOn(fp_email);
                    fp_email.setError("Please enter valid Email");
                    fp_email.requestFocus();
                }
                else
                {
                    startActivity(new Intent(ForgotPasswordActivity.this,OTPActivity.class).putExtra("email",fp_email.getText().toString()));
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    finish();
                }

            }
        });

    }

    public boolean isValideEmail(String Email) {

        String Email_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(Email_PATTERN);
        Matcher matcher = pattern.matcher(Email);
        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

    }
}
