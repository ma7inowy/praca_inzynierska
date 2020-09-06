package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
    Button addNewTaskBtn;
    DatabaseReference reference;
    DatabaseReference referenceLabels;
    //    MyDoes myDoes;
    Integer number;
    ArrayList<Label> labelList;
    Spinner labelsSpinner;
    String labelName = "labelName";
    ArrayAdapter<Label> labelAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        addDate = findViewById(R.id.addDate);
        addTitle = findViewById(R.id.addTitle);
        addDescription = findViewById(R.id.addDescription);
        addNewTaskBtn = findViewById(R.id.addNewTaskBtn);
        labelsSpinner = findViewById(R.id.labelsSpinner);
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
                reference.updateChildren(map);
                Toast.makeText(NewTaskActivity.this, addTitle.getText().toString() + " " + addDescription.getText().toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        labelsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Label label = (Label) parent.getSelectedItem();
                labelName = label.getName();
                Toast.makeText(parent.getContext(), labelName, Toast.LENGTH_LONG).show();
                Log.e("labelName","wcisniete");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(NewTaskActivity.this, "nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLabelAdapter(ArrayList<Label> list) {
        //adding labels to spinner
        labelAdapter = new ArrayAdapter<Label>(this, android.R.layout.simple_spinner_item, list);
        labelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        labelsSpinner.setAdapter(labelAdapter);
        labelAdapter.notifyDataSetChanged();
    }

    public void getSelectedLabel(View view){
        Label label = (Label) labelsSpinner.getSelectedItem();
        Toast.makeText(this, label.getId(), Toast.LENGTH_SHORT).show();
    }

//    public void saveNote(){
////        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Does" + number);
//        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Does" + number);
//        MyDoes myDoes = new MyDoes(addTitle.getText().toString(),addDate.getText().toString(), "jakis opis");
//        reference.push().getRef().child("titleEvent").setValue(addTitle.getText().toString());
//        finish();
//    }
}
