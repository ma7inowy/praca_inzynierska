package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import jakubw.pracainz.goalsexecutor.Model.Label;
import jakubw.pracainz.goalsexecutor.Model.NextAction;

import static androidx.recyclerview.widget.ItemTouchHelper.*;

public class NextActionFragment extends Fragment implements NextActionAdapter.OnItemListener, TaskFilterDialog.TaskFilterDialogListener {

    TextView titlePage, endPage;
    DatabaseReference reference;
    DatabaseReference referenceLabel;
    RecyclerView recyclerNextAction;
    ArrayList<NextAction> nextActionList;
    ArrayList<NextAction> filteredNextActionList;
    ArrayList<Label> labelList;
    NextActionAdapter nextActionAdapter;
    FloatingActionButton addNextActionBtn;
    FloatingActionButton addFilterBtn;

    GoogleSignInAccount signInAccount;
    boolean isFiltered = false; //gdzie te flage tu czy oncreate?

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_next_action, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();
        titlePage = getView().findViewById(R.id.titlepage);
        endPage = getView().findViewById(R.id.endpage);
        recyclerNextAction = getView().findViewById(R.id.recyclerNextAction);
        recyclerNextAction.setLayoutManager(new LinearLayoutManager(getActivity()));
        addNextActionBtn = getView().findViewById(R.id.addNextActionBtn);
        addFilterBtn = getView().findViewById(R.id.addFilterBtn);
        nextActionList = new ArrayList<>();
        labelList = new ArrayList<>();
        filteredNextActionList = new ArrayList<>();

        //google signin
        signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("NextAction").child(signInAccount.getId().toString());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nextActionList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    NextAction p = dataSnapshot1.getValue(NextAction.class);
                    nextActionList.add(p);
                }
                setAdapter(nextActionList);// zrob tak zeby tylko dodawalo 1 a nie od nowa czyscilo liste
                isFiltered = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        //pobieranie labelow MOZE ZROB TYLKO ZEBY RAZ POBIERAL
        referenceLabel = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Labels").child(signInAccount.getId().toString());
        referenceLabel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                labelList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Label p = dataSnapshot1.getValue(Label.class);
                    labelList.add(p);
                }
                setAdapter(nextActionList); // tu dodalem bo tak to wczytuje mi puste labele
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        addNextActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewNextActionActivity.class);
                startActivity(intent);
            }
        });

        addFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterDialog();
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerNextAction);
    }

    private void openFilterDialog() {
        TaskFilterDialog taskFilterDialog = new TaskFilterDialog();
        taskFilterDialog.setTargetFragment(NextActionFragment.this, 1);
        taskFilterDialog.show(getFragmentManager(), "Filter dialog");
    }

    // swipe left to delete task
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case LEFT:
                    ArrayList<NextAction> listForDelete;
                    final NextAction deletedTask;
                    if (isFiltered) {
                        deletedTask = filteredNextActionList.get(position);
                        listForDelete = filteredNextActionList;
                    } else {
                        deletedTask = nextActionList.get(position);
                        listForDelete = nextActionList;
                    }

                    String id = listForDelete.get(position).getId();
                    reference.child("Na" + id).removeValue();
                    listForDelete.remove(position);
                    setAdapter(listForDelete);
                    Snackbar.make(recyclerNextAction, "Task " + deletedTask.getTitle() + " deleted!", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), String.valueOf(isFiltered), Toast.LENGTH_SHORT).show();
                            if (isFiltered) {
                                filteredNextActionList.add(position, deletedTask);
                                setAdapter(filteredNextActionList); // moze lepiej nasluchiwac na jedna pozycje?
                            } else {
                                nextActionList.add(position, deletedTask);
                                setAdapter(nextActionList);
                            }
                            HashMap map = new HashMap();
                            map.put("title", deletedTask.getTitle());
                            map.put("description", deletedTask.getDescription());
                            map.put("estimatedTime", deletedTask.getEstimatedTime());
                            map.put("id", deletedTask.getId());
                            map.put("labelName", deletedTask.getLabelName());
                            map.put("priority", deletedTask.getPriority());
                            reference.child("Na" + deletedTask.getId()).updateChildren(map);
                        }
                    }).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorDeleteTask))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public void setAdapter(ArrayList<NextAction> list) {
        nextActionAdapter = new NextActionAdapter(getContext(), list, this, labelList);
        recyclerNextAction.setAdapter(nextActionAdapter);
        nextActionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        final NextAction nextAction;
        if (isFiltered) {
            nextAction = filteredNextActionList.get(position);
            isFiltered = false;
        } else nextAction = nextActionList.get(position);

        Intent intent = new Intent(getActivity(), EditNextActionActivity.class);
        intent.putExtra("title", nextAction.getTitle());
        intent.putExtra("description", nextAction.getDescription());
        intent.putExtra("estimatedTime", nextAction.getEstimatedTime());
        intent.putExtra("id", nextAction.getId());
        intent.putExtra("labelName", nextAction.getLabelName());
        intent.putExtra("priority", nextAction.getPriority());
        startActivity(intent);
        Toast.makeText(getContext(), "id" + nextAction.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item2:
                setAdapter(nextActionList);
                isFiltered = false;
                return true;
        }

        //nasluchuje ktore filtrowanie zostalo wcisnelo
        filteredNextActionList = new ArrayList<>();
        if (!item.getTitle().equals("FILTRUJ...")) {
            for (NextAction does : nextActionList) {
                if (item.getTitle().equals(does.getLabelName())) {
                    filteredNextActionList.add(does);
                }
            }
            setAdapter(filteredNextActionList);
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
//            menu.add(0,i, Menu.NONE,item.getTitle());
            menu.getItem(1).getSubMenu().add(0, i, Menu.NONE, item.getName());
            i++;
        }
        super.onPrepareOptionsMenu(menu);


    }

    //trzeba zmienic tez on click zeby bralo te liste przefiltrowana jak sie kliknie
    // jak zatwierdze filtry to to sie wywola
    @Override
    public void applyFilterData(boolean priorityLow, boolean priorityMedium, boolean priorityHigh, String label, String estimatedTime) {
        ArrayList<NextAction> fList = new ArrayList<>();
//        ArrayList<NextAction> fList2 = new ArrayList<>();
        int estimatedTimeInt = Integer.valueOf(estimatedTime);
        for (NextAction task : nextActionList) {
            if (task.getPriority().equals("1") && priorityHigh && task.getLabelName().equals(label) && (task.getEstimatedTime() <= estimatedTimeInt)) {
                fList.add(task);
            } else if (task.getPriority().equals("2") && priorityMedium && task.getLabelName().equals(label) && (task.getEstimatedTime() <= estimatedTimeInt)) {
                fList.add(task);
            } else if (task.getPriority().equals("3") && priorityLow && task.getLabelName().equals(label) && (task.getEstimatedTime() <= estimatedTimeInt)) {
                fList.add(task);
            }
        }
        if (!priorityHigh && !priorityLow && !priorityMedium) {
            for (NextAction task2 : nextActionList) {
                if (task2.getLabelName().equals(label) && (task2.getEstimatedTime() <= estimatedTimeInt))
                    fList.add(task2);
            }
        }

        if(!fList.isEmpty()){
            setAdapter(fList);
            filteredNextActionList = fList;
        } else {
            setAdapter(fList);
            filteredNextActionList = fList;
            Toast.makeText(getContext(), "No tasks match your search!", Toast.LENGTH_SHORT).show();
        }
        isFiltered = true;
    }
}
