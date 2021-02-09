package jakubw.pracainz.goalsexecutor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Pattern;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.util.Log;

import jakubw.pracainz.goalsexecutor.Model.CalendarEvent;

public class GoogleCalendarReader {

    public static final String[] FIELDS = {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT,                 // 3
            CalendarContract.Calendars.VISIBLE
    };


    static Cursor cursor;

    public static ArrayList<CalendarEvent> getEventsFromGoogleCalendar(Context context, String ownerEmail) {
        ArrayList eventList = new ArrayList<CalendarEvent>();
        ContentResolver contentResolver = context.getContentResolver();

        // Pobiera liste wszystkich kalendarzy z urzadzenia
        cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
                FIELDS, null, null, null);

        HashSet<String> calendarIds = new HashSet<String>();

        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String _id = cursor.getString(0);
//                    String columnName = cursor.getColumnName(0);
                    String ownerName = cursor.getString(3);
                    String calendarName = cursor.getString(2);
                    Boolean selected = !cursor.getString(4).equals("0");

                    Log.i("googlecalendarreader", "Id: " + _id + " Owner: " + ownerName + " Selected: " + selected + " Calendar: " + calendarName);
                    calendarIds.add(_id);
                }
            }
        } catch (AssertionError ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Pobiera wydarzenia z kalendarza konkretnego uzytkownika z ostatniego roku
        for (String id : calendarIds) {
            Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
//            Uri.Builder builder = Uri.parse("content://com.android.calendar/calendars").buildUpon();
            long now = new Date().getTime();

            ContentUris.appendId(builder, now - DateUtils.DAY_IN_MILLIS * 1000);
            ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS * 1000);

            Cursor eventCursor = contentResolver.query(builder.build(),
                    new String[]{"title", "begin", "end", "allDay"}, getSelection(ownerEmail),
                    null, null);

            Log.i("googlecalendarreader", ("eventCursor count=" + eventCursor.getCount()));
            if (eventCursor.getCount() > 0) {

                if (eventCursor.moveToFirst()) {
                    do {
//                        Object mbeg_date, beg_date, beg_time, end_date, end_time;

                        final String title = eventCursor.getString(0);
                        final Date begin = new Date(eventCursor.getLong(1));
//                        final Date end = new Date(eventCursor.getLong(2));
//                        final Boolean allDay = !eventCursor.getString(3).equals("0");
                        SimpleDateFormat postFormater = new SimpleDateFormat("h,dd,MM,yyyy");
                        String beginNew = postFormater.format(begin);
                        String[] table = beginNew.split(",");
                        int hour = Integer.parseInt(table[0]);
                        int day = Integer.parseInt(table[1]);
                        int month = Integer.parseInt(table[2]);
                        int year = Integer.parseInt(table[3]);
                        int id_event = new Random().nextInt();
                        // month -1 bo od 0 sie licza miesiace
                        eventList.add(new CalendarEvent(title, String.valueOf(id_event), hour, day, month - 1, year));

                    }
                    while (eventCursor.moveToNext());
                }
            }
            break;
        }
        return eventList;
    }

    public static String getSelection(String ownerEmail) {
        Calendar startTime = Calendar.getInstance();

        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.DATE, 365); // ile nastepnych dni ma brac

        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis()
                + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis()
                + " ) AND ( deleted != 1 ) AND " + CalendarContract.Events.OWNER_ACCOUNT + " = '" + ownerEmail + "')";

        return selection;
    }
}