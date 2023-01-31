package com.example.myapplication.add_location_management;

public class Photos {

    private int groupNumber;
    private String locationName;
    private String locationAddress;
    private String photos;

    public Photos(int groupNumber, String locationName, String locationAddress, String photos) {
        this.groupNumber = groupNumber;
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.photos = photos;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }
}
