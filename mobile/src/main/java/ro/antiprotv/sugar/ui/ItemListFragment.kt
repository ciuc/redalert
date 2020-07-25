package ro.antiprotv.sugar.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_item_list.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.antiprotv.sugar.R
import ro.antiprotv.sugar.repository.db.Item

/**
 * A placeholder fragment containing a simple view.
 */
class ItemListFragment : Fragment() {

    private val viewModel by viewModel<ItemViewModel>()
    private val alertViewModel by viewModel<AlertViewModel>()

    private lateinit var adapter: ItemListAdapter

    /**
     * this displays the "empty" view if there are no items, and hides it if there are items.
     *
     * @param items
     */
    private fun toggleItemListVisibility(items: List<Item>) {
        if (items.isNotEmpty()) {
            recyclerview_item_list.visibility = View.VISIBLE
            recyclerview_item_list_empty_view.visibility = View.GONE
        } else {
            recyclerview_item_list.visibility = View.GONE
            recyclerview_item_list_empty_view.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.activity_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            adapter = ItemListAdapter(it, viewModel, alertViewModel)
        }
        viewModel.allItems.observe(viewLifecycleOwner, Observer { newItems ->
            adapter.replaceItems(newItems)
            adapter.notifyDataSetChanged()
            toggleItemListVisibility(newItems)
        })

        button_item_list_add_item.setOnClickListener(AddItemClickListener())
        recyclerview_item_list.layoutManager = LinearLayoutManager(this.context)
        recyclerview_item_list.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                if (swipeDir == ItemTouchHelper.LEFT) {
                    val item = adapter.selectedItem()
                    alertViewModel.createAlertOrSetToNormalPriority(item)
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerview_item_list)
    }

    /**
     * The click listener that creates the item dialog to add items.
     */
    private inner class AddItemClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            val dialogBuilder = AddEditItemDialog(this@ItemListFragment.context!!, v, viewModel, adapter, Item(""))
            val dialog = dialogBuilder.create()
            dialogBuilder.setOnShowListener(dialog)
            dialog.show()
        }
    }

    private inner class RemoveAllItemsClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            val dialogBuilder = AlertDialog.Builder(v.context)
            dialogBuilder.setTitle(getString(R.string.interogative_remove_all_item))
            dialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            dialogBuilder.setPositiveButton(R.string.ok) { _, _ ->
                viewModel.removeAllItems()
                adapter.notifyDataSetChanged()
            }
            dialogBuilder.show()
        }
    }
}