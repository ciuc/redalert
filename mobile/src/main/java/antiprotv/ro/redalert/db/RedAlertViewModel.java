package antiprotv.ro.redalert.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import antiprotv.ro.redalert.db.Item;
import antiprotv.ro.redalert.db.ItemRepository;

public class RedAlertViewModel extends AndroidViewModel {
    private ItemRepository itemRepository;
    private AlertRepository alertRepository;

    private LiveData<List<Item>> allItems;
    private LiveData<List<Alert>> allAlerts;

    public RedAlertViewModel(Application app){
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

}
