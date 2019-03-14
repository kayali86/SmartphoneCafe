package com.kayali_developer.smartphonecafe.data.model;

import com.google.gson.annotations.SerializedName;

public class FirebasePostMessageByTopic {
    @SerializedName("to")
    private String topic;

    @SerializedName("data")
    private Object data;

    public FirebasePostMessageByTopic(String topic, Object data) {
        this.topic = topic;
        this.data = data;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Object getData() {
        return data;
    }

    public void setData(Event data) {
        this.data = data;
    }

}
