package ro.antiprotv.sugar.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import ro.antiprotv.sugar.R
import ro.antiprotv.sugar.repository.db.Category
import ro.antiprotv.sugar.repository.db.Item
import ro.antiprotv.sugar.util.NotificationManager

/**
 * The list adapter used by the RecyclerView.
 * It deals with the display of the items into the layout,
 * the creation of the item context menu - together with the handling of the action clicks
 */
class ItemListAdapter(val context: Context, val viewModel: ItemViewModel, val alertViewModel: AlertViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private val items = ArrayList<Item>()
    private var filtered = false
    private val listFilter = ListFilter()

    private lateinit var viewHolder: ItemViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ItemViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.content_item_list, parent, false)
        viewHolder = ItemViewHolder(itemView, Item())
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ItemViewHolder
        val res = viewHolder.itemView.context.resources
        holder.itemView.isLongClickable = true
        if (items.isNotEmpty()) {
            val item = items[position]
            holder.item = item
            holder.itView.text = item.name
            holder.quantityView.text = Integer.valueOf(item.quantity).toString()
            val muResId = res.getIdentifier(item.measureUnit.resId, "string", context.packageName)
            holder.unitView.text = res.getString(muResId)
            holder.addButton.setOnClickListener(AddQuantityOnClickListener(item))
            holder.subtractButton.setOnClickListener(SubtractQuantityOnClickListener(item))
            holder.editButton.setOnClickListener(EditItemOnClickListener(item))
            val categoryResId = res.getIdentifier(item.category.resId, "string", context.packageName)
            val category = res.getString(categoryResId)
            holder.category.text = "[$category]"
            holder.category.setOnClickListener(FilterByCategoryOnClickListener(item.category))
        } else {
            holder.itView.text = "n/a"
        }

    }

    fun replaceItems(newItems: List<Item>?) {
        newItems?.let {
            items.clear()
            items.addAll(it)
        }
    }

    fun selectedItem(): Item {
        return items[viewHolder.adapterPosition]
    }

    override fun getItemCount() = items.size

    override fun getFilter(): Filter {
        return listFilter
    }

    inner class ItemViewHolder(view: View, var item: Item) : RecyclerView.ViewHolder(view) {
        private val iconView: ImageView = view.findViewById(R.id.adapter_item_image_item_icon)
        val itView: TextView = view.findViewById(R.id.adapter_items_text_item_name)
        val quantityView: TextView = view.findViewById(R.id.adapter_items_text_quantity)
        val unitView: TextView = view.findViewById(R.id.adapter_item_text_unit)
        val addButton: ImageButton = view.findViewById(R.id.adapter_items_button_add_quantity)
        val subtractButton: ImageButton = view.findViewById(R.id.adapter_items_button_subtract_quantity)
        val editButton: ImageButton = view.findViewById(R.id.adapter_items_button_edit_item)
        val category: TextView = view.findViewById(R.id.adapter_items_text_category)
        val filterImage: ImageButton = view.findViewById(R.id.adapter_items_button_filterByCategory)

    }

    private inner class AddQuantityOnClickListener(private val item: Item) : KoinComponent, View.OnClickListener {
        private val notificationMgr = get<NotificationManager>()
        override fun onClick(v: View) {
            viewModel.addQuantity(item)
            if (item.autoRemoveAlert && item.quantity > item.quantityLeftAlert) {
                val alert = alertViewModel.findAlert(item)
                alert?.let {
                    alertViewModel.removeAlert(item)
                    notificationMgr.cancelNotification(it)
                }
            }
        }
    }

    private inner class SubtractQuantityOnClickListener(private val item: Item) : View.OnClickListener {
        override fun onClick(v: View) {
            viewModel.subtractQuantity(item)
            if (item.quantity == item.quantityLeftAlert) {
                alertViewModel.createAlertOrSetToNormalPriority(item)
            }
        }
    }

    private inner class EditItemOnClickListener(private val item: Item) : View.OnClickListener {
        override fun onClick(v: View) {
            val dialogBuilder = AddEditItemDialog(v.context, v, viewModel, this@ItemListAdapter, item)
            val dialog = dialogBuilder.create()
            dialogBuilder.setOnShowListener(dialog)
            dialog.show()
        }

    }

    private inner class FilterByCategoryOnClickListener internal constructor(private val category: Category) : View.OnClickListener, Filter() {
        private val lock = Any()
        override fun onClick(v: View) {
            filtered = if (!filtered) {
                publishResults("", performFiltering(category.toString()))
                true
            } else {
                publishResults("", performFiltering("1000"))
                false
            }
        }

        override fun performFiltering(categoryCode: CharSequence): FilterResults {
            val results = FilterResults()
            if (categoryCode.isEmpty()) {
                synchronized(lock) {
                    results.values = ArrayList<String>()
                    results.count = 0
                }
            } else {
                val search = categoryCode.toString().toInt()

                //Call to database to get matching records using room
                val matchValues: List<Item>
                matchValues = viewModel.getItemsByCategory(search)
                results.values = matchValues
                results.count = matchValues.size
            }
            return results
        }

        override fun publishResults(categoryCode: CharSequence, results: FilterResults) {
            items.clear()
            items.addAll(results.values as ArrayList<Item>)

            if (results.count == 0) {
                viewModel.allItems.value?.let {
                    items.addAll(it)
                }
            }
        }
    }


    private inner class ListFilter : Filter() {
        private val lock = Any()
        override fun performFiltering(categoryCode: CharSequence): FilterResults {
            val results = FilterResults()
            if (categoryCode.isEmpty()) {
                synchronized(lock) {
                    results.values = ArrayList<String>()
                    results.count = 0
                }
            } else {
                viewModel.viewModelScope.launch {
                    val search = categoryCode.toString().toInt()

                    //Call to database to get matching records using room
                    val matchValues: List<Item>
                    matchValues = viewModel.getItemsByCategory(search)
                    results.values = matchValues
                    results.count = matchValues.size
                }
            }
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            viewModel.viewModelScope.launch {
                items.clear()
                items.addAll(results.values as ArrayList<Item>)

                if (results.count == 0) {
                    viewModel.allItems.value?.let {
                        items.addAll(it)
                    }
                }
            }
        }
    }

}