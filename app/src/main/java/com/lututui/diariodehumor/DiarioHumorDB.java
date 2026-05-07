package com.lututui.diariodehumor;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.lututui.diariodehumor.dao.RegistroDeHumorDao;
import com.lututui.diariodehumor.dao.TagDao;
import com.lututui.diariodehumor.tags.Tag;
import com.lututui.diariodehumor.tags.TagCrossRefRegistroDeHumor;

@Database(entities = {
        RegistroDeHumorEntity.class, Tag.class, TagCrossRefRegistroDeHumor.class
}, version = 1)
@TypeConverters(Converters.class)
public abstract class DiarioHumorDB extends RoomDatabase {
    private static final String DB_NAME = "diario_humor_db";

    private static volatile DiarioHumorDB INSTANCE;

    public static DiarioHumorDB getInstance(final Context context) {
        if (INSTANCE == null) {

            synchronized (DiarioHumorDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, DiarioHumorDB.class, DB_NAME)
                                   .allowMainThreadQueries().build();
                }
            }
        }

        return INSTANCE;
    }

    public abstract RegistroDeHumorDao getRegistroDeHumorDao();
    public abstract TagDao getTagDao();
}
