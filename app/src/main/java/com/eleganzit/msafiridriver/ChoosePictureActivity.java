package com.eleganzit.msafiridriver;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.eleganzit.msafiridriver.fragment.TripFragment;
import com.eleganzit.msafiridriver.uploadImage.CallAPiActivity;
import com.eleganzit.msafiridriver.uploadImage.GetResponse;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.eleganzit.msafiridriver.ProfileActivity.profileActivity;

public class ChoosePictureActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 100;

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

    @Override
    protected void onResume() {
        super.onResume();
        pref = getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();
        photo=pref.getString("photo","");

        Log.d("photooooooo","photo "+photo);

        Glide
                .with(this)
                .load(photo)
                .apply(new RequestOptions().placeholder(R.drawable.pr).centerCrop().circleCrop()).into(profile_pic);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(ChoosePictureActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        setContentView(R.layout.activity_choose_picture);
        profile_pic=findViewById(R.id.profile_pic);
        final ImageView back=findViewById(R.id.back);
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
                EasyTransition.exit(ChoosePictureActivity.this,400, new DecelerateInterpolator());
                //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

            }
        });

        profile=findViewById(R.id.profile);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        URLUPDATEUSER = "http://itechgaints.com/M-safiri-API/driverPhoto";
        callAPiActivity = new CallAPiActivity(ChoosePictureActivity.this);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(ChoosePictureActivity.this)
                        .withPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {

                                    openImageChooser();

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
                                Toast.makeText(ChoosePictureActivity.this, "Error occurred! ", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onSameThread()
                        .check();

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
        EasyTransition.exit(ChoosePictureActivity.this,400, new DecelerateInterpolator());
        //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    void openImageChooser() {
        Intent galleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);*/
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), SELECT_PICTURE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {

            if (requestCode == SELECT_PICTURE) {
               /* File file = new File(String.valueOf(data));
                int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                Log.d("file_size1",""+file_size);
                onSelectFromGalleryResult(data);
                */

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

            }
        }
        if (resultCode==RESULT_CANCELED)
        {

        }

    }
    private void uploadProfile() {

        //progressDialog.show();
        if ((mediapath != null) && (!(mediapath.isEmpty()))) {

            HashMap<String, String> map = new HashMap<>();
            map.put("driver_id", pref.getString("driver_id", ""));
            callAPiActivity.doPostWithFiles(ChoosePictureActivity.this, map, URLUPDATEUSER, mediapath, "photo", new GetResponse() {
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
                                .with(ChoosePictureActivity.this)
                                .load(photo)
                                .apply(new RequestOptions().placeholder(R.drawable.pr).centerCrop().circleCrop())
                                .into(profile_pic);
                        Toast.makeText(ChoosePictureActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailureResult(String message) {
                    //progressDialog.dismiss();
                    //Toast.makeText(ChoosePictureActivity.this, "thisssss "+message, Toast.LENGTH_SHORT).show();
                    Log.d("messageeeeeeeeeee",message);

                }
            });
        }

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
