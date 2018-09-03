package antiprotv.ro.redalert.db;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {Item.class, Alert.class}, version = 1)
public abstract class RedalertRoomDatabase extends RoomDatabase {

    public abstract ItemDao itemDao();
    public abstract AlertDao alertDao();

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
