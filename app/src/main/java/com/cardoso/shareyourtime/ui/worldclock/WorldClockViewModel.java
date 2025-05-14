package com.cardoso.shareyourtime.ui.worldclock;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.cardoso.shareyourtime.data.AppDatabase;
import com.cardoso.shareyourtime.data.TimeZone;
import com.cardoso.shareyourtime.data.TimeZoneDao;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldClockViewModel extends AndroidViewModel {
    private final TimeZoneDao timeZoneDao;
    private final ExecutorService executorService;

    public WorldClockViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        timeZoneDao = db.timeZoneDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<TimeZone>> getSelectedTimeZones() {
        return timeZoneDao.getSelectedTimeZones();
    }

    public void insertTimeZone(TimeZone timeZone) {
        executorService.execute(() -> timeZoneDao.insertTimeZone(timeZone));
    }

    public void updateTimeZone(TimeZone timeZone) {
        executorService.execute(() -> timeZoneDao.updateTimeZone(timeZone));
    }

    public void deleteTimeZone(TimeZone timeZone) {
        executorService.execute(() -> timeZoneDao.deleteTimeZone(timeZone));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
} 