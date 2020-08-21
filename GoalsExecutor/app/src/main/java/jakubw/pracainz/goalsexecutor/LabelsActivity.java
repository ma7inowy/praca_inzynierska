package jakubw.pracainz.goalsexecutor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class LabelsActivity extends AppCompatActivity implements LabelsAdapter.OnNoteListener {

    FloatingActionButton addNewLabelFloatingBtn;
    RecyclerView myLabels;
    DatabaseReference reference;
    ArrayList<Label> labelList;
    GoogleSignInAccount signInAccount;
    LabelsAdapter labelsAdapter;
    Integer number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labels);

        addNewLabelFloatingBtn = findViewById(R.id.addNewLabelFloatingBtn);
        myLabels = findViewById(R.id.mylabels);
        labelList = new ArrayList<>();
        number = new Random().nextInt();
        myLabels.setLayoutManager(new LinearLayoutManager(this));
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
                    Log.e("lablesxd",p.getName());
                }
                setAdapter(labelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();

            }
        });
        addNewLabelFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToAddNewLabel();
            }
        });

    }

    private void openDialogToAddNewLabel() {
        NewLabelDialog newLabelDialog = new NewLabelDialog();
        newLabelDialog.show(getSupportFragmentManager(),"Exeamplxd");
    }

    public void setAdapter(ArrayList<Label> labelList) {
        labelsAdapter = new LabelsAdapter(LabelsActivity.this, labelList, this);
        myLabels.setAdapter(labelsAdapter); // wypelni wszystkie pola ViewHolderami
        labelsAdapter.notifyDataSetChanged();
    }


    @Override
    public void onNoteClick(int position) {
        final Label label = labelList.get(position);
//        Intent intent = new Intent(this, EditDoesActivity.class);
//        intent.putExtra("title", myDoes.getTitledoes());
//        intent.putExtra("desc", myDoes.getDescdoes());
//        intent.putExtra("date", myDoes.getDatedoes());
//        intent.putExtra("id", myDoes.getId());
//        startActivity(intent);
        Toast.makeText(this, "id" + label.getId(), Toast.LENGTH_SHORT).show();
    }
}
