package ro.antiprotv.redalert;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import ro.antiprotv.redalert.db.Alert;

/**
 * Manager class for notifications throughout the app
 */
public class NotificationManager {

    public static final String RED_ALERT_CHANNEL = "RED_ALERT_CHANNEL";
    private static NotificationManager INSTANCE = null;
    private NotificationManagerCompat notificationManagerCompat;
    private PendingIntent notifyPendingIntent;
    private Context context;

    private NotificationManager() {
    }

    public static NotificationManager getInstance() {
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
     *
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
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ctx.getResources().getString(R.string.channel_name);
            String description = ctx.getResources().getString(R.string.channel_description);
            int importance = android.app.NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(RED_ALERT_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    /**
     * This method creates, removes or updates a notification
     *
     * @param alert
     */
    public void notifyAlert(Alert alert) {
        if (alert.getLevel() == Alert.RED_ALERT) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, RED_ALERT_CHANNEL)
                    .setSmallIcon(alert.getIcon())
                    .setContentTitle(alert.getItem())
                    .setContentText(alert.getStore())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(notifyPendingIntent)
                    .setOngoing(true)
                    .setColor(context.getResources().getColor(alert.getColor()));
            notificationManagerCompat.notify((int) alert.getId(), mBuilder.build());
        } else {
            notificationManagerCompat.cancel((int) alert.getId());
        }
    }

    public void removeAllNotifications() {
        notificationManagerCompat.cancelAll();
    }
}
