package ro.antiprotv.sugar.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {Alert.class, Item.class}, version = 1)
public abstract class RedalertRoomDatabase extends RoomDatabase {

    private static RedalertRoomDatabase INSTANCE;

    public static RedalertRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RedalertRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RedalertRoomDatabase.class, "redalert_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract AlertDao alertDao();

    public abstract ItemDao itemDao();

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}
