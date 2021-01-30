package jakubw.pracainz.goalsexecutor;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import jakubw.pracainz.goalsexecutor.Model.Project;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;

public class ProjectsFragment extends Fragment implements ProjectsAdapter.OnItemListener {

    FloatingActionButton addNewProjectBtn;
    RecyclerView recyclerProject;
    DatabaseReference reference;
    ArrayList<Project> projectList;
    GoogleSignInAccount signInAccount;
    ProjectsAdapter projectAdapter;
    Integer idNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_projects, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        addNewProjectBtn = getView().findViewById(R.id.addNewProjectBtn);
        recyclerProject = getView().findViewById(R.id.recyclerProject);
        projectList = new ArrayList<>();
        idNumber = new Random().nextInt();
        recyclerProject.setLayoutManager(new LinearLayoutManager(getActivity()));
        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Projects").child(signInAccount.getId().toString());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                projectList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Project p = dataSnapshot1.getValue(Project.class);
                    projectList.add(p);
                    Log.e("projects", p.getTitle());
                }
                setAdapter(projectList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();

            }
        });
        addNewProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToAddNewProject();
                Toast.makeText(getContext(), "New Proj", Toast.LENGTH_SHORT).show();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerProject);
    }

    public void setAdapter(ArrayList<Project> projectList) {
        projectAdapter = new ProjectsAdapter(getActivity(), projectList, this);
        recyclerProject.setAdapter(projectAdapter); // wypelni wszystkie pola ViewHolderami
        projectAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        final Project project = projectList.get(position);
        Toast.makeText(getContext(), "id" + project.getId(), Toast.LENGTH_SHORT).show();
    }

    private void openDialogToAddNewProject() {
        NewProjectDialog newProjectDialog = new NewProjectDialog();
        newProjectDialog.show(getFragmentManager(), "Example");
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
                    final Project deletedTask = projectList.get(postion);

                    String id = deletedTask.getId();
                    reference.child("Project" + id).removeValue(); // usuwa z bazy
                    projectList.remove(postion);
                    setAdapter(projectList);
                    Snackbar.make(recyclerProject, "Project " + deletedTask.getTitle() + " deleted!", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            projectList.add(postion, deletedTask);
                            setAdapter(projectList);
                            HashMap map = new HashMap();
                            map.put("title", deletedTask.getTitle());
                            map.put("id", deletedTask.getId());
                            reference.child("Project" + deletedTask.getId()).updateChildren(map);
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
}
