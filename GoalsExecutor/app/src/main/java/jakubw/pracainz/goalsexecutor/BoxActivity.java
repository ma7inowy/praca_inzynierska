package jakubw.pracainz.goalsexecutor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class BoxActivity extends AppCompatActivity implements BoxTasksAdapter.OnNoteListener {

    FloatingActionButton addNewBoxTaskFloatingBtn;
    RecyclerView myBoxTasks;
    DatabaseReference reference;
    ArrayList<BoxTask> boxTasksList;
    GoogleSignInAccount signInAccount;
    BoxTasksAdapter boxTasksAdapter;
    Integer number;
    AlertDialog newActivityDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);

        addNewBoxTaskFloatingBtn = findViewById(R.id.addNewBoxTaskFloatingBtn);
        myBoxTasks = findViewById(R.id.myBoxTasks);
        boxTasksList = new ArrayList<>();
        number = new Random().nextInt();
        myBoxTasks.setLayoutManager(new LinearLayoutManager(this));
        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Box").child(signInAccount.getId().toString());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boxTasksList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    BoxTask p = dataSnapshot1.getValue(BoxTask.class);
                    boxTasksList.add(p);
                    Log.e("box", p.getTitle());
                }
                setAdapter(boxTasksList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();

            }
        });

        addNewBoxTaskFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToAddNewBoxTask();
                Toast.makeText(BoxActivity.this, "New box task", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setAdapter(ArrayList<BoxTask> boxTasksList) {
        boxTasksAdapter = new BoxTasksAdapter(BoxActivity.this, boxTasksList, this);
        myBoxTasks.setAdapter(boxTasksAdapter); // wypelni wszystkie pola ViewHolderami
        boxTasksAdapter.notifyDataSetChanged();
    }

    private void openDialogToAddNewBoxTask() {
        NewBoxTaskDialog newBoxTaskDialog = new NewBoxTaskDialog();
        newBoxTaskDialog.show(getSupportFragmentManager(), "Example");
    }

    @Override
    public void onNoteClick(final int position) {
        final BoxTask boxTask = boxTasksList.get(position);
//        Toast.makeText(this, "id?" + boxTask.getId(), Toast.LENGTH_SHORT).show();

        // otworz okienko aby wybrać do jakiej aktywności przekierować
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final CharSequence[] activityList = {"NextAction", "Calendar", "Someday"};
        builder.setTitle("Co to za zadanie?").setItems(activityList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(BoxActivity.this, activityList[which].toString(), Toast.LENGTH_SHORT).show();
                if (activityList[which].toString().equals("NextAction")) {
                    Intent intent = new Intent(BoxActivity.this, NewTaskActivity.class);
                    intent.putExtra("title", boxTask.getTitle());
                    Toast.makeText(BoxActivity.this, "na", Toast.LENGTH_SHORT).show();
                    startActivity(intent);

                    reference.child("Box" + boxTask.getId()).removeValue(); // czy na pewno tak robic
                }

                if (activityList[which].toString().equals("Calendar")) {
                    Intent intent = new Intent(BoxActivity.this, CalendarNewTaskActivity.class);
                    intent.putExtra("title", boxTask.getTitle());
//                    Toast.makeText(BoxActivity.this, "ca", Toast.LENGTH_SHORT).show();
                    startActivity(intent);

                    reference.child("Box" + boxTask.getId()).removeValue(); // czy na pewno tak robic
                }

                if (activityList[which].toString().equals("Someday")) {
//                    Intent intent = new Intent(BoxActivity.this, CalendarNewTaskActivity.class);
//                    intent.putExtra("title", boxTask.getTitle());
//                    startActivity(intent);
//                    reference.child("Box" + boxTask.getId()).removeValue(); // czy na pewno tak robic
                    Toast.makeText(BoxActivity.this, "Someday", Toast.LENGTH_SHORT).show();


                }

            }
        });
        newActivityDialog = builder.create();
        newActivityDialog.show();
    }
}
