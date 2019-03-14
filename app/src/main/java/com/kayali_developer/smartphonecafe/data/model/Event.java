package com.kayali_developer.smartphonecafe.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Event implements Cloneable{
    @SerializedName("eventId")
    private String eventId;
    @SerializedName("startTime")
    private long startTime;
    @SerializedName("endTime")
    private long endTime;
    @SerializedName("topic")
    private String topic;
    @SerializedName("location")
    private String location;
    @SerializedName("description")
    private String description;
    @SerializedName("organizerUID")
    private String organizerUID;
    @SerializedName("organizerFullName")
    private String organizerFullName;
    @SerializedName("membersUIDs")
    private List<String> membersUIDs;
    @SerializedName("isNextEvent")
    private boolean isNextEvent;
    @SerializedName("feedback")
    private double feedback;
    @SerializedName("feedbackCount")
    private int feedbackCount;
    @SerializedName("ratersUid")
    private List<String> ratersUid;

    public Event() {
    }

    public Event(String eventId, long startTime, long endTime, String topic, String location, String description, String organizerUID, String organizerFullName, List<String> membersUIDs, boolean isNextEvent,
        double feedback, int feedbackCount, List<String> ratersUid) {

        this.eventId = eventId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.topic = topic;
        this.location = location;
        this.description = description;
        this.organizerUID = organizerUID;
        this.organizerFullName = organizerFullName;
        this.membersUIDs = membersUIDs;
        this.isNextEvent = isNextEvent;
        this.feedback = feedback;
        this.feedbackCount = feedbackCount;
        this.ratersUid = ratersUid;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizerUID() {
        return organizerUID;
    }

    public void setOrganizerUID(String organizerUID) {
        this.organizerUID = organizerUID;
    }

    public String getOrganizerFullName() {
        return organizerFullName;
    }

    public void setOrganizerFullName(String organizerFullName) {
        this.organizerFullName = organizerFullName;
    }

    public List<String> getMembersUIDs() {
        return membersUIDs;
    }

    public void setMembersUIDs(List<String> membersUIDs) {
        this.membersUIDs = membersUIDs;
    }

    public boolean isNextEvent() {
        return isNextEvent;
    }

    public void setNextEvent(boolean nextEvent) {
        isNextEvent = nextEvent;
    }

    public double getFeedback() {
        return feedback;
    }

    public void setFeedback(double feedback) {
        this.feedback = feedback;
    }

    public int getFeedbackCount() {
        return feedbackCount;
    }

    public void setFeedbackCount(int feedbackCount) {
        this.feedbackCount = feedbackCount;
    }

    public List<String> getRatersUid() {
        return ratersUid;
    }

    public void setRatersUid(List<String> ratersUid) {
        this.ratersUid = ratersUid;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
