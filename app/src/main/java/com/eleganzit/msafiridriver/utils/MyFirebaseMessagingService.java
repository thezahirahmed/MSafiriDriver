package com.eleganzit.msafiridriver.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.activity.HomePassengerListActivity;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;


/**
 * Created by Uv on 2/7/2018.
 */

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    long[] vib;
    SharedPreferences sharedPreferences;
    String notification_type,message;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("mssgggggggg", "" + remoteMessage.getData().toString());

        try {
            JSONObject jsonObject=new JSONObject(remoteMessage.getData().get("message")+"");
            message=jsonObject.getString("message");
            notification_type=jsonObject.getString("type");
            if(notification_type.equalsIgnoreCase("go_online"))
            {
                showGoOnlineNotification(remoteMessage.getData().get("title"), message);
            }
            else if(notification_type.equalsIgnoreCase("driver_approvel"))
            {
                showApproveNotification(remoteMessage.getData().get("title"), message);
            }
            else if(notification_type.equalsIgnoreCase("user_confirm_trip"))
            {
                String trip_id=jsonObject.getString("trip_id");

                Log.d("tripiididddd",""+jsonObject.getString("trip_id"));
                showTripBookedNotification(remoteMessage.getData().get("title"), message,trip_id);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void showGoOnlineNotification(String title, String text) {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        Intent i = new Intent(this, NavHomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("from","notification");
        i.putExtra("type","go_online");
        i.putExtra("title",title);
        i.putExtra("content",text);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentTitle(title)
                    .setSmallIcon(getNotificationIcon())
                    .setContentText(text)

                    .setContentTitle("TuRyde")
                    .setSound(uri)
                    .setVibrate(new long[]{1000,500})
                    .setContentIntent(pendingIntent)
                    .setColor(getResources().getColor(R.color.colorPrimary));

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                String id = "id_product";
                // The user-visible name of the channel.
                CharSequence name = "TuRyde";
                // The user-visible description of the channel.
                String description = text;
                int importance = NotificationManager.IMPORTANCE_MAX;
                @SuppressLint("WrongConstant") NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                // Configure the notification channel.
                mChannel.setDescription(description);
                mChannel.enableLights(true);
                // Sets the notification light color for notifications posted to this
                // channel, if the device supports this feature.
                mChannel.setLightColor(Color.RED);
                assert manager != null;
                manager.createNotificationChannel(mChannel);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),"id_product")
                        .setSmallIcon(getNotificationIcon())//your app icon
                        .setBadgeIconType(getNotificationIcon()) //your app icon
                        .setChannelId(id)
                        .setSound(uri)
                        .setVibrate(new long[]{1000,500})
                        .setContentTitle(name)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setNumber(1)
                        .setColor(255)
                        .setContentText(text)
                        .setWhen(System.currentTimeMillis());

                manager.notify(m, notificationBuilder.build());
            }


            manager.notify(m, builder.build());


    }

    private void showApproveNotification(String title, String text) {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        Intent i = new Intent(this, NavHomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("from","notification");
        i.putExtra("type","approve");
        i.putExtra("title",title);
        i.putExtra("content",text);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle(title)
                .setSmallIcon(getNotificationIcon())
                .setContentText(text)

                .setContentTitle("TuRyde")
                .setSound(uri)
                .setVibrate(new long[]{1000,500})
                .setContentIntent(pendingIntent)
                .setColor(getResources().getColor(R.color.colorPrimary));

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String id = "id_product";
            // The user-visible name of the channel.
            CharSequence name = "TuRyde";
            // The user-visible description of the channel.
            String description = text;
            int importance = NotificationManager.IMPORTANCE_MAX;
            @SuppressLint("WrongConstant") NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            assert manager != null;
            manager.createNotificationChannel(mChannel);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),"id_product")
                    .setSmallIcon(getNotificationIcon())//your app icon
                    .setBadgeIconType(getNotificationIcon()) //your app icon
                    .setChannelId(id)
                    .setSound(uri)
                    .setVibrate(new long[]{1000,500})
                    .setContentTitle(name)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setNumber(1)
                    .setColor(255)
                    .setContentText(text)
                    .setWhen(System.currentTimeMillis());
            manager.notify(m, notificationBuilder.build());
        }


        manager.notify(m, builder.build());


    }

    private void showTripBookedNotification(String title, String text,String trip_id) {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        Intent i = new Intent(this, HomePassengerListActivity.class);
        i.putExtra("from","notification");
        i.putExtra("type","trip_booked");
        i.putExtra("trip_id",""+trip_id);
        i.putExtra("title",title);
        i.putExtra("content",text);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle(title)
                .setSmallIcon(getNotificationIcon())
                .setContentText(text)

                .setContentTitle("TuRyde")
                .setSound(uri)
                .setVibrate(new long[]{1000,500})
                .setContentIntent(pendingIntent)
                .setColor(getResources().getColor(R.color.colorPrimary));

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String id = "id_product";
            // The user-visible name of the channel.
            CharSequence name = "TuRyde";
            // The user-visible description of the channel.
            String description = text;
            int importance = NotificationManager.IMPORTANCE_MAX;
            @SuppressLint("WrongConstant") NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            assert manager != null;
            manager.createNotificationChannel(mChannel);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),"id_product")
                    .setSmallIcon(getNotificationIcon())//your app icon
                    .setBadgeIconType(getNotificationIcon()) //your app icon
                    .setChannelId(id)
                    .setSound(uri)
                    .setVibrate(new long[]{1000,500})
                    .setContentTitle(name)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setNumber(1)
                    .setColor(255)
                    .setContentText(text)
                    .setWhen(System.currentTimeMillis());
            manager.notify(m, notificationBuilder.build());
        }


        manager.notify(m, builder.build());


    }

    private int getNotificationIcon () {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.logo : R.drawable.logo;
    }
}
