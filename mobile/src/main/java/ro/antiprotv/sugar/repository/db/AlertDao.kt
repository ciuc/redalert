package ro.antiprotv.sugar.repository.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update

/**
 * The ROOM alert DAO
 */
@Dao
interface AlertDao {
    @Insert(onConflict = REPLACE)
    fun insert(alert: Alert): Long

    @Query("select * from alert")
    fun allAlerts(): LiveData<List<Alert>>

    @Query("update alert set type = 0 where type != 0")
    fun removeAll()

    @Update
    fun updateAlerts(vararg alerts: Alert)
}