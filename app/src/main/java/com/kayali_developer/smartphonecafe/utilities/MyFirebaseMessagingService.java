package com.kayali_developer.smartphonecafe.utilities;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kayali_developer.smartphonecafe.data.model.Article;
import com.kayali_developer.smartphonecafe.data.model.Event;

import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessaging";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String notificationTitle = null;
        String notificationText = null;
        if (remoteMessage.getNotification() != null) {
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationText = remoteMessage.getNotification().getBody();
        }

        if (remoteMessage.getData().size() > 0) {
            Event nextEvent = null;
            Article newArticle = null;

            boolean isEvent = false;


            Map<String, String> receivedMap = remoteMessage.getData();

            for (Map.Entry entry : receivedMap.entrySet()){
                if (entry.getKey().equals("eventId")) {
                    isEvent = true;
                }
            }

            if (isEvent) {
                nextEvent = new Event();

                nextEvent.setEventId(receivedMap.get("eventId"));
                nextEvent.setOrganizerUID(receivedMap.get("organizerUID"));
                nextEvent.setMembersUIDs(new Gson().fromJson(receivedMap.get("membersUIDs"), new TypeToken<List<String>>() {
                }.getType()));
                nextEvent.setNextEvent(Boolean.valueOf(receivedMap.get("isNextEvent")));
                nextEvent.setDescription(receivedMap.get("description"));
                nextEvent.setLocation(receivedMap.get("location"));
                nextEvent.setTopic(receivedMap.get("topic"));
                nextEvent.setOrganizerFullName(receivedMap.get("organizerFullName"));

                Double feedback = null;
                try {
                    feedback = Double.valueOf(receivedMap.get("feedback"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (feedback != null){
                    nextEvent.setFeedback(feedback);
                }


                Integer feedbackCount = null;
                try {
                    feedbackCount = Integer.valueOf(receivedMap.get("feedbackCount"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (feedbackCount != null){
                    nextEvent.setFeedbackCount(feedbackCount);
                }


                Long startTime = null;
                try {
                    startTime = Long.valueOf(receivedMap.get("startTime"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Long endTime = null;
                try {
                    endTime = Long.valueOf(receivedMap.get("endTime"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (startTime != null) {
                    nextEvent.setStartTime(startTime);
                }
                if (endTime != null) {
                    nextEvent.setEndTime(endTime);
                }

            } else {

                newArticle = new Article();

                newArticle.setArticleId(receivedMap.get("articleId"));
                newArticle.setImageURL(receivedMap.get("imageURL"));
                newArticle.setTitle(receivedMap.get("title"));
                newArticle.setShortDescription(receivedMap.get("shortDescription"));
                newArticle.setText(receivedMap.get("text"));
                newArticle.setAuthor(receivedMap.get("author"));
                newArticle.setAuthorId(receivedMap.get("authorId"));

                Double feedback = null;
                try {
                    feedback = Double.valueOf(receivedMap.get("feedback"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (feedback != null){
                    newArticle.setFeedback(feedback);
                }


                Integer feedbackCount = null;
                try {
                    feedbackCount = Integer.valueOf(receivedMap.get("feedbackCount"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (feedbackCount != null){
                    newArticle.setFeedbackCount(feedbackCount);
                }


                Long publishDate = null;
                try {
                    publishDate = Long.valueOf(receivedMap.get("publishDate"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (publishDate != null) {
                    newArticle.setPublishDate(publishDate);
                }

            }


            if (nextEvent != null) {
                NotificationUtils.showNotification(this, nextEvent, notificationTitle, notificationText);
            } else{
                NotificationUtils.showNotification(this, newArticle, notificationTitle, notificationText);

            }

        }
        else {
            NotificationUtils.showNotification(this, null, notificationTitle, notificationText);
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>Refreshed token: " + s);
    }
}