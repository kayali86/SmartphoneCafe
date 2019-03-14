package com.kayali_developer.smartphonecafe.data.remote;


public class ApiUtils {
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/";

    private ApiUtils() {
    }

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
