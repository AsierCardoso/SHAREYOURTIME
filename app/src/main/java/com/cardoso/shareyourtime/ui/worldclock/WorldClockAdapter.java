package com.cardoso.shareyourtime.ui.worldclock;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cardoso.shareyourtime.R;
import com.cardoso.shareyourtime.utils.PreferencesManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class WorldClockAdapter extends RecyclerView.Adapter<WorldClockAdapter.ViewHolder> {

    private final List<TimeZone> timeZones;
    private final SimpleDateFormat timeFormat;
    private final SimpleDateFormat dateFormat;
    private final Locale appLocale;
    private final TimeZone defaultTimeZone;

    public WorldClockAdapter(List<TimeZone> timeZones, Context context) {
        this.timeZones = timeZones;
        this.appLocale = new Locale(PreferencesManager.getInstance(context).getLanguage());
        this.timeFormat = new SimpleDateFormat("HH:mm:ss", appLocale);
        this.dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", appLocale);
        this.defaultTimeZone = timeZones.isEmpty() ? TimeZone.getDefault() : timeZones.get(0);
    }

    private String getTimeDifference(TimeZone timeZone) {
        int diffMillis = timeZone.getOffset(System.currentTimeMillis()) - 
                        defaultTimeZone.getOffset(System.currentTimeMillis());
        int diffHours = diffMillis / (60 * 60 * 1000);
        if (diffHours == 0) {
            return "";
        }
        return String.format(appLocale, "(%+d)", diffHours);
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
        TimeZone timeZone = timeZones.get(position);
        Date now = new Date();

        timeFormat.setTimeZone(timeZone);
        dateFormat.setTimeZone(timeZone);

        String timeDiff = getTimeDifference(timeZone);
        String timeZoneName = timeZone.getDisplayName(appLocale);
        holder.timeZoneName.setText(timeDiff.isEmpty() ? timeZoneName : timeZoneName + " " + timeDiff);
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