package ro.antiprotv.redalert.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ro.antiprotv.redalert.R;

@Entity(tableName = "alert")
public class Alert {
    public static final int RED_ALERT = 1;
    public static final int ORANGE_ALERT = 2;
    public static final int YELLOW_ALERT = 3;
    //as if it was disabled
    //we do not delete alerts so we can use them for autocompletion
    public static final int GREEN_ALERT = 0;
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    @NonNull
    private int level;
    @NonNull
    private String item;
    private String store;

    public Alert(@NonNull int level, @NonNull String item, String store) {
        this.level = level;
        this.item = item;
        this.store = store;
    }

    public static int getColor(int level) {
        if (level == RED_ALERT) {
            return R.string.red;
        }
        if (level == YELLOW_ALERT) {
            return R.string.yellow;
        }
        if (level == ORANGE_ALERT) {
            return R.string.orange;
        }
        return R.string.red;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getItem() {
        return item;
    }

    public void setItem(@NonNull String item) {
        this.item = item;
    }

    @NonNull
    public int getLevel() {
        return level;
    }

    public void setLevel(@NonNull int level) {
        this.level = level;
    }

    public int getColor() {
        if (level == RED_ALERT) {
            return R.color.red;
        }
        if (level == YELLOW_ALERT) {
            return R.color.yellow;
        }
        if (level == ORANGE_ALERT) {
            return R.color.orange;
        }
        return R.color.yellow;
    }

    public int getIcon() {
        /*if (level == RED_ALERT) {
            return R.drawable.ic_warning_red_24dp;
        }
        if (level == YELLOW_ALERT) {
            return R.drawable.ic_warning_red_24dp;
        }
        if (level == ORANGE_ALERT) {
            return R.drawable.ic_warning_red_24dp;
        }*/
        return R.drawable.ic_priority_high_black_24dp;
    }
}
