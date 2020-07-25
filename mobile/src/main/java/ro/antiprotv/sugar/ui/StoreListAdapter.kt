package ro.antiprotv.sugar.ui

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import java.util.*

/**
 * The adapter used for the autocomplete operations.
 * It is usable by either ITEM or STORE text areas autocomplete.
 * It performs db operations to retrieve results when the user inputs something.
 */
class StoreListAdapter(ctx: Context, itemLayout: Int, private var dataList: List<String>, private val viewModel: AlertViewModel) : ArrayAdapter<String>(ctx, itemLayout) {
    private val listFilter = ListFilter()
    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): String {
        return dataList[position]
    }

    override fun getFilter(): Filter {
        return listFilter
    }

    inner class ListFilter : Filter() {
        private val lock = Any()
        override fun performFiltering(prefix: CharSequence): FilterResults {
            val results = FilterResults()
            if (prefix.isEmpty()) {
                synchronized(lock) {
                    results.values = ArrayList<String>()
                    results.count = 0
                }
            } else {
                val searchStrLowerCase = prefix.toString().toLowerCase(Locale.ROOT) + "%"

                //Call to database to get matching records using room
                val matchValues = viewModel.selectStores(searchStrLowerCase)
                results.values = matchValues
                results.count = matchValues.size
            }
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            results.values?.let {
                dataList = it as ArrayList<String>
            } ?: Collections.emptyList<String>()
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }

}