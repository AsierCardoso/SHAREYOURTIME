package com.cardoso.shareyourtime.ui.worldclock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cardoso.shareyourtime.R;
import com.cardoso.shareyourtime.data.TimeZone;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WorldClockFragment extends Fragment {
    private WorldClockViewModel viewModel;
    private WorldClockAdapter adapter;
    private RecyclerView recyclerView;
    private Timer timer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_world_clock, container, false);

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        viewModel = new ViewModelProvider(this).get(WorldClockViewModel.class);
        adapter = new WorldClockAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Observar cambios en las zonas horarias
        viewModel.getSelectedTimeZones().observe(getViewLifecycleOwner(), timeZones -> {
            adapter.setTimeZones(timeZones);
            adapter.notifyDataSetChanged();
        });

        // Añadir zona horaria por defecto si no hay ninguna
        viewModel.getSelectedTimeZones().observe(getViewLifecycleOwner(), timeZones -> {
            if (timeZones.isEmpty()) {
                java.util.TimeZone defaultJavaZone = java.util.TimeZone.getDefault();
                TimeZone defaultZone = new TimeZone(defaultJavaZone.getID(), defaultJavaZone.getRawOffset() / 1000);
                viewModel.insertTimeZone(defaultZone);
            }
        });

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
                List<TimeZone> timeZones = adapter.getTimeZones();
                TimeZone removedTimeZone = timeZones.get(position);
                viewModel.deleteTimeZone(removedTimeZone);
                adapter.notifyItemRemoved(position);
            }
        }).attachToRecyclerView(recyclerView);

        FloatingActionButton fab = root.findViewById(R.id.fab_add_timezone);
        fab.setOnClickListener(v -> showTimeZonePicker());

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

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.add_timezone)
                .setItems(timeZoneDisplayNames, (dialog, which) -> {
                    String selectedId = mainTimeZones[which];
                    java.util.TimeZone selectedJavaTimeZone = java.util.TimeZone.getTimeZone(selectedId);
                    TimeZone selectedTimeZone = new TimeZone(
                        selectedId,
                        selectedJavaTimeZone.getRawOffset() / 1000
                    );
                    
                    // Verificar si la zona horaria ya está en la lista
                    List<TimeZone> currentTimeZones = adapter.getTimeZones();
                    boolean exists = false;
                    for (TimeZone tz : currentTimeZones) {
                        if (tz.getName().equals(selectedTimeZone.getName())) {
                            exists = true;
                            break;
                        }
                    }
                    
                    if (!exists) {
                        viewModel.insertTimeZone(selectedTimeZone);
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