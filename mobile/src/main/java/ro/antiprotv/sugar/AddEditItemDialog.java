package ro.antiprotv.sugar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import ro.antiprotv.sugar.db.Category;
import ro.antiprotv.sugar.db.Item;
import ro.antiprotv.sugar.db.MeasureUnit;
import ro.antiprotv.sugar.db.RedAlertViewModel;

public class AddEditItemDialog extends AlertDialog.Builder {
    ScrollView scrollView;

    protected AddEditItemDialog(Context context) {
        super(context);
    }

    public AddEditItemDialog(final Context ctx, View v, final RedAlertViewModel viewModel, final RecyclerView.Adapter adapter, final Item item) {
        super(ctx);

        setTitle(ctx.getString(R.string.add_new_item));
        LinearLayout layout = (LinearLayout) LayoutInflater.from(v.getContext()).inflate(R.layout.add_item_dialog, null);
        this.scrollView = layout.findViewById(R.id.dialog_item_scrollView);
        final TextView inputItem = layout.findViewById(R.id.dialog_item_textinput_add_item);
        final TextView inputQuantity = layout.findViewById(R.id.dialog_item_textinput_quantity);
        final Spinner unitDropdown = layout.findViewById(R.id.dialog_item_dropdown_unit);
        final Spinner categoryDropdown = layout.findViewById(R.id.dialog_item_dropdown_category);
        final TextView inputAlertAtQuantity = layout.findViewById(R.id.dialog_item_textinput_dialog_item_alert_notification_quantity_left_input);
        final CheckBox checkAutoremoveNotification = layout.findViewById(R.id.dialog_item_checkbox_auto_remove_alert);
        inputItem.setHint(R.string.what);
        inputQuantity.setHint(R.string.how_many);

        if (item != null) {
            inputItem.setText(item.getName());
            inputQuantity.setText(new Integer(item.getQuantity()).toString());
            unitDropdown.setSelection(((ArrayAdapter) unitDropdown.getAdapter()).getPosition(MeasureUnit.fromCode(item.getMeasureUnit())));
            categoryDropdown.setSelection( ((ArrayAdapter) categoryDropdown.getAdapter()).getPosition(Category.fromCode(item.getCategory())));
            inputAlertAtQuantity.setText(new Long(item.getQuantityLeftAlert()).toString());
            checkAutoremoveNotification.setChecked(item.isAutoRemoveAlert());
        }

        setView(layout);
        setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (inputItem.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ctx, R.string.empty_item, Toast.LENGTH_SHORT).show();
                } else {
                    int quantity = 0;
                    try {
                        quantity = new Integer(inputQuantity.getText().toString().trim());
                    } catch (Exception e) {
                        Toast.makeText(ctx, R.string.error_quantity_must_be_number, Toast.LENGTH_SHORT).show();
                    }
                    int unit = MeasureUnit.getCodeFromString(unitDropdown.getSelectedItem().toString());
                    int category = Category.getCodeFromString(categoryDropdown.getSelectedItem().toString());
                    int alertNotifyAt = 0;
                    try {
                        if (!inputAlertAtQuantity.getText().toString().trim().isEmpty()) {
                            alertNotifyAt = new Integer(inputAlertAtQuantity.getText().toString().trim());
                        }
                    } catch (Exception e) {
                        //do nothing
                    }
                    Item newItem = new Item(inputItem.getText().toString().trim(), quantity, unit, category, alertNotifyAt, checkAutoremoveNotification.isChecked());
                    if (item != null) {
                        newItem.setId(item.getId());
                    }
                    long itemId = viewModel.saveOrUpdate(newItem);

                    adapter.notifyDataSetChanged();
                }
            }
        });
        setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        setNeutralButton("HELP ME!", null);
    }

    public void setOnShowListener(final AlertDialog dialog) {
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        AddEditItemDialog.this.scrollView.setVisibility(View.VISIBLE);
                        //Dismiss once everything is OK.
                        //dialog.dismiss();
                    }
                });
            }
        });
    }

}

