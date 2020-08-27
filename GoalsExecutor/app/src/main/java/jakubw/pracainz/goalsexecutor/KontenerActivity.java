package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static androidx.recyclerview.widget.ItemTouchHelper.*;

public class KontenerActivity extends AppCompatActivity implements DoesAdapter.OnNoteListener {

    TextView titlepage, endpage;
    Button btnAddNew, btnSort;
    DatabaseReference reference;
    DatabaseReference referenceLabels;
    RecyclerView ourdoes;
    ArrayList<MyDoes> list;
    ArrayList<MyDoes> filteredList;
    ArrayList<Label> labelList;
    DoesAdapter doesAdapter;
    GoogleSignInAccount signInAccount;
    boolean isFiltered = false; //gdzie te flage tu czy oncreate?

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
        list = new ArrayList<>();
        labelList = new ArrayList<>();
        filteredList = new ArrayList<>();

        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("NextAction").child(signInAccount.getId().toString());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    MyDoes p = dataSnapshot1.getValue(MyDoes.class);
                    list.add(p);
                }
                setAdapter(list);// zrob tak zeby tylko dodawalo 1 a nie od nowa czyscilo liste
                isFiltered = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        //pobieranie labelow MOZE ZROB TYLKO ZEBY RAZ POBIERALO? ? ?
        referenceLabels = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Labels").child(signInAccount.getId().toString());
        referenceLabels.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                labelList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Label p = dataSnapshot1.getValue(Label.class);
                    labelList.add(p);
                }
                setAdapter(list); // tu dodalem bo tak to wczytuje mi puste labele
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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

        //tylko do testowania
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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(ourdoes);


    }

    // swipe left to delete task
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int postion = viewHolder.getAdapterPosition();

            switch (direction) {
                case LEFT:
//                    final MyDoes deletedTask = list.get(postion);
                    ArrayList<MyDoes> listForDelete;
                    final MyDoes deletedTask;
                    if (isFiltered) {
                        deletedTask = filteredList.get(postion);
                        listForDelete = filteredList;
                    } else {
                        deletedTask = list.get(postion);
                        listForDelete = list;
                    }


                    String id = listForDelete.get(postion).getId();
                    reference.child("Does" + id).removeValue(); // usuwa z bazy
                    listForDelete.remove(postion);
                    setAdapter(listForDelete);
                    Snackbar.make(ourdoes, "Task " + deletedTask.getTitledoes() + " deleted!", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(KontenerActivity.this, String.valueOf(isFiltered), Toast.LENGTH_SHORT).show();
                            if (isFiltered) {
                                filteredList.add(postion, deletedTask);
                                setAdapter(filteredList); // moze lepiej nasluchiwac na jedna pozycje?

                            } else {
                                list.add(postion, deletedTask);
                                setAdapter(list);
                            }
                            HashMap map = new HashMap();
                            map.put("titledoes", deletedTask.getTitledoes());
                            map.put("descdoes", deletedTask.getDescdoes());
                            map.put("datedoes", deletedTask.getDatedoes());
                            map.put("id", deletedTask.getId());
                            map.put("labelName", deletedTask.getLabelName());
                            reference.child("Does" + deletedTask.getId()).updateChildren(map);
                        }
                    }).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(KontenerActivity.this, R.color.colorDeleteTask))
//                    .addBackgroundColor(ContextCompat.getColor(KontenerActivity.this, R.color.colorPrimary))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public void setAdapter(ArrayList<MyDoes> list) {
        doesAdapter = new DoesAdapter(KontenerActivity.this, list, this, labelList);
        ourdoes.setAdapter(doesAdapter); // wypelni wszystkie pola ViewHolderami
        doesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNoteClick(int position) {
        final MyDoes myDoes;
        if (isFiltered) myDoes = filteredList.get(position);
        else myDoes = list.get(position);

        Intent intent = new Intent(this, EditDoesActivity.class);
        intent.putExtra("title", myDoes.getTitledoes());
        intent.putExtra("desc", myDoes.getDescdoes());
        intent.putExtra("date", myDoes.getDatedoes());
        intent.putExtra("id", myDoes.getId());
        intent.putExtra("labelName", myDoes.getLabelName());
        startActivity(intent);
        Toast.makeText(this, "id" + myDoes.getId(), Toast.LENGTH_SHORT).show();
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
            case R.id.item2:
                setAdapter(list);
                isFiltered = false;
                return true;
        }

//            case R.id.item2:
//                Toast.makeText(this, "akcja", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.item3:
//                Toast.makeText(this, "item3", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.etykieta_dom:
//                Toast.makeText(this, "dom", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.etykieta_miasto:
//                sortByMiasto();
//                Toast.makeText(this, "miasto", Toast.LENGTH_SHORT).show();
//                return true;

        //nasluchuje ktore filtrowanie zostalo wcisnelo
        filteredList = new ArrayList<>();
        if (!item.getTitle().equals("FILTRUJ...")) {
            for (MyDoes does : list) {
                if (item.getTitle().equals(does.getLabelName())) {
                    filteredList.add(does);
                }
            }
            setAdapter(filteredList);
            isFiltered = true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // albo ondatachange albo po prostu pobrac wszystkie labely ????
        invalidateOptionsMenu();
        int i = 0;
        for (Label item : labelList) {
//            menu.add(0,i, Menu.NONE,item.getTitledoes());
            menu.getItem(1).getSubMenu().add(0, i, Menu.NONE, item.getName());
            i++;
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
