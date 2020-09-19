package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class KontenerActivity extends Fragment implements DoesAdapter.OnNoteListener, TaskFilterDialog.TaskFilterDialogListener {

    TextView titlepage, endpage;
    Button btnSort;
    DatabaseReference reference;
    DatabaseReference referenceLabels;
    RecyclerView ourdoes;
    ArrayList<MyDoes> list;
    ArrayList<MyDoes> filteredList;
    ArrayList<Label> labelList;
    DoesAdapter doesAdapter;
    FloatingActionButton addNewTaskFloatingBtn;
    FloatingActionButton addFilterFloatingBtn;

    GoogleSignInAccount signInAccount;
    boolean isFiltered = false; //gdzie te flage tu czy oncreate?

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_kontener, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();
        titlepage = getView().findViewById(R.id.titlepage);
        endpage = getView().findViewById(R.id.endpage);
        btnSort = getView().findViewById(R.id.btnsort);
        ourdoes = getView().findViewById(R.id.ourdoes);
        ourdoes.setLayoutManager(new LinearLayoutManager(getActivity()));
        addNewTaskFloatingBtn = getView().findViewById(R.id.addNewTaskFloatingBtn);
        addFilterFloatingBtn = getView().findViewById(R.id.addFilterFloatingBtn);
        list = new ArrayList<>();
        labelList = new ArrayList<>();
        filteredList = new ArrayList<>();

        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

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
//                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
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
//                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });


        addNewTaskFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewTaskActivity.class);
                startActivity(intent);
            }
        });

        addFilterFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterDialog();
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

    private void openFilterDialog() {
        TaskFilterDialog taskFilterDialog = new TaskFilterDialog();
        taskFilterDialog.setTargetFragment(KontenerActivity.this, 1);
        taskFilterDialog.show(getFragmentManager(), "Filter dialog");
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_kontener);
//        titlepage = findViewById(R.id.titlepage);
//        endpage = findViewById(R.id.endpage);
//        btnAddNew = findViewById(R.id.btnAddNew);
//        btnSort = findViewById(R.id.btnsort);
//        ourdoes = findViewById(R.id.ourdoes);
//        ourdoes.setLayoutManager(new LinearLayoutManager(this));
//        list = new ArrayList<>();
//        labelList = new ArrayList<>();
//        filteredList = new ArrayList<>();
//
//        //google signin
//        signInAccount = GoogleSignIn.getLastSignedInAccount(this);

//        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("NextAction").child(signInAccount.getId().toString());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                list.clear();
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    MyDoes p = dataSnapshot1.getValue(MyDoes.class);
//                    list.add(p);
//                }
//                setAdapter(list);// zrob tak zeby tylko dodawalo 1 a nie od nowa czyscilo liste
//                isFiltered = false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
//            }
//        });

//        //pobieranie labelow MOZE ZROB TYLKO ZEBY RAZ POBIERALO? ? ?
//        referenceLabels = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Labels").child(signInAccount.getId().toString());
//        referenceLabels.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                labelList.clear();
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    Label p = dataSnapshot1.getValue(Label.class);
//                    labelList.add(p);
//                }
//                setAdapter(list); // tu dodalem bo tak to wczytuje mi puste labele
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
//            }
//        });


//    }

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
                            Toast.makeText(getContext(), String.valueOf(isFiltered), Toast.LENGTH_SHORT).show();
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
                            map.put("priority", deletedTask.getPriority());
                            reference.child("Does" + deletedTask.getId()).updateChildren(map);
                        }
                    }).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorDeleteTask))
//                    .addBackgroundColor(ContextCompat.getColor(KontenerActivity.this, R.color.colorPrimary))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public void setAdapter(ArrayList<MyDoes> list) {
        doesAdapter = new DoesAdapter(getContext(), list, this, labelList);
        ourdoes.setAdapter(doesAdapter); // wypelni wszystkie pola ViewHolderami
        doesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNoteClick(int position) {
        final MyDoes myDoes;
        if (isFiltered) {
            myDoes = filteredList.get(position);
            isFiltered = false;
        } else myDoes = list.get(position);

        Intent intent = new Intent(getActivity(), EditDoesActivity.class);
        intent.putExtra("title", myDoes.getTitledoes());
        intent.putExtra("desc", myDoes.getDescdoes());
        intent.putExtra("date", myDoes.getDatedoes());
        intent.putExtra("id", myDoes.getId());
        intent.putExtra("labelName", myDoes.getLabelName());
        intent.putExtra("priority", myDoes.getPriority());
        startActivity(intent);
        Toast.makeText(getContext(), "id" + myDoes.getId(), Toast.LENGTH_SHORT).show();
    }

    // menu w prawym gornym do filtrowania
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.settings_menu, menu);
//        return true;
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
    public void onPrepareOptionsMenu(Menu menu) {
        // albo ondatachange albo po prostu pobrac wszystkie labely ????
        getActivity().invalidateOptionsMenu();
        int i = 0;
        for (Label item : labelList) {
//            menu.add(0,i, Menu.NONE,item.getTitledoes());
            menu.getItem(1).getSubMenu().add(0, i, Menu.NONE, item.getName());
            i++;
        }
        super.onPrepareOptionsMenu(menu);


    }

    //trzeba zmienic tez on click zeby bralo te liste przefiltrowana jak sie kliknie
    // jak zatwierdze filtry to to sie wywola
    @Override
    public void applyFilterData(boolean priorityLow, boolean priorityMedium, boolean priorityHigh, String label) {
        ArrayList<MyDoes> fList = new ArrayList<>();
        ArrayList<MyDoes> fList2 = new ArrayList<>();
        for (MyDoes task : list) {
            if (task.getPriority().equals("1") && priorityHigh && task.getLabelName().equals(label)) {
                fList.add(task);
            } else if (task.getPriority().equals("2") && priorityMedium && task.getLabelName().equals(label)) {
                fList.add(task);
            } else if (task.getPriority().equals("3") && priorityLow && task.getLabelName().equals(label)) {
                fList.add(task);
            }
        }
        if (!priorityHigh && !priorityLow && !priorityMedium) {
            for (MyDoes task2 : list) {
                if (task2.getLabelName().equals(label)) fList2.add(task2);
            }
            setAdapter(fList2);
            filteredList = fList2;
        } else {
            setAdapter(fList);
            filteredList =fList;
        }

        isFiltered = true;
    }
}
