package com.cardoso.shareyourtime.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {
    @Insert
    long insert(Alarm alarm);

    @Update
    void update(Alarm alarm);

    @Delete
    void delete(Alarm alarm);

    @Query("DELETE FROM alarms")
    void deleteAll();

    @Query("SELECT * FROM alarms ORDER BY hour ASC, minute ASC")
    LiveData<List<Alarm>> getAllAlarms();

    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    LiveData<Alarm> getAlarm(int alarmId);
}