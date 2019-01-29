package com.eleganzit.msafiridriver.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class MyLocationReceiver extends BroadcastReceiver {

    String intent_action;
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref=context.getSharedPreferences("location_pref",MODE_PRIVATE);
        intent_action=pref.getString("action","");

        Log.i("wherreeeeeee stopped", "BroadcastReceiver Service Stops!  "+intent_action);
        if(intent_action.equalsIgnoreCase("start"))
        {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            {
                context.startForegroundService(new Intent(context, SensorService.class));
            }
            else
            {
                context.startService(new Intent(context, SensorService.class));
            }
        }
        else
        {
            context.stopService(new Intent(context, SensorService.class));
        }
    }
}
