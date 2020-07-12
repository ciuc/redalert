package ro.antiprotv.sugar.db;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class RedAlertViewModel extends AndroidViewModel {
    private AlertRepository alertRepository;
    private ItemRepository itemRepository;

    private LiveData<List<Alert>> allAlerts;
    private LiveData<List<Item>> allItems;

    public RedAlertViewModel(Application app) {
        super(app);
        alertRepository = new AlertRepository(app);
        itemRepository = new ItemRepository(app);
        allAlerts = alertRepository.getAllAlerts();
        allItems = itemRepository.getAllItems();
    }

    public LiveData<List<Alert>> getAllAlerts() {
        return allAlerts;
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public List<Item> getItemsByCategory(Category category) {
        return itemRepository.getItemsByCategory(category.code);
    }

    public List<Item> getItemsByCategory(int category) {
        return itemRepository.getItemsByCategory(category);
    }

    public long insert(Alert alert) {
        long id = alertRepository.insert(alert);
        itemRepository.insertIfNotExist(alert.getItemName());
        return id;
    }

    public long saveOrUpdate(Item item) {
        long id = item.getId();
        if (item.getId() == 0l) {
            id = itemRepository.insert(item);
        } else {
            itemRepository.update(item);
        }
        return id;
    }

    public Alert createAlert(Item item) {
        Alert alert = alertRepository.findAlertByItem(item.getName());
        if (alert == null) {
            alert = new Alert(Alert.RED_ALERT, item.getName(), null);
            alertRepository.insert(alert);

        }
        return alert;
    }

    public void removeAllAlerts() {
        alertRepository.removeAll();
    }

    public void removeAllItems() {
        itemRepository.removeAll();
    }

    //I can return here, b/c alerts are not deleted, but set to GREEN
    public Alert removeAlert(Item item) {
        return alertRepository.deleteAlertByItem(item.getName());
    }

    public void removeItem(Item alert) {
        itemRepository.delete(alert);
    }

    public void changeLevel(Alert alert, int level) {
        alertRepository.update(alert, level);
    }

    public void update(Alert alert) {
        alertRepository.update(alert);
    }

    public void update(Item item) {
        itemRepository.update(item);
    }

    public void addQuantity(Item item) {
        item.setQuantity(item.getQuantity() + 1);
        update(item);
    }

    public void subtractQuantity(Item item) {
        int q = item.getQuantity();
        if (q == 0) {
            createAlert(item);
            return;
        }
        item.setQuantity(q - 1);
        update(item);
    }

    public List<String> selectItems(String prefix) {
        return itemRepository.getItemsByPrefix(prefix);
    }

    public List<String> selectStores(String prefix) {
        return alertRepository.getStoresByPrefix(prefix);
    }

    public String selectStoresByItem(String item) {
        List<String> stores = alertRepository.getStoresByItem(item);
        if (stores != null && !stores.isEmpty()) {
            return stores.get(0);
        }
        return "";
    }

}
