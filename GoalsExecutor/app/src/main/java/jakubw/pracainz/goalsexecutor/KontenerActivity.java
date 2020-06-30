package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class KontenerActivity extends AppCompatActivity {

    TextView titlepage, endpage;
    Button btnAddNew;

    DatabaseReference reference;
    RecyclerView ourdoes;
    ArrayList<MyDoes> list;
    DoesAdapter doesAdapter;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontener);
            titlepage = findViewById(R.id.titlepage);
            endpage = findViewById(R.id.endpage);

            btnAddNew = findViewById(R.id.btnAddNew);

            ourdoes = findViewById(R.id.ourdoes);
            ourdoes.setLayoutManager(new LinearLayoutManager(this));
            list = new ArrayList<MyDoes>();

            reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //set code
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                    {
                        MyDoes p = dataSnapshot1.getValue(MyDoes.class);
                        list.add(p);
                    }
                    doesAdapter = new DoesAdapter(KontenerActivity.this, list);
                    ourdoes.setAdapter(doesAdapter); // wypelni wszystkie pola ViewHolderami
                    doesAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //set code
                    Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                }
            });

            btnAddNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(KontenerActivity.this, NewTaskActivity.class);
                    startActivity(intent);
                }
            });

    }
}
