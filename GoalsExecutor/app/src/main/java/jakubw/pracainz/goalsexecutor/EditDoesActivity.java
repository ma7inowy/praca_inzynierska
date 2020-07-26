package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditDoesActivity extends AppCompatActivity {

    EditText editDate;
    EditText editTitle;
    EditText editDescription;
    Button editTaskBtn;
    String id;

    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_does);

        editDate = findViewById(R.id.editDate);
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editTaskBtn = findViewById(R.id.editTaskBtn);

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor");

        Intent intent = getIntent();
        editTitle.setText(intent.getStringExtra("title"));
        editDate.setText(intent.getStringExtra("date"));
        editDescription.setText(intent.getStringExtra("desc"));
        id = intent.getStringExtra("id");

        editTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Does" + id);
                HashMap map = new HashMap();
                map.put("titledoes", editTitle.getText().toString());
                map.put("descdoes", editDescription.getText().toString());
                map.put("datedoes", editDate.getText().toString());
                reference.updateChildren(map);
                finish();
                //                reference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        HashMap map = new HashMap();
//                        map.put("titledoes", editTitle.getText().toString());
//                        map.put("descdoes", editDescription.getText().toString());
//                        map.put("datedoes", editDate.getText().toString());
//                        dataSnapshot.getRef().updateChildren(map);
//                        finish();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
                Toast.makeText(EditDoesActivity.this, id, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
