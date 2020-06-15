package com.example.mapapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
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

import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    ArrayList<Marker> markersArray;
    private ArrayList<Marker> currentMarkers;
    private GoogleMap googleMap;
    private String currentUserEmail;
    private ArrayList<com.example.mapapp.Location> onlineUsers;
   // private ArrayList<com.example.mapapp.Location> updatedOnlineUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        tick();

    }
    public synchronized void tick(){
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
                                            LatLng latLng=new LatLng(Double.parseDouble(location.latitude),Double.parseDouble(location.longitude));
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
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
        if(isRandomColor){
            vectorDrawable.setTint(Color.parseColor(getRandomColor()));
        }else {
            vectorDrawable.setTint(getResources().getColor(R.color.colorAccent));
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
    private class MarkerData{
        private double latitude;
        private double longitude;
        private String title;
        private String snippet;
        private int iconResID;

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getTitle() {
            return title;
        }

        public String getSnippet() {
            return snippet;
        }

        public int getIconResID() {
            return iconResID;
        }

        public MarkerData(double latitude, double longitude, String title, String snippet, int iconResID) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.title = title;
            this.snippet = snippet;
            this.iconResID = iconResID;
        }
    }
}