package com.eleganzit.msafiridriver.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            // Do something

            Log.d("Network Available ", "Flag No 1");
            //Toast.makeText(context, "internet "+wifi.isAvailable()+"   "+mobile.isAvailable(), Toast.LENGTH_SHORT).show();
        }
        else
        {
            //Toast.makeText(context, "internet "+wifi.isAvailable()+"   "+mobile.isAvailable(), Toast.LENGTH_SHORT).show();
        }
    }
}
