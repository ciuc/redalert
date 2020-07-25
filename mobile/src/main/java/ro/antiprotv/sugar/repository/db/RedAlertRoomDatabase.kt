package ro.antiprotv.sugar.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Alert::class, Item::class], version = 2)
@TypeConverters(Converters::class)
abstract class RedAlertRoomDatabase : RoomDatabase() {
    abstract fun alertDao(): AlertDao
    abstract fun itemDao(): ItemDao

    override fun clearAllTables() {}
}
