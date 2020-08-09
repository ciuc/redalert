package ro.antiprotv.sugar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import ro.antiprotv.sugar.repository.ItemRepository
import ro.antiprotv.sugar.repository.db.Category
import ro.antiprotv.sugar.repository.db.Item
import ro.antiprotv.sugar.repository.db.RedAlertRoomDatabase
import java.util.*

class ItemViewModel(app: Application) : KoinComponent, AndroidViewModel(app) {
    // may need to inject this for testing
    private val dispatcher = Dispatchers.IO

    private val itemRepository: ItemRepository = ItemRepository(get<RedAlertRoomDatabase>().itemDao())

    val allItems by lazy {
        itemRepository.getAll()
    }

    private fun getItemsByCategory(category: Category): List<Item> {
        return allItems.value?.filter { item -> item.category == category }
                ?: Collections.emptyList()
    }

    fun getItemsByCategory(categoryCode: Int): List<Item> {
        return getItemsByCategory(Category.fromCode(categoryCode))
    }

    fun insert(item: Item) {
        viewModelScope.launch(dispatcher) {
            itemRepository.insert(item)
        }
    }

    fun removeAllItems() {
        viewModelScope.launch(dispatcher) {
            itemRepository.removeAll()
        }
    }

    fun removeItem(alert: Item) {
        viewModelScope.launch(dispatcher) {
            itemRepository.remove(alert)
        }
    }

    private fun update(item: Item) = viewModelScope.launch(dispatcher) {
        itemRepository.update(item)
    }

    fun addQuantity(item: Item) {
        viewModelScope.launch(dispatcher) {
            item.quantity = item.quantity + 1
            update(item)
        }
    }

    fun subtractQuantity(item: Item) {
        viewModelScope.launch(dispatcher) {
            val q = item.quantity
            if (q != 0) {
                item.quantity = q - 1
                update(item)
            } else {
                insert(item)
            }
        }
    }

    fun selectItems(prefix: String): List<String> {
        return allItems.value
                ?.filter { item -> item.name.startsWith(prefix) }
                ?.map { item -> item.name }
                ?.distinct()
                ?.sorted()
                ?: Collections.emptyList()
    }
}