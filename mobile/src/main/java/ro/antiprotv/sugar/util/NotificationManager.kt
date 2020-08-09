package ro.antiprotv.sugar.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ro.antiprotv.sugar.MainActivity
import ro.antiprotv.sugar.R
import ro.antiprotv.sugar.repository.db.Alert
import ro.antiprotv.sugar.repository.db.AlertType

/**
 * Manager class for notifications throughout the app
 */
class NotificationManager(private val ctx: Context) {

    private val notificationManagerCompat: NotificationManagerCompat = NotificationManagerCompat.from(ctx)
    private val notifyPendingIntent: PendingIntent

    init {
        val listActivity = Intent(ctx, MainActivity::class.java)
        notifyPendingIntent = PendingIntent.getActivity(ctx, 0, listActivity, PendingIntent.FLAG_UPDATE_CURRENT)
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = ctx.resources.getString(R.string.channel_name)
            val description = ctx.resources.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(RED_ALERT_CHANNEL, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Reissue alerts when app starts; they might have been removed by the android system when closing app
     */
    fun reissueAllAlerts(alerts: List<Alert>) {
        for (alert in alerts) {
            notifyAlert(alert)
        }
    }

    /**
     * This method creates, removes or updates a notification
     *
     * @param alert
     */
    fun notifyAlert(alert: Alert) {
        if (alert.type == AlertType.RED) {
            val mBuilder = NotificationCompat.Builder(ctx, RED_ALERT_CHANNEL)
                    .setSmallIcon(alert.type.icon)
                    .setContentTitle(alert.itemName)
                    .setContentText(alert.store)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(notifyPendingIntent)
                    .setOngoing(true)
                    .setColor(ctx.resources.getColor(alert.type.color, ctx.theme))
            notificationManagerCompat.notify(alert.hashCode(), mBuilder.build())
        } else {
            notificationManagerCompat.cancel(alert.hashCode())
        }
    }

    fun cancelNotification(alert: Alert) {
        notificationManagerCompat.cancel(alert.hashCode())
    }

    fun removeAllNotifications() {
        notificationManagerCompat.cancelAll()
    }

    companion object {
        private const val RED_ALERT_CHANNEL = "RED_ALERT_CHANNEL"
    }
}