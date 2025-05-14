package com.cardoso.shareyourtime.ui.worldclock;

import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cardoso.shareyourtime.R;
import com.cardoso.shareyourtime.data.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorldClockAdapter extends RecyclerView.Adapter<WorldClockAdapter.ViewHolder> {

    private List<com.cardoso.shareyourtime.data.TimeZone> timeZones;
    private final SimpleDateFormat timeFormat;
    private final SimpleDateFormat dateFormat;

    public WorldClockAdapter(List<com.cardoso.shareyourtime.data.TimeZone> timeZones) {
        this.timeZones = timeZones;
        this.timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        this.dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
    }

    public void setTimeZones(List<com.cardoso.shareyourtime.data.TimeZone> timeZones) {
        this.timeZones = timeZones;
    }

    public List<com.cardoso.shareyourtime.data.TimeZone> getTimeZones() {
        return timeZones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_world_clock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        com.cardoso.shareyourtime.data.TimeZone timeZone = timeZones.get(position);
        java.util.TimeZone javaTimeZone = java.util.TimeZone.getTimeZone(timeZone.getName());
        
        Date now = new Date();
        timeFormat.setTimeZone(javaTimeZone);
        dateFormat.setTimeZone(javaTimeZone);
        
        holder.timeZoneName.setText(timeZone.getName());
        holder.currentTime.setText(timeFormat.format(now));
        holder.currentDate.setText(dateFormat.format(now));

        holder.itemView.setOnLongClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
            if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                v.getContext().startActivity(intent);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return timeZones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView timeZoneName;
        public final TextView currentTime;
        public final TextView currentDate;

        public ViewHolder(View view) {
            super(view);
            timeZoneName = view.findViewById(R.id.timezone_name);
            currentTime = view.findViewById(R.id.current_time);
            currentDate = view.findViewById(R.id.current_date);
        }
    }
} 