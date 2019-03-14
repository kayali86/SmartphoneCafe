package com.kayali_developer.smartphonecafe.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User implements Cloneable{
    public static final int DEVELOPER_PERMISSION_TYPE = 4;
    public static final int SUPER_ADMIN_PERMISSION_TYPE = 3;
    public static final int ADMIN_PERMISSION_TYPE = 2;
    public static final int HELPER_PERMISSION_TYPE = 1;
    public static final int MEMBER_PERMISSION_TYPE = 0;
    public static final int INACTIVE_USER_PERMISSION_TYPE = -1;

    @SerializedName("uid")
    private String uid;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("permissionType")
    private int permissionType;
    @SerializedName("phoneNr")
    private String phoneNr;
    @SerializedName("email")
    private String email;
    @SerializedName("imageURL")
    private String imageURL;
    @SerializedName("feedback")
    private double feedback;
    @SerializedName("feedbackCount")
    private int feedbackCount;
    @SerializedName("ratersUid")
    private List<String> ratersUid;

    public User() {
    }

    public User(String uid, String firstName, String lastName, int permissionType, String phoneNr, String email, String imageURL,
                double feedback, int feedbackCount, List<String> ratersUid) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.permissionType = permissionType;
        this.phoneNr = phoneNr;
        this.email = email;
        this.imageURL = imageURL;
        this.feedback = feedback;
        this.feedbackCount = feedbackCount;
        this.ratersUid = ratersUid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(int permissionType) {
        this.permissionType = permissionType;
    }

    public String getPhoneNr() {
        return phoneNr;
    }

    public void setPhoneNr(String phoneNr) {
        this.phoneNr = phoneNr;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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
