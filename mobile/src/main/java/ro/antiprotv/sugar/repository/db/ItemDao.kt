package ro.antiprotv.sugar.repository.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

/**
 * The ROOM alert DAO
 */
@Dao
interface ItemDao {
    @Insert(onConflict = REPLACE)
    fun insert(item: Item): Long

    @Query("select * from item")
    fun getAllItems(): LiveData<List<Item>>

    @Query("delete from item")
    fun removeAll()

    @Update
    fun updateItems(vararg items: Item)

    @Delete
    fun removeItems(vararg items: Item)
}