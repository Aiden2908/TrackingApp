package com.example.mapapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    ArrayList<Marker> markersArray;
    private ArrayList<Marker> currentMarkers;
    private GoogleMap googleMap;
    private String currentUserEmail;
    LocationManager locationManager;
    private ArrayList<com.example.mapapp.Location> onlineUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS);

        markersArray = new ArrayList<>();
        currentMarkers=new ArrayList<>();
        onlineUsers=new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            currentUserEmail= null;
            return;
        } else {
            currentUserEmail= extras.getString("USER");
        }
        getOnlineUsers();

        final Button btnShowUser=findViewById(R.id.showUser);

        btnShowUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(com.example.mapapp.Location location:onlineUsers) {
                    if(location.email.equals(currentUserEmail)){
                        LatLng latLng = new LatLng(Double.parseDouble(location.latitude), Double.parseDouble(location.longitude));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    }
                }

            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }
    private void getOnlineUsers(){
        final XML_Request onlineUsers = new XML_Request(XML_Request.API,"GET") {
            @Override
            void onResponse() {
                //:: Once a response has received comeback to UI thread and update :://
                MapActivity.this.onlineUsers=getResponse();
                Log.i(this.getClass().getName(),"::::::::::"+MapActivity.this.onlineUsers);

            }
        };

        Thread t = new Thread(onlineUsers, "");
        t.start();
    }
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    currentLocation=location;
                    SupportMapFragment supportMapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.googleMap);
                    supportMapFragment.getMapAsync(MapActivity.this);
                }
            }
        });
    }
    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            final double latitude=location.getLatitude();
            final double longitude=location.getLongitude();
            String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void displayMarkers(){
        if(googleMap!=null) {
            getOnlineUsers();
            try {
                for(com.example.mapapp.Location location:onlineUsers){
                    if(location.email.equals(currentUserEmail)){
                        markersArray.add(createMarker(googleMap,location.getLatitude(),location.getLongitude(),location.email,"Lat: "+location.getLatitude()+", Long: "+location.getLongitude(),false));
                        LatLng latLng=new LatLng(Double.parseDouble(location.latitude),Double.parseDouble(location.longitude));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                    }else{
                        markersArray.add(createMarker(googleMap,location.getLatitude(),location.getLongitude(),location.email,"Lat: "+location.getLatitude()+", Long: "+location.getLongitude(),true));
                    }
                }
            }catch (Exception er){}
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
        displayMarkers();
        updateMap();
    }
    public synchronized void updateMap(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(6000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(Marker marker:markersArray){
                                    marker.remove();
                                }
                                getOnlineUsers();
                                try {
                                    for(com.example.mapapp.Location location:onlineUsers){
                                        if(location.email.equals(currentUserEmail)){
                                            markersArray.add(createMarker(googleMap,location.getLatitude(),location.getLongitude(),location.email,"Lat: "+location.getLatitude()+", Long: "+location.getLongitude(),false));
                                        }else{
                                            markersArray.add(createMarker(googleMap,location.getLatitude(),location.getLongitude(),location.email,"Lat: "+location.getLatitude()+", Long: "+location.getLongitude(),true));
                                        }
                                    }
                                }catch (Exception er){}

                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int verctorResID,boolean isRandomColor){
        Drawable vectorDrawable= ContextCompat.getDrawable(context,verctorResID);
        if(!isRandomColor){
            vectorDrawable.setTint(getResources().getColor(R.color.colorAccent));
        }else {
            vectorDrawable.setTint(getResources().getColor(R.color.orange));
        }
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap=Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas =new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    protected Marker createMarker(GoogleMap googleMap,double latitude, double longitude, String title, String snippet,boolean isRandomColor) {
        return googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                //.icon(smallMarker));
                .icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_map_man,isRandomColor)));
    }
    private String getRandomColor() {
        String letters = "0123456789ABCDEF";
        String color = "#";
        for (int i = 0; i < 6; i++) {
            int o=(int)Math.floor(Math.random() * 16);
            color += letters.charAt(o);
        }
        return color;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getLastLocation();
                }
                break;
        }
    }

    private void updateUserLongLat(double lati,double longi){

    }

    @Override
    public void onBackPressed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                XML_Request xml_request=new XML_Request(XML_Request.LOGOUT_URL,"POST");
                try {
                    String res=xml_request.logoutUser(currentUserEmail);
                    Log.i(this.getClass().getName(),":::::::::: LOGED OUT"+res);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    overridePendingTransition(0,0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}