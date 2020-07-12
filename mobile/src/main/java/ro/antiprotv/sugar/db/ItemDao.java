package ro.antiprotv.sugar.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * The ROOM alert DAO
 */
@Dao
public abstract class ItemDao {

    @Insert
    public abstract long insert(Item item);

    @Query("select * from item")
    public abstract LiveData<List<Item>> getAllItems();

    @Query("select * from item")
    public abstract List<Item> getAllItemsSync();

    @Query("select * from item where name = :name")
    public abstract Item getItemByName(String name);

    @Query("delete from item")
    public abstract void removeAll();

    @Delete
    public abstract void removeItem(Item item);

    @Update
    public abstract void updateItems(Item... items);

    @Query("select distinct name from item where name like :prefix order by name")
    public abstract List<String> getItemsByPrefix(String prefix);

    @Query("select * from item where category = :category")
    public abstract List<Item> getItemsByCategory(int category);
}
