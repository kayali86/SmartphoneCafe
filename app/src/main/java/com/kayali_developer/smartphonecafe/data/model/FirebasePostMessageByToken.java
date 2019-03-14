package com.kayali_developer.smartphonecafe.data.model;

import com.google.gson.annotations.*;

import java.util.List;

public class FirebasePostMessageByToken {

    @SerializedName("registration_ids")
    private List<String> registration_ids;

    @SerializedName("data")
    private Event data;

    public FirebasePostMessageByToken(List<String> registration_ids, Event data) {
        this.registration_ids = registration_ids;
        this.data = data;
    }

    public List<String> getRegistration_ids() {
        return registration_ids;
    }

    public void setRegistration_ids(List<String> registration_ids) {
        this.registration_ids = registration_ids;
    }

    public Event getData() {
        return data;
    }

    public void setData(Event data) {
        this.data = data;
    }
}
