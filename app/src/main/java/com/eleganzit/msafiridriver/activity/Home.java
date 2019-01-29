package com.eleganzit.msafiridriver.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.eleganzit.msafiridriver.PickupLocation;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.fragment.AccountFragment;
import com.eleganzit.msafiridriver.fragment.HistoryFragment;
import com.eleganzit.msafiridriver.fragment.HomeFragment;
import com.eleganzit.msafiridriver.fragment.RouteFragment;
import com.eleganzit.msafiridriver.fragment.TripFragment;

import java.util.ArrayList;
import java.util.List;

import static android.view.Window.FEATURE_NO_TITLE;

public class Home extends AppCompatActivity  {
    List<String> list = new ArrayList<String>();
    FrameLayout frame;

    LinearLayout home,trip,route,history,account;
    ImageView imghome,imgtrip,imghistory,imgaccount;
    TextView txthome,txttrip,txthistory,txtaccount;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor  editor;
    String data="";
    ArrayList<String> getTenantses=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(Home.this,new String[]{android.Manifest.permission.CAMERA},1);

        setContentView(R.layout.activity_dummy);

        frame=findViewById(R.id.frame);
        sharedPreferences=getSharedPreferences("pref",MODE_PRIVATE);
        data=sharedPreferences.getString("key","");
        editor=sharedPreferences.edit();
        home=findViewById(R.id.homeclick);
        imghome=findViewById(R.id.home);
        txthome=findViewById(R.id.txthome);
        trip=findViewById(R.id.tripclick);
        imgtrip=findViewById(R.id.trip);
        txttrip=findViewById(R.id.txttrip);
        route=findViewById(R.id.routeclick);
        history=findViewById(R.id.historyclick);
        imghistory=findViewById(R.id.history);
        txthistory=findViewById(R.id.txthistory);
        account=findViewById(R.id.accountclick);
        imgaccount=findViewById(R.id.account);
        txtaccount=findViewById(R.id.txtaccount);
        imghome.setImageResource(R.drawable.homeblue);
        txthome.setTextColor(Color.parseColor("#0192D2"));
        imgtrip.setImageResource(R.drawable.carwhite);
        txttrip.setTextColor(Color.parseColor("#ffffff"));
        imghistory.setImageResource(R.drawable.historywhite);
        txthistory.setTextColor(Color.parseColor("#ffffff"));
        imgaccount.setImageResource(R.drawable.userwhite);
        txtaccount.setTextColor(Color.parseColor("#ffffff"));
        if(data.equalsIgnoreCase("save"))
        {
            final Dialog dialog=new Dialog(Home.this);

            dialog.getWindow().requestFeature(FEATURE_NO_TITLE);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            dialog.setContentView(R.layout.custom_popup);
            RecyclerView route=dialog.findViewById(R.id.route);
            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(Home.this, LinearLayoutManager.VERTICAL,false);
            route.setLayoutManager(layoutManager);
            TextView cancel=dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    editor.clear();
                    editor.commit();
                }
            });
            TextView add=dialog.findViewById(R.id.add);
            route.setAdapter(new TenantsAdapter(getTenantses,Home.this));
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Home.this,PickupLocation.class).putExtra("from","add"));
                    dialog.dismiss();
                    editor.clear();
                    editor.commit();
                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.setCancelable(false);
            dialog.show();
        }

    route.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(Home.this,PickupLocation.class));

