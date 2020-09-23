package jakubw.pracainz.goalsexecutor;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TaskFilterDialog extends AppCompatDialogFragment {
    RadioButton radioLowPrioBtn;
    RadioButton radioHighPrioBtn;
    RadioButton radioMediumPrioBtn;
    Spinner filterLabelSpinner;
    DatabaseReference referenceLabel;
    ArrayList<Label> labelList;
    ArrayAdapter<Label> labelAdapter;
    String labelName = "labelname";
    private TaskFilterDialogListener filterDialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.task_filter_dialog, null);
        builder.setView(view).setTitle("Set filters").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean priorityLow = radioLowPrioBtn.isChecked();
                boolean priorityMedium = radioMediumPrioBtn.isChecked();
                boolean priorityHigh = radioHighPrioBtn.isChecked();
                String label = labelName;
                filterDialogListener.applyFilterData(priorityLow, priorityMedium, priorityHigh, label);
            }
        });
        radioLowPrioBtn = view.findViewById(R.id.radioLowPrioBtn);
        radioHighPrioBtn = view.findViewById(R.id.radioHighPrioBtn);
        radioMediumPrioBtn = view.findViewById(R.id.radioMediumPrioBtn);
        filterLabelSpinner = view.findViewById(R.id.filterLabelSpinner);
        labelList = new ArrayList<>();
        //google signin
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

        //pobranie labelow z bazy MOZE ZNALEZC JAKIS LEPSZY SPOSOB?
        referenceLabel = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Labels").child(signInAccount.getId().toString());
        referenceLabel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                labelList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Label p = dataSnapshot1.getValue(Label.class);
                    labelList.add(p);
                }
                //bo cos krzyczy ze na null pointer jak chce dodac nowa etykiete w labels
                if (getActivity() != null) {
                    setLabelAdapter(labelList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        filterLabelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Label label = (Label) parent.getSelectedItem();
                labelName = label.getName();
                Toast.makeText(parent.getContext(), labelName, Toast.LENGTH_LONG).show();
                Log.e("labelName", "wcisniete");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return builder.create();
    }

    //dla dialogfragmentu
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            filterDialogListener = (TaskFilterDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement TaskFilterDialogListener");
        }
    }

    public interface TaskFilterDialogListener {
        void applyFilterData(boolean priorityLow, boolean priorityMedium, boolean priorityHigh, String label);
    }

    private void setLabelAdapter(ArrayList<Label> list) {
        //adding labels to spinner
        labelAdapter = new ArrayAdapter<Label>(getActivity(), android.R.layout.simple_spinner_item, list);
        labelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterLabelSpinner.setAdapter(labelAdapter);
        labelAdapter.notifyDataSetChanged();
    }
}
