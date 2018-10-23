package ro.antiprotv.redalert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import ro.antiprotv.redalert.db.Alert;
import ro.antiprotv.redalert.db.RedAlertViewModel;

public class AddEditAlertDialog {

    public void showAdd(final Context ctx, final RedAlertViewModel redAlertViewModel, final NotificationManager notificationManager, final AlertListAdapter adapter, int level) {
        AlertDialog.Builder dialogBuilder = buildDialog(ctx,redAlertViewModel, notificationManager, adapter);
        dialogBuilder.setTitle(ctx.getResources().getString(R.string.add_new_alert, ctx.getResources().getString(Alert.getColorString(level)).toUpperCase()));
        dialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (inputItem.getText().toString().trim().isEmpty()
                        && inputStore.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AlertListActivity.this, R.string.empty_alert, Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                } else {
                    Alert alert = new Alert(level, inputItem.getText().toString().trim(), inputStore.getText().toString().trim());
                    long alertId = redAlertViewModel.insert(alert);
                    alert.setId(alertId);
                    notificationManager.notifyAlert(alert);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        dialogBuilder.show();
    }

    private AlertDialog.Builder buildDialog(final Context ctx, final RedAlertViewModel redAlertViewModel, final NotificationManager notificationManager, final AlertListAdapter adapter) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);

        LinearLayout layout = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.add_alert_dialog, null);

        final AutoCompleteTextView inputItem = layout.findViewById(R.id.add_item);
        final AutoCompleteTextView inputStore = layout.findViewById(R.id.add_store);
        inputItem.setHint(R.string.what);
        inputStore.setHint(R.string.where);

        ArrayAdapter autocompleteItemAdapter = new ItemListAdapter(ctx, android.R.layout.simple_list_item_1, new ArrayList<String>(), redAlertViewModel, true);
        inputItem.setAdapter(autocompleteItemAdapter);
        inputItem.setThreshold(2);
        inputItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                inputItem.setText(item);
                inputStore.setText(redAlertViewModel.selectStoresByItem(item));
                inputStore.setSelectAllOnFocus(true);
            }
        });

        ArrayAdapter autocompleteStoreAdapter = new ItemListAdapter(ctx, android.R.layout.simple_list_item_1, new ArrayList<String>(), redAlertViewModel, false);
        inputStore.setAdapter(autocompleteStoreAdapter);

        dialogBuilder.setView(layout);

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }
}
