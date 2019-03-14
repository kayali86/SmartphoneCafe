package com.kayali_developer.smartphonecafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kayali_developer.smartphonecafe.data.model.Event;
import com.kayali_developer.smartphonecafe.utilities.AppDateUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventsHistoryFragmentAdapter extends RecyclerView.Adapter<EventsHistoryFragmentAdapter.EventsHistoryFragmentViewHolder> {

    private List<Event> mEventsHistory;

    @NonNull
    @Override
    public EventsHistoryFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_event;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new EventsHistoryFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsHistoryFragmentViewHolder holder, int position) {
        Event currentEvent = mEventsHistory.get(position);

        if (currentEvent.getTopic() != null){
            holder.tv_event_topic.setText(currentEvent.getTopic());
        }

        if (currentEvent.getDescription() != null){
            holder.tv_event_description.setText(currentEvent.getDescription());
        }

        if (currentEvent.getOrganizerFullName() != null){
            holder.tv_organizer.setText(currentEvent.getOrganizerFullName());
        }

        String eventDate = null;
        try {
            eventDate = AppDateUtils.longDateToDeFormat(currentEvent.getStartTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (eventDate != null){
            holder.tv_event_date.setText(eventDate);
        }
    }

    @Override
    public int getItemCount() {
        if (mEventsHistory == null) return 0;
        return mEventsHistory.size();
    }

    public void setData(List<Event> allEvents){
        mEventsHistory = allEvents;
        notifyDataSetChanged();
    }

    class EventsHistoryFragmentViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_event_topic;
        private TextView tv_event_description;
        private TextView tv_organizer;
        private TextView tv_event_date;

        EventsHistoryFragmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_event_topic = itemView.findViewById(R.id.tv_event_topic);
            tv_event_description = itemView.findViewById(R.id.tv_event_description);
            tv_organizer = itemView.findViewById(R.id.tv_organizer);
            tv_event_date = itemView.findViewById(R.id.tv_event_date);
        }

    }
}
