package com.cardoso.shareyourtime.ui.stopwatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cardoso.shareyourtime.R;
import java.util.ArrayList;
import java.util.List;

public class StopwatchAdapter extends RecyclerView.Adapter<StopwatchAdapter.LapViewHolder> {
    private final List<String> laps = new ArrayList<>();

    public void addLap(String lapTime) {
        laps.add(0, lapTime); // Agregar al principio para mostrar la última vuelta arriba
        notifyItemInserted(0);
    }

    public void clearLaps() {
        int size = laps.size();
        laps.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public LapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_lap, parent, false);
        return new LapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LapViewHolder holder, int position) {
        holder.bind(position + 1, laps.get(position));
    }

    @Override
    public int getItemCount() {
        return laps.size();
    }

    static class LapViewHolder extends RecyclerView.ViewHolder {
        private final TextView lapNumberText;
        private final TextView lapTimeText;

        public LapViewHolder(@NonNull View itemView) {
            super(itemView);
            lapNumberText = itemView.findViewById(R.id.lap_number);
            lapTimeText = itemView.findViewById(R.id.lap_time);
        }

        public void bind(int lapNumber, String lapTime) {
            lapNumberText.setText(String.format("Vuelta %d", lapNumber));
            lapTimeText.setText(lapTime);
        }
    }
} 
