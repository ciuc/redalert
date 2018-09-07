package ro.antiprotv.redalert;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ro.antiprotv.redalert.db.Alert;
import ro.antiprotv.redalert.db.RedAlertViewModel;

/**
 * The main activity (entry point) of the Red Alert! app
 */
public class AlertListActivity extends AppCompatActivity {
    public static final String RED_ALERT_CHANNEL = "RED_ALERT_CHANNEL";
    AlertListAdapter adapter;
    NotificationManagerCompat notificationManager;
    private RedAlertViewModel redAlertViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notificationManager = NotificationManagerCompat.from(this);
        redAlertViewModel = ViewModelProviders.of(this).get(RedAlertViewModel.class);
        adapter = new AlertListAdapter(this, redAlertViewModel);
        final RecyclerView recyclerView = findViewById(R.id.alert_list_view);
        final TextView noAlertsView = findViewById(R.id.empty_view);

        LiveData<List<Alert>> alerts = redAlertViewModel.getAllAlerts();

        toggleAlertListVisibility(alerts.getValue(), recyclerView, noAlertsView);

        adapter.setAlerts(alerts.getValue());
        redAlertViewModel.getAllAlerts().observe(this, new Observer<List<Alert>>() {
            @Override
            public void onChanged(@Nullable final List<Alert> alerts) {
                adapter.setAlerts(alerts);
                adapter.notifyDataSetChanged();
                toggleAlertListVisibility(alerts, recyclerView, noAlertsView);
            }
        });

        FloatingActionButton addRedFab = (FloatingActionButton) findViewById(R.id.add_red);
        addRedFab.setOnClickListener(new AddAlertClickListener(Alert.RED_ALERT));
        FloatingActionButton addOrangeFab = (FloatingActionButton) findViewById(R.id.add_orange);
        addOrangeFab.setOnClickListener(new AddAlertClickListener(Alert.ORANGE_ALERT));
        FloatingActionButton addYellowFab = (FloatingActionButton) findViewById(R.id.add_yellow);
        addYellowFab.setOnClickListener(new AddAlertClickListener(Alert.YELLOW_ALERT));
        FloatingActionButton removeAllFab = (FloatingActionButton) findViewById(R.id.remove_all);
        removeAllFab.setOnClickListener(new RemoveAllAlertsClickListener());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(RED_ALERT_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void removeAllNotifications() {
        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancelAll();
    }

    /**
     * this displays the "empty" view if there are no alerts, and hides it if there are alerts.
     * @param alerts
     * @param recyclerView
     * @param noAlertsView
     */
    private void toggleAlertListVisibility(List<Alert> alerts, View recyclerView, View noAlertsView) {
        if (alerts != null && !alerts.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            noAlertsView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            noAlertsView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alert_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent about = new Intent();
            about.setClassName(this, "ro.antiprotv.redalert.AboutActivity");
            startActivity(about);
            return true;
        }
        if (id == R.id.action_help) {
            Intent about = new Intent();
            about.setClassName(this, "ro.antiprotv.redalert.HelpActivity");
            startActivity(about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * The click listener that creates the alert dialog to add alerts.
     * It takes the alert level (color) as a param to know what it needs to add.
     * The dialog itself has the logic of autocomplete.
     */
    private class AddAlertClickListener implements View.OnClickListener {
        private int level;

        AddAlertClickListener(int level) {
            this.level = level;
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AlertListActivity.this);
            dialogBuilder.setTitle(getString(R.string.add_new_alert, Alert.getColor(level).toUpperCase()));
            LinearLayout layout = (LinearLayout) LayoutInflater.from(v.getContext()).inflate(R.layout.add_alert_dialog, null);

            final AutoCompleteTextView inputItem = layout.findViewById(R.id.add_item);
            final AutoCompleteTextView inputStore = layout.findViewById(R.id.add_store);
            inputItem.setHint(R.string.what);
            inputStore.setHint(R.string.where);

            ArrayAdapter autocompleteItemAdapter = new ItemListAdapter(AlertListActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>(), redAlertViewModel, true);
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

            ArrayAdapter autocompleteStoreAdapter = new ItemListAdapter(AlertListActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>(), redAlertViewModel, false);
            inputStore.setAdapter(autocompleteStoreAdapter);


            dialogBuilder.setView(layout);
            dialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RedAlertViewModel vm = new RedAlertViewModel(AlertListActivity.this.getApplication());
                    Alert alert = new Alert(level, inputItem.getText().toString(), inputStore.getText().toString());
                    vm.insert(alert);
                    adapter.notifyDataSetChanged();
                }
            });
            dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            //alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            dialogBuilder.show();
        }
    }

    private class RemoveAllAlertsClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AlertListActivity.this);
            dialogBuilder.setTitle(getString(R.string.interogative_remove_all_alert));
            dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface v, int which) {
                    redAlertViewModel.removeAllAlerts();
                    adapter.setAlerts(null);
                    adapter.notifyDataSetChanged();
                    removeAllNotifications();
                }
            });
            dialogBuilder.show();
        }

    }
}
