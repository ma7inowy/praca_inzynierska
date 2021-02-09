package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
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

import jakubw.pracainz.goalsexecutor.Model.User;

public class NewAndEditGroupTaskActivity extends AppCompatActivity implements FindColaborantsDialog.FindColaborantsDialogListener {

    EditText addTitle;
    EditText addDescription;
    Button addGroupTaskBtn, addPriorityBtn, addEstimationTimeBtn, addGroupTaskColaborants;
    DatabaseReference reference;
    DatabaseReference reference2;
    DatabaseReference reference3;
    Integer idNumber;
    String priority = "3";
    int estimatedTime = 0;
    ArrayList<String> colaborantEmailList = new ArrayList<>();
    ArrayList<User> colaborantList = new ArrayList<>();
    GoogleSignInAccount signInAccount;
    final String[] priorities = {"High", "Medium", "Low"};
    boolean editing = false;
    TextView titlepageGroup1;

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
        titlepageGroup1 = findViewById(R.id.titlepageGroup1);
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        // do tworzenia zadania z BoxFragment
        Intent intent = getIntent();
        if (intent.hasExtra("title")) {
            addTitle.setText(intent.getStringExtra("title"));
        }

        if (intent.hasExtra("description")) {
            addDescription.setText(intent.getStringExtra("description"));
        }

        if (intent.hasExtra("estimatedTime")) {
            estimatedTime = intent.getIntExtra("estimatedTime", 0);
            addEstimationTimeBtn.setText("ESTIMATED TIME: " + estimatedTime + " MIN");
        }

        if (intent.hasExtra("id")) {
            idNumber = Integer.valueOf(intent.getStringExtra("id"));
//            Toast.makeText(this, idNumber.toString(), Toast.LENGTH_SHORT).show();
        } else idNumber = new Random().nextInt();

        if (intent.hasExtra("priority")) {
            priority = intent.getStringExtra("priority");
            setPriorityButtonBackground();
            addPriorityBtn.setText("PRIORITY: " + priorities[Integer.valueOf(priority) - 1]);
        }

        if (intent.hasExtra("collaborants")) {
            colaborantEmailList = intent.getStringArrayListExtra("collaborants");
            String textt = "Colaborants (" + colaborantEmailList.size() + ")";
            addGroupTaskColaborants.setText(textt);
        }

        if (intent.hasExtra("edit")) {
            editing = true;
            setWidgetsForEditing();
        }

        addGroupTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addTitle.getText().toString().matches("")) {
                    if (!colaborantEmailList.contains(signInAccount.getEmail()))
                        colaborantEmailList.add(signInAccount.getEmail());

                    reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("GroupTasks").child("Group" + idNumber);
                    HashMap map = new HashMap();
                    map.put("title", addTitle.getText().toString());
                    map.put("description", addDescription.getText().toString());
                    map.put("id", idNumber.toString());
                    map.put("estimatedTime", estimatedTime);
                    map.put("priority", priority);
                    map.put("collaborants", colaborantEmailList);
                    reference.updateChildren(map);
                    if (!editing)
                        getCollaborantsProfiles();

                    sendResultToBoxActivity();
                    Toast.makeText(NewAndEditGroupTaskActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                    finish();
                } else
                    Toast.makeText(NewAndEditGroupTaskActivity.this, "Enter task name!", Toast.LENGTH_SHORT).show();
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
                if (!editing)
                    openFindColaborantsDialog();
                else
                    Toast.makeText(NewAndEditGroupTaskActivity.this, colaborantEmailList.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setWidgetsForEditing() {
        titlepageGroup1.setText("Edit Group Task");
        addGroupTaskBtn.setText("Save");
    }

    private void getCollaborantsProfiles() {
//        reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("UsersAdditionalInfo").child(signInAccount.getId()).child("GroupTasksId");
//        //MUSIALEM GETEMAIL BO NIE MOGE TEGO ID WZIAC BO NIE WIEM JAKIE ID MAJA UZYTKOWNICY Z LISTY (a MAM TYLKO EMAILE)

        // pobieranie odpowiednich uzytkownikow ktorych dodano w findcolaborants
        reference2 = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Users");
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                colaborantList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User p = dataSnapshot1.getValue(User.class);
                    assert p != null;
                    // jesli ko
                    if (colaborantEmailList.contains(p.getEmail())) {
                        sendGroupTaskIdsToCollaborants(p);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendGroupTaskIdsToCollaborants(User p) {

        //to wrzuc do additionalifno id zadania kazdemu uzytkownikowi ktory jest do niego przypisany
        // sprawdzenie czy uzytkownik juz ma przypisane to zadanie zeby nie robic tego 2  razy
        reference3 = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("UsersGroups").child(p.getId()).child("GroupTasksId");
        reference3.push().setValue(idNumber.toString());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(NewAndEditGroupTaskActivity.this);
        builder.setTitle("Choose estimated time");

        View view = LayoutInflater.from(NewAndEditGroupTaskActivity.this).inflate(R.layout.estimated_time_dialog, null);
        final TextView estimatedTimeProgress = view.findViewById(R.id.estimatedTimeProgress);
        final SeekBar estimatedTimeSeekBar = view.findViewById(R.id.estimatedTimeSeekBar);
        estimatedTimeSeekBar.setMax(240);
        estimatedTimeSeekBar.setProgress(estimatedTime);
        estimatedTimeProgress.setText(estimatedTime + " MIN");
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
//                Toast.makeText(NewAndEditGroupTaskActivity.this, String.valueOf(estimatedTime), Toast.LENGTH_SHORT).show();
                addEstimationTimeBtn.setText("ESTIMATED TIME: " + estimatedTime + " MIN");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(NewAndEditGroupTaskActivity.this, priority, Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(NewAndEditGroupTaskActivity.this);
        builder.setTitle("Choose priority");
        builder.setSingleChoiceItems(priorities, Integer.valueOf(priority) - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                priority = String.valueOf(which + 1);
//                Toast.makeText(NewAndEditGroupTaskActivity.this, priority, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addPriorityBtn.setText("PRIORITY: " + priorities[Integer.valueOf(priority) - 1]);
                setPriorityButtonBackground();

//                Toast.makeText(NewAndEditGroupTaskActivity.this, priority, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(NewAndEditGroupTaskActivity.this, priority, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void setPriorityButtonBackground() {
        int color = setColorForPriorityButton(priority);
        PaintDrawable pd = new PaintDrawable(color);
        pd.setCornerRadius(70);
        addPriorityBtn.setBackground(pd);
    }

    private int setColorForPriorityButton(String priority) {
        if (priority.equals(String.valueOf(1)))
            return Color.RED;
        else if (priority.equals(String.valueOf(2)))
            return Color.YELLOW;
        else if (priority.equals(String.valueOf(3)))
            return Color.GREEN;
        else {
            Toast.makeText(this, "error!", Toast.LENGTH_SHORT).show();
            return 0;
        }
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
