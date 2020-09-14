package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import java.util.Calendar;
import java.util.HashMap;

public class EditDoesActivity extends AppCompatActivity {

    EditText editDate;
    EditText editTitle;
    EditText editDescription;
    Button editTaskBtn;
    String id;
    DatabaseReference reference;
    DatabaseReference referenceLabels;
    ArrayList<Label> labelList;

    GoogleSignInAccount signInAccount;
    Spinner labelsSpinnerEdit;
    String labelId;
    ArrayAdapter<Label> labelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_does);

        editDate = findViewById(R.id.editDate);
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editTaskBtn = findViewById(R.id.editTaskBtn);
        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        labelList = new ArrayList<>();

        labelsSpinnerEdit = findViewById(R.id.labelsSpinnerEdit);
        Intent intent = getIntent();
        editTitle.setText(intent.getStringExtra("title"));
        editDate.setText(intent.getStringExtra("date"));
        editDescription.setText(intent.getStringExtra("desc"));
        id = intent.getStringExtra("id");
        labelId = intent.getStringExtra("labelName");

        //pobranie labelow z bazy MOZE ZNALEZC JAKIS LEPSZY SPOSOB? raz pobrac najlepiej i tyle
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

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("NextAction").child(signInAccount.getId().toString()).child("Does" + id);
        editTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap map = new HashMap();
                map.put("titledoes", editTitle.getText().toString());
                map.put("descdoes", editDescription.getText().toString());
                map.put("datedoes", editDate.getText().toString());
                map.put("labelName", labelId);
                reference.updateChildren(map);
                Toast.makeText(EditDoesActivity.this, editTitle.getText().toString() + " " + editDescription.getText().toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        labelsSpinnerEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Label label = (Label) parent.getSelectedItem();
                labelId = label.getName();
                Toast.makeText(parent.getContext(), labelId, Toast.LENGTH_LONG).show();
                Log.e("labelName", "wcisniete");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(EditDoesActivity.this, "nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLabelAdapter(ArrayList<Label> list) {
        //adding labels to spinner
        labelAdapter = new ArrayAdapter<Label>(this, android.R.layout.simple_spinner_item, list);
        labelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        labelsSpinnerEdit.setAdapter(labelAdapter);
        labelsSpinnerEdit.setSelection(getLabelForTask(list));
        labelAdapter.notifyDataSetChanged();
    }

    private int getLabelForTask(ArrayList<Label> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals(labelId))
                return i;
        }
        return 0;
    }

}