//        final Dialog dialog=new Dialog(Home.this);
//
//        dialog.getWindow().requestFeature(FEATURE_NO_TITLE);
//        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
//        dialog.setContentView(R.layout.custom_popup);
//        RecyclerView route=dialog.findViewById(R.id.route);
//        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(Home.this, LinearLayoutManager.VERTICAL,false);
//        route.setLayoutManager(layoutManager);
//        TextView cancel=dialog.findViewById(R.id.cancel);
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        TextView add=dialog.findViewById(R.id.add);
//        route.setAdapter(new TenantsAdapter(getTenantses,Home.this));
//        add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Home.this,PickupLocation.class));
//                dialog.dismiss();
//            }
//        });
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        dialog.setCancelable(false);
//        dialog.show();
    }
});
        HomeFragment homeFrag= new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, homeFrag,"HomeFragment")
                .commit();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment homeFrag= new HomeFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, homeFrag,"HomeFragment")
                        .addToBackStack(null)
                        .commit();
                imghome.setImageResource(R.drawable.homeblue);
                txthome.setTextColor(Color.parseColor("#0192D2"));
                imgtrip.setImageResource(R.drawable.carwhite);
                txttrip.setTextColor(Color.parseColor("#ffffff"));
                imghistory.setImageResource(R.drawable.historywhite);
                txthistory.setTextColor(Color.parseColor("#ffffff"));
                imgaccount.setImageResource(R.drawable.userwhite);
                txtaccount.setTextColor(Color.parseColor("#ffffff"));
            }
        });

        trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TripFragment tripFrag= new TripFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, tripFrag,"TripFragment")
                        .addToBackStack(null)
                        .commit();
                imghome.setImageResource(R.drawable.homewhite);
                txthome.setTextColor(Color.parseColor("#ffffff"));
                imgtrip.setImageResource(R.drawable.carblue);
                txttrip.setTextColor(Color.parseColor("#0192D2"));
                imghistory.setImageResource(R.drawable.historywhite);
                txthistory.setTextColor(Color.parseColor("#ffffff"));
                imgaccount.setImageResource(R.drawable.userwhite);
                txtaccount.setTextColor(Color.parseColor("#ffffff"));
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HistoryFragment historyFrag= new HistoryFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, historyFrag,"HistoryFragment")
                        .addToBackStack(null)
                        .commit();
                imghome.setImageResource(R.drawable.homewhite);
                txthome.setTextColor(Color.parseColor("#ffffff"));
                imgtrip.setImageResource(R.drawable.carwhite);
                txttrip.setTextColor(Color.parseColor("#ffffff"));
                imghistory.setImageResource(R.drawable.historyblue);
                txthistory.setTextColor(Color.parseColor("#0192D2"));
                imgaccount.setImageResource(R.drawable.userwhite);
                txtaccount.setTextColor(Color.parseColor("#ffffff"));
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountFragment accountFrag = new AccountFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, accountFrag,"AccountFragment")
                        .addToBackStack(null)
                        .commit();
                imghome.setImageResource(R.drawable.homewhite);
                txthome.setTextColor(Color.parseColor("#ffffff"));
                imgtrip.setImageResource(R.drawable.carwhite);
                txttrip.setTextColor(Color.parseColor("#ffffff"));
                imghistory.setImageResource(R.drawable.historywhite);
                txthistory.setTextColor(Color.parseColor("#ffffff"));
                imgaccount.setImageResource(R.drawable.userblue);
                txtaccount.setTextColor(Color.parseColor("#0192D2"));
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
        public TenantsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(context).inflate(R.layout.route_history_layout,parent,false);

            TenantsAdapter.MyViewHolder myViewHolder=new TenantsAdapter.MyViewHolder(v);

            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(TenantsAdapter.MyViewHolder holder, int position) {

            holder.radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context,DemoScreen.class));
                }
            });
      /*  GetTenants getTenants=arrayList.get(position);
        holder.profile.setImageResource(getTenants.getProfile_pic());
        holder.name.setText(getTenants.getName());
        holder.address.setText(getTenants.getAddress());
        holder.status.setText(getTenants.getStatus());
        holder.time.setText(getTenants.getDate());*/

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public  class MyViewHolder extends RecyclerView.ViewHolder {

            LinearLayout radio;
            public MyViewHolder(View itemView) {
                super(itemView);
                radio=itemView.findViewById(R.id.radio);

            }
        }
    }

}
