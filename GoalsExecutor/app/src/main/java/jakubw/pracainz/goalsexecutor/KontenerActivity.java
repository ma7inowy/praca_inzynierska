package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class KontenerActivity extends AppCompatActivity implements DoesAdapter.OnNoteListener {

    TextView titlepage, endpage;
    Button btnAddNew, btnSort;

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
        btnSort = findViewById(R.id.btnsort);

        ourdoes = findViewById(R.id.ourdoes);
        ourdoes.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<MyDoes>();
//        setAdapter(list);

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //set code
                list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    MyDoes p = dataSnapshot1.getValue(MyDoes.class);
                    list.add(p);
                }
                setAdapter(list);
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

        //sortowanie zadan po dlugosci tytulu
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MyDoes> list2 = new ArrayList();
                for (MyDoes myDoes : list) {
                    if (myDoes.getTitledoes().toLowerCase().equals("123")) {
                        list2.add(myDoes);
                    }
                }
                setAdapter(list2);
            }
        });

    }

    public void setAdapter(ArrayList<MyDoes> list) {
        doesAdapter = new DoesAdapter(KontenerActivity.this, list, this);
        ourdoes.setAdapter(doesAdapter); // wypelni wszystkie pola ViewHolderami
        doesAdapter.notifyDataSetChanged();
    }


    @Override
    public void onNoteClick(int position) {
        final MyDoes myDoes = list.get(position);
        Intent intent = new Intent(this, EditDoesActivity.class);
        intent.putExtra("title", myDoes.getTitledoes());
        intent.putExtra("desc", myDoes.getDescdoes());
        intent.putExtra("date", myDoes.getDatedoes());
        intent.putExtra("id", myDoes.getId());
        startActivity(intent);
        Toast.makeText(this, "id" +  myDoes.getId(), Toast.LENGTH_SHORT).show();
    }

    public ArrayList<MyDoes> sort(List<MyDoes> list) {
        ArrayList<MyDoes> listnew = new ArrayList<>();
        listnew.add(0, list.get(list.size() - 1));
        listnew.add(1, list.get(list.size() - 2));
        listnew.add(2, list.get(list.size() - 3));

        return listnew;

    }

    public void sortByMiasto() {
        ArrayList<MyDoes> listnew = new ArrayList<>();
        for (MyDoes item : list) {
            if (item.getTitledoes().toLowerCase().equals("miasto")) {
                listnew.add(item);
            }
        }
        setAdapter(listnew);
    }

    // menu w prawym gornym do filtrowania
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(this, "item1", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item2:
                Toast.makeText(this, "akcja", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item3:
                Toast.makeText(this, "item3", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.etykieta_dom:
                Toast.makeText(this, "dom", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.etykieta_miasto:
                sortByMiasto();
                Toast.makeText(this, "miasto", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
