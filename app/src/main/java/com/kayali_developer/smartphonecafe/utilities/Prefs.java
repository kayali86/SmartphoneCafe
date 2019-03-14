package com.kayali_developer.smartphonecafe.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefs {

    public static void setNotificationInitializationStatus(Context context, boolean status) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notification_initialization_status", status);
        editor.apply();
    }

    public static boolean getNotificationInitializationStatus(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("notification_initialization_status", false);
    }

    public static void setGeneralNotificationStatus(Context context, boolean status) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("general_notification_status", status);
        editor.apply();
    }

    public static boolean getGeneralNotificationStatus(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("general_notification_status", false);
    }

    public static void setNewEventNotificationStatus(Context context, boolean status) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("new_event_notification_status", status);
        editor.apply();
    }

    public static boolean getNewEventNotificationStatus(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("new_event_notification_status", false);
    }

    public static void setNewArticleNotificationStatus(Context context, boolean status) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("new_article_notification_status", status);
        editor.apply();
    }

    public static boolean getNewArticleNotificationStatus(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("new_article_notification_status", false);
    }

    public static void setHelpersNotificationStatus(Context context, boolean status) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("helpers_notification_status", status);
        editor.apply();
    }

    public static boolean getHelpersNotificationStatus(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("helpers_notification_status", false);
    }

}
