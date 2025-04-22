package com.cardoso.shareyourtime.ui.worldclock;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cardoso.shareyourtime.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class WorldClockFragment extends Fragment {

    private RecyclerView recyclerView;
    private WorldClockAdapter adapter;
    private List<TimeZone> timeZones;
    private Timer timer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_world_clock, container, false);

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        timeZones = new ArrayList<>();
        // Agregar la zona horaria local por defecto
        timeZones.add(TimeZone.getDefault());
        
        adapter = new WorldClockAdapter(timeZones);
        recyclerView.setAdapter(adapter);

        // Añadir funcionalidad de deslizar para eliminar
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, 
                                 @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                // No permitir eliminar la zona horaria predeterminada (primera posición)
                if (position == 0) {
                    adapter.notifyItemChanged(0);
                    Toast.makeText(getContext(), R.string.cannot_remove_default_timezone, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Eliminar la zona horaria de la lista
                timeZones.remove(position);
                adapter.notifyItemRemoved(position);
            }
        }).attachToRecyclerView(recyclerView);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(view -> showTimeZonePicker());

        // Configurar el temporizador para actualizar los relojes cada segundo
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                }
            }
        }, 0, 1000);

        return root;
    }

    private void showTimeZonePicker() {
        String[] availableTimeZones = TimeZone.getAvailableIDs();
        List<String> timeZoneNames = new ArrayList<>();
        List<String> timeZoneIds = new ArrayList<>();

        // Zonas horarias principales que queremos mostrar
        String[] mainTimeZones = {
            "America/New_York",    // EST
            "America/Chicago",     // CST
            "America/Denver",      // MST
            "America/Los_Angeles", // PST
            "Europe/London",       // GMT
            "Europe/Paris",        // CET
            "Europe/Moscow",       // MSK
            "Asia/Dubai",         // GST
            "Asia/Shanghai",      // CST
            "Asia/Tokyo",         // JST
            "Australia/Sydney",   // AEST
            "Pacific/Auckland"    // NZST
        };

        // Strings para los nombres de las zonas horarias
        String[] timeZoneDisplayNames = {
            getString(R.string.timezone_est),
            getString(R.string.timezone_cst),
            getString(R.string.timezone_mst),
            getString(R.string.timezone_pst),
            getString(R.string.timezone_gmt),
            getString(R.string.timezone_cet),
            getString(R.string.timezone_msk),
            getString(R.string.timezone_gst),
            getString(R.string.timezone_cst_asia),
            getString(R.string.timezone_jst),
            getString(R.string.timezone_aest),
            getString(R.string.timezone_nzst)
        };

        for (int i = 0; i < mainTimeZones.length; i++) {
            timeZoneNames.add(timeZoneDisplayNames[i]);
            timeZoneIds.add(mainTimeZones[i]);
        }

        String[] items = timeZoneNames.toArray(new String[0]);
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.add_timezone)
                .setItems(items, (dialog, which) -> {
                    String selectedId = timeZoneIds.get(which);
                    TimeZone selectedTimeZone = TimeZone.getTimeZone(selectedId);
                    
                    // Verificar si la zona horaria ya está en la lista
                    boolean exists = false;
                    for (TimeZone tz : timeZones) {
                        if (tz.getID().equals(selectedTimeZone.getID())) {
                            exists = true;
                            break;
                        }
                    }
                    
                    if (!exists) {
                        timeZones.add(selectedTimeZone);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), R.string.timezone_already_added, Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
} 