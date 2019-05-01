package com.eleganzit.msafiridriver.activities_from_register;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.eleganzit.msafiridriver.ChoosePictureActivity;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.uploadImage.CallAPiActivity;
import com.eleganzit.msafiridriver.uploadImage.GetResponse;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.hzn.lib.EasyTransition;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

import static com.eleganzit.msafiridriver.activity.NavHomeActivity.active;

public class RegisterChoosePictureActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 100;
    private static final int REQUEST_IMAGE = 101;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 102;
    private ArrayList<String> mSelectPath;
    RelativeLayout profile;
    Bitmap bitmap;
    ImageView profile_pic;
    String mediapath = "";
    File file;
    String photo;
    CallAPiActivity callAPiActivity;
    public static String URLUPDATEUSER;
    ProgressDialog progressDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private boolean finishEnter;
    boolean upcomingTripsAreDone = false;
    boolean currentTripIsDone = false;

    @Override
    protected void onResume() {
        super.onResume();
        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor = pref.edit();
        photo = pref.getString("photo", "");

        Log.d("photooooooo", "photo " + photo);

        Glide
                .with(this)
                .asBitmap()
                .apply(new RequestOptions().override(250, 250).placeholder(R.drawable.pr).centerCrop().circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(photo)
                .into(profile_pic);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(RegisterChoosePictureActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        setContentView(R.layout.activity_choose_picture);
        profile_pic = findViewById(R.id.profile_pic);
        final ImageView back = findViewById(R.id.back);
        back.setEnabled(true);
        back.setClickable(true);
        long transitionDuration = 400;
        EasyTransition.enter(
                this,
                transitionDuration,
                new DecelerateInterpolator(),
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // init other views after transition anim
                        finishEnter = true;
                    }
                });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back.setEnabled(false);
                back.setClickable(false);
                //onBackPressed();
                //supportFinishAfterTransition();
                EasyTransition.exit(RegisterChoosePictureActivity.this, 400, new DecelerateInterpolator());
                //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

            }
        });

        profile = findViewById(R.id.profile);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        URLUPDATEUSER = "http://itechgaints.com/M-safiri-API/driverPhoto";
        callAPiActivity = new CallAPiActivity(RegisterChoosePictureActivity.this);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(RegisterChoosePictureActivity.this)
                        .withPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {

                                    //getUpcomingTrips();
                                    //openImageChooser();
                                    pickImage();
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
                                Toast.makeText(RegisterChoosePictureActivity.this, "Error occurred! ", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onSameThread()
                        .check();

            }
        });
    }

    public void getUpcomingTrips() {
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getDriverTrips(pref.getString("driver_id", ""), "current", new retrofit.Callback<retrofit.client.Response>() {
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
                        if (status.equalsIgnoreCase("1")) {
                            upcomingTripsAreDone = false;
                        } else {
                            upcomingTripsAreDone = true;
                        }
                        getCurrentTrip();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterChoosePictureActivity.this, "" + stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Log.d("errorrrr", "" + error.getMessage());
                Toast.makeText(RegisterChoosePictureActivity.this, "Couldn't refresh trips", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void getCurrentTrip() {

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getDriverTrips(pref.getString("driver_id", ""), "upcoming", new retrofit.Callback<retrofit.client.Response>() {
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
                        if (status.equalsIgnoreCase("1")) {
                            currentTripIsDone = false;
                        } else {
                            currentTripIsDone = true;
                        }
                        if (upcomingTripsAreDone && currentTripIsDone) {

                        } else {
                            //Toast.makeText(ChoosePictureActivity.this, "You cannot update profile if you have any trips remaining!", Toast.LENGTH_LONG).show();
                            new android.app.AlertDialog.Builder(RegisterChoosePictureActivity.this).setMessage("You cannot update profile if you have any trips remaining!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterChoosePictureActivity.this, "" + stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Log.d("errorrrr", "" + error.getMessage());
                Toast.makeText(RegisterChoosePictureActivity.this, "Couldn't refresh trips", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //supportFinishAfterTransition();
        EasyTransition.exit(RegisterChoosePictureActivity.this, 400, new DecelerateInterpolator());
        //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        }else {

            MultiImageSelector selector = MultiImageSelector.create(RegisterChoosePictureActivity.this);
            selector.single();
            selector.showCamera(false);

            selector.origin(mSelectPath);
            selector.start(RegisterChoosePictureActivity.this, REQUEST_IMAGE);
        }
    }

    private void requestPermission(final String permission, String rationale, final int requestCode){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(RegisterChoosePictureActivity.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    void openImageChooser() {
       /* Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        *//*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);*//*
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), SELECT_PICTURE);
*/
/*
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(false)
                .setShowGif(false)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            /*if (requestCode == SELECT_PICTURE) {
               *//* File file = new File(String.valueOf(data));
                int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                Log.d("file_size1",""+file_size);
                onSelectFromGalleryResult(data);
                *//*

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getApplicationContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int clumnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediapath = cursor.getString(clumnIndex);
                file = new File(mediapath);
                int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
                Log.d("file_size", "mediapath : " + mediapath + " ---- " + file_size);
                uploadProfile();

            }*/
            /*if (requestCode == PhotoPicker.REQUEST_CODE) {
                if (data != null) {
                    ArrayList<String> photos =
                            data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    StringBuilder sb = new StringBuilder();
                    for(String p: photos){
                        sb.append(p);
                        sb.append("\n");
                    }


                    mediapath=""+sb.toString().trim();

                    Glide
                            .with(RegisterChoosePictureActivity.this)
                            .load(mediapath.trim())
                            .apply(new RequestOptions().placeholder(R.drawable.pr).centerCrop().circleCrop())
                            .into(profile_pic);
                    uploadProfile();


                    Log.d("sdadad",""+mediapath);
                }
            }*/
        }
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                StringBuilder sb = new StringBuilder();
                for(String p: mSelectPath){
                    sb.append(p);
                    sb.append("\n");
                }


                mediapath=""+sb.toString().trim();
                uploadProfile();

                /*Glide
                        .with(RegisterChoosePictureActivity.this)
                        .asBitmap()
                        .apply(new RequestOptions().override(250, 250).placeholder(R.drawable.pr).centerCrop().circleCrop())
                        .load(mediapath.trim())
                        .into(profile_pic);
*/
                Log.d("mediapathhhhhhhh",""+mediapath);
            }
        }
        if (resultCode == RESULT_CANCELED) {

        }

    }

    private void uploadProfile() {

        //progressDialog.show();
        if ((mediapath != null) && (!(mediapath.isEmpty()))) {
            Log.d("mediapathhhhhhhh",""+mediapath);

            HashMap<String, String> map = new HashMap<>();
            map.put("driver_id", pref.getString("driver_id", ""));
            callAPiActivity.doPostWithFiles(RegisterChoosePictureActivity.this, map, URLUPDATEUSER, mediapath, "photo", new GetResponse() {
                @Override
                public void onSuccessResult(JSONObject result) throws JSONException {
                    //progressDialog.dismiss();
                    String status = result.getString("status");
                    JSONArray jsonArray = null;
                    if (status.equalsIgnoreCase("1")) {
                        jsonArray = result.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            photo = jsonObject1.getString("photo");

                        }
                        Log.d("result", "" + result.toString());

                        editor.putString("photo", photo);
                        editor.commit();

                        if(photo==null || photo.equalsIgnoreCase("null"))
                        {
                            Toast.makeText(RegisterChoosePictureActivity.this, "Image format doesn't support", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            final int[] width = new int[1];
                            final int[] height = new int[1];
                            ViewTreeObserver vto = active.getViewTreeObserver();
                            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        active.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    } else {
                                        active.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    }
                                    width[0] = active.getMeasuredWidth() + 10;
                                    height[0] = active.getMeasuredHeight();
                                    Log.d("wwwwwhhhhh", "" + width[0] + " - " + convertPixelsToDp(width[0], RegisterChoosePictureActivity.this) + "     " + height[0] + " - " + convertPixelsToDp(height[0], RegisterChoosePictureActivity.this));

                                }
                            });
                            Glide
                                    .with(RegisterChoosePictureActivity.this)
                                    .asBitmap()
                                    .apply(new RequestOptions().override(300, 300).placeholder(R.drawable.pr).centerCrop().circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL))
                                    .load(photo)
                                    .thumbnail(.1f)
                                    .into(profile_pic);
                            Toast.makeText(RegisterChoosePictureActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onFailureResult(String message) {
                    //progressDialog.dismiss();
                    //Toast.makeText(ChoosePictureActivity.this, "thisssss "+message, Toast.LENGTH_SHORT).show();
                    Log.d("messageeeeeeeeeee", message);

                }
            });
        }

    }
    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

//    private void onSelectFromGalleryResult(Intent data) {
//        if (data != null) {
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (bitmap!=null)
//        {
//
//
//            Glide
//                    .with(ChoosePictureActivity.this)
//                    .load(data.getData()).transform(new CircleTransform(ChoosePictureActivity.this)).skipMemoryCache(true)
//                    .into(profile_pic);
//
//        }
//        else
//        {
//            Toast.makeText(this, "Image format not proper", Toast.LENGTH_SHORT).show();
//        }
//
//    }
    /*public static class CircleTransform extends BitmapTransformation {
        public CircleTransform(Context context) {
            super(context);
        }

        @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override public String getId() {
            return getClass().getName();
        }

    }*/
}
