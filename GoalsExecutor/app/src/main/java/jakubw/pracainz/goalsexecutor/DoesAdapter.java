package jakubw.pracainz.goalsexecutor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DoesAdapter extends RecyclerView.Adapter<DoesAdapter.MyViewHolder>{

    Context context;
    ArrayList<MyDoes> myDoes;
    OnNoteListener onNoteListener;

    public DoesAdapter(Context context, ArrayList<MyDoes> myDoes, OnNoteListener onNoteListener) {
        this.context = context;
        this.myDoes = myDoes;
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_kontener, parent, false),onNoteListener);
    }
    // wywoływana przez RecyclerView zeby wyswietlic dane na wybranych pozycjach
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.titledoes.setText(myDoes.get(i).getTitledoes());
//        holder.descdoes.setText(myDoes.get(i).getDescdoes());
        holder.datedoes.setText(myDoes.get(i).getDatedoes());

    }

    @Override
    public int getItemCount() {
        return myDoes.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView titledoes, descdoes, datedoes;
        OnNoteListener onNoteListener;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            titledoes = (TextView) itemView.findViewById(R.id.titledoes);
//            descdoes = (TextView) itemView.findViewById(R.id.descdoes);
            datedoes = (TextView) itemView.findViewById(R.id.datedoes);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
