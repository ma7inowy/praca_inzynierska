package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class NewTaskActivity extends AppCompatActivity {

    EditText addDate;
    EditText addTitle;
    Button addNewTaskBtn;
    DatabaseReference reference;
    //    MyDoes myDoes;
    Integer number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        addDate = findViewById(R.id.addDate);
        addTitle = findViewById(R.id.addTitle);
        addNewTaskBtn = findViewById(R.id.addNewTaskBtn);
        number = new Random().nextInt();
        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor");

        addNewTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Does" + number);
//            saveNote();
//              myDoes = new MyDoes(addTitle.getText().toString(),addDate.getText().toString(), "jakis opis");
//              reference.push().setValue(myDoes);
              reference.addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      dataSnapshot.getRef().child("titledoes").setValue(addTitle.getText().toString());
                      dataSnapshot.getRef().child("descdoes").setValue(addDate.getText().toString());
                      dataSnapshot.getRef().child("datedoes").setValue(addDate.getText().toString());
                      finish();
//                        Intent intent = new Intent(NewTaskActivity.this, KontenerActivity.class);
//                        startActivity(intent);
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {
                      Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                  }
              });
            }
        });

    }
}
