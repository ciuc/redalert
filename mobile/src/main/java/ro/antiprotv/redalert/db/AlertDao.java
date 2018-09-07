package ro.antiprotv.redalert.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * The ROOM alert DAO
 */
@Dao
public abstract class AlertDao {

    @Insert
    public abstract long insert(Alert alert);

    @Query("select * from alert where level != 0 order by level asc")
    public abstract LiveData<List<Alert>> getAllAlerts();

    @Query("delete from alert")
    public abstract void removeAll();

    @Delete
    public abstract void removeAlert(Alert alert);

    @Update
    public abstract void updateAlerts(Alert... alerts);

    @Query("select distinct item from alert where item like :prefix order by item")
    public abstract List<String> getItemsByPrefix(String prefix);

    @Query("select distinct store from alert where store like :prefix order by store")
    public abstract List<String> getStoresByPrefix(String prefix);

    @Query("select distinct store from alert where item = :item group by store order by count(store) desc")
    public abstract List<String> selectStoresByItem(String item);
}
