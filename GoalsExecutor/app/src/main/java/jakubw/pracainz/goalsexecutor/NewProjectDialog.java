package jakubw.pracainz.goalsexecutor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;

public class NewProjectDialog extends AppCompatDialogFragment {
    private EditText addProjectName;
    DatabaseReference reference;
    Integer idNumber;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        idNumber = new Random().nextInt();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_project_dialog, null);
        builder.setView(view).setTitle("Add new project").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!addProjectName.getText().toString().matches("")) {
                    reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Projects").child(signInAccount.getId().toString()).child("Project" + idNumber);
                    HashMap map = new HashMap();
                    map.put("title", addProjectName.getText().toString());
                    map.put("id", idNumber.toString());
                    reference.updateChildren(map);
                    Toast.makeText(getContext(), "Done!", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getContext(), "Enter project name!", Toast.LENGTH_SHORT).show();

            }
        });

        addProjectName = view.findViewById(R.id.addProjectName);

        return builder.create();
    }
}
