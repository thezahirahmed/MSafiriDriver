package com.eleganzit.msafiridriver.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eleganzit.msafiridriver.PickupLocation;
import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.utils.DirectionsJSONParser;
import com.eleganzit.msafiridriver.utils.MyInterface;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import spencerstudios.com.bungeelib.Bungee;

public class TripDetail extends AppCompatActivity implements OnMapReadyCallback {

    MapView mapView;
    private String id;
    GoogleMap googleMap;
    ProgressBar progress;
    TextView pickup_location,destination_location,pickup_time,destination_time,total_passenger,total_rating;
    ScrollView content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Bungee.slideRight(TripDetail.this);
            }
        });
        mapView= (MapView) findViewById(R.id.map);
        progress=findViewById(R.id.progress);
        content= findViewById(R.id.content);
        pickup_location=findViewById(R.id.pickup_location);
        pickup_location.setSelected(true);
        pickup_time=findViewById(R.id.pickup_time);
        destination_location=findViewById(R.id.destination_location);
        destination_location.setSelected(true);
        destination_time=findViewById(R.id.destination_time);
        total_passenger=findViewById(R.id.total_passenger);
        total_rating=findViewById(R.id.total_rating);
        if(mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);

        }
        id=getIntent().getStringExtra("id");
        getTrip();

    }

    public void getTrip()
    {
        content.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").setClient(new OkClient(okHttpClient)).build();
        final MyInterface myInterface = restAdapter.create(MyInterface.class);
        myInterface.getTrip(id, new retrofit.Callback<retrofit.client.Response>() {
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
                            progress.setVisibility(View.GONE);
                            content.setVisibility(View.VISIBLE);

                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                String id = jsonObject1.getString("trip_id");
                                String sfrom_title = jsonObject1.getString("from_title");
                                String from_lat = jsonObject1.getString("from_lat");
                                String from_lng = jsonObject1.getString("from_lng");
                                String sfrom_address = jsonObject1.getString("from_address");
                                String sto_title = jsonObject1.getString("to_title");
                                String to_lat = jsonObject1.getString("to_lat");
                                String to_lng = jsonObject1.getString("to_lng");
                                String sto_address = jsonObject1.getString("to_address");
                                String spickup_time = jsonObject1.getString("datetime");
                                String sdestination_time = jsonObject1.getString("end_datetime");
                                String statuss = jsonObject1.getString("status");
                                String stotal_passenger = jsonObject1.getString("total_passenger");
                                String ratting = jsonObject1.getString("ratting");

                                String inputPattern = "yyyy-MM-dd HH:mm:ss";
                                String outputPattern = "dd/MM/yy 'at' hh:mm a";
                                SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                                SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                                Date pdate = null;
                                Date ddate = null;
                                String pstr = null;
                                String dstr = null;


                                try {
                                    pdate = inputFormat.parse(spickup_time);
                                    pstr = outputFormat.format(pdate);
                                    ddate = inputFormat.parse(sdestination_time);
                                    dstr = outputFormat.format(ddate);



                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                pickup_location.setText(sfrom_title);
                                pickup_time.setText(pstr);
                                destination_location.setText(sto_title);
                                destination_time.setText(dstr);
                                total_passenger.setText(stotal_passenger);
                                total_rating.setText(ratting);
                                ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();

                                LatLng loc=new LatLng(Double.parseDouble(from_lat),Double.parseDouble(from_lng));
                                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.location_green);
                                Bitmap bm=bitmapdraw.getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(bm, 70, 70, false);
                                //DrawMarker.getInstance(this).draw(googleMap, loc2, BitmapDescriptorFactory.fromBitmap(smallMarker), "Pickup Location");

                                googleMap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Pickup Location"));

                                LatLng loc2=new LatLng(Double.parseDouble(to_lat),Double.parseDouble(to_lng));
                                BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.location_red);
                                Bitmap b2=bitmapdraw2.getBitmap();
                                Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, 70, 70, false);

                                //DrawMarker.getInstance(this).draw(googleMap, loc2, BitmapDescriptorFactory.fromBitmap(smallMarker), "Pickup Location");

                                googleMap.addMarker(new MarkerOptions().position(loc2).icon(BitmapDescriptorFactory.fromBitmap(smallMarker2)).title("Destination Location"));
                                String url = getDirectionsUrl(loc, loc2);
                                mMarkerArray.add(googleMap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Pickup Location")));
                                mMarkerArray.add(googleMap.addMarker(new MarkerOptions().position(loc2).icon(BitmapDescriptorFactory.fromBitmap(smallMarker2)).title("Destination Location")));

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (Marker marker : mMarkerArray) {
                                    builder.include(marker.getPosition());
                                }
                                final LatLngBounds bounds = builder.build();
                                int padding = 80; // offset from edges of the map in pixels
                                final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                                googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                    @Override
                                    public void onMapLoaded() {
                                        googleMap.moveCamera(cu);
                                        googleMap.animateCamera(cu);
                                    }
                                });
                                googleMap.getUiSettings().setAllGesturesEnabled(false);
                                DownloadTask downloadTask = new DownloadTask();
                                // Start downloading json data from Google Directions API
                                downloadTask.execute(url);
                            }

                        }
                        else
                        {
                            progress.setVisibility(View.GONE);
                            //content.setVisibility(View.VISIBLE);

                        }

                        // Toast.makeText(RegistrationActivity.this, "scc "+Token, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Toast.makeText(TripDetail.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progress.setVisibility(View.GONE);
                //content.setVisibility(View.VISIBLE);
                //Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(TripDetail.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
        MapsInitializer.initialize(getApplicationContext());
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        LatLng loc2=new LatLng(23.0262,72.5242);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc2));


        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
       // googleMap.addMarker(new MarkerOptions().position(loc2));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(TripDetail.this);
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
            googleMap.addPolyline(lineOptions);
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
        String maps_api_key=getResources().getString(R.string.google_api_key);
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+"&key="+maps_api_key;

        return url;
    }

    /*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void circularRevealActivity() {

        int cx = rootLayout.getWidth() * 12/ 10;
        int cy = rootLayout.getHeight() * 12/ 10;

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(500);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }*/
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

                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                }
            });

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
