package ro.antiprotv.redalert;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ro.antiprotv.redalert.db.Alert;
import ro.antiprotv.redalert.db.RedAlertViewModel;

/**
 * The list adapter used by the RecyclerView.
 * It deals with the display of the alerts into the layout,
 * the creation of the alert context menu - together with the handling of the action clicks
 */
public class AlertListAdapter extends RecyclerView.Adapter {
    private final LayoutInflater inflater;
    Logger logger = Logger.getLogger(AlertListAdapter.class.getName());
    RedAlertViewModel viewModel;
    private List<Alert> alerts;

    AlertListAdapter(Context context, RedAlertViewModel viewModel) {
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
            holder.itemView.setText(alert.getItem());
            holder.itemView.setTextColor(res.getColor(alert.getColor()));
            if (alert.getStore() != null && !alert.getStore().equals("")) {
                holder.storeView.setText(holder.view.getContext().getString(R.string.from, alert.getStore()));
            } else {
                holder.storeView.setText("");
            }
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
        private final View view;

        private Alert alert;

        AlertViewHolder(View view, Alert alert) {
            super(view);
            this.alert = alert;
            this.view = view;
            this.itemView = view.findViewById(R.id.item);
            this.storeView = view.findViewById(R.id.store);
            this.iconView = view.findViewById(R.id.alert_icon);


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
            MenuItem makeYellow;
            MenuItem makeOrange;
            if (alert.getLevel() == Alert.RED_ALERT) {
                makeOrange = menu.add(Menu.NONE, 3, 3, R.string.make_orange);
                makeYellow = menu.add(Menu.NONE, 4, 4, R.string.make_yellow);
                makeOrange.setOnMenuItemClickListener(new OnClickMenu(this.alert, v));
                makeYellow.setOnMenuItemClickListener(new OnClickMenu(this.alert, v));
            }
            if (alert.getLevel() == Alert.ORANGE_ALERT) {
                makeRed = menu.add(Menu.NONE, 5, 3, R.string.make_red);
                makeYellow = menu.add(Menu.NONE, 4, 4, R.string.make_yellow);
                makeRed.setOnMenuItemClickListener(new OnClickMenu(this.alert, v));
                makeYellow.setOnMenuItemClickListener(new OnClickMenu(this.alert, v));
            }
            if (alert.getLevel() == Alert.YELLOW_ALERT) {
                makeRed = menu.add(Menu.NONE, 5, 3, R.string.make_red);
                makeOrange = menu.add(Menu.NONE, 3, 4, R.string.make_orange);
                makeOrange.setOnMenuItemClickListener(new OnClickMenu(this.alert, v));
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
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
                switch (item.getItemId()) {
                    case 1:
                        //TODO: REFACTOR/REUSE
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
                        dialogBuilder.setTitle(v.getResources().getString(R.string.add_new_alert, v.getResources().getString(Alert.getColorString(alert.getLevel())).toUpperCase()));
                        LinearLayout layout = (LinearLayout) LayoutInflater.from(v.getContext()).inflate(R.layout.add_alert_dialog, null);

                        final AutoCompleteTextView inputItem = layout.findViewById(R.id.add_item);
                        final AutoCompleteTextView inputStore = layout.findViewById(R.id.add_store);
                        inputItem.setHint(R.string.what);
                        inputStore.setHint(R.string.where);
                        inputItem.setText(alert.getItem());
                        inputStore.setText(alert.getStore());

                        ArrayAdapter autocompleteItemAdapter = new ItemListAdapter(ctx, android.R.layout.simple_list_item_1, new ArrayList<String>(), viewModel, true);
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

                        ArrayAdapter autocompleteStoreAdapter = new ItemListAdapter(ctx, android.R.layout.simple_list_item_1, new ArrayList<String>(), viewModel, false);
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
                                    alert.setItem(inputItem.getText().toString().trim());
                                    alert.setStore(inputStore.getText().toString().trim());
                                    viewModel.update(alert);
                                    //build the intent to trigger the app on notification click
                                    Intent listActivity = new Intent(ctx, AlertListActivity.class);
                                    PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                                            ctx, 0, listActivity, PendingIntent.FLAG_UPDATE_CURRENT);
                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx, AlertListActivity.RED_ALERT_CHANNEL)
                                            .setSmallIcon(alert.getIcon())
                                            .setContentTitle(alert.getItem())
                                            .setContentText(alert.getStore())
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                            .setContentIntent(notifyPendingIntent)
                                            .setColor(v.getResources().getColor(alert.getColor()));
                                    notificationManager.notify((int) 0, mBuilder.build());
                                    AlertListAdapter.this.notifyDataSetChanged();
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
                        notificationManager.cancel((int) this.alert.getId());
                        break;
                    case 3:
                        viewModel.changeLevel(alert, Alert.ORANGE_ALERT);
                        updateNotification(notificationManager, ctx, alert, Alert.ORANGE_ALERT);
                        break;
                    case 4:
                        viewModel.changeLevel(alert, Alert.YELLOW_ALERT);
                        updateNotification(notificationManager, ctx, alert, Alert.YELLOW_ALERT);
                        break;
                    case 5:
                        viewModel.changeLevel(alert, Alert.RED_ALERT);
                        updateNotification(notificationManager, ctx, alert, Alert.RED_ALERT);
                        break;
                }
                return true;
            }

            private void updateNotification(NotificationManagerCompat notificationManager, Context ctx, Alert alert, int level) {
                Intent listActivity = new Intent(ctx, AlertListActivity.class);
                PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                        ctx, 0, listActivity, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(view.getContext(), AlertListActivity.RED_ALERT_CHANNEL)
                        .setSmallIcon(alert.getIcon())
                        .setContentTitle(alert.getItem())
                        .setContentText(alert.getStore())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentIntent(notifyPendingIntent)
                        .setColor(view.getResources().getColor(Alert.getColor(level)));
                notificationManager.notify((int) alert.getId(), mBuilder.build());
            }
        }

        ;


    }
}
