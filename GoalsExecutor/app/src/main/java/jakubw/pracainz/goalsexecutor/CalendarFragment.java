package jakubw.pracainz.goalsexecutor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {

    Button sortEventsBtn;
    DatabaseReference reference;
    RecyclerView recyclerCalendarEvent;
    ArrayList<CalendarEvent> calendarEventList;
    GoogleSignInAccount signInAccount;
    CalendarAdapter calendarAdapter;
    Integer idNumber;
    FloatingActionButton addNewCalendarEventBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        sortEventsBtn = getView().findViewById(R.id.btnsortEvents);
        recyclerCalendarEvent = getView().findViewById(R.id.recyclerCalendarEvent);
        recyclerCalendarEvent.setLayoutManager(new LinearLayoutManager(getActivity()));
        calendarEventList = new ArrayList<>();
        idNumber = new Random().nextInt();
        addNewCalendarEventBtn = getView().findViewById(R.id.addNewCalendarEventBtn);

        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Calendar").child(signInAccount.getId().toString());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                calendarEventList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    CalendarEvent p = dataSnapshot1.getValue(CalendarEvent.class);
                    calendarEventList.add(p);
                }
                setAdapter(calendarEventList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        addNewCalendarEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewCalendarEventActivity.class);
                startActivity(intent);
            }
        });

        sortEventsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEventsFromPhoneCalendars();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerCalendarEvent);
    }

    private void handleEventsFromPhoneCalendars() {
        ArrayList<CalendarEvent> events = ReadCalendar.readCalendar(getActivity(), signInAccount.getEmail());
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
            getActivity().finish();
        } else
            Toast.makeText(getContext(), "No events to load!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position) {
        final CalendarEvent event = calendarEventList.get(position);
        Intent intent = new Intent(getActivity(), EditCalendarEventActivity.class);
        intent.putExtra("title", event.getTitle());
        intent.putExtra("yearEvent", event.getYear());
        intent.putExtra("monthEvent", event.getMonth());
        intent.putExtra("dayEvent", event.getDay());
        intent.putExtra("hourEvent", event.getHour());
        intent.putExtra("minuteEvent", event.getMinute());
        intent.putExtra("description", event.getDescription());
        intent.putExtra("id", event.getId());

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, event.getYear());
        calendar1.set(Calendar.MONTH, event.getMonth());
        calendar1.set(Calendar.DATE, event.getDay());
        calendar1.set(Calendar.HOUR, event.getHour());
        calendar1.set(Calendar.MINUTE, event.getMinute());
        CharSequence dataCharSequenceForDate = DateFormat.format("dd MMM yyyy", calendar1);
        CharSequence dataCharSequenceForTime = DateFormat.format("HH:mm", calendar1);

        intent.putExtra("dateEvent", dataCharSequenceForDate);
        intent.putExtra("timeEvent", dataCharSequenceForTime);
        startActivity(intent);
    }

    public void setAdapter(ArrayList<CalendarEvent> list) {
        calendarAdapter = new CalendarAdapter(getActivity(), list, this);
        recyclerCalendarEvent.setAdapter(calendarAdapter);
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
                case LEFT:
                    final CalendarEvent deletedTask = calendarEventList.get(postion);

                    String id = deletedTask.getId();
                    reference.child("Does" + id).removeValue();
                    calendarEventList.remove(postion);
                    setAdapter(calendarEventList);
                    deleteAlarm(id);
                    Snackbar.make(recyclerCalendarEvent, "Event " + deletedTask.getTitle() + " deleted!", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            calendarEventList.add(postion, deletedTask);
                            setAdapter(calendarEventList);
//                            HashMap map = new HashMap();
//                            map.put("title", deletedTask.getTitle());
//                            map.put("year", deletedTask.getYear());
//                            map.put("month", deletedTask.getMonth());
//                            map.put("day", deletedTask.getDay());
//                            map.put("hour", deletedTask.getHour());
//                            map.put("minute", deletedTask.getMinute());
//                            map.put("description", deletedTask.getDescription());
//                            map.put("id", deletedTask.getId());
//                            reference.child("Does" + deletedTask.getId()).updateChildren(map);
                            addNewEventToDB(deletedTask);
                            undoDeletingAlarm(deletedTask);
                        }
                    }).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorDeleteTask))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    //moze tez dodawac nowy
    private void undoDeletingAlarm(CalendarEvent calendarEvent) {
        Intent intent = new Intent(getActivity(), ReminderBroadcast.class);
        intent.putExtra("desc", calendarEvent.getTitle());
        int requestcode = Integer.valueOf(calendarEvent.getId());
        //https://stackoverflow.com/questions/18649728/android-cannot-pass-intent-extras-though-alarmmanager/28203623 flaga do update
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), requestcode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = new GregorianCalendar(calendarEvent.getYear(), calendarEvent.getMonth(), calendarEvent.getDay(), calendarEvent.getHour(), calendarEvent.getMinute());
        long alarmTime = calendar.getTimeInMillis();
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }

    private void deleteAlarm(String id) {
        Intent intent = new Intent(getActivity(), ReminderBroadcast.class);
        int requestcode = Integer.valueOf(id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), requestcode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.calendar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.calendar_item_sync) {
            syncAndRefreshDataFromGoogleCalendar();
            Toast.makeText(getActivity(), "Sync", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void syncAndRefreshDataFromGoogleCalendar() {
        ArrayList<CalendarEvent> events = ReadCalendar.readCalendar(getActivity(), signInAccount.getEmail());
        Toast.makeText(getActivity(), String.valueOf(calendarEventList.size()), Toast.LENGTH_SHORT).show();
        if (events.isEmpty()) return;
        for (CalendarEvent event : events) { //ewenty z kalendarza
            int licznik = 0;
            for (CalendarEvent event2 : calendarEventList) { //ewenty w recyclerview
                if (event.toString().equals(event2.toString())) licznik++;
                Log.i("CalendarRefresh", String.valueOf(licznik));
            }
            if (licznik == 0) {
                calendarEventList.add(event);
                addNewEventToDB(event);
                undoDeletingAlarm(event);
            }
        }

        setAdapter(calendarEventList);
    }

    private void addNewEventToDB(CalendarEvent event) {
        HashMap map = new HashMap();
        map.put("title", event.getTitle());
        map.put("year", event.getYear());
        map.put("month", event.getMonth());
        map.put("day", event.getDay());
        map.put("hour", event.getHour());
        map.put("minute", event.getMinute());
        map.put("description", event.getDescription());
        map.put("id", event.getId());
        reference.child("Does" + event.getId()).updateChildren(map);
    }


}
