package com.eleganzit.msafiridriver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.eleganzit.msafiridriver.adapter.UpcomingTripAdapter;
import com.eleganzit.msafiridriver.adapter.VehicleImagesAdapter;
import com.eleganzit.msafiridriver.model.TripData;
import com.eleganzit.msafiridriver.model.VehicleData;
import com.eleganzit.msafiridriver.uploadImage.CallAPiActivity;
import com.eleganzit.msafiridriver.uploadImage.GetResponse;
import com.eleganzit.msafiridriver.utils.MyInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

public class VehicleDetailsActivity extends AppCompatActivity {

    String data="0";
    String id = "";
    Bitmap bitmap;
    FrameLayout num_add1,num_add2,num_add3,vehicle_add1,vehicle_add2,vehicle_add3;
    ImageView save,num_pic1,num_pic2,num_pic3,vehicle_pic1,vehicle_pic2,vehicle_pic3;
    com.eleganzit.msafiridriver.uploadMultupleImage.CallAPiActivity callAPiActivity;
    public static String URLUPDATEUSER;
    public static String URLUPDATEUSER2;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ArrayList<String> str_vehicle_array=new ArrayList<>();
    ArrayList<VehicleData> model_vehicle_array=new ArrayList<>();
    ArrayList<String> str_num_plate_array=new ArrayList<>();
    ArrayList<VehicleData> model_num_plate_array=new ArrayList<>();
    CircularProgressDrawable circularProgressDrawable;
    //ArrayList<String> images=new ArrayList<>();
    String mediapath = "";
    File file;
    ArrayList<String> demo=new ArrayList<>();
    EditText vehicle_name,vehicle_type,vehicle_number,seats;
    ProgressDialog progressDialog;
    RecyclerView rc_vehicle_images,rc_number_images;
    private String approvel;
    boolean upcomingTripsAreDone=false;
    boolean currentTripIsDone=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Bungee.slideRight(VehicleDetailsActivity.this);
            }
        });
        callAPiActivity = new com.eleganzit.msafiridriver.uploadMultupleImage.CallAPiActivity(VehicleDetailsActivity.this);
        URLUPDATEUSER = "http://itechgaints.com/M-safiri-API/addVehicledetail";
        URLUPDATEUSER2 = "http://itechgaints.com/M-safiri-API/updateVehicledetail";
        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();
        save=findViewById(R.id.save);
        rc_vehicle_images=findViewById(R.id.rc_vehicle_images);
        rc_number_images=findViewById(R.id.rc_number_images);

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(VehicleDetailsActivity.this,LinearLayoutManager.HORIZONTAL,false);
        RecyclerView.LayoutManager layoutManager2=new LinearLayoutManager(VehicleDetailsActivity.this,LinearLayoutManager.HORIZONTAL,false);
        rc_vehicle_images.setLayoutManager(layoutManager);
        rc_number_images.setLayoutManager(layoutManager2);

        num_add1=findViewById(R.id.num_add1);
        num_add2=findViewById(R.id.num_add2);
        num_add3=findViewById(R.id.num_add3);
        vehicle_add1=findViewById(R.id.vehicle_add1);
        vehicle_add2=findViewById(R.id.vehicle_add2);
        vehicle_add3=findViewById(R.id.vehicle_add3);
        num_pic1=findViewById(R.id.num_pic1);
        num_pic2=findViewById(R.id.num_pic2);
        num_pic3=findViewById(R.id.num_pic3);
        vehicle_pic1=findViewById(R.id.vehicle_pic1);
        vehicle_pic2=findViewById(R.id.vehicle_pic2);
        vehicle_pic3=findViewById(R.id.vehicle_pic3);
        vehicle_name=findViewById(R.id.vehicle_name);
        vehicle_type=findViewById(R.id.vehicle_type);
        vehicle_number=findViewById(R.id.vehicle_number);
        seats=findViewById(R.id.seats);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(4f);
        circularProgressDrawable.setCenterRadius(15f);
        circularProgressDrawable.start();
        getVehicledata();

        num_add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser(2);
            }
        });
        num_add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser(3);
            }
        });
        num_add3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser(4);
            }
        });
        vehicle_add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser(5);
            }
        });
        vehicle_add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser(6);
            }
        });
        vehicle_add3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser(7);
            }
        });

        vehicle_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog(VehicleDetailsActivity.this);
                dialog.setContentView(R.layout.vehicle_type_dialog);
                final TextView car=dialog.findViewById(R.id.car);
                final TextView van=dialog.findViewById(R.id.van);
                final TextView bike=dialog.findViewById(R.id.bike);

                car.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vehicle_type.setText("Car");
                        dialog.dismiss();
                    }
                });

                van.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vehicle_type.setText("Van");
                        dialog.dismiss();
                    }
                });

                bike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vehicle_type.setText("Bike");
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
                if(str_vehicle_array.size()==0)
                {
                    Toast.makeText(VehicleDetailsActivity.this, "Please upload vehicle images", Toast.LENGTH_SHORT).show();
                }
                else if(str_num_plate_array.size()==0)
                {
                    Toast.makeText(VehicleDetailsActivity.this, "Please upload number plate images", Toast.LENGTH_SHORT).show();
                }
                else if(vehicle_name.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(vehicle_name);
                    vehicle_name.setError("Please enter vehicle name");
                    vehicle_name.requestFocus();

                }
                else if(vehicle_type.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(vehicle_type);
                    vehicle_type.requestFocus();
                    vehicle_type.setError("Please enter vehicle type");
                }
                else if(vehicle_number.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(vehicle_number);
                    vehicle_number.requestFocus();
                    vehicle_number.setError("Please enter number plate");
                }
                else if(seats.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(seats);
                    seats.requestFocus();
                    seats.setError("Please select number of seats");
                }
                else
                {
                    /*if(data.equalsIgnoreCase("0"))
                    {
                        Log.d("whereeeeee","iffff   vehicle"+str_vehicle_array+"  numuberr "+str_num_plate_array);
                        uploadVehicle();
                    }
                    else
                    {*/
                        Log.d("whereeeeee","else   vehicle"+str_vehicle_array+"  numuberr "+str_num_plate_array);
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
                        Toast.makeText(VehicleDetailsActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(VehicleDetailsActivity.this, "Couldn't refresh trips", Toast.LENGTH_SHORT).show();

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
                            new AlertDialog.Builder(VehicleDetailsActivity.this).setMessage("Are you sure you want to save the changes?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            updateVehicle();
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
                            //Toast.makeText(VehicleDetailsActivity.this, "You cannot update profile if you have any trips remaining!", Toast.LENGTH_LONG).show();
                            new AlertDialog.Builder(VehicleDetailsActivity.this).setMessage("You cannot update profile if you have any trips remaining!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                        Toast.makeText(VehicleDetailsActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(VehicleDetailsActivity.this, "Couldn't refresh trips", Toast.LENGTH_SHORT).show();

            }
        });
    }


    void openImageChooser(int SELECT_PICTURE) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg|image/jpg|image/png");

        Intent chooser = Intent.createChooser(intent, "Choose a Picture");
        startActivityForResult(chooser, SELECT_PICTURE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {

            if (requestCode == 4) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getApplicationContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int clumnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediapath = cursor.getString(clumnIndex);

                str_num_plate_array.add(mediapath);
                VehicleData vehicleData=new VehicleData("","proof",mediapath);
                model_num_plate_array.add(vehicleData);
                rc_number_images.setAdapter(new VehicleImagesAdapter2(str_num_plate_array,model_num_plate_array,VehicleDetailsActivity.this));

                Log.d("file_size", "mediapath : " + mediapath + " ----num_pic3 ");
                Log.d("num_picssssss",""+str_num_plate_array);
                Log.d("num_picssssss",""+model_num_plate_array.size());

            }
            if (requestCode == 7) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getApplicationContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int clumnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediapath = cursor.getString(clumnIndex);

                str_vehicle_array.add(mediapath);
                VehicleData vehicleData=new VehicleData("","licence",mediapath);
                model_vehicle_array.add(vehicleData);
                rc_vehicle_images.setAdapter(new VehicleImagesAdapter(str_vehicle_array,model_vehicle_array,VehicleDetailsActivity.this));

                Log.d("file_size", "mediapath : " + mediapath + " ---- num_pic1");
                Log.d("imagesssssss",""+model_vehicle_array.size());

            }
        }
        if (resultCode==RESULT_CANCELED)
        {

        }

    }

    private void uploadVehicle() {

        progressDialog.show();

            HashMap<String, String> map = new HashMap<>();
            Log.d("iddddd",pref.getString("driver_id", ""));
            map.put("driver_id", pref.getString("driver_id", ""));
            map.put("vehicle_name", vehicle_name.getText().toString());
            map.put("vehicle_type", vehicle_type.getText().toString());
            map.put("vehicle_number", vehicle_number.getText().toString());
            map.put("seats", seats.getText().toString());
            callAPiActivity.doPostWithFiles(VehicleDetailsActivity.this, map, URLUPDATEUSER, str_vehicle_array, "vehicle_photo[]", str_num_plate_array, "numberplate_photo[]", new com.eleganzit.msafiridriver.uploadMultupleImage.GetResponse() {

                @Override
                public void onSuccesResult(JSONObject result) throws JSONException {

                    String status = result.getString("status");
                    if(status.equalsIgnoreCase("success"))
                    {
                        editor.putString("vehicleInfo", "filled");
                        editor.putString("vehicle_name",vehicle_name.getText().toString());
                        editor.commit();
                        Toast.makeText(VehicleDetailsActivity.this, "successfully uploaded", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    Log.d("messageeeeeeeeeee","succccccccessss"+status);
                }

                @Override
                public void onFailureResult(String message) {
                    progressDialog.dismiss();
                    if(message.equalsIgnoreCase("success"))
                    {
                        editor.putString("vehicleInfo", "filled");
                        editor.putString("vehicle_name",vehicle_name.getText().toString());
                        editor.commit();
                        Toast.makeText(VehicleDetailsActivity.this, "successfully uploaded", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    Log.d("messageeeeeeeeeee",message);

                }
            });


    }

    public class VehicleImagesAdapter extends RecyclerView.Adapter<VehicleImagesAdapter.MyViewHolder>
    {
        ArrayList<VehicleData> arrayList;
        ArrayList<String> arrayList2;
        Context context;

        public  VehicleImagesAdapter(ArrayList<String> str_vehicle_array,ArrayList<VehicleData> model_vehicle_array, Context context) {
            this.arrayList = model_vehicle_array;
            this.arrayList2 = str_vehicle_array;
            this.context = context;
        }

        @Override
        public VehicleImagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_img_layout,parent,false);
            VehicleImagesAdapter.MyViewHolder myViewHolder=new VehicleImagesAdapter.MyViewHolder(v);

            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(VehicleImagesAdapter.MyViewHolder holder, final int position) {

            final VehicleData vehicleData=arrayList.get(position);

            Glide
                    .with(context)
                    .load(arrayList2.get(position))
                    .apply(new RequestOptions().centerCrop().placeholder(circularProgressDrawable)).into(holder.image);

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url=vehicleData.getPhoto();


                    if(arrayList.size()==1)
                    {
                        Toast.makeText(context, "Minimum 1 photo is required", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(Patterns.WEB_URL.matcher(url).matches())
                        {
                            removePhoto(vehicleData.getPhotoId(),position);
                        }
                        else
                        {
                            removeAt(position);
                        }
                    }

                }
            });

        }
        public void removeAt(int position) {
            arrayList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, arrayList.size());
            model_vehicle_array=arrayList;
            arrayList2.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, arrayList2.size());
            str_vehicle_array=arrayList2;
        }
        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView image,remove;
            public MyViewHolder(View itemView) {
                super(itemView);
                image=itemView.findViewById(R.id.image);
                remove=itemView.findViewById(R.id.remove);

            }

        }

        public void removePhoto(String id, final int position)
        {
            if(context!=null) {
                progressDialog.show();
            }
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
            final MyInterface myInterface = restAdapter.create(MyInterface.class);
            myInterface.removePhoto(id, new retrofit.Callback<retrofit.client.Response>() {
                @Override
                public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                    if(context!=null) {
                        progressDialog.dismiss();
                    }

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
                                removeAt(position);

                                //Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                 Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                            }



                        }
                        else
                        {

                            Toast.makeText(context, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                        }


                    } catch (IOException e) {

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    if(context!=null) {
                        progressDialog.dismiss();
                    }
                    //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    }


    public class VehicleImagesAdapter2 extends RecyclerView.Adapter<VehicleImagesAdapter2.MyViewHolder>
    {
        ArrayList<VehicleData> arrayList;
        ArrayList<String> arrayList2;
        Context context;

        public VehicleImagesAdapter2(ArrayList<String> str_num_plate_array,ArrayList<VehicleData> model_num_plate_array, Context context) {
            this.arrayList = model_num_plate_array;
            this.arrayList2 = str_num_plate_array;
            this.context = context;
        }

        @Override
        public VehicleImagesAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_img_layout,parent,false);
            VehicleImagesAdapter2.MyViewHolder myViewHolder=new VehicleImagesAdapter2.MyViewHolder(v);

            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(VehicleImagesAdapter2.MyViewHolder holder, final int position) {

            final VehicleData vehicleData=arrayList.get(position);
            Log.d("removeeeeeed","pele "+model_num_plate_array.size()+"   "+arrayList.size());

            Log.d("ssimageeeeee","eeeee "+vehicleData.getPhoto()+"     "+arrayList2.get(position));
            Glide
                    .with(context)
                    .load(arrayList2.get(position))
                    .apply(new RequestOptions().centerCrop()).into(holder.image);

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url=vehicleData.getPhoto();
                    if(arrayList.size()==1)
                    {
                        Toast.makeText(context, "Minimum 1 photo is required", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (Patterns.WEB_URL.matcher(url).matches()) {
                            removePhoto(vehicleData.getPhotoId(), position);

                        } else {
                            removeAt(position);
                        }
                    }

                }
            });

        }
        public void removeAt(int position) {
            arrayList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, arrayList.size());
            model_num_plate_array=arrayList;
            arrayList2.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, arrayList2.size());
            str_num_plate_array=arrayList2;
            Log.d("removeeeeeed"," baadme"+model_num_plate_array.size()+"   "+arrayList.size());
        }
        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView image,remove;
            public MyViewHolder(View itemView) {
                super(itemView);
                image=itemView.findViewById(R.id.image);
                remove=itemView.findViewById(R.id.remove);

            }

        }

        public void removePhoto(String id, final int position)
        {
            if(context!=null) {
                progressDialog.show();
            }
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
            final MyInterface myInterface = restAdapter.create(MyInterface.class);
            myInterface.removePhoto(id, new retrofit.Callback<retrofit.client.Response>() {
                @Override
                public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                    if(context!=null) {
                        progressDialog.dismiss();
                    }
                    ArrayList<VehicleData> arrayList=new ArrayList<>();
                    ArrayList<VehicleData> arrayList2=new ArrayList<>();
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
                                removeAt(position);
                                //Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                            }



                        }
                        else
                        {

                            Toast.makeText(context, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                        }


                    } catch (IOException e) {

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    if(context!=null) {
                        progressDialog.dismiss();
                    }
                    //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    private void updateVehicle() {

        progressDialog.show();

        /*for(int i=0;i<str_vehicle_array.size();i++)
        {
            if(str_vehicle_array.get(i).contains("http"))
            {
                str_vehicle_array.remove(i);
            }
        }*/
        Iterator<String> iterator = str_vehicle_array.iterator();
        while(iterator.hasNext())
        {
            String value = iterator.next();
            if (value.contains("http"))
            {
                iterator.remove();
                // break;
            }
        }
       /* for(int i=0;i<str_num_plate_array.size();i++)
        {
            Log.d("forifff",i+"   forrr   "+str_num_plate_array.get(i));
            if(str_num_plate_array.get(i).contains("http"))
            {
                Log.d("forifff",i+"   iffff   "+str_num_plate_array.get(i));
                str_num_plate_array.remove(i);
            }
        }*/
        Iterator<String> iterator2 = str_num_plate_array.iterator();
        while(iterator2.hasNext())
        {
            String value = iterator2.next();
            if (value.contains("http"))
            {
                iterator2.remove();
                // break;
            }
        }
        Log.d("whereeeeee","innnnnnn   vehicle"+str_vehicle_array+"  numuberr "+str_num_plate_array);


        Log.d("arrraaaayyyy",str_vehicle_array.size()+"    "+str_num_plate_array.size());

        HashMap<String, String> map = new HashMap<>();
        Log.d("iddddd",pref.getString("driver_id", ""));
        map.put("driver_id", pref.getString("driver_id", ""));
        map.put("vehicle_id", id);
        map.put("vehicle_name", vehicle_name.getText().toString());

        map.put("vehicle_type", vehicle_type.getText().toString());
        map.put("vehicle_number", vehicle_number.getText().toString());
        map.put("seats", seats.getText().toString());
        callAPiActivity.doPostWithFiles(VehicleDetailsActivity.this, map, URLUPDATEUSER2, str_vehicle_array, "vehicle_photo[]", str_num_plate_array, "numberplate_photo[]", new com.eleganzit.msafiridriver.uploadMultupleImage.GetResponse() {

            @Override
            public void onSuccesResult(JSONObject result) throws JSONException {

                String status = result.getString("status");
                if(status.equalsIgnoreCase("1"))
                {
                    updateApprovalStatus();

                    editor.putString("vehicle_name",vehicle_name.getText().toString());
                    editor.commit();

                }
                else
                {
                    Toast.makeText(VehicleDetailsActivity.this, ""+result.getString("message"), Toast.LENGTH_SHORT).show();
                }

                Log.d("messageeeeeeeeeee","succccccccessss"+status);
            }

            @Override
            public void onFailureResult(String message) {
                progressDialog.dismiss();
                if(message.equalsIgnoreCase("success"))
                {
                    //updateApprovalStatus();

                    editor.putString("vehicle_name",vehicle_name.getText().toString());
                    editor.commit();
                    Toast.makeText(VehicleDetailsActivity.this, "successfully uploaded", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(VehicleDetailsActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                }
                Log.d("messageeeeeeeeeee",message);

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


                                        Toast.makeText(VehicleDetailsActivity.this, "Please submit your profile for approval", Toast.LENGTH_LONG).show();
                                        finish();
                                    }

                                }
                                else
                                {
                                    Toast.makeText(VehicleDetailsActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                Toast.makeText(VehicleDetailsActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(VehicleDetailsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void getVehicledata()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getVehicledata(pref.getString("driver_id",""), new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                progressDialog.dismiss();
                ArrayList<VehicleData> arrayList=new ArrayList<>();
                ArrayList<String> sarrayList=new ArrayList<>();
                ArrayList<VehicleData> arrayList2=new ArrayList<>();
                ArrayList<String> sarrayList2=new ArrayList<>();
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
                            data="1";
                            String photo_type;
                            String photo;
                            String vehicle_namee = "";
                            String photo_id = "";
                            String vehicle_typee="";
                            String vehicle_numberr="";
                            String seatss="";
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                photo_type = jsonObject1.getString("photo_type");
                                photo = jsonObject1.getString("photo");
                                id = jsonObject1.getString("id");
                                photo_id = jsonObject1.getString("vehicle_image_id");
                                vehicle_namee = jsonObject1.getString("vehicle_name");
                                vehicle_typee = jsonObject1.getString("vehicle_type");
                                vehicle_numberr = jsonObject1.getString("vehicle_number");
                                seatss = jsonObject1.getString("seats");

                                VehicleData vehicleData=new VehicleData(photo_id,photo_type,photo);
                                if(photo_type.equalsIgnoreCase(""))
                                {

                                }
                                else if(photo_type.equalsIgnoreCase("photo"))
                                {
                                    arrayList.add(vehicleData);
                                    sarrayList.add(photo);
                                }
                                else
                                {
                                    arrayList2.add(vehicleData);
                                    sarrayList2.add(photo);
                                }

                            }
                            vehicle_name.setText(vehicle_namee);
                            vehicle_type.setText(vehicle_typee);
                            vehicle_number.setText(vehicle_numberr);
                            seats.setText(seatss);
                            rc_vehicle_images.setAdapter(new VehicleImagesAdapter(sarrayList,arrayList,VehicleDetailsActivity.this));
                            model_vehicle_array=arrayList;
                            str_vehicle_array=sarrayList;
                            rc_number_images.setAdapter(new VehicleImagesAdapter2(sarrayList2,arrayList2,VehicleDetailsActivity.this));
                            model_num_plate_array=arrayList2;
                            str_num_plate_array=sarrayList2;
                        }
                        else
                        {
                            data="0";
                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(VehicleDetailsActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(VehicleDetailsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void onSelectFromGalleryResult(Intent data,ImageView profile_pic) {
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (bitmap!=null)
        {

            Glide
                    .with(VehicleDetailsActivity.this)
                    .load(data.getData())
                    .into(profile_pic);

        }
        else
        {
            Toast.makeText(this, "Image format not proper", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(VehicleDetailsActivity.this);
    }
}
