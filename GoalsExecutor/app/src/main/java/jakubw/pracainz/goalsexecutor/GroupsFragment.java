package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import jakubw.pracainz.goalsexecutor.Model.GroupTask;

public class GroupsFragment extends Fragment implements GroupTasksAdapter.OnItemListener {

    RecyclerView recyclerGroups;
    FloatingActionButton addGroupTaskBtn;

    DatabaseReference reference;
    DatabaseReference reference2;
    ArrayList<GroupTask> groupTaskList;
    ArrayList<String> userGroupTaskIdList;
    GoogleSignInAccount signInAccount;
    GroupTasksAdapter groupTasksAdapter;
    ArrayList<GroupTask> userGroupTaskList; //zadania grupowe konkretnego uzytkownika
    Integer idNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_groups, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        userGroupTaskList = new ArrayList<>();
        recyclerGroups = getView().findViewById(R.id.recyclerGroups);
        addGroupTaskBtn = getView().findViewById(R.id.addGroupTaskBtn);
        groupTaskList = new ArrayList<>();
        userGroupTaskIdList = new ArrayList<>();
        signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());
        idNumber = new Random().nextInt();
        recyclerGroups.setLayoutManager(new LinearLayoutManager(getActivity()));

        // jesli zawiera id uzytkownika to wczytaj
        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("GroupTasks");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupTaskList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    GroupTask p = dataSnapshot1.getValue(GroupTask.class);
                    groupTaskList.add(p);
                }
//                reference2 = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("UsersAdditionalInfo").child(signInAccount.getId().toString()).child("GroupTasksId");
                reference2 = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("UsersAdditionalInfo").child(signInAccount.getId().toString()).child("GroupTasksId");
                reference2.addValueEventListener(new ValueEventListener() {
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
//                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();

            }
        });

        addGroupTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewGroupTaskActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setAdapter(ArrayList<GroupTask> userGroupTaskList) {
        groupTasksAdapter = new GroupTasksAdapter(getContext(), userGroupTaskList, this);
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
        groupTask = userGroupTaskList.get(position);
        Intent intent = new Intent(getContext(), EditNextActionActivity.class);
        intent.putExtra("title", groupTask.getTitle());
        intent.putExtra("description", groupTask.getDescription());
        intent.putExtra("estimatedTime", groupTask.getEstimatedTime());
        intent.putExtra("id", groupTask.getId());
        intent.putExtra("priority", groupTask.getPriority());
        startActivity(intent);
//        Toast.makeText(getContext(), position, Toast.LENGTH_SHORT).show();
    }
}
