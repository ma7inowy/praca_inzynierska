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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class NewTaskActivity extends AppCompatActivity {

    EditText addDate;
    EditText addTitle;
    EditText addDescription;
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
        addDescription = findViewById(R.id.addDescription);
        addNewTaskBtn = findViewById(R.id.addNewTaskBtn);
        number = new Random().nextInt();
//        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor");

        //google signin
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        addNewTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("NextAction").child(signInAccount.getId()).child("Does" + number);

//              reference.addValueEventListener(new ValueEventListener() {
//                  @Override
//                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                      dataSnapshot.getRef().child("titleEvent").setValue(addTitle.getText().toString());
//                      dataSnapshot.getRef().child("descdoes").setValue(addDescription.getText().toString());
//                      dataSnapshot.getRef().child("datedoes").setValue(addDate.getText().toString());
//                      dataSnapshot.getRef().child("id").setValue(number.toString());
//                      finish();
//
//                  }
//
//                  @Override
//                  public void onCancelled(@NonNull DatabaseError databaseError) {
//                      Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
//                  }
//              });
                HashMap map = new HashMap();
                map.put("titledoes", addTitle.getText().toString());
                map.put("descdoes", addDescription.getText().toString());
                map.put("datedoes", addDate.getText().toString());
                map.put("id",number.toString());
                reference.updateChildren(map);
                Toast.makeText(NewTaskActivity.this, addTitle.getText().toString() + " " + addDescription.getText().toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });



    }

//    public void saveNote(){
////        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Does" + number);
//        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Does" + number);
//        MyDoes myDoes = new MyDoes(addTitle.getText().toString(),addDate.getText().toString(), "jakis opis");
//        reference.push().getRef().child("titleEvent").setValue(addTitle.getText().toString());
//        finish();
//    }
}
