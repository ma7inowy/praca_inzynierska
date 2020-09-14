package jakubw.pracainz.goalsexecutor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DoesAdapter extends RecyclerView.Adapter<DoesAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<MyDoes> myDoes;
    private ArrayList<Label> labels;
    private OnNoteListener onNoteListener;

    public DoesAdapter(Context context, ArrayList<MyDoes> myDoes, OnNoteListener onNoteListener, ArrayList<Label> labels) {
        this.context = context;
        this.myDoes = myDoes;
        this.onNoteListener = onNoteListener;
        this.labels = labels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_kontener, parent, false), onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {
        Label label1 = new Label("error", 0, "69"); // bo jak blad to pokaze ladnie
        for (Label label : labels) {
            if (label.getName().equals(myDoes.get(i).getLabelName())) {
                label1 = label;
            }
        }
        holder.titledoes.setText(myDoes.get(i).getTitledoes());
        holder.prioritydoes.setText(myDoes.get(i).getPriority());
        holder.labeldoes.setBackgroundColor(label1.getColor());
        holder.labeldoes.setText(label1.getName());
        holder.priorityBackground.setBackgroundColor(setPriorityColor(myDoes.get(i).getPriority()));

        //czy przekazac do doesadapter jeszcze liste wszystkich labelow?? i wtedy getbyid czy wsadzac tam obiekt caly?
//        holder.labeldoes.setText(myDoes.get(i).getLabel().getName());
//        holder.labeldoes.setBackgroundColor(myDoes.get(i).getLabel().getColor());

    }

    private int setPriorityColor(String priority) {
        if (priority.equals("1")) return ContextCompat.getColor(context, R.color.red);
        else if (priority.equals("2")) return ContextCompat.getColor(context, R.color.yellow);
        else return ContextCompat.getColor(context, R.color.green);
    }

    @Override
    public int getItemCount() {
        return myDoes.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titledoes, labeldoes, prioritydoes;
        LinearLayout priorityBackground;
        OnNoteListener onNoteListener;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            titledoes = (TextView) itemView.findViewById(R.id.titledoes);
            labeldoes = (TextView) itemView.findViewById(R.id.labeldoes);
            prioritydoes = (TextView) itemView.findViewById(R.id.prioritydoes);
            priorityBackground = (LinearLayout) itemView.findViewById(R.id.priorityBackground);
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
