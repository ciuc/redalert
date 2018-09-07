package ro.antiprotv.redalert.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * The ROOM alert repo
 */
public class AlertRepository {

    private AlertDao alertDao;
    private LiveData<List<Alert>> allAlerts;

    public AlertRepository(Application app) {
        RedalertRoomDatabase db = RedalertRoomDatabase.getDatabase(app);
        alertDao = db.alertDao();
        allAlerts = alertDao.getAllAlerts();
    }

    LiveData<List<Alert>> getAllAlerts() {
        return allAlerts;
    }

    List<String> getItemsByPrefix(String prefix) {
        return alertDao.getItemsByPrefix(prefix);
    }

    List<String> getStoresByPrefix(String prefix) {
        return alertDao.getStoresByPrefix(prefix);
    }

    List<String> getStoresByItem(String item) {
        retrieveStoreAsyncTask task = new retrieveStoreAsyncTask(alertDao);
        try {
            return task.execute(item).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    public long insert(Alert alert) {
        insertAsyncTask task = new insertAsyncTask(alertDao);
        try {
            return task.execute(alert).get();
        } catch (ExecutionException | InterruptedException e) {
            return 0l;
        }
    }

    public void removeAll() {
        new deleteAsyncTask(alertDao).execute();
    }

    public void delete(Alert... alerts) {
        new deleteAsyncTask(alertDao).execute(alerts);
    }

    public void update(Alert alert, int level) {
        if (level == Alert.RED_ALERT || level == Alert.ORANGE_ALERT || level == Alert.YELLOW_ALERT || level == Alert.GREEN_ALERT) {
            alert.setLevel(level);
            new updateAsyncTask(alertDao).execute(alert);
        }
    }

    private static class retrieveStoreAsyncTask extends AsyncTask<String, Void, List<String>> {
        private AlertDao mAsyncTaskDao;

        retrieveStoreAsyncTask(AlertDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<String> doInBackground(final String... items) {
            return mAsyncTaskDao.selectStoresByItem(items[0]);

        }
    }

    private static class insertAsyncTask extends AsyncTask<Alert, Void, Long> {
        private AlertDao mAsyncTaskDao;

        insertAsyncTask(AlertDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Long doInBackground(final Alert... params) {
            return mAsyncTaskDao.insert(params[0]);
        }
    }

    private static class updateAsyncTask extends AsyncTask<Alert, Void, Void> {
        private AlertDao mAsyncTaskDao;

        updateAsyncTask(AlertDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Alert... params) {
            mAsyncTaskDao.updateAlerts(params);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Alert, Void, Void> {
        private AlertDao mAsyncTaskDao;

        deleteAsyncTask(AlertDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Alert... params) {
            if (params.length == 0) {
                mAsyncTaskDao.removeAll();
            } else {
                for (Alert alert : params) {
                    mAsyncTaskDao.removeAlert(alert);
                }
            }
            return null;
        }
    }
}
