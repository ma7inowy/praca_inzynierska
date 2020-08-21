package jakubw.pracainz.goalsexecutor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LabelsAdapter extends RecyclerView.Adapter<LabelsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Label> labels;
    private OnNoteListener onNoteListener;

    public LabelsAdapter(Context context, ArrayList<Label> labels, OnNoteListener onNoteListener) {
        this.context = context;
        this.labels = labels;
        this.onNoteListener = onNoteListener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_label, parent, false), onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {
        holder.labeltitle.setText(labels.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView labeltitle;
        OnNoteListener onNoteListener;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            labeltitle = (TextView) itemView.findViewById(R.id.labeltitle);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}
