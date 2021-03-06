package com.eleganzit.msafiridriver.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganzit.msafiridriver.ChoosePictureActivity;
import com.eleganzit.msafiridriver.HomeProfileActivity;
import com.eleganzit.msafiridriver.ProfileActivity;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.SignInActivity;
import com.eleganzit.msafiridriver.activity.AboutUs;
import com.eleganzit.msafiridriver.activity.HelpActivity;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.eleganzit.msafiridriver.activity.RatingActivity;
import com.eleganzit.msafiridriver.activity.TripDetail;
import com.eleganzit.msafiridriver.activity.Utility;
import com.eleganzit.msafiridriver.adapter.UpcomingTripAdapter;
import com.eleganzit.msafiridriver.model.TripData;
import com.eleganzit.msafiridriver.uploadImage.CallAPiActivity;
import com.eleganzit.msafiridriver.uploadImage.GetResponse;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.hzn.lib.EasyTransitionOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.nereo.multi_image_selector.MultiImageSelector;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    LinearLayout rating,acsetting,help,aboutus,logout;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    CircleImageView drivercam,vehiclecam,driverimg,vehicleimg;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1, i =0,SELECT_FILE2 = 2;
    CallAPiActivity callAPiActivity;
    private String userChoosenTask;
    private String photo,vehicle_profile;
    private String mediapath;
    private File file;
    private String URLUPDATEUSER;
    private String URLUPDATEVEHICLE;
    RelativeLayout profile;
    TextView driver_name,vehicle_name;
    private String dname,vname;
    boolean upcomingTripsAreDone=false;
    boolean currentTripIsDone=false;
    ProgressDialog progressDialog;
    private static final int REQUEST_IMAGE1 = 101;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION1 = 102;
    private ArrayList<String> mSelectPath1;
    private static final int REQUEST_IMAGE2 = 201;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION2 = 202;
    private ArrayList<String> mSelectPath2;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        pref = getActivity().getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();
        photo=pref.getString("photo","");
        vehicle_profile=pref.getString("vehicle_profile","");

        Log.d("photooossss",photo+ "     "+vehicle_profile);

        dname=pref.getString("fullname","");
        vname=pref.getString("vehicle_name","");

        if(photo.equalsIgnoreCase("http://itechgaints.com/M-safiri-API/user_uploads/no_profile.png"))
        {
            Glide
                    .with(this)
                    .load(R.drawable.pr)
                    .into(driverimg);
        }
        else
        {
            Glide
                    .with(this)
                    .load(photo).apply(new RequestOptions().placeholder(R.drawable.pr))
                    .into(driverimg);
        }


        if(vehicle_profile.equalsIgnoreCase("http://itechgaints.com/M-safiri-API/user_uploads/no_profile.png"))
        {
            Glide
                    .with(this)
                    .load(R.mipmap.car_pr)
                    .into(vehicleimg);
        }
        else
        {
            Glide
                    .with(this)
                    .load(vehicle_profile).apply(new RequestOptions().placeholder(R.mipmap.car_pr))
                    .into(vehicleimg);
        }

        driver_name.setText(""+dname);
        vehicle_name.setText(""+vname);

    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_account, container, false);
        NavHomeActivity.home_title.setText("My Account");
        NavHomeActivity.active.setVisibility(View.GONE);
        NavHomeActivity.fab.setVisibility(View.GONE);
        profile = v.findViewById(R.id.profile);
        help = v.findViewById(R.id.help);
        aboutus = v.findViewById(R.id.aboutus);
        logout = v.findViewById(R.id.logout);
        acsetting = v.findViewById(R.id.acsetting);
        rating = v.findViewById(R.id.rating);
        drivercam = v.findViewById(R.id.dcamera);
        vehiclecam = v.findViewById(R.id.vcamera);
        driverimg = v.findViewById(R.id.dimage);
        vehicleimg = v.findViewById(R.id.vimage);
        driver_name = v.findViewById(R.id.driver_name);
        vehicle_name = v.findViewById(R.id.vehicle_name);
        URLUPDATEUSER = "http://itechgaints.com/M-safiri-API/driverPhoto";
        URLUPDATEVEHICLE= "http://itechgaints.com/M-safiri-API/drivervehiclePhoto";
        callAPiActivity = new CallAPiActivity(getActivity());
        progressDialog =new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");

        return  v;
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
                        Toast.makeText(getActivity(), ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Server or Internet Error", Toast.LENGTH_LONG).show();

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
                            if(i==0)
                            {
                                //openImageChooser();
                                pickImage1();
                            }
                            if(i==1)
                            {
                                //openImageChooser2();
                                pickImage2();
                            }
                        }
                        else
                        {
                            //Toast.makeText(getActivity(), "You cannot update profile if you have any trips remaining!", Toast.LENGTH_LONG).show();
                            new android.app.AlertDialog.Builder(getActivity()).setMessage("You cannot update profile if you have any trips remaining!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                        Toast.makeText(getActivity(), ""+stringBuilder, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Server or Internet Error", Toast.LENGTH_LONG).show();

            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RatingActivity homeFrag= new RatingActivity();
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.container, homeFrag,"TAG")
                        .addToBackStack(null)
                        .commit();
            }
        });
        acsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeProfileActivity.class).putExtra("from","account");
                startActivity(intent);
                Bungee.slideLeft(getActivity());
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
                Bungee.slideLeft(getActivity());


            }
        });

        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutUs.class);
                startActivity(intent);
                Bungee.slideLeft(getActivity());



            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(getActivity()).setTitle("Logout").setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.clear();
                                editor.commit();
                                editor.apply();
                                SharedPreferences p_pref=getActivity().getSharedPreferences("passenger_pref", Context.MODE_PRIVATE);
                                SharedPreferences.Editor p_editor=p_pref.edit();
                                SharedPreferences pref=getActivity().getSharedPreferences("location_pref",MODE_PRIVATE);
                                SharedPreferences.Editor editor=pref.edit();

                                p_editor.clear();
                                p_editor.commit();
                                p_editor.apply();

                                editor.clear();
                                editor.commit();
                                editor.apply();

                                Intent intent = new Intent(getActivity(), SignInActivity.class).putExtra("from","account");
                                startActivity(intent);
                                Bungee.slideLeft(getActivity());


                                getActivity().finish();
                                Bungee.slideRight(getActivity());


                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });


        driverimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 0;
                //selectImage();
                Dexter.withActivity(getActivity())
                        .withPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {

                                    getUpcomingTrips();

                                }

                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    // show alert dialog navigating to Settings
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).
                        withErrorListener(new PermissionRequestErrorListener() {
                            @Override
                            public void onError(DexterError error) {
                                Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onSameThread()
                        .check();

            }
        });

        vehicleimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                i = 1;
                Dexter.withActivity(getActivity())
                        .withPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {

                                    getUpcomingTrips();

                                }

                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    // show alert dialog navigating to Settings
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).
                        withErrorListener(new PermissionRequestErrorListener() {
                            @Override
                            public void onError(DexterError error) {
                                Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onSameThread()
                        .check();

            }
        });
    }

    private void showSettingsDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /*void openImageChooser() {
        Intent galleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        *//*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);*//*
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), SELECT_FILE);


    }*/
    private void pickImage1() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION1);
        }else {

            MultiImageSelector selector = MultiImageSelector.create(getActivity());
            selector.single();
            selector.showCamera(false);

            selector.origin(mSelectPath1);
            selector.start(AccountFragment.this, REQUEST_IMAGE1);
        }
    }

    private void pickImage2() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION2);
        }else {

            MultiImageSelector selector = MultiImageSelector.create(getActivity());
            selector.single();
            selector.showCamera(false);

            selector.origin(mSelectPath2);
            selector.start(AccountFragment.this, REQUEST_IMAGE2);
        }
    }

    private void requestPermission(final String permission, String rationale, final int requestCode){
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)){
            new android.support.v7.app.AlertDialog.Builder(getActivity())
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
        }
    }


