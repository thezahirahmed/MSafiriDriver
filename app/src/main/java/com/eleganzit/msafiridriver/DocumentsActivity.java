package com.eleganzit.msafiridriver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.eleganzit.msafiridriver.adapter.VehicleImagesAdapter;
import com.eleganzit.msafiridriver.model.VehicleData;
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

public class DocumentsActivity extends AppCompatActivity {

    Bitmap bitmap;
    FrameLayout lic_add,num_add1,num_add2,num_add3,proof_add1,proof_add2,proof_add3;
    ImageView save,lic_pic,num_pic1,num_pic2,num_pic3,proof_pic1,proof_pic2,proof_pic3;
    RecyclerView rc_licence_images,rc_number_images,rc_ad_proof_images;
    private String mediapath;

    com.eleganzit.msafiridriver.uploadMultupleImage.CallAPiActivity callAPiActivity;
    public static String URLUPDATEUSER;
    public static String URLUPDATEUSER2;
    ProgressDialog progressDialog;
    CircularProgressDrawable circularProgressDrawable;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<String> str_licence_array=new ArrayList<>();
    ArrayList<VehicleData> model_licence_array=new ArrayList<>();
    ArrayList<String> str_proof_array=new ArrayList<>();
    ArrayList<VehicleData> model_proof_array=new ArrayList<>();

