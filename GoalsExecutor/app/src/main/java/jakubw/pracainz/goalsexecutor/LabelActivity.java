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
import jakubw.pracainz.goalsexecutor.Model.Label;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;

public class LabelActivity extends AppCompatActivity implements LabelAdapter.OnItemListener {

    FloatingActionButton addLabelBtn;
    RecyclerView recyclerLabel;
    DatabaseReference reference;
    ArrayList<Label> labelList;
    GoogleSignInAccount signInAccount;
    LabelAdapter labelAdapter;
    Integer idNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);

        addLabelBtn = findViewById(R.id.addLabelBtn);
        recyclerLabel = findViewById(R.id.recyclerLabel);
        labelList = new ArrayList<>();
        idNumber = new Random().nextInt();
        recyclerLabel.setLayoutManager(new LinearLayoutManager(this));
        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Labels").child(signInAccount.getId().toString());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                labelList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Label p = dataSnapshot1.getValue(Label.class);
                    labelList.add(p);
                    Log.e("lablesxd", p.getName());
                }
                setAdapter(labelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();

            }
        });
        addLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToAddNewLabel();
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerLabel);
    }

    private void openDialogToAddNewLabel() {
        NewLabelDialog newLabelDialog = new NewLabelDialog();
        newLabelDialog.show(getSupportFragmentManager(), "Exeamplxd");
    }

    public void setAdapter(ArrayList<Label> labelList) {
        labelAdapter = new LabelAdapter(LabelActivity.this, labelList, this);
        recyclerLabel.setAdapter(labelAdapter); // wypelni wszystkie pola ViewHolderami
        labelAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(int position) {
        final Label label = labelList.get(position);
        Toast.makeText(this, "id" + label.getId(), Toast.LENGTH_SHORT).show();
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
                    final Label deletedTask = labelList.get(postion);

                    String id = deletedTask.getId();
                    reference.child("Label" + id).removeValue(); // usuwa z bazy
                    labelList.remove(postion);
                    setAdapter(labelList);
                    Snackbar.make(recyclerLabel, "Label " + deletedTask.getName() + " deleted!", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            labelList.add(postion, deletedTask);
                            setAdapter(labelList);
                            HashMap map = new HashMap();
                            map.put("name", deletedTask.getName());
                            map.put("color", deletedTask.getColor());
                            map.put("id", deletedTask.getId());
                            reference.child("Label" + deletedTask.getId()).updateChildren(map);
                        }
                    }).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(LabelActivity.this, R.color.colorDeleteTask))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}
