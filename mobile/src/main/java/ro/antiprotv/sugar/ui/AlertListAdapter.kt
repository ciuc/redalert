package ro.antiprotv.sugar.ui

import android.app.AlertDialog
import android.content.Context
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.view.View.OnCreateContextMenuListener
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import org.koin.core.KoinComponent
import org.koin.core.get
import ro.antiprotv.sugar.R
import ro.antiprotv.sugar.repository.db.Alert
import ro.antiprotv.sugar.repository.db.AlertType
import ro.antiprotv.sugar.util.NotificationManager

/**
 * The list adapter used by the RecyclerView.
 * It deals with the display of the alerts into the layout,
 * the creation of the alert context menu - together with the handling of the action clicks
 */
class AlertListAdapter(private val context: Context, private val viewModel: AlertViewModel) : KoinComponent, RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val alerts: MutableList<Alert> = ArrayList()

    private val notificationMgr = get<NotificationManager>()

    private lateinit var holder: AlertViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): AlertViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.content_alert_list, parent, false)
        return AlertViewHolder(itemView, Alert())
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        holder = (viewHolder as AlertViewHolder)
        val res = viewHolder.itemView.context.resources
        holder.view.isLongClickable = true

        if (alerts.isNotEmpty()) {
            val alert = alerts[position]
            holder.alert = alert
            holder.iconView.setImageResource(alert.type.icon)
            holder.iconView.setColorFilter(res.getColor(alert.type.color, holder.view.context.theme))
            holder.alertView.text = alert.itemName
            holder.alertView.setTextColor(res.getColor(alert.type.color, holder.view.context.theme))
            if (alert.store != "") {
                holder.storeView.text = holder.view.context.getString(R.string.from, alert.store)
            } else {
                holder.storeView.text = ""
            }
            holder.editButton.setOnClickListener(EditAlertOnClickListener(alert))
        } else {
            holder.alertView.text = "n/a"
            holder.storeView.text = "n/a"
        }
    }

    override fun getItemCount() = alerts.size

    fun replaceAlerts(newAlerts: List<Alert>?) {
        newAlerts?.let {
            alerts.clear()
            alerts.addAll(it)
        }
    }

    fun selectedAlert(): Alert {
        return alerts[holder.adapterPosition]
    }

    inner class AlertViewHolder(val view: View, var alert: Alert) : RecyclerView.ViewHolder(view), OnCreateContextMenuListener {
        val iconView: ImageView = view.findViewById(R.id.image_alert_icon)
        val alertView: TextView = view.findViewById(R.id.adapter_alerts_text_item)
        val storeView: TextView = view.findViewById(R.id.adapter_alerts_text_store)
        val editButton: ImageButton = view.findViewById(R.id.adapter_alerts_button_edit_alert)

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
            //super.onCreateContextMenu(menu, v, menuInfo);
            menu.setHeaderTitle("Select The Action")
            val edit = menu.add(Menu.NONE, 1, 1, R.string.edit)
            val delete = menu.add(Menu.NONE, 2, 2, R.string.delete)
            val alert = alert
            val makeRed: MenuItem
            val makeOrange: MenuItem
            if (alert.type == AlertType.RED) {
                makeOrange = menu.add(Menu.NONE, 3, 3, R.string.make_orange)
                makeOrange.setOnMenuItemClickListener(OnClickMenu(this.alert, v))
            }
            if (alert.type == AlertType.ORANGE) {
                makeRed = menu.add(Menu.NONE, 4, 3, R.string.make_red)
                makeRed.setOnMenuItemClickListener(OnClickMenu(this.alert, v))
            }
            edit.setOnMenuItemClickListener(OnClickMenu(this.alert, v))
            delete.setOnMenuItemClickListener(OnClickMenu(this.alert, v))
        }

        private inner class OnClickMenu internal constructor(val alert: Alert, var v: View) : MenuItem.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                val ctx = view.context

                when (item.itemId) {
                    1 -> {
                        //TODO: REFACTOR/REUSE
                        val dialogBuilder = AlertDialog.Builder(ctx)
                        dialogBuilder.setTitle(v.resources.getString(R.string.edit_alert))
                        val layout = LayoutInflater.from(v.context).inflate(R.layout.add_alert_dialog, null) as LinearLayout
                        val inputItem = layout.findViewById<AutoCompleteTextView>(R.id.dialog_alert_autocomplete_add_item)
                        val inputStore = layout.findViewById<AutoCompleteTextView>(R.id.dialog_alert_autocomplete_add_store)
                        inputItem.setHint(R.string.what)
                        inputStore.setHint(R.string.where)
                        inputItem.setText(alert.itemName)
                        inputStore.setText(alert.store)
                        val autocompleteItemAdapter: ArrayAdapter<*> = ItemListAutocompleteAdapter(ctx, android.R.layout.simple_list_item_1, ArrayList(), viewModel)
                        inputItem.setAdapter(autocompleteItemAdapter)
                        inputItem.threshold = 2
                        inputItem.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                            val listItem: String = parent.getItemAtPosition(position).toString()
                            inputItem.setText(listItem)
                            inputStore.setText(viewModel.selectStoresByItem(listItem))
                            inputStore.setSelectAllOnFocus(true)
                        }
                        val autocompleteStoreAdapter: ArrayAdapter<*> = ItemListAutocompleteAdapter(ctx, android.R.layout.simple_list_item_1, ArrayList(), viewModel)
                        inputStore.setAdapter(autocompleteStoreAdapter)
                        dialogBuilder.setView(layout)
                        dialogBuilder.setPositiveButton(R.string.add) { dialog, _ ->
                            if ((inputItem.text.toString().trim { it <= ' ' }.isEmpty()
                                            && inputStore.text.toString().trim { it <= ' ' }.isEmpty())) {
                                Toast.makeText(ctx, R.string.empty_alert, Toast.LENGTH_SHORT).show()
                                dialog.cancel()
                            } else {
                                alert.itemName = inputItem.text.toString().trim { it <= ' ' }
                                alert.store = inputStore.text.toString().trim { it <= ' ' }
                                viewModel.update(alert)
                                notificationMgr.notifyAlert(alert)
                                notifyDataSetChanged()
                            }
                        }
                        dialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                        dialogBuilder.show()
                    }
                    2 -> viewModel.changeLevel(alert, AlertType.DELETED)
                    3 -> viewModel.changeLevel(alert, AlertType.ORANGE)
                    4 -> viewModel.changeLevel(alert, AlertType.RED)
                }
                notificationMgr.notifyAlert(alert)
                return true
            }

        }

        init {
            view.setOnCreateContextMenuListener(this)
        }
    }

    private inner class EditAlertOnClickListener(private val alert: Alert) : View.OnClickListener {
        override fun onClick(v: View) {
            //TODO: REFACTOR/REUSE
            val dialogBuilder = AlertDialog.Builder(v.context)
            dialogBuilder.setTitle(v.resources.getString(R.string.edit_alert))
            val layout = LayoutInflater.from(v.context).inflate(R.layout.add_alert_dialog, null) as LinearLayout
            val inputItem = layout.findViewById<AutoCompleteTextView>(R.id.dialog_alert_autocomplete_add_item)
            val inputStore = layout.findViewById<AutoCompleteTextView>(R.id.dialog_alert_autocomplete_add_store)
            inputItem.setHint(R.string.what)
            inputStore.setHint(R.string.where)
            inputItem.setText(alert.itemName)
            inputStore.setText(alert.store)
            val autocompleteItemAdapter: ArrayAdapter<*> = ItemListAutocompleteAdapter(v.context, android.R.layout.simple_list_item_1, ArrayList(), viewModel)
            inputItem.setAdapter(autocompleteItemAdapter)
            inputItem.threshold = 2
            inputItem.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                val item: String = parent.getItemAtPosition(position).toString()
                inputItem.setText(item)
                inputStore.setText(viewModel.selectStoresByItem(item))
                inputStore.setSelectAllOnFocus(true)
            }
            val autocompleteStoreAdapter: ArrayAdapter<*> = ItemListAutocompleteAdapter(v.context, android.R.layout.simple_list_item_1, ArrayList(), viewModel)
            inputStore.setAdapter(autocompleteStoreAdapter)
            dialogBuilder.setView(layout)
            dialogBuilder.setPositiveButton(R.string.add) { dialog, _ ->
                if ((inputItem.text.toString().trim { it <= ' ' }.isEmpty()
                                && inputStore.text.toString().trim { it <= ' ' }.isEmpty())) {
                    Toast.makeText(v.context, R.string.empty_alert, Toast.LENGTH_SHORT).show()
                    dialog.cancel()
                } else {
                    alert.itemName = inputItem.text.toString().trim { it <= ' ' }
                    alert.store = inputStore.text.toString().trim { it <= ' ' }
                    viewModel.update(alert)
                    notificationMgr.notifyAlert(alert)
                    notifyDataSetChanged()
                }
            }
            dialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            dialogBuilder.show()
        }

    }

}