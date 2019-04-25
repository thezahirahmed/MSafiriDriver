package com.eleganzit.msafiridriver.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganzit.msafiridriver.DocumentsActivity;
import com.eleganzit.msafiridriver.PickupLocation;
import com.eleganzit.msafiridriver.ProfileActivity;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.activity.Home;
import com.eleganzit.msafiridriver.activity.NavHomeActivity;
import com.eleganzit.msafiridriver.activity.OnboardPassengerListActivity;
import com.eleganzit.msafiridriver.activity.PassengerListActivity;
import com.eleganzit.msafiridriver.adapter.PassengerAdapter;
import com.eleganzit.msafiridriver.adapter.UpcomingTripAdapter;
import com.eleganzit.msafiridriver.model.MarkerData;
import com.eleganzit.msafiridriver.model.PassengerData;
import com.eleganzit.msafiridriver.model.TripData;
import com.eleganzit.msafiridriver.utils.DirectionsJSONParser;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.eleganzit.msafiridriver.utils.RobotoMediumTextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import spencerstudios.com.bungeelib.Bungee;

import static android.content.Context.MODE_PRIVATE;
import static android.view.Window.FEATURE_NO_TITLE;


/**
 * A simple {@link Fragment} subclass.
 */
public class TripFragment extends Fragment implements OnMapReadyCallback {


    private String lat,lng,lat2,lng2,trip_status;
    private String photo;
    private String photoPath;
    SharedPreferences p_pref;
    SharedPreferences.Editor p_editor;

