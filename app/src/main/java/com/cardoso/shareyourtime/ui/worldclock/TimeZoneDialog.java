package com.cardoso.shareyourtime.ui.worldclock;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.cardoso.shareyourtime.R;
import com.cardoso.shareyourtime.data.TimeZone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimeZoneDialog extends DialogFragment {
    private OnTimeZoneSelectedListener listener;

    public interface OnTimeZoneSelectedListener {
        void onTimeZoneSelected(TimeZone timeZone);
    }

    public void setOnTimeZoneSelectedListener(OnTimeZoneSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_timezone, null);

        ListView listView = view.findViewById(R.id.timezone_list);
        String[] timeZoneIds = java.util.TimeZone.getAvailableIDs();
        List<String> timeZoneNames = new ArrayList<>(Arrays.asList(timeZoneIds));
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            timeZoneNames
        );
        
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, v, position, id) -> {
            String selectedTimeZoneId = timeZoneNames.get(position);
            java.util.TimeZone javaTimeZone = java.util.TimeZone.getTimeZone(selectedTimeZoneId);
            TimeZone timeZone = new TimeZone(
                selectedTimeZoneId,
                javaTimeZone.getRawOffset() / 1000
            );
            if (listener != null) {
                listener.onTimeZoneSelected(timeZone);
            }
            dismiss();
        });

        builder.setView(view)
               .setTitle(R.string.select_timezone)
               .setNegativeButton(R.string.cancel, (dialog, id) -> dismiss());

        return builder.create();
    }
} 