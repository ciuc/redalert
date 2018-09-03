package antiprotv.ro.redalert.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public abstract class ItemDao {

    @Insert
    public abstract void insert(Item item);

    @Query("select * from item order by name asc")
    public abstract LiveData<List<Item>> getAllItems();
}
