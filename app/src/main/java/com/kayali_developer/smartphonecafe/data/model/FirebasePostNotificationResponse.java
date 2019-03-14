package com.kayali_developer.smartphonecafe.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FirebasePostNotificationResponse {
    @SerializedName("multicast_id")
    private long multicast_id;
    @SerializedName("success")
    private int success;
    @SerializedName("failure")
    private int failure;
    @SerializedName("canonical_ids")
    private int canonical_ids;
    @SerializedName("results")
    private List<ResultsBean> results;

    public long getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(long multicast_id) {
        this.multicast_id = multicast_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCanonical_ids() {
        return canonical_ids;
    }

    public void setCanonical_ids(int canonical_ids) {
        this.canonical_ids = canonical_ids;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        @SerializedName("message_id")
        private String message_id;

        public String getMessage_id() {
            return message_id;
        }

        public void setMessage_id(String message_id) {
            this.message_id = message_id;
        }
    }
}
