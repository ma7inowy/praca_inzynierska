package jakubw.pracainz.goalsexecutor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class FindColaborantsDialog extends AppCompatDialogFragment implements UserAdapter.OnItemListener {

    private EditText addColaborantText;
    private Button addColaborantBtn;
    DatabaseReference reference;
    Integer idNumber;
    RecyclerView recyclerAddColaborants;
    ArrayList<User> userList = new ArrayList<>();
    UserAdapter userAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        idNumber = new Random().nextInt();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_colaborants_dialog, null);
        builder.setView(view).setTitle("Find colaborants").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //wyslij dane do bazy
//                reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Users");
//                HashMap map = new HashMap();
//                map.put("name", addLabelName.getText().toString());
//                map.put("color", labelColor);
//                map.put("id", idNumber.toString());
//                reference.updateChildren(map);

            }
        });

        addColaborantText = view.findViewById(R.id.addColaborantText);
        addColaborantBtn = view.findViewById(R.id.addColaborantBtn);
        recyclerAddColaborants = view.findViewById(R.id.recyclerAddColaborants);
        recyclerAddColaborants.setLayoutManager(new LinearLayoutManager(getActivity()));
        addColaborantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseUserSearch(addColaborantText.getText().toString());
            }
        });

        return builder.create();
    }

    private void firebaseUserSearch(String name) {
//        https://www.youtube.com/watch?v=b_tz8kbFUsU&list=LL&index=2&ab_channel=TVACStudio
        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Users1");

//        Query ref = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Users1").orderByChild("name").startAt(name).endAt(name + "\uf8ff");
        Query query = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Users1").orderByChild("name").startAt(name).endAt(name + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User p = dataSnapshot1.getValue(User.class);
                    userList.add(p);
                }
                setAdapter(userList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAdapter(ArrayList<User> userList) {
        userAdapter = new UserAdapter(getActivity(), userList,this);
        recyclerAddColaborants.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
    }


}
