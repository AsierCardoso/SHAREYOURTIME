package com.cardoso.shareyourtime.data;

import androidx.room.TypeConverter;
import java.util.Date;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}