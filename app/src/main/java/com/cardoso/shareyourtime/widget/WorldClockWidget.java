package com.cardoso.shareyourtime.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;
import com.cardoso.shareyourtime.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Controla el Widget de reloj mundial. Muestra la hora actual de la zona horaria del sistema
 * y se actualiza cada segundo utilizando Handler y Runnable.
 */
public class WorldClockWidget extends AppWidgetProvider {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
    private static final String ACTION_AUTO_UPDATE = "com.cardoso.shareyourtime.ACTION_AUTO_UPDATE";
    private static final long UPDATE_INTERVAL = 1000; // 1 segundo en milisegundos

    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Runnable updateRunnable;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        // Iniciar el proceso de actualización cada segundo
        startUpdatingEverySecond(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        startUpdatingEverySecond(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        stopUpdatingEverySecond();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_AUTO_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new android.content.ComponentName(context, WorldClockWidget.class));
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    private void startUpdatingEverySecond(final Context context) {
        if (updateRunnable == null) {
            updateRunnable = new Runnable() {
                @Override
                public void run() {
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                            new android.content.ComponentName(context, WorldClockWidget.class));
                    onUpdate(context, appWidgetManager, appWidgetIds);
                    // Actualiza el widget cada segundo
                    handler.postDelayed(this, UPDATE_INTERVAL);
                }
            };
            handler.post(updateRunnable);  // Comienza la actualización cada segundo
        }
    }

    private void stopUpdatingEverySecond() {
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);  // Detener las actualizaciones
            updateRunnable = null;
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.world_clock_widget);

        // Obtener la zona horaria del sistema
        TimeZone systemTimeZone = TimeZone.getDefault();
        Date now = new Date();

        timeFormat.setTimeZone(systemTimeZone);
        dateFormat.setTimeZone(systemTimeZone);

        // Actualiza la hora y la fecha en el widget
        views.setTextViewText(R.id.widget_time, timeFormat.format(now));
        views.setTextViewText(R.id.widget_date, dateFormat.format(now));
        views.setTextViewText(R.id.widget_timezone, systemTimeZone.getDisplayName());

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
