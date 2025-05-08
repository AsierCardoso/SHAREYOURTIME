package com.cardoso.shareyourtime.ui.worldclock;

import android.content.Intent;
import android.os.Bundle;
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
import com.cardoso.shareyourtime.MapActivity;
import com.cardoso.shareyourtime.utils.TimeZoneManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class WorldClockFragment extends Fragment {
    private static final int REQUEST_LOCATION = 1001;
    private RecyclerView recyclerView;
    private WorldClockAdapter adapter;
    private List<TimeZone> timeZones;
    private Timer timer;
    private TimeZoneManager timeZoneManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_world_clock, container, false);

        timeZoneManager = new TimeZoneManager("AIzaSyAQYmGcJxl1dZr3aBJYIMeFF74Q0kTNwjk");
        recyclerView = root.findViewById(R.id.recyclerViewTimeZones);
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

        // Configurar el botón de añadir por ubicación
        root.findViewById(R.id.btnAddByLocation).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivityForResult(intent, REQUEST_LOCATION);
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION && resultCode == getActivity().RESULT_OK) {
            double lat = data.getDoubleExtra("lat", 0);
            double lon = data.getDoubleExtra("lon", 0);
            
            timeZoneManager.getTimeZone(lat, lon, new TimeZoneManager.TimeZoneCallback() {
                @Override
                public void onTimeZoneReceived(String timeZoneId) {
                    TimeZone selectedTimeZone = TimeZone.getTimeZone(timeZoneId);
                    
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
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), 
                        getString(R.string.error_getting_timezone, error), 
                        Toast.LENGTH_SHORT).show();
                }
            });
        }
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