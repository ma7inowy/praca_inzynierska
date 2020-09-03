package jakubw.pracainz.goalsexecutor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;

public class NewBoxTaskDialog extends AppCompatDialogFragment {
    private EditText addBoxTaskName;
    DatabaseReference reference;
    Integer number;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        number = new Random().nextInt();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_boxtask_dialog, null);
        builder.setView(view).setTitle("Add new box task").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //wyslij dane do bazy
                reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Box").child(signInAccount.getId().toString()).child("Box" + number);
                HashMap map = new HashMap();
                map.put("title", addBoxTaskName.getText().toString());
                map.put("id", number.toString());
                reference.updateChildren(map);
//                Toast.makeText(getContext(), addTitle.getText().toString() + " " + addDescription.getText().toString(), Toast.LENGTH_SHORT).show();
//                finish();
            }
        });

        addBoxTaskName = view.findViewById(R.id.addBoxTaskName);

        return builder.create();
    }

}
