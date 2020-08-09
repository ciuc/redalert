package ro.antiprotv.sugar.ui

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import ro.antiprotv.sugar.R
import ro.antiprotv.sugar.repository.db.Category
import ro.antiprotv.sugar.repository.db.Item
import ro.antiprotv.sugar.repository.db.MeasureUnit

class AddEditItemDialog(
        ctx: Context,
        v: View,
        viewModel: ItemViewModel,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        item: Item
) : AlertDialog.Builder(ctx) {
    private var scrollView: ScrollView

    init {
        setTitle(ctx.getString(R.string.add_new_item))
        val layout = LayoutInflater.from(v.context).inflate(R.layout.add_item_dialog, null) as LinearLayout
        scrollView = layout.findViewById(R.id.dialog_item_scrollView)
        val inputItem = layout.findViewById<TextView>(R.id.dialog_item_textinput_add_item)
        val inputQuantity = layout.findViewById<TextView>(R.id.dialog_item_textinput_quantity)
        val unitDropdown = layout.findViewById<Spinner>(R.id.dialog_item_dropdown_unit)
        val categoryDropdown = layout.findViewById<Spinner>(R.id.dialog_item_dropdown_category)
        val inputAlertAtQuantity = layout.findViewById<TextView>(R.id.dialog_item_textinput_dialog_item_alert_notification_quantity_left_input)
        val checkAutoRemoveNotification = layout.findViewById<CheckBox>(R.id.dialog_item_checkbox_auto_remove_alert)
        inputItem.setHint(R.string.what)
        inputQuantity.setHint(R.string.how_many)
        inputItem.text = item.name
        inputQuantity.text = item.quantity.toString()
        unitDropdown.setSelection((unitDropdown.adapter as ArrayAdapter<String>).getPosition(MeasureUnit.fromCode(item.measureUnit.code)))
        categoryDropdown.setSelection((categoryDropdown.adapter as ArrayAdapter<String>).getPosition(Category.fromCode(item.category.code).toString()))
        inputAlertAtQuantity.text = item.quantityLeftAlert.toString()
        checkAutoRemoveNotification.isChecked = item.autoRemoveAlert
        setView(layout)
        setPositiveButton(R.string.add) { _, _ ->
            if (inputItem.text.toString().trim { it <= ' ' }.isEmpty()) {
                Toast.makeText(ctx, R.string.empty_item, Toast.LENGTH_SHORT).show()
            } else {
                var quantity = 0
                try {
                    quantity = inputQuantity.text.toString().trim { it <= ' ' }.toInt()
                } catch (e: Exception) {
                    Toast.makeText(ctx, R.string.error_quantity_must_be_number, Toast.LENGTH_SHORT).show()
                }
                val unit = MeasureUnit.fromString(unitDropdown.selectedItem.toString())
                val category = Category.fromString(categoryDropdown.selectedItem.toString())
                var alertNotifyAt = 0
                try {
                    if (inputAlertAtQuantity.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                        alertNotifyAt = inputAlertAtQuantity.text.toString().trim { it <= ' ' }.toInt()
                    }
                } catch (e: Exception) {
                    //do nothing
                }
                val newItem = Item(inputItem.text.toString().trim { it <= ' ' }, quantity, unit, category, alertNotifyAt, checkAutoRemoveNotification.isChecked)
                viewModel.insert(newItem)
                adapter.notifyDataSetChanged()
            }
        }
        setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
        setNeutralButton("HELP ME!", null)
    }

    fun setOnShowListener(dialog: AlertDialog) {
        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
            button.setOnClickListener {
                // TODO Do something
                scrollView.visibility = View.VISIBLE
                //Dismiss once everything is OK.
                //dialog.dismiss();
            }
        }
    }
}