package antiprotv.ro.redalert;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class RedAlertViewModel extends AndroidViewModel {
    private ItemRepository itemRepository;

    private LiveData<List<Item>> allItems;

    public RedAlertViewModel(Application app){
        super(app);
        itemRepository = new ItemRepository(app);
        allItems = itemRepository.getAllItems();
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public void insert(Item item) {
        itemRepository.insert(item);
    }

}
