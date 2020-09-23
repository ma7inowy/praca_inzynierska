package jakubw.pracainz.goalsexecutor;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import java.util.HashMap;
import java.util.Random;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;

public class BoxActivity extends AppCompatActivity implements BoxTaskAdapter.OnItemListener {

    FloatingActionButton addNewBoxTaskBtn;
    RecyclerView recyclerBoxTask;
    DatabaseReference reference;
    ArrayList<BoxTask> boxTaskList;
    GoogleSignInAccount signInAccount;
    BoxTaskAdapter boxTaskAdapter;
    Integer idNumber;
    AlertDialog chooseActivityDialog;
    BoxTask boxTask; // tylko zeby miec globalnie id konkretnego wcisnietego zadania zeby mozna bylo zrobic onActivityResult
    public static final int NEXT_ACTION_REQUEST = 11; //kody do otrzymania danych o dodaniu taskbox w jakies miejsce np do nextaction albo do calendar (o dostaniu sie do odpowiedniego activity)
    public static final int CALENDAR_REQUEST = 12; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);

        addNewBoxTaskBtn = findViewById(R.id.addNewBoxTaskFloatingBtn);
        recyclerBoxTask = findViewById(R.id.myBoxTasks);
        boxTaskList = new ArrayList<>();
        idNumber = new Random().nextInt();
        recyclerBoxTask.setLayoutManager(new LinearLayoutManager(this));
        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Box").child(signInAccount.getId().toString());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boxTaskList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    BoxTask p = dataSnapshot1.getValue(BoxTask.class);
                    boxTaskList.add(p);
                    Log.e("box", p.getTitle());
                }
                setAdapter(boxTaskList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        addNewBoxTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToAddNewBoxTask();
                Toast.makeText(BoxActivity.this, "New box task", Toast.LENGTH_SHORT).show();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerBoxTask);
    }

    public void setAdapter(ArrayList<BoxTask> boxTasksList) {
        boxTaskAdapter = new BoxTaskAdapter(BoxActivity.this, boxTasksList, this);
        recyclerBoxTask.setAdapter(boxTaskAdapter); // wypelni wszystkie pola ViewHolderami
        boxTaskAdapter.notifyDataSetChanged();
    }

    private void openDialogToAddNewBoxTask() {
        NewBoxTaskDialog newBoxTaskDialog = new NewBoxTaskDialog();
        newBoxTaskDialog.show(getSupportFragmentManager(), "Example");
    }

    @Override
    public void onItemClick(final int position) {
        boxTask = boxTaskList.get(position);
        // otworz okienko aby wybrać do jakiej aktywności przekierować
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final CharSequence[] activityList = {"NextAction", "Calendar", "Someday"};
        builder.setTitle("Co to za zadanie?").setItems(activityList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (activityList[which].toString().equals("NextAction")) {
                    Intent intent = new Intent(BoxActivity.this, NewNextActionActivity.class);
                    intent.putExtra("title", boxTask.getTitle());
                    startActivityForResult(intent, BoxActivity.NEXT_ACTION_REQUEST);
                }
                if (activityList[which].toString().equals("Calendar")) {
                    Intent intent = new Intent(BoxActivity.this, NewCalendarEventActivity.class);
                    intent.putExtra("title", boxTask.getTitle());
                    startActivityForResult(intent, BoxActivity.CALENDAR_REQUEST);
                }
                if (activityList[which].toString().equals("Someday")) {
//                    Intent intent = new Intent(BoxActivity.this, NewCalendarEventActivity.class);
//                    intent.putExtra("title", boxTask.getTitle());
//                    startActivity(intent);
//                    reference.child("Box" + boxTask.getId()).removeValue(); // czy na pewno tak robic
                    Toast.makeText(BoxActivity.this, "Someday", Toast.LENGTH_SHORT).show();
                }
            }
        });
        chooseActivityDialog = builder.create();
        chooseActivityDialog.show();
    }

    // swipe left to delete task
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case LEFT:
                    final BoxTask deletedTask = boxTaskList.get(position);

                    String id = deletedTask.getId();
                    reference.child("Box" + id).removeValue(); // usuwa z bazy
                    boxTaskList.remove(position);
                    setAdapter(boxTaskList);
                    Snackbar.make(recyclerBoxTask, "Task " + deletedTask.getTitle() + " deleted!", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boxTaskList.add(position, deletedTask);
                            setAdapter(boxTaskList);
                            HashMap map = new HashMap();
                            map.put("title", deletedTask.getTitle());
                            map.put("id", deletedTask.getId());
                            reference.child("Box" + deletedTask.getId()).updateChildren(map);
                        }
                    }).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(BoxActivity.this, R.color.colorDeleteTask))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    // zeby usuwac BoxTask z listy dopiero jesli dane zadanie zostanie utworzone jako np NextAct/Calendar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEXT_ACTION_REQUEST && resultCode == RESULT_OK) {
            if (data.getExtras().containsKey("taskAdded")) {
                if (data.getBooleanExtra("taskAdded", false))
                    reference.child("Box" + boxTask.getId()).removeValue();
            }
        }
        if (requestCode == CALENDAR_REQUEST && resultCode == RESULT_OK) {
            if (data.getExtras().containsKey("taskAdded")) {
                if (data.getBooleanExtra("taskAdded", false))
                    reference.child("Box" + boxTask.getId()).removeValue();
            }
        }
    }
}