    public TripFragment() {
        // Required empty public constructor
    }
    ArrayList<MarkerData> markersArray = new ArrayList<MarkerData>();
    MapView mapView;
    ArrayList<PassengerData> arrayList=new ArrayList<>();
    GoogleMap map;
    TextView btn_list,pickup,destination;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    ProgressBar progress_bar;
    RelativeLayout content,no_trip,main_content;
    CircleImageView profile;
    String id,vehicle_name;
    RobotoMediumTextView txtvehicle_name;
    

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_trip, container, false);
        NavHomeActivity.home_title.setText("Current Trip");
        NavHomeActivity.active.setVisibility(View.GONE);
        NavHomeActivity.fab.setVisibility(View.GONE);
        pref = getActivity().getSharedPreferences("mysession", MODE_PRIVATE);
        editor=pref.edit();
        p_pref=getActivity().getSharedPreferences("passenger_pref",Context.MODE_PRIVATE);
        p_editor=p_pref.edit();
        photo=pref.getString("photo","");
        vehicle_name=pref.getString("vehicle_name","");
        mapView= v.findViewById(R.id.map);
        profile= v.findViewById(R.id.fab);
        txtvehicle_name= v.findViewById(R.id.vehicle_name);
        progress_bar= v.findViewById(R.id.progress_bar);
        main_content= v.findViewById(R.id.main_content);
        content= v.findViewById(R.id.content);
        no_trip= v.findViewById(R.id.no_trip);

        Glide
                .with(getActivity())
                .load(photo).apply(new RequestOptions().placeholder(R.drawable.pr))
                .into(profile);

        txtvehicle_name.setText("Vehicle Name: "+vehicle_name);

        btn_list=v.findViewById(R.id.passenger_list);
        pickup=v.findViewById(R.id.pickup_location);
        destination=v.findViewById(R.id.destination_location);
        progressDialog=new ProgressDialog(getActivity());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.location_green);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);
        BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.location_red);
        Bitmap b2 = bitmapdraw2.getBitmap();
        Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, 60, 60, false);
        MarkerData markerData=new MarkerData("Pickup","location",smallMarker,23.0262,72.5242);
        MarkerData markerData1=new MarkerData("Destination","location",smallMarker2,19.0760,72.8777);
        markersArray.add(markerData);
        markersArray.add(markerData1);
        mapView.getMapAsync(this);
        if(mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
        getUpcomingTrip();
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //final Dialog dialog=new Dialog(getActivity());

                p_editor.putString("trip_id",id+"");
                p_editor.putString("trip_lat",lat+"");
                p_editor.putString("trip_lng",lng+"");
                p_editor.putString("trip_lat2",lat2+"");
                p_editor.putString("trip_lng2",lng2+"");
                p_editor.putString("trip_status",trip_status+"");

                Log.d("trip_statusssssid","  "+id);
                if(p_pref.getString("trip_status","").equalsIgnoreCase("ongoing"))
                {
                    getActivity().startActivity(new Intent(getActivity(),OnboardPassengerListActivity.class).putExtra("from","trip"));
                    Bungee.slideLeft(getActivity());
                }
                else
                {
                    if(p_pref.getBoolean("firstTime",false))
                    {
                        //Toast.makeText(getActivity(), p_pref.getBoolean("firstTime",false)+" already captured", Toast.LENGTH_SHORT).show();
                        getActivity().startActivity(new Intent(getActivity(),PassengerListActivity.class).putExtra("from","trip"));
                        Bungee.slideLeft(getActivity());
                    }
                    else
                    {
                        //Toast.makeText(getActivity(), p_pref.getBoolean("firstTime",false)+" captured now", Toast.LENGTH_SHORT).show();
                        p_editor.putBoolean("firstTime",true);

                        CaptureMapScreen();
                    }
                    p_editor.commit();


                }

/*
                dialog.getWindow().requestFeature(FEATURE_NO_TITLE);
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
                dialog.setContentView(R.layout.checkbox_popup);
                RecyclerView route=dialog.findViewById(R.id.route);
                RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                route.setLayoutManager(layoutManager);
                final TextView drop=dialog.findViewById(R.id.drop);
                final TextView pick=dialog.findViewById(R.id.pick);
                final LinearLayout start=dialog.findViewById(R.id.start);
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr="+lat+","+lng+"&daddr="+lat2+","+lng2));
                        startActivity(intent);

                        dialog.dismiss();
                    }
                });
                drop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drop.setBackgroundColor(Color.parseColor("#016d9c"));
                        pick.setBackgroundColor(Color.parseColor("#0192D2"));

                    }
                });
                pick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pick.setBackgroundColor(Color.parseColor("#016d9c"));
                        drop.setBackgroundColor(Color.parseColor("#0192D2"));

                    }
                });
                route.setAdapter(new PassengerAdapter(arrayList,getActivity()));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.setCancelable(true);
                dialog.show();*/
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        MapsInitializer.initialize(getContext().getApplicationContext());
        map.getUiSettings().setAllGesturesEnabled(false);
        //map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.style_json));
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        LatLng loc2=new LatLng(23.0262,72.5242);
        map.moveCamera(CameraUpdateFactory.newLatLng(loc2));

        map.animateCamera(CameraUpdateFactory.zoomTo(13.0f));

        /*for(int i = 0 ; i < markersArray.size() ; i++) {

            createMarker(markersArray.get(i).getLatitude(), markersArray.get(i).getLongitude(), markersArray.get(i).getTitle(), markersArray.get(i).getSnippet(), markersArray.get(i).getIconResID());
        }*/
    }

    public void CaptureMapScreen()
    {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                bitmap = snapshot;
                try {

                    print2(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        Log.d("Exceptiondataaao","done");

        map.snapshot(callback);


    }

    private void print2(Bitmap bitmapp){
        ProgressDialog dialog = new ProgressDialog(getActivity());

        dialog.setMessage("Saving...");
        dialog.show();

        //bitmapp = getBitmapFromView(native_resit,native_resit.getChildAt(0).getHeight(),native_resit.getChildAt(0).getWidth());
        try {
            File defaultFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/MSafiriData");
            if (!defaultFile.exists()){
                defaultFile.mkdirs();

            }

            String filename = "snap"+pref.getString("driver_id","")+""+ System.currentTimeMillis()+".png";
            File file = new File(defaultFile,filename);
            if (file.exists()) {
                file.delete();
                file = new File(defaultFile,filename);
            }

            FileOutputStream output = new FileOutputStream(file);
            bitmapp.compress(Bitmap.CompressFormat.PNG, 100, output);

            output.flush();
            output.close();
            // String photoPath = Environment.getExternalStorageDirectory() + "/SoberSense/" + myfile;//<---

            photoPath=""+file;

            Log.d("photoPath",photoPath);

            dialog.dismiss();
            startActivity(new Intent(getActivity(),PassengerListActivity.class).putExtra("from","trip").putExtra("photoPath",photoPath+""));

            p_editor.putString("photoPath",""+photoPath);
            p_editor.commit();

            Bungee.slideLeft(getActivity());

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("eeeeeeeeeeeeeeeeee",e.getMessage()+"");
            dialog.dismiss();
        }
    }

    public void getUpcomingTrip()
    {

        if(!progressDialog.isShowing())
        {
            progressDialog.show();
        }
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getDriverTrips(pref.getString("driver_id",""), "upcoming", new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                arrayList = new ArrayList<>();

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

                            main_content.setVisibility(View.VISIBLE);
                            content.setVisibility(View.VISIBLE);

                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                id = jsonObject1.getString("id");
                                String from_title = jsonObject1.getString("from_title");
                                String from_lat = jsonObject1.getString("from_lat");
                                String from_lng = jsonObject1.getString("from_lng");
                                String from_address = jsonObject1.getString("from_address");
                                String to_title = jsonObject1.getString("to_title");
                                String to_lat = jsonObject1.getString("to_lat");
                                String to_lng = jsonObject1.getString("to_lng");
                                String to_address = jsonObject1.getString("to_address");
                                String pickup_time = jsonObject1.getString("datetime");
                                String destination_time = jsonObject1.getString("end_datetime");
                                String statuss = jsonObject1.getString("status");
                                lat=from_lat;
                                lng=from_lng;
                                lat2=to_lat;
                                lng2=to_lng;
                                trip_status=statuss;
                                pickup.setText(from_address);
                                destination.setText(to_address);

                                map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                    @Override
                                    public void onMapLoaded() {
                                        drawRoute(Double.parseDouble(lat),Double.parseDouble(lng),Double.parseDouble(lat2),Double.parseDouble(lng2));
                                        progressDialog.dismiss();
                                    }
                                });

                            }
                            no_trip.setVisibility(View.GONE);
                        }
                        else
                        {
                            progress_bar.setVisibility(View.GONE);
                            main_content.setVisibility(View.GONE);
                            no_trip.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        }

                    }
                    else
                    {
                        progressDialog.dismiss();

                        Toast.makeText(getActivity(), ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    progressDialog.dismiss();


                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();

                }

            }

            @Override
            public void failure(RetrofitError error) {
                progress_bar.setVisibility(View.GONE);
                no_trip.setVisibility(View.VISIBLE);
                content.setVisibility(View.VISIBLE);
                progressDialog.dismiss();

                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void drawRoute(double from_lat, double from_lng, double to_lat, double to_lng) {

        LatLng origin = new LatLng(from_lat, from_lng);
        LatLng destination = new LatLng(to_lat, to_lng);
        ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
        MarkerOptions options = new MarkerOptions();
        // Setting the position of the marker
        options.position(origin);

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.location_green);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap firstMarker = Bitmap.createScaledBitmap(b, 70   , 70, false);

        Marker amarker1=    map.addMarker(options.icon(BitmapDescriptorFactory.fromBitmap(firstMarker)));
        MarkerOptions options2 = new MarkerOptions();
        BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.location_red);
        Bitmap b2=bitmapdraw2.getBitmap();
        Bitmap firstMarker2 = Bitmap.createScaledBitmap(b2, 70   , 70, false);

        // Setting the position of the marker
        options2.position(destination);
        Marker amarker2=    map.addMarker(options2.icon(BitmapDescriptorFactory.fromBitmap(firstMarker2)));

        mMarkerArray.add(amarker1);
        mMarkerArray.add(amarker2);


        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkerArray) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 80; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.moveCamera(cu);
        map.animateCamera(cu);
        String url = getDirectionsUrl(origin, destination);
        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = new ArrayList();
            PolylineOptions lineOptions = new PolylineOptions();
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.parseColor("#4885ed"));
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);

        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String maps_api_key=getResources().getString(R.string.google_maps_key);
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+"&key="+maps_api_key;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("Exception", data);

            JSONObject jsonObject=new JSONObject(""+data);
            JSONArray jsonArray=jsonObject.getJSONArray("routes");

            for (int i=0;i<jsonArray.length();i++)
            {

                JSONObject childObject=jsonArray.getJSONObject(i);

                JSONArray jsonArray1=childObject.getJSONArray("legs");
                for (int j=0;j<jsonArray1.length();j++)
                {
                    JSONObject jsonObject1=jsonArray1.getJSONObject(i);
                    Log.d("Exceptiondataaa",jsonObject1.getJSONObject("distance").getString("text"));
                    Log.d("Exceptiondataaa",jsonObject1.getJSONObject("duration").getString("text"));
                    String[] str=jsonObject1.getJSONObject("distance").getString("text").split(" ");

                    /*Log.d("Exceptiondataaa",""+convertKmsToMiles(Double.parseDouble(str[0]))+" miles");
                    miles = convertKmsToMiles(Double.parseDouble(str[0]))+"";
                    dmiles = convertKmsToMiles(Double.parseDouble(str[0]));
                    distance = jsonObject1.getJSONObject("duration").getString("text");*/
                }
            }
/*

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    distance_time.setText(new DecimalFormat("##.##").format(dmiles)+" miles - "+distance);
                }
            });
*/

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
