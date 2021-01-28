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

import jakubw.pracainz.goalsexecutor.Model.CalendarEvent;

class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<CalendarEvent> calendarEventList;
    private OnItemListener onItemListener;

    public CalendarAdapter(Context context, ArrayList<CalendarEvent> calendarEventList, OnItemListener onItemListener) {
        this.context = context;
        this.calendarEventList = calendarEventList;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_calendar_event, parent, false), onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, calendarEventList.get(i).getYear());
        calendar1.set(Calendar.MONTH, calendarEventList.get(i).getMonth());
        calendar1.set(Calendar.DATE, calendarEventList.get(i).getDay()); //bo taki format ze dni od 0
        calendar1.set(Calendar.HOUR_OF_DAY, calendarEventList.get(i).getHour());
        calendar1.set(Calendar.MINUTE, calendarEventList.get(i).getMinute());
        CharSequence dataCharSequence = DateFormat.format("dd MMM yyyy HH:mm", calendar1);

        holder.eventTitle.setText(calendarEventList.get(i).getTitle());
        holder.eventDate.setText(dataCharSequence);
//        holder.eventDuration.setText("CZAS trwania");
    }

    @Override
    public int getItemCount() {
        return calendarEventList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView eventTitle, eventDate;
        OnItemListener onItemListener;

        public MyViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            eventTitle = (TextView) itemView.findViewById(R.id.eventTitle);
            eventDate = (TextView) itemView.findViewById(R.id.eventDate);
//            eventDuration = (TextView) itemView.findViewById(R.id.eventDuration);
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
