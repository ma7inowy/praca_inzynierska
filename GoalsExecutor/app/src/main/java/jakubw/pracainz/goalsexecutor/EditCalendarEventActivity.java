package jakubw.pracainz.goalsexecutor;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class EditCalendarEventActivity extends AppCompatActivity {

    EditText editEventTitle, editEventDescription;
    Button editEventDateBtn, editEventTimeBtn, editEventBtn;
    DatabaseReference reference;
    GoogleSignInAccount signInAccount;
    String id;
    Integer yearEvent, monthEvent, dayEvent, hourEvent, minuteEvent;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_calendar_event);

        editEventTitle = findViewById(R.id.editEventTitle);
        editEventDescription = findViewById(R.id.editEventDescription);
        editEventDateBtn = findViewById(R.id.editEventDateBtn);
        editEventTimeBtn = findViewById(R.id.editEventTimeBtn);
        editEventBtn = findViewById(R.id.editEventBtn);
        calendar = Calendar.getInstance();
        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);


        //set time and data
        Intent intent = getIntent();
        editEventTitle.setText(intent.getStringExtra("title"));
        editEventDescription.setText(intent.getStringExtra("description"));
        editEventDateBtn.setText(intent.getCharSequenceExtra("dateEvent"));
        editEventTimeBtn.setText(intent.getCharSequenceExtra("timeEvent"));
        yearEvent = intent.getIntExtra("yearEvent", 1970);
        monthEvent = intent.getIntExtra("monthEvent", 1);
        dayEvent = intent.getIntExtra("dayEvent", 1);
        hourEvent = intent.getIntExtra("hourEvent", 0);
        minuteEvent = intent.getIntExtra("minuteEvent", 0);
        createNotificationChanel();
        id = intent.getStringExtra("id");

        editEventDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButton();
            }
        });
        editEventTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTimeButton();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Calendar").child(signInAccount.getId().toString()).child("Does" + id);
        editEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap map = new HashMap();
                map.put("title", editEventTitle.getText().toString());
                map.put("year", yearEvent);
                map.put("month", monthEvent);
                map.put("day", dayEvent);
                map.put("hour", hourEvent);
                map.put("minute", minuteEvent);
                map.put("description", editEventDescription.getText().toString());
                reference.updateChildren(map);
                editNotification();
                finish();
            }
        });
    }

    private void handleTimeButton() {
        int HOUR = hourEvent;
        int MINUTE = minuteEvent;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hourEvent = hourOfDay;
                minuteEvent = minute;
                editEventTimeBtn.setText(getDataCharSequenceForTime());
            }
        }, HOUR, MINUTE, true);

        timePickerDialog.show();
    }

    private void handleDateButton() {
        int YEAR = yearEvent;
        int MONTH = monthEvent;
        int DATE = dayEvent;
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                yearEvent = year;
                monthEvent = month;
                dayEvent = dayOfMonth;
                editEventDateBtn.setText(getDataCharSequenceForDate());
            }
        }, YEAR, MONTH, DATE);
        datePickerDialog.show();
    }

    public CharSequence getDataCharSequenceForDate() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, yearEvent);
        calendar1.set(Calendar.MONTH, monthEvent);
        calendar1.set(Calendar.DATE, dayEvent);
        CharSequence dataCharSequenceForDate = DateFormat.format("dd MMM yyyy", calendar1);

        return dataCharSequenceForDate;
    }

    public CharSequence getDataCharSequenceForTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, hourEvent);
        calendar1.set(Calendar.MINUTE, minuteEvent);
        CharSequence dataCharSequenceForTime = DateFormat.format("HH:mm", calendar1);

        return dataCharSequenceForTime;
    }

    // dla api >= 26
    private void createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "GoalsExecutorChannel";
            String description = "Channel for GoalsExecutor";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyGoalsExecutor", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void editNotification() {
        Intent intent = new Intent(EditCalendarEventActivity.this, ReminderBroadcast.class);
        intent.putExtra("desc", editEventTitle.getText().toString());
        int requestcode = Integer.valueOf(id);
        //https://stackoverflow.com/questions/18649728/android-cannot-pass-intent-extras-though-alarmmanager/28203623 flaga do update
        PendingIntent pendingIntent = PendingIntent.getBroadcast(EditCalendarEventActivity.this, requestcode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long currentTime = System.currentTimeMillis();
        Calendar calendar = new GregorianCalendar(yearEvent, monthEvent, dayEvent, hourEvent, minuteEvent);
        long alarmTime = calendar.getTimeInMillis();
        long tenSec = 1000 * 10;
        Log.e("timealarm", "Alarm " + alarmTime);
        Log.e("timealarm", "current " + currentTime);
        long diff = alarmTime - currentTime;
        Log.e("timealarm", "difference " + diff);

        if (diff > 0)
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }
}
