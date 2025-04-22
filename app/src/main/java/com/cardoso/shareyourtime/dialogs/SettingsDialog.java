package com.cardoso.shareyourtime.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.cardoso.shareyourtime.R;
import com.cardoso.shareyourtime.utils.SettingsManager;

public class SettingsDialog extends DialogFragment {
    private SettingsManager settingsManager;
    private RadioGroup themeGroup;
    private RadioGroup languageGroup;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        settingsManager = new SettingsManager(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_settings, null);

        themeGroup = view.findViewById(R.id.theme_group);
        languageGroup = view.findViewById(R.id.language_group);

        // Establecer selección actual
        ((RadioButton) themeGroup.getChildAt(settingsManager.isDarkTheme() ? 1 : 0)).setChecked(true);
        ((RadioButton) languageGroup.getChildAt("en".equals(settingsManager.getLanguage()) ? 1 : 0)).setChecked(true);

        builder.setView(view)
               .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                   boolean isDark = themeGroup.getCheckedRadioButtonId() == R.id.theme_dark;
                   String language = languageGroup.getCheckedRadioButtonId() == R.id.lang_en ? "en" : "es";
                   
                   settingsManager.setTheme(isDark);
                   settingsManager.setLanguage(language);
               })
               .setNegativeButton(android.R.string.cancel, null);

        return builder.create();
    }
} 

 