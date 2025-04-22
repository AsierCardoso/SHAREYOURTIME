package com.cardoso.shareyourtime.ui.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cardoso.shareyourtime.R;
import com.cardoso.shareyourtime.data.Alarm;
import com.cardoso.shareyourtime.databinding.FragmentAlarmBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Calendar;

public class AlarmFragment extends Fragment {
    private FragmentAlarmBinding binding;
    private AlarmViewModel alarmViewModel;
    private AlarmManager alarmManager;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddAlarm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        binding = FragmentAlarmBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        
        recyclerView = binding.alarmsList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        AlarmAdapter adapter = new AlarmAdapter(alarm -> {
            // Manejar el clic en una alarma
            // Por ejemplo, editar la alarma
        });
        recyclerView.setAdapter(adapter);

        alarmViewModel.getAllAlarms().observe(getViewLifecycleOwner(), alarms -> {
            adapter.submitList(alarms);
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
                Alarm alarm = adapter.getCurrentList().get(position);
                // Cancelar la alarma programada
                cancelAlarm(alarm);
                // Eliminar de la base de datos
                alarmViewModel.delete(alarm);
            }
        }).attachToRecyclerView(recyclerView);

        fabAddAlarm = binding.fabAddAlarm;
        fabAddAlarm.setOnClickListener(v -> showTimePickerDialog());

        return root;
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
            getContext(),
            (view, hourOfDay, minute1) -> setAlarm(hourOfDay, minute1),
            hour,
            minute,
            true
        );
        timePickerDialog.show();
    }

    private void setAlarm(int hourOfDay, int minute) {
        Alarm alarm = new Alarm(hourOfDay, minute);
        alarm.setLabel(getString(R.string.alarm)); // Etiqueta por defecto
        
        // Guardar la alarma en la base de datos
        alarmViewModel.insert(alarm).observe(getViewLifecycleOwner(), alarmId -> {
            if (alarmId != null) {
                alarm.setId(alarmId.intValue());
                scheduleAlarm(alarm);
            }
        });
    }

    private void scheduleAlarm(Alarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("ALARM_ID", alarm.getId());
        intent.putExtra("ALARM_LABEL", alarm.getLabel());
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            getContext(),
            alarm.getId(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
            );
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
            );
        }
    }

    private void cancelAlarm(Alarm alarm) {
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            getContext(),
            alarm.getId(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Cancelar la alarma
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}