package com.eleganzit.msafiridriver.activity;

import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.eleganzit.msafiridriver.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;


public class DemoScreen extends AppCompatActivity  implements OnMapReadyCallback{

    MapView map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_screen);
        map=findViewById(R.id.map);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
