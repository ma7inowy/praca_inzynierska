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

public class EditDoesActivity extends AppCompatActivity {

    EditText editDate;
    EditText editTitle;
    Button editTaskBtn;
    String id;

    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_does);

        editDate = findViewById(R.id.editDate);
        editTitle = findViewById(R.id.editTitle);
        editTaskBtn = findViewById(R.id.editTaskBtn);

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor");

        Intent intent = getIntent();
        editTitle.setText(intent.getStringExtra("title"));
        editDate.setText(intent.getStringExtra("date"));
        id = intent.getStringExtra("id");

        editTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Does" + id);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("titledoes").setValue(editTitle.getText().toString());
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Toast.makeText(EditDoesActivity.this, id, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
