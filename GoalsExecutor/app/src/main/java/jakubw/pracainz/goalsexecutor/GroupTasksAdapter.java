package jakubw.pracainz.goalsexecutor;

import android.content.Context;
import android.graphics.Color;
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

import jakubw.pracainz.goalsexecutor.Model.GroupTask;

public class GroupTasksAdapter extends RecyclerView.Adapter<GroupTasksAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<GroupTask> groupTaskList;
    private OnItemListener onItemListener;

    public GroupTasksAdapter(Context context, ArrayList<GroupTask> groupTaskList, OnItemListener onItemListener) {
        this.context = context;
        this.groupTaskList = groupTaskList;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_group_task, parent, false), onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupTasksAdapter.MyViewHolder holder, int i) {

        holder.groupTaskItemTitle.setText(groupTaskList.get(i).getTitle());
        holder.groupTaskItemPriority.setText(groupTaskList.get(i).getPriority());
        holder.groupTaskItemEstimatedTime.setText(groupTaskList.get(i).getEstimatedTime() + "min");


        //setting background of priority
//        holder.nextActionItemPriorityBackground.getBackground().setColorFilter(setPriorityColor(nextActionList.get(i).getPriority()), PorterDuff.Mode.SRC_ATOP);
        GradientDrawable drawable1 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[] { setPriorityColor(groupTaskList.get(i).getPriority()), Color.WHITE});
        drawable1.setShape(GradientDrawable.RECTANGLE);
        drawable1.setCornerRadii(new float[] { 60, 60, 60, 60, 60, 60, 60, 60 });

        holder.groupTaskItemPriorityBackground.setBackground(drawable1);
    }

    private int setPriorityColor(String priority) {
        if (priority.equals("1")) return ContextCompat.getColor(context, R.color.red);
        else if (priority.equals("2")) return ContextCompat.getColor(context, R.color.yellow);
        else return ContextCompat.getColor(context, R.color.green);
    }

    @Override
    public int getItemCount() {
        return groupTaskList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView groupTaskItemTitle,groupTaskItemPriority,groupTaskItemEstimatedTime;
        LinearLayout groupTaskItemPriorityBackground;
        OnItemListener onItemListener;

        public MyViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            groupTaskItemTitle = (TextView) itemView.findViewById(R.id.groupTaskItemTitle);
            groupTaskItemPriority = (TextView) itemView.findViewById(R.id.groupTaskItemPriority);
            groupTaskItemPriorityBackground = (LinearLayout) itemView.findViewById(R.id.groupTaskItemPriorityBackground);
            groupTaskItemEstimatedTime = (TextView) itemView.findViewById(R.id.groupTaskItemEstimatedTime);
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
