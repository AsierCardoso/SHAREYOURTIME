package com.cardoso.shareyourtime.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TimeZoneDao {
    @Insert
    long insertTimeZone(TimeZone timeZone);

    @Update
    void updateTimeZone(TimeZone timeZone);

    @Delete
    void deleteTimeZone(TimeZone timeZone);

    @Query("SELECT * FROM timezones")
    LiveData<List<TimeZone>> getAllTimeZones();

    @Query("SELECT * FROM timezones WHERE isSelected = 1")
    LiveData<List<TimeZone>> getSelectedTimeZones();
} 