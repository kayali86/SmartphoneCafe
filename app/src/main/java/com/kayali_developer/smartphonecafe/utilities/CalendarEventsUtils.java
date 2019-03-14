package com.kayali_developer.smartphonecafe.utilities;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;

public class CalendarEventsUtils {

    public static void setNewEvent(Context context, long startTime, long endTime){
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
/*
        Calendar cal = Calendar.getInstance();
        long startTime = cal.getTimeInMillis();
        long endTime = cal.getTimeInMillis() + 60 * 60 * 1000;
*/
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

        intent.putExtra(CalendarContract.Events.TITLE, "Smartphone Cafe");
        intent.putExtra(CalendarContract.Events.DESCRIPTION, "Caritashaus Maria Frieden");
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Bernhard-Itzel-Straße 3, 35392 Gießen");

        context.startActivity(intent);
    }

}
