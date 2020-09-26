package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class EditNextActionActivity extends AppCompatActivity {

    EditText editTitle;
    EditText editDescription;
    Button editNextActionBtn,editEstimationTimeBtn,editPriorityBtn;
    String id;
    DatabaseReference reference;
    DatabaseReference referenceLabel;
    ArrayList<Label> labelList;

    GoogleSignInAccount signInAccount;
    Spinner editLabelSpinner;
    String labelId;
    ArrayAdapter<Label> labelAdapter;
    int estimatedTime;
    String priority;
    final String[] priorities = {"High", "Medium", "Low"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_does);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editNextActionBtn = findViewById(R.id.editTaskBtn);
        editEstimationTimeBtn = findViewById(R.id.editEstimationTimeBtn);
        editPriorityBtn = findViewById(R.id.editPriorityBtn);
        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        labelList = new ArrayList<>();

        editLabelSpinner = findViewById(R.id.labelsSpinnerEdit);
        Intent intent = getIntent();
        editTitle.setText(intent.getStringExtra("title"));
        editDescription.setText(intent.getStringExtra("description"));
        id = intent.getStringExtra("id");
        labelId = intent.getStringExtra("labelName");
        estimatedTime = intent.getIntExtra("estimatedTime",0);
        editEstimationTimeBtn.setText("Potrzebny czas: " + estimatedTime + "minut");
        priority = intent.getStringExtra("priority");
        editPriorityBtn.setText("PRIORYTET: " + priorities[Integer.valueOf(priority) - 1]);

        //pobranie labelow z bazy MOZE ZNALEZC JAKIS LEPSZY SPOSOB? raz pobrac najlepiej i tyle
        referenceLabel = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Labels").child(signInAccount.getId().toString());
        referenceLabel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                labelList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Label p = dataSnapshot1.getValue(Label.class);
                    labelList.add(p);
                }
                setLabelAdapter(labelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("NextAction").child(signInAccount.getId().toString()).child("Does" + id);
        editNextActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap map = new HashMap();
                map.put("title", editTitle.getText().toString());
                map.put("description", editDescription.getText().toString());
                map.put("estimatedTime", estimatedTime);
                map.put("priority",priority);
                map.put("labelName", labelId);
                reference.updateChildren(map);
                Toast.makeText(EditNextActionActivity.this, editTitle.getText().toString() + " " + editDescription.getText().toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        editLabelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Label label = (Label) parent.getSelectedItem();
                labelId = label.getName();
                Toast.makeText(parent.getContext(), labelId, Toast.LENGTH_LONG).show();
                Log.e("labelName", "wcisniete");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(EditNextActionActivity.this, "nothing selected", Toast.LENGTH_SHORT).show();
            }
        });

        editEstimationTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEstimationTimeDialog();
            }
        });

        editPriorityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrioritiesDialog();
            }
        });
    }

    private void setLabelAdapter(ArrayList<Label> list) {
        //adding labels to spinner
        labelAdapter = new ArrayAdapter<Label>(this, android.R.layout.simple_spinner_item, list);
        labelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editLabelSpinner.setAdapter(labelAdapter);
        editLabelSpinner.setSelection(getLabelForTask(list));
        labelAdapter.notifyDataSetChanged();
    }

    private int getLabelForTask(ArrayList<Label> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals(labelId))
                return i;
        }
        return 0;
    }

    private void showEstimationTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditNextActionActivity.this);
        builder.setTitle("Choose estimated time");

        View view  = LayoutInflater.from(EditNextActionActivity.this).inflate(R.layout.estimated_time_dialog, null);
        final TextView estimatedTimeProgress = view.findViewById(R.id.estimatedTimeProgress);
        final SeekBar estimatedTimeSeekBar = view.findViewById(R.id.estimatedTimeSeekBar);
        estimatedTimeSeekBar.setMax(240);
        estimatedTimeSeekBar.setProgress(estimatedTime);
        estimatedTimeProgress.setText(estimatedTime + " min");
        estimatedTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                estimatedTimeProgress.setText(progress  + " min");
                estimatedTime = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(EditNextActionActivity.this, String.valueOf(estimatedTime), Toast.LENGTH_SHORT).show();
                editEstimationTimeBtn.setText("Potrzebny czas: " + estimatedTime + "minut");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(EditNextActionActivity.this, priority, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setView(view);
        builder.show();
    }

    private void showPrioritiesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditNextActionActivity.this);
        builder.setTitle("Choose priority");
        builder.setSingleChoiceItems(priorities, Integer.valueOf(priority)-1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                priority = String.valueOf(which + 1);
                Toast.makeText(EditNextActionActivity.this, priority, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editPriorityBtn.setText("PRIORYTET: " + priorities[Integer.valueOf(priority) - 1]);

                Toast.makeText(EditNextActionActivity.this, priority, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(EditNextActionActivity.this, priority, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
