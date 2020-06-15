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
import android.graphics.Canvas;
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

//:: A class to handle showing the user a map using google map api :://
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private ArrayList<Marker> markersArray;
    private ArrayList<Marker> currentMarkers;
    private GoogleMap googleMap;
    private String currentUserEmail;
    private LocationManager locationManager;
    private ArrayList<com.example.mapapp.Location> onlineUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //:: A location listener class to get the latest long/lat values :://
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS);

        markersArray = new ArrayList<>();
        currentMarkers = new ArrayList<>();
        onlineUsers = new ArrayList<>();


        //:: Getting logged in email from LoginActivity :://
        currentUserEmail = getIntent().getExtras().getString("USER");
        getOnlineUsers();

        final Button btnShowUser = findViewById(R.id.showUser);

        //:: A button to handle map camera to move back to user's location :://
        btnShowUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (com.example.mapapp.Location location : onlineUsers) {
                    if (location.email.equals(currentUserEmail)) {
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

    //:: A method to get all current online users :://
    private void getOnlineUsers() {
        final XML_Request onlineUsers = new XML_Request(XML_Request.API, "GET") {
            @Override
            void onResponse() {
                MapActivity.this.onlineUsers = getResponse();
            }
        };

        Thread t = new Thread(onlineUsers, "");
        t.start();
    }

    //:: A method to get the last know location using wifi :://
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
                    supportMapFragment.getMapAsync(MapActivity.this);
                }
            }
        });
    }

    LocationListener locationListenerGPS = new LocationListener() {
        //:: A method that listens for location change and forwards it to the API :://
        @Override
        public void onLocationChanged(android.location.Location location) {
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    XML_Request xml_request = new XML_Request(XML_Request.LONGLAT_URL, "POST");
                    try {
                        xml_request.longLat(currentUserEmail, longitude, latitude);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

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

    //:: A method to show all user markers on the map :://
    private void displayMarkers() {
        if (googleMap != null) {
            getOnlineUsers();
            try {
                for (com.example.mapapp.Location location : onlineUsers) {
                    if (location.email.equals(currentUserEmail)) {
                        markersArray.add(createMarker(googleMap, location.getLatitude(), location.getLongitude(), location.email, "Lat: " + location.getLatitude() + ", Long: " + location.getLongitude(), false));
                        LatLng latLng = new LatLng(Double.parseDouble(location.latitude), Double.parseDouble(location.longitude));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    } else {
                        markersArray.add(createMarker(googleMap, location.getLatitude(), location.getLongitude(), location.email, "Lat: " + location.getLatitude() + ", Long: " + location.getLongitude(), true));
                    }
                }
            } catch (Exception er) {
            }
        }
    }

    //:: A method that is called when the google map is on ready state. :://
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        displayMarkers();
        updateMap();
    }

    //:: A method that checks for online users continuously and redraw map markers  :://
    public synchronized void updateMap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(4000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (Marker marker : markersArray) {
                                    marker.remove();
                                }
                                getOnlineUsers();
                                try {
                                    for (com.example.mapapp.Location location : onlineUsers) {
                                        if (location.email.equals(currentUserEmail)) {
                                            markersArray.add(createMarker(googleMap, location.getLatitude(), location.getLongitude(), location.email, "Lat: " + location.getLatitude() + ", Long: " + location.getLongitude(), false));
                                        } else {
                                            markersArray.add(createMarker(googleMap, location.getLatitude(), location.getLongitude(), location.email, "Lat: " + location.getLatitude() + ", Long: " + location.getLongitude(), true));
                                        }
                                    }
                                } catch (Exception er) {
                                }

                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //:: A method that converts vector assets to be used as a icon for google map  :://
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int verctorResID, boolean isRandomColor) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, verctorResID);
        if (!isRandomColor) {
            vectorDrawable.setTint(getResources().getColor(R.color.colorAccent));
        } else {
            vectorDrawable.setTint(getResources().getColor(R.color.orange));
        }
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //:: A method that creates map markers and returns them to be added to the mapMarker array later to be removed  :://
    protected Marker createMarker(GoogleMap googleMap, double latitude, double longitude, String title, String snippet, boolean isRandomColor) {
        return googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_map_man, isRandomColor)));
    }


    //:: A method that handle location permissions :://
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                }
                break;
        }
    }

    //:: A method that overrides back-press causing the user to log out :://
    @Override
    public void onBackPressed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                XML_Request xml_request = new XML_Request(XML_Request.LOGOUT_URL, "POST");
                try {
                    String res = xml_request.logoutUser(currentUserEmail);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    overridePendingTransition(0, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}