package ro.antiprotv.sugar.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * The ROOM item repo
 */
public class ItemRepository {

    private ItemDao itemDao;
    private LiveData<List<Item>> allItems;

    public ItemRepository(Application app) {
        RedalertRoomDatabase db = RedalertRoomDatabase.getDatabase(app);
        itemDao = db.itemDao();
        allItems = itemDao.getAllItems();
    }

    LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    List<Item> getItemsByCategory(int category) {
        RetrieveFiteredAsyncTask task = new RetrieveFiteredAsyncTask(itemDao);
        try {
            return task.execute(category).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    List<String> getItemsByPrefix(String prefix) {
        return itemDao.getItemsByPrefix(prefix);
    }


    public void insertIfNotExist(String name) {
        long id = 0;
        InsertEmptyIfNotExistsTask task = new InsertEmptyIfNotExistsTask(itemDao);
        try {
            task.execute(name).get();
        } catch (ExecutionException | InterruptedException e) {
            //TODO: what to do here?;
        }
    }

    public long insert(Item item) {
        InsertAsyncTask task = new InsertAsyncTask(itemDao);
        try {
            return task.execute(item).get();
        } catch (ExecutionException | InterruptedException e) {
            return 0l;
        }
    }

    public void removeAll() {
        new DeleteAsyncTask(itemDao).execute();
    }

    public void delete(Item... items) {
        new DeleteAsyncTask(itemDao).execute(items);
    }

    public void update(Item item) {
        new UpdateAsyncTask(itemDao).execute(item);
    }

    private static class InsertAsyncTask extends AsyncTask<Item, Void, Long> {
        private ItemDao mAsyncTaskDao;

        InsertAsyncTask(ItemDao dao) {
            mAsyncTaskDao = dao;
        }

        /*
        We check if there is already an item with the same name
        If not, we create one
         */
        @Override
        protected Long doInBackground(final Item... params) {
            //only one to insert
            Item newItem = params[0];
            List<Item> items = mAsyncTaskDao.getAllItemsSync();
            for (Item item : items) {
                if (newItem.getName().equals(item.getName())) {
                    mAsyncTaskDao.updateItems(item);
                    return item.getId();
                }
            }
            return mAsyncTaskDao.insert(params[0]);
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<Item, Void, Void> {
        private ItemDao mAsyncTaskDao;

        UpdateAsyncTask(ItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Item... params) {
            mAsyncTaskDao.updateItems(params);
            return null;
        }
    }

    private static class InsertEmptyIfNotExistsTask extends AsyncTask<String, Void, Long> {
        private ItemDao mAsyncTaskDao;

        InsertEmptyIfNotExistsTask(ItemDao dao) {
            mAsyncTaskDao = dao;
        }

        /*
        We check if there is already an item with the same name
        If not, we create one
         */
        @Override
        protected Long doInBackground(final String... names) {
            Long id = 0l;
            //only one to insert
            String name = names[0];
            if (mAsyncTaskDao.getItemByName(name) == null) {
                id = mAsyncTaskDao.insert(new Item(name));
            }
            return id;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Item, Void, Void> {
        private ItemDao mAsyncTaskDao;

        DeleteAsyncTask(ItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Item... params) {
            if (params.length == 0) {
                mAsyncTaskDao.removeAll();
            } else {
                for (Item item : params) {
                    mAsyncTaskDao.removeItem(item);
                }
            }
            return null;
        }
    }

    private static class RetrieveFiteredAsyncTask extends AsyncTask<Integer, Void, List<Item>> {
        private ItemDao itemDao;
        RetrieveFiteredAsyncTask(ItemDao itemDao) {
            this.itemDao = itemDao;
        }
        @Override
        protected List<Item> doInBackground(Integer... category) {
            return itemDao.getItemsByCategory(category[0]);
        }
    }
}
