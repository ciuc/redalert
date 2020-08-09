package ro.antiprotv.sugar.repository

import androidx.lifecycle.LiveData
import ro.antiprotv.sugar.repository.db.Item
import ro.antiprotv.sugar.repository.db.ItemDao

/**
 * The ROOM item repo
 */
class ItemRepository(private val itemDao: ItemDao) {

    fun insert(item: Item) = itemDao.insert(item)

    fun getAll(): LiveData<List<Item>> = itemDao.getAllItems()

    fun update(item: Item) = itemDao.updateItems(item)

    fun removeAll() = itemDao.removeAll()

    fun remove(vararg items: Item) = itemDao.removeItems(*items)
}