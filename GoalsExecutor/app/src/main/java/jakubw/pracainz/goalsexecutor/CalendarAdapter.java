package jakubw.pracainz.goalsexecutor;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<CalendarEvent> eventList;
    private OnNoteListener onNoteListener;

    public CalendarAdapter(Context context, ArrayList<CalendarEvent> eventList, OnNoteListener onNoteListener) {
        this.context = context;
        this.eventList = eventList;
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_calendar_event, parent, false), onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, eventList.get(i).getYear());
        calendar1.set(Calendar.MONTH, eventList.get(i).getMonth());
        calendar1.set(Calendar.DATE, eventList.get(i).getDay()); //bo taki format ze dni od 0
        calendar1.set(Calendar.HOUR_OF_DAY, eventList.get(i).getHour());
        calendar1.set(Calendar.MINUTE, eventList.get(i).getMinute());
        CharSequence dataCharSequence = DateFormat.format("dd MMM yyyy HH:mm", calendar1);

        holder.eventTitle.setText(eventList.get(i).getTitle());
        holder.eventBeginDate.setText(dataCharSequence);
        holder.eventDuration.setText("CZAS trwania");
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView eventTitle, eventBeginDate, eventDuration;
        OnNoteListener onNoteListener;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            eventTitle = (TextView) itemView.findViewById(R.id.eventTitle);
            eventBeginDate = (TextView) itemView.findViewById(R.id.eventBeginDate);
            eventDuration = (TextView) itemView.findViewById(R.id.eventDuration);
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
