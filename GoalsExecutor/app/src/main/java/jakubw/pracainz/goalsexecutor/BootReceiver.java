package jakubw.pracainz.goalsexecutor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


import jakubw.pracainz.goalsexecutor.Model.CalendarEvent;

import static android.content.Context.ALARM_SERVICE;

public class BootReceiver extends BroadcastReceiver {

    DatabaseReference reference;
    GoogleSignInAccount signInAccount;
    ArrayList<CalendarEvent> calendarEventList;


    @Override
    public void onReceive(Context context, Intent intent) {
        // setting alarms after reboot


        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            getStoredEvents(context);
        }


    }

    public void getStoredEvents(final Context context) {
        calendarEventList = new ArrayList<CalendarEvent>();
        signInAccount = GoogleSignIn.getLastSignedInAccount(context);
        if (signInAccount != null)
            Log.i("BootReceiverxd", signInAccount.getEmail());
        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Calendar").child(signInAccount.getId().toString());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                calendarEventList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    CalendarEvent p = dataSnapshot1.getValue(CalendarEvent.class);
                    calendarEventList.add(p);
                }
                Log.i("BootReceiverxd", String.valueOf(calendarEventList.size()));
                // ustaw alarmy
                setAfterREBOOTAlarms(calendarEventList, context);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAfterREBOOTAlarms(ArrayList<CalendarEvent> calendarEventList, Context context) {

        for (CalendarEvent event : calendarEventList) {
            Log.i("BootReceiverxd", event.getTitle());

            Intent intent = new Intent(context, EventReminderBroadcast.class);
//            intent.setAction("android.intent.action.BOOT_COMPLETED");
//            intent.setAction("android.intent.action.QUICKBOOT_POWERON");
            intent.putExtra("desc", event.getTitle());
            int requestcode = Integer.valueOf(event.getId());

            //z ta falg update po to zeby updatowac dane z powiadomieniu jak np zmienie tytul zadania
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestcode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            long currentTime = System.currentTimeMillis();
            Calendar calendar = new GregorianCalendar(event.getYear(), event.getMonth(), event.getDay(), event.getHour(), event.getMinute());
            long alarmTime = calendar.getTimeInMillis();
            Log.i("timealarmBoot", "Alarm " + alarmTime);
            Log.i("timealarmBoot", "current " + currentTime);
            long diff = alarmTime - currentTime;
            Log.i("timealarmBoot", "difference " + diff);
            if (diff > 0)
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
    }
}
