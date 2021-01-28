package jakubw.pracainz.goalsexecutor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import jakubw.pracainz.goalsexecutor.Model.BoxTask;

class BoxTaskAdapter extends RecyclerView.Adapter<BoxTaskAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<BoxTask> boxTaskList;
    private OnItemListener onItemListener;

    public BoxTaskAdapter(Context context, ArrayList<BoxTask> boxTaskList, OnItemListener onItemListener) {
        this.context = context;
        this.boxTaskList = boxTaskList;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_box, parent, false), onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.boxTaskTitle.setText(boxTaskList.get(i).getTitle());
    }

    @Override
    public int getItemCount() {
        return boxTaskList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView boxTaskTitle;
        OnItemListener onItemListener;

        public MyViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            boxTaskTitle = (TextView) itemView.findViewById(R.id.boxTaskTitle);
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
