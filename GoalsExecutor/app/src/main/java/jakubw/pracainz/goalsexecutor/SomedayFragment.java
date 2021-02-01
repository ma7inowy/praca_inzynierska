package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import jakubw.pracainz.goalsexecutor.Model.Someday;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;

public class SomedayFragment extends Fragment implements SomedayAdapter.OnItemListener{

    RecyclerView recyclerSomeday;
    FloatingActionButton addNewSomedayBtn;
    DatabaseReference reference;
    ArrayList<Someday> somedayList;
    GoogleSignInAccount signInAccount;
    SomedayAdapter somedayAdapter;
    Integer idNumber;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_someday, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerSomeday = getView().findViewById(R.id.recyclerSomeday);
        addNewSomedayBtn = getView().findViewById(R.id.addNewSomedayBtn);
        somedayList = new ArrayList<>();
        idNumber = new Random().nextInt();
        recyclerSomeday.setLayoutManager(new LinearLayoutManager(getActivity()));
        signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Someday").child(signInAccount.getId().toString());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                somedayList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Someday p = dataSnapshot1.getValue(Someday.class);
                    somedayList.add(p);
                    Log.e("someday", p.getTitle());
                }
                setAdapter(somedayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        addNewSomedayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToAddNewSomedayTask();
                Toast.makeText(getContext(), "New someday", Toast.LENGTH_SHORT).show();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerSomeday);
    }

    private void openDialogToAddNewSomedayTask() {
        NewSomedayDialog newSomedayDialog = new NewSomedayDialog();
        newSomedayDialog.show(getFragmentManager(), "Example");
    }

    private void setAdapter(ArrayList<Someday> somedayList) {
        somedayAdapter = new SomedayAdapter(getActivity(), somedayList, this);
        recyclerSomeday.setAdapter(somedayAdapter); // wypelni wszystkie pola ViewHolderami
        somedayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        final Someday someday = somedayList.get(position);
        Toast.makeText(getContext(), "id" + someday.getId(), Toast.LENGTH_SHORT).show();
    }

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
                    final Someday deletedTask = somedayList.get(postion);

                    String id = deletedTask.getId();
                    reference.child("Someday" + id).removeValue(); // usuwa z bazy
                    somedayList.remove(postion);
                    setAdapter(somedayList);
                    Snackbar.make(recyclerSomeday, "Someday " + deletedTask.getTitle() + " deleted!", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            somedayList.add(postion, deletedTask);
                            setAdapter(somedayList);
                            HashMap map = new HashMap();
                            map.put("title", deletedTask.getTitle());
                            map.put("id", deletedTask.getId());
                            reference.child("Someday" + deletedTask.getId()).updateChildren(map);
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
