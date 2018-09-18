package ro.antiprotv.redalert.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.List;

import ro.antiprotv.redalert.AlertListActivity;

public class RedAlertViewModel extends AndroidViewModel {
    private AlertRepository alertRepository;

    private LiveData<List<Alert>> allAlerts;

    public RedAlertViewModel(Application app) {
        super(app);
        alertRepository = new AlertRepository(app);
        allAlerts = alertRepository.getAllAlerts();
    }

    public LiveData<List<Alert>> getAllAlerts() {
        return allAlerts;
    }

    public long insert(Alert alert) {
        long id = alertRepository.insert(alert);
        return id;
    }

    public void removeAllAlerts() {
        alertRepository.removeAll();
    }

    public void removeAlert(Alert alert) {
        alertRepository.delete(alert);
    }

    public void changeLevel(Alert alert, int level) {
        alertRepository.update(alert, level);
    }
    public void update(Alert alert) {
        alertRepository.update(alert);
    }
    public List<String> selectItems(String prefix) {return alertRepository.getItemsByPrefix(prefix); }
    public List<String> selectStores(String prefix) {return alertRepository.getStoresByPrefix(prefix); }
    public String selectStoresByItem(String item) {
        List<String> stores = alertRepository.getStoresByItem(item);
        if (stores != null && !stores.isEmpty()){
            return stores.get(0);
        }
        return "";
    }

}
