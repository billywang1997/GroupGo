package com.example.myapplication.helper.Model;

import com.example.myapplication.place_address_adapter.PlaceAddressItem;

import java.util.ArrayList;

public class MainPojo {
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Listclass> getPredictions() {
        return predictions;
    }

    public void setPredictions(ArrayList<Listclass> predictions) {
        this.predictions = predictions;
    }

    String status;
    ArrayList<Listclass> predictions;

}
