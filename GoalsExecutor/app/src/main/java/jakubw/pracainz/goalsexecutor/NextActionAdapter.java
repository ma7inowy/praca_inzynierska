package jakubw.pracainz.goalsexecutor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
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
        holder.nextActionItemTitle.setText(nextActionList.get(i).getTitle());
        holder.nextActionItemPriority.setText(nextActionList.get(i).getPriority());
        holder.nextActionItemLabel.setBackgroundColor(label1.getColor());
        holder.nextActionItemLabel.setText(label1.getName());
        holder.nextActionItemEstimatedTime.setText(nextActionList.get(i).getEstimatedTime() + "min");


        //setting background of priority
//        holder.nextActionItemPriorityBackground.getBackground().setColorFilter(setPriorityColor(nextActionList.get(i).getPriority()), PorterDuff.Mode.SRC_ATOP);
        GradientDrawable drawable1 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[] { setPriorityColor(nextActionList.get(i).getPriority()), Color.WHITE});
        drawable1.setShape(GradientDrawable.RECTANGLE);
        drawable1.setCornerRadii(new float[] { 60, 60, 60, 60, 60, 60, 60, 60 });

        holder.nextActionItemPriorityBackground.setBackground(drawable1);

//        //setting stroke (ramka)
//        GradientDrawable drawable = (GradientDrawable) holder.nextActionItemPriorityBackground.getBackground();
//        drawable.setStroke(10, setPriorityColor(nextActionList.get(i).getPriority()));
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

        TextView nextActionItemTitle, nextActionItemLabel, nextActionItemPriority, nextActionItemEstimatedTime;
        LinearLayout nextActionItemPriorityBackground;
        OnItemListener onItemListener;

        public MyViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            nextActionItemTitle = (TextView) itemView.findViewById(R.id.nextActionItemTitle);
            nextActionItemLabel = (TextView) itemView.findViewById(R.id.nextActionItemLabel);
            nextActionItemPriority = (TextView) itemView.findViewById(R.id.nextActionItemPriority);
            nextActionItemPriorityBackground = (LinearLayout) itemView.findViewById(R.id.nextActionItemPriorityBackground);
            nextActionItemEstimatedTime = (TextView) itemView.findViewById(R.id.nextActionItemEstimatedTime);
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
