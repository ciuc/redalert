package ro.antiprotv.sugar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import ro.antiprotv.sugar.repository.AlertRepository
import ro.antiprotv.sugar.repository.ItemRepository
import ro.antiprotv.sugar.repository.db.Alert
import ro.antiprotv.sugar.repository.db.AlertType
import ro.antiprotv.sugar.repository.db.Item
import ro.antiprotv.sugar.repository.db.RedAlertRoomDatabase
import java.util.*

class AlertViewModel(app: Application) : KoinComponent, AndroidViewModel(app) {
    // may need to inject this for testing
    private val dispatcher = Dispatchers.IO

    private val alertRepository: AlertRepository = AlertRepository(get<RedAlertRoomDatabase>().alertDao())
    private val itemRepository: ItemRepository = ItemRepository(get<RedAlertRoomDatabase>().itemDao())

    val allAlerts by lazy {
        alertRepository.getAll()
    }

    fun insert(alert: Alert) {
        viewModelScope.launch(dispatcher) {
            alertRepository.insert(alert)
            itemRepository.insert(Item(alert.itemName))
        }
    }

    fun createAlert(item: Item) {
        val alert = allAlerts.value?.firstOrNull { alert -> alert.itemName == item.name }
        if (alert == null) {
            val newAlert = Alert(AlertType.RED, item.name)
            viewModelScope.launch(dispatcher) {
                alertRepository.insert(newAlert)
            }
        }
    }

    fun createAlertOrSetToNormalPriority(item: Item) {
        val alert = allAlerts.value?.firstOrNull { alert -> alert.itemName == item.name }
        when {
            alert == null -> {
                val newAlert = Alert(AlertType.RED, item.name)
                viewModelScope.launch(dispatcher) {
                    alertRepository.insert(newAlert)
                }
            }
            alert.type == AlertType.DELETED -> {
                changeLevel(alert, AlertType.ORANGE)
            }
            else -> {
                // TODO: kotlin quirk here
            }
        }
    }

    fun findAlert(item: Item): Alert? {
        return allAlerts.value
                ?.firstOrNull { it.itemName == item.name }
    }

    fun removeAllAlerts() {
        viewModelScope.launch(dispatcher) {
            alertRepository.removeAll()
        }
    }

    fun removeAlert(item: Item) {
        val alert = allAlerts.value
                ?.firstOrNull { it.itemName == item.name }
        alert?.let {
            viewModelScope.launch(dispatcher) {
                if (alert.type != AlertType.DELETED)
                    alertRepository.update(alert, AlertType.DELETED)
            }
        }
    }

    fun changeLevel(alert: Alert, type: AlertType) {
        viewModelScope.launch(dispatcher) {
            alertRepository.update(alert, type)
        }
    }

    fun update(alert: Alert) {
        viewModelScope.launch(dispatcher) {
            alertRepository.update(alert)
        }
    }

    fun selectStores(prefix: String): List<String> {
        return allAlerts.value?.filter { alert -> alert.store.startsWith(prefix) }?.map { alert -> alert.store }?.distinct()?.sorted()
                ?: Collections.emptyList()
    }

    fun selectStoresByItem(itemName: String): String {
        val storeList = allAlerts.value?.filter { alert -> alert.itemName == itemName }?.map { alert -> alert.store }?.distinct()?.sorted()
                ?: Collections.emptyList()

        return storeList.joinToString(separator = ".")
    }
}