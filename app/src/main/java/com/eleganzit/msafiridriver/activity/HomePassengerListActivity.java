package com.eleganzit.msafiridriver.activity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.activities_from_register.RegisterVehicleDetailsActivity;
import com.eleganzit.msafiridriver.model.PassengerData;
import com.eleganzit.msafiridriver.model.SubPassengersData;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.eleganzit.msafiridriver.utils.SensorService;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class HomePassengerListActivity extends AppCompatActivity {
    RecyclerView passengers;
    ProgressBar progress;
    TextView no_passenger;
    private String trip_id;
    SharedPreferences p_pref;
    SharedPreferences.Editor p_editor;
    ImageView reload_passengers;

    ProgressDialog progressDialog;
    PassengerAdapter passengerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_passenger_list);

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Bungee.slideRight(HomePassengerListActivity.this);
            }
        });

        passengers=findViewById(R.id.passengers);

        progress=findViewById(R.id.progress);
        no_passenger=findViewById(R.id.no_passenger);
        reload_passengers = findViewById(R.id.reload_passengers);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");
        reload_passengers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPassengers();
            }
        });

        trip_id=getIntent().getStringExtra("trip_id");

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        passengers.setLayoutManager(layoutManager);

    }

    @Override
    protected void onDestroy() {

        //stopService(mServiceIntent);

        Log.i("wherreeeeeee", "onDestroy!");
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        p_pref=getSharedPreferences("passenger_pref",Context.MODE_PRIVATE);
        p_editor=p_pref.edit();
        //trip_id=p_pref.getString("trip_id","");

        Log.d("trip_status","activity Home PassengerList");

        getPassengers();

    }

    public void getPassengers()
    {
        progress.setVisibility(View.VISIBLE);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        progressDialog.show();
        reload_passengers.setVisibility(View.GONE);

        Log.d("ppppppppstringBuilder", "" + trip_id);

        myInterface.getPassengers(trip_id, new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                ArrayList<PassengerData> arrayList = new ArrayList<>();
                progress.setVisibility(View.GONE);
                progressDialog.dismiss();

                final StringBuilder stringBuilder = new StringBuilder();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("ppppppppstringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {

                            passengers.setVisibility(View.VISIBLE);
                            /*if(from.equalsIgnoreCase("home"))
                            {
                                start.setVisibility(View.GONE);
                                top.setVisibility(View.GONE);
                            }
                            else
                            {
                                start.setVisibility(View.VISIBLE);
                                top.setVisibility(View.VISIBLE);
                            }
*/
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                String id = jsonObject1.getString("id");
                                String user_id = jsonObject1.getString("user_id");
                                String rating = jsonObject1.getString("rating");
                                String rstatus = jsonObject1.getString("status");
                                String fname = jsonObject1.getString("fname");
                                String lname = jsonObject1.getString("lname");
                                String photo = jsonObject1.getString("photo");
                                String name;
                                name=fname+" "+lname;
                                JSONObject jsonObject2=jsonObject1.getJSONObject("passanger");
                                if(jsonObject2!=null || !jsonObject2.toString().equalsIgnoreCase(""))
                                {
                                    Log.d("tttttttttt","passenger not null");
                                    JSONArray jsonArray1=jsonObject2.getJSONArray("data");
                                    ArrayList<SubPassengersData> arrayList1=new ArrayList<>();
                                    String passanger_id ="";
                                    String passanger_name = "";
                                    String user_name = "";
                                    String bookedSeats = "";
                                    for (int j=0;j<jsonArray1.length();j++)
                                    {
                                        Log.d("tttttttttt","second for");
                                        JSONObject jsonObject3=jsonArray1.getJSONObject(j);
                                        passanger_id = jsonObject3.getString("passanger_id");
                                        passanger_name = jsonObject3.getString("passanger_name");
                                        String book_id = jsonObject3.getString("book_id");
                                        bookedSeats=String.valueOf(jsonArray1.length());
                                        if(j!=0)
                                        {
                                            SubPassengersData subPassengersData=new SubPassengersData(id,passanger_id,"","",passanger_name,"","");
                                            arrayList1.add(subPassengersData);
                                        }
                                        else
                                        {
                                            user_name=passanger_name+","+photo;
                                        }

                                    }

                                    PassengerData passengerData=new PassengerData(id,arrayList1,bookedSeats,user_id,rating,rstatus,user_name,lname,photo);
                                    arrayList.add(passengerData);

                                }
                                else
                                {
                                    Log.d("tttttttttt","pass null");
                                    PassengerData passengerData=new PassengerData(id,null,"",user_id,rating,rstatus,fname,lname,photo);
                                    arrayList.add(passengerData);
                                }

                            }
                            passengerAdapter=new PassengerAdapter(arrayList);
                            passengers.setAdapter(passengerAdapter);
                        }
                        else
                        {

                            no_passenger.setVisibility(View.VISIBLE);

                            passengers.setVisibility(View.GONE);

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(HomePassengerListActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progress.setVisibility(View.GONE);
                progressDialog.dismiss();

                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(HomePassengerListActivity.this, "Server or Internet Error", Toast.LENGTH_LONG).show();
                Log.d("errorrrr",""+error.getMessage());

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        passengerAdapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        passengerAdapter.onRestoreInstanceState(savedInstanceState);
    }

   /* public class PassengerAdapter extends ExpandableRecyclerViewAdapter<PassengerAdapter.MyViewHolder, PassengerAdapter.SubViewHolder>
    {
        Context context;
        boolean isSelectedAll;

        public PassengerAdapter(List<? extends ExpandableGroup> groups,Context context) {
            super(groups);
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(context).inflate(R.layout.passenger_list_layout,parent,false);

            MyViewHolder myViewHolder=new MyViewHolder(v);

            return myViewHolder;
        }

        @Override
        public MyViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(context).inflate(R.layout.passenger_list_layout,parent,false);

            MyViewHolder myViewHolder=new MyViewHolder(v);

            return myViewHolder;
        }

        @Override
        public SubViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(context).inflate(R.layout.sub_passenger_list_layout,parent,false);

            SubViewHolder subViewHolder=new SubViewHolder(v);

            return subViewHolder;
        }

        @Override
        public void onBindChildViewHolder(SubViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
            final SubPassengersData subPassengersData = ((PassengerData) group).getItems().get(childIndex);
            holder.setSubPassengerName(subPassengersData);
        }

        @Override
        public void onBindGroupViewHolder(MyViewHolder holder, int flatPosition, ExpandableGroup group) {
            final PassengerData passengerData=((PassengerData)group);

            Glide
                    .with(context)
                    .load(passengerData.getPhoto()).apply(new RequestOptions().placeholder(R.drawable.pr))
                    .into(holder.p_photo);

            holder.setPassengerName(group);

        }

        public  class MyViewHolder extends GroupViewHolder {

            CircleImageView p_photo;
            ImageView img_drop_down;
            TextView p_name;

            public MyViewHolder(View itemView) {
                super(itemView);
                p_photo=itemView.findViewById(R.id.p_photo);
                img_drop_down=itemView.findViewById(R.id.img_drop_down);
                p_name=itemView.findViewById(R.id.p_name);

            }

            public void setPassengerName(ExpandableGroup group) {
                p_name.setText(group.getTitle());
            }
        }

        public  class SubViewHolder extends ChildViewHolder {

            CircleImageView p_photo;
            TextView p_name;

            public SubViewHolder(View itemView) {
                super(itemView);
                p_photo=itemView.findViewById(R.id.p_photo);
                p_name=itemView.findViewById(R.id.p_name);

            }

            public void setSubPassengerName(SubPassengersData subPassengersData) {
                p_name.setText(subPassengersData.getFname());
            }

        }
    }
*/
    public class PassengerAdapter extends ExpandableRecyclerViewAdapter<PassengerAdapter.MyViewHolder, PassengerAdapter.SubViewHolder> {

        public PassengerAdapter(List<? extends ExpandableGroup> groups) {
            super(groups);
        }

        @Override
        public MyViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.passenger_list_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public SubViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sub_passenger_list_layout, parent, false);
            return new SubViewHolder(view);
        }

        @Override
        public void onBindChildViewHolder(SubViewHolder holder, int flatPosition,
                                          ExpandableGroup group, int childIndex) {

            final SubPassengersData artist = ((PassengerData) group).getItems().get(childIndex);
            holder.setSubPassengerName(artist);
        }

        @Override
        public void onBindGroupViewHolder(MyViewHolder holder, int flatPosition,
                                          ExpandableGroup group) {

            holder.setPassengerName(group);
        }

        public  class MyViewHolder extends GroupViewHolder {

            CircleImageView p_photo;
            ImageView img_drop_down;
            TextView p_name,seats_count;

            public MyViewHolder(View itemView) {
                super(itemView);
                p_photo=itemView.findViewById(R.id.p_photo);
                img_drop_down=itemView.findViewById(R.id.img_drop_down);
                p_name=itemView.findViewById(R.id.p_name);
                seats_count=itemView.findViewById(R.id.seats_count);

            }

            public void setPassengerName(ExpandableGroup group) {

                String currentString = group.getTitle();
                String[] separated = currentString.split(",");
                Log.d("photooooop",separated[1]+"");
                Glide
                        .with(HomePassengerListActivity.this)
                        .load(separated[1]).apply(new RequestOptions().placeholder(R.drawable.pr))
                        .into(p_photo);

                p_name.setText(separated[0].trim());
                if(group.getItems().size()==0)
                {
                    seats_count.setText((group.getItems().size()+1)+" seat");
                    img_drop_down.setVisibility(View.GONE);
                }
                else
                {
                    seats_count.setText((group.getItems().size()+1)+" seats");
                }

            }

            @Override
            public void expand() {
                super.expand();
                animateExpand();
            }

            @Override
            public void collapse() {
                super.collapse();
                animateCollapse();
            }

            private void animateExpand() {
                //Toast.makeText(HomePassengerListActivity.this, "expand", Toast.LENGTH_SHORT).show();
                /*RotateAnimation rotate =
                        new RotateAnimation(0, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(300);
                rotate.setFillAfter(true);*/
                img_drop_down.animate().rotation(180).setDuration(300);
            }

            private void animateCollapse() {
                //Toast.makeText(HomePassengerListActivity.this, "collapse", Toast.LENGTH_SHORT).show();
                /*RotateAnimation rotate =
                        new RotateAnimation(180, 0, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(300);
                rotate.setFillAfter(true);*/
                img_drop_down.animate().rotation(0).setDuration(300);
            }
        }

        public  class SubViewHolder extends ChildViewHolder {

            CircleImageView p_photo;
            TextView p_name;

            public SubViewHolder(View itemView) {
                super(itemView);
                p_photo=itemView.findViewById(R.id.p_photo);
                p_name=itemView.findViewById(R.id.p_name);

            }

            public void setSubPassengerName(SubPassengersData subPassengersData) {
                p_name.setText(subPassengersData.getFname());
            }

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(HomePassengerListActivity.this);

    }

}
