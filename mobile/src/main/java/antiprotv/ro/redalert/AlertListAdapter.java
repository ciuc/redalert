package antiprotv.ro.redalert;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import antiprotv.ro.redalert.db.Alert;

public class AlertListAdapter extends RecyclerView.Adapter {
    Logger logger = Logger.getLogger(AlertListAdapter.class.getName());

    class AlertViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemView;
        private final TextView storeView;
        private final View view;

        AlertViewHolder (View view) {
            super(view);
            this.view = view;
            this.itemView = view.findViewById(R.id.item);
            this.storeView = view.findViewById(R.id.store);
        }
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
        return new AlertViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        AlertViewHolder holder = ((AlertViewHolder)viewHolder);
        Resources res = viewHolder.itemView.getContext().getResources();
        if (alerts != null){
            Alert alert = alerts.get(position);

            //holder.view.setBackgroundColor(res.getColor(alert.getColor()));
            //((GradientDrawable)holder.view.getBackground().getCurrent()).setColor(res.getColor(alert.getColor()));
            ((GradientDrawable)holder.view.getBackground().getCurrent()).setStroke(0,res.getColor(alert.getColor()));
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
