package ro.antiprotv.sugar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ro.antiprotv.sugar.db.Alert;
import ro.antiprotv.sugar.db.Item;
import ro.antiprotv.sugar.db.RedAlertViewModel;

/**
 * The list adapter used by the RecyclerView.
 * It deals with the display of the alerts into the layout,
 * the creation of the alert context menu - together with the handling of the action clicks
 */
public class RecyclerView_AlertListAdapter extends RecyclerView.Adapter {
    private final LayoutInflater inflater;
    Logger logger = Logger.getLogger(RecyclerView_AlertListAdapter.class.getName());
    RedAlertViewModel viewModel;
    private List<Alert> alerts;

    public RecyclerView_AlertListAdapter(Context context, RedAlertViewModel viewModel) {
        this.viewModel = viewModel;
        inflater = LayoutInflater.from(context);
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View itemView = inflater.inflate(R.layout.content_alert_list, parent, false);
        return new AlertViewHolder(itemView, null);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        AlertViewHolder holder = ((AlertViewHolder) viewHolder);
        Resources res = viewHolder.itemView.getContext().getResources();
        holder.view.setLongClickable(true);
        if (alerts != null) {
            Alert alert = alerts.get(position);
            holder.alert = alert;
            holder.iconView.setImageResource(alert.getIcon());
            holder.iconView.setColorFilter(res.getColor(alert.getColor()));
            holder.itemView.setText(alert.getItemName());
            holder.itemView.setTextColor(res.getColor(alert.getColor()));
            if (alert.getStore() != null && !alert.getStore().equals("")) {
                holder.storeView.setText(holder.view.getContext().getString(R.string.from, alert.getStore()));
            } else {
                holder.storeView.setText("");
            }
            holder.editButton.setOnClickListener(new EditAlertOnClickListener(alert));
        } else {
            holder.itemView.setText("n/a");
            holder.storeView.setText("n/a");
        }
    }

    @Override
    public int getItemCount() {
        if (alerts != null) {
            return alerts.size();
        }
        return 0;
    }

    class AlertViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final ImageView iconView;
        private final TextView itemView;
        private final TextView storeView;
        private final ImageButton editButton;
        private final View view;

        private Alert alert;

        AlertViewHolder(View view, Alert alert) {
            super(view);
            this.alert = alert;
            this.view = view;
            this.itemView = view.findViewById(R.id.adapter_alerts_text_item);
            this.storeView = view.findViewById(R.id.adapter_alerts_text_store);
            this.iconView = view.findViewById(R.id.image_alert_icon);
            this.editButton = view.findViewById(R.id.adapter_alerts_button_edit_alert);

            this.view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            //super.onCreateContextMenu(menu, v, menuInfo);
            menu.setHeaderTitle("Select The Action");
            MenuItem edit = menu.add(Menu.NONE, 1, 1, R.string.edit);
            MenuItem delete = menu.add(Menu.NONE, 2, 2, R.string.delete);
            Alert alert = this.alert;
            MenuItem makeRed;
            MenuItem makeOrange;
            if (alert.getLevel() == Alert.RED_ALERT) {
                makeOrange = menu.add(Menu.NONE, 3, 3, R.string.make_orange);
                makeOrange.setOnMenuItemClickListener(new OnClickMenu(this.alert, v));
            }
            if (alert.getLevel() == Alert.ORANGE_ALERT) {
                makeRed = menu.add(Menu.NONE, 4, 3, R.string.make_red);
                makeRed.setOnMenuItemClickListener(new OnClickMenu(this.alert, v));
            }
            edit.setOnMenuItemClickListener(new OnClickMenu(this.alert, v));
            delete.setOnMenuItemClickListener(new OnClickMenu(this.alert, v));

        }

        private class OnClickMenu implements MenuItem.OnMenuItemClickListener {
            final Alert alert;
            View v;

            OnClickMenu(Alert alert, View v) {
                this.alert = alert;
                this.v = v;
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final Context ctx = view.getContext();
                //We assume the notification manager has already been initialized
                final NotificationManager notificationManager = NotificationManager.getInstance();
                switch (item.getItemId()) {
                    case 1:
                        //TODO: REFACTOR/REUSE
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
                        dialogBuilder.setTitle(v.getResources().getString(R.string.edit_alert));
                        LinearLayout layout = (LinearLayout) LayoutInflater.from(v.getContext()).inflate(R.layout.add_alert_dialog, null);

                        final AutoCompleteTextView inputItem = layout.findViewById(R.id.dialog_alert_autocomplete_add_item);
                        final AutoCompleteTextView inputStore = layout.findViewById(R.id.dialog_alert_autocomplete_add_store);
                        inputItem.setHint(R.string.what);
                        inputStore.setHint(R.string.where);
                        inputItem.setText(alert.getItemName());
                        inputStore.setText(alert.getStore());

                        ArrayAdapter autocompleteItemAdapter = new ItemListAutocompleteAdapter(ctx, android.R.layout.simple_list_item_1, new ArrayList<String>(), viewModel, true);
                        inputItem.setAdapter(autocompleteItemAdapter);
                        inputItem.setThreshold(2);
                        inputItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String item = parent.getItemAtPosition(position).toString();
                                inputItem.setText(item);
                                inputStore.setText(viewModel.selectStoresByItem(item));
                                inputStore.setSelectAllOnFocus(true);
                            }
                        });

