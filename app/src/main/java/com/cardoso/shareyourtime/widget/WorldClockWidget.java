package com.cardoso.shareyourtime.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import com.cardoso.shareyourtime.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WorldClockWidget extends AppWidgetProvider {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.world_clock_widget);
        
        // Obtener la zona horaria predeterminada
        TimeZone defaultTimeZone = TimeZone.getDefault();
        Date now = new Date();

        timeFormat.setTimeZone(defaultTimeZone);
        dateFormat.setTimeZone(defaultTimeZone);

        views.setTextViewText(R.id.widget_time, timeFormat.format(now));
        views.setTextViewText(R.id.widget_date, dateFormat.format(now));
        views.setTextViewText(R.id.widget_timezone, defaultTimeZone.getDisplayName());

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
} 