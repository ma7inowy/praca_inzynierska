package jakubw.pracainz.goalsexecutor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.HashMap;

public class TaskFilterDialog extends AppCompatDialogFragment {
    RadioButton radioLowPrioBtn;
    RadioButton radioHighPrioBtn;
    RadioButton radioMediumPrioBtn;
    Spinner filterLabelsSpinner;
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
                String label ="label";

                filterDialogListener.applyFilterData(priorityLow,priorityMedium,priorityHigh,label);


            }
        });
        radioLowPrioBtn = view.findViewById(R.id.radioLowPrioBtn);
        radioHighPrioBtn = view.findViewById(R.id.radioHighPrioBtn);
        radioMediumPrioBtn = view.findViewById(R.id.radioMediumPrioBtn);

        return builder.create();
    }
    //dla dialogfragmentu
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            filterDialogListener = (TaskFilterDialogListener)getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement TaskFilterDialogListener");
        }
    }

    public interface TaskFilterDialogListener{
        void applyFilterData(boolean priorityLow,boolean priorityMedium, boolean priorityHigh, String label);
    }
}
