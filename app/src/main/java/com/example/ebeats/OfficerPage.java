package com.example.ebeats;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.primitives.Doubles;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class OfficerPage extends AppCompatActivity {

    private TextView FONameTextView;
    private TextView StatusTextView;
    private TextView CoordTextView;
    private String name;
    private String uid;
    private Button button;
    private ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
    private ArrayList<ArrayList<Double>> geofenceBundle = new ArrayList<ArrayList<Double>>();
    private ArrayList<ArrayList<Double>> BeatPointBundle = new ArrayList<ArrayList<Double>>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(95,176,201)));

        FONameTextView = findViewById(R.id.FOName);
        StatusTextView = findViewById(R.id.status);
        CoordTextView = findViewById(R.id.coord);
        button = findViewById(R.id.button);

        name = getIntent().getExtras().getString("USER");
        uid = getIntent().getExtras().getString("UID");




        FONameTextView.setText(name);

        Trip trip = null;

        OkHttpClient client = new OkHttpClient();

        String url = "https://api.geospark.co/v1/api/trip/?user_id="+uid;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Api-Key", "0a39fd0363c04286b0959cc50d6b9120")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    OfficerPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Trip trip = extractTripIDSFromJson(myResponse);
                            if (trip.getTripID().isEmpty()){
                                CoordTextView.setText("No Available Trips");
                            }
                            else {
                                setTripCoords(trip);
                            }
                            updateUI(trip);

                        }
                    });
                }
            }
        });





        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OfficerPage.this,ChatActivity.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OfficerPage.this, NewMapActivity.class);

                intent.putExtra("tripCoords", result);
                intent.putExtra("Geofence", geofenceBundle);
                intent.putExtra("Beatpoints", BeatPointBundle);

                Log.e("COORDS", geofenceBundle.toString());


                if(result.size()==0){
                    Toast toast = Toast.makeText(OfficerPage.this, "Officer is Inactive, and has no Past Trips",Toast.LENGTH_SHORT);
                    toast.show();

                } else {
                    startActivity(intent);
                }
                // getSupportFragmentManager().beginTransaction().replace(R.id.linearlayout,new MapFragment()).commit();
            }
        });

    }

    public void updateUI(Trip trip){
        if(trip.getEndAddress().isEmpty() && trip.getStartAddress().isEmpty()){
            StatusTextView.setText("INACTIVE");
            StatusTextView.setTextColor(getResources().getColor(R.color.red));
        }
        else if(trip.getEndAddress().isEmpty() && !trip.getStartAddress().isEmpty()){
            StatusTextView.setText("ACTIVE");
            StatusTextView.setTextColor(getResources().getColor(R.color.green));

        }else {
            StatusTextView.setText("INACTIVE");
            StatusTextView.setTextColor(getResources().getColor(R.color.red));

        }
    }

    public void setTripCoords(Trip trip){

        final Trip tripp = trip;


        if(StatusTextView.getText()=="INACTIVE") {


            String url = "https://api.geospark.co/v1/api/trip/route?trip_id=" + trip.getTripID();

            OkHttpClient client = new OkHttpClient();


            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Api-Key", "0a39fd0363c04286b0959cc50d6b9120")
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String myResponse = response.body().string();

                        OfficerPage.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList<ArrayList<Double>> coordinates = extractTripCoords(myResponse);
                                tripp.setTripCoord(coordinates);
                                CoordTextView.setText(tripp.getTripCoord().toString());
                            }
                        });
                    }
                }
            });
        }
        else {
            String url = "https://api.geospark.co/v1/api/location/?user_id=" + uid;

            OkHttpClient client = new OkHttpClient();


            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Api-Key", "0a39fd0363c04286b0959cc50d6b9120")
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String myResponse = response.body().string();

                        OfficerPage.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList<ArrayList<Double>> coordinates = extractTripCoordsACTIVE(myResponse);
                                tripp.setTripCoord(coordinates);
                                Log.e("NEW LOCA",coordinates.toString());
                                CoordTextView.setText(tripp.getTripCoord().toString());
                            }
                        });
                    }
                }
            });
        }

        String url = "https://api.geospark.co/v1/api/geofence/?tag=" + uid;

        OkHttpClient client = new OkHttpClient();


        final Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Api-Key", "0a39fd0363c04286b0959cc50d6b9120")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    Log.e("GEOFENCE",response.toString());

                    OfficerPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            getGeoFenceCoords(myResponse);
                        }
                    });
                }
            }
        });

        String url2 = "https://api.geospark.co/v1/api/geofence/?tag=" + uid + "BP";

        OkHttpClient client2 = new OkHttpClient();


        final Request request2 = new Request.Builder()
                .url(url2)
                .get()
                .addHeader("Api-Key", "0a39fd0363c04286b0959cc50d6b9120")
                .build();


        client2.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    Log.e("GEOFENCE",response.toString());

                    OfficerPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            getBeatPoints(myResponse);
                        }
                    });
                }
            }
        });


    }

    public ArrayList<ArrayList<Double>> getBeatPoints(String response){

        ArrayList<ArrayList<Double>> BeatPoints = new ArrayList<ArrayList<Double>>();

        if (TextUtils.isEmpty(response)) {
            return null;
        }

        Log.e("RESPONSE!!!!",response);


        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(response);

            JSONObject data = baseJsonResponse.getJSONObject("data");


            JSONArray locations = data.getJSONArray("geofences");

            for(int i=0; i<locations.length(); i++ ){
                JSONObject currentObject = locations.getJSONObject(i);

                JSONObject center = currentObject.getJSONObject("geometry_center");

                JSONArray coordinates;
                try {
                    coordinates = center.getJSONArray("coordinates");
                    for(int j=0; j<coordinates.length(); j++){


                        ArrayList<Double> coord = new ArrayList<Double>();


                        coord.add((Double) coordinates.get(j));

                        Log.e("ONEBP", coord.toString());


                        BeatPoints.add(coord);

                    }
                } catch(JSONException e){
                    BeatPoints = null;
                }




            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        BeatPointBundle = BeatPoints;
        return BeatPoints;
    }

    public ArrayList<ArrayList<Double>> getGeoFenceCoords(String response){
        ArrayList<ArrayList<Double>> geoFence = new ArrayList<ArrayList<Double>>();

        if (TextUtils.isEmpty(response)) {
            return null;
        }

        Log.e("RESPONSE",response);


        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(response);

            JSONObject data = baseJsonResponse.getJSONObject("data");


            JSONArray locations = data.getJSONArray("geofences");

            JSONObject geofencedata = locations.getJSONObject(0);

            JSONObject geometry = geofencedata.getJSONObject("geometry");

            JSONArray coords = geometry.getJSONArray("coordinates");

            JSONArray actualCoords = coords.getJSONArray(0);



            for(int i=0; i<actualCoords.length(); i++){


                ArrayList<Double> coord = new ArrayList<Double>();

                JSONArray coordArray = actualCoords.getJSONArray(i);


                for (int j= 0; j < coordArray.length(); j++) {
                    coord.add((Double) coordArray.get(j));
                }

                geoFence.add(coord);

            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        geofenceBundle = geoFence;
        return geoFence;
    }

    public ArrayList<ArrayList<Double>> extractTripCoordsACTIVE(String response){
        if (TextUtils.isEmpty(response)) {
            return null;
        }

        Log.e("RESPONSE",response);


        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(response);

            JSONObject data = baseJsonResponse.getJSONObject("data");

            JSONArray locations = data.getJSONArray("locations");
            Log.e("NO LOCA", locations.toString());

            for(int i=0; i<locations.length(); i++){

                JSONObject currentObject = locations.getJSONObject(i);
                JSONObject coordObj;
                JSONArray coordArray = new JSONArray();

                try {
                    coordObj = currentObject.getJSONObject("coordinates");
                    coordArray = coordObj.getJSONArray("coordinates");
                }
                catch(Exception e){

                }

                Log.e("NEWWW LOCA", coordArray.toString());

                ArrayList<Double> coord = new ArrayList<Double>();


                for (int j= 0; j < coordArray.length(); j++) {
                    coord.add((Double) coordArray.get(j));
                }

                result.add(coord);

            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return result;


    }
    public ArrayList<ArrayList<Double>> extractTripCoords(String response){
        if (TextUtils.isEmpty(response)) {
            return null;
        }

        Log.e("RESPONSE",response);

        ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();


        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(response);

            JSONArray data = baseJsonResponse.getJSONArray("data");

            Log.e("DATATRIPCOORDS",data.toString());


            // Extract the JSONArray associated with the key called "users",
            // which represents a list of users.
            for(int i=0; i<data.length(); i++) {
                JSONObject currentData = data.getJSONObject(i);

                JSONArray currentCoord = currentData.getJSONArray("coordinates");

                ArrayList<Double> coord = new ArrayList<Double>();

                for (int j= 0; j < currentCoord.length(); j++) {
                    coord.add((Double) currentCoord.get(j));
                }

                result.add(coord);

            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return result;

    }


    public static Trip extractTripIDSFromJson(String fieldofficerJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(fieldofficerJSON)) {
            return null;
        }

        Log.e("RESPONSE",fieldofficerJSON);

        Trip lastTrip = null;

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(fieldofficerJSON);

            JSONObject data = baseJsonResponse.getJSONObject("data");

            Log.e("DATA",data.toString());

            // Extract the JSONArray associated with the key called "users",
            // which represents a list of users.
            JSONArray tripsArray = data.getJSONArray("trips");
            if(tripsArray.length()==0){
                lastTrip = new Trip("","","","","", Double.parseDouble("0"),Double.parseDouble("0"));
                return lastTrip;
            }

            // For each officer in the officerArray, create an FieldOfficer object


            // Get a single officer at position i within the list of officers
            JSONObject currentTrip = tripsArray.getJSONObject(0);
            Log.e("CURRENTTRIP",currentTrip.toString());


            // For a given officer, extract the UID associated with the
            // officer.

            // Extract the value for the key called "id"
            String TripID = currentTrip.getString("trip_id");
            String startAdd = currentTrip.getString("start_address");
            String endAdd;
            String endTime;
            Double dur;
            Double dis;
            try {
                endAdd = currentTrip.getString("end_address");
                endTime = currentTrip.getString("trip_ended_at");
                dur = currentTrip.getDouble("duration");
                dis = currentTrip.getDouble("distance_covered");
            } catch(JSONException e){
                endAdd="";
                endTime="";
                dur=0.0;
                dis=0.0;
            }
            String startTime = currentTrip.getString("trip_started_at");




            lastTrip = new Trip(TripID,startAdd, endAdd,startTime,endTime,dur,dis);


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return lastTrip;
    }

}

