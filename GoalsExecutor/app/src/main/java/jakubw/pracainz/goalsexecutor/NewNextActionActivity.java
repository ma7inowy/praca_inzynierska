package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
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
import java.util.Random;

import jakubw.pracainz.goalsexecutor.Model.Label;

public class NewNextActionActivity extends AppCompatActivity {

    EditText addTitle;
    EditText addDescription;
    Button addNextActionBtn, addPriorityBtn, addEstimationTimeBtn;
    DatabaseReference reference;
    DatabaseReference referenceLabel;
    //    NextAction myDoes;
    Integer idNumber;
    ArrayList<Label> labelList;
    Spinner labelSpinner;
    String labelName = "labelName";
    ArrayAdapter<Label> labelAdapter;
    String priority = "3";
    int estimatedTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        addTitle = findViewById(R.id.addTitle);
        addDescription = findViewById(R.id.addDescription);
        addNextActionBtn = findViewById(R.id.addNextActionBtn);
        labelSpinner = findViewById(R.id.labelSpinner);
        addPriorityBtn = findViewById(R.id.addPriorityBtn);
        addEstimationTimeBtn = findViewById(R.id.addEstimationTimeBtn);
        idNumber = new Random().nextInt();
        labelList = new ArrayList<>();

        // do tworzenia zadania z BoxFragment
        Intent intent = getIntent();
        if (intent.hasExtra("title")) {
            addTitle.setText(intent.getStringExtra("title"));
        }

        //google signin
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        //pobranie labelow z bazy MOZE ZNALEZC JAKIS LEPSZY SPOSOB?
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


        addNextActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("NextAction").child(signInAccount.getId()).child("Na" + idNumber);
                HashMap map = new HashMap();
                map.put("title", addTitle.getText().toString());
                map.put("description", addDescription.getText().toString());
                map.put("id", idNumber.toString());
                map.put("labelName", labelName);
                map.put("estimatedTime", estimatedTime);
                map.put("priority", priority);
                reference.updateChildren(map);
                sendResultToBoxActivity();
                Toast.makeText(NewNextActionActivity.this, addTitle.getText().toString() + " " + addDescription.getText().toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        addPriorityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrioritiesDialog();
            }
        });
        addEstimationTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEstimationTimeDialog();
            }
        });

        labelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Label label = (Label) parent.getSelectedItem();
                labelName = label.getName();
                Toast.makeText(parent.getContext(), labelName, Toast.LENGTH_LONG).show();
                Log.e("labelName", "wcisniete");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(NewNextActionActivity.this, "nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEstimationTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewNextActionActivity.this);
        builder.setTitle("Choose estimated time:");

        View view = LayoutInflater.from(NewNextActionActivity.this).inflate(R.layout.estimated_time_dialog, null);
        final TextView estimatedTimeProgress = view.findViewById(R.id.estimatedTimeProgress);
        final SeekBar estimatedTimeSeekBar = view.findViewById(R.id.estimatedTimeSeekBar);
        estimatedTimeSeekBar.setMax(240);
        estimatedTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                estimatedTimeProgress.setText("" + progress + " MIN");
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
                Toast.makeText(NewNextActionActivity.this, String.valueOf(estimatedTime), Toast.LENGTH_SHORT).show();
                addEstimationTimeBtn.setText("ESTIMATED TIME: " + estimatedTime + " MIN");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(NewNextActionActivity.this, priority, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setView(view);
        builder.show();
    }

    // dla usuwania zadania z BoxActiv
    private void sendResultToBoxActivity() {
        Intent i = getIntent();
//        i.putExtra("taskAdded", true);
        i.putExtra("taskAdded", true);
        setResult(RESULT_OK, i);
    }

    private void showPrioritiesDialog() {
        final String[] priorities = {"High", "Medium", "Low"};
        AlertDialog.Builder builder = new AlertDialog.Builder(NewNextActionActivity.this);
        builder.setTitle("Choose priority:");
        builder.setSingleChoiceItems(priorities, Integer.valueOf(priority) - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                priority = String.valueOf(which + 1);
                Toast.makeText(NewNextActionActivity.this, priority, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addPriorityBtn.setText("PRIORITY: " + priorities[Integer.valueOf(priority) - 1]);
                setPriorityButtonBackground();

                Toast.makeText(NewNextActionActivity.this, priority, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(NewNextActionActivity.this, priority, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void setPriorityButtonBackground() {
        int color = setColorForPriorityButton(priority);
        PaintDrawable pd = new PaintDrawable(color);
        pd.setCornerRadius(70);
        addPriorityBtn.setBackground(pd);
    }

    private int setColorForPriorityButton(String priority) {
        if (priority.equals(String.valueOf(1)))
            return Color.RED;
        else if (priority.equals(String.valueOf(2)))
            return Color.YELLOW;
        else if (priority.equals(String.valueOf(3)))
            return Color.GREEN;
        else {
            Toast.makeText(this, "error!", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    private void setLabelAdapter(ArrayList<Label> list) {
        //adding labels to spinner
        labelAdapter = new ArrayAdapter<Label>(this, android.R.layout.simple_spinner_item, list);
        labelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        labelSpinner.setAdapter(labelAdapter);
        labelAdapter.notifyDataSetChanged();
    }

    public void getSelectedLabel(View view) {
        Label label = (Label) labelSpinner.getSelectedItem();
        Toast.makeText(this, label.getId(), Toast.LENGTH_SHORT).show();
    }


}
