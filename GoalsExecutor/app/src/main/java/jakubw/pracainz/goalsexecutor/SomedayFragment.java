package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import jakubw.pracainz.goalsexecutor.Model.Project;
import jakubw.pracainz.goalsexecutor.Model.Someday;

public class SomedayFragment extends Fragment implements SomedayAdapter.OnItemListener{

    RecyclerView recyclerSomeday;
    FloatingActionButton addNewSomedayBtn;
    DatabaseReference reference;
    ArrayList<Someday> somedayList;
    GoogleSignInAccount signInAccount;
    SomedayAdapter somedayAdapter;
    Integer idNumber;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_someday, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerSomeday = getView().findViewById(R.id.recyclerSomeday);
        addNewSomedayBtn = getView().findViewById(R.id.addNewSomedayBtn);
        somedayList = new ArrayList<>();
        idNumber = new Random().nextInt();
        recyclerSomeday.setLayoutManager(new LinearLayoutManager(getActivity()));
        signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Someday").child(signInAccount.getId().toString());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                somedayList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Someday p = dataSnapshot1.getValue(Someday.class);
                    somedayList.add(p);
                    Log.e("someday", p.getTitle());
                }
                setAdapter(somedayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        addNewSomedayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToAddNewSomedayTask();
                Toast.makeText(getContext(), "New someday", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openDialogToAddNewSomedayTask() {
        NewSomedayDialog newSomedayDialog = new NewSomedayDialog();
        newSomedayDialog.show(getFragmentManager(), "Example");
    }

    private void setAdapter(ArrayList<Someday> somedayList) {
        somedayAdapter = new SomedayAdapter(getActivity(), somedayList, this);
        recyclerSomeday.setAdapter(somedayAdapter); // wypelni wszystkie pola ViewHolderami
        somedayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        final Someday someday = somedayList.get(position);
        Toast.makeText(getContext(), "id" + someday.getId(), Toast.LENGTH_SHORT).show();
    }
}
