package jakubw.pracainz.goalsexecutor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import jakubw.pracainz.goalsexecutor.Model.Someday;

public class SomedayAdapter extends RecyclerView.Adapter<SomedayAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Someday> somedayList;
    private OnItemListener onItemListener;

    public SomedayAdapter(Context context, ArrayList<Someday> somedayList, OnItemListener onItemListener) {
        this.context = context;
        this.somedayList = somedayList;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_someday, parent, false), onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.somedayTitle.setText(somedayList.get(i).getTitle());
    }

    @Override
    public int getItemCount() {
        return somedayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView somedayTitle;
        OnItemListener onItemListener;

        public MyViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            somedayTitle = (TextView) itemView.findViewById(R.id.somedayTitleItem);
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
