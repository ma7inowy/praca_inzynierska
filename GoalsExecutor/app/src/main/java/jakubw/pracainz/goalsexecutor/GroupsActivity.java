package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

public class GroupsActivity extends AppCompatActivity implements GroupTasksAdapter.OnItemListener {

    RecyclerView recyclerGroups;
    FloatingActionButton addGroupTaskBtn;

    DatabaseReference reference;
    DatabaseReference reference2;
    ArrayList<GroupTask> groupTaskList;
    ArrayList<String> userGroupTaskIdList;
    GoogleSignInAccount signInAccount;
    GroupTasksAdapter groupTasksAdapter;
    Integer idNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        recyclerGroups = findViewById(R.id.recyclerGroups);
        addGroupTaskBtn = findViewById(R.id.addGroupTaskBtn);
        groupTaskList = new ArrayList<>();
        userGroupTaskIdList = new ArrayList<>();
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        idNumber = new Random().nextInt();
        recyclerGroups.setLayoutManager(new LinearLayoutManager(this));

        // jesli zawiera id uzytkownika to wczytaj
        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("GroupTasks");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupTaskList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    GroupTask p = dataSnapshot1.getValue(GroupTask.class);
//                    if(p!=null)
//                    if(p.getId().equals(user.getGroupId))
                    groupTaskList.add(p);
//                    Log.e("groupTask", p.getTitle());
                }
                reference2 = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Users").child(signInAccount.getId().toString()).child("GroupTasksId");
                reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    ArrayList<GroupTask> userGroupTaskList = new ArrayList<>(); //zadania grupowe konkretnego uzytkownika

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userGroupTaskIdList.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            String p = dataSnapshot1.getValue(String.class);
                            userGroupTaskIdList.add(p);
                        }

                        if (!getOnlyUserGroupTasks(userGroupTaskIdList, groupTaskList).isEmpty()) {
                            userGroupTaskList = getOnlyUserGroupTasks(userGroupTaskIdList, groupTaskList);
                            setAdapter(userGroupTaskList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();

            }
        });

        addGroupTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupsActivity.this, NewGroupTaskActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setAdapter(ArrayList<GroupTask> userGroupTaskList) {
        groupTasksAdapter = new GroupTasksAdapter(GroupsActivity.this, userGroupTaskList, this);
        recyclerGroups.setAdapter(groupTasksAdapter);
        groupTasksAdapter.notifyDataSetChanged();
    }

    private ArrayList<GroupTask> getOnlyUserGroupTasks(ArrayList<String> userGroupTaskIdList, ArrayList<GroupTask> groupTaskList) {
        ArrayList<GroupTask> list = new ArrayList<>();
        for (String id : userGroupTaskIdList) {
            for (GroupTask groupTask : groupTaskList) {
                if (groupTask.getId().equals(id)) {
                    list.add(groupTask);
                }
            }
        }
        return list;
    }

    @Override
    public void onItemClick(int position) {
        final GroupTask groupTask;
        groupTask = groupTaskList.get(position);

        Intent intent = new Intent(GroupsActivity.this, EditNextActionActivity.class);
        intent.putExtra("title", groupTask.getTitle());
        intent.putExtra("description", groupTask.getDescription());
        intent.putExtra("estimatedTime", groupTask.getEstimatedTime());
        intent.putExtra("id", groupTask.getId());
        intent.putExtra("priority", groupTask.getPriority());
        startActivity(intent);
        Toast.makeText(GroupsActivity.this, "id" + groupTask.getId(), Toast.LENGTH_SHORT).show();
    }
}
