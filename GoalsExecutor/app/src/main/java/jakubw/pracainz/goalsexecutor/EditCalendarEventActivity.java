package jakubw.pracainz.goalsexecutor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
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

import java.util.Calendar;
import java.util.HashMap;

public class EditCalendarEventActivity extends AppCompatActivity {

    EditText editTitleEvent, editDescriptionEvent;
    Button editDateBtn, editTimeBtn, editCalendarTaskBtn;
    DatabaseReference reference;
    GoogleSignInAccount signInAccount;
    String id;
    Integer yearEvent, monthEvent, dayEvent, hourEvent, minuteEvent;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_calendar_event);

        editTitleEvent = findViewById(R.id.editTitleEvent);
        editDescriptionEvent = findViewById(R.id.editDescriptionEvent);
        editDateBtn = findViewById(R.id.editDateBtn);
        editTimeBtn = findViewById(R.id.editTimeBtn);
        editCalendarTaskBtn = findViewById(R.id.editCalendarTaskBtn);
        calendar = Calendar.getInstance();
        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);


        //set time and data
        Intent intent = getIntent();
        editTitleEvent.setText(intent.getStringExtra("title"));
        editDescriptionEvent.setText(intent.getStringExtra("description"));
        editDateBtn.setText(intent.getCharSequenceExtra("dateEvent"));
        editTimeBtn.setText(intent.getCharSequenceExtra("timeEvent"));
        yearEvent = intent.getIntExtra("yearEvent",1970);
        monthEvent = intent.getIntExtra("monthEvent",1);
        dayEvent = intent.getIntExtra("dayEvent",1);
        hourEvent = intent.getIntExtra("hourEvent",0);
        minuteEvent = intent.getIntExtra("minuteEvent",0);

        id = intent.getStringExtra("id");

        editDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButton();
            }
        });

        editTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTimeButton();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Calendar").child(signInAccount.getId().toString()).child("Does" + id);
        editCalendarTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap map = new HashMap();
                map.put("title", editTitleEvent.getText().toString());
                map.put("year", yearEvent);
                map.put("month", monthEvent);
                map.put("day", dayEvent);
                map.put("hour", hourEvent);
                map.put("minute", minuteEvent);
                map.put("description", editDescriptionEvent.getText().toString());
                reference.updateChildren(map);
//                Toast.makeText(EditDoesActivity.this, editTitle.getText().toString() + " " + editDescription.getText().toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void handleTimeButton() {
        int HOUR = hourEvent;
        int MINUTE = minuteEvent;
//        boolean is24HourFormat = DateFormat.is24HourFormat(this);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hourEvent = hourOfDay;
                minuteEvent = minute;
                editTimeBtn.setText(getDataCharSequenceForTime());
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
                editDateBtn.setText(getDataCharSequenceForDate());
            }
        }, YEAR, MONTH, DATE);
        datePickerDialog.show();
    }

    public CharSequence getDataCharSequenceForDate(){
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, yearEvent);
        calendar1.set(Calendar.MONTH, monthEvent);
        calendar1.set(Calendar.DATE, dayEvent);
        CharSequence dataCharSequenceForDate = DateFormat.format("dd MMM yyyy", calendar1);

        return dataCharSequenceForDate;
    }

    public CharSequence getDataCharSequenceForTime(){
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR, hourEvent);
        calendar1.set(Calendar.MINUTE, minuteEvent);
        CharSequence dataCharSequenceForTime = DateFormat.format("HH:mm", calendar1);

        return dataCharSequenceForTime;
    }
}
