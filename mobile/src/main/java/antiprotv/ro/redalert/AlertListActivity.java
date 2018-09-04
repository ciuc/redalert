package antiprotv.ro.redalert;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.List;

import antiprotv.ro.redalert.db.Alert;
import antiprotv.ro.redalert.db.RedAlertViewModel;

public class AlertListActivity extends AppCompatActivity {
    private RedAlertViewModel redAlertViewModel;
    AlertListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        redAlertViewModel = ViewModelProviders.of(this).get(RedAlertViewModel.class);
        adapter= new AlertListAdapter(this);
        adapter.setAlerts(redAlertViewModel.getAllAlerts().getValue());
        redAlertViewModel.getAllAlerts().observe(this, new Observer<List<Alert>>() {
            @Override
            public void onChanged(@Nullable final List<Alert> alerts) {
                // Update the cached copy of the words in the adapter.
                adapter.setAlerts(alerts);
                adapter.notifyDataSetChanged();
            }
        });

        FloatingActionButton addRedFab = (FloatingActionButton) findViewById(R.id.add_red);
        addRedFab.setOnClickListener(new AddAlertClickListener(Alert.RED_ALERT));
        FloatingActionButton addOrangeFab = (FloatingActionButton) findViewById(R.id.add_orange);
        addOrangeFab.setOnClickListener(new AddAlertClickListener(Alert.ORANGE_ALERT));
        FloatingActionButton addYellowFab = (FloatingActionButton) findViewById(R.id.add_yellow);
        addYellowFab.setOnClickListener(new AddAlertClickListener(Alert.YELLOW_ALERT));

        RecyclerView recyclerView = findViewById(R.id.alert_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FloatingActionButton removeAllFab = (FloatingActionButton) findViewById(R.id.remove_all);
        removeAllFab.setOnClickListener(new View.OnClickListener(

        ) {
            @Override
            public void onClick(View v) {
                redAlertViewModel.removeAllAlerts();

            }
        });
    }


    private class AddAlertClickListener implements View.OnClickListener {
        private int level;

        AddAlertClickListener(int level) {
            this.level = level;
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AlertListActivity.this);
            builder.setTitle("Add New Alert: " + Alert.getColor(level));
            LinearLayout layout = new LinearLayout(AlertListActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText inputItem = new EditText(AlertListActivity.this);
            inputItem.setInputType(InputType.TYPE_CLASS_TEXT);
            layout.addView(inputItem);

            final EditText inputStore = new EditText(AlertListActivity.this);
            inputItem.setInputType(InputType.TYPE_CLASS_TEXT);
            layout.addView(inputStore);

            builder.setView(layout);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RedAlertViewModel vm = new RedAlertViewModel(AlertListActivity.this.getApplication());
                    Alert alert = new Alert(level, inputItem.getText().toString(), inputStore.getText().toString());
                    vm.insert(alert);
                    adapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
