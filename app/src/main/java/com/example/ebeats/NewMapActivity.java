package com.example.ebeats;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;

public class NewMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            mMap = googleMap;

            PolygonOptions polygonOptions = new PolygonOptions().clickable(true);
            PolylineOptions polylineOptions = new PolylineOptions().clickable(true);
            ArrayList<MarkerOptions> markerOptionsArrayList = new ArrayList<>();

            // Add a marker in Sydney and move the camera
            // LatLng sydney = new LatLng(-34, 151);
            // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


            // Getting data from previous activity
            ArrayList<ArrayList<Double>> result = (ArrayList<ArrayList<Double>>) getIntent().getExtras().get("tripCoords");
            ArrayList<ArrayList<Double>> geofence = (ArrayList<ArrayList<Double>>) getIntent().getExtras().get("Geofence");
            Log.e("GEO",geofence.toString());
            // ArrayList<ArrayList<Double>> beatpoints = (ArrayList<ArrayList<Double>>) getIntent().getExtras().get("Beatpoints");

            ArrayList<LatLng> tripCoords = new ArrayList<>();
            ArrayList<LatLng> geofenceCoords = new ArrayList<>();
            // ArrayList<LatLng> beatpointsCoords = new ArrayList<>();

            Iterator<ArrayList<Double>> iterator = result.iterator();
            // Iterator<MarkerOptions> markerOptionsIterator = markerOptionsArrayList.iterator();

            while (iterator.hasNext()) {
                ArrayList<Double> next = iterator.next();
                //Log.d("main",next.get(1)+" "+next.get(0));
                try {
                    tripCoords.add(new LatLng(next.get(1), next.get(0)));
                    polylineOptions.add(new LatLng(next.get(1), next.get(0)));
                }
                catch (Exception e){
                    
                }
            }

            if (geofence == null) {
                Log.d("GEOFENCE", "onMapReady: OH NO NULLLL");
            }

            for (ArrayList<Double> coord : geofence) {
                Log.e("CURRENT coord", coord.toString());
                polygonOptions.add(new LatLng(coord.get(1), coord.get(0)));
            }

//        for (ArrayList<Double> coord : beatpoints) {
//            markerOptionsArrayList.add(new MarkerOptions().position(new LatLng(coord.get(1), coord.get(0))));
//        }

//        for (MarkerOptions markerOptions : markerOptionsArrayList) {
//            mMap.addMarker(markerOptions.title("Beat point"));
//        }
            mMap.addMarker(new MarkerOptions().position(new LatLng(13.03030840075909, 77.6029266447944)).title("Beat point"));
            mMap.addMarker(new MarkerOptions().position((new LatLng(13.053408375888093, 77.5785852620928))).title("Beat point"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(13.034607785852023, 77.54107900654324)).title("Beat point"));

            Log.e("TAG", tripCoords.toString());

            Log.e("GEOFENCEEEEEEE", "onMapReady: " + geofence.get(0));
//                new LatLng(-35.016, 143.321),
//                new LatLng(-34.747, 145.592),
//                new LatLng(-34.364, 147.891),
//                new LatLng(-33.501, 150.217),
//                new LatLng(-32.306, 149.248),
//                new LatLng(-32.491, 147.309)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mMap.addPolyline(polylineOptions);
            }

            mMap.addMarker(new MarkerOptions().position(tripCoords.get(tripCoords.size() - 1)).title("End point"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(tripCoords.get(tripCoords.size() - 1)));
            CameraPosition campos = new CameraPosition.Builder()
                    .target(tripCoords.get(tripCoords.size() - 1))
                    .zoom(17F)
                    .bearing(90)
                    .tilt(40)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(campos));

            polygonOptions.fillColor(R.color.red);
            mMap.addPolygon(polygonOptions);
        }
        catch (Exception e){
            Toast toast = Toast.makeText(NewMapActivity.this, "No geofence",Toast.LENGTH_SHORT);
            Log.e("ERROR",e.toString());
            toast.show();
        }
    }
}
