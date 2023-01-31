package com.example.myapplication.place_address_adapter;

public class PlaceAddressItem {
    private String name;

    public String getName() {
        return name;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    private String formatted_address;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private double latitude;
    private double longitude;

    public PlaceAddressItem(String name, String formatted_address, double latitude, double longitude) {
        this.name = name;
        this.formatted_address = formatted_address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