    private String id;
    private String hasDocs="no";
    private String approvel;
    boolean upcomingTripsAreDone=false;
    boolean currentTripIsDone=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Bungee.slideRight(DocumentsActivity.this);
            }
        });
        callAPiActivity = new com.eleganzit.msafiridriver.uploadMultupleImage.CallAPiActivity(DocumentsActivity.this);

        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();
        URLUPDATEUSER = "http://itechgaints.com/M-safiri-API/drvierDocument";
        //URLUPDATEUSER2 = "http://itechgaints.com/M-safiri-API/updateVehicledetail";
        rc_licence_images=findViewById(R.id.rc_licence_images);
        rc_number_images=findViewById(R.id.rc_number_images);
        rc_ad_proof_images=findViewById(R.id.rc_ad_proof_images);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(4f);
        circularProgressDrawable.setCenterRadius(15f);
        circularProgressDrawable.start();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(DocumentsActivity.this,LinearLayoutManager.HORIZONTAL,false);
        RecyclerView.LayoutManager layoutManager2=new LinearLayoutManager(DocumentsActivity.this,LinearLayoutManager.HORIZONTAL,false);
        RecyclerView.LayoutManager layoutManager3=new LinearLayoutManager(DocumentsActivity.this,LinearLayoutManager.HORIZONTAL,false);
        rc_licence_images.setLayoutManager(layoutManager);
        rc_number_images.setLayoutManager(layoutManager2);
        rc_ad_proof_images.setLayoutManager(layoutManager3);
        lic_add=findViewById(R.id.lic_add);
        num_add1=findViewById(R.id.num_add1);
        num_add2=findViewById(R.id.num_add2);
        num_add3=findViewById(R.id.num_add3);
        proof_add1=findViewById(R.id.proof_add1);
        proof_add2=findViewById(R.id.proof_add2);
        proof_add3=findViewById(R.id.proof_add3);
        lic_pic=findViewById(R.id.lic_pic);
        num_pic1=findViewById(R.id.num_pic1);
        num_pic2=findViewById(R.id.num_pic2);
        num_pic3=findViewById(R.id.num_pic3);
        proof_pic1=findViewById(R.id.proof_pic1);
        proof_pic2=findViewById(R.id.proof_pic2);
        proof_pic3=findViewById(R.id.proof_pic3);
        save=findViewById(R.id.save);

        lic_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser(1);
            }
        });
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
        proof_add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser(5);
            }
        });
        proof_add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser(6);
            }
        });
        proof_add3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser(7);
            }
        });
        getDriverdocuments();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(str_licence_array.size()==0)
                {
                    Toast.makeText(DocumentsActivity.this, "Please upload driving licence", Toast.LENGTH_SHORT).show();
                }
                else if(str_proof_array.size()==0)
                {
                    Toast.makeText(DocumentsActivity.this, "Please upload address proof", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    getUpcomingTrips();
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
                        Toast.makeText(DocumentsActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DocumentsActivity.this, "Couldn't refresh trips", Toast.LENGTH_SHORT).show();

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
                            new AlertDialog.Builder(DocumentsActivity.this).setMessage("Are you sure you want to save the changes?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            uploadDocument();
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
                            //Toast.makeText(DocumentsActivity.this, "You cannot update profile if you have any trips remaining!", Toast.LENGTH_LONG).show();
                            new AlertDialog.Builder(DocumentsActivity.this).setMessage("You cannot update profile if you have any trips remaining!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                        Toast.makeText(DocumentsActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DocumentsActivity.this, "Couldn't refresh trips", Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();


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

            if (requestCode == 1) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getApplicationContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int clumnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediapath = cursor.getString(clumnIndex);

                str_licence_array.add(mediapath);
                VehicleData vehicleData=new VehicleData("","photo",mediapath);
                model_licence_array.add(vehicleData);
                rc_licence_images.setAdapter(new VehicleImagesAdapter(str_licence_array,model_licence_array,DocumentsActivity.this));

                Log.d("file_size", "mediapath : " + mediapath + " ---- num_pic1");
                Log.d("imagesssssss",""+str_licence_array);

            }

            if (requestCode == 5) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getApplicationContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int clumnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediapath = cursor.getString(clumnIndex);

                str_proof_array.add(mediapath);
                VehicleData vehicleData=new VehicleData("","photo",mediapath);
                model_proof_array.add(vehicleData);
                rc_ad_proof_images.setAdapter(new VehicleImagesAdapter2(str_proof_array,model_proof_array,DocumentsActivity.this));

                Log.d("file_size", "mediapath : " + mediapath + " ---- num_pic1");
                Log.d("imagesssssss",""+str_proof_array);

            }

        }
        if (resultCode==RESULT_CANCELED)
        {

        }

    }

    public void getDriverdocuments()
    {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getDriverdocuments(pref.getString("driver_id",""), new retrofit.Callback<retrofit.client.Response>() {
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
                    Log.d("docsstringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {

                            hasDocs="yes";
                            String photo_type;
                            String photo;
                            String photo_id;
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                photo_type = jsonObject1.getString("photo_type");
                                photo = jsonObject1.getString("photo");
                                photo_id = jsonObject1.getString("id");

                                VehicleData vehicleData=new VehicleData(photo_id,photo_type,photo);
                                if(photo_type.equalsIgnoreCase(""))
                                {

                                }
                                else if(photo_type.equalsIgnoreCase("licence"))
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

                            rc_licence_images.setAdapter(new VehicleImagesAdapter(sarrayList,arrayList,DocumentsActivity.this));
                            model_licence_array=arrayList;
                            str_licence_array=sarrayList;
                            rc_ad_proof_images.setAdapter(new VehicleImagesAdapter2(sarrayList2,arrayList2,DocumentsActivity.this));
                            model_proof_array=arrayList2;
                            str_proof_array=sarrayList2;
                        }
                        else
                        {

                        }

                    }
                    else
                    {

                        Toast.makeText(DocumentsActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DocumentsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public class VehicleImagesAdapter extends RecyclerView.Adapter<VehicleImagesAdapter.MyViewHolder>
    {
        ArrayList<VehicleData> arrayList;
        ArrayList<String> arrayList2;
        Context context;

        public  VehicleImagesAdapter(ArrayList<String> str_licence_array,ArrayList<VehicleData> model_licence_array, Context context) {
            this.arrayList = model_licence_array;
            this.arrayList2 = str_licence_array;
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_img_layout,parent,false);
            MyViewHolder myViewHolder=new MyViewHolder(v);

            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {

            final VehicleData vehicleData=arrayList.get(position);

            Toast.makeText(context, ""+arrayList.get(position).getPhoto_type(), Toast.LENGTH_SHORT).show();

            if(arrayList.get(position).getPhoto_type().equalsIgnoreCase(""))
            {

            }
            else
            {
                Glide
                        .with(context)
                        .load(arrayList2.get(position))
                        .apply(new RequestOptions().centerCrop().placeholder(circularProgressDrawable)).into(holder.image);
            }

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url=vehicleData.getPhoto();

                    if(arrayList.size()==1) {
                        Toast.makeText(context, "Minimum 1 document is required", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
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
            model_licence_array=arrayList;
            arrayList2.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, arrayList2.size());
            str_licence_array=arrayList2;
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
            myInterface.deleteDocuments(id, new retrofit.Callback<retrofit.client.Response>() {
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

        public VehicleImagesAdapter2(ArrayList<String> str_proof_array,ArrayList<VehicleData> model_proof_array, Context context) {
            this.arrayList = model_proof_array;
            this.arrayList2 = str_proof_array;
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_img_layout,parent,false);
            MyViewHolder myViewHolder=new MyViewHolder(v);

            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {

            final VehicleData vehicleData=arrayList.get(position);

            Log.d("removeeeeeed","pele "+model_proof_array.size()+"   "+arrayList.size());

            Log.d("ssimageeeeee","eeeee "+vehicleData.getPhoto());

            if(arrayList.get(position).getPhoto_type().equalsIgnoreCase(""))
            {
                //Toast.makeText(context, "if  prooof "+arrayList.get(position).getPhoto_type(), Toast.LENGTH_SHORT).show();
            }
            else
                {
                  //  Toast.makeText(context, "else  prooof "+arrayList.get(position).getPhoto_type(), Toast.LENGTH_SHORT).show();
                Glide
                        .with(context)
                        .load(arrayList2.get(position))
                        .apply(new RequestOptions().centerCrop().placeholder(circularProgressDrawable)).into(holder.image);
            }
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url=vehicleData.getPhoto();

                    if(arrayList.size()==1) {
                        Toast.makeText(context, "Minimum 1 document is required", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
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
            model_proof_array=arrayList;
            arrayList2.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, arrayList2.size());
            str_proof_array=arrayList2;
            Log.d("removeeeeeed"," baadme"+model_proof_array.size()+"   "+arrayList.size());
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
            myInterface.deleteDocuments(id, new retrofit.Callback<retrofit.client.Response>() {
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

    private void uploadDocument() {

        progressDialog.show();

        Iterator<String> iterator = str_licence_array.iterator();
        while(iterator.hasNext())
        {
            String value = iterator.next();
            if (value.contains("http"))
            {
                iterator.remove();
                // break;
            }
        }

        Iterator<String> iterator2 = str_proof_array.iterator();
        while(iterator2.hasNext())
        {
            String value = iterator2.next();
            if (value.contains("http"))
            {
                iterator2.remove();
                // break;
            }
        }

        Log.d("whereeeeee","innnnnnn   docss"+str_licence_array+"  numuberr "+str_proof_array);

        HashMap<String, String> map = new HashMap<>();
        Log.d("iddddd",pref.getString("driver_id", ""));
        map.put("driver_id", pref.getString("driver_id", ""));

        callAPiActivity.doPostWithFiles(DocumentsActivity.this, map, URLUPDATEUSER, str_licence_array, "driving_livence[]", str_proof_array, "address_proof[]", new com.eleganzit.msafiridriver.uploadMultupleImage.GetResponse() {

            @Override
            public void onSuccesResult(JSONObject result) throws JSONException {
                progressDialog.dismiss();
                String status = result.getString("status");
                if(status.equalsIgnoreCase("1"))
                {

                    /*if(hasDocs.equalsIgnoreCase("yes"))
                    {*/
                        /*editor.putString("documentsInfo", "changed");
                        editor.commit();*/
                        updateApprovalStatus();
                    /*}
                    else
                    {
                        *//*editor.putString("documentsInfo", "filled");
                        editor.commit();*//*
                    }
*/
                }
                else
                {
                    Toast.makeText(DocumentsActivity.this, ""+result.getString("message"), Toast.LENGTH_SHORT).show();
                }

                Log.d("messageeeeeeeeeee","succccccccessss"+status);
            }

            @Override
            public void onFailureResult(String message) {
                progressDialog.dismiss();
                if(message.equalsIgnoreCase("success"))
                {
                    updateApprovalStatus();
                }
                else
                {
                    Toast.makeText(DocumentsActivity.this, ""+message, Toast.LENGTH_SHORT).show();
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

                                        Toast.makeText(DocumentsActivity.this, "Please submit your profile for approval", Toast.LENGTH_LONG).show();
                                        finish();
                                    }

                                }
                                else
                                {
                                    Toast.makeText(DocumentsActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                Toast.makeText(DocumentsActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DocumentsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

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
                    .with(DocumentsActivity.this)
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
        Bungee.slideRight(DocumentsActivity.this);
    }
}
