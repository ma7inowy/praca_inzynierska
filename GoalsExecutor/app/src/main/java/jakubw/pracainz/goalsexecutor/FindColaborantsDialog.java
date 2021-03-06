package jakubw.pracainz.goalsexecutor;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import java.util.Random;

import jakubw.pracainz.goalsexecutor.Model.User;


public class FindColaborantsDialog extends AppCompatDialogFragment implements UserAdapter.OnItemListener {

    private EditText addColaborantText;
    private Button addColaborantBtn;
    DatabaseReference reference;
    Integer idNumber;
    RecyclerView recyclerAllUsers;
    RecyclerView recyclerAddedUsers;
    ArrayList<User> userList = new ArrayList<>();
    ArrayList<User> addedUserList = new ArrayList<>();
    UserAdapter allUserAdapter;
    UserAdapter addedUserAdapter;
    private FindColaborantsDialogListener findColaborantsListener;
    GoogleSignInAccount signInAccount;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
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

                findColaborantsListener.applyData(addedUserList);


            }
        });
        addColaborantText = view.findViewById(R.id.addColaborantText);
        addColaborantBtn = view.findViewById(R.id.addColaborantBtn);
        recyclerAllUsers = view.findViewById(R.id.recyclerAllUsers);
        recyclerAddedUsers = view.findViewById(R.id.recyclerAddedUsers);
        recyclerAllUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAddedUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        readDataFromNewGroupTaskActivity();
        addColaborantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseUserSearch(addColaborantText.getText().toString());
            }
        });

        return builder.create();
    }

    private void readDataFromNewGroupTaskActivity() {
        ArrayList<String> userArrayList1 = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("collaborants")) {
            userArrayList1 = bundle.getStringArrayList("collaborants");

            for (String string : userArrayList1) {
                User user = new User();
                user.setEmail(string);
                addedUserList.add(user);
            }

            setAddedUserAdapter(addedUserList);
        }


    }

    private void firebaseUserSearch(String name) {
        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Users");

//        Query ref = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Users1").orderByChild("name").startAt(name).endAt(name + "\uf8ff");
        Query query = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Users").orderByChild("email").startAt(name).endAt(name + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User p = dataSnapshot1.getValue(User.class);
                    if (!p.getEmail().equals(signInAccount.getEmail()))
                        userList.add(p);
                }
                setAllUserAdapter(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAddedUserAdapter(ArrayList<User> userList) {
        addedUserAdapter = new UserAdapter(getActivity(), userList, this, false);
        recyclerAddedUsers.setAdapter(addedUserAdapter);
        addedUserAdapter.notifyDataSetChanged();
    }

    private void setAllUserAdapter(ArrayList<User> userList) {
        allUserAdapter = new UserAdapter(getActivity(), userList, this, true);
        recyclerAllUsers.setAdapter(allUserAdapter);
        allUserAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position, boolean allUsers) {
        boolean theSame = false;

        if (allUsers) {
//            Toast.makeText(getActivity(), "siemaALL", Toast.LENGTH_SHORT).show();
            for (User user : addedUserList) {
                if (user.getEmail().equals(userList.get(position).getEmail())) {
                    theSame = true;
                    break;
                }
            }
            if (!theSame) {
                addedUserList.add(userList.get(position));
                setAddedUserAdapter(addedUserList);
            }


//            if (!addedUserList.contains(userList.get(position))) {
//                addedUserList.add(userList.get(position));
//                setAddedUserAdapter(addedUserList);
//            }

        } else {
//            Toast.makeText(getActivity(), "siemaKoledzy", Toast.LENGTH_SHORT).show();
            addedUserList.remove(position);
            setAddedUserAdapter(addedUserList);
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            findColaborantsListener = (FindColaborantsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement FindColaborantsDialogListener");
        }
    }

    //yt
    public interface FindColaborantsDialogListener {
        void applyData(ArrayList<User> collaborants);
    }
}
