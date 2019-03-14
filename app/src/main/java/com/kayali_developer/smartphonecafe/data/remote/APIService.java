package com.kayali_developer.smartphonecafe.data.remote;


import com.kayali_developer.smartphonecafe.data.model.FirebasePostMessageByToken;
import com.kayali_developer.smartphonecafe.data.model.FirebasePostMessageByTopic;
import com.kayali_developer.smartphonecafe.data.model.FirebasePostNotificationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({"Content-Type:application/json", "Authorization:key=AAAArabHBm8:APA91bHg1YUipI-oLZ3a4bykKl1LiChqjienUlubKAC8Xu6VKqDo585eZ_LVKSw-nRRHQKyGu7ak6gh78ms46XamBinPxBn9jnoTNXpHFMD39gukbaN0OQyy5N05LujdhS3W1BZdkWqw"})
    @POST("send")
    Call<FirebasePostNotificationResponse> postEventByTopic(@Body FirebasePostMessageByTopic firebasePostMessageByTopic);

    @Headers({"Content-Type:application/json", "Authorization:key=AAAArabHBm8:APA91bHg1YUipI-oLZ3a4bykKl1LiChqjienUlubKAC8Xu6VKqDo585eZ_LVKSw-nRRHQKyGu7ak6gh78ms46XamBinPxBn9jnoTNXpHFMD39gukbaN0OQyy5N05LujdhS3W1BZdkWqw"})
    @POST("send")
    Call<FirebasePostNotificationResponse> postEventByToken(@Body FirebasePostMessageByToken firebasePostMessageByToken);
}