package ro.antiprotv.sugar.repository.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * What I have in the house
 */
@Entity(tableName = "item")
data class Item(
        @PrimaryKey
        val name: String = "",
        var quantity: Int = 0,
        val measureUnit: MeasureUnit = MeasureUnit.OTHER,
        val category: Category = Category.OTHER,
        val quantityLeftAlert: Int = 0,
        val autoRemoveAlert: Boolean = false) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}