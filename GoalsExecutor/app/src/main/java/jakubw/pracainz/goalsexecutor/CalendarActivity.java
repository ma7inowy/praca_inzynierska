package jakubw.pracainz.goalsexecutor;

//import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;

public class CalendarActivity extends Fragment implements CalendarAdapter.OnNoteListener {

    Button btnsortEvents;
    DatabaseReference reference;
    RecyclerView calendarEvents;
    ArrayList<CalendarEvent> eventList;
    GoogleSignInAccount signInAccount;
    CalendarAdapter calendarAdapter;
    Integer number;
    FloatingActionButton addNewCalendarEventFloatingBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_calendar, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        btnsortEvents = getView().findViewById(R.id.btnsortEvents);
        calendarEvents = getView().findViewById(R.id.calendarEvents);
        calendarEvents.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventList = new ArrayList<>();
        number = new Random().nextInt();
        addNewCalendarEventFloatingBtn = getView().findViewById(R.id.addNewCalendarEventFloatingBtn);


        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

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
//                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();

            }
        });

        addNewCalendarEventFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalendarNewTaskActivity.class);
                startActivity(intent);
            }
        });

        btnsortEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEventsFromPhoneCalendars();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(calendarEvents);
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_calendar);
//
//        btnsortEvents = findViewById(R.id.btnsortEvents);
//        btnAddNewEvent = findViewById(R.id.btnAddNewEvent);
//        calendarEvents = findViewById(R.id.calendarEvents);
//        calendarEvents.setLayoutManager(new LinearLayoutManager(this));
//        eventList = new ArrayList<>();
//        number = new Random().nextInt();
//
//
//        //google signin
//        signInAccount = GoogleSignIn.getLastSignedInAccount(this);
//
//        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Calendar").child(signInAccount.getId().toString());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                eventList.clear();
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    CalendarEvent p = dataSnapshot1.getValue(CalendarEvent.class);
//                    eventList.add(p);
//                }
//                setAdapter(eventList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        btnAddNewEvent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(CalendarActivity.this, CalendarNewTaskActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        btnsortEvents.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handleEventsFromPhoneCalendars();
//            }
//        });
//    }

    private void handleEventsFromPhoneCalendars() {
        ArrayList<CalendarEvent> events = ReadCalendar.readCalendar(getActivity());
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
//            Toast.makeText(getContext(), "Events loaded!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else
            Toast.makeText(getContext(), "No events to load!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNoteClick(int position) {
        final CalendarEvent event = eventList.get(position);
        Intent intent = new Intent(getActivity(), EditCalendarEventActivity.class);
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
        calendar1.set(Calendar.DATE, event.getDay() - 1); //bo data od 0 dzien
        calendar1.set(Calendar.HOUR, event.getHour());
        calendar1.set(Calendar.MINUTE, event.getMinute());
        CharSequence dataCharSequenceForDate = DateFormat.format("dd MMM yyyy", calendar1);
        CharSequence dataCharSequenceForTime = DateFormat.format("HH:mm", calendar1);

        intent.putExtra("dateEvent", dataCharSequenceForDate);
        intent.putExtra("timeEvent",dataCharSequenceForTime);
        startActivity(intent);
//        Toast.makeText(getContext(), "id" + event.getId(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), "dzien" + event.getDay(), Toast.LENGTH_SHORT).show();
    }

    public void setAdapter(ArrayList<CalendarEvent> list) {
        calendarAdapter = new CalendarAdapter(getActivity(), list, this);
        calendarEvents.setAdapter(calendarAdapter); // wypelni wszystkie pola ViewHolderami
        calendarAdapter.notifyDataSetChanged();
    }

    // swipe left to delete task
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int postion = viewHolder.getAdapterPosition();

            switch (direction) {
                case LEFT: // zrobic cos ze w momencie usuniecia ewentu usuwa sie alarm!
                    final CalendarEvent deletedTask = eventList.get(postion);

                    String id = deletedTask.getId();
                    reference.child("Does" + id).removeValue(); // usuwa z bazy
                    eventList.remove(postion);
                    setAdapter(eventList);
                    Snackbar.make(calendarEvents, "Event " + deletedTask.getTitle() + " deleted!", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eventList.add(postion, deletedTask);
                            setAdapter(eventList);
                            HashMap map = new HashMap();
                            map.put("title", deletedTask.getTitle());
                            map.put("year", deletedTask.getYear());
                            map.put("month", deletedTask.getMonth());
                            map.put("day", deletedTask.getDay());
                            map.put("hour", deletedTask.getHour());
                            map.put("minute", deletedTask.getMinute());
                            map.put("description", deletedTask.getDescription());
                            map.put("id", deletedTask.getId());
                            reference.child("Does" + deletedTask.getId()).updateChildren(map);
                        }
                    }).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorDeleteTask))
//                    .addBackgroundColor(ContextCompat.getColor(KontenerActivity.this, R.color.colorPrimary))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}
