package com.cardoso.shareyourtime.ui.task;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cardoso.shareyourtime.data.Task;
import com.cardoso.shareyourtime.data.TaskDao;
import com.cardoso.shareyourtime.data.AppDatabase;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Calendar;

public class TaskViewModel extends AndroidViewModel {
    private final TaskDao taskDao;
    private final ExecutorService executorService;

    public TaskViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        taskDao = db.taskDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Task>> getUpcomingTasks() {
        return taskDao.getUpcomingTasks(new Date());
    }

    public LiveData<Long> insert(Task task) {
        MutableLiveData<Long> result = new MutableLiveData<>();
        executorService.execute(() -> {
            long id = taskDao.insert(task);
            result.postValue(id);
        });
        return result;
    }

    public void update(Task task) {
        executorService.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        executorService.execute(() -> taskDao.delete(task));
    }

    public void cleanupPastTasks() {
        executorService.execute(() -> {
            // Eliminar tareas completadas más antiguas que 7 días
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -7);
            taskDao.deletePastTasks(calendar.getTime());
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
} 