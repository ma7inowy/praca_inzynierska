package jakubw.pracainz.goalsexecutor;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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

public class ReadCalendar {

    public static final String[] FIELDS = {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT,                  // 3
            CalendarContract.Calendars.VISIBLE
//            CalendarContract.Calendars.ACCOUNT_TYPE
    };


    static Cursor cursor;

    public static ArrayList<CalendarEvent> readCalendar(Context context) {
        ArrayList eventList = new ArrayList<CalendarEvent>();

        ContentResolver contentResolver = context.getContentResolver();

        // Fetch a list of all calendars synced with the device, their display names and whether the

        cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
                FIELDS, null, null, null);

        HashSet<String> calendarIds = new HashSet<String>();

        try {
            Log.i("readcalendar", "Count=" + cursor.getCount());
            if (cursor.getCount() > 0) {
                Log.i("readcalendar", "the control is just inside of the cursor.count loop");
                while (cursor.moveToNext()) {
                    String _id = cursor.getString(0);
                    String columnName = cursor.getColumnName(0);
                    String displayName = cursor.getString(3);
                    String displayCalendarName = cursor.getString(2);
                    Boolean selected = !cursor.getString(4).equals("0");

                    Log.i("readcalendar", "Id: " + _id + " Owner Name: " + displayName + " Selected: " + selected + " CalendarName: " + displayCalendarName);
                    calendarIds.add(_id);
                }
            }
        } catch (AssertionError ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // For each calendar, display all the events from the previous week to the end of next week.
        for (String id : calendarIds) {
            Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
//            Uri.Builder builder = Uri.parse("content://com.android.calendar/calendars").buildUpon();
            long now = new Date().getTime();

            ContentUris.appendId(builder, now - DateUtils.DAY_IN_MILLIS * 1000);
            ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS * 1000);

            Cursor eventCursor = contentResolver.query(builder.build(),
                    new String[]{"title", "begin", "end", "allDay"}, getSelection(),
                    null, null);

            Log.i("readcalendar", ("eventCursor count=" + eventCursor.getCount()));
            if (eventCursor.getCount() > 0) {

                if (eventCursor.moveToFirst()) {
                    do {
                        Object mbeg_date, beg_date, beg_time, end_date, end_time;

                        final String title = eventCursor.getString(0);
                        final Date begin = new Date(eventCursor.getLong(1));
                        final Date end = new Date(eventCursor.getLong(2));
                        final Boolean allDay = !eventCursor.getString(3).equals("0");

                        /*  System.out.println("Title: " + title + " Begin: " + begin + " End: " + end +
                                    " All Day: " + allDay);
                        */
                        Log.i("readcalendar", "Title:" + title);
                        //TESTOWANKO DANE DO Date
                        SimpleDateFormat postFormater = new SimpleDateFormat("h,dd,MM,yyyy");
                        String beginNew = postFormater.format(begin);
                        String[] table = beginNew.split(",");
                        int hour = Integer.parseInt(table[0]);
                        int day = Integer.parseInt(table[1]);
                        int month = Integer.parseInt(table[2]);
                        int year = Integer.parseInt(table[3]);
                        int id_event = new Random().nextInt();
                        eventList.add(new CalendarEvent(title,String.valueOf(id_event),hour,day,month,year));

//                        LocalDate dataa = new LocalDate(year,month,day,hour,0); tak sie NIE DA :(
//                        LocalDate dataa = new LocalDate(year,month,day);
//                        Date dataa = new Date(year, month, day);
                        Log.i("readcalendar", "Begin:" + beginNew);
                        Log.i("readcalendar", "End:" + end);
                        Log.i("readcalendar", "All Day:" + allDay);

                        /* the calendar control metting-begin events Respose  sub-string (starts....hare) */

                        Pattern p = Pattern.compile(" ");
                        String[] items = p.split(begin.toString());
                        String scalendar_metting_beginday, scalendar_metting_beginmonth, scalendar_metting_beginyear, scalendar_metting_begindate, scalendar_metting_begintime, scalendar_metting_begingmt;

                        scalendar_metting_beginday = items[0];
                        scalendar_metting_beginmonth = items[1];
                        scalendar_metting_begindate = items[2];
                        scalendar_metting_begintime = items[3];
                        scalendar_metting_begingmt = items[4];
                        scalendar_metting_beginyear = items[5];


                        String calendar_metting_beginday = scalendar_metting_beginday;
                        String calendar_metting_beginmonth = scalendar_metting_beginmonth.toString().trim();

                        int calendar_metting_begindate = Integer.parseInt(scalendar_metting_begindate.trim());

                        String calendar_metting_begintime = scalendar_metting_begintime.toString().trim();
                        String calendar_metting_begingmt = scalendar_metting_begingmt;
                        int calendar_metting_beginyear = Integer.parseInt(scalendar_metting_beginyear.trim());


                        Log.i("readcalendar", "calendar_metting_beginday=" + calendar_metting_beginday);

                        Log.i("readcalendar", "calendar_metting_beginmonth =" + calendar_metting_beginmonth);

                        Log.i("readcalendar", "calendar_metting_begindate =" + calendar_metting_begindate);

                        Log.i("readcalendar", "calendar_metting_begintime=" + calendar_metting_begintime);

                        Log.i("readcalendar", "calendar_metting_begingmt =" + calendar_metting_begingmt);

                        Log.i("readcalendar", "calendar_metting_beginyear =" + calendar_metting_beginyear);

                        /* the calendar control metting-begin events Respose  sub-string (starts....ends) */

                        /* the calendar control metting-end events Respose  sub-string (starts....hare) */

                        Pattern p1 = Pattern.compile(" ");
                        String[] enditems = p.split(end.toString());
                        String scalendar_metting_endday, scalendar_metting_endmonth, scalendar_metting_endyear, scalendar_metting_enddate, scalendar_metting_endtime, scalendar_metting_endgmt;

                        scalendar_metting_endday = enditems[0];
                        scalendar_metting_endmonth = enditems[1];
                        scalendar_metting_enddate = enditems[2];
                        scalendar_metting_endtime = enditems[3];
                        scalendar_metting_endgmt = enditems[4];
                        scalendar_metting_endyear = enditems[5];


                        String calendar_metting_endday = scalendar_metting_endday;
                        String calendar_metting_endmonth = scalendar_metting_endmonth.toString().trim();

                        int calendar_metting_enddate = Integer.parseInt(scalendar_metting_enddate.trim());

                        String calendar_metting_endtime = scalendar_metting_endtime.toString().trim();
                        String calendar_metting_endgmt = scalendar_metting_endgmt;
                        int calendar_metting_endyear = Integer.parseInt(scalendar_metting_endyear.trim());


                        Log.i("readcalendar", "calendar_metting_beginday=" + calendar_metting_endday);

                        Log.i("readcalendar", "calendar_metting_beginmonth =" + calendar_metting_endmonth);

                        Log.i("readcalendar", "calendar_metting_begindate =" + calendar_metting_enddate);

                        Log.i("readcalendar", "calendar_metting_begintime=" + calendar_metting_endtime);

                        Log.i("readcalendar", "calendar_metting_begingmt =" + calendar_metting_endgmt);

                        Log.i("readcalendar", "calendar_metting_beginyear =" + calendar_metting_endyear);

                        /* the calendar control metting-end events Respose  sub-string (starts....ends) */

                        Log.i("readcalendar", "only date begin of events=" + begin.getDate());
                        Log.i("readcalendar", "only begin time of events=" + begin.getHours() + ":" + begin.getMinutes() + ":" + begin.getSeconds());


                        Log.i("readcalendar", "only date begin of events=" + end.getDate());
                        Log.i("readcalendar", "only begin time of events=" + end.getHours() + ":" + end.getMinutes() + ":" + end.getSeconds());

                        beg_date = begin.getDate();
                        mbeg_date = begin.getDate() + "/" + calendar_metting_beginmonth + "/" + calendar_metting_beginyear;
                        beg_time = begin.getHours();

                        System.out.println("the vaule of mbeg_date=" + mbeg_date.toString().trim());
                        end_date = end.getDate();
                        end_time = end.getHours();


//                        CallHandlerUI.metting_begin_date.add(beg_date.toString());
//                        CallHandlerUI.metting_begin_mdate.add(mbeg_date.toString());
//
//                        CallHandlerUI.metting_begin_mtime.add(calendar_metting_begintime.toString());
//
//                        CallHandlerUI.metting_end_date.add(end_date.toString());
//                        CallHandlerUI.metting_end_time.add(end_time.toString());
//                        CallHandlerUI.metting_end_mtime.add(calendar_metting_endtime.toString());

                    }
                    while (eventCursor.moveToNext());
                }
            }
            break;
        }
        return eventList;
    }

    public static String getSelection() {
        Calendar startTime = Calendar.getInstance();

        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.DATE, 30);

        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis()
                + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis()
        + " ) AND ( deleted != 1 ) AND " + CalendarContract.Events.OWNER_ACCOUNT + " = 'kuba.wu1910@gmail.com')";

        return selection;
    }
}