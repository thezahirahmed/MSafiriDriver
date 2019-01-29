package com.eleganzit.msafiridriver.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eleganzit.msafiridriver.PickupLocation;
import com.eleganzit.msafiridriver.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;

public class SaveRouteActivity extends AppCompatActivity  {
    MapView mapView;
    Button saveroutebtn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    RecyclerView recycler;
    ArrayList<String> getTenantses=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_route);
        ImageView home=findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SaveRouteActivity.this, NavHomeActivity.class));
                finish();
            }
        });
        sharedPreferences=getSharedPreferences("pref",MODE_PRIVATE);
        editor=sharedPreferences.edit();
        mapView= (MapView) findViewById(R.id.map);
        recycler=findViewById(R.id.recycler);
        saveroutebtn=findViewById(R.id.saveroutebtn);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SaveRouteActivity.this);
        recycler.setLayoutManager(mLayoutManager);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(SaveRouteActivity.this, LinearLayoutManager.VERTICAL,false);
        recycler.setLayoutManager(layoutManager);
        recycler.setNestedScrollingEnabled(false);
        recycler.setAdapter(new TenantsAdapter(getTenantses,SaveRouteActivity.this));
        saveroutebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("key","save");
                editor.commit();
                startActivity(new Intent(SaveRouteActivity.this,NavHomeActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private class TenantsAdapter extends RecyclerView.Adapter<TenantsAdapter.MyViewHolder>
    {
        ArrayList<String> arrayList;
        Context context;

        public TenantsAdapter(ArrayList<String> arrayList, Context context)
        {
            this.arrayList = arrayList;
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(context).inflate(R.layout.saverouterow,parent,false);

            MyViewHolder myViewHolder=new MyViewHolder(v);

            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

      /*  GetTenants getTenants=arrayList.get(position);
        holder.profile.setImageResource(getTenants.getProfile_pic());
        holder.name.setText(getTenants.getName());
        holder.address.setText(getTenants.getAddress());
        holder.status.setText(getTenants.getStatus());
        holder.time.setText(getTenants.getDate());*/

        }

        @Override
        public int getItemCount() {
            return 15;
        }

        public  class MyViewHolder extends RecyclerView.ViewHolder {


            public MyViewHolder(View itemView) {
                super(itemView);

            }
        }
    }
}
