package com.example.mapapp;

import androidx.annotation.NonNull;

//:: A class to handle parsing XML :://
public class Location {
    public String email, password, longitude, latitude;

    public Location(String email, String password, String longitude, String latitude) {
        this.email = email;
        this.password = password;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Location() {
    }

    @NonNull
    @Override
    public String toString() {
        return email + ",\n" +
                latitude + ",\n" +
                longitude + ",\n";
    }

    public String getEmail() {
        return email;
    }

    public Double getLongitude() {
        return Double.parseDouble(longitude);
    }

    public Double getLatitude() {
        return Double.parseDouble(latitude);
    }
}
