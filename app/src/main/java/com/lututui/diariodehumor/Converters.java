package com.lututui.diariodehumor;

import androidx.room.TypeConverter;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static long dateToLong(Date date) {
        return date == null ? 0 : date.getTime();
    }

    @TypeConverter
    public static Date longToDate(long timestamp) {
        return new Date(timestamp);
    }
}
