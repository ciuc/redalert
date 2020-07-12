package ro.antiprotv.sugar.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;


/**
 * What I have in the house
 */
@Entity(tableName = "item")
public class Item {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;

    private String name;

    private int quantity;
    private int measureUnit;
    private long timestampChanged;
    private int category;
    private boolean autoRemoveAlert;
    private int quantityLeftAlert;

    public Item() {
        //empty constructor required by law
    }

    public Item(String name, int quantity, int unit, int category) {
        this.name = name;
        this.quantity = quantity;
        this.measureUnit = unit;
        this.category = category;
    }

    public Item(String name, int quantity, int unit, int category, int quantityLeftAlert, boolean autoRemoveAlert) {
        this.name = name;
        this.quantity = quantity;
        this.measureUnit = unit;
        this.category = category;
        this.quantityLeftAlert = quantityLeftAlert;
        this.autoRemoveAlert = autoRemoveAlert;
    }

    public Item(String name) {
        this.name = name;
        this.quantity = 0;
        this.measureUnit = MeasureUnit.OTHER.code;
        this.category = Category.OTHER.code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(int measureUnit) {
        this.measureUnit = measureUnit;
    }

    public long getTimestampChanged() {
        return timestampChanged;
    }

    public void setTimestampChanged(long timestampChanged) {
        this.timestampChanged = timestampChanged;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getQuantityLeftAlert() {
        return quantityLeftAlert;
    }

    public void setQuantityLeftAlert(int quantityLeftAlert) {
        this.quantityLeftAlert = quantityLeftAlert;
    }

    public boolean isAutoRemoveAlert() {
        return autoRemoveAlert;
    }

    public void setAutoRemoveAlert(boolean autoRemoveAlert) {
        this.autoRemoveAlert = autoRemoveAlert;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
