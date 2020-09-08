package jakubw.pracainz.goalsexecutor;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class ProjectsActivity extends AppCompatActivity implements ProjectsAdapter.OnNoteListener {

    FloatingActionButton addNewProjectFloatingBtn;
    RecyclerView myProjects;
    DatabaseReference reference;
    ArrayList<Project> projectList;
    GoogleSignInAccount signInAccount;
    ProjectsAdapter projectsAdapter;
    Integer number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        addNewProjectFloatingBtn = findViewById(R.id.addNewProjectFloatingBtn);
        myProjects = findViewById(R.id.myProjects);
        projectList = new ArrayList<>();
        number = new Random().nextInt();
        myProjects.setLayoutManager(new LinearLayoutManager(this));
        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);

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
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();

            }
        });
        addNewProjectFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToAddNewProject();
                Toast.makeText(ProjectsActivity.this, "New Proj", Toast.LENGTH_SHORT).show();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(myProjects);

    }

    public void setAdapter(ArrayList<Project> projectList) {
        projectsAdapter = new ProjectsAdapter(ProjectsActivity.this, projectList, this);
        myProjects.setAdapter(projectsAdapter); // wypelni wszystkie pola ViewHolderami
        projectsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNoteClick(int position) {
        final Project project = projectList.get(position);
        Toast.makeText(this, "id" + project.getId(), Toast.LENGTH_SHORT).show();
    }

    private void openDialogToAddNewProject() {
        NewProjectDialog newProjectDialog = new NewProjectDialog();
        newProjectDialog.show(getSupportFragmentManager(),"Example");
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
                    Snackbar.make(myProjects, "Project " + deletedTask.getTitle() + " deleted!", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
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
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ProjectsActivity.this, R.color.colorDeleteTask))
//                    .addBackgroundColor(ContextCompat.getColor(KontenerActivity.this, R.color.colorPrimary))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}
