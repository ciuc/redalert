package antiprotv.ro.redalert.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

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

    public void insert(Alert alert) {
        new insertAsyncTask(alertDao).execute(alert);
    }

    public void removeAll() {
        new deleteAsyncTask(alertDao).execute();
    }

    public void delete(Alert... alerts) {
        new deleteAsyncTask(alertDao).execute(alerts);
    }

    public void update(Alert alert, int level) {
        if (level == Alert.RED_ALERT || level == Alert.ORANGE_ALERT || level == Alert.YELLOW_ALERT) {
            alert.setLevel(level);
            new updateAsyncTask(alertDao).execute(alert);
        }
    }

    private static class insertAsyncTask extends AsyncTask<Alert, Void, Void> {
        private AlertDao mAsyncTaskDao;

        insertAsyncTask(AlertDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Alert... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
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
