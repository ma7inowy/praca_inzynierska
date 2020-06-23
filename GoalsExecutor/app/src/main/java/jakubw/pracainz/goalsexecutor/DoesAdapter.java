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

    public DoesAdapter(Context context, ArrayList<MyDoes> myDoes) {
        this.context = context;
        this.myDoes = myDoes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_kontener, parent, false));
    }
    // wywoływana przez RecyclerView zeby wyswietlic dane na wybranych pozycjach
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.titledoes.setText(myDoes.get(i).getTitledoes());
        holder.descdoes.setText(myDoes.get(i).getDescdoes());
        holder.datedoes.setText(myDoes.get(i).getDatedoes());
    }

    @Override
    public int getItemCount() {
        return myDoes.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titledoes, descdoes, datedoes;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titledoes = (TextView) itemView.findViewById(R.id.titledoes);
            descdoes = (TextView) itemView.findViewById(R.id.descdoes);
            datedoes = (TextView) itemView.findViewById(R.id.datedoes);
        }
    }
}
