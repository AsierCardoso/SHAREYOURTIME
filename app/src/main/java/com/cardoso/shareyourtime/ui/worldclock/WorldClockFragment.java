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
import com.cardoso.shareyourtime.utils.FirestoreManager;
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
    private FirestoreManager firestoreManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_world_clock, container, false);

        timeZoneManager = new TimeZoneManager("AIzaSyAQYmGcJxl1dZr3aBJYIMeFF74Q0kTNwjk");
        firestoreManager = new FirestoreManager();

        recyclerView = root.findViewById(R.id.recyclerViewTimeZones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        timeZones = new ArrayList<>();

        adapter = new WorldClockAdapter(timeZones, requireContext());
        recyclerView.setAdapter(adapter);

        loadSavedTimeZones(); // 👈 Cargar desde Firestore al iniciar

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position == 0) {
                    adapter.notifyItemChanged(0);
                    Toast.makeText(getContext(), R.string.cannot_remove_default_timezone, Toast.LENGTH_SHORT).show();
                    return;
                }

                timeZones.remove(position);
                adapter.notifyItemRemoved(position);
                saveTimeZones(); // 👈 Guardar cambios
            }
        }).attachToRecyclerView(recyclerView);

        root.findViewById(R.id.btnAddByLocation).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivityForResult(intent, REQUEST_LOCATION);
        });

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
                        saveTimeZones(); // 👈 Guardar tras añadir
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

    private void saveTimeZones() {
        List<String> ids = new ArrayList<>();
        for (TimeZone tz : timeZones) {
            ids.add(tz.getID());
        }

        String defaultZone = timeZones.isEmpty() ? TimeZone.getDefault().getID() : timeZones.get(0).getID();

        firestoreManager.saveTimeZones(ids, defaultZone, new FirestoreManager.FirestoreCallback() {
            @Override
            public void onSuccess() {
                // Guardado con éxito (puedes mostrar un log si quieres)
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error al guardar zonas: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSavedTimeZones() {
        firestoreManager.loadTimeZones(new FirestoreManager.TimeZonesCallback() {
            @Override
            public void onTimeZonesLoaded(List<String> tzIds, String defaultZoneId) {
                timeZones.clear();
                for (String id : tzIds) {
                    timeZones.add(TimeZone.getTimeZone(id));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error al cargar zonas: " + error, Toast.LENGTH_SHORT).show();
            }
        });
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
