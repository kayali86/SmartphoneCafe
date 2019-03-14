package com.kayali_developer.smartphonecafe.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.kayali_developer.smartphonecafe.MainActivity;
import com.kayali_developer.smartphonecafe.R;
import com.kayali_developer.smartphonecafe.data.model.Article;
import com.kayali_developer.smartphonecafe.data.model.Event;

import androidx.core.app.NotificationCompat;

class NotificationUtils {
    private static final String NOTIFICATION_CHANNEL_ID_STR = "SmartphoneCafe";
    private static final int NOTIFICATION_CHANNEL_ID_INT = 35392;

    static void showNotification(Context context, Object object, String notificationTitle, String notificationBody) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_STR);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
        builder.setContentIntent(mainPendingIntent);

        if (object instanceof Event){
            Event event = (Event) object;

            bigTextStyle.setBigContentTitle(event.getTopic());
            bigTextStyle.bigText(event.getLocation() + " " + AppDateUtils.longDateToDeFormat(event.getStartTime()) + "\n" + event.getDescription());
            if (event.getStartTime() != -1 && event.getEndTime() != -1) {

                Intent joinToEventIntent = new Intent(context, MainActivity.class);
                joinToEventIntent.setAction(MainActivity.JOIN_TO_EVENT_ACTION);
                joinToEventIntent.putExtra(MainActivity.NEXT_EVENT_ID_KEY, event.getEventId());
                PendingIntent joinToEventPendingIntent = PendingIntent.getActivity(context, 0, joinToEventIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Action joinToEventAction = new NotificationCompat.Action(android.R.drawable.ic_input_add, "Join to Event", joinToEventPendingIntent);
                builder.addAction(joinToEventAction);
            }

        }else if (object instanceof Article){
            Article article = (Article) object;

            bigTextStyle.setBigContentTitle(article.getTitle());
            bigTextStyle.bigText(article.getShortDescription());

            Intent showArticleIntent = new Intent(context, MainActivity.class);
            showArticleIntent.putExtra(MainActivity.NEW_ARTICLE_KEY, new Gson().toJson(article));
            PendingIntent joinToEventPendingIntent = PendingIntent.getActivity(context, 0, showArticleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action joinToEventAction = new NotificationCompat.Action(android.R.drawable.ic_input_add, "Show the Article", joinToEventPendingIntent);
            builder.addAction(joinToEventAction);

        }else if (notificationBody != null && !TextUtils.isEmpty(notificationBody)){
            bigTextStyle.setBigContentTitle(notificationTitle);
            bigTextStyle.bigText(notificationBody);
        }

        builder.setStyle(bigTextStyle);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIconBitmap);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setColor(context.getResources().getColor(R.color.colorAccent));
        builder.setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_STR, NOTIFICATION_CHANNEL_ID_STR, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(NOTIFICATION_CHANNEL_ID_STR);
            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        } else {
            builder.setPriority(Notification.PRIORITY_MAX);
        }



        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(NOTIFICATION_CHANNEL_ID_INT, notification);
    }

}
