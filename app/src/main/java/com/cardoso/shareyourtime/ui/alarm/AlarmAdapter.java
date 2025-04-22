package com.cardoso.shareyourtime.ui.alarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.cardoso.shareyourtime.R;
import com.cardoso.shareyourtime.data.Alarm;
import java.util.Locale;

public class AlarmAdapter extends ListAdapter<Alarm, AlarmAdapter.AlarmViewHolder> {
    private final OnAlarmClickListener listener;

    public interface OnAlarmClickListener {
        void onAlarmClick(Alarm alarm);
    }

    protected AlarmAdapter(OnAlarmClickListener listener) {
        super(new DiffUtil.ItemCallback<Alarm>() {
            @Override
            public boolean areItemsTheSame(@NonNull Alarm oldItem, @NonNull Alarm newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Alarm oldItem, @NonNull Alarm newItem) {
                return oldItem.getHour() == newItem.getHour() &&
                       oldItem.getMinute() == newItem.getMinute() &&
                       oldItem.isEnabled() == newItem.isEnabled();
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = getItem(position);
        holder.bind(alarm, listener);
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder {
        private final TextView timeText;
        private final TextView labelText;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.alarm_time);
            labelText = itemView.findViewById(R.id.alarm_label);
        }

        public void bind(Alarm alarm, OnAlarmClickListener listener) {
            String time = String.format(Locale.getDefault(), "%02d:%02d", 
                alarm.getHour(), alarm.getMinute());
            timeText.setText(time);
            labelText.setText(alarm.getLabel());
            
            itemView.setOnClickListener(v -> listener.onAlarmClick(alarm));
        }
    }
} 