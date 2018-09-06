package antiprotv.ro.redalert.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class RedAlertViewModel extends AndroidViewModel {
    private ItemRepository itemRepository;
    private AlertRepository alertRepository;

    private LiveData<List<Item>> allItems;
    private LiveData<List<Alert>> allAlerts;

    public RedAlertViewModel(Application app) {
        super(app);
        itemRepository = new ItemRepository(app);
        allItems = itemRepository.getAllItems();
        alertRepository = new AlertRepository(app);
        allAlerts = alertRepository.getAllAlerts();
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public LiveData<List<Alert>> getAllAlerts() {
        return allAlerts;
    }

    public void insert(Item item) {
        itemRepository.insert(item);
    }

    public void insert(Alert alert) {
        alertRepository.insert(alert);
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
