package com.example.myapplication.add_location_management;

public class Comments {

    private int groupNumber;
    private String locationName;
    private String locationAddress;
    private String comments;

    public Comments(int groupNumber, String locationName, String locationAddress, String comments) {
        this.groupNumber = groupNumber;
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.comments = comments;
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
