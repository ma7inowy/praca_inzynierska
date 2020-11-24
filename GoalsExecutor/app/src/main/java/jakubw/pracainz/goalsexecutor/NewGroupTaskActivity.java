package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class NewGroupTaskActivity extends AppCompatActivity implements FindColaborantsDialog.FindColaborantsDialogListener {

    EditText addTitle;
    EditText addDescription;
    Button addGroupTaskBtn, addPriorityBtn, addEstimationTimeBtn, addGroupTaskColaborants;
    DatabaseReference reference;
    DatabaseReference reference2;
    Integer idNumber;
    String priority = "3";
    int estimatedTime = 0;
    ArrayList<String> colaborantEmailList = new ArrayList<>();
    ArrayList<User> colaborantList = new ArrayList<>();
    GoogleSignInAccount signInAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group_task);

        addTitle = findViewById(R.id.addGroupTaskTitle);
        addDescription = findViewById(R.id.addGroupTaskDescription);
        addGroupTaskBtn = findViewById(R.id.addGroupTaskBtn);
        addPriorityBtn = findViewById(R.id.addGroupTaskPriorityBtn);
        addEstimationTimeBtn = findViewById(R.id.addGroupTaskEstimationTimeBtn);
        addGroupTaskColaborants = findViewById(R.id.addGroupTaskColaborants);
        idNumber = new Random().nextInt();
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        // do tworzenia zadania z BoxActivity
        Intent intent = getIntent();
        if (intent.hasExtra("title")) {
            addTitle.setText(intent.getStringExtra("title"));
        }


        addGroupTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colaborantEmailList.add(signInAccount.getEmail()); // nwm czy tu czy gdzies indziej lepiej
                reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("GroupTasks").child("Does" + idNumber);
                HashMap map = new HashMap();
                map.put("title", addTitle.getText().toString());
                map.put("description", addDescription.getText().toString());
                map.put("id", idNumber.toString());
                map.put("estimatedTime", estimatedTime);
                map.put("priority", priority);
                map.put("collaborants", colaborantEmailList);
                reference.updateChildren(map);

                sendGroupTaskIdsToCollaborants();


                sendResultToBoxActivity();
                finish();
            }
        });

        addPriorityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrioritiesDialog();
            }
        });
        addEstimationTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEstimationTimeDialog();
            }
        });

        addGroupTaskColaborants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFindColaborantsDialog();
            }
        });

    }

    private void sendGroupTaskIdsToCollaborants() {
//        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("UsersAdditionalInfo").child(signInAccount.getId()).child("GroupTasksId");
//        //MUSIALEM GETEMAIL BO NIE MOGE TEGO ID WZIAC BO NIE WIEM JAKIE ID MAJA UZYTKOWNICY Z LISTY (a MAM TYLKO EMAILE)
//
//        reference.push().setValue(idNumber.toString());




        // pobieranie odpowiednich uzytkownikow ktorych dodano w findcolaborants
        reference2 = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Users");
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                colaborantList.clear();
                for (String userEmail : colaborantEmailList) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        User p = dataSnapshot1.getValue(User.class);
                        if (p.getEmail().equals(userEmail))
                            colaborantList.add(p);
                    }
                }

                if(!colaborantList.isEmpty()){
                    //to wrzuc do additionalifno
                    for(User user: colaborantList) {
                        reference2 = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("UsersAdditionalInfo").child(user.getId()).child("GroupTasksId");
                        reference2.push().setValue(idNumber.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void openFindColaborantsDialog() {
        FindColaborantsDialog findColaborantsDialog = new FindColaborantsDialog();
        if (!colaborantEmailList.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("collaborants", colaborantEmailList);
            findColaborantsDialog.setArguments(bundle);
        }
        findColaborantsDialog.show(getSupportFragmentManager(), "Find Colaborants");
    }

    private void showEstimationTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewGroupTaskActivity.this);
        builder.setTitle("Choose estimated time");

        View view = LayoutInflater.from(NewGroupTaskActivity.this).inflate(R.layout.estimated_time_dialog, null);
        final TextView estimatedTimeProgress = view.findViewById(R.id.estimatedTimeProgress);
        final SeekBar estimatedTimeSeekBar = view.findViewById(R.id.estimatedTimeSeekBar);
        estimatedTimeSeekBar.setMax(240);
        estimatedTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                estimatedTimeProgress.setText("" + progress + " min");
                estimatedTime = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(NewGroupTaskActivity.this, String.valueOf(estimatedTime), Toast.LENGTH_SHORT).show();
                addEstimationTimeBtn.setText("Potrzebny czas: " + estimatedTime + "minut");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(NewGroupTaskActivity.this, priority, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setView(view);
        builder.show();
    }

    // dla usuwania zadania z BoxActiv DODAJ DO BOX ACTIVITY TO INFO O GROUP TASK
    private void sendResultToBoxActivity() {
        Intent i = getIntent();
//        i.putExtra("taskAdded", true);
        i.putExtra("taskAdded", true);
        setResult(RESULT_OK, i);
    }

    private void showPrioritiesDialog() {
        final String[] priorities = {"High", "Medium", "Low"};
        AlertDialog.Builder builder = new AlertDialog.Builder(NewGroupTaskActivity.this);
        builder.setTitle("Choose priority");
        builder.setSingleChoiceItems(priorities, Integer.valueOf(priority) - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                priority = String.valueOf(which + 1);
                Toast.makeText(NewGroupTaskActivity.this, priority, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addPriorityBtn.setText("PRIORYTET: " + priorities[Integer.valueOf(priority) - 1]);

                Toast.makeText(NewGroupTaskActivity.this, priority, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(NewGroupTaskActivity.this, priority, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void applyData(ArrayList<User> collaborants) {
        colaborantEmailList.clear();
//        colaborantList.add(signInAccount.getEmail()); TO DODAJ JUZ W MOMENCIE DODAWANIA ZADANIA
        String textt = "Colaborants (" + collaborants.size() + ")";
        addGroupTaskColaborants.setText(textt);

        for (User user : collaborants) {
            colaborantEmailList.add(user.getEmail());
        }
        //przepisuje obiekty z Array<User> do Array<String>, zeby mozna bylo
        //pozniej z activity przekazac do dialogu (bo nie powinno sie wlasnych obiektow tym przekazywac)
    }
}
