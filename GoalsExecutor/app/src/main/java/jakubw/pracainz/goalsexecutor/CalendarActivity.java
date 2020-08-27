package jakubw.pracainz.goalsexecutor;

//import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnNoteListener {

    Button btnsortEvents;
    Button btnAddNewEvent;
    DatabaseReference reference;
    RecyclerView calendarEvents;
    ArrayList<CalendarEvent> eventList;
    GoogleSignInAccount signInAccount;
    CalendarAdapter calendarAdapter;
    Integer number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        btnsortEvents = findViewById(R.id.btnsortEvents);
        btnAddNewEvent = findViewById(R.id.btnAddNewEvent);
        calendarEvents = findViewById(R.id.calendarEvents);
        calendarEvents.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        number = new Random().nextInt();


        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Calendar").child(signInAccount.getId().toString());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    CalendarEvent p = dataSnapshot1.getValue(CalendarEvent.class);
                    eventList.add(p);
                }
                setAdapter(eventList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();

            }
        });

        btnAddNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, CalendarNewTaskActivity.class);
                startActivity(intent);
            }
        });

        btnsortEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEventsFromPhoneCalendars();
            }
        });
    }

    private void handleEventsFromPhoneCalendars() {
        ArrayList<CalendarEvent> events = ReadCalendar.readCalendar(CalendarActivity.this);
        if (!events.isEmpty()) {
            for (CalendarEvent event : events) {
                reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Calendar").child(signInAccount.getId()).child("Does" + event.getId());
                HashMap map = new HashMap();
                map.put("title", event.getTitle());
                map.put("year", event.getYear());
                map.put("month", event.getMonth());
                map.put("day", event.getDay());
                map.put("hour", event.getHour());
                map.put("minute", 0);
                map.put("description", "desc");
                map.put("id", event.getId());
                reference.updateChildren(map);
            }
            Toast.makeText(CalendarActivity.this, "Events loaded!", Toast.LENGTH_SHORT).show();
            finish();
        } else
            Toast.makeText(CalendarActivity.this, "No events to load!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNoteClick(int position) {
        final CalendarEvent event = eventList.get(position);
        Intent intent = new Intent(this, EditCalendarEventActivity.class);
        intent.putExtra("title", event.getTitle());
        intent.putExtra("yearEvent", event.getYear());
        intent.putExtra("monthEvent", event.getMonth());
        intent.putExtra("dayEvent", event.getDay());
        intent.putExtra("hourEvent", event.getHour());
        intent.putExtra("minuteEvent", event.getMinute());
        intent.putExtra("description", event.getDescription());
        intent.putExtra("id", event.getId());
        // jeszcze cos zeby wysylac date na button
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, event.getYear());
        calendar1.set(Calendar.MONTH, event.getMonth());
        calendar1.set(Calendar.DATE, event.getDay());
        calendar1.set(Calendar.HOUR, event.getHour());
        calendar1.set(Calendar.MINUTE, event.getMinute());
        CharSequence dataCharSequenceForDate = DateFormat.format("dd MMM yyyy", calendar1);
        CharSequence dataCharSequenceForTime = DateFormat.format("HH:mm", calendar1);

        intent.putExtra("dateEvent", dataCharSequenceForDate);
        intent.putExtra("timeEvent",dataCharSequenceForTime);
        startActivity(intent);
        Toast.makeText(this, "id" + event.getId(), Toast.LENGTH_SHORT).show();
    }

    public void setAdapter(ArrayList<CalendarEvent> list) {
        calendarAdapter = new CalendarAdapter(CalendarActivity.this, list, this);
        calendarEvents.setAdapter(calendarAdapter); // wypelni wszystkie pola ViewHolderami
        calendarAdapter.notifyDataSetChanged();
    }
}
