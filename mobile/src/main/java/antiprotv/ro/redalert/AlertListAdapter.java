package antiprotv.ro.redalert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import antiprotv.ro.redalert.db.Alert;

public class AlertListAdapter extends RecyclerView.Adapter {
    Logger logger = Logger.getLogger(AlertListAdapter.class.getName());

    class AlertViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private Alert alert;
        private final ImageView iconView;
        private final TextView itemView;
        private final TextView storeView;
        private final View view;

        AlertViewHolder (View view, Alert alert) {
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
            MenuItem edit = menu.add(Menu.NONE, 1, 1, "Edit this alert");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete this alert");
            Alert alert = this.alert;
            MenuItem makeRed;
            MenuItem makeYellow;
            MenuItem makeOrange;
            if (alert.getLevel() == Alert.RED_ALERT) {
                makeOrange = menu.add(Menu.NONE, 3, 3, "Make Orange");
                makeYellow = menu.add(Menu.NONE, 4, 4, "Make Yellow");
                makeOrange.setOnMenuItemClickListener(new OnClickMenu(this.alert.getId(), v));
                makeYellow.setOnMenuItemClickListener(new OnClickMenu(this.alert.getId(), v));
            }
            if (alert.getLevel() == Alert.ORANGE_ALERT) {
                makeRed = menu.add(Menu.NONE, 3, 3, "Make Red");
                makeYellow = menu.add(Menu.NONE, 4, 4, "Make Yellow");
                makeRed.setOnMenuItemClickListener(new OnClickMenu(this.alert.getId(), v));
                makeYellow.setOnMenuItemClickListener(new OnClickMenu(this.alert.getId(), v));
            }
            if (alert.getLevel() == Alert.YELLOW_ALERT) {
                makeRed = menu.add(Menu.NONE, 3, 3, "Make Red");
                makeOrange = menu.add(Menu.NONE, 4, 4, "Make Orange");
                makeOrange.setOnMenuItemClickListener(new OnClickMenu(this.alert.getId(), v));
                makeRed.setOnMenuItemClickListener(new OnClickMenu(this.alert.getId(), v));
            }
            edit.setOnMenuItemClickListener(new OnClickMenu(this.alert.getId(), v));
            delete.setOnMenuItemClickListener(new OnClickMenu(this.alert.getId(), v));

        }

        private class OnClickMenu implements MenuItem.OnMenuItemClickListener {
            int alertId;
            View v;

            OnClickMenu(int alertId, View v){
                this.alertId = alertId;
                this.v = v;
            }
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case 1:
                        Toast.makeText(v.getContext(), new Integer(alertId).toString(), Toast.LENGTH_SHORT).show();
                        break;

                    case 2:
                        Toast.makeText(v.getContext(), new Integer(alertId).toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        };


    }
    

    private final LayoutInflater inflater;

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    private List<Alert> alerts;

    AlertListAdapter(Context context) {
        inflater =  LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View itemView = inflater.inflate(R.layout.content_alert_list, parent, false);
        return new AlertViewHolder(itemView, null);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        AlertViewHolder holder = ((AlertViewHolder)viewHolder);
        Resources res = viewHolder.itemView.getContext().getResources();
        holder.view.setLongClickable(true);
        if (alerts != null){
            Alert alert = alerts.get(position);
            holder.alert = alert;
            //View listItemView = holder.view;
            //holder.view.setBackgroundColor(res.getColor(alert.getColor()));
            //((GradientDrawable)holder.view.getBackground().getCurrent()).setColor(res.getColor(alert.getColor()));
            //((GradientDrawable)listItemView.getBackground().getCurrent()).setStroke(0,res.getColor(alert.getColor()));
            holder.iconView.setColorFilter(res.getColor(alert.getColor()));
            holder.itemView.setText(alert.getItem());
            holder.itemView.setTextColor(res.getColor(alert.getColor()));
            if (alert.getStore() != null && !alert.getStore().equals("")) {
                holder.storeView.setText("FROM: " + alert.getStore());
            } else {holder.storeView.setText("");}
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
}
