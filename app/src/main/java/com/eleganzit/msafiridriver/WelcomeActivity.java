package com.eleganzit.msafiridriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {

    LinearLayout create;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView username;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();
        create=findViewById(R.id.create);
        username=findViewById(R.id.username);

        username.setText(pref.getString("fullname",""));

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,ProfileActivity.class).putExtra("from","welcome"));
            }
        });

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please complete your profile", Toast.LENGTH_SHORT).show();
    }
}
