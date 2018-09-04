package antiprotv.ro.redalert.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import antiprotv.ro.redalert.R;

@Entity(tableName = "alert")
public class Alert {
    public static final int RED_ALERT = 1;
    public static final int ORANGE_ALERT = 2;
    public static final int YELLOW_ALERT = 3;

    @IntDef({RED_ALERT, ORANGE_ALERT, YELLOW_ALERT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AlertLevel{}

    @PrimaryKey (autoGenerate = true)
    @NonNull
    private int id;

    @NonNull
    private int level;

    @NonNull
    private String item;

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    private String store;

    public Alert(@NonNull int level, @NonNull String item, String store){
        this.level = level;
        this.item = item;
        this.store = store;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public static String getColor(int level){
        if (level == RED_ALERT) {
            return "red";
        }
        if (level == YELLOW_ALERT) {
            return "yellow";
        }
        if (level == ORANGE_ALERT) {
            return "orange";
        }
        return "yellow";
    }

    public void setLevel(@NonNull int level) {
        this.level = level;
    }
}
