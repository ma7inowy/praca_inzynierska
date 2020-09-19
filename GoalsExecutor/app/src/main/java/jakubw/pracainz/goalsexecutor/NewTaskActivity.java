package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class NewTaskActivity extends AppCompatActivity {

    EditText addDate;
    EditText addTitle;
    EditText addDescription;
    Button addNewTaskBtn, addPriorityBtn;
    DatabaseReference reference;
    DatabaseReference referenceLabels;
    //    MyDoes myDoes;
    Integer number;
    ArrayList<Label> labelList;
    Spinner labelsSpinner;
    String labelName = "labelName";
    ArrayAdapter<Label> labelAdapter;
    AlertDialog addPriorityDialog;
    String priority = "3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        addDate = findViewById(R.id.addDate);
        addTitle = findViewById(R.id.addTitle);
        addDescription = findViewById(R.id.addDescription);
        addNewTaskBtn = findViewById(R.id.addNewTaskBtn);
        labelsSpinner = findViewById(R.id.labelsSpinner);
        addPriorityBtn = findViewById(R.id.addPriorityBtn);
        number = new Random().nextInt();
        labelList = new ArrayList<>();

        // do tworzenia zadania z BoxActivity
        Intent intent = getIntent();
        if (intent.hasExtra("title")) {
            addTitle.setText(intent.getStringExtra("title"));
        }

//        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor");

        //google signin
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        //pobranie labelow z bazy MOZE ZNALEZC JAKIS LEPSZY SPOSOB?
        referenceLabels = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Labels").child(signInAccount.getId().toString());
        referenceLabels.addValueEventListener(new ValueEventListener() {
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


        addNewTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("NextAction").child(signInAccount.getId()).child("Does" + number);
                HashMap map = new HashMap();
                map.put("titledoes", addTitle.getText().toString());
                map.put("descdoes", addDescription.getText().toString());
                map.put("datedoes", addDate.getText().toString());
                map.put("id", number.toString());
                map.put("labelName", labelName);
                map.put("priority", priority);
                reference.updateChildren(map);
                sendResultToBoxActivity();
                Toast.makeText(NewTaskActivity.this, addTitle.getText().toString() + " " + addDescription.getText().toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        addPriorityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrioritiesDialog();
            }
        });

        labelsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Label label = (Label) parent.getSelectedItem();
                labelName = label.getName();
                Toast.makeText(parent.getContext(), labelName, Toast.LENGTH_LONG).show();
                Log.e("labelName", "wcisniete");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(NewTaskActivity.this, "nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // dla usuwania zadania z BoxActiv
    private void sendResultToBoxActivity() {
        Intent i = getIntent();
//        i.putExtra("taskAdded", true);
        i.putExtra("taskAdded",true);
        setResult(RESULT_OK,i);
    }

    private void showPrioritiesDialog() {
        final String[] priorities = {"High", "Medium", "Low"};
        AlertDialog.Builder builder = new AlertDialog.Builder(NewTaskActivity.this);
        builder.setTitle("Choose priority");
        builder.setSingleChoiceItems(priorities, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                priority = String.valueOf(which + 1);
                Toast.makeText(NewTaskActivity.this, priority, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addPriorityBtn.setText("PRIORYTET: " + priorities[Integer.valueOf(priority) - 1]);

                Toast.makeText(NewTaskActivity.this, priority, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(NewTaskActivity.this, priority, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void setLabelAdapter(ArrayList<Label> list) {
        //adding labels to spinner
        labelAdapter = new ArrayAdapter<Label>(this, android.R.layout.simple_spinner_item, list);
        labelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        labelsSpinner.setAdapter(labelAdapter);
        labelAdapter.notifyDataSetChanged();
    }

    public void getSelectedLabel(View view) {
        Label label = (Label) labelsSpinner.getSelectedItem();
        Toast.makeText(this, label.getId(), Toast.LENGTH_SHORT).show();
    }


}
