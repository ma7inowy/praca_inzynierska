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

public class NextActionAdapter extends RecyclerView.Adapter<NextActionAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<NextAction> nextActionList;
    private ArrayList<Label> labelList;
    private OnItemListener onItemListener;

    public NextActionAdapter(Context context, ArrayList<NextAction> nextActionList, OnItemListener onItemListener, ArrayList<Label> labelList) {
        this.context = context;
        this.nextActionList = nextActionList;
        this.onItemListener = onItemListener;
        this.labelList = labelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_kontener, parent, false), onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {
        Label label1 = new Label("error", 0, "1");
        for (Label label : labelList) {
            if (label.getName().equals(nextActionList.get(i).getLabelName())) {
                label1 = label;
            }
        }
        holder.nextActionTitle.setText(nextActionList.get(i).getTitle());
        holder.nextActionPriority.setText(nextActionList.get(i).getPriority());
        holder.nextActionLabel.setBackgroundColor(label1.getColor());
        holder.nextActionLabel.setText(label1.getName());
        holder.priorityBackground.setBackgroundColor(setPriorityColor(nextActionList.get(i).getPriority()));
    }

    private int setPriorityColor(String priority) {
        if (priority.equals("1")) return ContextCompat.getColor(context, R.color.red);
        else if (priority.equals("2")) return ContextCompat.getColor(context, R.color.yellow);
        else return ContextCompat.getColor(context, R.color.green);
    }

    @Override
    public int getItemCount() {
        return nextActionList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nextActionTitle, nextActionLabel, nextActionPriority;
        LinearLayout priorityBackground;
        OnItemListener onItemListener;

        public MyViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            nextActionTitle = (TextView) itemView.findViewById(R.id.itemNextActionTitle);
            nextActionLabel = (TextView) itemView.findViewById(R.id.labeldoes);
            nextActionPriority = (TextView) itemView.findViewById(R.id.prioritydoes);
            priorityBackground = (LinearLayout) itemView.findViewById(R.id.priorityBackground);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
