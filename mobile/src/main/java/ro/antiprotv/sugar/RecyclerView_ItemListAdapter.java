package ro.antiprotv.sugar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
 * It deals with the display of the items into the layout,
 * the creation of the item context menu - together with the handling of the action clicks
 */
public class RecyclerView_ItemListAdapter extends RecyclerView.Adapter implements Filterable {
    final NotificationManager notificationManager = NotificationManager.getInstance();
    private final LayoutInflater inflater;
    Logger logger = Logger.getLogger(RecyclerView_ItemListAdapter.class.getName());
    RedAlertViewModel viewModel;
    private List<Item> items;
    private ItemViewHolder viewHolder;
    private boolean filtered = false;
    private RecyclerView_ItemListAdapter.ListFilter listFilter = new ListFilter();

    public RecyclerView_ItemListAdapter(Context context, RedAlertViewModel viewModel) {
        this.viewModel = viewModel;
        inflater = LayoutInflater.from(context);
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View itemView = inflater.inflate(R.layout.content_item_list, parent, false);
        this.viewHolder = new ItemViewHolder(itemView, null);
        return this.viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ItemViewHolder holder = ((ItemViewHolder) viewHolder);
        Resources res = viewHolder.itemView.getContext().getResources();
        holder.itemView.setLongClickable(true);
        if (items != null) {
            final Item item = items.get(position);
            holder.item = item;
            holder.itemView.setText(item.getName());
            holder.quantityView.setText(Integer.valueOf(item.getQuantity()).toString());
            int muResId = res.getIdentifier("item.units." + item.getMeasureUnit(), "string", this.getClass().getPackage().getName());
            holder.unitView.setText(res.getString(muResId));
            holder.addButton.setOnClickListener(new AddQuantityOnClickListener(item));
            holder.subtractButton.setOnClickListener(new SubtractQuantityOnClickListener(item));
            holder.editButton.setOnClickListener(new EditItemOnClickListener(item));
            int categoryResId = res.getIdentifier("item.categories." + item.getCategory(), "string", this.getClass().getPackage().getName());
            String category = res.getString(categoryResId);
            holder.category.setText("[" + category + "]");
            holder.category.setOnClickListener(new FilterByCategoryOnClickListener(item.getCategory()));
        } else {
            holder.itemView.setText("n/a");
        }

    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return listFilter;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconView;
        private final TextView itemView;
        private final TextView quantityView;
        private final TextView unitView;
        private final ImageButton addButton;
        private final ImageButton subtractButton;
        private final ImageButton editButton;
        private final TextView category;
        private final ImageButton filterImage;
        private final View view;
        private Item item;

        ItemViewHolder(View view, final Item item) {
            super(view);
            this.item = item;
            this.view = view;
            this.itemView = view.findViewById(R.id.adapter_items_text_item_name);
            this.quantityView = view.findViewById(R.id.adapter_items_text_quantity);
            this.iconView = view.findViewById(R.id.adapter_item_image_item_icon);
            this.unitView = view.findViewById(R.id.adapter_item_text_unit);
            this.addButton = view.findViewById(R.id.adapter_items_button_add_quantity);
            this.subtractButton = view.findViewById(R.id.adapter_items_button_subtract_quantity);
            this.editButton = view.findViewById(R.id.adapter_items_button_edit_item);
            this.category = view.findViewById(R.id.adapter_items_text_category);
            this.filterImage = view.findViewById(R.id.adapter_items_button_filterByCategory);
        }

        public ImageButton getFilterImage() {
            return filterImage;
        }
    }

    private class AddQuantityOnClickListener implements View.OnClickListener {
        private final Item item;

        public AddQuantityOnClickListener(Item item) {
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            viewModel.addQuantity(item);
            notifyDataSetChanged();
            if (item.isAutoRemoveAlert() && item.getQuantity() > item.getQuantityLeftAlert()) {
                Alert alert = viewModel.removeAlert(item);
                if (alert != null) {
                    notificationManager.cancelNotification(alert);
                }
            }
        }
    }

    private class SubtractQuantityOnClickListener implements View.OnClickListener {
        private final Item item;

        public SubtractQuantityOnClickListener(Item item) {
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            viewModel.subtractQuantity(item);
            notifyDataSetChanged();
            if (item.getQuantity() == item.getQuantityLeftAlert()) {
                Alert alert = viewModel.createAlert(item);
                if (alert.getLevel() == Alert.GREEN_ALERT || alert.getLevel() == Alert.ORANGE_ALERT) {
                    viewModel.changeLevel(alert, Alert.RED_ALERT);
                }
            }
        }
    }

    private class EditItemOnClickListener implements View.OnClickListener {
        private final Item item;

        public EditItemOnClickListener(Item item) {
            this.item = item;
        }

        @Override
        public void onClick(final View v) {
            AddEditItemDialog dialogBuilder = new AddEditItemDialog(v.getContext(), v, viewModel, RecyclerView_ItemListAdapter.this, item);
            final AlertDialog dialog = dialogBuilder.create();
            dialogBuilder.setOnShowListener(dialog);
            dialog.show();
        }
    }

    private class FilterByCategoryOnClickListener implements View.OnClickListener {
        private int category;

        FilterByCategoryOnClickListener(int category) {
            this.category = category;
        }

        @Override
        public void onClick(final View v) {
            if (!filtered) {
                listFilter.publishResults("", listFilter.performFiltering(Integer.toString(category)));
                filtered = true;
            } else {
                listFilter.publishResults("", listFilter.performFiltering("1000"));
                filtered = false;
            }

        }
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence categoryCode) {
            FilterResults results = new FilterResults();

            if (categoryCode == null || categoryCode.length() == 0) {
                synchronized (lock) {
                    results.values = new ArrayList<String>();
                    results.count = 0;
                }
            } else {
                final int search = Integer.parseInt(categoryCode.toString());

                //Call to database to get matching records using room
                List<Item> matchValues;
                matchValues = viewModel.getItemsByCategory(search);

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                items = (ArrayList<Item>) results.values;
            } else {
                items = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                items = viewModel.getAllItems().getValue();
                notifyDataSetChanged();
            }
        }

    }
}
