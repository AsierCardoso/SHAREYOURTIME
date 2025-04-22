package com.cardoso.shareyourtime.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.Date;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks WHERE dateTime >= :now AND completed = 0 ORDER BY dateTime ASC")
    LiveData<List<Task>> getUpcomingTasks(Date now);

    @Query("SELECT * FROM tasks WHERE dateTime < :now OR completed = 1")
    LiveData<List<Task>> getPastTasks(Date now);

    @Query("DELETE FROM tasks WHERE dateTime < :date AND completed = 1")
    void deletePastTasks(Date date);
} 