/*

    void openImageChooser2() {
        Intent galleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        */
/*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);*//*

        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), SELECT_FILE2);
    }
*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Gallery"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(getActivity());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask ="Choose from Gallery";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            /*if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int clumnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediapath = cursor.getString(clumnIndex);
                file = new File(mediapath);
                int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
                Log.d("file_size", "mediapath : " + mediapath + " ---- " + file_size);
                uploadProfile();
            }
            if (requestCode == SELECT_FILE2) {
                onSelectFromGalleryResult(data);
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int clumnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediapath = cursor.getString(clumnIndex);
                file = new File(mediapath);
                int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
                Log.d("file_size", "mediapath : " + mediapath + " ---- " + file_size);
                uploadVehicleProfile();
            }*/

            if(requestCode == REQUEST_IMAGE1){
                if(resultCode == Activity.RESULT_OK){
                    mSelectPath1 = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                    StringBuilder sb = new StringBuilder();
                    for(String p: mSelectPath1){
                        sb.append(p);
                        sb.append("\n");
                    }


                    mediapath=""+sb.toString().trim();

                /*Glide
                        .with(getActivity())
                        .load(mediapath.trim())
                        .apply(new RequestOptions().placeholder(R.drawable.pr).centerCrop().circleCrop())
                        .into(driverimg);*/
                    uploadProfile();


                    Log.d("mediapathhhhhhhh",""+mediapath);
                }
            }

            if(requestCode == REQUEST_IMAGE2){
                if(resultCode == Activity.RESULT_OK){
                    mSelectPath2 = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                    StringBuilder sb = new StringBuilder();
                    for(String p: mSelectPath2){
                        sb.append(p);
                        sb.append("\n");
                    }


                    mediapath=""+sb.toString().trim();

                /*Glide
                        .with(getActivity())
                        .load(mediapath.trim())
                        .apply(new RequestOptions().placeholder(R.drawable.pr).centerCrop().circleCrop())
                        .into(vehicleimg);*/
                    uploadVehicleProfile();


                    Log.d("mediapathhhhhhhh",""+mediapath);
                }
            }
            if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void uploadProfile() {

        //progressDialog.show();
        if ((mediapath != null) && (!(mediapath.isEmpty()))) {

            HashMap<String, String> map = new HashMap<>();
            map.put("driver_id", pref.getString("driver_id", ""));
            callAPiActivity.doPostWithFiles(getActivity(), map, URLUPDATEUSER, mediapath, "photo", new GetResponse() {
                @Override
                public void onSuccessResult(JSONObject result) throws JSONException {
                    //progressDialog.dismiss();
                    String status = result.getString("status");
                    JSONArray jsonArray = null;
                    if(status.equalsIgnoreCase("1")) {
                        jsonArray = result.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            photo = jsonObject1.getString("photo");

                        }
                        Log.d("result",""+photo);

                        editor.putString("photo", photo);
                        editor.commit();
                        Glide
                                .with(getActivity())
                                .load(mediapath).apply(new RequestOptions().placeholder(R.drawable.pr))
                                .into(driverimg);
                        Glide
                                .with(getActivity())
                                .load(mediapath).apply(new RequestOptions().placeholder(R.drawable.pr))
                                .into(NavHomeActivity.profile_image);

                        if(photo==null || photo.equalsIgnoreCase("null"))
                        {
                            Toast.makeText(getActivity(), "Image format doesn't support", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Profile picture updated", Toast.LENGTH_SHORT).show();
                        }

                    }

                }

                @Override
                public void onFailureResult(String message) {
                    //progressDialog.dismiss();
                    //Toast.makeText(getActivity(), "thisssss "+message, Toast.LENGTH_SHORT).show();
                    Log.d("messageeeeeeeeeee",message);
                    Toast.makeText(getActivity(), "Server or Internet Error", Toast.LENGTH_LONG).show();

                }
            });
        }

    }

    private void uploadVehicleProfile() {

        //progressDialog.show();
        if ((mediapath != null) && (!(mediapath.isEmpty()))) {

            HashMap<String, String> map = new HashMap<>();
            map.put("driver_id", pref.getString("driver_id", ""));
            Log.d("mediapathtttttt",mediapath+"");
            callAPiActivity.doPostWithFiles(getActivity(), map, URLUPDATEVEHICLE, mediapath, "vehicle_profile", new GetResponse() {
                @Override
                public void onSuccessResult(JSONObject result) throws JSONException {
                    //progressDialog.dismiss();
                    String status = result.getString("status");
                    JSONArray jsonArray = null;
                    if(status.equalsIgnoreCase("1")) {
                        jsonArray = result.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            vehicle_profile = jsonObject1.getString("vehicle_profile");

                        }
                        Log.d("result",""+vehicle_profile);

                        editor.putString("vehicle_profile", vehicle_profile);
                        editor.commit();
                        Glide
                                .with(getActivity())
                                .load(mediapath)
                                .into(vehicleimg);

                        if(photo==null || photo.equalsIgnoreCase("null"))
                        {
                            Toast.makeText(getActivity(), "Image format doesn't support", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Profile picture updated", Toast.LENGTH_SHORT).show();
                        }

                    }

                }

                @Override
                public void onFailureResult(String message) {
                    //progressDialog.dismiss();
                    //Toast.makeText(getActivity(), "thisssss "+message, Toast.LENGTH_SHORT).show();
                    Log.d("messageeeeeeeeeee",message);
                    Toast.makeText(getActivity(), "Server or Internet Error", Toast.LENGTH_LONG).show();

                }
            });
        }

    }
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(i == 0){
            driverimg.setImageBitmap(thumbnail);
        }
        else {
            vehicleimg.setImageBitmap(thumbnail);

        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(i == 0){
            //driverimg.setImageBitmap(bm);
        }
        else {
            vehicleimg.setImageBitmap(bm);

        }

    }


}









