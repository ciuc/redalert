package ro.antiprotv.sugar.repository.db

import androidx.room.Entity

@Entity(primaryKeys = ["itemName", "store"])
data class Alert(
        var type: AlertType = AlertType.ORANGE,
        var itemName: String = "",
        var store: String = ""
) {

    fun demote() {
        when (type) {
            AlertType.RED -> type = AlertType.ORANGE
            AlertType.ORANGE -> type = AlertType.DELETED
            AlertType.DELETED -> {
            }
        }
    }
}