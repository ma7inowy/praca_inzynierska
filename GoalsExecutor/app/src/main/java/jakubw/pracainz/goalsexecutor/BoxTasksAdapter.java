package jakubw.pracainz.goalsexecutor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class BoxTasksAdapter extends RecyclerView.Adapter<BoxTasksAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<BoxTask> boxTasks;
    private OnNoteListener onNoteListener;

    public BoxTasksAdapter(Context context, ArrayList<BoxTask> boxTasks, OnNoteListener onNoteListener) {
        this.context = context;
        this.boxTasks = boxTasks;
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_box, parent, false), onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.boxTaskTitle.setText(boxTasks.get(i).getTitle());
    }

    @Override
    public int getItemCount() {
        return boxTasks.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView boxTaskTitle;
        OnNoteListener onNoteListener;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            boxTaskTitle = (TextView) itemView.findViewById(R.id.boxTaskTitle);
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
