package com.lututui.diariodehumor;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.lututui.diariodehumor.dao.RegistroDeHumorDao;
import com.lututui.diariodehumor.dao.TagDao;
import com.lututui.diariodehumor.tags.Tag;
import com.lututui.diariodehumor.tags.TagRegistroDeHumor;

@Database(entities = {
        RegistroDeHumorEntity.class, Tag.class, TagRegistroDeHumor.class
}, version = 1)
@TypeConverters(Converters.class)
public abstract class DiarioHumorDB extends RoomDatabase {
    private static final String DB_NAME = "diario_humor_db";
    private static final String DB_DEMO_NAME = "diario_humor_db_demo";

    private static volatile DiarioHumorDB INSTANCE;
    private static volatile DiarioHumorDB DEMO_INSTANCE;

    public static void resetDemo(Context context) {
        synchronized (DiarioHumorDB.class) {
            if (DEMO_INSTANCE != null && DEMO_INSTANCE.isOpen()) {
                DEMO_INSTANCE.close();
            }

            context.deleteDatabase(DB_DEMO_NAME);

            DEMO_INSTANCE = Room.databaseBuilder(context, DiarioHumorDB.class, DB_DEMO_NAME)
                                .allowMainThreadQueries().build();
        }
    }

    public static DiarioHumorDB getInstance(Context context) {
        var sharedPref = context.getSharedPreferences(Util.SharedPreferences.FILE, MODE_PRIVATE);
        var demo = sharedPref.getBoolean(Util.SharedPreferences.SP_DEMO, false);

        if (demo) {
            fecharLive();
            return getDemoInstance(context);
        }

        fecharDemo();
        return getLiveInstance(context);
    }

    private static void fecharLive() {
        synchronized (DiarioHumorDB.class) {
            if (INSTANCE != null && INSTANCE.isOpen()) {
                INSTANCE.close();
                INSTANCE = null;
            }
        }
    }

    private static void fecharDemo() {
        synchronized (DiarioHumorDB.class) {
            if (DEMO_INSTANCE != null && DEMO_INSTANCE.isOpen()) {
                DEMO_INSTANCE.close();
                DEMO_INSTANCE = null;
            }
        }
    }

    private static DiarioHumorDB getLiveInstance(Context context) {
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

    private static DiarioHumorDB getDemoInstance(Context context) {
        if (DEMO_INSTANCE == null) {

            synchronized (DiarioHumorDB.class) {
                if (DEMO_INSTANCE == null) {
                    DEMO_INSTANCE = Room.databaseBuilder(context, DiarioHumorDB.class, DB_DEMO_NAME)
                                        .allowMainThreadQueries().build();
                }
            }
        }

        return DEMO_INSTANCE;
    }

    public abstract RegistroDeHumorDao getRegistroDeHumorDao();

    public abstract TagDao getTagDao();
}
