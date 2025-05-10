package com.cardoso.shareyourtime.ui.alarm;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cardoso.shareyourtime.data.Alarm;
import com.cardoso.shareyourtime.data.AlarmDao;
import com.cardoso.shareyourtime.data.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlarmViewModel extends AndroidViewModel {
    private final AlarmDao alarmDao;
    private final ExecutorService executorService;

    public AlarmViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        alarmDao = db.alarmDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Alarm>> getAllAlarms() {
        return alarmDao.getAllAlarms();
    }

    public LiveData<Long> insert(Alarm alarm) {
        MutableLiveData<Long> result = new MutableLiveData<>();
        executorService.execute(() -> {
            long id = alarmDao.insert(alarm);
            result.postValue(id);
        });
        return result;
    }

    public void update(Alarm alarm) {
        executorService.execute(() -> alarmDao.update(alarm));
    }

    public void delete(Alarm alarm) {
        executorService.execute(() -> alarmDao.delete(alarm));
    }

    public void deleteAll() {
        executorService.execute(alarmDao::deleteAll);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
