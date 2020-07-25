package ro.antiprotv.sugar.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_alert_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.viewmodel.ext.android.viewModel
import ro.antiprotv.sugar.R
import ro.antiprotv.sugar.repository.db.Alert
import ro.antiprotv.sugar.repository.db.AlertType
import ro.antiprotv.sugar.util.NotificationManager
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class AlertListFragment : Fragment() {
    private val viewModel by viewModel<AlertViewModel>()

    private val notificationMgr = get<NotificationManager>()

    private lateinit var adapter: AlertListAdapter

    /**
     * this displays the "empty" view if there are no alerts, and hides it if there are alerts.
     *
     * @param alerts
     */
    private fun toggleAlertListVisibility(alerts: List<Alert>) {
        if (alerts.isNotEmpty()) {
            recyclerview_alert_list.visibility = View.VISIBLE
            recyclerview_alert_list_empty_view.visibility = View.GONE
        } else {
            recyclerview_alert_list.visibility = View.GONE
            recyclerview_alert_list_empty_view.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_alert_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            adapter = AlertListAdapter(it, viewModel)
        }

        recyclerview_alert_list.layoutManager = LinearLayoutManager(this.context)
        recyclerview_alert_list.adapter = adapter
        viewModel.allAlerts.observe(viewLifecycleOwner, Observer { newAlerts ->
            newAlerts?.let { alerts ->
                val visibleAlerts = alerts.filterNot { it.type == AlertType.DELETED }
                adapter.replaceAlerts(visibleAlerts)
                adapter.notifyDataSetChanged()
                toggleAlertListVisibility(visibleAlerts)
                notificationMgr.reissueAllAlerts(newAlerts)
            }
        })

        button_alert_list_add_red.setOnClickListener(AddAlertClickListener(AlertType.RED))
        button_alert_list_add_orange.setOnClickListener(AddAlertClickListener(AlertType.ORANGE))
        button_alert_list_remove_all.setOnClickListener(RemoveAllAlertsClickListener())

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                if (swipeDir == ItemTouchHelper.LEFT) {
                    val alert = adapter.selectedAlert()
                    viewModel.changeLevel(alert, AlertType.DELETED)
                    notificationMgr.notifyAlert(alert)
                } else if (swipeDir == ItemTouchHelper.RIGHT) {
                    val alert = adapter.selectedAlert()
                    alert.demote()
                    viewModel.update(alert)
                    notificationMgr.notifyAlert(alert)
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerview_alert_list)
    }

    /**
     * The click listener that creates the alert dialog to add alerts.
     * It takes the alert level (color) as a param to know what it needs to add.
     * The dialog itself has the logic of autocomplete.
     */
    private inner class AddAlertClickListener internal constructor(private val type: AlertType) : View.OnClickListener {
        override fun onClick(v: View) {
            val dialogBuilder = AlertDialog.Builder(this@AlertListFragment.context)
            dialogBuilder.setTitle(getString(R.string.add_new_alert, resources.getString(type.colorName).toUpperCase(Locale.getDefault())))
            val layout = LayoutInflater.from(v.context).inflate(R.layout.add_alert_dialog, null) as LinearLayout

            val addItem = layout.findViewById<AutoCompleteTextView>(R.id.dialog_alert_autocomplete_add_item)
            val addStore = layout.findViewById<AutoCompleteTextView>(R.id.dialog_alert_autocomplete_add_store)
            addItem.setHint(R.string.what)
            addStore.setHint(R.string.where)
            val autocompleteItemAdapter: ArrayAdapter<*> = ItemListAutocompleteAdapter(this@AlertListFragment.context!!, android.R.layout.simple_list_item_1, ArrayList(), viewModel)
            addItem.setAdapter(autocompleteItemAdapter)
            addItem.threshold = 2
            addItem.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    val item = parent.getItemAtPosition(position).toString()
                    addItem.setText(item)
                    addStore.setText(viewModel.selectStoresByItem(item))
                    addStore.setSelectAllOnFocus(true)
                }
            }
            val autocompleteStoreAdapter: ArrayAdapter<*> = ItemListAutocompleteAdapter(this@AlertListFragment.context!!, android.R.layout.simple_list_item_1, ArrayList(), viewModel)
            addStore.setAdapter(autocompleteStoreAdapter)
            dialogBuilder.setView(layout)
            dialogBuilder.setPositiveButton(R.string.add) { dialog, _ ->
                val vm by viewModel<AlertViewModel>()
                if (addItem.text.toString().trim { it <= ' ' }.isEmpty()
                        && addStore.text.toString().trim { it <= ' ' }.isEmpty()) {
                    Toast.makeText(this@AlertListFragment.context, R.string.empty_alert, Toast.LENGTH_SHORT).show()
                    dialog.cancel()
                } else {
                    val alert = Alert(type, addItem.text.toString().trim { it <= ' ' }, addStore.text.toString().trim { it <= ' ' })
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        Log.d(javaClass.name, "Inserting $alert")
                        vm.insert(alert)
                    }
                    notificationMgr.notifyAlert(alert)
                }
            }
            dialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            dialogBuilder.show()
        }

    }

    private inner class RemoveAllAlertsClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            val dialogBuilder = AlertDialog.Builder(view.context)
            dialogBuilder.setTitle(getString(R.string.interogative_remove_all_alert))
            dialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            dialogBuilder.setPositiveButton(R.string.ok) { _, _ ->
                viewModel.removeAllAlerts()
                notificationMgr.removeAllNotifications()
            }
            dialogBuilder.show()
        }
    }
}