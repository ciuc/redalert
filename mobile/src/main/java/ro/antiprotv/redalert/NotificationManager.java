package ro.antiprotv.redalert;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import ro.antiprotv.redalert.db.Alert;

/**
 * Manager class for notifications throughout the app
 */
public class NotificationManager {

    private static NotificationManager INSTANCE = null;
    private NotificationManagerCompat notificationManagerCompat;
    private PendingIntent notifyPendingIntent;
    private Context context;
    private NotificationManager() {
    }

    ;

    private static NotificationManager getInstance(Context ctx) {
        if (INSTANCE == null) {
            synchronized (NotificationManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NotificationManager();

                }
            }
        }
        return INSTANCE;
    }

    /**
     * Initializes the singleton
     * @param ctx
     */
    public void init(Context ctx) {
        if (notificationManagerCompat == null) {
            notificationManagerCompat = NotificationManagerCompat.from(ctx);
        }
        if (notifyPendingIntent == null) {
            Intent listActivity = new Intent(ctx, AlertListActivity.class);
            notifyPendingIntent = PendingIntent.getActivity(
                    ctx, 0, listActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        if (context == null) {
            context = ctx;
        }
    }


    /**
     * This method creates, removes or updates a notification
     * @param alert
     */
    public void notifyAlert(Alert alert) {
        if (alert.getLevel() == Alert.RED_ALERT) {
           NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, AlertListActivity.RED_ALERT_CHANNEL)
                    .setSmallIcon(alert.getIcon())
                    .setContentTitle(alert.getItem())
                    .setContentText(alert.getStore())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(notifyPendingIntent)
                    .setOngoing(true)
                    .setColor(context.getResources().getColor(alert.getColor()));
            notificationManagerCompat.notify((int) alert.getId(), mBuilder.build());
        }
        else {
            notificationManagerCompat.cancel((int)alert.getId());
        }
    }


}
