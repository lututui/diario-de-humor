package com.lututui.diariodehumor;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class Converters {
    @TypeConverter
    public static long localDateToLong(LocalDate data) {
        if (data == null) {
            return 0;
        }

        return data.toEpochDay();
    }

    @TypeConverter
    public static LocalDate longToLocalDate(long epochDay) {
        return LocalDate.ofEpochDay(epochDay);
    }
}