                        ArrayAdapter autocompleteStoreAdapter = new ItemListAutocompleteAdapter(ctx, android.R.layout.simple_list_item_1, new ArrayList<String>(), viewModel, false);
                        inputStore.setAdapter(autocompleteStoreAdapter);


                        dialogBuilder.setView(layout);
                        dialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (inputItem.getText().toString().trim().isEmpty()
                                        && inputStore.getText().toString().trim().isEmpty()) {
                                    Toast.makeText(ctx, R.string.empty_alert, Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                } else {
                                    Item item = new Item();
                                    item.setName(inputItem.getText().toString().trim());
                                    alert.setItemName(inputItem.getText().toString().trim());
                                    alert.setStore(inputStore.getText().toString().trim());
                                    viewModel.update(alert);
                                    notificationManager.notifyAlert(alert);

                                    RecyclerView_AlertListAdapter.this.notifyDataSetChanged();
                                }
                            }
                        });
                        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        dialogBuilder.show();
                        break;
                    case 2:
                        viewModel.changeLevel(alert, Alert.GREEN_ALERT);
                        break;
                    case 3:
                        viewModel.changeLevel(alert, Alert.ORANGE_ALERT);
                        break;
                    case 4:
                        viewModel.changeLevel(alert, Alert.RED_ALERT);
                        break;
                }
                notificationManager.notifyAlert(alert);
                return true;
            }
        }

        ;
    }

    private class EditAlertOnClickListener implements View.OnClickListener {
        private final Alert alert;

        public EditAlertOnClickListener(Alert alert) {
            this.alert = alert;
        }

        @Override
        public void onClick(final View v) {
            final NotificationManager notificationManager = NotificationManager.getInstance();
            //TODO: REFACTOR/REUSE
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
            dialogBuilder.setTitle(v.getResources().getString(R.string.edit_alert));
            LinearLayout layout = (LinearLayout) LayoutInflater.from(v.getContext()).inflate(R.layout.add_alert_dialog, null);

            final AutoCompleteTextView inputItem = layout.findViewById(R.id.dialog_alert_autocomplete_add_item);
            final AutoCompleteTextView inputStore = layout.findViewById(R.id.dialog_alert_autocomplete_add_store);
            inputItem.setHint(R.string.what);
            inputStore.setHint(R.string.where);
            inputItem.setText(alert.getItemName());
            inputStore.setText(alert.getStore());

            ArrayAdapter autocompleteItemAdapter = new ItemListAutocompleteAdapter(v.getContext(), android.R.layout.simple_list_item_1, new ArrayList<String>(), viewModel, true);
            inputItem.setAdapter(autocompleteItemAdapter);
            inputItem.setThreshold(2);
            inputItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = parent.getItemAtPosition(position).toString();
                    inputItem.setText(item);
                    inputStore.setText(viewModel.selectStoresByItem(item));
                    inputStore.setSelectAllOnFocus(true);
                }
            });

            ArrayAdapter autocompleteStoreAdapter = new ItemListAutocompleteAdapter(v.getContext(), android.R.layout.simple_list_item_1, new ArrayList<String>(), viewModel, false);
            inputStore.setAdapter(autocompleteStoreAdapter);


            dialogBuilder.setView(layout);
            dialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (inputItem.getText().toString().trim().isEmpty()
                            && inputStore.getText().toString().trim().isEmpty()) {
                        Toast.makeText(v.getContext(), R.string.empty_alert, Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    } else {
                        Item item = new Item();
                        item.setName(inputItem.getText().toString().trim());
                        alert.setItemName(inputItem.getText().toString().trim());
                        alert.setStore(inputStore.getText().toString().trim());
                        viewModel.update(alert);
                        notificationManager.notifyAlert(alert);

                        RecyclerView_AlertListAdapter.this.notifyDataSetChanged();
                    }
                }
            });
            dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            dialogBuilder.show();
        }
    }
}
