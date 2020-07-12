package ro.antiprotv.sugar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ro.antiprotv.sugar.db.Alert;
import ro.antiprotv.sugar.db.RedAlertViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class AlertListFragment extends Fragment {


    RecyclerView_AlertListAdapter adapter;
    NotificationManager notificationManager;
    private RedAlertViewModel redAlertViewModel;
    private RecyclerView recyclerView;

    public static AlertListFragment newInstance() {
        AlertListFragment fragment = new AlertListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_alert_list);

    }


    /**
     * this displays the "empty" view if there are no alerts, and hides it if there are alerts.
     *
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
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_alert_list, container, false);

        notificationManager = NotificationManager.getInstance();
        notificationManager.init(this.getContext());
        redAlertViewModel = ViewModelProviders.of(this).get(RedAlertViewModel.class);
        adapter = new RecyclerView_AlertListAdapter(this.getContext(), redAlertViewModel);
        recyclerView = root.findViewById(R.id.recyclerview_alert_list);
        final TextView noAlertsView = root.findViewById(R.id.recyclerview_alert_list_empty_view);

        final LiveData<List<Alert>> alerts = redAlertViewModel.getAllAlerts();

        toggleAlertListVisibility(alerts.getValue(), recyclerView, noAlertsView);

        adapter.setAlerts(alerts.getValue());
        redAlertViewModel.getAllAlerts().observe(this, new Observer<List<Alert>>() {
            @Override
            public void onChanged(@Nullable final List<Alert> alerts) {
                adapter.setAlerts(alerts);
                adapter.notifyDataSetChanged();
                toggleAlertListVisibility(alerts, recyclerView, noAlertsView);
                notificationManager.reissueAllAlerts(alerts);
            }
        });

        FloatingActionButton addRedFab = (FloatingActionButton) root.findViewById(R.id.button_alert_list_add_red);
        addRedFab.setOnClickListener(new AlertListFragment.AddAlertClickListener(Alert.RED_ALERT));
        FloatingActionButton addOrangeFab = (FloatingActionButton) root.findViewById(R.id.button_alert_list_add_orange);
        addOrangeFab.setOnClickListener(new AlertListFragment.AddAlertClickListener(Alert.ORANGE_ALERT));
        FloatingActionButton removeAllFab = (FloatingActionButton) root.findViewById(R.id.button_alert_list_remove_all);
        removeAllFab.setOnClickListener(new AlertListFragment.RemoveAllAlertsClickListener());

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                if (swipeDir == ItemTouchHelper.LEFT) {
                    Alert alert = alerts.getValue().get(viewHolder.getAdapterPosition());
                    redAlertViewModel.changeLevel(alert, Alert.GREEN_ALERT);
                    adapter.notifyDataSetChanged();
                    notificationManager.notifyAlert(alert);
                } else if (swipeDir == ItemTouchHelper.RIGHT) {
                    Alert alert = alerts.getValue().get(viewHolder.getAdapterPosition());
                    alert.demote();
                    redAlertViewModel.update(alert);
                    adapter.notifyDataSetChanged();
                    notificationManager.notifyAlert(alert);
                }
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

        return root;
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
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AlertListFragment.this.getContext());
            dialogBuilder.setTitle(getString(R.string.add_new_alert, getResources().getString(Alert.getColorString(level)).toUpperCase()));
            LinearLayout layout = (LinearLayout) LayoutInflater.from(v.getContext()).inflate(R.layout.add_alert_dialog, null);

            final AutoCompleteTextView inputItem = layout.findViewById(R.id.dialog_alert_autocomplete_add_item);
            final AutoCompleteTextView inputStore = layout.findViewById(R.id.dialog_alert_autocomplete_add_store);
            inputItem.setHint(R.string.what);
            inputStore.setHint(R.string.where);

            ArrayAdapter autocompleteItemAdapter = new ItemListAutocompleteAdapter(AlertListFragment.this.getContext(), android.R.layout.simple_list_item_1, new ArrayList<String>(), redAlertViewModel, true);
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

            ArrayAdapter autocompleteStoreAdapter = new ItemListAutocompleteAdapter(AlertListFragment.this.getContext(), android.R.layout.simple_list_item_1, new ArrayList<String>(), redAlertViewModel, false);
            inputStore.setAdapter(autocompleteStoreAdapter);

            dialogBuilder.setView(layout);
            dialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RedAlertViewModel vm = new RedAlertViewModel(AlertListFragment.this.getActivity().getApplication());
                    if (inputItem.getText().toString().trim().isEmpty()
                            && inputStore.getText().toString().trim().isEmpty()) {
                        Toast.makeText(AlertListFragment.this.getContext(), R.string.empty_alert, Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    } else {
                        Alert alert = new Alert(level, inputItem.getText().toString().trim(), inputStore.getText().toString().trim());
                        long alertId = vm.insert(alert);
                        alert.setId(alertId);
                        notificationManager.notifyAlert(alert);
                        adapter.notifyDataSetChanged();
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

    private class RemoveAllAlertsClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
            dialogBuilder.setTitle(getString(R.string.interogative_remove_all_alert));
            dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface v, int which) {
                    redAlertViewModel.removeAllAlerts();
                    adapter.setAlerts(null);
                    adapter.notifyDataSetChanged();
                    notificationManager.removeAllNotifications();
                }
            });
            dialogBuilder.show();
        }
    }
}