package antiprotv.ro.redalert;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import antiprotv.ro.redalert.db.Alert;

public class AlertListAdapter extends RecyclerView.Adapter {

    class AlertViewHolder extends RecyclerView.ViewHolder {
        private final TextView levelView;
        //private final TextView itemView;
        //private final TextView storeView;

        AlertViewHolder (View view) {
            super(view);
            this.levelView = view.findViewById(R.id.level);
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
        if (alerts!=null){
            Alert alert = alerts.get(position);
            ((AlertViewHolder)viewHolder).levelView.setText(alert.getLevel());
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
