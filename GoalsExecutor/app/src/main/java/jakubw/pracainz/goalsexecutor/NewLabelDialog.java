package jakubw.pracainz.goalsexecutor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

import yuku.ambilwarna.AmbilWarnaDialog;

public class NewLabelDialog extends AppCompatDialogFragment {

    private EditText addLabelName;
    private Button addLabelColor2;
    DatabaseReference reference;
    Integer number;
    int colorLabel = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        number = new Random().nextInt();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_label_dialog, null);
        builder.setView(view).setTitle("Add own label").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //wyslij dane do bazy
                reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Labels").child(signInAccount.getId()).child("Label" + number);
                HashMap map = new HashMap();
                map.put("name", addLabelName.getText().toString());
//                map.put("color", addLabelColor.getText().toString());
                map.put("color", colorLabel);
                map.put("id", number.toString());
                reference.updateChildren(map);
//                Toast.makeText(getContext(), addTitle.getText().toString() + " " + addDescription.getText().toString(), Toast.LENGTH_SHORT).show();
//                finish();
            }
        });

        addLabelName = view.findViewById(R.id.addLabelName);
        addLabelColor2 = view.findViewById(R.id.addLabelColor2);
        addLabelColor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        return builder.create();
    }

    private void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(getContext(), android.R.color.background_dark, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                colorLabel = color;
            }
        });

        colorPicker.show();
    }
}
