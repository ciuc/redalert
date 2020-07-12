package ro.antiprotv.sugar.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * The ROOM alert DAO
 */
@Dao
public abstract class AlertDao {

    @Insert
    public abstract long insert(Alert alert);

    /**
     * Retrieves all active alerts
     *
     * @return
     */
    @Query("select * from alert where level != 0 order by level asc")
    public abstract LiveData<List<Alert>> getAllActiveAlerts();

    @Query("select * from alert where level == 0")
    public abstract List<Alert> getAllDisabledAlertsSync();

    @Query("select * from alert where itemName = :itemName")
    public abstract List<Alert> getAlertsByItem(String itemName);


    @Query("update alert set level = 0")
    public abstract void removeAll();

    @Update
    public abstract void updateAlerts(Alert... alerts);

    @Query("select distinct store from alert where store like :prefix order by store")
    public abstract List<String> getStoresByPrefix(String prefix);

    @Query("select distinct store from alert where itemName = :item group by store order by count(store) desc")
    public abstract List<String> selectStoresByItem(String item);
}
