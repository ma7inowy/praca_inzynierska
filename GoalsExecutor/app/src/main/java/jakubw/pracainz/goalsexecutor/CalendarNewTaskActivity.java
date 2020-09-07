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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;

public class CalendarNewTaskActivity extends AppCompatActivity {

    EditText addTitleEvent, addDateEvent, addDescriptionEvent;
    Button addCalendarNewTaskBtn, setDateBtn, setTimeBtn;
    DatabaseReference reference;
    Integer number;
    Integer yearEvent, monthEvent, dayEvent, hourEvent, minuteEvent;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_new_task);

        addTitleEvent = findViewById(R.id.addTitleEvent);
        addDateEvent = findViewById(R.id.addDateEvent);
        addDescriptionEvent = findViewById(R.id.addDescriptionEvent);
        addCalendarNewTaskBtn = findViewById(R.id.addCalendarNewTaskBtn);
        setDateBtn = findViewById(R.id.setDateBtn);
        setTimeBtn = findViewById(R.id.setTimeBtn);
        number = new Random().nextInt();
        calendar = Calendar.getInstance();
        //default data
        yearEvent = calendar.get(Calendar.YEAR);
        monthEvent = calendar.get(Calendar.MONTH);
        dayEvent = calendar.get(Calendar.DATE);
        hourEvent = 0;
        minuteEvent = 0;
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        createNotificationChanel();

        // do tworzenia zadania z BoxActivity
        Intent intent = getIntent();
        if (intent.hasExtra("title")) {
            addTitleEvent.setText(intent.getStringExtra("title"));
        }

        addCalendarNewTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Calendar").child(signInAccount.getId()).child("Does" + number);
                HashMap map = new HashMap();

                map.put("title", addTitleEvent.getText().toString());
                map.put("year", yearEvent);
                map.put("month", monthEvent);
                map.put("day", dayEvent);
                map.put("hour", hourEvent);
                map.put("minute", minuteEvent);
                map.put("description", addDescriptionEvent.getText().toString());
                map.put("id", number.toString());
                reference.updateChildren(map);
                makeNotification();
                Toast.makeText(CalendarNewTaskActivity.this, "done!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButton();
            }
        });

        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTimeButton();
            }
        });

    }

    private void handleTimeButton() {
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = DateFormat.is24HourFormat(this);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hourEvent = hourOfDay;
                minuteEvent = minute;
                setTimeBtn.setText(getDataCharSequenceForTime());
            }
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();
    }

    private void handleDateButton() {
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                yearEvent = year;
                monthEvent = month;
                dayEvent = dayOfMonth;
                setDateBtn.setText(getDataCharSequenceForDate());
            }
        }, YEAR, MONTH, DATE);
        datePickerDialog.show();
    }

    public ArrayList<Integer> modifyDateFromStringToDate(String date) {
        String[] dateTable;
        dateTable = date.split("/");
        ArrayList dateListInt = new ArrayList<Integer>();
        dateListInt.add(Integer.parseInt(dateTable[0]));
        dateListInt.add(Integer.parseInt(dateTable[1]));
        dateListInt.add(Integer.parseInt(dateTable[2]));
        dateListInt.add(Integer.parseInt(dateTable[3]));
        return dateListInt;
        //h,d,m,y
    }

    public CharSequence getDataCharSequenceForTime(){
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR, hourEvent);
        calendar1.set(Calendar.MINUTE, minuteEvent);
        CharSequence dataCharSequenceForTime = DateFormat.format("HH:mm", calendar1);

        return dataCharSequenceForTime;
    }

    public CharSequence getDataCharSequenceForDate(){
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, yearEvent);
        calendar1.set(Calendar.MONTH, monthEvent);
        calendar1.set(Calendar.DATE, dayEvent);
        CharSequence dataCharSequenceForDate = DateFormat.format("dd MMM yyyy", calendar1);

        return dataCharSequenceForDate;
    }

    // dla api >= 26
    private void createNotificationChanel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "GoalsExecutorChannel";
            String description = "Channel for GoalsExecutor";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyGoalsExecutor",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void makeNotification() {
        Intent intent = new Intent(CalendarNewTaskActivity.this,ReminderBroadcast.class);
        intent.putExtra("desc", addTitleEvent.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(CalendarNewTaskActivity.this,0,intent,0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long currentTime = System.currentTimeMillis();
        Calendar calendar = new GregorianCalendar(yearEvent, monthEvent, dayEvent, hourEvent, minuteEvent);
        long alarmTime = calendar.getTimeInMillis();
        long tenSec = 1000 *10;
        Log.e("timealarm", "Alarm " + alarmTime);
        Log.e("timealarm", "current " + currentTime);
        long diff = alarmTime-currentTime;
        Log.e("timealarm", "difference " + diff);
        alarmManager.set(AlarmManager.RTC_WAKEUP,alarmTime,pendingIntent);
    }
}
