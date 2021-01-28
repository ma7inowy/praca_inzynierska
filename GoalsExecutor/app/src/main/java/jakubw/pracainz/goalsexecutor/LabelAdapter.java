package jakubw.pracainz.goalsexecutor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import jakubw.pracainz.goalsexecutor.Model.Label;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Label> labelList;
    private OnItemListener onItemListener;

    public LabelAdapter(Context context, ArrayList<Label> labelList, OnItemListener onItemListener) {
        this.context = context;
        this.labelList = labelList;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_label, parent, false), onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {
        holder.titleLabel.setText(labelList.get(i).getName());
        holder.linearLayout.setBackgroundColor(labelList.get(i).getColor());
    }

    @Override
    public int getItemCount() {
        return labelList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleLabel;
        LinearLayout linearLayout;
        OnItemListener onItemListener;

        public MyViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            titleLabel = (TextView) itemView.findViewById(R.id.labeltitle);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.labellayout);
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
