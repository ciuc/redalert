package antiprotv.ro.redalert.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public abstract class AlertDao {

    @Insert
    public abstract void insert(Alert alert);

    @Query("select * from alert order by level asc")
    public abstract LiveData<List<Alert>> getAllAlerts();
}
