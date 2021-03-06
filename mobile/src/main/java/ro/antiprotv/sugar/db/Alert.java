package ro.antiprotv.sugar.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import ro.antiprotv.sugar.R;

@Entity(tableName = "alert")
public class Alert {
    public static final int RED_ALERT = 1;
    public static final int ORANGE_ALERT = 2;
    //as if it was disabled
    //we do not delete alerts so we can use them for autocompletion
    public static final int GREEN_ALERT = 0;
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    @NonNull
    private int level;
    @NonNull
    private String itemName;
    private String store;

    public Alert(@NonNull int level, @NonNull String itemName, String store) {
        this.level = level;
        this.itemName = itemName;
        this.store = store;
    }

    public static int getColorString(int level) {
        int color = R.string.red;
        switch (level) {
            case RED_ALERT:
                color = R.string.red;
                break;
            case ORANGE_ALERT:
                color = R.string.orange;
                break;
            case GREEN_ALERT:
                color = R.string.green;
                break;
        }

        return color;
    }

    public static int getColor(int level) {
        int color = R.string.red;
        switch (level) {
            case RED_ALERT:
                color = R.color.red;
                break;
            case ORANGE_ALERT:
                color = R.color.orange;
                break;
            case GREEN_ALERT:
                color = R.color.green;
                break;
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
    public String getItemName() {
        return itemName;
    }

    public void setItemName(@NonNull String itemId) {
        this.itemName = itemId;
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
        if (level == ORANGE_ALERT) {
            return R.color.orange;
        }
        return R.color.yellow;
    }

    public void demote() {
        switch (getLevel()) {
            case RED_ALERT:
                setLevel(ORANGE_ALERT);
                break;
            case ORANGE_ALERT:
                setLevel(GREEN_ALERT);
                break;
        }
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
        return R.drawable.ic_error_outline_black_24dp;
    }
}
