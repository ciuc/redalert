package ro.antiprotv.sugar.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

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
        allAlerts = alertDao.getAllActiveAlerts();
    }

    LiveData<List<Alert>> getAllAlerts() {
        return allAlerts;
    }

    Alert findAlertByItem(String item) {
        FindAlertsByItemAsyncTask task = new FindAlertsByItemAsyncTask(alertDao);
        try {
            return task.execute(item).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
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
        new updateAsyncTask(alertDao).execute();
    }

    public Alert deleteAlertByItem(String itemName) {
        Alert alert = findAlertByItem(itemName);
        if (alert != null) {
            update(alert, Alert.GREEN_ALERT);
        }
        return alert;
    }

    public void update(Alert alert, int level) {
        if (level == Alert.RED_ALERT || level == Alert.ORANGE_ALERT || level == Alert.GREEN_ALERT) {
            alert.setLevel(level);
            new updateAsyncTask(alertDao).execute(alert);
        }
    }

    public void update(Alert alert) {
        new updateAsyncTask(alertDao).execute(alert);
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

    private static class FindAlertsByItemAsyncTask extends AsyncTask<String, Void, Alert> {
        private AlertDao mAsyncTaskDao;

        FindAlertsByItemAsyncTask(AlertDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Alert doInBackground(final String... items) {
            Alert alert;
            List<Alert> alerts = mAsyncTaskDao.getAlertsByItem(items[0]);
            if (alerts.size() >= 1) {
                alert = alerts.get(0);
            } else {
                alert = null;
            }
            return alert;
        }
    }

    private static class insertAsyncTask extends AsyncTask<Alert, Void, Long> {
        private AlertDao mAsyncTaskDao;

        insertAsyncTask(AlertDao dao) {
            mAsyncTaskDao = dao;
        }

        /*
        We check if there is already a disabled alert with the same item and store
        If so, we enable(update) that one, instead of creating a new one.
        If not, we create.
         */
        @Override
        protected Long doInBackground(final Alert... params) {
            Alert newAlert = params[0];
            List<Alert> alerts = mAsyncTaskDao.getAllDisabledAlertsSync();
            for (Alert alert : alerts) {
                if (newAlert.getItemName().equals(alert.getItemName())
                        && newAlert.getStore().equals(alert.getStore())) {
                    alert.setLevel(newAlert.getLevel());
                    mAsyncTaskDao.updateAlerts(alert);
                    return alert.getId();
                }
            }
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


}
