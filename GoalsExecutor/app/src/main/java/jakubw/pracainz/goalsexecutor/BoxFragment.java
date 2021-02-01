package jakubw.pracainz.goalsexecutor;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import java.util.HashMap;
import java.util.Random;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import jakubw.pracainz.goalsexecutor.Model.BoxTask;

import static android.app.Activity.RESULT_OK;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;

public class BoxFragment extends Fragment implements BoxTaskAdapter.OnItemListener {

    FloatingActionButton addNewBoxTaskBtn;
    RecyclerView recyclerBoxTask;
    DatabaseReference reference;
    DatabaseReference referenceTasks; // polaczneie z wezlami bazy aby dodac np projekt, group, someday (zeby nie mieszac z reference)
    ArrayList<BoxTask> boxTaskList;
    GoogleSignInAccount signInAccount;
    BoxTaskAdapter boxTaskAdapter;
    Integer idNumber;
    AlertDialog chooseActivityDialog;
    BoxTask boxTask; // tylko zeby miec globalnie id konkretnego wcisnietego zadania zeby mozna bylo zrobic onActivityResult
    public static final int NEXT_ACTION_REQUEST = 11; //kody do otrzymania danych o dodaniu taskbox w jakies miejsce np do nextaction albo do calendar (o dostaniu sie do odpowiedniego activity)
    public static final int CALENDAR_REQUEST = 12; //
    public static final int GROUP_REQUEST = 13; //

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_box, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        addNewBoxTaskBtn = getView().findViewById(R.id.addNewBoxTaskFloatingBtn);
        recyclerBoxTask = getView().findViewById(R.id.myBoxTasks);
        boxTaskList = new ArrayList<>();
        idNumber = new Random().nextInt();
        recyclerBoxTask.setLayoutManager(new LinearLayoutManager(getActivity()));
        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Box").child(signInAccount.getId().toString());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boxTaskList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    BoxTask p = dataSnapshot1.getValue(BoxTask.class);
                    boxTaskList.add(p);
                    Log.e("Box", p.getTitle());
                }
                setAdapter(boxTaskList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//               Toast.makeText(getContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        addNewBoxTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToAddNewBoxTask();
                Toast.makeText(getContext(), "New box task", Toast.LENGTH_SHORT).show();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerBoxTask);

    }

    public void setAdapter(ArrayList<BoxTask> boxTasksList) {
        boxTaskAdapter = new BoxTaskAdapter(getActivity(), boxTasksList, this);
        recyclerBoxTask.setAdapter(boxTaskAdapter); // wypelni wszystkie pola ViewHolderami
        boxTaskAdapter.notifyDataSetChanged();
    }

    private void openDialogToAddNewBoxTask() {
        NewBoxTaskDialog newBoxTaskDialog = new NewBoxTaskDialog();
        newBoxTaskDialog.show(getFragmentManager(), "Example");
    }

    @Override
    public void onItemClick(final int position) {
        boxTask = boxTaskList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final CharSequence[] activityList = {"NextAction", "Calendar", "Someday", "Group", "Project"};
        builder.setTitle("What's the type of task?").setItems(activityList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (activityList[which].toString().equals("NextAction")) {
                    Intent intent = new Intent(getActivity(), NewNextActionActivity.class);
                    intent.putExtra("title", boxTask.getTitle());
                    startActivityForResult(intent, BoxFragment.NEXT_ACTION_REQUEST);
                }
                if (activityList[which].toString().equals("Calendar")) {
                    Intent intent = new Intent(getActivity(), NewCalendarEventActivity.class);
                    intent.putExtra("title", boxTask.getTitle());
                    startActivityForResult(intent, BoxFragment.CALENDAR_REQUEST);
                }
                if (activityList[which].toString().equals("Someday")) {
                    Toast.makeText(getActivity(), "Someday", Toast.LENGTH_SHORT).show();
                }
                if (activityList[which].toString().equals("Group")) {
                    Intent intent = new Intent(getActivity(), NewGroupTaskActivity.class);
                    intent.putExtra("title", boxTask.getTitle());
                    startActivityForResult(intent, BoxFragment.GROUP_REQUEST);
                    Toast.makeText(getActivity(), "Group", Toast.LENGTH_SHORT).show();
                }
                if (activityList[which].toString().equals("Project")) {
                    referenceTasks = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Projects").child(signInAccount.getId().toString()).child("Project" + idNumber);
                    HashMap map = new HashMap();
                    map.put("title", boxTask.getTitle());
                    map.put("id", idNumber.toString());
                    referenceTasks.updateChildren(map);
                    reference.child("Box" + boxTask.getId()).removeValue();
                    Toast.makeText(getActivity(), "Added " + boxTask.getTitle() + " to Projects!", Toast.LENGTH_SHORT).show();
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
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorDeleteTask))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    // zeby usuwac BoxTask z listy dopiero jesli dane zadanie zostanie utworzone jako np NextAct/Calendar
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

        if (requestCode == GROUP_REQUEST && resultCode == RESULT_OK) {
            if (data.getExtras().containsKey("taskAdded")) {
                if (data.getBooleanExtra("taskAdded", false))
                    reference.child("Box" + boxTask.getId()).removeValue();
            }
        }
    }
}
