package ro.antiprotv.sugar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.util.List;

import ro.antiprotv.sugar.db.Alert;
import ro.antiprotv.sugar.db.Item;
import ro.antiprotv.sugar.db.RedAlertViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class ItemListFragment extends Fragment {

    RecyclerView_ItemListAdapter adapter;
    private RedAlertViewModel redAlertViewModel;
    private RecyclerView recyclerView;

    public static ItemListFragment newInstance() {
        ItemListFragment fragment = new ItemListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * this displays the "empty" view if there are no items, and hides it if there are items.
     *
     * @param items
     * @param recyclerView
     * @param noItemsView
     */
    private void toggleItemListVisibility(List<Item> items, View recyclerView, View noItemsView) {
        if (items != null && !items.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            noItemsView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            noItemsView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_item_list, container, false);

        redAlertViewModel = ViewModelProviders.of(this).get(RedAlertViewModel.class);
        adapter = new RecyclerView_ItemListAdapter(this.getContext(), redAlertViewModel);
        recyclerView = root.findViewById(R.id.recyclerview_item_list);
        final TextView noItemsView = root.findViewById(R.id.recyclerview_item_list_empty_view);

        final LiveData<List<Item>> items = redAlertViewModel.getAllItems();

        toggleItemListVisibility(items.getValue(), recyclerView, noItemsView);

        adapter.setItems(items.getValue());
        redAlertViewModel.getAllItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable final List<Item> items) {
                adapter.setItems(items);
                adapter.notifyDataSetChanged();
                toggleItemListVisibility(items, recyclerView, noItemsView);
            }
        });

        FloatingActionButton addRedFab = (FloatingActionButton) root.findViewById(R.id.button_item_list_add_item);
        addRedFab.setOnClickListener(new ItemListFragment.AddItemClickListener());

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
                    Item item = items.getValue().get(viewHolder.getAdapterPosition());
                    Alert alert = redAlertViewModel.createAlert(item);
                    if (alert.getLevel() == Alert.GREEN_ALERT) {
                        redAlertViewModel.changeLevel(alert, Alert.ORANGE_ALERT);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return root;
    }

    /**
     * The click listener that creates the item dialog to add items.
     */
    private class AddItemClickListener implements View.OnClickListener {
        private int level;

        @Override
        public void onClick(View v) {
            AddEditItemDialog dialogBuilder = new AddEditItemDialog(ItemListFragment.this.getContext(), v, redAlertViewModel, adapter, null);
            final AlertDialog dialog = dialogBuilder.create();
            dialogBuilder.setOnShowListener(dialog);
            dialog.show();
        }
    }

    private class RemoveAllItemsClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
            dialogBuilder.setTitle(getString(R.string.interogative_remove_all_item));
            dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface v, int which) {
                    redAlertViewModel.removeAllItems();

                    adapter.notifyDataSetChanged();

                }
            });
            dialogBuilder.show();
        }
    }
}